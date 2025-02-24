package controller;

import model.Quiz;

public class QuizController {
    private Quiz quiz;
    private int currentQuestionIndex;
    private UserController userController;
    
    public QuizController(UserController userController) {
        this.quiz = new Quiz();
        this.currentQuestionIndex = 0;
        this.userController = userController;
    }
    
    public Quiz.QuizQuestion getCurrentQuestion() {
        return quiz.getQuestion(currentQuestionIndex);
    }
    
    public boolean answerQuestion(int selectedAnswer) {
        Quiz.QuizQuestion question = getCurrentQuestion();
        boolean isCorrect = (selectedAnswer == question.getCorrectAnswer());
        
        if (isCorrect) {
            quiz.incrementScore();
            userController.addPoints(10); // Award points for correct answers
        }
        
        currentQuestionIndex++;
        return isCorrect;
    }
    
    public boolean hasMoreQuestions() {
        return currentQuestionIndex < quiz.getTotalQuestions();
    }
    
    public int getCurrentScore() {
        return quiz.getCurrentScore();
    }
}
