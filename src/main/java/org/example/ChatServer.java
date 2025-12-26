package org.example;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class ChatServer {
    private static final int PORT = 8080;
    private static final Map<String, ClientHandler> clientHandlers = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        System.out.println("Starting Mentor Chat Server on port " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler newClient = new ClientHandler(clientSocket);
                newClient.start();
            }
        } catch (IOException e) {
            System.err.println("Server critical error: " + e.getMessage());
        }
    }
    public static void sendPrivateMessage(String senderName, String recipientName, String message) {
        String formattedMessage = "[" + senderName + "]: " + message;
        System.out.println("Sending to " + recipientName + ": " + formattedMessage);
        ClientHandler recipient = clientHandlers.get(recipientName);
        if (recipient != null) {
            recipient.sendMessage(formattedMessage);
        } else {
            System.out.println("Recipient not found: " + recipientName);
        }
    }
    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private SocketIOUtils ioUtils;
        private String clientName;
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        @Override
        public void run() {
            try {
                ioUtils = new SocketIOUtils(clientSocket);
                clientName = ioUtils.receiveMessage();
                if (clientName == null) return;
                clientHandlers.put(clientName, this);
                System.out.println( clientName + " has joined the chat.");
                String clientMessage;
                while ((clientMessage = ioUtils.receiveMessage()) != null) {
                    String[] parts = clientMessage.split(":", 2);
                    if (parts.length == 2) {
                        String recipientName = parts[0];
                        String message = parts[1];
                        ChatServer.sendPrivateMessage(clientName, recipientName, message);
                    }
                }
            } catch (IOException e) {
                System.out.println(" Client disconnected or error: " + clientName + " (" + e.getMessage() + ")");
            } finally {
                if (clientName != null) {
                    clientHandlers.remove(clientName);
                    System.out.println(clientName + " has left.");
                }
                if (ioUtils != null) {
                    ioUtils.close();
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {

                }
            }
        }
        public void sendMessage(String message) {
            ioUtils.sendMessage(message);
        }
    }
}
