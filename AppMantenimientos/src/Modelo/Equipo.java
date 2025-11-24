package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Equipo {
    private int id;
    private String nombre;
    private String tipo;
    private List<Mantenimiento> mantenimientos; // Relación muchos-a-muchos
    
    public Equipo(int id, String nombre, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.mantenimientos = new ArrayList<>();
    }
    
    public void agregarMantenimiento(Mantenimiento mantenimiento) {
        if (!this.mantenimientos.contains(mantenimiento)) {
            this.mantenimientos.add(mantenimiento);
            mantenimiento.agregarEquipo(this); // Bidireccional
        }
    }
    
    public List<Mantenimiento> getMantenimientos() {
        return new ArrayList<>(mantenimientos);
    }
    
    // Getters y setters existentes
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    @Override
    public String toString() {
        return "Equipo[ID=" + id + ", Nombre=" + nombre + ", Tipo=" + tipo + "]";
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