package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a financial literacy quiz with questions and scoring functionality.
 */
public class Quiz {
    private List<QuizQuestion> questions;
    private int currentQuestionIndex;
    private int score;
    private List<Boolean> answeredQuestions;  // Track which questions have been answered
    private int selectedAnswer;  // Track the currently selected answer
    
    /**
     * Creates a new quiz with an empty question list.
     */
    public Quiz() {
        this.questions = new ArrayList<>();
        this.currentQuestionIndex = 0;
        this.score = 0;
        this.answeredQuestions = new ArrayList<>();
        this.selectedAnswer = -1;  // Initialize with no selection
    }
    
    /**
     * Adds a question to the quiz.
     * @param question The question to add
     */
    public void addQuestion(QuizQuestion question) {
        questions.add(question);
        answeredQuestions.add(false);  // Initialize as unanswered
    }
    
    /**
     * Gets all questions in the quiz.
     * @return List of quiz questions
     */
    public List<QuizQuestion> getQuestions() {
        return Collections.unmodifiableList(questions);
    }
    
    /**
     * Gets the current question in the quiz.
     * @return The current question or null if there are no questions
     */
    public QuizQuestion getCurrentQuestion() {
        if (currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex);
        }
        return null;
    }
    
    /**
     * Sets the currently selected answer for the current question.
     * @param answerIndex The index of the selected answer
     */
    public void setSelectedAnswer(int answerIndex) {
        this.selectedAnswer = answerIndex;
    }
    
    /**
     * Gets the currently selected answer for the current question.
     * @return The index of the selected answer, or -1 if no answer is selected
     */
    public int getSelectedAnswer() {
        return selectedAnswer;
    }
    
    /**
     * Moves to the next question in the quiz.
     * @return true if there is a next question, false otherwise
     */
    public boolean nextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            return true;
        }
        return false;
    }
    
    /**
     * Checks if the provided answer is correct for the current question.
     * @param answerIndex The index of the selected answer
     * @return true if the answer is correct, false otherwise
     */
    public boolean checkAnswer(int answerIndex) {
        QuizQuestion currentQuestion = getCurrentQuestion();
        if (currentQuestion == null) {
            return false;
        }
        
        // Update the selected answer
        setSelectedAnswer(answerIndex);
        
        // If already answered, just return if the answer is correct
        if (answeredQuestions.get(currentQuestionIndex)) {
            return answerIndex == currentQuestion.getCorrectAnswerIndex();
        }
        
        // Process new answer
        boolean isCorrect = answerIndex == currentQuestion.getCorrectAnswerIndex();
        if (isCorrect) {
            score += currentQuestion.getPoints();
        }
        answeredQuestions.set(currentQuestionIndex, true);
        return isCorrect;
    }
    
    /**
     * Gets the current score in the quiz.
     * @return The current score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Resets the quiz to the beginning.
     */
    public void reset() {
        currentQuestionIndex = 0;
        score = 0;
        selectedAnswer = -1;
        // Reset answered questions list
        answeredQuestions = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            answeredQuestions.add(false);
        }
        // Reset shuffled answers for each question
        for (QuizQuestion question : questions) {
            question.shuffleAnswers();
        }
        // Shuffle the order of questions
        Collections.shuffle(questions);
    }
    
    /**
     * Shuffles the order of questions in the quiz.
     */
    public void shuffleQuestions() {
        Collections.shuffle(questions);
        reset();  // Reset all state after shuffling
    }
    
    /**
     * Gets the total number of questions in the quiz.
     * @return The number of questions
     */
    public int getQuestionCount() {
        return questions.size();
    }
    
    /**
     * Gets the index of the current question.
     * @return The current question index (0-based)
     */
    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
    
    /**
     * Gets the maximum possible score for the quiz.
     * @return The maximum possible score
     */
    public int getMaximumScore() {
        int maxScore = 0;
        for (QuizQuestion question : questions) {
            maxScore += question.getPoints();
        }
        return maxScore;
    }
    
    /**
     * Inner class representing a quiz question with multiple choice answers.
     */
    public static class QuizQuestion {
        private String question;
        private List<String> answers;
        private int correctAnswerIndex;
        private int points;
        private String explanation;
        private String category;
        private List<String> shuffledAnswers;  // Store shuffled answers
        private int shuffledCorrectIndex;  // Store correct index after shuffling
        
        /**
         * Creates a new quiz question.
         * 
         * @param question The question text
         * @param answers List of possible answers
         * @param correctAnswerIndex The index of the correct answer (0-based)
         * @param points The points awarded for a correct answer
         * @param explanation The explanation of the correct answer
         * @param category The category of the question (budgeting, investing, etc.)
         */
        public QuizQuestion(String question, List<String> answers, int correctAnswerIndex, 
                           int points, String explanation, String category) {
            this.question = question;
            this.answers = new ArrayList<>(answers);
            this.correctAnswerIndex = correctAnswerIndex;
            this.points = points;
            this.explanation = explanation;
            this.category = category;
            shuffleAnswers();  // Initialize shuffled answers
        }
        
        /**
         * Shuffles the answers and updates the correct answer index.
         */
        public void shuffleAnswers() {
            // Create a list of indices to track the original positions
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < answers.size(); i++) {
                indices.add(i);
            }
            
            // Shuffle the indices
            Collections.shuffle(indices);
            
            // Create new shuffled answers list
            shuffledAnswers = new ArrayList<>();
            for (int i = 0; i < indices.size(); i++) {
                shuffledAnswers.add(answers.get(indices.get(i)));
                if (indices.get(i) == correctAnswerIndex) {
                    shuffledCorrectIndex = i;
                }
            }
        }
        
        public String getQuestion() {
            return question;
        }
        
        public List<String> getAnswers() {
            return Collections.unmodifiableList(shuffledAnswers != null ? shuffledAnswers : answers);
        }
        
        public int getCorrectAnswerIndex() {
            return shuffledAnswers != null ? shuffledCorrectIndex : correctAnswerIndex;
        }
        
        public int getPoints() {
            return points;
        }
        
        public String getExplanation() {
            return explanation;
        }
        
        public String getCategory() {
            return category;
        }
    }
}
