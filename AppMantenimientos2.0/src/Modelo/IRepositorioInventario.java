package Modelo;
import java.util.List;
//Clase creada por: Leandro Lagos 
public interface IRepositorioInventario {
    void agregarAsociacion(Equipo e, Mantenimiento m);
    List<ParAsociado<Equipo, Mantenimiento>> listar();
    void eliminarEquipo(int idEquipo);
    void limpiarInventario();
    
    void agregarTipoMantenimiento(TipoMantenimiento tipo);
    List<TipoMantenimiento> obtenerTiposMantenimiento();
    List<Equipo> listarEquipos();
    List<Mantenimiento> listarMantenimientos();
    Equipo buscarEquipoPorId(int id);
}