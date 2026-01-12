package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MedicalServer {
    private static final int PORT = 5555;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean running;
    
    public MedicalServer() {
        this.threadPool = Executors.newCachedThreadPool();
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("✅ Serveur médical démarré sur le port " + PORT);
            
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("🔗 Nouveau client connecté: " + 
                    clientSocket.getInetAddress().getHostAddress());
                
                ClientHandler handler = new ClientHandler(clientSocket);
                threadPool.execute(handler);
            }
            
        } catch (IOException e) {
            System.err.println("❌ Erreur serveur: " + e.getMessage());
        } finally {
            stop();
        }
    }
    
    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
            threadPool.shutdown();
            System.out.println("🛑 Serveur arrêté.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        MedicalServer server = new MedicalServer();
        server.start();
    }
}