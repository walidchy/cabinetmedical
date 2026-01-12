package dao.impl;
import dao.interfaces.MedecinDAO;
import dao.DatabaseConnection;
import models.Medecin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinDAOImpl implements MedecinDAO {
    private Connection connection;
    
    public MedecinDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    @Override
    public void addMedecin(Medecin medecin) {
        String sql = "INSERT INTO Medecins (nom, specialite, telephone) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, medecin.getNom());
            pstmt.setString(2, medecin.getSpecialite());
            pstmt.setString(3, medecin.getTelephone());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        medecin.setIdMedecin(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
            throw new RuntimeException("Erreur lors de l'ajout du médecin", e);
        }
    }
    
    @Override
    public Medecin getMedecin(int id) {
        String sql = "SELECT * FROM Medecins WHERE id_medecin = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedecin(rs);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return null;
    }
    
    @Override
    public List<Medecin> getAllMedecins() {
        List<Medecin> medecins = new ArrayList<>();
        String sql = "SELECT * FROM Medecins ORDER BY nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                medecins.add(mapResultSetToMedecin(rs));
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        
        return medecins;
    }
    
    @Override
    public void updateMedecin(Medecin medecin) {
        String sql = "UPDATE Medecins SET nom = ?, specialite = ?, telephone = ? WHERE id_medecin = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, medecin.getNom());
            pstmt.setString(2, medecin.getSpecialite());
            pstmt.setString(3, medecin.getTelephone());
            pstmt.setInt(4, medecin.getIdMedecin());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
            throw new RuntimeException("Erreur lors de la mise à jour du médecin", e);
        }
    }
    
    @Override
    public void deleteMedecin(int id) {
        String sql = "DELETE FROM Medecins WHERE id_medecin = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
            throw new RuntimeException("Erreur lors de la suppression du médecin", e);
        }
    }
    
    private Medecin mapResultSetToMedecin(ResultSet rs) throws SQLException {
        Medecin medecin = new Medecin();
        medecin.setIdMedecin(rs.getInt("id_medecin"));
        medecin.setNom(rs.getString("nom"));
        medecin.setSpecialite(rs.getString("specialite"));
        medecin.setTelephone(rs.getString("telephone"));
        return medecin;
    }
    
    private void handleSQLException(SQLException e) {
        System.err.println("SQL Error: " + e.getMessage());
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Error Code: " + e.getErrorCode());
    }
}