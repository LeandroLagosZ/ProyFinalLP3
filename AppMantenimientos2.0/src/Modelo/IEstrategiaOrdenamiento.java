package Modelo;
import java.util.List;
//Clase creada por: Leandro Lagos
public interface IEstrategiaOrdenamiento {
    void ordenar(List<ParAsociado<Equipo, Mantenimiento>> lista);
}