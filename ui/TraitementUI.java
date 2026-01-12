package ui;

import dao.impl.MedecinDAOImpl;
import dao.impl.PatientDAOImpl;
import dao.impl.TraitementDAOImpl;
import dao.interfaces.MedecinDAO;
import dao.interfaces.PatientDAO;
import dao.interfaces.TraitementDAO;
import models.Medecin;
import models.Patient;
import models.Traitement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

public class TraitementUI extends JFrame {
    private TraitementDAO traitementDAO;
    private PatientDAO patientDAO;
    private MedecinDAO medecinDAO;
    
    private JTable traitementTable;
    private DefaultTableModel tableModel;
    private JComboBox<Patient> cbPatient;
    private JComboBox<Medecin> cbMedecin;
    private JTextArea txtDescription;
    private JTextField txtCout;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnClear, btnActualiser, btnGenererRapport;
    private JLabel lblTotalTraitements, lblTotalCout;
    
    // CONSTRUCTEURS AJOUTÉS
    public TraitementUI() {
        traitementDAO = new TraitementDAOImpl();
        patientDAO = new PatientDAOImpl();
        medecinDAO = new MedecinDAOImpl();
        
        setTitle("Gestion des Traitements");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
        loadTraitements();
        loadCombos();
    }
    
    // Constructeur avec tous les DAO (pour mode réseau)
    public TraitementUI(TraitementDAO traDAO, PatientDAO patDAO, MedecinDAO medDAO) {
        this.traitementDAO = traDAO;
        this.patientDAO = patDAO;
        this.medecinDAO = medDAO;
        
        setTitle("Gestion des Traitements");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
        loadTraitements();
        loadCombos();
    }
    
