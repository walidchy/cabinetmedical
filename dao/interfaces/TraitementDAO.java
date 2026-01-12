package dao.interfaces;
import models.Traitement;
import java.util.List;
public interface TraitementDAO {
    void addTraitement(Traitement traitement);       
    Traitement getTraitement(int id);                  
    List<Traitement> getAllTraitements();             
    void updateTraitement(Traitement traitement);     
    void deleteTraitement(int id);                    
    List<Traitement> getTreatmentsByPatientId(int patientId);  
    List<Traitement> getTreatmentsByMedecinId(int medecinId);  
}