package ge.boxwood.espace.security;

import ge.boxwood.espace.config.providers.TimeProvider;
import ge.boxwood.espace.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenHelper {

    static final String AUDIENCE_UNKNOWN = "unknown";
    static final String AUDIENCE_WEB = "web";
    static final String AUDIENCE_MOBILE = "mobile";
    static final String AUDIENCE_TABLET = "tablet";
    @Value("${jwt.secret}")
    public String SECRET;
    @Autowired
    TimeProvider timeProvider;
    @Value("${app.name}")
    private String APP_NAME;
    @Value("${jwt.expires_in}")
    private int EXPIRES_IN;
    @Value("${jwt.mobile_expires_in}")
    private int MOBILE_EXPIRES_IN;
    @Value("${jwt.header}")
    private String AUTH_HEADER;
    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public Date getIssuedAtDateFromToken(String token) {
        Date issueAt;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (Exception e) {
            issueAt = null;
        }
        return issueAt;
    }

    public String getAudienceFromToken(String token) {
        String audience;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            audience = claims.getAudience();
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    public String refreshToken(String token, Device device) {
        String refreshedToken;
        Date a = timeProvider.now();
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            claims.setIssuedAt(a);
            refreshedToken = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(generateExpirationDate(device))
                    .signWith(SIGNATURE_ALGORITHM, SECRET)
                    .compact();
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public String generateRefreshToken(String newToken, Device device) {
        if (StringUtils.isBlank(getUsernameFromToken(newToken))) {
            throw new IllegalArgumentException("Cannot create JWT Token without username");
        }

        DateTime currentTime = new DateTime();

        String token = Jwts.builder()
                .setSubject(getUsernameFromToken(newToken))
                .setClaims(getAllClaimsFromToken(newToken))
                .setIssuer(APP_NAME)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(currentTime.toDate())
                .setExpiration(new Date(timeProvider.now().getTime() + 604800)) // One week for refresh token
                .signWith(SIGNATURE_ALGORITHM, SECRET)
                .compact();

        return token;
    }

    public String generateToken(User userContext, Device device) {
        if (StringUtils.isBlank(userContext.getUsername()))
            throw new IllegalArgumentException("Cannot create JWT Token without username");

        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty())
            throw new IllegalArgumentException("User doesn't have any privileges");

        Claims claims = Jwts.claims().setSubject(userContext.getUsername());
        claims.put("scopes", userContext.getAuthorities());

        DateTime currentTime = new DateTime();

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(APP_NAME)
                .setIssuedAt(currentTime.toDate())
                .setExpiration(generateExpirationDate(device))
                .signWith(SIGNATURE_ALGORITHM, SECRET)
                .compact();

        return token;
    }

    private String generateAudience(Device device) {
        String audience = AUDIENCE_UNKNOWN;
        if (device.isNormal()) {
            audience = AUDIENCE_WEB;
        } else if (device.isTablet()) {
            audience = AUDIENCE_TABLET;
        } else if (device.isMobile()) {
            audience = AUDIENCE_MOBILE;
        }
        return audience;
    }

    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private Date generateExpirationDate(Device device) {
        long expiresIn = device.isTablet() || device.isMobile() ? MOBILE_EXPIRES_IN : EXPIRES_IN;
        return new Date(timeProvider.now().getTime() + expiresIn * 1000);
    }

    public int getExpiredIn(Device device) {
        return device.isMobile() || device.isTablet() ? MOBILE_EXPIRES_IN : EXPIRES_IN;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        User user = (User) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);
        return (
                username != null &&
                        username.equals(userDetails.getUsername()) &&
                        !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())
        );
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    public String getToken(HttpServletRequest request) {
        /**
         *  Getting the token from Authentication header
         *  e.g Bearer your_token
         */
        String authHeader = getAuthHeaderFromHeader(request);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER);
    }

}