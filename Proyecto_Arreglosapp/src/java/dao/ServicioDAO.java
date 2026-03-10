package dao;

import config.ConectionDB;
import model.Servicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {

    public List<Servicio> obtenerServicios() throws Exception {
        String sql = "SELECT arreglo_id, arreglo_nombre, arreglo_descripcion, arreglo_precio_base, " +
                "arreglo_imagen_url, arreglo_tiempo_estimado, arreglo_disponible FROM arreglos " +
                "WHERE arreglo_disponible = 1 ORDER BY arreglo_id ASC";
        List<Servicio> lista = new ArrayList<>();

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Servicio s = new Servicio();
                s.setArregloId(rs.getInt("arreglo_id"));
                s.setNombre(rs.getString("arreglo_nombre"));
                s.setDescripcion(rs.getString("arreglo_descripcion"));
                s.setPrecioBase(rs.getDouble("arreglo_precio_base"));
                s.setImagenUrl(rs.getString("arreglo_imagen_url"));
                s.setTiempoEstimado(rs.getString("arreglo_tiempo_estimado"));
                s.setDisponible(rs.getInt("arreglo_disponible") == 1);
                lista.add(s);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener servicios: " + e.getMessage());
        }
        return lista;
    }

    public Servicio obtenerPorId(int arregloId) throws Exception {
        String sql = "SELECT arreglo_id, arreglo_nombre, arreglo_descripcion, arreglo_precio_base, " +
                "arreglo_imagen_url, arreglo_tiempo_estimado, arreglo_disponible FROM arreglos WHERE arreglo_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, arregloId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Servicio s = new Servicio();
                    s.setArregloId(rs.getInt("arreglo_id"));
                    s.setNombre(rs.getString("arreglo_nombre"));
                    s.setDescripcion(rs.getString("arreglo_descripcion"));
                    s.setPrecioBase(rs.getDouble("arreglo_precio_base"));
                    s.setImagenUrl(rs.getString("arreglo_imagen_url"));
                    s.setTiempoEstimado(rs.getString("arreglo_tiempo_estimado"));
                    s.setDisponible(rs.getInt("arreglo_disponible") == 1);
                    return s;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener servicio: " + e.getMessage());
        }
        return null;
    }

    public boolean crearServicio(Servicio s) throws Exception {
        String sql = "INSERT INTO arreglos (categoria_id, arreglo_nombre, arreglo_descripcion, " +
                "arreglo_precio_base, arreglo_imagen_url, arreglo_tiempo_estimado, arreglo_disponible) " +
                "VALUES (1, ?, ?, ?, ?, ?, 1)";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.setDouble(3, s.getPrecioBase());
            ps.setString(4, s.getImagenUrl());
            ps.setString(5, s.getTiempoEstimado());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al crear servicio: " + e.getMessage());
        }
    }

    public boolean actualizarServicio(Servicio s) throws Exception {
        String sql = "UPDATE arreglos SET arreglo_nombre = ?, arreglo_descripcion = ?, " +
                "arreglo_precio_base = ?, arreglo_tiempo_estimado = ?, " +
                "arreglo_imagen_url = COALESCE(?, arreglo_imagen_url) " +
                "WHERE arreglo_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.setDouble(3, s.getPrecioBase());
            ps.setString(4, s.getTiempoEstimado());
            ps.setString(5, s.getImagenUrl());
            ps.setInt(6, s.getArregloId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al actualizar servicio: " + e.getMessage());
        }
    }

    public boolean eliminarServicio(int arregloId) throws Exception {
        String sql = "UPDATE arreglos SET arreglo_disponible = 0 WHERE arreglo_id = ?";

        try (Connection con = ConectionDB.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, arregloId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new Exception("Error al eliminar servicio: " + e.getMessage());
        }
    }
}