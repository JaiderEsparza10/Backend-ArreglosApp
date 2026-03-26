/**
 * ══════════════════════════════════════════════════════════════════════════════
 * @file: PedidoDAO.java
 * @author: Jaider Andres Esparza Arenas con ayuda de Antigravity.
 * @version: 1.1
 * @description: Orquestador de la persistencia y consulta de órdenes de servicio.
 *               Implementa consultas complejas (JOINs) para reconstruir la 
 *               trazabilidad completa: Cliente -> Pedido -> Arreglo -> 
 *               Personalización -> Servicio -> Cita.
 * ══════════════════════════════════════════════════════════════════════════════
 */
package dao;

import config.ConectionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase de Acceso a Datos (DAO) para el motor de pedidos.
 * Gestiona el ciclo de vida comercial (Pendiente, En Proceso, Terminado, Cancelado).
 */
public class PedidoDAO {

    /**
     * Recupera las órdenes vigentes que requieren atención operativa.
     * Incluye estados: 'pendiente', 'confirmado', 'en_proceso'.
     * 
     * @param userId Identificador del cliente.
     * @return Lista de mapas con la hidratación completa del pedido.
     * @throws Exception Error en la resolución de los 5 niveles de JOIN.
     */
    public List<Map<String, Object>> obtenerPedidosActivos(int userId) throws Exception {
        // Consulta multi-tabla para reconstruir el objeto de negocio complejo
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, p.pedido_total, " +
                "s.servicio_id, s.servicio_nombre, s.servicio_descripcion, s.servicio_precio_base, " +
                "a.arreglo_id, a.arreglo_nombre, a.arreglo_descripcion, a.arreglo_imagen_url, " +
                "per.descripcion as personalizacion_descripcion, per.material_tela, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, c.cita_direccion_entrega " +
                "FROM PEDIDOS p " +
                "LEFT JOIN DETALLE_PEDIDO dp ON p.pedido_id = dp.pedido_id " +
                "LEFT JOIN ARREGLOS a ON dp.arreglo_id = a.arreglo_id " +
                "LEFT JOIN PERSONALIZACIONES per ON a.personalizacion_id = per.personalizacion_id " +
                "LEFT JOIN SERVICIOS s ON per.servicio_id = s.servicio_id " +
                "LEFT JOIN CITAS c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND p.pedido_estado IN ('pendiente','confirmado','en_proceso') " +
                "ORDER BY p.pedido_fecha DESC";
        return ejecutarConsulta(sql, userId);
    }

