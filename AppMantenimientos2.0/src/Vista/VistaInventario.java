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

    private JTextField txtIdEquipo, txtNombreEquipo, txtTipoEquipo, txtDescTecnica;
    private JTextField txtIdMant, txtDesc, txtTecnico, txtFecha, txtCosto;
    private JComboBox<TipoMantenimiento> comboTipoMantenimiento;
    private JButton btnAgregarTipo;

    private JComboBox<String> comboEstrategiaOrden;
    private JComboBox<String> comboEstrategiaPresentacion;
    private JCheckBox chkNotificaciones; 

    private DefaultListModel<String> modeloLista;
    private JList<String> listaAsociaciones;
    
    private IEstrategiaPresentacion estrategiaPresentacion = new PresentacionDetallada();

    public VistaInventario(ControladorInventario controlador) {
        this.controlador = controlador;
        this.controlador.agregarObservador(this);

        setTitle("Sistema Avanzado de Inventario (SOLID + MVC + Strategy)");
        setSize(900, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        add(crearPanelFormulario(), BorderLayout.NORTH);
        
        modeloLista = new DefaultListModel<>();
        listaAsociaciones = new JList<>(modeloLista);
        add(new JScrollPane(listaAsociaciones), BorderLayout.CENTER);

        add(crearPanelInferior(), BorderLayout.SOUTH);
        configurarAutocompletado();

        setVisible(true);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridLayout(6, 4, 5, 5)); 
        panel.setBorder(BorderFactory.createTitledBorder("Registro de Inventario"));

        panel.add(new JLabel("ID Equipo:"));
        txtIdEquipo = new JTextField();
        panel.add(txtIdEquipo);

        panel.add(new JLabel("Nombre Equipo:"));
        txtNombreEquipo = new JTextField();
        panel.add(txtNombreEquipo);

        panel.add(new JLabel("Tipo Equipo:"));
        txtTipoEquipo = new JTextField();
        panel.add(txtTipoEquipo);
        
        panel.add(new JLabel("Desc. Técnica:"));
        txtDescTecnica = new JTextField();
        panel.add(txtDescTecnica);

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

        panel.add(panelTipo);

        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createTitledBorder("Controles y Estrategias"));

        JPanel panelConfig = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        chkNotificaciones = new JCheckBox("Activar Notificaciones", true);
        panelConfig.add(chkNotificaciones);
        
        JButton btnVerLogs = new JButton("Ver Logs");
        btnVerLogs.addActionListener(e -> abrirVistaLogs());
        panelConfig.add(btnVerLogs);
        
        panelConfig.add(new JSeparator(SwingConstants.VERTICAL));

        panelConfig.add(new JLabel("Ordenar por:"));
        comboEstrategiaOrden = new JComboBox<>(new String[]{"Costo", "Técnico", "Fecha"});
        panelConfig.add(comboEstrategiaOrden);

        panelConfig.add(new JLabel("Vista:"));
        comboEstrategiaPresentacion = new JComboBox<>(new String[]{"Detallada", "Compacta", "Técnica"});
        panelConfig.add(comboEstrategiaPresentacion);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Guardar");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnLimpiar = new JButton("Limpiar BD");
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnLimpiar);

        btnAgregar.addActionListener(e -> agregarAsociacion());
        btnRefrescar.addActionListener(e -> listarAsociaciones());
        btnLimpiar.addActionListener(e -> limpiarBD());

        comboEstrategiaOrden.addActionListener(e -> {
            String sel = (String) comboEstrategiaOrden.getSelectedItem();
            switch (sel) {
                case "Costo": controlador.setEstrategiaOrdenamiento(new OrdenarPorCosto()); break;
                case "Técnico": controlador.setEstrategiaOrdenamiento(new OrdenarPorTecnico()); break;
                case "Fecha": controlador.setEstrategiaOrdenamiento(new OrdenarPorFecha()); break;
            }
            listarAsociaciones();
        });


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

    @Override
    public void notificar(String evento, String mensaje) {
        if (chkNotificaciones.isSelected()) {
            JOptionPane.showMessageDialog(this, mensaje, "Evento: " + evento, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void agregarAsociacion() {
        try {
            TipoMantenimiento tipoSel = (TipoMantenimiento) comboTipoMantenimiento.getSelectedItem();
            if (tipoSel == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un tipo de mantenimiento");
                return;
            }
            if (txtIdEquipo.getText().trim().isEmpty() || txtIdMant.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID Equipo y ID Mantenimiento son obligatorios");
                return;
            }

            int idEquipo = Integer.parseInt(txtIdEquipo.getText().trim());
            Equipo equipoExistente = controlador.buscarEquipoPorId(idEquipo);
            
            Equipo equipo;
            if (equipoExistente != null) {
                equipo = equipoExistente;
                System.out.println("Usando equipo existente: " + equipo.getNombre());
            } else {
                if (txtNombreEquipo.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nombre de equipo es obligatorio para equipos nuevos");
                    return;
                }
                equipo = new Equipo(
                    idEquipo,
                    txtNombreEquipo.getText().trim(),
                    txtTipoEquipo.getText().trim(),
                    txtDescTecnica.getText().trim()
                );
                System.out.println("Creando nuevo equipo: " + equipo.getNombre());
            }

            Mantenimiento mantenimiento = new Mantenimiento(
                Integer.parseInt(txtIdMant.getText().trim()),
                LocalDate.parse(txtFecha.getText().trim()),
                txtTecnico.getText().trim(),
                Double.parseDouble(txtCosto.getText().trim()),
                txtDesc.getText().trim(),
                tipoSel
            );

            controlador.registrarAsociacion(equipo, mantenimiento);
            limpiarFormulario();
            listarAsociaciones();

            JOptionPane.showMessageDialog(this, 
                equipoExistente != null ? 
                "Mantenimiento agregado a equipo existente" : 
                "Nuevo equipo y mantenimiento registrados",
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirVistaLogs() {
        VistaLogs vistaLogs = new VistaLogs();
        vistaLogs.setVisible(true);
    }
    
    private void listarAsociaciones() {
        modeloLista.clear();
        List<ParAsociado<Equipo, Mantenimiento>> lista = controlador.listarAsociaciones();

        for (ParAsociado<Equipo, Mantenimiento> par : lista) {
            String textoFormateado = estrategiaPresentacion.formatear(par);
            modeloLista.addElement(textoFormateado);
        }
    }
  
    private void cargarTiposMantenimiento() {
        comboTipoMantenimiento.removeAllItems();
        List<TipoMantenimiento> tipos = controlador.obtenerTiposMantenimiento();
        if (tipos != null) tipos.forEach(comboTipoMantenimiento::addItem);
    }
    
    private void agregarNuevoTipo() {

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
    
    private void configurarAutocompletado() {
        txtIdEquipo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                autocompletarCamposEquipo();
            }
        });
        
        txtIdEquipo.addActionListener(e -> autocompletarCamposEquipo());
    }
    
    private void autocompletarCamposEquipo() {
        try {
            String idText = txtIdEquipo.getText().trim();
            if (!idText.isEmpty()) {
                int id = Integer.parseInt(idText);
                Equipo equipo = controlador.buscarEquipoPorId(id);
                
                if (equipo != null) {
                    txtNombreEquipo.setText(equipo.getNombre());
                    txtTipoEquipo.setText(equipo.getTipo());
                    txtDescTecnica.setText(equipo.getDescripcionTecnica());
                    
                    JOptionPane.showMessageDialog(this, 
                        "Equipo encontrado y cargado: " + equipo.getNombre(), 
                        "Autocompletado", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al buscar equipo: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}