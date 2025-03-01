package main.java.view;

import main.java.controller.UserController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizPanel extends JPanel {
    
    private UserController userController;
    private JPanel quizContentPanel;
    private CardLayout cardLayout;
    private JLabel questionLabel;
    private JPanel answerPanel;
    private JLabel resultLabel;
    private JLabel xpLabel;
    private JButton nextButton;
    
    private List<QuizQuestion> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    
    public QuizPanel(UserController userController) {
        this.userController = userController;
        
        initializeQuestions();
        initializeUI();
    }
    
    private void initializeQuestions() {
        questions = new ArrayList<>();
        
        // Add financial literacy questions
        questions.add(new QuizQuestion(
                "What is the best way to build an emergency fund?",
                new String[]{
                        "Save a small amount regularly",
                        "Wait until you have a large sum to deposit",
                        "Use credit cards for emergencies",
                        "Borrow from friends and family"
                },
                0
        ));
        
        questions.add(new QuizQuestion(
                "What is compound interest?",
                new String[]{
                        "Interest calculated on the initial principal only",
                        "Interest calculated on the initial principal and the accumulated interest",
                        "A fixed amount paid at the end of a loan term",
                        "The same as simple interest"
                },
                1
        ));
        
        questions.add(new QuizQuestion(
                "Which of these is typically the most costly form of borrowing?",
                new String[]{
                        "Mortgage loan",
                        "Auto loan",
                        "Credit card debt",
                        "Student loan"
                },
                2
        ));
        
        questions.add(new QuizQuestion(
                "What is a budget?",
                new String[]{
                        "A restriction on your spending",
                        "A plan for how you will spend your money",
                        "A way to track your past spending only",
                        "A type of investment"
                },
                1
        ));
        
        questions.add(new QuizQuestion(
                "Which of these is generally considered a good financial habit?",
                new String[]{
                        "Paying only the minimum on credit cards",
                        "Spending exactly what you earn",
                        "Paying yourself first (saving before spending)",
                        "Taking on debt for non-essential purchases"
                },
                2
        ));
        
        // Randomize questions
        Random random = new Random();
        for (int i = 0; i < questions.size(); i++) {
            int j = random.nextInt(questions.size());
            QuizQuestion temp = questions.get(i);
            questions.set(i, questions.get(j));
            questions.set(j, temp);
        }
    }
    
    private void initializeUI() {
        setBackground(MainUI.DARK_PURPLE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Quiz content with card layout
        cardLayout = new CardLayout();
        quizContentPanel = new JPanel(cardLayout);
        quizContentPanel.setOpaque(false);
        
        // Question panel
        JPanel questionPanel = createQuestionPanel();
        quizContentPanel.add(questionPanel, "question");
        
        // Result panel
        JPanel resultPanel = createResultPanel();
        quizContentPanel.add(resultPanel, "result");
        
        add(quizContentPanel, BorderLayout.CENTER);
        
        // Start with question
        displayQuestion(0);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainUI.MEDIUM_PURPLE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Financial Literacy Quiz");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        
        JLabel subtitleLabel = new JLabel("Test your knowledge and earn XP!");
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);
        labelPanel.add(titleLabel, BorderLayout.NORTH);
        labelPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        panel.add(labelPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MainUI.MEDIUM_PURPLE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Question number
        JLabel questionNumberLabel = new JLabel("Question 1 of " + questions.size());
        questionNumberLabel.setForeground(new Color(200, 200, 200));
        questionNumberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(questionNumberLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Question text
        questionLabel = new JLabel("Question text goes here");
        questionLabel.setForeground(Color.WHITE);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(questionLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Answer choices
        answerPanel = new JPanel();
        answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));
        answerPanel.setOpaque(false);
        answerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(answerPanel);
        
        return panel;
    }
    
    private JPanel createResultPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MainUI.MEDIUM_PURPLE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Result title
        JLabel resultTitleLabel = new JLabel("Quiz Complete!");
        resultTitleLabel.setForeground(Color.WHITE);
        resultTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        resultTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(resultTitleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Result message
        resultLabel = new JLabel("You got X out of Y correct!");
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(resultLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // XP gained
        xpLabel = new JLabel("You earned X XP!");
        xpLabel.setForeground(new Color(100, 255, 100));
        xpLabel.setFont(new Font("Arial", Font.BOLD, 16));
        xpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(xpLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Try again button
        JButton tryAgainButton = new JButton("Try Again");
        tryAgainButton.setBackground(MainUI.LIGHT_PURPLE);
        tryAgainButton.setForeground(Color.WHITE);
        tryAgainButton.setFont(new Font("Arial", Font.BOLD, 14));
        tryAgainButton.setFocusPainted(false);
        tryAgainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        tryAgainButton.setMaximumSize(new Dimension(200, 40));
        tryAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetQuiz();
            }
        });
        
        panel.add(tryAgainButton);
        
        return panel;
    }
    
    private void displayQuestion(int index) {
        currentQuestionIndex = index;
        QuizQuestion question = questions.get(index);
        
        // Update question number
        JLabel questionNumberLabel = (JLabel) ((JPanel) quizContentPanel.getComponent(0)).getComponent(0);
        questionNumberLabel.setText("Question " + (index + 1) + " of " + questions.size());
        
        // Update question text
        questionLabel.setText(question.getQuestion());
        
        // Clear previous answers
        answerPanel.removeAll();
        
        // Create answer buttons
        ButtonGroup answerGroup = new ButtonGroup();
        for (int i = 0; i < question.getAnswers().length; i++) {
            final int answerIndex = i;
            JRadioButton answerButton = new JRadioButton(question.getAnswers()[i]);
            answerButton.setForeground(Color.WHITE);
            answerButton.setFont(new Font("Arial", Font.PLAIN, 16));
            answerButton.setBackground(null);
            answerButton.setOpaque(false);
            answerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            answerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkAnswer(answerIndex);
                }
            });
            
            answerGroup.add(answerButton);
            answerPanel.add(answerButton);
            answerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        
        answerPanel.revalidate();
        answerPanel.repaint();
        
        // Show question card
        cardLayout.show(quizContentPanel, "question");
    }
    
    private void checkAnswer(int selectedAnswer) {
        QuizQuestion question = questions.get(currentQuestionIndex);
        boolean isCorrect = selectedAnswer == question.getCorrectAnswer();
        
        if (isCorrect) {
            correctAnswers++;
        }
        
        // Add some delay to show the result
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentQuestionIndex < questions.size() - 1) {
                    // Go to next question
                    displayQuestion(currentQuestionIndex + 1);
                } else {
                    // Show final result
                    showResult();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void showResult() {
        int xpGained = correctAnswers * 50; // 50 XP per correct answer
        
        resultLabel.setText("You got " + correctAnswers + " out of " + questions.size() + " correct!");
        xpLabel.setText("You earned " + xpGained + " XP!");
        
        // Add XP to user
        userController.addXP(xpGained);
        
        // Show result panel
        cardLayout.show(quizContentPanel, "result");
    }
    
    private void resetQuiz() {
        // Reset counters
        currentQuestionIndex = 0;
        correctAnswers = 0;
        
        // Randomize questions again
        Random random = new Random();
        for (int i = 0; i < questions.size(); i++) {
            int j = random.nextInt(questions.size());
            QuizQuestion temp = questions.get(i);
            questions.set(i, questions.get(j));
            questions.set(j, temp);
        }
        
        // Show first question
        displayQuestion(0);
    }
    
    // Quiz Question class
    private class QuizQuestion {
        private String question;
        private String[] answers;
        private int correctAnswer;
        
        public QuizQuestion(String question, String[] answers, int correctAnswer) {
            this.question = question;
            this.answers = answers;
            this.correctAnswer = correctAnswer;
        }
        
        public String getQuestion() {
            return question;
        }
        
        public String[] getAnswers() {
            return answers;
        }
        
        public int getCorrectAnswer() {
            return correctAnswer;
        }
    }
} 