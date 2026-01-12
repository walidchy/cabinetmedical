package client;

import dao.interfaces.*;
import models.*;
import common.*;
import java.util.List;

public class NetworkDAO implements PatientDAO, MedecinDAO, RendezVousDAO, TraitementDAO {
    private MedicalClient client;
    private boolean autoConnect = true;

    public NetworkDAO() {
        this.client = new MedicalClient();
    }

    public boolean connect() {
        if (client.isConnected()) {
            return true;
        }
        return client.connect();
    }

    public boolean testConnection() {
        return client.testConnection();
    }

    public void disconnect() {
        client.disconnect();
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    // Méthode privée pour envoyer des requêtes avec reconnexion automatique
    private Response sendRequestWithRetry(Request request) {
        // Essayer d'envoyer la requête
        Response response = client.sendRequest(request);

        // Si non connecté, essayer de se reconnecter
        if (!response.isSuccess() && response.getErrorMessage().contains("Non connecté") && autoConnect) {
            System.out.println("🔄 Tentative de reconnexion...");
            if (connect() && testConnection()) {
                // Réessayer la requête
                response = client.sendRequest(request);
            }
        }

        return response;
    }

    // ========== PATIENT DAO ==========
    @Override
    public void addPatient(Patient patient) {
        Request request = new Request("ADD_PATIENT", patient);
        Response response = sendRequestWithRetry(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur ajout patient: " + response.getErrorMessage());
        }

        Patient saved = (Patient) response.getData();
        if (saved != null) {
            patient.setIdPatient(saved.getIdPatient());
        }
    }

    @Override
    public List<Patient> getAllPatients() {
        System.out.println("📊 Demande de tous les patients...");
        Request request = new Request("GET_ALL_PATIENTS", null);
        Response response = sendRequestWithRetry(request);

        if (!response.isSuccess()) {
            System.err.println("❌ Erreur getAllPatients: " + response.getErrorMessage());
            throw new RuntimeException("Erreur réseau: " + response.getErrorMessage());
        }

        Object data = response.getData();
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            System.out.println("✅ " + list.size() + " patients reçus");
            return (List<Patient>) data;
        } else {
            throw new RuntimeException("Type de données inattendu");
        }
    }

    @Override
    public List<Patient> searchPatientsByName(String name) {
        Request request = new Request("SEARCH_PATIENTS", name);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur réseau: " + response.getErrorMessage());
        }

