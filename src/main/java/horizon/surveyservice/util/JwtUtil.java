package horizon.surveyservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.UUID;
@Component
public class JwtUtil {
    private final Key signingKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }


    public UUID extractUUIDClaim(String token, String claimKey) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String value = (String) claims.get(claimKey);
            return value != null ? UUID.fromString(value) : null;
        } catch (Exception e) {
            return null;
        }
    }
    public UUID extractUserId(String token) {
        return extractUUIDClaim(token, "userId");
    }

    public UUID extractOrganizationId(String token) {
        return extractUUIDClaim(token, "organizationId");
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

