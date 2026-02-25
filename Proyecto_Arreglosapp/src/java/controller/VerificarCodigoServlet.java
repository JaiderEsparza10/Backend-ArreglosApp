package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/VerificarCodigoServlet")
public class VerificarCodigoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String codigo = request.getParameter("codigo");
        String contextPath = request.getContextPath();
        String rutaVerificar = contextPath + "/Public/auth/verificar-codigo.jsp";
        String rutaNuevaContrasena = contextPath + "/Public/auth/nueva-contrasena.jsp";

        System.out.println("=== VERIFICANDO CÓDIGO ===");
        System.out.println("Código recibido: " + codigo);

        // Validar campo vacío
        if (codigo == null || codigo.trim().isEmpty()) {
            System.out.println("ERROR: Código vacío");
            response.sendRedirect(rutaVerificar + "?msg=camposVacios");
            return;
        }

        // Validar formato de código (5 dígitos)
        if (!codigo.matches("[0-9]{5}")) {
            System.out.println("ERROR: Código inválido - formato incorrecto");
            response.sendRedirect(rutaVerificar + "?msg=codigoInvalido");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("ERROR: Sesión nula");
            response.sendRedirect(contextPath + "/Public/auth/recuperar-contrasena.jsp?msg=sesionExpirada");
            return;
        }

        String codigoGuardado = (String) session.getAttribute("codigoRecuperacion");
        String emailRecuperacion = (String) session.getAttribute("emailRecuperacion");
        Long tiempoCodigo = (Long) session.getAttribute("tiempoCodigo");

        System.out.println("Datos en sesión:");
        System.out.println("- Código guardado: " + codigoGuardado);
        System.out.println("- Email: " + emailRecuperacion);
        System.out.println("- Tiempo: " + tiempoCodigo);

        // Verificar si el código coincide
        if (!codigo.equals(codigoGuardado)) {
            System.out.println("ERROR: Código incorrecto");
            response.sendRedirect(rutaVerificar + "?msg=codigoIncorrecto");
            return;
        }

        // Verificar si el código no ha expirado (15 minutos)
        if (tiempoCodigo == null || (System.currentTimeMillis() - tiempoCodigo) > 15 * 60 * 1000) {
            System.out.println("ERROR: Código expirado");
            session.invalidate();
            response.sendRedirect(contextPath + "/Public/auth/recuperar-contrasena.jsp?msg=codigoExpirado");
            return;
        }

        // Código correcto, marcar como verificado y redirigir a nueva contraseña
        session.setAttribute("codigoVerificado", true);
        System.out.println("¡Código verificado correctamente!");
        System.out.println("Redirigiendo a nueva contraseña...");
        response.sendRedirect(rutaNuevaContrasena + "?msg=codigoVerificado");
    }
}
