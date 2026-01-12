package dao.impl;

import dao.interfaces.RendezVousDAO;
import dao.DatabaseConnection;
import models.RendezVous;
import models.Patient;
import models.Medecin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RendezVousDAOImpl implements RendezVousDAO {
    private Connection connection;
    
    public RendezVousDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    @Override
    public void addRendezVous(RendezVous rendezVous) {
        String sql = "INSERT INTO RendezVous (date, id_patient, id_medecin) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setTimestamp(1, rendezVous.getDate());
            pstmt.setInt(2, rendezVous.getIdPatient());
            pstmt.setInt(3, rendezVous.getIdMedecin());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        rendezVous.setIdRdv(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
            throw new RuntimeException("Erreur lors de l'ajout du rendez-vous", e);
        }
    }
    
    @Override
    public RendezVous getRendezVous(int id) {
        String sql = "SELECT r.*, p.nom as patient_nom, p.prenom as patient_prenom, " +
                    "m.nom as medecin_nom, m.specialite as medecin_specialite " +
                    "FROM RendezVous r " +
                    "JOIN Patients p ON r.id_patient = p.id_patient " +
                    "JOIN Medecins m ON r.id_medecin = m.id_medecin " +
                    "WHERE r.id_rdv = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRendezVous(rs);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return null;
    }
    
    @Override
    public List<RendezVous> getAllRendezVous() {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String sql = "SELECT r.*, p.nom as patient_nom, p.prenom as patient_prenom, " +
                    "m.nom as medecin_nom, m.specialite as medecin_specialite " +
                    "FROM RendezVous r " +
                    "JOIN Patients p ON r.id_patient = p.id_patient " +
                    "JOIN Medecins m ON r.id_medecin = m.id_medecin " +
                    "ORDER BY r.date";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rendezVousList.add(mapResultSetToRendezVous(rs));
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        
        return rendezVousList;
    }
    
    @Override
    public List<RendezVous> getAppointmentsByPatient(Patient patient) {
        return getAppointmentsByPatientId(patient.getIdPatient());
    }
    
    @Override
    public List<RendezVous> getAppointmentsByPatientId(int patientId) {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String sql = "SELECT r.*, p.nom as patient_nom, p.prenom as patient_prenom, " +
                    "m.nom as medecin_nom, m.specialite as medecin_specialite " +
                    "FROM RendezVous r " +
                    "JOIN Patients p ON r.id_patient = p.id_patient " +
                    "JOIN Medecins m ON r.id_medecin = m.id_medecin " +
                    "WHERE r.id_patient = ? " +
                    "ORDER BY r.date";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rendezVousList.add(mapResultSetToRendezVous(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        
        return rendezVousList;
    }
    
    @Override
    public List<RendezVous> getAppointmentsByMedecin(Medecin medecin) {
        return getAppointmentsByMedecinId(medecin.getIdMedecin());
    }
    
    @Override
    public List<RendezVous> getAppointmentsByMedecinId(int medecinId) {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String sql = "SELECT r.*, p.nom as patient_nom, p.prenom as patient_prenom, " +
                    "m.nom as medecin_nom, m.specialite as medecin_specialite " +
                    "FROM RendezVous r " +
                    "JOIN Patients p ON r.id_patient = p.id_patient " +
                    "JOIN Medecins m ON r.id_medecin = m.id_medecin " +
                    "WHERE r.id_medecin = ? " +
                    "ORDER BY r.date";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, medecinId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rendezVousList.add(mapResultSetToRendezVous(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        
        return rendezVousList;
    }
    
    @Override
    public void updateRendezVous(RendezVous rendezVous) {
        String sql = "UPDATE RendezVous SET date = ?, id_patient = ?, id_medecin = ? WHERE id_rdv = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, rendezVous.getDate());
            pstmt.setInt(2, rendezVous.getIdPatient());
            pstmt.setInt(3, rendezVous.getIdMedecin());
            pstmt.setInt(4, rendezVous.getIdRdv());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
            throw new RuntimeException("Erreur lors de la mise à jour du rendez-vous", e);
        }
    }
    
    @Override
    public void deleteRendezVous(int id) {
        String sql = "DELETE FROM RendezVous WHERE id_rdv = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
            throw new RuntimeException("Erreur lors de la suppression du rendez-vous", e);
        }
    }
    
    private RendezVous mapResultSetToRendezVous(ResultSet rs) throws SQLException {
        RendezVous rendezVous = new RendezVous();
        rendezVous.setIdRdv(rs.getInt("id_rdv"));
        rendezVous.setDate(rs.getTimestamp("date"));
        rendezVous.setIdPatient(rs.getInt("id_patient"));
        rendezVous.setIdMedecin(rs.getInt("id_medecin"));
        rendezVous.setPatientNom(rs.getString("patient_nom") + " " + rs.getString("patient_prenom"));
        rendezVous.setMedecinNom(rs.getString("medecin_nom") + " (" + rs.getString("medecin_specialite") + ")");
        return rendezVous;
    }
    
    private void handleSQLException(SQLException e) {
        System.err.println("SQL Error: " + e.getMessage());
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Error Code: " + e.getErrorCode());
    }
}