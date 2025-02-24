package view;

import controller.QuizController;
import controller.UserController;
import model.Quiz;

import javax.swing.*;
import java.awt.*;

public class QuizUI extends JFrame {
    private QuizController quizController;
    private JLabel questionLabel;
    private JButton[] answerButtons;
    private JLabel scoreLabel;
    
    public QuizUI(UserController userController) {
        quizController = new QuizController(userController);
        setTitle("Financial Literacy Quiz");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        setupUI();
        displayQuestion();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Question Panel
        JPanel questionPanel = new JPanel();
        questionLabel = new JLabel();
        questionPanel.add(questionLabel);
        add(questionPanel, BorderLayout.NORTH);
        
        // Answers Panel
        JPanel answersPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        answerButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new JButton();
            final int index = i;
            answerButtons[i].addActionListener(e -> handleAnswer(index));
            answersPanel.add(answerButtons[i]);
        }
        add(answersPanel, BorderLayout.CENTER);
        
        // Score Panel
        scoreLabel = new JLabel("Score: 0");
        add(scoreLabel, BorderLayout.SOUTH);
    }
    
    private void displayQuestion() {
        if (quizController.hasMoreQuestions()) {
            Quiz.QuizQuestion question = quizController.getCurrentQuestion();
            questionLabel.setText(question.getQuestion());
            String[] options = question.getOptions();
            for (int i = 0; i < options.length; i++) {
                answerButtons[i].setText(options[i]);
            }
        } else {
            showResults();
        }
    }
    
    private void handleAnswer(int selectedAnswer) {
        boolean correct = quizController.answerQuestion(selectedAnswer);
        scoreLabel.setText("Score: " + quizController.getCurrentScore());
        
        if (quizController.hasMoreQuestions()) {
            displayQuestion();
        } else {
            showResults();
        }
    }
    
    private void showResults() {
        JOptionPane.showMessageDialog(this,
            "Quiz completed!\nFinal Score: " + quizController.getCurrentScore(),
            "Quiz Results",
            JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
