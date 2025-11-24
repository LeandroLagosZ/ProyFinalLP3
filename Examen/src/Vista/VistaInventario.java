package Vista;

import Controlador.ControladorInventario;
import Modelo.Equipo;
import Modelo.Mantenimiento;
import Modelo.ParAsociado;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;


public class VistaInventario extends JFrame {

    private ControladorInventario controlador;

    private JTextField txtIdEquipo;
    private JTextField txtNombreEquipo;
    private JTextField txtTipoEquipo;

    private JTextField txtIdMant;
    private JTextField txtDesc;
    private JTextField txtTecnico;
    private JTextField txtFecha;
    private JTextField txtCosto;

    private DefaultListModel<String> modeloLista;
    private JList<String> listaAsociaciones;

    public VistaInventario(ControladorInventario controlador) {
        this.controlador = controlador;

        setTitle("Sistema de Inventario - Equipos y Mantenimientos");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridLayout(10, 2, 5, 5));

        panelFormulario.add(new JLabel("ID Equipo:"));
        txtIdEquipo = new JTextField();
        panelFormulario.add(txtIdEquipo);

        panelFormulario.add(new JLabel("Nombre Equipo:"));
        txtNombreEquipo = new JTextField();
        panelFormulario.add(txtNombreEquipo);

        panelFormulario.add(new JLabel("Tipo Equipo:"));
        txtTipoEquipo = new JTextField();
        panelFormulario.add(txtTipoEquipo);

        // Campos del Mantenimiento
        panelFormulario.add(new JLabel("ID Mantenimiento:"));
        txtIdMant = new JTextField();
        panelFormulario.add(txtIdMant);

        panelFormulario.add(new JLabel("Descripción:"));
        txtDesc = new JTextField();
        panelFormulario.add(txtDesc);

        panelFormulario.add(new JLabel("Técnico:"));
        txtTecnico = new JTextField();
        panelFormulario.add(txtTecnico);

        panelFormulario.add(new JLabel("Fecha (AAAA-MM-DD):"));
        txtFecha = new JTextField();
        panelFormulario.add(txtFecha);

        panelFormulario.add(new JLabel("Costo:"));
        txtCosto = new JTextField();
        panelFormulario.add(txtCosto);

        add(panelFormulario, BorderLayout.NORTH);

        modeloLista = new DefaultListModel<>();
        listaAsociaciones = new JList<>(modeloLista);
        JScrollPane scroll = new JScrollPane(listaAsociaciones);

        add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnAgregar = new JButton("Agregar Asociación");
        JButton btnListar = new JButton("Listar");
        JButton btnGuardar = new JButton("Guardar en Archivo");
        JButton btnCargar = new JButton("Cargar desde Archivo");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnListar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCargar);

        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarAsociacion());

        btnListar.addActionListener(e -> listarAsociaciones());

        btnGuardar.addActionListener(e -> elegirArchivoGuardar());

        btnCargar.addActionListener(e -> elegirArchivoCargar());

        setVisible(true);
    }



    private void agregarAsociacion() {
        try {
            int idE = Integer.parseInt(txtIdEquipo.getText());
            String nom = txtNombreEquipo.getText();
            String tipo = txtTipoEquipo.getText();

            int idM = Integer.parseInt(txtIdMant.getText());
            String desc = txtDesc.getText();
            String tecnico = txtTecnico.getText();
            LocalDate fecha = LocalDate.parse(txtFecha.getText());
            double costo = Double.parseDouble(txtCosto.getText());

            Equipo e = new Equipo(idE, nom, tipo);
            Mantenimiento m = new Mantenimiento(idM, desc, tecnico, fecha, costo);

            controlador.registrarAsociacion(e, m);

            JOptionPane.showMessageDialog(this, "Asociación registrada.");
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

    private void elegirArchivoGuardar() {
        JFileChooser chooser = new JFileChooser();
        int opcion = chooser.showSaveDialog(this);

        if (opcion == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().getAbsolutePath();
            controlador.getRepositorio().setNombreArchivo(ruta);

            if (controlador.guardarArchivo()) {
                JOptionPane.showMessageDialog(this, "Archivo guardado.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar.");
            }
        }
    }

    private void elegirArchivoCargar() {
        JFileChooser chooser = new JFileChooser();
        int opcion = chooser.showOpenDialog(this);

        if (opcion == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().getAbsolutePath();
            controlador.getRepositorio().setNombreArchivo(ruta);

            if (controlador.cargarArchivo()) {
                JOptionPane.showMessageDialog(this, "Archivo cargado.");
                listarAsociaciones();
            } else {
                JOptionPane.showMessageDialog(this, "Error al cargar.");
            }
        }
    }
}
