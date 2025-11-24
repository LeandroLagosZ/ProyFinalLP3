package Patrones;
//Clase creada por: Leandro Lagos y Arnaldo Torres
import java.util.ArrayList;
import java.util.List;

public class LoggerManager {
    private static LoggerManager instancia;
    private List<String> logs;
    
    private LoggerManager() {
        logs = new ArrayList<>();
    }
    
    public static LoggerManager getInstancia() {
        if (instancia == null) {
            instancia = new LoggerManager();
        }
        return instancia;
    }
    
    public void agregarLog(String evento, String mensaje) {
        String logEntry = String.format("[%s] %s - %s", 
            java.time.LocalDateTime.now().toString(), evento, mensaje);
        logs.add(logEntry);
        System.out.println(logEntry); 
    }
    
    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }
    
    public void limpiarLogs() {
        logs.clear();
    }
    
    public String getLogsComoString() {
        StringBuilder sb = new StringBuilder();
        for (String log : logs) {
            sb.append(log).append("\n");
        }
        return sb.toString();
    }
}