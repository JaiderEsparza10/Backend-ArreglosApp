package controller;

import dao.UsuarioDAO;
import java.io.IOException;
import java.util.Random;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.EmailService;

@WebServlet("/RecuperarServlet")
public class RecuperarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        String contextPath = request.getContextPath();
        String rutaRecuperar = contextPath + "/Public/auth/recuperar-contrasena.jsp";
        String rutaVerificar = contextPath + "/Public/auth/verificar-codigo.jsp";

        if (email == null || email.trim().isEmpty()) {
            response.sendRedirect(rutaRecuperar + "?msg=camposVacios");
            return;
        }

        try {
            UsuarioDAO dao = new UsuarioDAO();
            if (!dao.existeEmail(email.trim())) {
                response.sendRedirect(rutaRecuperar + "?msg=emailNoExiste");
                return;
            }

            String codigo = generarCodigo();

            HttpSession session = request.getSession();
            session.setAttribute("codigoRecuperacion", codigo);
            session.setAttribute("emailRecuperacion", email.trim());
            session.setAttribute("tiempoCodigo", System.currentTimeMillis());

            System.out.println("=== CÓDIGO DE RECUPERACIÓN GENERADO ===");
            System.out.println("Email: " + email.trim());
            System.out.println("Código: " + codigo);
            System.out.println("=====================================");

            boolean enviado = EmailService.sendRecoveryCode(email.trim(), codigo);
            System.out.println("[RecuperarServlet] Código enviado por correo: " + enviado);

            response.sendRedirect(rutaVerificar + "?msg=codigoEnviado&email="
                    + java.net.URLEncoder.encode(email.trim(), "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(rutaRecuperar + "?msg=errorServidor");
        }
    }

    private String generarCodigo() {
        Random random = new Random();
        int codigo = 10000 + random.nextInt(90000);
        return String.valueOf(codigo);
    }
}
