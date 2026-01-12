package models;
import java.sql.Timestamp;
import java.io.Serializable;

public class RendezVous implements Serializable {
    private static final long serialVersionUID = 102L; // UPDATED ID
    private int idRdv;
    private Timestamp date;
    private int idPatient;
    private int idMedecin;
    private String patientNom; 
    private String medecinNom; 
    public RendezVous() {}
    public RendezVous(Timestamp date, int idPatient, int idMedecin) {
        this.date = date;
        this.idPatient = idPatient;
        this.idMedecin = idMedecin;
    }
    public int getIdRdv() { return idRdv; }
    public void setIdRdv(int idRdv) { this.idRdv = idRdv; }
    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }
    public int getIdPatient() { return idPatient; }
    public void setIdPatient(int idPatient) { this.idPatient = idPatient; }
    public int getIdMedecin() { return idMedecin; }
    public void setIdMedecin(int idMedecin) { this.idMedecin = idMedecin; }
    public String getPatientNom() { return patientNom; }
    public void setPatientNom(String patientNom) { this.patientNom = patientNom; }
    public String getMedecinNom() { return medecinNom; }
    public void setMedecinNom(String medecinNom) { this.medecinNom = medecinNom; }
}