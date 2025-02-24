package model;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private List<QuizQuestion> questions;
    private int currentScore;
    
    public Quiz() {
        this.questions = new ArrayList<>();
        this.currentScore = 0;
        initializeQuestions();
    }
    
    private void initializeQuestions() {
        questions.add(new QuizQuestion(
            "What is a budget?",
            new String[]{
                "A financial plan for spending and saving",
                "A type of bank account",
                "A credit card limit",
                "A type of investment"
            },
            0
        ));
        
        questions.add(new QuizQuestion(
            "What is compound interest?",
            new String[]{
                "Interest earned only on the principal amount",
                "Interest earned on both principal and accumulated interest",
                "A fixed interest rate that never changes",
                "A type of loan"
            },
            1
        ));
        
        questions.add(new QuizQuestion(
            "Which is generally considered the most risky investment?",
            new String[]{
                "Government bonds",
                "Certificate of deposit",
                "Individual stocks",
                "Savings account"
            },
            2
        ));
        
        questions.add(new QuizQuestion(
            "What is the 50/30/20 budgeting rule?",
            new String[]{
                "50% savings, 30% needs, 20% wants",
                "50% needs, 30% wants, 20% savings",
                "50% wants, 30% savings, 20% needs",
                "50% needs, 30% savings, 20% wants"
            },
            1
        ));
    }
    
    public QuizQuestion getQuestion(int index) {
        return questions.get(index);
    }
    
    public int getTotalQuestions() {
        return questions.size();
    }
    
    public void incrementScore() {
        currentScore++;
    }
    
    public int getCurrentScore() {
        return currentScore;
    }
    
    public static class QuizQuestion {
        private String question;
        private String[] options;
        private int correctAnswer;
        
        public QuizQuestion(String question, String[] options, int correctAnswer) {
            this.question = question;
            this.options = options;
            this.correctAnswer = correctAnswer;
        }
        
        public String getQuestion() { return question; }
        public String[] getOptions() { return options; }
        public int getCorrectAnswer() { return correctAnswer; }
    }
}
