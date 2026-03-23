/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Proveer métodos para la creación, validación y extracción de datos de tokens JWT (JSON Web Tokens).
 */
package util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

/**
 * Esta utilidad gestiona la seguridad de las sesiones mediante tokens compactos y verificables.
 */
public class JWTUtil {

    // Clave secreta generada automáticamente para firmar los tokens
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // Tiempo de vida del token establecido en 24 horas
    private static final long EXPIRATION_TIME = 86400000; 

    /**
     * Genera un nuevo token JWT para un usuario autenticado.
     * 
     * @param email Correo electrónico del usuario.
     * @param userId Identificador único del usuario.
     * @param rolId Identificador del rol asignado.
     * @param nombre Nombre del usuario.
     * @return Una cadena que representa el token JWT generado.
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

    /**
     * Valida el token proporcionado y extrae sus reclamos (claims).
     * 
     * @param token El token JWT a validar.
     * @return El objeto Claims si el token es válido, o null si falla la validación.
     */
    public static Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // Retorna nulo si el token ha sido alterado o ha expirado
            return null;
        }
    }

    /**
     * Extrae el correo electrónico contenido en el token.
     */
    public static String extractEmail(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * Recupera el identificador de usuario almacenado en el token.
     */
    public static Integer extractUserId(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.get("userId", Integer.class) : null;
    }

    /**
     * Obtiene el identificador de rol registrado en el token.
     */
    public static Integer extractRolId(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.get("rolId", Integer.class) : null;
    }

    /**
     * Extrae el nombre del usuario desde el token.
     */
    public static String extractNombre(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.get("nombre", String.class) : null;
    }

    /**
     * Determina si el token ha superado su fecha de expiración.
     * 
     * @param token El token a verificar.
     * @return Verdadero si el token ya no es válido por tiempo, falso de lo contrario.
     */
    public static boolean isTokenExpired(String token) {
        Claims claims = validateToken(token);
        if (claims == null)
            return true;

        // Compara la fecha de expiración con la fecha y hora actual
        return claims.getExpiration().before(new Date());
    }
}
