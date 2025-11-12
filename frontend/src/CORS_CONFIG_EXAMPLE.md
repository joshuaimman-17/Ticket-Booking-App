# Spring Boot CORS Configuration Examples

Add these configurations to your Spring Boot microservices to enable frontend connectivity.

## Option 1: Global CORS Configuration (Recommended)

Create this file in each microservice:

```java
package com.ticketapp.[service_name].config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Allow your frontend URLs
                .allowedOrigins(
                    "http://localhost:3000",      // React default
                    "http://localhost:5173",      // Vite default
                    "http://localhost:4200",      // Angular default
                    "https://yourdomain.com"      // Production
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-User-Role", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

## Option 2: Controller-Level CORS

Add `@CrossOrigin` to individual controllers:

```java
package com.ticketapp.event_service.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@CrossOrigin(
    origins = {"http://localhost:3000", "http://localhost:5173"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, 
               RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    exposedHeaders = {"X-User-Role"},
    allowCredentials = "true"
)
public class EventController {
    // Controller methods...
}
```

## Option 3: Method-Level CORS

For specific endpoints only:

```java
@GetMapping("/events/allEvents")
@CrossOrigin(origins = "http://localhost:3000")
public ResponseEntity<List<Event>> getAllEvents() {
    // Method implementation
}
```

## For Each Microservice

### User Service (Port 8081)

```java
package com.ticketapp.user_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/users/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### Admin Service (Port 8082)

```java
package com.ticketapp.admin_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/admin/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-User-Role")
                .allowCredentials(true);
    }
}
```

### Event Service (Port 8083)

```java
package com.ticketapp.event_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/events/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### Booking Service (Port 8084)

```java
package com.ticketapp.booking_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/bookings/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### Payment Service (Port 8085)

```java
package com.ticketapp.payment_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/payments/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### Ticket Service (Port 8086)

```java
package com.ticketapp.ticket_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/tickets/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

## Production CORS Configuration

For production, use environment variables:

```java
package com.ticketapp.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Value("${cors.allowed.origins}")
    private String[] allowedOrigins;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-User-Role", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

In `application.properties`:
```properties
# Development
cors.allowed.origins=http://localhost:3000,http://localhost:5173

# Production
# cors.allowed.origins=https://yourdomain.com,https://www.yourdomain.com
```

## Security Filter Configuration (Alternative)

For more control, use a security filter:

```java
package com.ticketapp.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsFilterConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173",
            "https://yourdomain.com"
        ));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        config.setExposedHeaders(Arrays.asList("X-User-Role", "Authorization"));
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
```

## Troubleshooting CORS

### Issue: Preflight OPTIONS requests failing

Add OPTIONS to allowed methods:
```java
.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
```

### Issue: Custom headers not visible in frontend

Add headers to `exposedHeaders`:
```java
.exposedHeaders("X-User-Role", "Authorization", "Content-Type")
```

### Issue: Credentials not being sent

Ensure both backend and frontend are configured:

Backend:
```java
.allowCredentials(true)
```

Frontend (if needed):
```typescript
fetch(url, {
    credentials: 'include'
})
```

### Issue: Multiple CORS headers

Don't mix configuration methods. Choose ONE:
- Global configuration (CorsConfig)
- Controller-level (@CrossOrigin)
- Filter-based (CorsFilter)

## Testing CORS

Test with curl:
```bash
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: GET" \
     -H "Access-Control-Request-Headers: X-Requested-With" \
     -X OPTIONS \
     --verbose \
     http://localhost:8083/events/allEvents
```

Expected headers in response:
```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Access-Control-Allow-Headers: *
Access-Control-Allow-Credentials: true
```

## Best Practices

1. ✅ Use specific origins in production (not "*")
2. ✅ Enable credentials only when needed
3. ✅ Limit exposed headers to only what's necessary
4. ✅ Set reasonable max-age for preflight caching
5. ✅ Use environment-specific configuration
6. ❌ Don't use `*` for origins with credentials
7. ❌ Don't expose sensitive headers
8. ❌ Don't allow all origins in production
