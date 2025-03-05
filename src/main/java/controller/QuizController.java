package controller;

import model.Quiz;
import model.Quiz.QuizQuestion;
import controller.UserController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for managing the quiz functionality.
 * Handles loading quiz questions, checking answers, and updating scores.
 */
public class QuizController {
    private Quiz quiz;
    private UserController userController;
    
    /**
     * Creates a new QuizController with default questions.
     * 
     * @param userController The UserController to update user points
     */
    public QuizController(UserController userController) {
        this.userController = userController;
        this.quiz = new Quiz();
        loadDefaultQuestions();
    }
    
    /**
     * Gets the quiz model.
     * 
     * @return The quiz model
     */
    public Quiz getQuiz() {
        return quiz;
    }
    
    /**
     * Submits an answer for the current question.
     * 
     * @param selectedAnswerIndex The index of the selected answer
     * @return true if the answer is correct, false otherwise
     */
    public boolean submitAnswer(int selectedAnswerIndex) {
        boolean isCorrect = quiz.checkAnswer(selectedAnswerIndex);
        
        // If the answer is correct, award points to the user
        if (isCorrect && userController != null) {
            QuizQuestion currentQuestion = quiz.getCurrentQuestion();
            if (currentQuestion != null) {
                userController.addPoints(currentQuestion.getPoints());
            }
        }
        
        return isCorrect;
    }
    
    /**
     * Moves to the next question in the quiz.
     * 
     * @return true if there is a next question, false otherwise
     */
    public boolean nextQuestion() {
        return quiz.nextQuestion();
    }
    
    /**
     * Starts a new quiz by resetting the current quiz and shuffling questions.
     */
    public void startNewQuiz() {
        quiz.reset();
        quiz.shuffleQuestions();
    }
    
    /**
     * Loads a predefined set of financial literacy quiz questions.
     */
    private void loadDefaultQuestions() {
        // Category: Budgeting
        quiz.addQuestion(new QuizQuestion(
            "What is the 50/30/20 rule in budgeting?",
            Arrays.asList(
                "Save 50%, spend 30%, invest 20% of income",
                "Spend 50% on needs, 30% on wants, and save 20% of income",
                "Allocate 50% to debt, 30% to savings, and 20% to expenses",
                "Pay 50% in taxes, spend 30%, and save 20% of income"
            ),
            1, 10,
            "The 50/30/20 rule suggests allocating 50% of your income to needs, 30% to wants, and 20% to savings and debt repayment.",
            "Budgeting"
        ));
        
        quiz.addQuestion(new QuizQuestion(
            "Which of these is NOT typically considered a fixed expense?",
            Arrays.asList(
                "Rent or mortgage payment",
                "Restaurant meals",
                "Car insurance premium",
                "Internet bill"
            ),
            1, 10,
            "Restaurant meals are variable expenses because they change based on your discretion and aren't consistent monthly costs.",
            "Budgeting"
        ));
        
        // Category: Saving
        quiz.addQuestion(new QuizQuestion(
            "What is an emergency fund?",
            Arrays.asList(
                "A fund for luxury purchases",
                "Money set aside for unexpected expenses or financial hardships",
                "A retirement account",
                "An investment in the stock market"
            ),
            1, 10,
            "An emergency fund is money set aside to cover unexpected expenses like medical emergencies, car repairs, or job loss.",
            "Saving"
        ));
        
        quiz.addQuestion(new QuizQuestion(
            "How much should you ideally have in your emergency fund?",
            Arrays.asList(
                "1 month of expenses",
                "3-6 months of expenses",
                "1 year of expenses",
                "As much as possible"
            ),
            1, 10,
            "Financial experts typically recommend having 3-6 months of living expenses saved in your emergency fund.",
            "Saving"
        ));
        
        // Category: Investing
        quiz.addQuestion(new QuizQuestion(
            "What is compound interest?",
            Arrays.asList(
                "Interest paid only on the principal amount",
                "Interest earned on both the initial principal and the accumulated interest",
                "A fixed interest rate that never changes",
                "Interest that is always compounded daily"
            ),
            1, 15,
            "Compound interest is interest earned not only on your initial investment but also on the interest previously earned, leading to exponential growth over time.",
            "Investing"
        ));
        
        quiz.addQuestion(new QuizQuestion(
            "Which investment typically has the highest risk and potential return?",
            Arrays.asList(
                "Savings account",
                "Government bonds",
                "Mutual funds",
                "Individual stocks"
            ),
            3, 15,
            "Individual stocks typically have the highest risk and potential return among these options because their value can fluctuate significantly based on company performance.",
            "Investing"
        ));
        
        // Category: Credit and Debt
        quiz.addQuestion(new QuizQuestion(
            "What factor has the biggest impact on your credit score?",
            Arrays.asList(
                "Your income level",
                "Payment history",
                "Number of credit cards you own",
                "Your education level"
            ),
            1, 10,
            "Payment history (whether you pay your bills on time) typically accounts for about 35% of your credit score and has the biggest impact.",
            "Credit"
        ));
        
        quiz.addQuestion(new QuizQuestion(
            "What is considered a good credit utilization ratio?",
            Arrays.asList(
                "0% (using no credit)",
                "Below 30% of available credit",
                "50-70% of available credit",
                "90-100% of available credit"
            ),
            1, 10,
            "Financial experts generally recommend keeping your credit utilization ratio below 30% to maintain a healthy credit score.",
            "Credit"
        ));
        
        // Category: Taxes
        quiz.addQuestion(new QuizQuestion(
            "What is the difference between a tax deduction and a tax credit?",
            Arrays.asList(
                "There is no difference",
                "A tax deduction reduces taxable income, while a tax credit reduces taxes owed directly",
                "A tax credit reduces taxable income, while a tax deduction reduces taxes owed directly",
                "A tax deduction is for businesses, while a tax credit is for individuals"
            ),
            1, 15,
            "A tax deduction reduces your taxable income, while a tax credit directly reduces the amount of tax you owe, dollar for dollar.",
            "Taxes"
        ));
        
        quiz.addQuestion(new QuizQuestion(
            "Which retirement account typically allows tax-free withdrawals in retirement?",
            Arrays.asList(
                "Traditional 401(k)",
                "Traditional IRA",
                "Roth IRA",
                "Simple IRA"
            ),
            2, 15,
            "Roth IRAs are funded with after-tax dollars, meaning qualified withdrawals in retirement are tax-free, unlike Traditional IRAs and 401(k)s which are taxed upon withdrawal.",
            "Taxes"
        ));
    }
    
    /**
     * Gets a list of all quiz categories.
     * 
     * @return List of unique categories
     */
    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        for (QuizQuestion question : quiz.getQuestions()) {
            String category = question.getCategory();
            if (!categories.contains(category)) {
                categories.add(category);
            }
        }
        return categories;
    }
    
    /**
     * Filters questions by category and starts a new quiz with only those questions.
     * 
     * @param category The category to filter by, or null for all categories
     */
    public void filterByCategory(String category) {
        Quiz filteredQuiz = new Quiz();
        
        if (category == null || category.isEmpty()) {
            // Reset to all questions
            this.quiz.reset();
            return;
        }
        
        // Add only questions from the selected category
        for (QuizQuestion question : this.quiz.getQuestions()) {
            if (question.getCategory().equals(category)) {
                filteredQuiz.addQuestion(question);
            }
        }
        
        // Only update if we have matching questions
        if (filteredQuiz.getQuestionCount() > 0) {
            this.quiz = filteredQuiz;
            this.quiz.shuffleQuestions();
        }
    }
}
