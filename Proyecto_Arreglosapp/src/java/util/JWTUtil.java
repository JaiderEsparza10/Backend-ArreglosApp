package util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

/**
 * Esta utilidad proporciona métodos para la creación, validación y extracción
 * de datos de tokens JWT.
 * Se utiliza para mantener la sesión segura del usuario de forma compacta y
 * verificable.
 */
public class JWTUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000; // 24 horas en milisegundos

    /**
     * Genera un token JWT que contiene la información esencial del usuario y una
     * fecha de expiración.
     */
    public static String generateToken(String email, int userId, int rolId, String nombre) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("rolId", rolId)
                .claim("nombre", nombre)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public static String extractEmail(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    public static Integer extractUserId(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.get("userId", Integer.class) : null;
    }

    public static Integer extractRolId(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.get("rolId", Integer.class) : null;
    }

    public static String extractNombre(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.get("nombre", String.class) : null;
    }

    public static boolean isTokenExpired(String token) {
        Claims claims = validateToken(token);
        if (claims == null)
            return true;

        return claims.getExpiration().before(new Date());
    }
}
