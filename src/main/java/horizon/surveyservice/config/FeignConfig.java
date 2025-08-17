package horizon.surveyservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // Forward all X- headers and important headers
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    String headerValue = request.getHeader(headerName);
                    
                    // Forward X- headers, Authorization, and Cookie headers
                    if (headerName.startsWith("X-") || 
                        headerName.equals("Authorization") || 
                        headerName.equals("Cookie")) {
                        requestTemplate.header(headerName, headerValue);
                    }
                }
                
                // Ensure specific headers are forwarded
                String username = request.getHeader("X-Username");
                String userId = request.getHeader("X-User-Id");
                String organizationId = request.getHeader("X-Organization-Id");
                String authorities = request.getHeader("X-Authorities");
                String userAuthorities = request.getHeader("X-User-Authorities");
                
                if (username != null) {
                    requestTemplate.header("X-Username", username);
                    requestTemplate.header("X-User-Name", username);
                }
                if (userId != null) {
                    requestTemplate.header("X-User-Id", userId);
                }
                if (organizationId != null) {
                    requestTemplate.header("X-Organization-Id", organizationId);
                }
                if (authorities != null) {
                    requestTemplate.header("X-Authorities", authorities);
                }
                if (userAuthorities != null) {
                    requestTemplate.header("X-User-Authorities", userAuthorities);
                }
            }
        };
    }
}
