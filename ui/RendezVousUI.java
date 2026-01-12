package ui;

import dao.impl.MedecinDAOImpl;
import dao.impl.PatientDAOImpl;
import dao.impl.RendezVousDAOImpl;
import dao.interfaces.MedecinDAO;
import dao.interfaces.PatientDAO;
import dao.interfaces.RendezVousDAO;
import models.Medecin;
import models.Patient;
import models.RendezVous;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RendezVousUI extends JFrame {
    private RendezVousDAO rendezVousDAO;
    private PatientDAO patientDAO;
    private MedecinDAO medecinDAO;
    
    private JTable rdvTable;
    private DefaultTableModel tableModel;
    private JComboBox<Patient> cbPatient;
    private JComboBox<Medecin> cbMedecin;
    private JTextField txtDate, txtHeure;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnClear, btnActualiser, btnAujourdhui;
    private JLabel lblTotalRdv;
    
    // CONSTRUCTEURS AJOUTÉS
    public RendezVousUI() {
        rendezVousDAO = new RendezVousDAOImpl();
        patientDAO = new PatientDAOImpl();
        medecinDAO = new MedecinDAOImpl();
        
        setTitle("Gestion des Rendez-vous");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
        loadRendezVous();
        loadCombos();
    }
    
    // Constructeur avec tous les DAO (pour mode réseau)
    public RendezVousUI(RendezVousDAO rdvDAO, PatientDAO patDAO, MedecinDAO medDAO) {
        this.rendezVousDAO = rdvDAO;
        this.patientDAO = patDAO;
        this.medecinDAO = medDAO;
        
        setTitle("Gestion des Rendez-vous");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
        loadRendezVous();
        loadCombos();
    }
    
    // MÉTHODES SETTER AJOUTÉES
    public void setRendezVousDAO(RendezVousDAO rendezVousDAO) {
        this.rendezVousDAO = rendezVousDAO;
        loadRendezVous();
    }
    
    public void setPatientDAO(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
        loadPatientsCombo();
    }
    
    public void setMedecinDAO(MedecinDAO medecinDAO) {
        this.medecinDAO = medecinDAO;
        loadMedecinsCombo();
    }
    
    // MÉTHODES UTILITAIRES AJOUTÉES
    private void loadPatientsCombo() {
        SwingWorker<List<Patient>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Patient> doInBackground() throws Exception {
                return patientDAO.getAllPatients();
            }
            
            @Override
            protected void done() {
                try {
                    cbPatient.removeAllItems();
                    List<Patient> patients = get();
                    for (Patient p : patients) {
                        cbPatient.addItem(p);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RendezVousUI.this,
                        "Erreur chargement patients: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void loadMedecinsCombo() {
        SwingWorker<List<Medecin>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Medecin> doInBackground() throws Exception {
                return medecinDAO.getAllMedecins();
            }
            
            @Override
            protected void done() {
                try {
                    cbMedecin.removeAllItems();
                    List<Medecin> medecins = get();
                    for (Medecin m : medecins) {
                        cbMedecin.addItem(m);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RendezVousUI.this,
                        "Erreur chargement médecins: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void loadCombos() {
        loadPatientsCombo();
        loadMedecinsCombo();
    }
    
    private void initUI() {
        // Panel principal avec fond blanc
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Nouveau Rendez-vous"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Labels en noir
        JLabel lblPatient = new JLabel("Patient *:");
        lblPatient.setForeground(Color.BLACK);
        JLabel lblMedecin = new JLabel("Médecin *:");
        lblMedecin.setForeground(Color.BLACK);
        JLabel lblDateLabel = new JLabel("Date (AAAA-MM-JJ):");
        lblDateLabel.setForeground(Color.BLACK);
        JLabel lblHeure = new JLabel("Heure (HH:MM):");
        lblHeure.setForeground(Color.BLACK);
        
        // Ligne 1: Patient
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lblPatient, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        cbPatient = new JComboBox<>();
        cbPatient.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Patient) {
                    Patient p = (Patient) value;
                    setText(p.getIdPatient() + " - " + p.getNom() + " " + p.getPrenom());
                }
                setForeground(Color.BLACK);
                setBackground(isSelected ? new Color(173, 216, 230) : Color.WHITE);
                return this;
            }
        });
        cbPatient.setForeground(Color.BLACK);
        cbPatient.setBackground(Color.WHITE);
        formPanel.add(cbPatient, gbc);
        
        // Ligne 2: Médecin
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(lblMedecin, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        cbMedecin = new JComboBox<>();
        cbMedecin.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Medecin) {
                    Medecin m = (Medecin) value;
                    setText(m.getIdMedecin() + " - Dr. " + m.getNom() + " (" + m.getSpecialite() + ")");
                }
                setForeground(Color.BLACK);
                setBackground(isSelected ? new Color(144, 238, 144) : Color.WHITE);
                return this;
            }
        });
        cbMedecin.setForeground(Color.BLACK);
        cbMedecin.setBackground(Color.WHITE);
        formPanel.add(cbMedecin, gbc);
        
        // Ligne 3: Date et Heure
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(lblDateLabel, gbc);
        
        gbc.gridx = 1;
        txtDate = createTextField();
        formPanel.add(txtDate, gbc);
        
        gbc.gridx = 2;
        formPanel.add(lblHeure, gbc);
        
        gbc.gridx = 3;
        txtHeure = createTextField();
        formPanel.add(txtHeure, gbc);
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE);
        
        btnAjouter = createStyledButton(" Ajouter RDV", new Color(173, 216, 230), Color.BLACK);
        btnModifier = createStyledButton(" Modifier RDV", new Color(144, 238, 144), Color.BLACK);
        btnSupprimer = createStyledButton(" Supprimer RDV", new Color(240, 128, 128), Color.BLACK);
        btnClear = createStyledButton(" Effacer", new Color(211, 211, 211), Color.BLACK);
        btnActualiser = createStyledButton(" Actualiser", new Color(221, 160, 221), Color.BLACK);
        btnAujourdhui = createStyledButton(" Aujourd'hui", new Color(255, 218, 185), Color.BLACK);
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnActualiser);
        buttonPanel.add(btnAujourdhui);
        
        // Panel de filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtres"));
        
        JLabel lblFiltrePatient = new JLabel("Filtrer par patient:");
        lblFiltrePatient.setForeground(Color.BLACK);
        JComboBox<Patient> cbFilterPatient = new JComboBox<>();
        cbFilterPatient.addItem(null); // Option "Tous"
        cbFilterPatient.setForeground(Color.BLACK);
        cbFilterPatient.setBackground(Color.WHITE);
        
        JLabel lblFiltreMedecin = new JLabel("Filtrer par médecin:");
        lblFiltreMedecin.setForeground(Color.BLACK);
        JComboBox<Medecin> cbFilterMedecin = new JComboBox<>();
        cbFilterMedecin.addItem(null); // Option "Tous"
        cbFilterMedecin.setForeground(Color.BLACK);
        cbFilterMedecin.setBackground(Color.WHITE);
        
        filterPanel.add(lblFiltrePatient);
        filterPanel.add(cbFilterPatient);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(lblFiltreMedecin);
        filterPanel.add(cbFilterMedecin);
        
        // Table des rendez-vous
        String[] columns = {"ID", "Date/Heure", "Patient", "Médecin", "Spécialité"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        rdvTable = new JTable(tableModel);
        
        // Personnalisation table - texte noir
        rdvTable.setForeground(Color.BLACK);
        rdvTable.setBackground(Color.WHITE);
        rdvTable.setGridColor(Color.LIGHT_GRAY);
        rdvTable.setSelectionBackground(new Color(255, 218, 185));
        rdvTable.setSelectionForeground(Color.BLACK);
        
        rdvTable.setRowHeight(30);
        rdvTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        rdvTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        rdvTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        rdvTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        
        // En-tête de table
        rdvTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        rdvTable.getTableHeader().setForeground(Color.BLACK);
        rdvTable.getTableHeader().setBackground(new Color(240, 240, 240));
        
        JScrollPane scrollPane = new JScrollPane(rdvTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des Rendez-vous"));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Panel de statistiques
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(Color.WHITE);
        lblTotalRdv = new JLabel("Total rendez-vous: 0");
        lblTotalRdv.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalRdv.setForeground(Color.BLACK);
        statsPanel.add(lblTotalRdv);
        
        // Assemblage
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statsPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        setupListeners(cbFilterPatient, cbFilterMedecin);
        setDateAujourdhui();
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField(15);
        field.setForeground(Color.BLACK);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void setupListeners(JComboBox<Patient> cbFilterPatient, JComboBox<Medecin> cbFilterMedecin) {
        btnAjouter.addActionListener(e -> ajouterRendezVous());
        btnModifier.addActionListener(e -> modifierRendezVous());
        btnSupprimer.addActionListener(e -> supprimerRendezVous());
        btnClear.addActionListener(e -> clearForm());
        btnActualiser.addActionListener(e -> loadRendezVous());
        btnAujourdhui.addActionListener(e -> setDateAujourdhui());
        
        cbFilterPatient.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                filterByPatient((Patient) cbFilterPatient.getSelectedItem());
            }
        });
        
        cbFilterMedecin.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                filterByMedecin((Medecin) cbFilterMedecin.getSelectedItem());
            }
        });
        
        rdvTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectionnerRendezVous();
            }
        });
    }
    
    private void loadRendezVous() {
        SwingWorker<List<RendezVous>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<RendezVous> doInBackground() throws Exception {
                return rendezVousDAO.getAllRendezVous();
            }
            
            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    List<RendezVous> rendezVous = get();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    
                    for (RendezVous rdv : rendezVous) {
                        Object[] row = {
                            rdv.getIdRdv(),
                            sdf.format(rdv.getDate()),
                            rdv.getPatientNom(),
                            rdv.getMedecinNom().split("\\(")[0].trim(),
                            rdv.getMedecinNom().split("\\(")[1].replace(")", "")
                        };
                        tableModel.addRow(row);
                    }
                    
                    lblTotalRdv.setText("Total rendez-vous: " + rendezVous.size());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RendezVousUI.this,
                        "Erreur lors du chargement des rendez-vous: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void ajouterRendezVous() {
        if (!validateForm()) {
            return;
        }
        
        try {
            Patient patient = (Patient) cbPatient.getSelectedItem();
            Medecin medecin = (Medecin) cbMedecin.getSelectedItem();
            
            // Combiner date et heure
            String dateTimeStr = txtDate.getText().trim() + " " + txtHeure.getText().trim() + ":00";
            Timestamp timestamp = Timestamp.valueOf(dateTimeStr);
            
            RendezVous rdv = new RendezVous();
            rdv.setDate(timestamp);
            rdv.setIdPatient(patient.getIdPatient());
            rdv.setIdMedecin(medecin.getIdMedecin());
            
            rendezVousDAO.addRendezVous(rdv);
            
            JOptionPane.showMessageDialog(this,
                "Rendez-vous ajouté avec succès!\nID RDV: " + rdv.getIdRdv(),
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            
            loadRendezVous();
            clearForm();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ajout: " + e.getMessage() + 
                "\nFormat attendu: AAAA-MM-JJ HH:MM",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void selectionnerRendezVous() {
        int selectedRow = rdvTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int idRdv = (int) tableModel.getValueAt(selectedRow, 0);
                RendezVous rdv = rendezVousDAO.getRendezVous(idRdv);
                
                if (rdv != null) {
                    // Trouver et sélectionner le patient
                    for (int i = 0; i < cbPatient.getItemCount(); i++) {
                        Patient p = cbPatient.getItemAt(i);
                        if (p.getIdPatient() == rdv.getIdPatient()) {
                            cbPatient.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    // Trouver et sélectionner le médecin
                    for (int i = 0; i < cbMedecin.getItemCount(); i++) {
                        Medecin m = cbMedecin.getItemAt(i);
                        if (m.getIdMedecin() == rdv.getIdMedecin()) {
                            cbMedecin.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    // Formater la date et l'heure
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    Date date = new Date(rdv.getDate().getTime());
                    
                    txtDate.setText(dateFormat.format(date));
                    txtHeure.setText(timeFormat.format(date));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void modifierRendezVous() {
        int selectedRow = rdvTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un rendez-vous à modifier",
                "Avertissement",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateForm()) {
            return;
        }
        
        try {
            int idRdv = (int) tableModel.getValueAt(selectedRow, 0);
            RendezVous rdv = rendezVousDAO.getRendezVous(idRdv);
            
            if (rdv != null) {
                Patient patient = (Patient) cbPatient.getSelectedItem();
                Medecin medecin = (Medecin) cbMedecin.getSelectedItem();
                
                String dateTimeStr = txtDate.getText().trim() + " " + txtHeure.getText().trim() + ":00";
                rdv.setDate(Timestamp.valueOf(dateTimeStr));
                rdv.setIdPatient(patient.getIdPatient());
                rdv.setIdMedecin(medecin.getIdMedecin());
                
                rendezVousDAO.updateRendezVous(rdv);
                
                JOptionPane.showMessageDialog(this,
                    "Rendez-vous modifié avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadRendezVous();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la modification: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerRendezVous() {
        int selectedRow = rdvTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un rendez-vous à supprimer",
                "Avertissement",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idRdv = (int) tableModel.getValueAt(selectedRow, 0);
        String dateRdv = tableModel.getValueAt(selectedRow, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer le rendez-vous:\n" +
            "ID: " + idRdv + "\n" +
            "Date: " + dateRdv + "?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                rendezVousDAO.deleteRendezVous(idRdv);
                
                JOptionPane.showMessageDialog(this,
                    "Rendez-vous supprimé avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadRendezVous();
                clearForm();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void filterByPatient(Patient patient) {
        if (patient == null) {
            loadRendezVous();
            return;
        }
        
        SwingWorker<List<RendezVous>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<RendezVous> doInBackground() throws Exception {
                return rendezVousDAO.getAppointmentsByPatient(patient);
            }
            
            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    List<RendezVous> rendezVous = get();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    
                    for (RendezVous rdv : rendezVous) {
                        Object[] row = {
                            rdv.getIdRdv(),
                            sdf.format(rdv.getDate()),
                            rdv.getPatientNom(),
                            rdv.getMedecinNom().split("\\(")[0].trim(),
                            rdv.getMedecinNom().split("\\(")[1].replace(")", "")
                        };
                        tableModel.addRow(row);
                    }
                    
                    lblTotalRdv.setText("Rendez-vous pour " + patient.getNom() + ": " + rendezVous.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void filterByMedecin(Medecin medecin) {
        if (medecin == null) {
            loadRendezVous();
            return;
        }
        
        SwingWorker<List<RendezVous>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<RendezVous> doInBackground() throws Exception {
                return rendezVousDAO.getAppointmentsByMedecin(medecin);
            }
            
            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    List<RendezVous> rendezVous = get();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    
                    for (RendezVous rdv : rendezVous) {
                        Object[] row = {
                            rdv.getIdRdv(),
                            sdf.format(rdv.getDate()),
                            rdv.getPatientNom(),
                            rdv.getMedecinNom().split("\\(")[0].trim(),
                            rdv.getMedecinNom().split("\\(")[1].replace(")", "")
                        };
                        tableModel.addRow(row);
                    }
                    
                    lblTotalRdv.setText("Rendez-vous avec Dr. " + medecin.getNom() + ": " + rendezVous.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private boolean validateForm() {
        if (cbPatient.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un patient",
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            cbPatient.requestFocus();
            return false;
        }
        
        if (cbMedecin.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un médecin",
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            cbMedecin.requestFocus();
            return false;
        }
        
        if (txtDate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "La date est obligatoire",
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            txtDate.requestFocus();
            return false;
        }
        
        if (txtHeure.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "L'heure est obligatoire",
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            txtHeure.requestFocus();
            return false;
        }
        
        // Validation format date
        if (!txtDate.getText().trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                "Format de date invalide. Utilisez AAAA-MM-JJ",
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            txtDate.requestFocus();
            return false;
        }
        
        // Validation format heure
        if (!txtHeure.getText().trim().matches("\\d{2}:\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                "Format d'heure invalide. Utilisez HH:MM",
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            txtHeure.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void setDateAujourdhui() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        txtDate.setText(dateFormat.format(cal.getTime()));
        txtHeure.setText(timeFormat.format(cal.getTime()));
    }
    
    private void clearForm() {
        cbPatient.setSelectedIndex(-1);
        cbMedecin.setSelectedIndex(-1);
        setDateAujourdhui();
        rdvTable.clearSelection();
    }
}