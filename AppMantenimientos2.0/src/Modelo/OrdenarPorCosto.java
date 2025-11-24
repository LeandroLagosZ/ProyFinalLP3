package Modelo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrdenarPorCosto implements IEstrategiaOrdenamiento {
    @Override
    public void ordenar(List<ParAsociado<Equipo, Mantenimiento>> lista) {
        Collections.sort(lista, Comparator.comparingDouble(
            p -> p.getSegundo().getCosto()));
    }
}

