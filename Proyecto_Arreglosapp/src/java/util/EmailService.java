/**
 * Author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * Propósito: Proveer una utilidad para el envío de correos electrónicos mediante SMTP con reflexión.
 */
package util;

import java.util.Properties;

/**
 * Esta utilidad facilita el envío de notificaciones por correo electrónico.
 */
public final class EmailService {

    private EmailService() {
    }

    /**
     * Envía un código para que el usuario pueda recuperar su contraseña.
     * 
     * @param toEmail Dirección de destino.
     * @param code Código de seguridad generado.
     * @return Verdadero si el correo se envió correctamente, falso de lo contrario.
     */
    public static boolean sendRecoveryCode(String toEmail, String code) {
        // Obtiene la configuración del servidor SMTP desde el entorno o propiedades del sistema
        String host = envOrProp("SMTP_HOST");
        String port = envOrProp("SMTP_PORT");
        String user = envOrProp("SMTP_USER");
        String pass = envOrProp("SMTP_PASS");
        String from = envOrProp("SMTP_FROM");
        String tls = envOrProp("SMTP_TLS");

        // Verifica que los datos esenciales estén configurados
        if (isBlank(host) || isBlank(port) || isBlank(user) || isBlank(pass) || isBlank(from)) {
            System.out.println("[EmailService] SMTP no configurado. No se envía correo.");
            return false;
        }

        try {
            // Configura las propiedades de la sesión de correo
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.starttls.enable", (isBlank(tls) ? "true" : tls));
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", host);

            // Crea la sesión, el mensaje y procede al envío
            Object session = createSession(props, user, pass);
            Object message = createMessage(session, from, toEmail, code);
            sendMessage(message);
            System.out.println("[EmailService] Correo enviado a: " + toEmail);
            return true;

        } catch (Throwable ex) {
            // Captura cualquier error durante el proceso de envío
            System.out.println("[EmailService] No fue posible enviar el correo: " + ex.getClass().getName() + " - "
                    + ex.getMessage());
            return false;
        }
    }

    /**
     * Crea una sesión de correo autenticada dinámica.
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
     * Construye el contenido del mensaje de correo electrónico.
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

        messageClass.getMethod("setSubject", String.class).invoke(message, "Código de recuperación de contraseña");
        messageClass.getMethod("setText", String.class).invoke(message,
                "Tu código de recuperación es: " + code + "\n\nEste código expira en 15 minutos.");
        return message;
    }

    /**
     * Transmite el mensaje a través del servidor SMTP.
     */
    private static void sendMessage(Object message) throws Exception {
        Class<?> transportClass = Class.forName("javax.mail.Transport");
        Class<?> messageClass = Class.forName("javax.mail.Message");
        transportClass.getMethod("send", messageClass).invoke(null, message);
    }

    /**
     * Busca una configuración en las variables de entorno o en las propiedades de Java.
     */
    private static String envOrProp(String key) {
        String v = System.getenv(key);
        if (!isBlank(v))
            return v;
        return System.getProperty(key);
    }

    /**
     * Verifica si una cadena de texto está vacía o contiene solo espacios.
     */
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
