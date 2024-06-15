import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class QuizServer {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Quiz Server started. Waiting for connections...");
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    System.out.println("Client connected.");
                    // Sample quiz questions in the format: "question;option1;option2;option3;option4;correctOptionIndex"
                    String[] quizData = {
                            "What is the capital of France?;Paris;London;Berlin;Madrid;0",
                            "What is 2 + 2?;3;4;5;6;1",
                            "What is the largest planet in our solar system?;Earth;Mars;Jupiter;Saturn;2",
                            "Which language is primarily used for Android development?;Python;Java;Swift;Kotlin;3"
                    };
                    for (String question : quizData) {
                        out.println(question);
                    }
                } catch (IOException e) {
                    System.out.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}
