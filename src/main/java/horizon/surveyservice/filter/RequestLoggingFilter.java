package horizon.surveyservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        // Generate or use existing request ID
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        
        // Add request ID to response headers
        response.setHeader(REQUEST_ID_HEADER, requestId);
        
        // Set up MDC for logging
        MDC.put("requestId", requestId);
        MDC.put("requestMethod", request.getMethod());
        MDC.put("requestPath", request.getRequestURI());
        
        long startTime = System.currentTimeMillis();
        
        logger.info("Survey Service Request - Method: {}, Path: {}, Request-ID: {}",
                request.getMethod(),
                request.getRequestURI(),
                requestId);
        
        try {
            filterChain.doFilter(request, response);
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Survey Service Response - Status: {}, Duration: {}ms, Request-ID: {}",
                    response.getStatus(),
                    duration,
                    requestId);
                    
        } catch (Exception e) {
            logger.error("Survey Service Error - Method: {}, Path: {}, Request-ID: {}, Error: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    requestId,
                    e.getMessage(),
                    e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}
