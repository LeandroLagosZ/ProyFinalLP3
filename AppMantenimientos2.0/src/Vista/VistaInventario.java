package Vista;
//Clase creada por: Leandro Lagos y Eduardo Motta


import Controlador.ControladorInventario;
import Modelo.*;
import Patrones.IObservador;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VistaInventario extends JFrame implements IObservador {

    private ControladorInventario controlador;

    // Campos del Formulario
    private JTextField txtIdEquipo, txtNombreEquipo, txtTipoEquipo, txtDescTecnica;
    private JTextField txtIdMant, txtDesc, txtTecnico, txtFecha, txtCosto;
    private JComboBox<TipoMantenimiento> comboTipoMantenimiento;
    private JButton btnAgregarTipo;

    // Controles de Estrategia y Configuración
    private JComboBox<String> comboEstrategiaOrden;
    private JComboBox<String> comboEstrategiaPresentacion;
    private JCheckBox chkNotificaciones; // Control para activar/desactivar eventos

    // Lista
    private DefaultListModel<String> modeloLista;
    private JList<String> listaAsociaciones;
    
    // Estrategia de presentación actual (Default: Detallada)
    private IEstrategiaPresentacion estrategiaPresentacion = new PresentacionDetallada();

    public VistaInventario(ControladorInventario controlador) {
        this.controlador = controlador;
        this.controlador.agregarObservador(this);

        setTitle("Sistema Avanzado de Inventario (SOLID + MVC + Strategy)");
        setSize(900, 600); // Un poco más grande para los nuevos campos
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        add(crearPanelFormulario(), BorderLayout.NORTH);
        
        modeloLista = new DefaultListModel<>();
        listaAsociaciones = new JList<>(modeloLista);
        add(new JScrollPane(listaAsociaciones), BorderLayout.CENTER);

        add(crearPanelInferior(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(6, 4, 5, 5)); // Grid más ancho
        panel.setBorder(BorderFactory.createTitledBorder("Registro de Inventario"));

        // Columna 1 y 2: Equipo
        panel.add(new JLabel("ID Equipo:"));
        txtIdEquipo = new JTextField();
        panel.add(txtIdEquipo);

        panel.add(new JLabel("Nombre Equipo:"));
        txtNombreEquipo = new JTextField();
        panel.add(txtNombreEquipo);

        panel.add(new JLabel("Tipo Equipo:"));
        txtTipoEquipo = new JTextField();
        panel.add(txtTipoEquipo);
        
        panel.add(new JLabel("Desc. Técnica (Nuevo):"));
        txtDescTecnica = new JTextField();
        panel.add(txtDescTecnica);

        // Columna 3 y 4: Mantenimiento
        panel.add(new JLabel("ID Mantenimiento:"));
        txtIdMant = new JTextField();
        panel.add(txtIdMant);

        panel.add(new JLabel("Fecha (YYYY-MM-DD):"));
        txtFecha = new JTextField();
        panel.add(txtFecha);

        panel.add(new JLabel("Técnico:"));
        txtTecnico = new JTextField();
        panel.add(txtTecnico);

        panel.add(new JLabel("Costo:"));
        txtCosto = new JTextField();
        panel.add(txtCosto);

        panel.add(new JLabel("Observaciones:"));
        txtDesc = new JTextField();
        panel.add(txtDesc);

        panel.add(new JLabel("Tipo Mant.:"));
        JPanel panelTipo = new JPanel(new BorderLayout());
        comboTipoMantenimiento = new JComboBox<>();
        cargarTiposMantenimiento();
        panelTipo.add(comboTipoMantenimiento, BorderLayout.CENTER);
        btnAgregarTipo = new JButton("+");
        btnAgregarTipo.addActionListener(e -> agregarNuevoTipo());
        panelTipo.add(btnAgregarTipo, BorderLayout.EAST);
        panel.add(panelTipo);

        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createTitledBorder("Controles y Estrategias"));

        // 1. Panel de Configuración (Notificaciones y Estrategias)
        JPanel panelConfig = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Control de Notificaciones (Requisito 3.1)
        chkNotificaciones = new JCheckBox("Activar Notificaciones", true);
        panelConfig.add(chkNotificaciones);
        
        panelConfig.add(new JSeparator(SwingConstants.VERTICAL));

        // Selector de Ordenamiento (Requisito 2.1)
        panelConfig.add(new JLabel("Ordenar por:"));
        comboEstrategiaOrden = new JComboBox<>(new String[]{"Costo", "Técnico", "Fecha"});
        panelConfig.add(comboEstrategiaOrden);

        // Selector de Presentación (Requisito 2.2)
        panelConfig.add(new JLabel("Vista:"));
        comboEstrategiaPresentacion = new JComboBox<>(new String[]{"Detallada", "Compacta", "Técnica"});
        panelConfig.add(comboEstrategiaPresentacion);

        // 2. Panel de Botones de Acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Guardar");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnLimpiar = new JButton("Limpiar BD");
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnLimpiar);

        // Eventos
        btnAgregar.addActionListener(e -> agregarAsociacion());
        btnRefrescar.addActionListener(e -> listarAsociaciones());
        btnLimpiar.addActionListener(e -> limpiarBD());
        
        // Evento cambio de Ordenamiento
        comboEstrategiaOrden.addActionListener(e -> {
            String sel = (String) comboEstrategiaOrden.getSelectedItem();
            switch (sel) {
                case "Costo": controlador.setEstrategiaOrdenamiento(new OrdenarPorCosto()); break;
                case "Técnico": controlador.setEstrategiaOrdenamiento(new OrdenarPorTecnico()); break;
                case "Fecha": controlador.setEstrategiaOrdenamiento(new OrdenarPorFecha()); break;
            }
            listarAsociaciones();
        });

        // Evento cambio de Presentación
        comboEstrategiaPresentacion.addActionListener(e -> {
            String sel = (String) comboEstrategiaPresentacion.getSelectedItem();
            switch (sel) {
                case "Detallada": estrategiaPresentacion = new PresentacionDetallada(); break;
                case "Compacta": estrategiaPresentacion = new PresentacionCompacta(); break;
                case "Técnica": estrategiaPresentacion = new PresentacionTecnica(); break;
            }
            listarAsociaciones();
        });

        panelPrincipal.add(panelConfig, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        return panelPrincipal;
    }

    // --- Implementación del Patrón Observer ---
    @Override
    public void notificar(String evento, String mensaje) {
        // Solo mostramos el popup si el checkbox está marcado
        if (chkNotificaciones.isSelected()) {
            JOptionPane.showMessageDialog(this, mensaje, "Evento: " + evento, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void agregarAsociacion() {
        try {
            TipoMantenimiento tipoSel = (TipoMantenimiento) comboTipoMantenimiento.getSelectedItem();
            if (tipoSel == null) return;

            // Recolectar datos
            Equipo e = new Equipo(
                Integer.parseInt(txtIdEquipo.getText()),
                txtNombreEquipo.getText(),
                txtTipoEquipo.getText(),
                txtDescTecnica.getText() // Nuevo campo
            );

            Mantenimiento m = new Mantenimiento(
                Integer.parseInt(txtIdMant.getText()),
                LocalDate.parse(txtFecha.getText()),
                txtTecnico.getText(),
                Double.parseDouble(txtCosto.getText()),
                txtDesc.getText(),
                tipoSel
            );

            controlador.registrarAsociacion(e, m);
            limpiarFormulario();
            listarAsociaciones(); // Refrescar automáticamente

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en datos: " + ex.getMessage());
        }
    }

    private void listarAsociaciones() {
        modeloLista.clear();
        // 1. Obtener lista ordenada del controlador (Strategy de Ordenamiento ya aplicado dentro)
        List<ParAsociado<Equipo, Mantenimiento>> lista = controlador.listarAsociaciones();

        // 2. Aplicar Strategy de Presentación para mostrar en la lista
        for (ParAsociado<Equipo, Mantenimiento> par : lista) {
            String textoFormateado = estrategiaPresentacion.formatear(par);
            modeloLista.addElement(textoFormateado);
        }
    }

    // ... (Métodos auxiliares cargarTiposMantenimiento, agregarNuevoTipo, limpiarBD, limpiarFormulario se mantienen igual) ...
    
    private void cargarTiposMantenimiento() {
        comboTipoMantenimiento.removeAllItems();
        List<TipoMantenimiento> tipos = controlador.obtenerTiposMantenimiento();
        if (tipos != null) tipos.forEach(comboTipoMantenimiento::addItem);
    }
    
    private void agregarNuevoTipo() {
         // (Copia tu lógica existente aquí para agregar tipo)
         // ...
         // Al final llamar: controlador.agregarTipoMantenimiento(nuevoTipo); cargarTiposMantenimiento();
    }

    private void limpiarBD() {
        if(JOptionPane.showConfirmDialog(this, "¿Borrar todo?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controlador.limpiarBaseDeDatos();
            listarAsociaciones();
        }
    }
    
    private void limpiarFormulario() {
        txtIdEquipo.setText(""); txtNombreEquipo.setText(""); txtTipoEquipo.setText(""); txtDescTecnica.setText("");
        txtIdMant.setText(""); txtFecha.setText(""); txtTecnico.setText(""); txtCosto.setText(""); txtDesc.setText("");
    }
}