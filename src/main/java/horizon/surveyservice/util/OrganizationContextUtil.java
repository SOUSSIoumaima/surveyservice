package horizon.surveyservice.util;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;
@Component
public class OrganizationContextUtil {
    private final JwtUtil jwtUtil;

    public OrganizationContextUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public UUID getCurrentOrganizationId() {
        String token = extractTokenFromRequest();
        if (token == null) throw new SecurityException("No token found");

        UUID organizationId = jwtUtil.extractOrganizationId(token);
        if (organizationId == null) throw new SecurityException("No organization ID found in token");

        return organizationId;
    }

    public void validateOrganizationAccess(UUID resourceOrganizationId) {
        if (isRootAdmin()) return;

        UUID currentOrgId = getCurrentOrganizationId();
        if (!resourceOrganizationId.equals(currentOrgId)) {
            throw new SecurityException("Access denied: different organization");
        }
    }

    public boolean isRootAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("SYS_ADMIN_ROOT"));
    }

    private String extractTokenFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;

        HttpServletRequest request = attributes.getRequest();
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