        return (List<Patient>) response.getData();
    }

    @Override
    public void updatePatient(Patient patient) {
        Request request = new Request("UPDATE_PATIENT", patient);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur réseau: " + response.getErrorMessage());
        }
    }

    @Override
    public void deletePatient(int id) {
        Request request = new Request("DELETE_PATIENT", id);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur suppression: " + response.getErrorMessage());
        }
    }

    // ========== MEDECIN DAO IMPLEMENTATION ==========
    @Override
    public void addMedecin(Medecin medecin) {
        Request request = new Request("ADD_MEDECIN", medecin);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur ajout médecin: " + response.getErrorMessage());
        }

        Medecin saved = (Medecin) response.getData();
        if (saved != null) {
            medecin.setIdMedecin(saved.getIdMedecin());
        }
    }

    @Override
    public Medecin getMedecin(int id) {
        throw new UnsupportedOperationException("Non implémenté en réseau");
    }

    @Override
    public List<Medecin> getAllMedecins() {
        System.out.println("🔍 Demande des médecins...");
        Request request = new Request("GET_ALL_MEDECINS", null);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur récupération médecins: " + response.getErrorMessage());
        }

        return (List<Medecin>) response.getData();
    }

    @Override
    public void updateMedecin(Medecin medecin) {
        Request request = new Request("UPDATE_MEDECIN", medecin);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur modification: " + response.getErrorMessage());
        }
    }

    @Override
    public void deleteMedecin(int id) {
        Request request = new Request("DELETE_MEDECIN", id);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur suppression: " + response.getErrorMessage());
        }
    }

    // ========== RENDEZVOUS DAO IMPLEMENTATION ==========
    @Override
    public void addRendezVous(RendezVous rendezVous) {
        Request request = new Request("ADD_RENDEZVOUS", rendezVous);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur ajout RDV: " + response.getErrorMessage());
        }

        RendezVous saved = (RendezVous) response.getData();
        if (saved != null) {
            rendezVous.setIdRdv(saved.getIdRdv());
        }
    }

    @Override
    public RendezVous getRendezVous(int id) {
        throw new UnsupportedOperationException("Non implémenté en réseau");
    }

    @Override
    public List<RendezVous> getAllRendezVous() {
        System.out.println("🔍 Demande des rendez-vous...");
        Request request = new Request("GET_ALL_RENDEZVOUS", null);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur récupération RDV: " + response.getErrorMessage());
        }

        return (List<RendezVous>) response.getData();
    }

    @Override
    public void updateRendezVous(RendezVous rendezVous) {
        throw new UnsupportedOperationException("Non implémenté en réseau");
    }

    @Override
    public void deleteRendezVous(int id) {
        Request request = new Request("DELETE_RENDEZVOUS", id);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur suppression RDV: " + response.getErrorMessage());
        }
    }

    @Override
    public List<RendezVous> getAppointmentsByPatient(Patient patient) {
        return getAppointmentsByPatientId(patient.getIdPatient());
    }

    @Override
    public List<RendezVous> getAppointmentsByPatientId(int patientId) {
        Request request = new Request("GET_RENDEZVOUS_BY_PATIENT", patientId);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur réseau: " + response.getErrorMessage());
        }

        return (List<RendezVous>) response.getData();
    }

    @Override
    public List<RendezVous> getAppointmentsByMedecin(Medecin medecin) {
        return getAppointmentsByMedecinId(medecin.getIdMedecin());
    }

    @Override
    public List<RendezVous> getAppointmentsByMedecinId(int medecinId) {
        Request request = new Request("GET_RENDEZVOUS_BY_MEDECIN", medecinId);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur réseau: " + response.getErrorMessage());
        }

        return (List<RendezVous>) response.getData();
    }

    // ========== IMPLÉMENTATION TRAITEMENTDAO ==========
    @Override
    public void addTraitement(Traitement traitement) {
        Request request = new Request("ADD_TRAITEMENT", traitement);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur réseau: " + response.getErrorMessage());
        }

        Traitement savedTraitement = (Traitement) response.getData();
        if (savedTraitement != null) {
            traitement.setIdTraitement(savedTraitement.getIdTraitement());
        }
    }

    @Override
    public Traitement getTraitement(int id) {
        Request request = new Request("GET_TRAITEMENT", id);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur réseau: " + response.getErrorMessage());
        }

        return (Traitement) response.getData();
    }

    @Override
    public List<Traitement> getAllTraitements() {
        Request request = new Request("GET_ALL_TRAITEMENTS", null);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur réseau: " + response.getErrorMessage());
        }

        return (List<Traitement>) response.getData();
    }

    @Override
    public void updateTraitement(Traitement traitement) {
        Request request = new Request("UPDATE_TRAITEMENT", traitement);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur réseau: " + response.getErrorMessage());
        }
    }

    @Override
    public void deleteTraitement(int id) {
        Request request = new Request("DELETE_TRAITEMENT", id);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur réseau: " + response.getErrorMessage());
        }
    }

    @Override
    public List<Traitement> getTreatmentsByPatientId(int patientId) {
        Request request = new Request("GET_TRAITEMENTS_BY_PATIENT", patientId);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur réseau: " + response.getErrorMessage());
        }

        return (List<Traitement>) response.getData();
    }

    @Override
    public List<Traitement> getTreatmentsByMedecinId(int medecinId) {
        Request request = new Request("GET_TRAITEMENTS_BY_MEDECIN", medecinId);
        Response response = client.sendRequest(request);

        if (!response.isSuccess()) {
            throw new RuntimeException("Erreur réseau: " + response.getErrorMessage());
        }

        return (List<Traitement>) response.getData();
    }
}