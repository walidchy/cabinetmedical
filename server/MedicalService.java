package server;

import dao.impl.*;
import models.*;
import java.util.List;

public class MedicalService {
    private PatientDAOImpl patientDAO;
    private MedecinDAOImpl medecinDAO;
    private RendezVousDAOImpl rendezVousDAO;
    private TraitementDAOImpl traitementDAO;

    public MedicalService() {
        this.patientDAO = new PatientDAOImpl();
        this.medecinDAO = new MedecinDAOImpl();
        this.rendezVousDAO = new RendezVousDAOImpl();
        this.traitementDAO = new TraitementDAOImpl();
    }

    // ========== PATIENTS ==========
    public void addPatient(Patient patient) {
        patientDAO.addPatient(patient);
    }

    public Patient getPatient(int id) {
        return patientDAO.getPatient(id);
    }

    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    public List<Patient> searchPatients(String searchTerm) {
        return patientDAO.searchPatientsByName(searchTerm);
    }

    public void updatePatient(Patient patient) {
        patientDAO.updatePatient(patient);
    }

    public void deletePatient(int id) {
        patientDAO.deletePatient(id);
    }

    // ========== MÉDECINS ==========
    public void addMedecin(Medecin medecin) {
        medecinDAO.addMedecin(medecin);
    }

    public Medecin getMedecin(int id) {
        return medecinDAO.getMedecin(id);
    }

    public List<Medecin> getAllMedecins() {
        return medecinDAO.getAllMedecins();
    }

    public void updateMedecin(Medecin medecin) {
        medecinDAO.updateMedecin(medecin);
    }

    public void deleteMedecin(int id) {
        medecinDAO.deleteMedecin(id);
    }

    // ========== RENDEZ-VOUS ==========
    public RendezVous addRendezVous(RendezVous rdv) {
        rendezVousDAO.addRendezVous(rdv);
        return rdv;
    }

    public RendezVous getRendezVous(int id) {
        return rendezVousDAO.getRendezVous(id);
    }

    public List<RendezVous> getAllRendezVous() {
        return rendezVousDAO.getAllRendezVous();
    }

    public List<RendezVous> getRendezVousByPatient(int patientId) {
        return rendezVousDAO.getAppointmentsByPatientId(patientId);
    }

    public List<RendezVous> getRendezVousByMedecin(int medecinId) {
        return rendezVousDAO.getAppointmentsByMedecinId(medecinId);
    }

    public void updateRendezVous(RendezVous rdv) {
        rendezVousDAO.updateRendezVous(rdv);
    }

    public void deleteRendezVous(int id) {
        rendezVousDAO.deleteRendezVous(id);
    }

    // ========== TRAITEMENTS ==========
    public Traitement addTraitement(Traitement traitement) {
        traitementDAO.addTraitement(traitement);
        return traitement;
    }

    public Traitement getTraitement(int id) {
        return traitementDAO.getTraitement(id);
    }

    public List<Traitement> getAllTraitements() {
        return traitementDAO.getAllTraitements();
    }

    public List<Traitement> getTraitementsByPatient(int patientId) {
        return traitementDAO.getTreatmentsByPatientId(patientId);
    }

    public List<Traitement> getTraitementsByMedecin(int medecinId) {
        return traitementDAO.getTreatmentsByMedecinId(medecinId);
    }

    public void updateTraitement(Traitement traitement) {
        traitementDAO.updateTraitement(traitement);
    }

    public void deleteTraitement(int id) {
        traitementDAO.deleteTraitement(id);
    }
}