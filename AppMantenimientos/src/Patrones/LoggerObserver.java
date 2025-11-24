package Patrones;
public class LoggerObserver implements IObservador {
    @Override
    public void notificar(String evento, String mensaje) {
        System.out.println("[LOG - " + evento + "]: " + mensaje);
    }
}