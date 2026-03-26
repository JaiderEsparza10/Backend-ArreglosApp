/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: EmailService.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Motor de mensajería asíncrona (SMTP) diseñado mediante 
 *               Reflexión Computacional. Facilita el desacoplamiento de 
 *               librerías de correo (Jakarta/JavaMail) permitiendo el envío 
 *               dinámico de códigos de recuperación y notificaciones.
 * ══════════════════════════════════════════════════════════════════════════════
 */
package util;

import java.util.Properties;

/**
 * Utilidad maestra para la gestión de comunicaciones vía correo electrónico.
 * Implementa una arquitectura basada en reflexión para evitar dependencias rígidas
 * de compilación con Javax.Mail o Jakarta.Mail en entornos híbridos.
 */
public final class EmailService {

    /**
     * Constructor privado para prevenir la instanciación de una clase utilitaria.
     */
    private EmailService() {
    }

    /**
     * Tramita el envío de un código de seguridad para la restauración de credenciales.
     * 
     * @param toEmail Receptáculo del mensaje (Correo del usuario).
     * @param code    Token numérico/alfanumérico de validación.
     * @return true si la transmisión SMTP fue exitosa; false en caso de fallo de red o config.
     */
    public static boolean sendRecoveryCode(String toEmail, String code) {
        // Orquestación de parámetros desde variables de entorno o propiedades de JVM
        String host = envOrProp("SMTP_HOST");
        String port = envOrProp("SMTP_PORT");
        String user = envOrProp("SMTP_USER");
        String pass = envOrProp("SMTP_PASS");
        String from = envOrProp("SMTP_FROM");
        String tls = envOrProp("SMTP_TLS");

        // Regla de Negocio: Silenciar el servicio si no hay infraestructura configurada
        if (isBlank(host) || isBlank(port) || isBlank(user) || isBlank(pass) || isBlank(from)) {
            System.out.println("WARN [EmailService]: Infraestructura SMTP no detectada. El correo se ha omitido.");
            return false;
        }

        try {
            // Configuración del stack de propiedades para la sesión segura
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.starttls.enable", (isBlank(tls) ? "true" : tls));
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", host);

            // Ejecución dinámica de la lógica de envío mediante el puente de reflexión
            Object session = createSession(props, user, pass);
            Object message = createMessage(session, from, toEmail, code);
            sendMessage(message);
            
            System.out.println("INFO [EmailService]: Notificación de recuperación expedida a: " + toEmail);
            return true;

        } catch (Throwable ex) {
            // Captura de excepciones de bajo nivel para prevenir ruptura del flujo de negocio
            System.out.println("ERROR [EmailService]: Fallo crítico en el túnel SMTP: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            return false;
        }
    }

    /**
     * Factory Method (Reflexión): Instancia una sesión de correo autenticada.
     */
    private static Object createSession(Properties props, String user, String pass) throws Exception {
        Class<?> sessionClass = Class.forName("javax.mail.Session");
        Class<?> authenticatorClass = Class.forName("javax.mail.Authenticator");
        Class<?> passwordAuthClass = Class.forName("javax.mail.PasswordAuthentication");

        Object authenticator = java.lang.reflect.Proxy.newProxyInstance(
                authenticatorClass.getClassLoader(),
                new Class<?>[] { authenticatorClass },
                (proxy, method, args) -> {
                    if ("getPasswordAuthentication".equals(method.getName())) {
                        return passwordAuthClass.getConstructor(String.class, String.class).newInstance(user, pass);
                    }
                    return null;
                });

        return sessionClass.getMethod("getInstance", Properties.class, authenticatorClass)
                .invoke(null, props, authenticator);
    }

    /**
     * Builder Method (Reflexión): Construye la estructura MIME del mensaje.
     */
    private static Object createMessage(Object session, String from, String toEmail, String code) throws Exception {
        Class<?> messageClass = Class.forName("javax.mail.Message");
        Class<?> mimeMessageClass = Class.forName("javax.mail.internet.MimeMessage");
        Class<?> internetAddressClass = Class.forName("javax.mail.internet.InternetAddress");
        Class<?> recipientTypeClass = Class.forName("javax.mail.Message$RecipientType");
        Class<?> internetAddressArrayClass = Class.forName("[Ljavax.mail.internet.InternetAddress;");

        Object message = mimeMessageClass.getConstructor(Class.forName("javax.mail.Session")).newInstance(session);

        Object fromAddr = internetAddressClass.getConstructor(String.class).newInstance(from);
        messageClass.getMethod("setFrom", Class.forName("javax.mail.Address")).invoke(message, fromAddr);

        Object toAddrs = internetAddressClass.getMethod("parse", String.class).invoke(null, toEmail);
        Object toType = recipientTypeClass.getField("TO").get(null);
        messageClass.getMethod("setRecipients", recipientTypeClass, internetAddressArrayClass).invoke(message, toType,
                toAddrs);

        messageClass.getMethod("setSubject", String.class).invoke(message, "ArreglosApp: Código de recuperación de contraseña");
        messageClass.getMethod("setText", String.class).invoke(message,
                "Se ha solicitado un cambio de credenciales.\n\n" +
                "Tu token de seguridad es: " + code + "\n\n" +
                "Por seguridad, este código tiene una vigencia limitada (15 min).");
        return message;
    }

    /**
     * Dispatcher Method (Reflexión): Transmite el objeto mensaje al transportador SMTP.
     */
    private static void sendMessage(Object message) throws Exception {
        Class<?> transportClass = Class.forName("javax.mail.Transport");
        Class<?> messageClass = Class.forName("javax.mail.Message");
        transportClass.getMethod("send", messageClass).invoke(null, message);
    }

    /**
     * Selector de Configuración: Resuelve valores de entorno con fallback a System Properties.
     */
    private static String envOrProp(String key) {
        String v = System.getenv(key);
        if (!isBlank(v))
            return v;
        return System.getProperty(key);
    }

    /**
     * Validador de cadenas: Determina nulidad o vacuidad de texto.
     */
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
