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
    private static final String HEADER_ORGANIZATION_ID = "X-Organization-Id";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_ROLES = "X-User-Roles";
    private static final String HEADER_DEPARTMENT_ID = "X-Department-Id";
    private static final String HEADER_TEAM_ID = "X-Team-Id";


    public UUID getCurrentOrganizationId() {
        String orgIdStr = getHeaderValue(HEADER_ORGANIZATION_ID);
        if (orgIdStr == null) throw new SecurityException("No Organization ID found in headers");
        return UUID.fromString(orgIdStr);
    }

    public UUID getCurrentUserId() {
        String userIdStr = getHeaderValue(HEADER_USER_ID);
        if (userIdStr == null) throw new SecurityException("No User ID found in headers");
        return UUID.fromString(userIdStr);
    }

    public boolean isRootAdmin() {
        String rolesStr = getHeaderValue(HEADER_ROLES);
        if (rolesStr == null) return false;
        String[] roles = rolesStr.split(",");
        for (String role : roles) {
            if (role.trim().equals("SYS_ADMIN_ROOT")) {
                return true;
            }
        }
        return false;
    }

    public void validateOrganizationAccess(UUID resourceOrganizationId) {
        if (isRootAdmin()) return;

        UUID currentOrgId = getCurrentOrganizationId();
        if (!resourceOrganizationId.equals(currentOrgId)) {
            throw new SecurityException("Access denied: different organization");
        }
    }

    private String getHeaderValue(String headerName) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        HttpServletRequest request = attributes.getRequest();
        return request.getHeader(headerName);
    }

    public UUID getCurrentDepartmentIdOrNull() {
        String departmentIdStr = getHeaderValue(HEADER_DEPARTMENT_ID);
        return (departmentIdStr != null && !departmentIdStr.isBlank()) ? UUID.fromString(departmentIdStr) : null;
    }

    public UUID getCurrentTeamIdOrNull() {
        String teamIdStr = getHeaderValue(HEADER_TEAM_ID);
        return (teamIdStr != null && !teamIdStr.isBlank()) ? UUID.fromString(teamIdStr) : null;
    }
    public boolean hasRole(String roleToCheck) {
        String rolesStr = getHeaderValue(HEADER_ROLES);
        if (rolesStr == null) return false;
        String[] roles = rolesStr.split(",");
        for (String role : roles) {
            if (role.trim().equals(roleToCheck)) {
                return true;
            }
        }
        return false;
    }

}
