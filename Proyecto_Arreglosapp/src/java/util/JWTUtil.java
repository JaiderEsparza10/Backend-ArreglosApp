/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: JWTUtil.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Motor de seguridad y provisión de Identidad (JWT).
 *               Gestiona el ciclo de vida de los tokens de sesión, 
 *               incluyendo la firma criptográfica (HS256), validación de 
 *               expiración y extracción de Claims (Metadatos de Usuario).
 * ══════════════════════════════════════════════════════════════════════════════
 */
package util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

/**
 * Utilidad criptográfica para la gestión de JSON Web Tokens (JWT).
 * Proporciona una capa de abstracción sobre la librería JJWT para manejar la seguridad stateless.
 */
public class JWTUtil {

    // Semilla criptográfica generada dinámicamente para la firma HS256
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // TTL (Time To Live): Duración de validez del token (24 Horas)
    private static final long EXPIRATION_TIME = 86400000; 

    /**
     * Genera un token compacto y firmado que encapsula la identidad del usuario.
     * 
     * @param email  Identificador primario (Subject).
     * @param userId ID único de base de datos.
     * @param rolId  Privilegios de acceso (RBAC).
     * @param nombre Alias descriptivo para la UI.
     * @return String con estructura Header.Payload.Signature.
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
     * Valida la integridad estructural y temporal de un token.
     * 
     * @param token Cadena JWT proveniente de la sesión o cabecera.
     * @return Claims si el token es íntegro y vigente; null si fue alterado o expiró.
     */
    public static Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // Se silencia la excepción para retornar un estado booleano/nulo controlado
            return null;
        }
    }

    /**
     * Accesor: Recupera el Email (Subject) del token.
     */
    public static String extractEmail(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * Accesor: Recupera el ID de usuario interno.
     */
    public static Integer extractUserId(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.get("userId", Integer.class) : null;
    }

    /**
     * Accesor: Recupera el Rol Id para lógica de autorización.
     */
    public static Integer extractRolId(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.get("rolId", Integer.class) : null;
    }

    /**
     * Accesor: Recupera el nombre legible del portador.
     */
    public static String extractNombre(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.get("nombre", String.class) : null;
    }

    /**
     * Validador Temporal: Verifica si el token ha superado su fecha de expiración.
     * 
     * @param token JWT a evaluar.
     * @return true si la sesión debe ser renovada o finalizada.
     */
    public static boolean isTokenExpired(String token) {
        Claims claims = validateToken(token);
        if (claims == null)
            return true;

        // Evaluación cronológica contra el reloj del servidor
        return claims.getExpiration().before(new Date());
    }
}
