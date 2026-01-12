package models;
import java.io.Serializable;

public class Medecin implements Serializable {
    private static final long serialVersionUID = 101L; // UPDATED ID
    private int idMedecin;
    private String nom;
    private String specialite;
    private String telephone;
    public Medecin() {}
    public Medecin(String nom, String specialite, String telephone) {
        this.nom = nom;
        this.specialite = specialite;
        this.telephone = telephone;
    }
    public int getIdMedecin() { return idMedecin; }
    public void setIdMedecin(int idMedecin) { this.idMedecin = idMedecin; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String toString() {
        return "Dr. " + nom + " - " + specialite + " (" + telephone + ")";
    }
}