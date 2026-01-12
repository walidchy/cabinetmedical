package ui;

import javax.swing.*;
import client.NetworkDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    private JButton btnPatients, btnMedecins, btnRendezVous, btnTraitements, btnQuitter;
    private NetworkDAO networkDAO;
    private boolean isNetworkMode = false;

    public MainMenu() {
        setTitle("Cabinet Médical");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    public void setNetworkDAO(NetworkDAO networkDAO) {
        this.networkDAO = networkDAO;
        this.isNetworkMode = true;
        System.out.println("📡 Mode réseau activé dans MainMenu");
    }

    private void openPatientUI() {
        try {
            PatientUI patientUI = new PatientUI();
            if (isNetworkMode && networkDAO != null) {
                patientUI.setPatientDAO(networkDAO);
            }
            patientUI.setVisible(true);
        } catch (Exception e) {
            showError("Erreur PatientUI", e);
        }
    }

    private void openMedecinUI() {
        try {
            MedecinUI medecinUI = new MedecinUI();
            if (isNetworkMode && networkDAO != null) {
                medecinUI.setMedecinDAO(networkDAO);
            }
            medecinUI.setVisible(true);
        } catch (Exception e) {
            showError("Erreur MedecinUI", e);
        }
    }

    private void openRendezVousUI() {
        try {
            RendezVousUI rdvUI = new RendezVousUI();
            if (isNetworkMode && networkDAO != null) {
                rdvUI.setRendezVousDAO(networkDAO);
                rdvUI.setPatientDAO(networkDAO);
                rdvUI.setMedecinDAO(networkDAO);
            }
            rdvUI.setVisible(true);
        } catch (Exception e) {
            showError("Erreur RendezVousUI", e);
        }
    }

    private void openTraitementUI() {
        try {
            TraitementUI traitementUI = new TraitementUI();
            if (isNetworkMode && networkDAO != null) {
                traitementUI.setTraitementDAO(networkDAO);
                traitementUI.setPatientDAO(networkDAO);
                traitementUI.setMedecinDAO(networkDAO);
            }
            traitementUI.setVisible(true);
        } catch (Exception e) {
            showError("Erreur TraitementUI", e);
        }
    }

    private void showError(String title, Exception e) {
        JOptionPane.showMessageDialog(this,
                "Erreur : " + e.getMessage(),
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    private void initUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("CABINET MÉDICAL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel subtitleLabel = new JLabel("Système de Gestion Intégré", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setOpaque(false);

        btnPatients = createButton("Gestion des Patients", new Color(230, 242, 255));
        btnMedecins = createButton("Gestion des Médecins", new Color(230, 255, 230));
        btnRendezVous = createButton("Gestion des Rendez-vous", new Color(255, 242, 230));
        btnTraitements = createButton("Gestion des Traitements", new Color(250, 230, 250));
        btnQuitter = createButton("Quitter", new Color(255, 230, 230));

        buttonPanel.add(btnPatients);
        buttonPanel.add(btnMedecins);
        buttonPanel.add(btnRendezVous);
        buttonPanel.add(btnTraitements);
        buttonPanel.add(btnQuitter);

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(subtitleLabel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("© 2025 Cabinet Médical CW", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);

        btnPatients.addActionListener(e -> openPatientUI());
        btnMedecins.addActionListener(e -> openMedecinUI());
        btnRendezVous.addActionListener(e -> openRendezVousUI());
        btnTraitements.addActionListener(e -> openTraitementUI());
        btnQuitter.addActionListener(e -> confirmQuit());
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void confirmQuit() {
        int response = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir quitter l'application?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
