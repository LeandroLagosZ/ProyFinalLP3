package Controlador;

import Modelo.*;
import Modelo.OrdenarPorCosto; // Importar estrategias
import Patrones.IObservador;
import java.util.ArrayList;
import java.util.List;

public class ControladorInventario {
    
    // Dependencia abstracta (DIP)
    private IRepositorioInventario repositorio;
    
    // Lista de observadores
    private List<IObservador> observadores = new ArrayList<>();
    
    // Estrategia actual (por defecto null o una básica)
    private IEstrategiaOrdenamiento estrategiaOrdenamiento;

    public ControladorInventario(IRepositorioInventario repositorio) {
        this.repositorio = repositorio;
        // Estrategia por defecto
        this.estrategiaOrdenamiento = new OrdenarPorCosto();
    }

    // --- Métodos de Lógica de Negocio ---

    public void registrarAsociacion(Equipo e, Mantenimiento m) {
        repositorio.agregarAsociacion(e, m);
        notificarObservadores("REGISTRO", "Nuevo equipo/mantenimiento: " + e.getNombre());
    }

    public List<ParAsociado<Equipo, Mantenimiento>> listarAsociaciones() {
        List<ParAsociado<Equipo, Mantenimiento>> lista = repositorio.listar();
        
        // Aplicar patrón Strategy si hay una estrategia definida
        if (estrategiaOrdenamiento != null) {
            estrategiaOrdenamiento.ordenar(lista);
        }
        
        return lista;
    }
    
    public void limpiarBaseDeDatos() {
        repositorio.limpiarInventario();
        notificarObservadores("LIMPIEZA", "Se ha borrado todo el inventario.");
    }

    // --- Gestión del Patrón Strategy ---
    public void setEstrategiaOrdenamiento(IEstrategiaOrdenamiento estrategia) {
        this.estrategiaOrdenamiento = estrategia;
        notificarObservadores("CONFIG", "Estrategia de ordenamiento cambiada.");
    }

    // --- Gestión del Patrón Observer ---
    
    public void agregarObservador(IObservador obs) {
        observadores.add(obs);
    }

    private void notificarObservadores(String evento, String mensaje) {
        for (IObservador obs : observadores) {
            obs.notificar(evento, mensaje);
        }
    }
 // Agregar estos métodos al controlador:
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
}