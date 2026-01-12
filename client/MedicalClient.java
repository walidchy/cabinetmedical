package client;

import java.io.*;
import java.net.*;
import common.*;

public class MedicalClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5555;
    private static final int CONNECTION_TIMEOUT = 5000; // 5 seconds

    // One connection per request - SIMPLE and RELIABLE
    public Response sendRequest(Request request) {
        Socket socket = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;

        try {
            // 1. Create new connection
            socket = new Socket();
            socket.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT), CONNECTION_TIMEOUT);

            // 2. Create streams in CORRECT ORDER
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush(); // CRITICAL: Send header

            input = new ObjectInputStream(socket.getInputStream());

            // 3. Send request
            output.writeObject(request);
            output.flush();

            // 4. Receive response
            Object response = input.readObject();

            if (response instanceof Response) {
                return (Response) response;
            } else {
                throw new IOException("Invalid response type: " + response.getClass());
            }

        } catch (ConnectException e) {
            return createErrorResponse("Serveur non disponible");
        } catch (SocketTimeoutException e) {
            return createErrorResponse("Timeout de connexion");
        } catch (IOException | ClassNotFoundException e) {
            return createErrorResponse("Erreur communication: " + e.getMessage());
        } finally {
            // 5. ALWAYS close resources
            closeResources(socket, output, input);
        }
    }

    // Simple connection test
    public boolean testConnection() {
        Request ping = new Request("PING", null);
        Response response = sendRequest(ping);
        return response.isSuccess() && "PONG".equals(response.getData());
    }

    private Response createErrorResponse(String message) {
        Response error = new Response();
        error.setSuccess(false);
        error.setErrorMessage(message);
        return error;
    }

    private void closeResources(Socket socket, ObjectOutputStream output, ObjectInputStream input) {
        try {
            if (output != null)
                output.close();
        } catch (IOException e) {
            // Ignore
        }

        try {
            if (input != null)
                input.close();
        } catch (IOException e) {
            // Ignore
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }
}
