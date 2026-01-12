package ui;

import dao.impl.PatientDAOImpl;
import dao.interfaces.PatientDAO;
import models.Patient;
import client.NetworkDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class PatientUI extends JFrame {
    private PatientDAO patientDAO;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtNom, txtPrenom, txtDateNaissance, txtAdresse, txtTelephone, txtSearch;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnClear, btnActualiser;
    private JLabel lblTotalPatients;

    // CONSTRUCTEURS
    public PatientUI() {
        patientDAO = new PatientDAOImpl(); // Mode local par défaut
        setTitle("Gestion des Patients");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadPatients();
    }

    // Constructeur avec DAO (pour mode réseau)
    public PatientUI(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
        setTitle("Gestion des Patients");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadPatients();
    }

    // MÉTHODE SETTER AJOUTÉE (IMPORTANT pour mode réseau)
    public void setPatientDAO(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
        loadPatients(); // Recharger les données avec le nouveau DAO
    }

    // MÉTHODE POUR GÉRER LES ERREURS RÉSEAU
    private void handleNetworkError(Exception e) {
        if (patientDAO instanceof NetworkDAO) {
            JOptionPane.showMessageDialog(this,
                    "Erreur réseau: " + e.getMessage(),
                    "Erreur réseau",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        // Panel principal avec fond clair
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Recherche de Patients"));

        JLabel lblSearch = new JLabel("Rechercher:");
        lblSearch.setForeground(Color.BLACK);
        txtSearch = new JTextField(30);
        txtSearch.setForeground(Color.BLACK);
        txtSearch.setBackground(Color.WHITE);

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);

        // Panel de formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Informations du Patient"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Créer des labels avec texte noir
        JLabel lblNom = new JLabel("Nom *:");
        lblNom.setForeground(Color.BLACK);
        JLabel lblPrenom = new JLabel("Prénom *:");
        lblPrenom.setForeground(Color.BLACK);
        JLabel lblDate = new JLabel("Date naissance (AAAA-MM-JJ):");
        lblDate.setForeground(Color.BLACK);
        JLabel lblTel = new JLabel("Téléphone *:");
        lblTel.setForeground(Color.BLACK);
        JLabel lblAdresse = new JLabel("Adresse:");
        lblAdresse.setForeground(Color.BLACK);

        // Ligne 1: Nom et Prénom
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblNom, gbc);

        gbc.gridx = 1;
        txtNom = createTextField();
        formPanel.add(txtNom, gbc);

        gbc.gridx = 2;
        formPanel.add(lblPrenom, gbc);

        gbc.gridx = 3;
        txtPrenom = createTextField();
        formPanel.add(txtPrenom, gbc);

        // Ligne 2: Date de naissance et Téléphone
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblDate, gbc);

        gbc.gridx = 1;
        txtDateNaissance = createTextField();
        formPanel.add(txtDateNaissance, gbc);

        gbc.gridx = 2;
        formPanel.add(lblTel, gbc);

        gbc.gridx = 3;
        txtTelephone = createTextField();
        formPanel.add(txtTelephone, gbc);

        // Ligne 3: Adresse
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(lblAdresse, gbc);

        gbc.gridy = 3;
        txtAdresse = createTextField();
        formPanel.add(txtAdresse, gbc);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        btnAjouter = createStyledButton("Ajouter", new Color(173, 216, 230), Color.BLACK);
        btnModifier = createStyledButton(" Modifier", new Color(144, 238, 144), Color.BLACK);
        btnSupprimer = createStyledButton("Supprimer", new Color(240, 128, 128), Color.BLACK);
        btnClear = createStyledButton("Effacer", new Color(211, 211, 211), Color.BLACK);
        btnActualiser = createStyledButton("Actualiser", new Color(221, 160, 221), Color.BLACK);

        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnActualiser);

        // Table des patients
        String[] columns = { "ID", "Nom", "Prénom", "Date Naissance", "Adresse", "Téléphone", "Âge" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0)
                    return Integer.class;
                return String.class;
            }
        };

        patientTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        patientTable.setRowSorter(sorter);

        // Personnalisation de la table - texte noir
        patientTable.setForeground(Color.BLACK);
        patientTable.setBackground(Color.WHITE);
        patientTable.setGridColor(Color.LIGHT_GRAY);
        patientTable.setSelectionBackground(new Color(173, 216, 230));
        patientTable.setSelectionForeground(Color.BLACK);

        patientTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        patientTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        patientTable.getColumnModel().getColumn(6).setPreferredWidth(50);

        patientTable.setRowHeight(30);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // En-tête de table en noir
        patientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        patientTable.getTableHeader().setForeground(Color.BLACK);
        patientTable.getTableHeader().setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des Patients"));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Panel de statistiques
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(Color.WHITE);
        lblTotalPatients = new JLabel("Total patients: 0");
        lblTotalPatients.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalPatients.setForeground(Color.BLACK);
        statsPanel.add(lblTotalPatients);

        // Assemblage
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statsPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Ajout des listeners
        setupListeners();
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setForeground(Color.BLACK);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
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

    private void setupListeners() {
        btnAjouter.addActionListener(e -> ajouterPatient());
        btnModifier.addActionListener(e -> modifierPatient());
        btnSupprimer.addActionListener(e -> supprimerPatient());
        btnClear.addActionListener(e -> clearForm());
        btnActualiser.addActionListener(e -> loadPatients());

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });

        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectionnerPatient();
            }
        });
    }

    private void loadPatients() {
        SwingWorker<List<Patient>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Patient> doInBackground() throws Exception {
                return patientDAO.getAllPatients();
            }

            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    List<Patient> patients = get();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                    for (Patient patient : patients) {
                        String dateNaissance = patient.getDateNaissance() != null
                                ? sdf.format(patient.getDateNaissance())
                                : "";

                        // Calcul de l'âge
                        String age = "";
                        if (patient.getDateNaissance() != null) {
                            int anneeNaissance = patient.getDateNaissance().toLocalDate().getYear();
                            int anneeActuelle = java.time.LocalDate.now().getYear();
                            age = String.valueOf(anneeActuelle - anneeNaissance);
                        }

                        Object[] row = {
                                patient.getIdPatient(),
                                patient.getNom(),
                                patient.getPrenom(),
                                dateNaissance,
                                patient.getAdresse(),
                                patient.getTelephone(),
                                age
                        };
                        tableModel.addRow(row);
                    }

                    lblTotalPatients.setText("Total patients: " + patients.size());
                } catch (Exception e) {
                    handleNetworkError(e);
                    JOptionPane.showMessageDialog(PatientUI.this,
                            "Erreur lors du chargement des patients: " + e.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void filterTable() {
        String text = txtSearch.getText().trim();
        if (text.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2));
        }
    }

    private void ajouterPatient() {
        if (!validateForm()) {
            return;
        }

        try {
            Patient patient = new Patient();
            patient.setNom(txtNom.getText().trim());
            patient.setPrenom(txtPrenom.getText().trim());

            if (!txtDateNaissance.getText().trim().isEmpty()) {
                patient.setDateNaissance(Date.valueOf(txtDateNaissance.getText().trim()));
            }

            patient.setAdresse(txtAdresse.getText().trim());
            patient.setTelephone(txtTelephone.getText().trim());

            patientDAO.addPatient(patient);

            JOptionPane.showMessageDialog(this,
                    "Patient ajouté avec succès!\nID Patient: " + patient.getIdPatient(),
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            loadPatients();
            clearForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectionnerPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = patientTable.convertRowIndexToModel(selectedRow);

            txtNom.setText(tableModel.getValueAt(modelRow, 1).toString());
            txtPrenom.setText(tableModel.getValueAt(modelRow, 2).toString());
            txtDateNaissance.setText(tableModel.getValueAt(modelRow, 3).toString());
            txtAdresse.setText(tableModel.getValueAt(modelRow, 4).toString());
            txtTelephone.setText(tableModel.getValueAt(modelRow, 5).toString());
        }
    }

    private void modifierPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un patient à modifier",
                    "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            int modelRow = patientTable.convertRowIndexToModel(selectedRow);
            int idPatient = (int) tableModel.getValueAt(modelRow, 0);

            Patient patient = patientDAO.getPatient(idPatient);
            if (patient != null) {
                patient.setNom(txtNom.getText().trim());
                patient.setPrenom(txtPrenom.getText().trim());

                if (!txtDateNaissance.getText().trim().isEmpty()) {
                    patient.setDateNaissance(Date.valueOf(txtDateNaissance.getText().trim()));
                } else {
                    patient.setDateNaissance(null);
                }

                patient.setAdresse(txtAdresse.getText().trim());
                patient.setTelephone(txtTelephone.getText().trim());

                patientDAO.updatePatient(patient);

                JOptionPane.showMessageDialog(this,
                        "Patient modifié avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);

                loadPatients();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la modification: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un patient à supprimer",
                    "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        int idPatient = (int) tableModel.getValueAt(modelRow, 0);
        String nomPatient = tableModel.getValueAt(modelRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer le patient:\n" +
                        nomPatient + " (ID: " + idPatient + ")?\n\n" +
                        "Cette action est irréversible et supprimera également\n" +
                        "tous ses rendez-vous et traitements associés.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                patientDAO.deletePatient(idPatient);

                JOptionPane.showMessageDialog(this,
                        "Patient supprimé avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);

                loadPatients();
                clearForm();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (txtNom.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le nom est obligatoire",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            txtNom.requestFocus();
            return false;
        }

        if (txtPrenom.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le prénom est obligatoire",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            txtPrenom.requestFocus();
            return false;
        }

        if (txtTelephone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le téléphone est obligatoire",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            txtTelephone.requestFocus();
            return false;
        }

        if (!txtDateNaissance.getText().trim().isEmpty()) {
            try {
                Date.valueOf(txtDateNaissance.getText().trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Format de date invalide. Utilisez AAAA-MM-JJ",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                txtDateNaissance.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void clearForm() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtDateNaissance.setText("");
        txtAdresse.setText("");
        txtTelephone.setText("");
        patientTable.clearSelection();
    }
}