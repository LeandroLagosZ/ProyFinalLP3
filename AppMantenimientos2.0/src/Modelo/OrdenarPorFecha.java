package Modelo;
//Clase creada por: Eduardo Motta

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrdenarPorFecha implements IEstrategiaOrdenamiento {
    @Override
    public void ordenar(List<ParAsociado<Equipo, Mantenimiento>> lista) {
        Collections.sort(lista, Comparator.comparing(
            p -> p.getSegundo().getFecha(), 
            Comparator.reverseOrder())); // Invertir para ver lo más nuevo arriba
    }
}