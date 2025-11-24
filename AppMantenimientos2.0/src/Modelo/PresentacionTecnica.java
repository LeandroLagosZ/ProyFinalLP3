package Modelo;
//Clase creada por: Eduardo Motta

public class PresentacionTecnica implements IEstrategiaPresentacion {
    @Override
    public String formatear(ParAsociado<Equipo, Mantenimiento> par) {
        Equipo e = par.getPrimero();
        Mantenimiento m = par.getSegundo();
        // Enfatiza la descripción técnica y el técnico
        return String.format("[TÉCNICO: %s] %s - Spec: %s",
                m.getTecnico().toUpperCase(), e.getNombre(), 
                (e.getDescripcionTecnica() != null ? e.getDescripcionTecnica() : "N/A"));
    }
}
