package Modelo;
//Clase creada por: Eduardo Motta

public class PresentacionDetallada implements IEstrategiaPresentacion {
    @Override
    public String formatear(ParAsociado<Equipo, Mantenimiento> par) {
        Equipo e = par.getPrimero();
        Mantenimiento m = par.getSegundo();
        // Muestra todos los datos
        return String.format("EQUIPO: %s (%s) | MANT: %s por %s | FECHA: %s | COSTO: $%.2f",
                e.getNombre(), e.getTipo(), m.getTipo().getNombre(), m.getTecnico(), m.getFecha(), m.getCosto());
    }
}