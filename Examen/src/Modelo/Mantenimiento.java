package Modelo;

import java.time.LocalDate;

public class Mantenimiento {
	private int id;
	private String descripcion;
	private String tecnico;
	private LocalDate fecha;
	private double costo;
	public Mantenimiento(int id, String descripcion, String tecnico, LocalDate fecha, double costo) {
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.tecnico = tecnico;
		this.fecha = fecha;
		this.costo = costo;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getTecnico() {
		return tecnico;
	}
	public void setTecnico(String tecnico) {
		this.tecnico = tecnico;
	}
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	public double getCosto() {
		return costo;
	}
	public void setCosto(double costo) {
		this.costo = costo;
	}
	@Override
	public String toString() {
	    return "Mantenimiento[ID=" + id + ", Desc=" + descripcion + 
	           ", Técnico=" + tecnico + ", Fecha=" + fecha + ", Costo=" + costo + "]";
	}

	
}
