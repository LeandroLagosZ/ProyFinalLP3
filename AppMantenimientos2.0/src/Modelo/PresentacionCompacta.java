package Modelo;
//Clase creada por: Eduardo Motta

public class PresentacionCompacta implements IEstrategiaPresentacion {
    @Override
    public String formatear(ParAsociado<Equipo, Mantenimiento> par) {
        return par.getPrimero().getNombre();
    }
}