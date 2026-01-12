package dao.interfaces;
import models.RendezVous;
import models.Patient;
import models.Medecin;
import java.util.List;
public interface RendezVousDAO {
    void addRendezVous(RendezVous rendezVous);       
    RendezVous getRendezVous(int id);                 
    List<RendezVous> getAllRendezVous();              
    void updateRendezVous(RendezVous rendezVous);    
    void deleteRendezVous(int id);                    
    List<RendezVous> getAppointmentsByPatient(Patient patient);  
    List<RendezVous> getAppointmentsByMedecin(Medecin medecin); 
    List<RendezVous> getAppointmentsByPatientId(int patientId);
    List<RendezVous> getAppointmentsByMedecinId(int medecinId); 
}