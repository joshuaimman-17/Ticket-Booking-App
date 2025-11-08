package com.ticketapp.ticket_service.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ticketapp.ticket_service.client.BookingClient;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final BookingClient bookingClient;

    @Value("${ticket.files.path:/tmp/tickets}")
    private String ticketsPath;

    public TicketService(TicketRepository ticketRepository, BookingClient bookingClient) {
        this.ticketRepository = ticketRepository;
        this.bookingClient = bookingClient;
    }

    // ✅ Only bookingId is needed now
    public TicketRecord generateTicket(UUID bookingId) throws IOException, WriterException {

        // Fetch booking details
        Map<String, Object> bookingDetails = bookingClient.getBooking(bookingId);

        if (bookingDetails == null || bookingDetails.isEmpty()) {
            throw new IOException("Booking not found for ID: " + bookingId);
        }

        UUID userId = UUID.fromString((String) bookingDetails.get("userId"));
        UUID eventId = UUID.fromString((String) bookingDetails.get("eventId"));

        Path dir = Path.of(ticketsPath);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        String qrText = String.format("TICKET|%s|%s|%s|%s", bookingId, userId, eventId, LocalDateTime.now());
        String filename = "ticket_" + bookingId + ".pdf";
        Path out = dir.resolve(filename);

        // Generate QR Code
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix bm = qrWriter.encode(qrText, BarcodeFormat.QR_CODE, 220, 220);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bm);

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A6);
            doc.addPage(page);
            var pdImage = LosslessFactory.createFromImage(doc, qrImage);

            try (PDPageContentStream content = new PDPageContentStream(doc, page)) {

                // === Background ===
                content.setNonStrokingColor(new Color(250, 250, 250));
                content.addRect(0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
                content.fill();

                // === Header Bar ===
                content.setNonStrokingColor(new Color(33, 150, 243));
                content.addRect(0, page.getMediaBox().getHeight() - 45, page.getMediaBox().getWidth(), 45);
                content.fill();

                // === Header Text ===
                content.beginText();
                content.setNonStrokingColor(Color.WHITE);
                content.setFont(PDType1Font.HELVETICA_BOLD, 18);
                content.newLineAtOffset(25, page.getMediaBox().getHeight() - 30);
                content.showText(" Ticket Confirmation");
                content.endText();

                // === Booking Info ===
                float y = page.getMediaBox().getHeight() - 70;
                content.setNonStrokingColor(Color.BLACK);

                drawLabelValue(content, "Booking ID:", bookingId.toString(), 25, y); y -= 15;
                drawLabelValue(content, "User ID:", userId.toString(), 25, y); y -= 15;
                drawLabelValue(content, "Event ID:", eventId.toString(), 25, y); y -= 15;

                drawLabelValue(content, "Quantity:", String.valueOf(bookingDetails.getOrDefault("quantity", "N/A")), 25, y); y -= 15;
                drawLabelValue(content, "Amount:", "Rs " + bookingDetails.getOrDefault("amount", "N/A"), 25, y); y -= 15;
                drawLabelValue(content, "Payment ID:", String.valueOf(bookingDetails.getOrDefault("paymentId", "N/A")), 25, y); y -= 15;
                drawLabelValue(content, "Status:", String.valueOf(bookingDetails.getOrDefault("status", "N/A")), 25, y); y -= 15;
                drawLabelValue(content, "Ticket Type:", String.valueOf(bookingDetails.getOrDefault("ticketType", "Standard")), 25, y); y -= 20;

                // Divider line
                content.setStrokingColor(Color.LIGHT_GRAY);
                content.moveTo(20, y);
                content.lineTo(page.getMediaBox().getWidth() - 20, y);
                content.stroke();

                // === QR Code Center ===
                float imgW = 160f, imgH = 160f;
                float startX = (page.getMediaBox().getWidth() - imgW) / 2f;
                float startY = y - imgH - 15;
                content.drawImage(pdImage, startX, startY, imgW, imgH);

                // === Footer ===
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                content.setNonStrokingColor(Color.DARK_GRAY);
                content.newLineAtOffset(45, 55);
                content.showText("Please present this ticket at event entry.");
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
                content.newLineAtOffset(75, 42);
                content.showText("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
                content.setNonStrokingColor(new Color(33, 150, 243));
                content.newLineAtOffset(90, 30);
                content.showText("Powered by TicketApp");
                content.endText();
            }

            doc.save(out.toFile());
        }

        // Save record
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

    private void drawLabelValue(PDPageContentStream content, String label, String value, float x, float y) throws IOException {
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 10);
        content.newLineAtOffset(x, y);
        content.showText(label);
        content.endText();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 10);
        content.newLineAtOffset(x + 80, y);
        content.showText(value);
        content.endText();
    }

    public Optional<TicketRecord> findByBookingId(UUID bookingId) {
        return ticketRepository.findByBookingId(bookingId);
    }

    public byte[] getTicketPdfData(String path) throws IOException {
        Path safePath = Path.of(path.replace("\\", "/"));
        if (!Files.exists(safePath)) throw new IOException("Ticket file not found at: " + safePath.toAbsolutePath());
        return Files.readAllBytes(safePath);
    }
}
