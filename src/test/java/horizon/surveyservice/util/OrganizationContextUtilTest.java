package horizon.surveyservice.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class OrganizationContextUtilTest {
    private OrganizationContextUtil orgUtil;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        orgUtil = new OrganizationContextUtil();
        request = mock(HttpServletRequest.class);
        ServletRequestAttributes attrs = mock(ServletRequestAttributes.class);
        when(attrs.getRequest()).thenReturn(request);
        RequestContextHolder.setRequestAttributes(attrs);
    }

    @Test
    void testGetCurrentOrganizationId() {
        UUID orgId = UUID.randomUUID();
        when(request.getHeader("X-Organization-Id")).thenReturn(orgId.toString());

        UUID result = orgUtil.getCurrentOrganizationId();
        assertEquals(orgId, result);
    }

    @Test
    void testGetCurrentUserId() {
        UUID userId = UUID.randomUUID();
        when(request.getHeader("X-User-Id")).thenReturn(userId.toString());

        UUID result = orgUtil.getCurrentUserId();
        assertEquals(userId, result);
    }

    @Test
    void testIsRootAdminTrue() {
        when(request.getHeader("X-User-Roles")).thenReturn("SYS_ADMIN_ROOT,USER");
        assertTrue(orgUtil.isRootAdmin());
    }

    @Test
    void testIsRootAdminFalse() {
        when(request.getHeader("X-User-Roles")).thenReturn("USER,ADMIN");
        assertFalse(orgUtil.isRootAdmin());
    }

    @Test
    void testValidateOrganizationAccess_AllowsRootAdmin() {
        when(request.getHeader("X-User-Roles")).thenReturn("SYS_ADMIN_ROOT");
        assertDoesNotThrow(() -> orgUtil.validateOrganizationAccess(UUID.randomUUID()));
    }

    @Test
    void testValidateOrganizationAccess_DeniesDifferentOrg() {
        UUID resourceOrg = UUID.randomUUID();
        UUID currentOrg = UUID.randomUUID();
        when(request.getHeader("X-Organization-Id")).thenReturn(currentOrg.toString());
        when(request.getHeader("X-User-Roles")).thenReturn("USER");

        SecurityException exception = assertThrows(SecurityException.class,
                () -> orgUtil.validateOrganizationAccess(resourceOrg));
        assertEquals("Access denied: different organization", exception.getMessage());
    }

    @Test
    void testGetCurrentDepartmentIdOrNull() {
        UUID deptId = UUID.randomUUID();
        when(request.getHeader("X-Department-Id")).thenReturn(deptId.toString());
        assertEquals(deptId, orgUtil.getCurrentDepartmentIdOrNull());

        when(request.getHeader("X-Department-Id")).thenReturn(null);
        assertNull(orgUtil.getCurrentDepartmentIdOrNull());
    }

    @Test
    void testGetCurrentTeamIdOrNull() {
        UUID teamId = UUID.randomUUID();
        when(request.getHeader("X-Team-Id")).thenReturn(teamId.toString());
        assertEquals(teamId, orgUtil.getCurrentTeamIdOrNull());

        when(request.getHeader("X-Team-Id")).thenReturn(null);
        assertNull(orgUtil.getCurrentTeamIdOrNull());
    }

    @Test
    void testHasAuthority() {
        Authentication auth = mock(Authentication.class);
        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("TEST_AUTH");

        doReturn(Collections.singletonList(authority)).when(auth).getAuthorities();

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        assertTrue(orgUtil.hasAuthority("TEST_AUTH"));
        assertFalse(orgUtil.hasAuthority("OTHER_AUTH"));
    }

}
