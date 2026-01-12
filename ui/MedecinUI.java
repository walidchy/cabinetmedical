package ui;

import dao.impl.MedecinDAOImpl;
import dao.interfaces.MedecinDAO;
import models.Medecin;
import client.NetworkDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class MedecinUI extends JFrame {
    private MedecinDAO medecinDAO;
    private JTable medecinTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtNom, txtSpecialite, txtTelephone, txtSearch;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnClear, btnActualiser;
    private JLabel lblTotalMedecins;
    
    // CONSTRUCTEURS AJOUTÉS POUR LE MODE RÉSEAU
    public MedecinUI() {
        medecinDAO = new MedecinDAOImpl();  // Mode local par défaut
        setTitle("Gestion des Médecins");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
        loadMedecins();
    }
    
    // Constructeur avec DAO (pour mode réseau)
    public MedecinUI(MedecinDAO medecinDAO) {
        this.medecinDAO = medecinDAO;
        setTitle("Gestion des Médecins");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
        loadMedecins();
    }
    
    // MÉTHODE SETTER AJOUTÉE (IMPORTANT pour mode réseau)
    public void setMedecinDAO(MedecinDAO medecinDAO) {
        this.medecinDAO = medecinDAO;
        loadMedecins();  // Recharger les données avec le nouveau DAO
    }
    
    // MÉTHODE POUR GÉRER LES OPÉRATIONS NON SUPPORTÉES EN RÉSEAU
    private void handleUnsupportedOperation() {
        if (medecinDAO instanceof NetworkDAO) {
            JOptionPane.showMessageDialog(this,
                "Cette fonctionnalité n'est pas disponible en mode réseau.",
                "Non supporté",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Recherche de Médecins"));
        
        JLabel lblSearch = new JLabel("Rechercher:");
        lblSearch.setForeground(Color.BLACK);
        txtSearch = new JTextField(25);
        txtSearch.setForeground(Color.BLACK);
        txtSearch.setBackground(Color.WHITE);
        
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        
        // Panel de formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Informations du Médecin"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Labels en noir
        JLabel lblNom = new JLabel("Nom *:");
        lblNom.setForeground(Color.BLACK);
        JLabel lblSpecialite = new JLabel("Spécialité:");
        lblSpecialite.setForeground(Color.BLACK);
        JLabel lblTel = new JLabel("Téléphone *:");
        lblTel.setForeground(Color.BLACK);
        
        // Ligne 1: Nom
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lblNom, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtNom = createTextField();
        formPanel.add(txtNom, gbc);
        
        // Ligne 2: Spécialité
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(lblSpecialite, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtSpecialite = createTextField();
        formPanel.add(txtSpecialite, gbc);
        
        // Ligne 3: Téléphone
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(lblTel, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtTelephone = createTextField();
        formPanel.add(txtTelephone, gbc);
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE);
        
        btnAjouter = createStyledButton(" Ajouter", new Color(173, 216, 230), Color.BLACK);
        btnModifier = createStyledButton(" Modifier", new Color(144, 238, 144), Color.BLACK);
        btnSupprimer = createStyledButton(" Supprimer", new Color(240, 128, 128), Color.BLACK);
        btnClear = createStyledButton(" Effacer", new Color(211, 211, 211), Color.BLACK);
        btnActualiser = createStyledButton(" Actualiser", new Color(221, 160, 221), Color.BLACK);
        
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnActualiser);
        
        // Table des médecins
        String[] columns = {"ID", "Nom", "Spécialité", "Téléphone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        medecinTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        medecinTable.setRowSorter(sorter);
        
        // Table en noir sur blanc
        medecinTable.setForeground(Color.BLACK);
        medecinTable.setBackground(Color.WHITE);
        medecinTable.setGridColor(Color.LIGHT_GRAY);
        medecinTable.setSelectionBackground(new Color(144, 238, 144));
        medecinTable.setSelectionForeground(Color.BLACK);
        
        medecinTable.setRowHeight(30);
        medecinTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        medecinTable.getTableHeader().setForeground(Color.BLACK);
        medecinTable.getTableHeader().setBackground(new Color(240, 240, 240));
        
        JScrollPane scrollPane = new JScrollPane(medecinTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des Médecins"));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Panel de statistiques
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(Color.WHITE);
        lblTotalMedecins = new JLabel("Total médecins: 0");
        lblTotalMedecins.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalMedecins.setForeground(Color.BLACK);
        statsPanel.add(lblTotalMedecins);
        
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
        
        setupListeners();
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField(30);
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
    
    private void setupListeners() {
        btnAjouter.addActionListener(e -> ajouterMedecin());
        btnModifier.addActionListener(e -> modifierMedecin());
        btnSupprimer.addActionListener(e -> supprimerMedecin());
        btnClear.addActionListener(e -> clearForm());
        btnActualiser.addActionListener(e -> loadMedecins());
        
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });
        
        medecinTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectionnerMedecin();
            }
        });
    }
    
    private void loadMedecins() {
        SwingWorker<List<Medecin>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Medecin> doInBackground() throws Exception {
                return medecinDAO.getAllMedecins();
            }
            
            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    List<Medecin> medecins = get();
                    
                    for (Medecin medecin : medecins) {
                        Object[] row = {
                            medecin.getIdMedecin(),
                            medecin.getNom(),
                            medecin.getSpecialite(),
                            medecin.getTelephone()
                        };
                        tableModel.addRow(row);
                    }
                    
                    lblTotalMedecins.setText("Total médecins: " + medecins.size());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MedecinUI.this,
                        "Erreur lors du chargement des médecins: " + e.getMessage(),
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
    
    private void ajouterMedecin() {
        if (!validateForm()) {
            return;
        }
        
        try {
            Medecin medecin = new Medecin();
            medecin.setNom(txtNom.getText().trim());
            medecin.setSpecialite(txtSpecialite.getText().trim());
            medecin.setTelephone(txtTelephone.getText().trim());
            
            medecinDAO.addMedecin(medecin);
            
            JOptionPane.showMessageDialog(this,
                "Médecin ajouté avec succès!\nID Médecin: " + medecin.getIdMedecin(),
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            
            loadMedecins();
            clearForm();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ajout: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void selectionnerMedecin() {
        int selectedRow = medecinTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = medecinTable.convertRowIndexToModel(selectedRow);
            
            txtNom.setText(tableModel.getValueAt(modelRow, 1).toString());
            txtSpecialite.setText(tableModel.getValueAt(modelRow, 2).toString());
            txtTelephone.setText(tableModel.getValueAt(modelRow, 3).toString());
        }
    }
    
    private void modifierMedecin() {
        int selectedRow = medecinTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un médecin à modifier",
                "Avertissement",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateForm()) {
            return;
        }
        
        try {
            int modelRow = medecinTable.convertRowIndexToModel(selectedRow);
            int idMedecin = (int) tableModel.getValueAt(modelRow, 0);
            
            Medecin medecin = medecinDAO.getMedecin(idMedecin);
            if (medecin != null) {
                medecin.setNom(txtNom.getText().trim());
                medecin.setSpecialite(txtSpecialite.getText().trim());
                medecin.setTelephone(txtTelephone.getText().trim());
                
                medecinDAO.updateMedecin(medecin);
                
                JOptionPane.showMessageDialog(this,
                    "Médecin modifié avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadMedecins();
            }
        } catch (UnsupportedOperationException e) {
            // Si en mode réseau et opération non supportée
            handleUnsupportedOperation();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la modification: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerMedecin() {
        int selectedRow = medecinTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un médecin à supprimer",
                "Avertissement",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = medecinTable.convertRowIndexToModel(selectedRow);
        int idMedecin = (int) tableModel.getValueAt(modelRow, 0);
        String nomMedecin = tableModel.getValueAt(modelRow, 1).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer le médecin:\n" +
            nomMedecin + " (ID: " + idMedecin + ")?\n\n" +
            "Cette action supprimera également tous ses\n" +
            "rendez-vous et traitements associés.",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                medecinDAO.deleteMedecin(idMedecin);
                
                JOptionPane.showMessageDialog(this,
                    "Médecin supprimé avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadMedecins();
                clearForm();
                
            } catch (UnsupportedOperationException e) {
                // Si en mode réseau et opération non supportée
                handleUnsupportedOperation();
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
        
        if (txtTelephone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Le téléphone est obligatoire",
                "Validation",
                JOptionPane.WARNING_MESSAGE);
            txtTelephone.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        txtNom.setText("");
        txtSpecialite.setText("");
        txtTelephone.setText("");
        medecinTable.clearSelection();
    }
}