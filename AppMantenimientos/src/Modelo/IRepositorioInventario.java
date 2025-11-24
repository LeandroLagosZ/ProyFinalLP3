package Modelo;
import java.util.List;

public interface IRepositorioInventario {
    void agregarAsociacion(Equipo e, Mantenimiento m);
    List<ParAsociado<Equipo, Mantenimiento>> listar();
    void eliminarEquipo(int idEquipo);
    void limpiarInventario();
    
    // NUEVOS MÉTODOS para N:N
    void agregarTipoMantenimiento(TipoMantenimiento tipo);
    List<TipoMantenimiento> obtenerTiposMantenimiento();
    List<Equipo> listarEquipos();
    List<Mantenimiento> listarMantenimientos();
}