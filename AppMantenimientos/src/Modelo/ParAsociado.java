package Modelo;

import java.io.Serializable;

public class ParAsociado <T,U> implements Serializable{
	private T primero;
	private U segundo;
	public ParAsociado(T primero, U segundo) {
		super();
		this.primero = primero;
		this.segundo = segundo;
	}
	public T getPrimero() {
		return primero;
	}
	public void setPrimero(T primero) {
		this.primero = primero;
	}
	public U getSegundo() {
		return segundo;
	}
	public void setSegundo(U segundo) {
		this.segundo = segundo;
	}
	
	@Override
	public String toString() {
		return primero.toString() + " --> "+segundo.toString();
	}

}
