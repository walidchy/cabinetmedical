package dao.interfaces;
import models.Patient;
import java.util.List;

public interface PatientDAO {
    void addPatient(Patient patient);
    void updatePatient(Patient patient);
    void deletePatient(int id);
    Patient getPatient(int id);
    List<Patient> getAllPatients();
    List<Patient> searchPatientsByName(String name);
}