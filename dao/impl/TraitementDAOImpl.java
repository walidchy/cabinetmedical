package dao.impl;

import dao.interfaces.TraitementDAO;
import dao.DatabaseConnection;
import models.Traitement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TraitementDAOImpl implements TraitementDAO {
    private Connection connection;
    
    public TraitementDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    @Override
    public void addTraitement(Traitement traitement) {
        String sql = "INSERT INTO Traitements (description, cout, id_patient, id_medecin) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, traitement.getDescription());
            pstmt.setBigDecimal(2, traitement.getCout());
            pstmt.setInt(3, traitement.getIdPatient());
            pstmt.setInt(4, traitement.getIdMedecin());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        traitement.setIdTraitement(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
            throw new RuntimeException("Erreur lors de l'ajout du traitement", e);
        }
    }
    
    @Override
    public Traitement getTraitement(int id) {
        String sql = "SELECT t.*, p.nom as patient_nom, p.prenom as patient_prenom, " +
                    "m.nom as medecin_nom, m.specialite as medecin_specialite " +
                    "FROM Traitements t " +
                    "JOIN Patients p ON t.id_patient = p.id_patient " +
                    "JOIN Medecins m ON t.id_medecin = m.id_medecin " +
                    "WHERE t.id_traitement = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTraitement(rs);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return null;
    }
    
    @Override
    public List<Traitement> getAllTraitements() {
        List<Traitement> traitements = new ArrayList<>();
        String sql = "SELECT t.*, p.nom as patient_nom, p.prenom as patient_prenom, " +
                    "m.nom as medecin_nom, m.specialite as medecin_specialite " +
                    "FROM Traitements t " +
                    "JOIN Patients p ON t.id_patient = p.id_patient " +
                    "JOIN Medecins m ON t.id_medecin = m.id_medecin " +
                    "ORDER BY t.id_traitement";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                traitements.add(mapResultSetToTraitement(rs));
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        
        return traitements;
    }
    
    @Override
    public List<Traitement> getTreatmentsByPatientId(int patientId) {
        List<Traitement> traitements = new ArrayList<>();
        String sql = "SELECT t.*, p.nom as patient_nom, p.prenom as patient_prenom, " +
                    "m.nom as medecin_nom, m.specialite as medecin_specialite " +
                    "FROM Traitements t " +
                    "JOIN Patients p ON t.id_patient = p.id_patient " +
                    "JOIN Medecins m ON t.id_medecin = m.id_medecin " +
                    "WHERE t.id_patient = ? " +
                    "ORDER BY t.id_traitement";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    traitements.add(mapResultSetToTraitement(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        
        return traitements;
    }
    
    @Override
    public List<Traitement> getTreatmentsByMedecinId(int medecinId) {
        List<Traitement> traitements = new ArrayList<>();
        String sql = "SELECT t.*, p.nom as patient_nom, p.prenom as patient_prenom, " +
                    "m.nom as medecin_nom, m.specialite as medecin_specialite " +
                    "FROM Traitements t " +
                    "JOIN Patients p ON t.id_patient = p.id_patient " +
                    "JOIN Medecins m ON t.id_medecin = m.id_medecin " +
                    "WHERE t.id_medecin = ? " +
                    "ORDER BY t.id_traitement";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, medecinId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    traitements.add(mapResultSetToTraitement(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        
        return traitements;
    }
    
    @Override
    public void updateTraitement(Traitement traitement) {
        String sql = "UPDATE Traitements SET description = ?, cout = ?, id_patient = ?, id_medecin = ? WHERE id_traitement = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, traitement.getDescription());
            pstmt.setBigDecimal(2, traitement.getCout());
            pstmt.setInt(3, traitement.getIdPatient());
            pstmt.setInt(4, traitement.getIdMedecin());
            pstmt.setInt(5, traitement.getIdTraitement());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
            throw new RuntimeException("Erreur lors de la mise à jour du traitement", e);
        }
    }
    
    @Override
    public void deleteTraitement(int id) {
        String sql = "DELETE FROM Traitements WHERE id_traitement = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
            throw new RuntimeException("Erreur lors de la suppression du traitement", e);
        }
    }
    
    private Traitement mapResultSetToTraitement(ResultSet rs) throws SQLException {
        Traitement traitement = new Traitement();
        traitement.setIdTraitement(rs.getInt("id_traitement"));
        traitement.setDescription(rs.getString("description"));
        traitement.setCout(rs.getBigDecimal("cout"));
        traitement.setIdPatient(rs.getInt("id_patient"));
        traitement.setIdMedecin(rs.getInt("id_medecin"));
        traitement.setPatientNom(rs.getString("patient_nom") + " " + rs.getString("patient_prenom"));
        traitement.setMedecinNom(rs.getString("medecin_nom") + " (" + rs.getString("medecin_specialite") + ")");
        return traitement;
    }
    
    private void handleSQLException(SQLException e) {
        System.err.println("SQL Error: " + e.getMessage());
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Error Code: " + e.getErrorCode());
    }
}