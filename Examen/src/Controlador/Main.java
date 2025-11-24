package Controlador;

import Controlador.ControladorInventario;
import Modelo.RepositorioArchivo;
import Vista.VistaInventario;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        RepositorioArchivo repo = new RepositorioArchivo("inventario.csv");
        ControladorInventario controlador = new ControladorInventario(repo);

        new VistaInventario(controlador);
    }
}


