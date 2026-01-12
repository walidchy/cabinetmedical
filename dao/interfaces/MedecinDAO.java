package dao.interfaces;
import models.Medecin;
import java.util.List;
public interface MedecinDAO {
    void addMedecin(Medecin medecin);                
    Medecin getMedecin(int id);                      
    List<Medecin> getAllMedecins();                 
    void updateMedecin(Medecin medecin);             
    void deleteMedecin(int id);                      
}