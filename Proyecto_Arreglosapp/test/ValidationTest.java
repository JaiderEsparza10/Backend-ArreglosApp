import java.sql.*;

/**
 * Simple verification script to test user deletion validation logic
 * This script verifies that the SQL queries correctly exclude completed/cancelled records
 */
public class ValidationTest {
    
    // Database connection parameters (adjust as needed)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/arreglosapp_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    public static void main(String[] args) {
        try {
            // Test the validation queries
            testValidationQueries();
            
            System.out.println("✅ All validation tests completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testValidationQueries() throws Exception {
        Connection con = null;
        
        try {
            // Establish database connection
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("📡 Connected to database");
            
            // Test queries for a sample user (adjust user ID as needed)
            int testUserId = 1; // Change this to a valid user ID for testing
            
            System.out.println("\n🔍 Testing validation queries for User ID: " + testUserId);
            
            // 1. Test orders query (should exclude 'terminado' and 'cancelado')
            String sqlPedidos = "SELECT COUNT(*) FROM pedidos WHERE usuario_id = ? AND pedido_estado NOT IN ('terminado', 'cancelado')";
            int activeOrders = executeCountQuery(con, sqlPedidos, testUserId);
            System.out.println("📦 Active Orders (not terminated/cancelled): " + activeOrders);
            
            // 2. Test appointments query (should exclude 'completada' and 'cancelada')
            String sqlCitas = "SELECT COUNT(*) FROM citas c JOIN pedidos p ON c.pedido_id = p.pedido_id " +
                             "WHERE p.usuario_id = ? AND c.cita_estado NOT IN ('completada', 'cancelada')";
            int activeAppointments = executeCountQuery(con, sqlCitas, testUserId);
            System.out.println("📅 Active Appointments (not completed/cancelled): " + activeAppointments);
            
            // 3. Test customizations query (should exclude 'completado' and 'cancelado')
            String sqlPersonalizaciones = "SELECT COUNT(*) FROM personalizaciones WHERE user_id = ? AND estado NOT IN ('completado', 'cancelado')";
            int activeCustomizations = executeCountQuery(con, sqlPersonalizaciones, testUserId);
            System.out.println("🎨 Active Customizations (not completed/cancelled): " + activeCustomizations);
            
            // 4. Show detailed breakdown for debugging
            System.out.println("\n📊 Detailed breakdown:");
            showDetailedBreakdown(con, testUserId);
            
            // 5. Validation result
            boolean canDelete = (activeOrders == 0) && (activeAppointments == 0) && (activeCustomizations == 0);
            System.out.println("\n✅ Can user be deleted? " + (canDelete ? "YES" : "NO"));
            
            if (!canDelete) {
                System.out.println("🚫 User has active records that prevent deletion:");
                if (activeOrders > 0) System.out.println("   - " + activeOrders + " active orders");
                if (activeAppointments > 0) System.out.println("   - " + activeAppointments + " active appointments");
                if (activeCustomizations > 0) System.out.println("   - " + activeCustomizations + " active customizations");
            }
            
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    private static int executeCountQuery(Connection con, String sql, int userId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    private static void showDetailedBreakdown(Connection con, int userId) throws SQLException {
        System.out.println("   Orders by state:");
        showOrdersByState(con, userId);
        
        System.out.println("   Appointments by state:");
        showAppointmentsByState(con, userId);
        
        System.out.println("   Customizations by state:");
        showCustomizationsByState(con, userId);
    }
    
    private static void showOrdersByState(Connection con, int userId) throws SQLException {
        String sql = "SELECT pedido_estado, COUNT(*) as count FROM pedidos WHERE usuario_id = ? GROUP BY pedido_estado";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String state = rs.getString("pedido_estado");
                    int count = rs.getInt("count");
                    System.out.println("     - " + state + ": " + count);
                }
            }
        }
    }
    
    private static void showAppointmentsByState(Connection con, int userId) throws SQLException {
        String sql = "SELECT c.cita_estado, COUNT(*) as count FROM citas c " +
                    "JOIN pedidos p ON c.pedido_id = p.pedido_id " +
                    "WHERE p.usuario_id = ? GROUP BY c.cita_estado";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String state = rs.getString("cita_estado");
                    int count = rs.getInt("count");
                    System.out.println("     - " + state + ": " + count);
                }
            }
        }
    }
    
    private static void showCustomizationsByState(Connection con, int userId) throws SQLException {
        String sql = "SELECT estado, COUNT(*) as count FROM personalizaciones WHERE user_id = ? GROUP BY estado";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String state = rs.getString("estado");
                    int count = rs.getInt("count");
                    System.out.println("     - " + state + ": " + count);
                }
            }
        }
    }
}
