package pl.lodz.p.it.securental.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.lodz.p.it.securental.adapters.mok.BlacklistedJwtAdapter;
import pl.lodz.p.it.securental.aop.annotations.RequiresNewTransaction;
import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class JwtUtils {

    private final BlacklistedJwtAdapter blacklistedJwtAdapter;

    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public static String extractString(String token, String claim) {
        final Claims claims = extractAllClaims(token);
        return claims.get(claim, String.class);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(ApplicationProperties.JWT_KEY).parseClaimsJws(token).getBody();
    }

    private static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private static String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * ApplicationProperties.JWT_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, ApplicationProperties.JWT_KEY).compact();
    }

    @RequiresNewTransaction
    public Boolean validateToken(String token, UserDetails userDetails) throws ApplicationBaseException {
        final String username = extractUsername(token);
        if (blacklistedJwtAdapter.getBlacklistedJwt(token).isEmpty()) {
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } else {
            return false;
        }
    }
}
