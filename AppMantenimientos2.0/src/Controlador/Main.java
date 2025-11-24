package Controlador;

import Modelo.RepositorioSQL;
import Vista.VistaInventario;
import Patrones.LoggerObserver;

public class Main {
    public static void main(String[] args) {
        RepositorioSQL repo = new RepositorioSQL();
        
        ControladorInventario controlador = new ControladorInventario(repo);
        
        controlador.agregarObservador(new LoggerObserver());
        
        new VistaInventario(controlador);
    }
}