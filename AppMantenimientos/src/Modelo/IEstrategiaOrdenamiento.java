package Modelo;
import java.util.List;

public interface IEstrategiaOrdenamiento {
    void ordenar(List<ParAsociado<Equipo, Mantenimiento>> lista);
}