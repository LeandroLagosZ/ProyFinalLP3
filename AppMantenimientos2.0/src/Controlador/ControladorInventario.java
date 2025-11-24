package Controlador;
//Clase creada por: Leandro Lagos 
import Modelo.*;
import Modelo.OrdenarPorCosto; 
import Patrones.IObservador;
import Patrones.LoggerManager;

import java.util.ArrayList;
import java.util.List;

public class ControladorInventario {
    
    private IRepositorioInventario repositorio;
    private List<IObservador> observadores = new ArrayList<>();
    private IEstrategiaOrdenamiento estrategiaOrdenamiento;

    public ControladorInventario(IRepositorioInventario repositorio) {
        this.repositorio = repositorio;
        this.estrategiaOrdenamiento = new OrdenarPorCosto();
    }


    public void registrarAsociacion(Equipo e, Mantenimiento m) {
        repositorio.agregarAsociacion(e, m);
        notificarObservadores("REGISTRO", "Nuevo equipo/mantenimiento: " + e.getNombre());
    }

    public List<ParAsociado<Equipo, Mantenimiento>> listarAsociaciones() {
        List<ParAsociado<Equipo, Mantenimiento>> lista = repositorio.listar();
        
        if (estrategiaOrdenamiento != null) {
            estrategiaOrdenamiento.ordenar(lista);
        }
        
        return lista;
    }
    
    public void limpiarBaseDeDatos() {
        repositorio.limpiarInventario();
        notificarObservadores("LIMPIEZA", "Se ha borrado todo el inventario.");
    }

    public void setEstrategiaOrdenamiento(IEstrategiaOrdenamiento estrategia) {
        this.estrategiaOrdenamiento = estrategia;
        notificarObservadores("CONFIG", "Estrategia de ordenamiento cambiada.");
    }
    public void agregarObservador(IObservador obs) {
        observadores.add(obs);
    }


    private void notificarObservadores(String evento, String mensaje) {
        for (IObservador obs : observadores) {
            obs.notificar(evento, mensaje);
        }
    }

    public List<TipoMantenimiento> obtenerTiposMantenimiento() {
        return repositorio.obtenerTiposMantenimiento();
    }

    public void agregarTipoMantenimiento(TipoMantenimiento tipo) {
        repositorio.agregarTipoMantenimiento(tipo);
        notificarObservadores("NUEVO_TIPO", "Tipo de mantenimiento agregado: " + tipo.getNombre());
    }

    public List<Equipo> listarEquipos() {
        return repositorio.listarEquipos();
    }

    public List<Mantenimiento> listarMantenimientos() {
        return repositorio.listarMantenimientos();
    }
    public Equipo buscarEquipoPorId(int id) {
        return repositorio.buscarEquipoPorId(id);
    }
}