package Controlador;

import Modelo.RepositorioSQL;
import Vista.VistaInventario;
import Patrones.LoggerObserver;

public class Main {
    public static void main(String[] args) {
        // 1. Crear el repositorio concreto (SQL)
        RepositorioSQL repo = new RepositorioSQL();
        
        // 2. Inyectarlo al controlador (Inyección de Dependencias)
        ControladorInventario controlador = new ControladorInventario(repo);
        
        // 3. Agregar un observador extra (Consola)
        controlador.agregarObservador(new LoggerObserver());
        
        // 4. Iniciar Vista
        new VistaInventario(controlador);
    }
}