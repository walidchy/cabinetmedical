package server;

import java.io.*;
import java.net.*;
import common.*;
import dao.impl.*;
import models.*;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private final MedicalService medicalService;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.medicalService = new MedicalService();
    }

    @Override
    public void run() {
        System.out.println("📡 ClientHandler démarré pour " +
                clientSocket.getInetAddress().getHostAddress());

        try {
            // IMPORTANT: Output stream MUST be created before input stream
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush(); // Send header immediately

            input = new ObjectInputStream(clientSocket.getInputStream());

            // Keep connection alive and process requests
            while (clientSocket.isConnected() && !clientSocket.isClosed()) {
                try {
                    // Read request from client
                    Request request = (Request) input.readObject();
                    System.out.println("📨 Requête reçue: " + request.getOperation());

                    // Process request
                    Response response = processRequest(request);

                    // Send response
                    output.writeObject(response);
                    output.flush();
                    System.out.println("📤 Réponse envoyée");

                } catch (EOFException e) {
                    // Client closed connection normally
                    System.out.println("👋 Client déconnecté normalement");
                    break;
                } catch (SocketException e) {
                    // Connection lost
                    System.out.println("🔌 Connexion perdue: " + e.getMessage());
                    break;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Erreur ClientHandler: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private Response processRequest(Request request) {
        Response response = new Response();

        try {
            switch (request.getOperation()) {
                // PATIENTS
                case "ADD_PATIENT":
                    Patient patient = (Patient) request.getData();
                    medicalService.addPatient(patient);
                    response.setSuccess(true);
                    response.setData(patient);
                    System.out.println("   → Patient ajouté: ID " + patient.getIdPatient());
                    break;

                case "GET_ALL_PATIENTS":
                    List<Patient> patients = medicalService.getAllPatients();
                    response.setSuccess(true);
                    response.setData(patients);
                    System.out.println("   → " + patients.size() + " patients envoyés");
                    break;

                case "UPDATE_PATIENT":
                    Patient patientToUpdate = (Patient) request.getData();
                    medicalService.updatePatient(patientToUpdate);
                    response.setSuccess(true);
                    System.out.println("   → Patient modifié: ID " + patientToUpdate.getIdPatient());
                    break;

                case "DELETE_PATIENT":
                    int patientId = (Integer) request.getData();
                    medicalService.deletePatient(patientId);
                    response.setSuccess(true);
                    System.out.println("   → Patient supprimé: ID " + patientId);
                    break;

                case "SEARCH_PATIENTS":
                    String searchTerm = (String) request.getData();
                    List<Patient> searchResults = medicalService.searchPatients(searchTerm);
                    response.setSuccess(true);
                    response.setData(searchResults);
                    System.out.println("   → Recherche: " + searchResults.size() + " résultats");
                    break;

                // MÉDECINS
                case "ADD_MEDECIN":
                    Medecin medecin = (Medecin) request.getData();
                    medicalService.addMedecin(medecin);
                    response.setSuccess(true);
                    response.setData(medecin);
                    System.out.println("   → Médecin ajouté: ID " + medecin.getIdMedecin());
                    break;

                case "GET_ALL_MEDECINS":
                    List<Medecin> medecins = medicalService.getAllMedecins();
                    response.setSuccess(true);
                    response.setData(medecins);
                    System.out.println("   → " + medecins.size() + " médecins envoyés");
                    break;

                case "UPDATE_MEDECIN":
                    Medecin medecinToUpdate = (Medecin) request.getData();
                    medicalService.updateMedecin(medecinToUpdate);
                    response.setSuccess(true);
                    System.out.println("   → Médecin modifié: ID " + medecinToUpdate.getIdMedecin());
                    break;

                case "DELETE_MEDECIN":
                    int medecinId = (Integer) request.getData();
                    medicalService.deleteMedecin(medecinId);
                    response.setSuccess(true);
                    System.out.println("   → Médecin supprimé: ID " + medecinId);
                    break;

                // RENDEZ-VOUS
                case "ADD_RENDEZVOUS":
                    RendezVous rdv = (RendezVous) request.getData();
                    medicalService.addRendezVous(rdv);
                    response.setSuccess(true);
                    response.setData(rdv);
                    System.out.println("   → Rendez-vous ajouté: ID " + rdv.getIdRdv());
                    break;

                case "GET_ALL_RENDEZVOUS":
                    List<RendezVous> rendezVous = medicalService.getAllRendezVous();
                    response.setSuccess(true);
                    response.setData(rendezVous);
                    System.out.println("   → " + rendezVous.size() + " RDV envoyés");
                    break;

                case "GET_RENDEZVOUS_BY_PATIENT":
                    int patId = (Integer) request.getData();
                    List<RendezVous> rdvByPatient = medicalService.getRendezVousByPatient(patId);
                    response.setSuccess(true);
                    response.setData(rdvByPatient);
                    System.out.println("   → RDV patient " + patId + ": " + rdvByPatient.size());
                    break;

                case "DELETE_RENDEZVOUS":
                    int rdvId = (Integer) request.getData();
                    medicalService.deleteRendezVous(rdvId);
                    response.setSuccess(true);
                    System.out.println("   → RDV supprimé: ID " + rdvId);
                    break;

                // TRAITEMENTS
                case "ADD_TRAITEMENT":
                    Traitement traitement = (Traitement) request.getData();
                    medicalService.addTraitement(traitement);
                    response.setSuccess(true);
                    response.setData(traitement);
                    System.out.println("   → Traitement ajouté: ID " + traitement.getIdTraitement());
                    break;

                case "GET_ALL_TRAITEMENTS":
                    List<Traitement> traitements = medicalService.getAllTraitements();
                    response.setSuccess(true);
                    response.setData(traitements);
                    System.out.println("   → " + traitements.size() + " traitements envoyés");
                    break;

                case "GET_TRAITEMENTS_BY_PATIENT":
                    int patId2 = (Integer) request.getData();
                    List<Traitement> treatmentsByPatient = medicalService.getTraitementsByPatient(patId2);
                    response.setSuccess(true);
                    response.setData(treatmentsByPatient);
                    System.out.println("   → Traitements patient " + patId2 + ": " + treatmentsByPatient.size());
                    break;

                case "DELETE_TRAITEMENT":
                    int traitementId = (Integer) request.getData();
                    medicalService.deleteTraitement(traitementId);
                    response.setSuccess(true);
                    System.out.println("   → Traitement supprimé: ID " + traitementId);
                    break;

                // TEST
                case "PING":
                    response.setSuccess(true);
                    response.setData("PONG");
                    System.out.println("   → Ping/Pong");
                    break;

                default:
                    response.setSuccess(false);
                    response.setErrorMessage("Opération non supportée: " + request.getOperation());
                    System.err.println("❌ Opération non supportée: " + request.getOperation());
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage("Erreur serveur: " + e.getMessage());
            System.err.println("❌ Erreur traitement: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    private void closeResources() {
        try {
            if (output != null)
                output.close();
            if (input != null)
                input.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("❌ Erreur fermeture ressources: " + e.getMessage());
        }
    }
}
