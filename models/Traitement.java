package models;
import java.math.BigDecimal;
import java.io.Serializable;

public class Traitement implements Serializable {
    private static final long serialVersionUID = 103L; // UPDATED ID
    private int idTraitement;
    private String description;
    private BigDecimal cout;
    private int idPatient;
    private int idMedecin;
    private String patientNom; 
    private String medecinNom; 
    public Traitement() {}
    public Traitement(String description, BigDecimal cout, int idPatient, int idMedecin) {
        this.description = description;
        this.cout = cout;
        this.idPatient = idPatient;
        this.idMedecin = idMedecin;
    }
    public int getIdTraitement() { return idTraitement; }
    public void setIdTraitement(int idTraitement) { this.idTraitement = idTraitement; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getCout() { return cout; }
    public void setCout(BigDecimal cout) { this.cout = cout; }
    public int getIdPatient() { return idPatient; }
    public void setIdPatient(int idPatient) { this.idPatient = idPatient; }
    public int getIdMedecin() { return idMedecin; }
    public void setIdMedecin(int idMedecin) { this.idMedecin = idMedecin; }
    public String getPatientNom() { return patientNom; }
    public void setPatientNom(String patientNom) { this.patientNom = patientNom; }
    public String getMedecinNom() { return medecinNom; }
    public void setMedecinNom(String medecinNom) { this.medecinNom = medecinNom; }
}