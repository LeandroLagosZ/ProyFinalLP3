package Modelo;
//Clase creada por: Leandro Lagos y Eduardo Motta

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepositorioSQL implements IRepositorioInventario {
    
    private Connection conn;

    public RepositorioSQL() {
        this.conn = ConexionDB.getInstancia().getConexion();
        actualizarTablaEquipos(); // Asegurar que existe la columna nueva
    }

    // Método auxiliar para agregar la columna si no existe (Migración simple)
    private void actualizarTablaEquipos() {
        try (Statement stmt = conn.createStatement()) {
            // Intentamos agregar la columna. Si ya existe, SQLite lanzará error que ignoramos.
            stmt.execute("ALTER TABLE Equipos ADD COLUMN descripcion_tecnica TEXT DEFAULT 'N/A'");
        } catch (SQLException ignored) { 
            // La columna ya existe, continuamos.
        }
    }

    @Override
    public void agregarAsociacion(Equipo e, Mantenimiento m) {
        try {
            conn.setAutoCommit(false); 
            guardarEquipo(e);
            guardarTipoMantenimiento(m.getTipo());
            guardarMantenimiento(m);
            asociarEquipoMantenimiento(e.getId(), m.getId());
            conn.commit(); 
        } catch (SQLException ex) {
            try { conn.rollback(); } catch (SQLException rollbackEx) {}
            throw new RuntimeException("Error: " + ex.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    private void guardarEquipo(Equipo e) throws SQLException {
        // Actualizado para incluir descripcion_tecnica
        String sql = "INSERT OR REPLACE INTO Equipos (id, nombre, tipo, descripcion_tecnica) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, e.getId());
            pstmt.setString(2, e.getNombre());
            pstmt.setString(3, e.getTipo());
            pstmt.setString(4, e.getDescripcionTecnica());
            pstmt.executeUpdate();
        }
    }

    // ... (guardarTipoMantenimiento y guardarMantenimiento se mantienen IGUAL que antes) ...
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
            pstmt.setInt(6, m.getTipo().getId());
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
        
        // Actualizado QUERY para traer descripcion_tecnica
        String sql = "SELECT e.id, e.nombre, e.tipo, e.descripcion_tecnica, " +
                     "m.id as mid, m.fecha, m.tecnico, m.costo, m.descripcion, " +
                     "tm.id as tmid, tm.nombre as tmnombre, tm.descripcion as tmdesc " +
                     "FROM Equipos e " +
                     "JOIN EquipoMantenimiento em ON e.id = em.equipo_id " +
                     "JOIN Mantenimientos m ON em.mantenimiento_id = m.id " +
                     "JOIN TiposMantenimiento tm ON m.tipo_mantenimiento_id = tm.id";
        
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                TipoMantenimiento tipo = new TipoMantenimiento(
                    rs.getInt("tmid"), rs.getString("tmnombre"), rs.getString("tmdesc")
                );
                
                Equipo e = new Equipo(
                    rs.getInt("id"), rs.getString("nombre"), rs.getString("tipo"),
                    rs.getString("descripcion_tecnica") // Recuperar nuevo campo
                );
                
                Mantenimiento m = new Mantenimiento(
                    rs.getInt("mid"), LocalDate.parse(rs.getString("fecha")),
                    rs.getString("tecnico"), rs.getDouble("costo"), 
                    rs.getString("descripcion"), tipo
                );
                lista.add(new ParAsociado<>(e, m));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar: " + ex.getMessage());
        }
        return lista;
    }

    // ... (El resto de métodos eliminarEquipo, limpiarInventario, etc. se mantienen IGUAL) ...
    @Override
    public void eliminarEquipo(int idEquipo) {
        try {
            String sql = "DELETE FROM Equipos WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idEquipo);
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    @Override
    public void limpiarInventario() {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM EquipoMantenimiento");
            stmt.executeUpdate("DELETE FROM Mantenimientos");
            stmt.executeUpdate("DELETE FROM Equipos");
        } catch (SQLException ex) { throw new RuntimeException(ex); }
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
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    @Override
    public List<TipoMantenimiento> obtenerTiposMantenimiento() {
        List<TipoMantenimiento> tipos = new ArrayList<>();
        try {
            String sql = "SELECT id, nombre, descripcion FROM TiposMantenimiento ORDER BY nombre";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    tipos.add(new TipoMantenimiento(rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion")));
                }
            }
        } catch (SQLException ex) { throw new RuntimeException(ex); }
        return tipos;
    }
    
    @Override
    public List<Equipo> listarEquipos() { return new ArrayList<>(); }
    @Override
    public List<Mantenimiento> listarMantenimientos() { return new ArrayList<>(); }
}