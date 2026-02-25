package util;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.nio.charset.StandardCharsets;

public class SimpleAuthUtil {
    
    private static final String SECRET_KEY = "ArreglosAppSecretKey2024ParaJWTTokenGeneration";
    private static final long EXPIRATION_TIME = 86400000; // 24 horas
    
    public static String generateToken(String email, int userId, int rolId, String nombre) {
        try {
            long expirationTime = System.currentTimeMillis() + EXPIRATION_TIME;
            
            // Crear payload
            String payload = String.format("{\"email\":\"%s\",\"userId\":%d,\"rolId\":%d,\"nombre\":\"%s\",\"iat\":%d,\"exp\":%d}",
                email, userId, rolId, nombre, System.currentTimeMillis(), expirationTime);
            
            // Firmar con HMAC-SHA256
            String signature = calculateHMAC(payload, SECRET_KEY);
            
            // Crear token simple: header.payload.signature
            String header = Base64.getEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
            String encodedPayload = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
            
            return header + "." + encodedPayload + "." + signature;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Map<String, Object> validateToken(String token) {
        try {
            if (token == null || !token.contains(".")) {
                return null;
            }
            
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            
            // Decodificar payload
            String payload = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            
            // Verificar firma
            String expectedSignature = calculateHMAC(payload, SECRET_KEY);
            if (!expectedSignature.equals(parts[2])) {
                return null;
            }
            
            // Parsear payload simple
            Map<String, Object> claims = new HashMap<>();
            payload = payload.replace("{", "").replace("}", "").replace("\"", "");
            
            String[] pairs = payload.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    
                    if (key.equals("email") || key.equals("nombre")) {
                        claims.put(key, value);
                    } else if (key.equals("userId") || key.equals("rolId") || key.equals("iat") || key.equals("exp")) {
                        try {
                            claims.put(key, Long.parseLong(value));
                        } catch (NumberFormatException e) {
                            claims.put(key, 0);
                        }
                    }
                }
            }
            
            // Verificar expiración
            Long exp = (Long) claims.get("exp");
            if (exp != null && exp < System.currentTimeMillis()) {
                return null;
            }
            
            return claims;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String extractEmail(String token) {
        Map<String, Object> claims = validateToken(token);
        return claims != null ? (String) claims.get("email") : null;
    }
    
    public static Integer extractUserId(String token) {
        Map<String, Object> claims = validateToken(token);
        if (claims != null && claims.get("userId") instanceof Long) {
            return ((Long) claims.get("userId")).intValue();
        }
        return null;
    }
    
    public static Integer extractRolId(String token) {
        Map<String, Object> claims = validateToken(token);
        if (claims != null && claims.get("rolId") instanceof Long) {
            return ((Long) claims.get("rolId")).intValue();
        }
        return null;
    }
    
    public static String extractNombre(String token) {
        Map<String, Object> claims = validateToken(token);
        return claims != null ? (String) claims.get("nombre") : null;
    }
    
    private static String calculateHMAC(String data, String key) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}