    // MÉTHODES SETTER AJOUTÉES
    public void setTraitementDAO(TraitementDAO traitementDAO) {
        this.traitementDAO = traitementDAO;
        loadTraitements();
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
                    JOptionPane.showMessageDialog(TraitementUI.this,
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
                    JOptionPane.showMessageDialog(TraitementUI.this,
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Nouveau Traitement"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Labels en noir
        JLabel lblPatient = new JLabel("Patient *:");
        lblPatient.setForeground(Color.BLACK);
        JLabel lblMedecin = new JLabel("Médecin *:");
        lblMedecin.setForeground(Color.BLACK);
        JLabel lblDescription = new JLabel("Description *:");
        lblDescription.setForeground(Color.BLACK);
        JLabel lblCout = new JLabel("Coût (€) *:");
        lblCout.setForeground(Color.BLACK);
        
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
        
        // Ligne 3: Description
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(lblDescription, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.gridheight = 2;
        txtDescription = new JTextArea(4, 30);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setForeground(Color.BLACK);
        txtDescription.setBackground(Color.WHITE);
        txtDescription.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollDescription = new JScrollPane(txtDescription);
        scrollDescription.getViewport().setBackground(Color.WHITE);
        formPanel.add(scrollDescription, gbc);
        
        // Ligne 4: Coût
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridheight = 1; gbc.gridwidth = 1;
        formPanel.add(lblCout, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtCout = createTextField();
        formPanel.add(txtCout, gbc);
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE);
        
        btnAjouter = createStyledButton(" Ajouter Traitement", new Color(173, 216, 230), Color.BLACK);
        btnModifier = createStyledButton(" Modifier Traitement", new Color(144, 238, 144), Color.BLACK);
        btnSupprimer = createStyledButton(" Supprimer Traitement", new Color(240, 128, 128), Color.BLACK);
        btnClear = createStyledButton(" Effacer", new Color(211, 211, 211), Color.BLACK);
        btnActualiser = createStyledButton(" Actualiser", new Color(221, 160, 221), Color.BLACK);
        btnGenererRapport = createStyledButton(" Générer Rapport", new Color(255, 218, 185), Color.BLACK);
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnActualiser);
        buttonPanel.add(btnGenererRapport);
        
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
        
        // Table des traitements
        String[] columns = {"ID", "Patient", "Médecin", "Description", "Coût (€)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Double.class;
                return String.class;
            }
        };
        
        traitementTable = new JTable(tableModel);
        
        // Personnalisation table - texte noir
        traitementTable.setForeground(Color.BLACK);
        traitementTable.setBackground(Color.WHITE);
        traitementTable.setGridColor(Color.LIGHT_GRAY);
        traitementTable.setSelectionBackground(new Color(221, 160, 221));
        traitementTable.setSelectionForeground(Color.BLACK);
        
        traitementTable.setRowHeight(30);
        traitementTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        traitementTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        traitementTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        traitementTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        traitementTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        
        // En-tête de table
        traitementTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        traitementTable.getTableHeader().setForeground(Color.BLACK);
        traitementTable.getTableHeader().setBackground(new Color(240, 240, 240));
        
        JScrollPane scrollPane = new JScrollPane(traitementTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des Traitements"));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Panel de statistiques
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statsPanel.setBackground(Color.WHITE);
        lblTotalTraitements = new JLabel("Total traitements: 0");
        lblTotalCout = new JLabel("Coût total: 0,00 €");
        
        lblTotalTraitements.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalTraitements.setForeground(Color.BLACK);
        lblTotalCout.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalCout.setForeground(new Color(0, 102, 0));
        
        statsPanel.add(lblTotalTraitements);
        statsPanel.add(lblTotalCout);
        
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
        btnAjouter.addActionListener(e -> ajouterTraitement());
        btnModifier.addActionListener(e -> modifierTraitement());
        btnSupprimer.addActionListener(e -> supprimerTraitement());
        btnClear.addActionListener(e -> clearForm());
        btnActualiser.addActionListener(e -> loadTraitements());
        btnGenererRapport.addActionListener(e -> genererRapport());
        
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
        
        traitementTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectionnerTraitement();
            }
        });
    }
    
    private void loadTraitements() {
        SwingWorker<List<Traitement>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Traitement> doInBackground() throws Exception {
                return traitementDAO.getAllTraitements();
            }
            
            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    List<Traitement> traitements = get();
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(2);
                    
                    BigDecimal totalCout = BigDecimal.ZERO;
                    
                    for (Traitement traitement : traitements) {
                        Object[] row = {
                            traitement.getIdTraitement(),
                            traitement.getPatientNom(),
                            traitement.getMedecinNom().split("\\(")[0].trim(),
                            traitement.getDescription(),
                            traitement.getCout().doubleValue()
                        };
                        tableModel.addRow(row);
                        totalCout = totalCout.add(traitement.getCout());
                    }
                    
                    lblTotalTraitements.setText("Total traitements: " + traitements.size());
                    lblTotalCout.setText("Coût total: " + nf.format(totalCout) + " €");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TraitementUI.this,
                        "Erreur lors du chargement des traitements: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void ajouterTraitement() {
        if (!validateForm()) {
            return;
        }
        
        try {
            Patient patient = (Patient) cbPatient.getSelectedItem();
            Medecin medecin = (Medecin) cbMedecin.getSelectedItem();
            
            Traitement traitement = new Traitement();
            traitement.setDescription(txtDescription.getText().trim());
            traitement.setCout(new BigDecimal(txtCout.getText().trim().replace(",", ".")));
            traitement.setIdPatient(patient.getIdPatient());
            traitement.setIdMedecin(medecin.getIdMedecin());
            
            traitementDAO.addTraitement(traitement);
            
            JOptionPane.showMessageDialog(this,
                "Traitement ajouté avec succès!\nID Traitement: " + traitement.getIdTraitement(),
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            
            loadTraitements();
            clearForm();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ajout: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void selectionnerTraitement() {
        int selectedRow = traitementTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int idTraitement = (int) tableModel.getValueAt(selectedRow, 0);
                Traitement traitement = traitementDAO.getTraitement(idTraitement);
                
                if (traitement != null) {
                    // Trouver et sélectionner le patient
                    for (int i = 0; i < cbPatient.getItemCount(); i++) {
                        Patient p = cbPatient.getItemAt(i);
                        if (p.getIdPatient() == traitement.getIdPatient()) {
                            cbPatient.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    // Trouver et sélectionner le médecin
                    for (int i = 0; i < cbMedecin.getItemCount(); i++) {
                        Medecin m = cbMedecin.getItemAt(i);
                        if (m.getIdMedecin() == traitement.getIdMedecin()) {
                            cbMedecin.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    txtDescription.setText(traitement.getDescription());
                    txtCout.setText(traitement.getCout().toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void modifierTraitement() {
        int selectedRow = traitementTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un traitement à modifier",
                "Avertissement",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateForm()) {
            return;
        }
        
        try {
            int idTraitement = (int) tableModel.getValueAt(selectedRow, 0);
            Traitement traitement = traitementDAO.getTraitement(idTraitement);
            
            if (traitement != null) {
                Patient patient = (Patient) cbPatient.getSelectedItem();
                Medecin medecin = (Medecin) cbMedecin.getSelectedItem();
                
                traitement.setDescription(txtDescription.getText().trim());
                traitement.setCout(new BigDecimal(txtCout.getText().trim().replace(",", ".")));
                traitement.setIdPatient(patient.getIdPatient());
                traitement.setIdMedecin(medecin.getIdMedecin());
                
                traitementDAO.updateTraitement(traitement);
                
                JOptionPane.showMessageDialog(this,
                    "Traitement modifié avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadTraitements();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la modification: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerTraitement() {
        int selectedRow = traitementTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un traitement à supprimer",
                "Avertissement",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idTraitement = (int) tableModel.getValueAt(selectedRow, 0);
        String description = tableModel.getValueAt(selectedRow, 3).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer le traitement:\n" +
            "ID: " + idTraitement + "\n" +
            "Description: " + (description.length() > 50 ? description.substring(0, 50) + "..." : description),
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                traitementDAO.deleteTraitement(idTraitement);
                
                JOptionPane.showMessageDialog(this,
                    "Traitement supprimé avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadTraitements();
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
            loadTraitements();
            return;
        }
        
        SwingWorker<List<Traitement>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Traitement> doInBackground() throws Exception {
                return traitementDAO.getTreatmentsByPatientId(patient.getIdPatient());
            }
            
            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    List<Traitement> traitements = get();
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(2);
                    
                    BigDecimal totalCout = BigDecimal.ZERO;
                    
                    for (Traitement traitement : traitements) {
                        Object[] row = {
                            traitement.getIdTraitement(),
                            traitement.getPatientNom(),
                            traitement.getMedecinNom().split("\\(")[0].trim(),
                            traitement.getDescription(),
                            traitement.getCout().doubleValue()
                        };
                        tableModel.addRow(row);
                        totalCout = totalCout.add(traitement.getCout());
                    }
                    
                    lblTotalTraitements.setText("Traitements pour " + patient.getNom() + ": " + traitements.size());
                    lblTotalCout.setText("Coût total: " + nf.format(totalCout) + " €");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void filterByMedecin(Medecin medecin) {
        if (medecin == null) {
            loadTraitements();
            return;
        }
        
        SwingWorker<List<Traitement>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Traitement> doInBackground() throws Exception {
                return traitementDAO.getTreatmentsByMedecinId(medecin.getIdMedecin());
            }
            
            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    List<Traitement> traitements = get();
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(2);
                    
                    BigDecimal totalCout = BigDecimal.ZERO;
                    
                    for (Traitement traitement : traitements) {
                        Object[] row = {
                            traitement.getIdTraitement(),
                            traitement.getPatientNom(),
                            traitement.getMedecinNom().split("\\(")[0].trim(),
                            traitement.getDescription(),
                            traitement.getCout().doubleValue()
                        };
                        tableModel.addRow(row);
                        totalCout = totalCout.add(traitement.getCout());
                    }
                    
                    lblTotalTraitements.setText("Traitements par Dr. " + medecin.getNom() + ": " + traitements.size());
                    lblTotalCout.setText("Coût total: " + nf.format(totalCout) + " €");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void genererRapport() {
        Patient patient = (Patient) cbPatient.getSelectedItem();
        if (patient == null) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un patient pour générer un rapport",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        SwingWorker<List<Traitement>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Traitement> doInBackground() throws Exception {
                return traitementDAO.getTreatmentsByPatientId(patient.getIdPatient());
            }
            
            @Override
            protected void done() {
                try {
                    List<Traitement> traitements = get();
                    if (traitements.isEmpty()) {
                        JOptionPane.showMessageDialog(TraitementUI.this,
                            "Aucun traitement trouvé pour ce patient.",
                            "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(2);
                    
                    BigDecimal totalCout = BigDecimal.ZERO;
                    StringBuilder rapport = new StringBuilder();
                    
                    rapport.append("RAPPORT DES TRAITEMENTS\n");
                    rapport.append("=====================\n\n");
                    rapport.append("Patient: ").append(patient.getNom()).append(" ").append(patient.getPrenom()).append("\n");
                    rapport.append("ID Patient: ").append(patient.getIdPatient()).append("\n");
                    rapport.append("Téléphone: ").append(patient.getTelephone()).append("\n");
                    rapport.append("\nTRAITEMENTS:\n");
                    rapport.append("------------\n");
                    
                    for (Traitement traitement : traitements) {
                        rapport.append("\nID: ").append(traitement.getIdTraitement()).append("\n");
                        rapport.append("Médecin: ").append(traitement.getMedecinNom()).append("\n");
                        rapport.append("Description: ").append(traitement.getDescription()).append("\n");
                        rapport.append("Coût: ").append(nf.format(traitement.getCout())).append(" €\n");
                        totalCout = totalCout.add(traitement.getCout());
                    }
                    
                    rapport.append("\n=====================\n");
                    rapport.append("Nombre de traitements: ").append(traitements.size()).append("\n");
                    rapport.append("Coût total: ").append(nf.format(totalCout)).append(" €\n");
                    
                    // Afficher le rapport dans une boîte de dialogue
                    JTextArea textArea = new JTextArea(rapport.toString(), 20, 50);
                    textArea.setEditable(false);
                    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    textArea.setForeground(Color.BLACK);
                    textArea.setBackground(Color.WHITE);
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.getViewport().setBackground(Color.WHITE);
                    
                    JOptionPane.showMessageDialog(TraitementUI.this, scrollPane,
                        "Rapport des Traitements - " + patient.getNom() + " " + patient.getPrenom(),
                        JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TraitementUI.this,
                        "Erreur lors de la génération du rapport: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
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
        
        if (txtDescription.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "La description est obligatoire",
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            txtDescription.requestFocus();
            return false;
        }
        
        if (txtCout.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Le coût est obligatoire",
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            txtCout.requestFocus();
            return false;
        }
        
        // Validation coût numérique
        try {
            new BigDecimal(txtCout.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Le coût doit être un nombre valide",
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            txtCout.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        cbPatient.setSelectedIndex(-1);
        cbMedecin.setSelectedIndex(-1);
        txtDescription.setText("");
        txtCout.setText("");
        traitementTable.clearSelection();
    }
}