    /**
     * Recupera el archivo histórico de transacciones finalizadas.
     * Filtra por estados terminales: 'terminado', 'cancelado'.
     * 
     * @param userId Propietario de la cuenta.
     * @return Lista de pedidos históricos.
     * @throws Exception Error JDBC.
     */
    public List<Map<String, Object>> obtenerHistorialPedidos(int userId) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, p.pedido_total, " +
                "s.servicio_id, s.servicio_nombre, s.servicio_descripcion, s.servicio_precio_base, " +
                "a.arreglo_id, a.arreglo_nombre, a.arreglo_descripcion, a.arreglo_imagen_url, " +
                "per.descripcion as personalizacion_descripcion, per.material_tela, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, c.cita_direccion_entrega " +
                "FROM PEDIDOS p " +
                "LEFT JOIN DETALLE_PEDIDO dp ON p.pedido_id = dp.pedido_id " +
                "LEFT JOIN ARREGLOS a ON dp.arreglo_id = a.arreglo_id " +
                "LEFT JOIN PERSONALIZACIONES per ON a.personalizacion_id = per.personalizacion_id " +
                "LEFT JOIN SERVICIOS s ON per.servicio_id = s.servicio_id " +
                "LEFT JOIN CITAS c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND p.pedido_estado IN ('terminado','cancelado') " +
                "ORDER BY p.pedido_fecha DESC";
        return ejecutarConsulta(sql, userId);
    }

    /**
     * Consulta parametrizada por un estado específico.
     * 
     * @param userId Identificador del cliente.
     * @param estado Valor del ENUM pedido_estado.
     * @return Colección filtrada.
     * @throws Exception Error en ResultSet mapping.
     */
    public List<Map<String, Object>> obtenerPedidosPorEstado(int userId, String estado) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, p.pedido_total, " +
                "s.servicio_id, s.servicio_nombre, s.servicio_descripcion, s.servicio_precio_base, " +
                "a.arreglo_id, a.arreglo_nombre, a.arreglo_descripcion, a.arreglo_imagen_url, " +
                "per.descripcion as personalizacion_descripcion, per.material_tela, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, c.cita_direccion_entrega " +
                "FROM PEDIDOS p " +
                "LEFT JOIN DETALLE_PEDIDO dp ON p.pedido_id = dp.pedido_id " +
                "LEFT JOIN ARREGLOS a ON dp.arreglo_id = a.arreglo_id " +
                "LEFT JOIN PERSONALIZACIONES per ON a.personalizacion_id = per.personalizacion_id " +
                "LEFT JOIN SERVICIOS s ON per.servicio_id = s.servicio_id " +
                "LEFT JOIN CITAS c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND p.pedido_estado = ? " +
                "ORDER BY p.pedido_fecha DESC";
        return ejecutarConsulta(sql, userId, estado);
    }

    /**
     * Motor de búsqueda por coincidencia parcial en el catálogo.
     * 
     * @param userId         Propietario de la cuenta.
     * @param nombreServicio Término de búsqueda (LIKE).
     * @return Lista de pedidos que contienen el servicio buscado.
     * @throws Exception Error SQL.
     */
    public List<Map<String, Object>> buscarPedidosPorNombreServicio(int userId, String nombreServicio) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, p.pedido_total, " +
                "s.servicio_id, s.servicio_nombre, s.servicio_descripcion, s.servicio_precio_base, " +
                "a.arreglo_id, a.arreglo_nombre, a.arreglo_descripcion, a.arreglo_imagen_url, " +
                "per.descripcion as personalizacion_descripcion, per.material_tela, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, c.cita_direccion_entrega " +
                "FROM PEDIDOS p " +
                "LEFT JOIN DETALLE_PEDIDO dp ON p.pedido_id = dp.pedido_id " +
                "LEFT JOIN ARREGLOS a ON dp.arreglo_id = a.arreglo_id " +
                "LEFT JOIN PERSONALIZACIONES per ON a.personalizacion_id = per.personalizacion_id " +
                "LEFT JOIN SERVICIOS s ON per.servicio_id = s.servicio_id " +
                "LEFT JOIN CITAS c ON p.pedido_id = c.pedido_id " +
                "WHERE p.usuario_id = ? AND s.servicio_nombre LIKE ? " +
                "ORDER BY p.pedido_fecha DESC";
        return ejecutarConsulta(sql, userId, "%" + nombreServicio + "%");
    }

    /**
     * Permite al cliente desistir de una solicitud antes de que inicie la ejecución física.
     * Regla: Solo pedidos en 'pendiente' o 'confirmado' pueden ser cancelados por esta vía.
     * 
     * @param pedidoId Identificador de la orden.
     * @param userId   Validación de propiedad.
     * @return true si la actualización fue exitosa.
     * @throws Exception Violación de reglas de negocio o fallo SQL.
     */
    public boolean cancelarPedido(int pedidoId, int userId) throws Exception {
        String sql = "UPDATE PEDIDOS SET pedido_estado = 'cancelado', " +
                "pedido_fecha = CURRENT_TIMESTAMP " +
                "WHERE pedido_id = ? AND usuario_id = ? " +
                "AND pedido_estado IN ('pendiente','confirmado')";
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al cancelar pedido: " + e.getMessage());
        }
    }

    /**
     * Vista administrativa global de todas las operaciones comerciales.
     * Enriquecida con datos de contacto del cliente para gestión de CRM.
     * 
     * @return Lista exhaustiva de órdenes en el sistema.
     * @throws Exception Error en JOINs de 6 niveles (Pedidos+Detalles+Arreglos+Personalizaciones+Servicios+Usuarios+Citas).
     */
    public List<Map<String, Object>> listarTodos() throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, p.pedido_total, p.usuario_id, " +
                "s.servicio_id, s.servicio_nombre, s.servicio_descripcion, s.servicio_precio_base, " +
                "a.arreglo_id, a.arreglo_nombre, a.arreglo_descripcion, a.arreglo_imagen_url, " +
                "per.descripcion as personalizacion_descripcion, per.material_tela, " +
                "u.user_nombre, u.user_email, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, c.cita_direccion_entrega " +
                "FROM pedidos p " +
                "LEFT JOIN detalle_pedido dp ON p.pedido_id = dp.pedido_id " +
                "LEFT JOIN arreglos a ON dp.arreglo_id = a.arreglo_id " +
                "LEFT JOIN personalizaciones per ON a.personalizacion_id = per.personalizacion_id " +
                "LEFT JOIN servicios s ON per.servicio_id = s.servicio_id " +
                "LEFT JOIN usuarios u ON p.usuario_id = u.user_id " +
                "LEFT JOIN citas c ON p.pedido_id = c.pedido_id " +
                "ORDER BY p.pedido_fecha DESC";
        return ejecutarConsultaAdmin(sql);
    }

    /**
     * Filtrado administrativo por estado operativo.
     * Útil para segmentar el flujo de trabajo en el tablero de control.
     * 
     * @param estado Criterio de segmentación ('pendiente', 'confirmado', etc).
     * @return Lista segmentada de pedidos.
     * @throws Exception Error JDBC.
     */
    public List<Map<String, Object>> listarPorEstado(String estado) throws Exception {
        String sql = "SELECT p.pedido_id, p.pedido_estado, p.pedido_fecha, p.pedido_total, p.usuario_id, " +
                "s.servicio_id, s.servicio_nombre, s.servicio_descripcion, s.servicio_precio_base, " +
                "a.arreglo_id, a.arreglo_nombre, a.arreglo_descripcion, a.arreglo_imagen_url, " +
                "per.descripcion as personalizacion_descripcion, per.material_tela, " +
                "u.user_nombre, u.user_email, " +
                "c.cita_id, c.cita_fecha_hora, c.cita_estado, c.cita_notas, c.cita_motivo, c.cita_direccion_entrega " +
                "FROM pedidos p " +
                "LEFT JOIN detalle_pedido dp ON p.pedido_id = dp.pedido_id " +
                "LEFT JOIN arreglos a ON dp.arreglo_id = a.arreglo_id " +
                "LEFT JOIN personalizaciones per ON a.personalizacion_id = per.personalizacion_id " +
                "LEFT JOIN servicios s ON per.servicio_id = s.servicio_id " +
                "LEFT JOIN usuarios u ON p.usuario_id = u.user_id " +
                "LEFT JOIN citas c ON p.pedido_id = c.pedido_id " +
                "WHERE p.pedido_estado = ? " +
                "ORDER BY p.pedido_fecha DESC";
        return ejecutarConsultaAdmin(sql, estado);
    }

    /**
     * Motor de mapeo genérico para consultas de cliente.
     * Transforma un ResultSet en una estructura desacoplada (Map) para facilitar
     * la serialización a JSON o el acceso dinámico desde el JSP.
     * 
     * @param sql    Sentencia SQL con placeholders (?).
     * @param params Argumentos de filtrado.
     * @return Colección de objetos hidratados.
     * @throws Exception Error de mapeo o conexión.
     */
    private List<Map<String, Object>> ejecutarConsulta(String sql, Object... params) throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            // Inyección segura de parámetros para prevenir SQL Injection
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // LinkedHashMap preserva el orden de inserción de las llaves
                    Map<String, Object> p = new LinkedHashMap<>();
                    
                    // Bloque A: Metadata del Pedido
                    p.put("pedidoId", rs.getInt("pedido_id"));
                    p.put("pedidoEstado", rs.getString("pedido_estado"));
                    p.put("pedidoTotal", rs.getDouble("pedido_total"));
                    if (rs.getTimestamp("pedido_fecha") != null) {
                        p.put("pedidoFecha", rs.getTimestamp("pedido_fecha").toLocalDateTime());
                    }
                    
                    // Bloque B: Especificaciones del Servicio Base
                    p.put("servicioId", rs.getInt("servicio_id"));
                    p.put("servicioNombre", rs.getString("servicio_nombre"));
                    p.put("servicioDescripcion", rs.getString("servicio_descripcion"));
                    p.put("servicioPrecioBase", rs.getDouble("servicio_precio_base"));
                    
                    // Bloque C: Detalle del Arreglo Materializado
                    p.put("arregloId", rs.getInt("arreglo_id"));
                    p.put("arregloNombre", rs.getString("arreglo_nombre"));
                    p.put("arregloDescripcion", rs.getString("arreglo_descripcion"));
                    p.put("arregloImagenUrl", rs.getString("arreglo_imagen_url"));
                    
                    // Bloque D: Detalles de Personalización Técnica
                    p.put("personalizacionDescripcion", rs.getString("personalizacion_descripcion"));
                    p.put("materialTela", rs.getString("material_tela"));
                    
                    // Bloque E: Logística vinculada (Cita)
                    p.put("citaId", rs.getInt("cita_id"));
                    if (rs.getTimestamp("cita_fecha_hora") != null) {
                        p.put("citaFechaHora", rs.getTimestamp("cita_fecha_hora").toLocalDateTime());
                    }
                    p.put("citaEstado", rs.getString("cita_estado"));
                    p.put("citaNotas", rs.getString("cita_notas"));
                    p.put("citaMotivo", rs.getString("cita_motivo"));
                    p.put("citaDireccion", rs.getString("cita_direccion_entrega"));
                    
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al ejecutar consulta de pedidos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Motor de mapeo especializado para el panel administrativo.
     * Extiende la hidratación básica incluyendo la identidad del cliente (Nombre/Email).
     * 
     * @param sql    Query administrativa con JOIN a la tabla de usuarios.
     * @param params Filtros dinámicos.
     * @return Lista de mapas con trazabilidad administrativa total.
     * @throws Exception Error JDBC.
     */
    private List<Map<String, Object>> ejecutarConsultaAdmin(String sql, Object... params) throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> p = new LinkedHashMap<>();
                    
                    // Datos básicos de la orden
                    p.put("pedidoId", rs.getInt("pedido_id"));
                    p.put("pedidoEstado", rs.getString("pedido_estado"));
                    p.put("pedidoTotal", rs.getDouble("pedido_total"));
                    p.put("usuarioId", rs.getInt("usuario_id"));
                    if (rs.getTimestamp("pedido_fecha") != null) {
                        p.put("pedidoFecha", rs.getTimestamp("pedido_fecha").toLocalDateTime());
                    }
                    
                    // Especificaciones técnicas
                    p.put("servicioId", rs.getInt("servicio_id"));
                    p.put("servicioNombre", rs.getString("servicio_nombre"));
                    p.put("servicioDescripcion", rs.getString("servicio_descripcion"));
                    p.put("servicioPrecioBase", rs.getDouble("servicio_precio_base"));
                    
                    // Entidad de confección (Arreglo)
                    p.put("arregloId", rs.getInt("arreglo_id"));
                    p.put("arregloNombre", rs.getString("arreglo_nombre"));
                    p.put("arregloDescripcion", rs.getString("arreglo_descripcion"));
                    p.put("arregloImagenUrl", rs.getString("arreglo_imagen_url"));
                    
                    // Requerimientos del cliente
                    p.put("personalizacionDescripcion", rs.getString("personalizacion_descripcion"));
                    p.put("materialTela", rs.getString("material_tela"));
                    
                    // Identidad del cliente (Exclusivo Admin)
                    p.put("userNombre", rs.getString("user_nombre"));
                    p.put("userEmail", rs.getString("user_email"));
                    
                    // Datos logísticos (Cita)
                    p.put("citaId", rs.getInt("cita_id"));
                    if (rs.getTimestamp("cita_fecha_hora") != null) {
                        p.put("citaFechaHora", rs.getTimestamp("cita_fecha_hora").toLocalDateTime());
                    }
                    p.put("citaEstado", rs.getString("cita_estado"));
                    p.put("citaNotas", rs.getString("cita_notas"));
                    p.put("citaMotivo", rs.getString("cita_motivo"));
                    p.put("citaDireccion", rs.getString("cita_direccion_entrega"));
                    
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al ejecutar consulta de pedidos admin: " + e.getMessage());
        }
        return lista;
    }
}