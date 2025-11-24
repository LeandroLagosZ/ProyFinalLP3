package Modelo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepositorioSQL implements IRepositorioInventario {
    
    private Connection conn;

    public RepositorioSQL() {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    @Override
    public void agregarAsociacion(Equipo e, Mantenimiento m) {
        try {
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Guardar equipo
            guardarEquipo(e);
            
            // 2. Guardar tipo de mantenimiento si es nuevo
            guardarTipoMantenimiento(m.getTipo());
            
            // 3. Guardar mantenimiento
            guardarMantenimiento(m);
            
            // 4. Crear asociación N:N
            asociarEquipoMantenimiento(e.getId(), m.getId());
            
            conn.commit(); // Confirmar transacción
            System.out.println("✅ Asociación agregada: Equipo " + e.getId() + " - Mantenimiento " + m.getId());
            
        } catch (SQLException ex) {
            try {
                conn.rollback(); // Revertir en caso de error
            } catch (SQLException rollbackEx) {
                System.err.println("Error en rollback: " + rollbackEx.getMessage());
            }
            throw new RuntimeException("Error al agregar asociación: " + ex.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Error restaurando auto-commit: " + ex.getMessage());
            }
        }
    }

    private void guardarEquipo(Equipo e) throws SQLException {
        String sql = "INSERT OR REPLACE INTO Equipos (id, nombre, tipo) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, e.getId());
            pstmt.setString(2, e.getNombre());
            pstmt.setString(3, e.getTipo());
            pstmt.executeUpdate();
        }
    }

    private void guardarTipoMantenimiento(TipoMantenimiento tipo) throws SQLException {
        String sql = "INSERT OR IGNORE INTO TiposMantenimiento (id, nombre, descripcion) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tipo.getId());
            pstmt.setString(2, tipo.getNombre());
            pstmt.setString(3, tipo.getDescripcion());
            pstmt.executeUpdate();
        }
    }

    private void guardarMantenimiento(Mantenimiento m) throws SQLException {
        String sql = "INSERT OR REPLACE INTO Mantenimientos (id, fecha, tecnico, costo, descripcion, tipo_mantenimiento_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, m.getId());
            pstmt.setString(2, m.getFecha().toString());
            pstmt.setString(3, m.getTecnico());
            pstmt.setDouble(4, m.getCosto());
            pstmt.setString(5, m.getObservaciones());
            pstmt.setInt(6, m.getTipo().getId()); // ✅ Esta es la clave foránea
            pstmt.executeUpdate();
        }
    }

    private void asociarEquipoMantenimiento(int equipoId, int mantenimientoId) throws SQLException {
        String sql = "INSERT OR IGNORE INTO EquipoMantenimiento (equipo_id, mantenimiento_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, equipoId);
            pstmt.setInt(2, mantenimientoId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<ParAsociado<Equipo, Mantenimiento>> listar() {
        List<ParAsociado<Equipo, Mantenimiento>> lista = new ArrayList<>();
        
        String sql = "SELECT e.id, e.nombre, e.tipo, " +
                     "m.id as mid, m.fecha, m.tecnico, m.costo, m.descripcion, " +
                     "tm.id as tmid, tm.nombre as tmnombre, tm.descripcion as tmdesc " +
                     "FROM Equipos e " +
                     "JOIN EquipoMantenimiento em ON e.id = em.equipo_id " +
                     "JOIN Mantenimientos m ON em.mantenimiento_id = m.id " +
                     "JOIN TiposMantenimiento tm ON m.tipo_mantenimiento_id = tm.id";
        
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                TipoMantenimiento tipo = new TipoMantenimiento(
                    rs.getInt("tmid"), 
                    rs.getString("tmnombre"), 
                    rs.getString("tmdesc")
                );
                
                Equipo e = new Equipo(
                    rs.getInt("id"), 
                    rs.getString("nombre"), 
                    rs.getString("tipo")
                );
                
                Mantenimiento m = new Mantenimiento(
                    rs.getInt("mid"), 
                    LocalDate.parse(rs.getString("fecha")),
                    rs.getString("tecnico"), 
                    rs.getDouble("costo"), 
                    rs.getString("descripcion"), 
                    tipo
                );
                
                lista.add(new ParAsociado<>(e, m));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar: " + ex.getMessage());
        }
        return lista;
    }

    @Override
    public void eliminarEquipo(int idEquipo) {
        try {
            String sql = "DELETE FROM Equipos WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idEquipo);
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar equipo: " + ex.getMessage());
        }
    }

    @Override
    public void limpiarInventario() {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM EquipoMantenimiento");
            stmt.executeUpdate("DELETE FROM Mantenimientos");
            stmt.executeUpdate("DELETE FROM Equipos");
            System.out.println("✅ Base de datos limpiada");
        } catch (SQLException ex) {
            throw new RuntimeException("Error al limpiar: " + ex.getMessage());
        }
    }

    @Override
    public void agregarTipoMantenimiento(TipoMantenimiento tipo) {
        try {
            String sql = "INSERT OR REPLACE INTO TiposMantenimiento (id, nombre, descripcion) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, tipo.getId());
                pstmt.setString(2, tipo.getNombre());
                pstmt.setString(3, tipo.getDescripcion());
                pstmt.executeUpdate();
            }
            System.out.println("✅ Tipo de mantenimiento agregado: " + tipo.getNombre());
        } catch (SQLException ex) {
            throw new RuntimeException("Error al agregar tipo: " + ex.getMessage());
        }
    }

    @Override
    public List<TipoMantenimiento> obtenerTiposMantenimiento() {
        List<TipoMantenimiento> tipos = new ArrayList<>();
        try {
            String sql = "SELECT id, nombre, descripcion FROM TiposMantenimiento ORDER BY nombre";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    tipos.add(new TipoMantenimiento(
                        rs.getInt("id"), 
                        rs.getString("nombre"), 
                        rs.getString("descripcion")
                    ));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener tipos: " + ex.getMessage());
        }
        return tipos;
    }

    @Override
    public List<Equipo> listarEquipos() {
        // Implementación básica
        return new ArrayList<>();
    }

    @Override
    public List<Mantenimiento> listarMantenimientos() {
        // Implementación básica
        return new ArrayList<>();
    }
}