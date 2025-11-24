package Vista;
//Clase creada por: Leandro Lagos y Arnaldo Torres
import Patrones.LoggerManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VistaLogs extends JFrame {
    private JTextArea areaLogs;
    private JButton btnLimpiar;
    private JButton btnActualizar;
    
    public VistaLogs() {
        setTitle("Sistema de Logs - Registro de Eventos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnActualizar = new JButton("Actualizar");
        btnLimpiar = new JButton("Limpiar Logs");
        
        panelSuperior.add(btnActualizar);
        panelSuperior.add(btnLimpiar);
        
        areaLogs = new JTextArea();
        areaLogs.setEditable(false);
        areaLogs.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(areaLogs);
        
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        actualizarLogs();
        
        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarLogs();
            }
        });
        
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int respuesta = JOptionPane.showConfirmDialog(
                    VistaLogs.this, 
                    "¿Está seguro de que desea limpiar todos los logs?",
                    "Confirmar limpieza",
                    JOptionPane.YES_NO_OPTION
                );
                if (respuesta == JOptionPane.YES_OPTION) {
                    LoggerManager.getInstancia().limpiarLogs();
                    actualizarLogs();
                }
            }
        });
    }
    
    private void actualizarLogs() {
        String logs = LoggerManager.getInstancia().getLogsComoString();
        areaLogs.setText(logs);
        areaLogs.setCaretPosition(areaLogs.getDocument().getLength());
    }
}