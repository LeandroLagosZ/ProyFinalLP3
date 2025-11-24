package Modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Mantenimiento {
    private int id;
    private LocalDate fecha;
    private String tecnico;
    private double costo;
    private String observaciones; // Detalles específicos de esta instancia
    private TipoMantenimiento tipo; // ✅ REFERENCIA al tipo de mantenimiento
    private List<Equipo> equipos; // Relación muchos-a-muchos
    
    public Mantenimiento(int id, LocalDate fecha, String tecnico, double costo, 
                        String observaciones, TipoMantenimiento tipo) {
        this.id = id;
        this.fecha = fecha;
        this.tecnico = tecnico;
        this.costo = costo;
        this.observaciones = observaciones;
        this.tipo = tipo;
        this.equipos = new ArrayList<>();
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public String getTecnico() { return tecnico; }
    public void setTecnico(String tecnico) { this.tecnico = tecnico; }
    
    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public TipoMantenimiento getTipo() { return tipo; }
    public void setTipo(TipoMantenimiento tipo) { this.tipo = tipo; }
    
    public void agregarEquipo(Equipo equipo) {
        if (!this.equipos.contains(equipo)) {
            this.equipos.add(equipo);
        }
    }
    
    public List<Equipo> getEquipos() {
        return new ArrayList<>(equipos);
    }
    
    @Override
    public String toString() {
        return "Mantenimiento[ID=" + id + ", Tipo=" + tipo.getNombre() + 
               ", Técnico=" + tecnico + ", Fecha=" + fecha + ", Costo=" + costo + "]";
    }
}