package unitTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.QuizController;
import controller.UserController;
import model.Quiz;
import model.Quiz.QuizQuestion;

class QuizControllerTest {
    private QuizController quizController;
    private UserController userController;
    private static final int TEST_USER_ID = 999;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final double INITIAL_BALANCE = 1000.0;
    
    @BeforeEach
    void setUp() {
        userController = new UserController(TEST_USERNAME, TEST_EMAIL, INITIAL_BALANCE);
        quizController = new QuizController(userController);
    }
    
    @Test
    void testInitialization() {
        assertNotNull(quizController.getQuiz(), "Quiz should be initialized");
        assertNotNull(quizController.getCategories(), "Categories should be initialized");
        assertTrue(quizController.getCategories().size() > 0, "Should have at least one category");
    }
    
    @Test
    void testSubmitCorrectAnswer() {
        Quiz quiz = quizController.getQuiz();
        QuizQuestion currentQuestion = quiz.getCurrentQuestion();
        int correctAnswerIndex = currentQuestion.getCorrectAnswerIndex();
        int initialPoints = userController.getPoints();
        
        boolean result = quizController.submitAnswer(correctAnswerIndex);
        
        assertTrue(result, "Should return true for correct answer");
        assertEquals(initialPoints + currentQuestion.getPoints(), userController.getPoints(),
            "Points should be increased by question points");
    }
    
    @Test
    void testSubmitIncorrectAnswer() {
        Quiz quiz = quizController.getQuiz();
        QuizQuestion currentQuestion = quiz.getCurrentQuestion();
        int incorrectAnswerIndex = (currentQuestion.getCorrectAnswerIndex() + 1) % 4;
        int initialPoints = userController.getPoints();
        
        boolean result = quizController.submitAnswer(incorrectAnswerIndex);
        
        assertFalse(result, "Should return false for incorrect answer");
        assertEquals(initialPoints, userController.getPoints(),
            "Points should not change for incorrect answer");
    }
    
    @Test
    void testNextQuestion() {
        // First question
        Quiz quiz = quizController.getQuiz();
        QuizQuestion firstQuestion = quiz.getCurrentQuestion();
        
        // Move to next question
        boolean hasNext = quizController.nextQuestion();
        
        assertTrue(hasNext, "Should have more questions");
        assertNotEquals(firstQuestion, quiz.getCurrentQuestion(), 
            "Current question should be different after moving to next");
    }
    
    @Test
    void testStartNewQuiz() {
        // Complete the current quiz
        while (quizController.nextQuestion()) {
            quizController.submitAnswer(0);
        }
        
        // Start new quiz
        quizController.startNewQuiz();
        
        // Verify quiz is reset
        Quiz quiz = quizController.getQuiz();
        assertNotNull(quiz.getCurrentQuestion(), "Should have a current question after reset");
    }
    
    @Test
    void testGetCategories() {
        List<String> categories = quizController.getCategories();
        
        assertNotNull(categories, "Categories should not be null");
        assertTrue(categories.contains("Budgeting"), "Should contain Budgeting category");
        assertTrue(categories.contains("Saving"), "Should contain Saving category");
        assertTrue(categories.contains("Investing"), "Should contain Investing category");
        assertTrue(categories.contains("Credit"), "Should contain Credit category");
        assertTrue(categories.contains("Taxes"), "Should contain Taxes category");
    }
    
    @Test
    void testFilterByCategory() {
        String testCategory = "Budgeting";
        quizController.filterByCategory(testCategory);
        
        // Check all questions are from the test category
        Quiz quiz = quizController.getQuiz();
        for (QuizQuestion question : quiz.getQuestions()) {
            assertEquals(testCategory, question.getCategory(), 
                "All questions should be from " + testCategory + " category");
        }
    }
    
    @Test
    void testFilterByCategoryNull() {
        // Get initial question count
        int initialCount = quizController.getQuiz().getQuestions().size();
        
        // First filter to a specific category
        quizController.filterByCategory("Budgeting");
        
        // Then filter by null to reset
        quizController.filterByCategory(null);
        
        // Verify all questions are restored
        assertEquals(initialCount, quizController.getQuiz().getQuestions().size(),
            "Should restore all questions when filter is null");
    }
    
    @Test
    void testQuizCompletion() {
        Quiz quiz = quizController.getQuiz();
        int totalQuestions = quiz.getQuestions().size();
        int questionCount = 0;
        int initialPoints = userController.getPoints();
        int pointsEarned = 0;
        
        // Go through all questions
        do {
            questionCount++;
            QuizQuestion currentQuestion = quiz.getCurrentQuestion();
            if (quizController.submitAnswer(currentQuestion.getCorrectAnswerIndex())) {
                pointsEarned += currentQuestion.getPoints();
            }
        } while (quizController.nextQuestion());
        
        assertEquals(totalQuestions, questionCount, 
            "Should go through all questions before completing");
        assertFalse(quizController.nextQuestion(), 
            "Should return false when no more questions");
        assertEquals(initialPoints + pointsEarned, userController.getPoints(),
            "Total points should match points earned from correct answers");
    }
    
    @Test
    void testQuestionShuffling() {
        // Get initial order
        List<QuizQuestion> initialOrder = quizController.getQuiz().getQuestions();
        
        // Start new quiz (which shuffles questions)
        quizController.startNewQuiz();
        
        // Get new order
        List<QuizQuestion> newOrder = quizController.getQuiz().getQuestions();
        
        // Verify questions are the same but potentially in different order
        assertEquals(initialOrder.size(), newOrder.size(), 
            "Should have same number of questions");
        assertTrue(initialOrder.containsAll(newOrder) && newOrder.containsAll(initialOrder),
            "Should contain same questions, possibly in different order");
    }
    
    @Test
    void testMultipleQuizAttempts() {
        int initialPoints = userController.getPoints();
        int totalPointsEarned = 0;
        
        // Complete quiz multiple times
        for (int attempt = 0; attempt < 3; attempt++) {
            quizController.startNewQuiz();
            
            // Answer all questions correctly
            do {
                QuizQuestion currentQuestion = quizController.getQuiz().getCurrentQuestion();
                if (quizController.submitAnswer(currentQuestion.getCorrectAnswerIndex())) {
                    totalPointsEarned += currentQuestion.getPoints();
                }
            } while (quizController.nextQuestion());
        }
        
        assertEquals(initialPoints + totalPointsEarned, userController.getPoints(),
            "Total points should accumulate across multiple quiz attempts");
    }
    
    @Test
    void testCategoryProgress() {
        String testCategory = "Budgeting";
        quizController.filterByCategory(testCategory);
        Quiz quiz = quizController.getQuiz();
        int categoryQuestions = quiz.getQuestions().size();
        int correctAnswers = 0;
        
        // Answer all questions in category
        do {
            if (quizController.submitAnswer(quiz.getCurrentQuestion().getCorrectAnswerIndex())) {
                correctAnswers++;
            }
        } while (quizController.nextQuestion());
        
        assertEquals(categoryQuestions, correctAnswers,
            "Should be able to answer all questions in category correctly");
    }
} 