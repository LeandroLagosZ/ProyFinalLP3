package Controlador;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import Modelo.*;

public class ControladorInventario {
	private RepositorioArchivo repositorio;
	
	public ControladorInventario(RepositorioArchivo repositorio) {
		super();
		this.repositorio = repositorio;
	}
	
	
	public void registrarAsociacion(Equipo e, Mantenimiento m) {
		repositorio.agregar(e, m);
	}
	
	public List<ParAsociado<Equipo,Mantenimiento>> listarAsociaciones(){
		return repositorio.listar();
	}
	
	public boolean guardarArchivo() {
		try {
			repositorio.guardarEnArchivo();
			return true;
			
		}catch(IOException e) {
			return false;
		}
	}
	
	public boolean cargarArchivo() {
		try {
			repositorio.cargarDesdeArchivo();
			return true;
		}catch (IOException | ClassNotFoundException e) {
			return false;
		}
	}
	
	public RepositorioArchivo getRepositorio() {
	    return repositorio;
	}

}
