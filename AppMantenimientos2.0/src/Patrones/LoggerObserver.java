package Patrones;
//Clase creada por: Leandro Lagos y Arnaldo Torres
public class LoggerObserver implements IObservador {
    @Override
    public void notificar(String evento, String mensaje) {
        LoggerManager.getInstancia().agregarLog(evento, mensaje);
    }
}