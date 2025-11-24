package Vista;

import Controlador.ControladorInventario;
import Modelo.Equipo;
import Modelo.Mantenimiento;
import Modelo.TipoMantenimiento;
import Modelo.OrdenarPorCosto;
import Modelo.OrdenarPorTecnico;
import Modelo.ParAsociado;
import Patrones.IObservador;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VistaInventario extends JFrame implements IObservador {

    private ControladorInventario controlador;

    // Campos originales
    private JTextField txtIdEquipo;
    private JTextField txtNombreEquipo;
    private JTextField txtTipoEquipo;
    private JTextField txtIdMant;
    private JTextField txtDesc;
    private JTextField txtTecnico;
    private JTextField txtFecha;
    private JTextField txtCosto;

    // Nuevos componentes para selección de tipo
    private JComboBox<TipoMantenimiento> comboTipoMantenimiento;
    private JButton btnAgregarTipo;

    private DefaultListModel<String> modeloLista;
    private JList<String> listaAsociaciones;
    private JComboBox<String> comboEstrategia;

    public VistaInventario(ControladorInventario controlador) {
        this.controlador = controlador;
        this.controlador.agregarObservador(this);

        setTitle("Sistema de Inventario - Equipos y Mantenimientos");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Panel Superior - Formulario
        JPanel panelFormulario = crearPanelFormulario();
        add(panelFormulario, BorderLayout.NORTH);

        // Panel Central - Lista
        modeloLista = new DefaultListModel<>();
        listaAsociaciones = new JList<>(modeloLista);
        JScrollPane scroll = new JScrollPane(listaAsociaciones);
        add(scroll, BorderLayout.CENTER);

        // Panel Inferior - Botones y Estrategia
        JPanel panelInferior = crearPanelInferior();
        add(panelInferior, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(10, 2, 5, 5));

        // Campos del Equipo
        panel.add(new JLabel("ID Equipo:"));
        txtIdEquipo = new JTextField();
        panel.add(txtIdEquipo);

        panel.add(new JLabel("Nombre Equipo:"));
        txtNombreEquipo = new JTextField();
        panel.add(txtNombreEquipo);

        panel.add(new JLabel("Tipo Equipo:"));
        txtTipoEquipo = new JTextField();
        panel.add(txtTipoEquipo);

        // Campos del Mantenimiento
        panel.add(new JLabel("ID Mantenimiento:"));
        txtIdMant = new JTextField();
        panel.add(txtIdMant);

        panel.add(new JLabel("Descripción:"));
        txtDesc = new JTextField();
        panel.add(txtDesc);

        panel.add(new JLabel("Tipo Mantenimiento:"));
        JPanel panelTipo = new JPanel(new BorderLayout());
        comboTipoMantenimiento = new JComboBox<>();
        cargarTiposMantenimiento();
        panelTipo.add(comboTipoMantenimiento, BorderLayout.CENTER);
        
        btnAgregarTipo = new JButton("+");
        btnAgregarTipo.addActionListener(e -> agregarNuevoTipo());
        panelTipo.add(btnAgregarTipo, BorderLayout.EAST);
        
        panel.add(panelTipo);

        panel.add(new JLabel("Técnico:"));
        txtTecnico = new JTextField();
        panel.add(txtTecnico);

        panel.add(new JLabel("Fecha (AAAA-MM-DD):"));
        txtFecha = new JTextField();
        panel.add(txtFecha);

        panel.add(new JLabel("Costo:"));
        txtCosto = new JTextField();
        panel.add(txtCosto);

        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel de estrategias
        JPanel panelEstrategia = new JPanel(new FlowLayout());
        panelEstrategia.add(new JLabel("Ordenar por: "));
        comboEstrategia = new JComboBox<>(new String[]{"Costo", "Técnico"});
        panelEstrategia.add(comboEstrategia);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnAgregar = new JButton("Agregar Asociación");
        JButton btnListar = new JButton("Refrescar Lista");
        JButton btnLimpiar = new JButton("Limpiar BD");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnListar);
        panelBotones.add(btnLimpiar);

        // Configurar eventos
        btnAgregar.addActionListener(e -> agregarAsociacion());
        btnListar.addActionListener(e -> listarAsociaciones());
        btnLimpiar.addActionListener(e -> limpiarBD());

        // Evento estrategia
        comboEstrategia.addActionListener(e -> cambiarEstrategia());

        panel.add(panelEstrategia, BorderLayout.NORTH);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarTiposMantenimiento() {
        comboTipoMantenimiento.removeAllItems();
        List<TipoMantenimiento> tipos = controlador.obtenerTiposMantenimiento();
        
        if (tipos != null && !tipos.isEmpty()) {
            for (TipoMantenimiento tipo : tipos) {
                comboTipoMantenimiento.addItem(tipo);
            }
        } else {
            comboTipoMantenimiento.addItem(new TipoMantenimiento(0, "No hay tipos", ""));
        }
    }

    private void agregarNuevoTipo() {
        JTextField txtId = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtDescripcion = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("ID:"));
        panel.add(txtId);
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Descripción:"));
        panel.add(txtDescripcion);

        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Nuevo Tipo de Mantenimiento", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int id = Integer.parseInt(txtId.getText());
                String nombre = txtNombre.getText().trim();
                String descripcion = txtDescripcion.getText().trim();

                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre es requerido");
                    return;
                }

                TipoMantenimiento nuevoTipo = new TipoMantenimiento(id, nombre, descripcion);
                controlador.agregarTipoMantenimiento(nuevoTipo);
                cargarTiposMantenimiento(); // Recargar combo
                
                JOptionPane.showMessageDialog(this, "Tipo de mantenimiento agregado: " + nombre);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID debe ser numérico");
            }
        }
    }

    // Observer
    @Override
    public void notificar(String evento, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Notificación: " + evento, 
                                    JOptionPane.INFORMATION_MESSAGE);
    }

    private void agregarAsociacion() {
        try {
            // Validar que hay un tipo seleccionado
            TipoMantenimiento tipoSeleccionado = (TipoMantenimiento) comboTipoMantenimiento.getSelectedItem();
            if (tipoSeleccionado == null || tipoSeleccionado.getId() == 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un tipo de mantenimiento válido");
                return;
            }

            // Datos del equipo
            int idE = Integer.parseInt(txtIdEquipo.getText());
            String nom = txtNombreEquipo.getText();
            String tipoEquipo = txtTipoEquipo.getText();

            // Datos del mantenimiento
            int idM = Integer.parseInt(txtIdMant.getText());
            String desc = txtDesc.getText();
            String tecnico = txtTecnico.getText();
            LocalDate fecha = LocalDate.parse(txtFecha.getText());
            double costo = Double.parseDouble(txtCosto.getText());

            Equipo e = new Equipo(idE, nom, tipoEquipo);
            Mantenimiento m = new Mantenimiento(idM, fecha, tecnico, costo, desc, tipoSeleccionado);

            controlador.registrarAsociacion(e, m);

            limpiarFormulario();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Entrada inválida", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarAsociaciones() {
        modeloLista.clear();
        List<ParAsociado<Equipo, Mantenimiento>> lista = controlador.listarAsociaciones();

        for (ParAsociado<?, ?> par : lista) {
            modeloLista.addElement(par.toString());
        }
    }

    private void limpiarBD() {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Limpiar toda la base de datos?", "Confirmar", 
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controlador.limpiarBaseDeDatos();
            listarAsociaciones();
        }
    }

    private void cambiarEstrategia() {
        String seleccion = (String) comboEstrategia.getSelectedItem();
        if ("Costo".equals(seleccion)) {
            controlador.setEstrategiaOrdenamiento(new OrdenarPorCosto());
        } else {
            controlador.setEstrategiaOrdenamiento(new OrdenarPorTecnico());
        }
        listarAsociaciones();
    }

    private void limpiarFormulario() {
        txtIdEquipo.setText("");
        txtNombreEquipo.setText("");
        txtTipoEquipo.setText("");
        txtIdMant.setText("");
        txtDesc.setText("");
        txtTecnico.setText("");
        txtFecha.setText("");
        txtCosto.setText("");
    }
}