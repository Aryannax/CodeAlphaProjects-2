import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class QuizClient extends JFrame {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private ArrayList<String> questions;
    private ArrayList<ArrayList<String>> options;
    private ArrayList<Integer> correctAnswers;
    private int currentQuestionIndex;
    private int score;

    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private JButton nextButton;
    private ButtonGroup optionsGroup;

    public QuizClient() {
        questions = new ArrayList<>();
        options = new ArrayList<>();
        correctAnswers = new ArrayList<>();
        currentQuestionIndex = 0;
        score = 0;

        // Initialize GUI components
        questionLabel = new JLabel("Question will appear here");
        optionButtons = new JRadioButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
        }
        nextButton = new JButton("Next");
        optionsGroup = new ButtonGroup();
        for (JRadioButton optionButton : optionButtons) {
            optionsGroup.add(optionButton);
        }

        // Layout setup
        setLayout(new BorderLayout());
        add(questionLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1));
        for (JRadioButton optionButton : optionButtons) {
            optionsPanel.add(optionButton);
        }
        add(optionsPanel, BorderLayout.CENTER);
        add(nextButton, BorderLayout.SOUTH);

        nextButton.addActionListener(new NextButtonListener());

        setTitle("Online Quiz");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadQuestionsFromServer();
        displayCurrentQuestion();
    }

    private void loadQuestionsFromServer() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(";");
                questions.add(parts[0]);
                ArrayList<String> optionList = new ArrayList<>();
                for (int i = 1; i <= 4; i++) {
                    optionList.add(parts[i]);
                }
                options.add(optionList);
                correctAnswers.add(Integer.parseInt(parts[5]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            questionLabel.setText(questions.get(currentQuestionIndex));
            ArrayList<String> currentOptions = options.get(currentQuestionIndex);
            for (int i = 0; i < 4; i++) {
                optionButtons[i].setText(currentOptions.get(i));
            }
            optionsGroup.clearSelection();
        } else {
            questionLabel.setText("Quiz Over! Your score: " + score);
            for (JRadioButton optionButton : optionButtons) {
                optionButton.setVisible(false);
            }
            nextButton.setVisible(false);
        }
    }

    private class NextButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedOption = -1;
            for (int i = 0; i < 4; i++) {
                if (optionButtons[i].isSelected()) {
                    selectedOption = i;
                    break;
                }
            }
            if (selectedOption == correctAnswers.get(currentQuestionIndex)) {
                score++;
            }
            currentQuestionIndex++;
            displayCurrentQuestion();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizClient().setVisible(true));
    }
}
