import ui.MainMenu;
import dao.DatabaseConnection;
import client.NetworkDAO;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainApp {

    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> showModeSelection());
    }

    private static void showModeSelection() {
        String[] options = { "🔗 Mode Réseau", "💻 Mode Local" };
        int choice = JOptionPane.showOptionDialog(null,
                "Choisissez le mode de fonctionnement :",
                "Medical Cabinet - Choix du Mode",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            startNetworkMode();
        } else if (choice == 1) {
            startLocalMode();
        } else {
            System.exit(0);
        }
    }

    private static void startNetworkMode() {
        NetworkDAO networkDAO = new NetworkDAO();
        System.out.println("⏳ Tentative de connexion au serveur...");

        if (networkDAO.testConnection()) {
            System.out.println("✅ Connecté au serveur en mode réseau");

            MainMenu mainMenu = new MainMenu();
            mainMenu.setNetworkDAO(networkDAO);
            mainMenu.setTitle("Cabinet Médical [Mode Réseau]");
            mainMenu.setVisible(true);

            mainMenu.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("👋 Fermeture de l'application réseau");
                }
            });
        } else {
            int retry = JOptionPane.showConfirmDialog(null,
                    "Impossible de se connecter au serveur.\nVoulez-vous réessayer ?",
                    "Erreur de Connexion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE);

            if (retry == JOptionPane.YES_OPTION) {
                startNetworkMode();
            } else {
                showModeSelection();
            }
        }
    }

    private static void startLocalMode() {
        System.out.println("⏳ Démarrage en mode local...");

        if (testDatabaseConnection()) {
            System.out.println("✅ Base de données locale connectée");
            MainMenu mainMenu = new MainMenu();
            mainMenu.setTitle("Cabinet Médical [Mode Local]");
            mainMenu.setVisible(true);
        } else {
            int choice = JOptionPane.showConfirmDialog(null,
                    "Erreur : Impossible de se connecter à la base de données locale.\nVoulez-vous essayer le mode réseau ?",
                    "Erreur Fatale",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                startNetworkMode();
            } else {
                System.exit(1);
            }
        }
    }

    private static boolean testDatabaseConnection() {
        try {
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            if (dbConnection.getConnection() == null || dbConnection.getConnection().isClosed()) {
                return false;
            }
            dbConnection.getConnection().createStatement().executeQuery("SELECT 1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
