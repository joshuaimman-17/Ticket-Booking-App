package com.ticketapp.ticket_service.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ticketapp.ticket_service.entity.TicketRecord;
import com.ticketapp.ticket_service.repository.TicketRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Value("${ticket.files.path:/tmp/tickets}")
    private String ticketsPath;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public TicketRecord generateTicket(UUID bookingId, UUID userId, UUID eventId) throws IOException, WriterException {
        Path dir = Path.of(ticketsPath);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        String qrText = String.format("TICKET|%s|%s|%s|%s", bookingId, userId, eventId, LocalDateTime.now());
        String filename = "ticket_" + bookingId + ".pdf";
        Path out = dir.resolve(filename);

        // QR code
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix bm = qrWriter.encode(qrText, BarcodeFormat.QR_CODE, 220, 220);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bm);

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A6);
            doc.addPage(page);

            var pdImage = LosslessFactory.createFromImage(doc, qrImage);

            try (PDPageContentStream content = new PDPageContentStream(doc, page)) {

                // Light background
                content.setNonStrokingColor(new Color(245, 245, 245));
                content.addRect(0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
                content.fill();

                // Header bar
                content.setNonStrokingColor(new Color(63, 81, 181));
                content.addRect(0, page.getMediaBox().getHeight() - 40, page.getMediaBox().getWidth(), 40);
                content.fill();

                // Title text (no emoji)
                content.beginText();
                content.setNonStrokingColor(Color.WHITE);
                content.setFont(PDType1Font.HELVETICA_BOLD, 16);
                content.newLineAtOffset(20, page.getMediaBox().getHeight() - 28);
                content.showText("Event Ticket");
                content.endText();

                // Reset color to black
                content.setNonStrokingColor(Color.BLACK);
                float y = page.getMediaBox().getHeight() - 70;

                // Booking Info
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.newLineAtOffset(20, y);
                content.showText("Booking Details:");
                content.endText();

                y -= 20;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 10);
                content.newLineAtOffset(20, y);
                content.showText("Booking ID: " + bookingId);
                content.endText();

                y -= 15;
                content.beginText();
                content.newLineAtOffset(20, y);
                content.showText("User ID: " + userId);
                content.endText();

                y -= 15;
                content.beginText();
                content.newLineAtOffset(20, y);
                content.showText("Event ID: " + eventId);
                content.endText();

                y -= 15;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                content.newLineAtOffset(20, y);
                content.showText("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                content.endText();

                // Divider Line
                content.setStrokingColor(Color.GRAY);
                content.moveTo(20, y - 10);
                content.lineTo(page.getMediaBox().getWidth() - 20, y - 10);
                content.stroke();

                // QR Code in center
                float imgW = 180f;
                float imgH = 180f;
                float startX = (page.getMediaBox().getWidth() - imgW) / 2f;
                float startY = (page.getMediaBox().getHeight() / 2f) - 130;
                content.drawImage(pdImage, startX, startY, imgW, imgH);

                // Footer note
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                content.setNonStrokingColor(Color.DARK_GRAY);
                content.newLineAtOffset(50, 60);
                content.showText("Present this QR at the venue entrance");
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
                content.newLineAtOffset(80, 48);
                content.showText("Powered by TicketApp");
                content.endText();
            }

            doc.save(out.toFile());
        }

        TicketRecord rec = new TicketRecord();
        rec.setBookingId(bookingId);
        rec.setUserId(userId);
        rec.setEventId(eventId);
        rec.setQrText(qrText);
        rec.setTicketPdfPath(out.toAbsolutePath().toString());
        rec.setCreatedAt(LocalDateTime.now());

        ticketRepository.save(rec);
        return rec;
    }


    public Optional<TicketRecord> findByBookingId(UUID bookingId) {
        return ticketRepository.findByBookingId(bookingId);
    }

    public byte[] getTicketPdfData(String path) throws IOException {
        Path safePath = Path.of(path.replace("\\", "/"));
        if (!Files.exists(safePath)) {
            throw new IOException("Ticket file not found at: " + safePath.toAbsolutePath());
        }
        return Files.readAllBytes(safePath);
    }
}
