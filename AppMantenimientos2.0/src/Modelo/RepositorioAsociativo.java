package Modelo;

import java.util.ArrayList;
import java.util.List;

public class RepositorioAsociativo <T,U>{
	protected List<ParAsociado<T,U>> asociaciones=new ArrayList<>();
	public void agregar(T t, U u) {
		asociaciones.add(new ParAsociado<>(t,u));
	}
	
	public List<ParAsociado<T,U>> listar(){
		return new ArrayList<>(asociaciones);
	}

}
