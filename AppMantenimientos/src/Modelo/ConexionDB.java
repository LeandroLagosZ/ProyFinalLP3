package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class ConexionDB {
    private static ConexionDB instancia;
    private Connection conexion;
    private final String URL = "jdbc:sqlite:inventario.db";

    private ConexionDB() {
        try {
            conexion = DriverManager.getConnection(URL);
            crearTablas();
            insertarTiposMantenimientoPredefinidos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ConexionDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }

    private void crearTablas() throws SQLException {
        // Primero eliminar tablas existentes si hay problemas
        try {
            Statement dropStmt = conexion.createStatement();
            dropStmt.execute("DROP TABLE IF EXISTS EquipoMantenimiento");
            dropStmt.execute("DROP TABLE IF EXISTS Mantenimientos");
            dropStmt.execute("DROP TABLE IF EXISTS Equipos");
            dropStmt.execute("DROP TABLE IF EXISTS TiposMantenimiento");
        } catch (SQLException e) {
            System.out.println("No se pudieron eliminar tablas existentes: " + e.getMessage());
        }

        // Tabla de Tipos de Mantenimiento (CATÁLOGO)
        String sqlTiposMantenimiento = "CREATE TABLE IF NOT EXISTS TiposMantenimiento (" +
                "id INTEGER PRIMARY KEY, " +
                "nombre TEXT NOT NULL, " +
                "descripcion TEXT)";
        
        // Tabla de Equipos
        String sqlEquipos = "CREATE TABLE IF NOT EXISTS Equipos (" +
                "id INTEGER PRIMARY KEY, " +
                "nombre TEXT, " +
                "tipo TEXT)";
        
        // Tabla de Mantenimientos - CORREGIDO: agregar tipo_mantenimiento_id
        String sqlMantenimientos = "CREATE TABLE IF NOT EXISTS Mantenimientos (" +
                "id INTEGER PRIMARY KEY, " +
                "fecha TEXT, " +
                "tecnico TEXT, " +
                "costo REAL, " +
                "descripcion TEXT, " +
                "tipo_mantenimiento_id INTEGER, " + // ✅ COLUMNA AGREGADA
                "FOREIGN KEY(tipo_mantenimiento_id) REFERENCES TiposMantenimiento(id))";
        
        // Tabla intermedia para relación N:N entre Equipos y Mantenimientos
        String sqlEquipoMantenimiento = "CREATE TABLE IF NOT EXISTS EquipoMantenimiento (" +
                "equipo_id INTEGER, " +
                "mantenimiento_id INTEGER, " +
                "PRIMARY KEY (equipo_id, mantenimiento_id), " +
                "FOREIGN KEY(equipo_id) REFERENCES Equipos(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(mantenimiento_id) REFERENCES Mantenimientos(id) ON DELETE CASCADE)";
        
        Statement stmt = conexion.createStatement();
        stmt.execute(sqlTiposMantenimiento);
        stmt.execute(sqlEquipos);
        stmt.execute(sqlMantenimientos);
        stmt.execute(sqlEquipoMantenimiento);
        
        System.out.println("✅ Tablas creadas correctamente:");
        System.out.println("   - TiposMantenimiento");
        System.out.println("   - Equipos");
        System.out.println("   - Mantenimientos (con tipo_mantenimiento_id)");
        System.out.println("   - EquipoMantenimiento");
    }

    private void insertarTiposMantenimientoPredefinidos() {
        String sql = "INSERT OR IGNORE INTO TiposMantenimiento (id, nombre, descripcion) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            // Tipos predefinidos
            String[][] tipos = {
                {"1", "Limpieza", "Limpieza general del equipo"},
                {"2", "Reparación", "Reparación de componentes dañados"},
                {"3", "Cambio de componentes", "Sustitución de piezas"},
                {"4", "Actualización", "Actualización de software/firmware"},
                {"5", "Revisión preventiva", "Revisión general preventiva"}
            };
            
            for (String[] tipo : tipos) {
                pstmt.setInt(1, Integer.parseInt(tipo[0]));
                pstmt.setString(2, tipo[1]);
                pstmt.setString(3, tipo[2]);
                pstmt.executeUpdate();
            }
            System.out.println("✅ Tipos de mantenimiento predefinidos insertados");
        } catch (SQLException e) {
            System.err.println("Error al insertar tipos predefinidos: " + e.getMessage());
        }
    }
}