package dao.impl;

import dao.interfaces.PatientDAO;
import dao.DatabaseConnection;
import models.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAOImpl implements PatientDAO {
    private Connection connection;
    
    public PatientDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    @Override
    public void addPatient(Patient patient) {
        String sql = "INSERT INTO Patients (nom, prenom, date_naissance, adresse, telephone) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, patient.getNom());
            pstmt.setString(2, patient.getPrenom());
            pstmt.setDate(3, patient.getDateNaissance());
            pstmt.setString(4, patient.getAdresse());
            pstmt.setString(5, patient.getTelephone());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patient.setIdPatient(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
            throw new RuntimeException("Erreur lors de l'ajout du patient", e);
        }
    }
    
    @Override
    public Patient getPatient(int id) {
        String sql = "SELECT * FROM Patients WHERE id_patient = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return null;
    }
    
    @Override
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients ORDER BY nom, prenom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        
        return patients;
    }
    
    @Override
    public List<Patient> searchPatientsByName(String name) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + name + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        
        return patients;
    }
    
    @Override
    public void updatePatient(Patient patient) {
        String sql = "UPDATE Patients SET nom = ?, prenom = ?, date_naissance = ?, adresse = ?, telephone = ? WHERE id_patient = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, patient.getNom());
            pstmt.setString(2, patient.getPrenom());
            pstmt.setDate(3, patient.getDateNaissance());
            pstmt.setString(4, patient.getAdresse());
            pstmt.setString(5, patient.getTelephone());
            pstmt.setInt(6, patient.getIdPatient());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
            throw new RuntimeException("Erreur lors de la mise à jour du patient", e);
        }
    }
    
    @Override
    public void deletePatient(int id) {
        String sql = "DELETE FROM Patients WHERE id_patient = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
            throw new RuntimeException("Erreur lors de la suppression du patient", e);
        }
    }
    
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setIdPatient(rs.getInt("id_patient"));
        patient.setNom(rs.getString("nom"));
        patient.setPrenom(rs.getString("prenom"));
        patient.setDateNaissance(rs.getDate("date_naissance"));
        patient.setAdresse(rs.getString("adresse"));
        patient.setTelephone(rs.getString("telephone"));
        return patient;
    }
    
    private void handleSQLException(SQLException e) {
        System.err.println("SQL Error: " + e.getMessage());
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Error Code: " + e.getErrorCode());
    }
}