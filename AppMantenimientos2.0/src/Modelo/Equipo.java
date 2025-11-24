package Modelo;
//Clase creada por: Leandro Lagos y Eduardo Motta

import java.util.ArrayList;
import java.util.List;

public class Equipo {
    private int id;
    private String nombre;
    private String tipo;
    private String descripcionTecnica; 
    private List<Mantenimiento> mantenimientos;
    
    public Equipo(int id, String nombre, String tipo) {
        this(id, nombre, tipo, "Sin especificaciones técnicas.");
    }

    public Equipo(int id, String nombre, String tipo, String descripcionTecnica) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.descripcionTecnica = descripcionTecnica;
        this.mantenimientos = new ArrayList<>();
    }
    
    public void agregarMantenimiento(Mantenimiento mantenimiento) {
        if (!this.mantenimientos.contains(mantenimiento)) {
            this.mantenimientos.add(mantenimiento);
            mantenimiento.agregarEquipo(this); 
        }
    }
    
    public List<Mantenimiento> getMantenimientos() {
        return new ArrayList<>(mantenimientos);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcionTecnica() { return descripcionTecnica; }
    public void setDescripcionTecnica(String descripcionTecnica) { this.descripcionTecnica = descripcionTecnica; }
    
    @Override
    public String toString() {
        return "Equipo[ID=" + id + ", Nombre=" + nombre + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Equipo equipo = (Equipo) obj;
        return id == equipo.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}