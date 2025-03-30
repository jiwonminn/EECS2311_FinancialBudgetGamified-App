package controller;

import model.Quiz;
import model.Quiz.QuizQuestion;
import controller.UserController;
import controller.QuestController;
import utils.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.SQLException;

/**
 * Controller for managing the quiz functionality.
 * Handles loading quiz questions, checking answers, and updating scores.
 */
public class QuizController {
    private Quiz quiz;
    private UserController userController;
    private QuestController questController;

    /**
     * Creates a new QuizController with default questions.
     *
     * @param userController The UserController to update user points
     */
    public QuizController(UserController userController) {
        this.userController = userController;
        this.quiz = new Quiz();
        loadDefaultQuestions();
        
        // Initialize QuestController for rewarding XP and tracking quests
        try {
            this.questController = new QuestController();
        } catch (Exception e) {
            System.out.println("Error initializing QuestController: " + e.getMessage());
        }
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
        // Check if an answer is selected
        if (selectedAnswerIndex < 0) {
            return false;
        }
        
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
     * Starts a new quiz with the current questions.
     */
    public void startNewQuiz() {
        if (quiz != null) {
            quiz.reset();
        }
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

        quiz.addQuestion(new QuizQuestion(
                "What is zero-based budgeting?",
                Arrays.asList(
                        "A budget where you only use cash, no credit",
                        "A method where income minus expenses equals zero because every dollar has a job",
                        "A budget that starts from zero every year in corporate accounting",
                        "A budget where you try to spend as little as possible"
                ),
                1, 12,
                "Zero-based budgeting is a method where you allocate every dollar of your income to a specific purpose (expenses, savings, investments), so income minus expenses equals exactly zero.",
                "Budgeting"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is the envelope budgeting system?",
                Arrays.asList(
                        "A digital system for organizing receipts",
                        "A method where you put cash in physical envelopes for different spending categories",
                        "A type of investment portfolio allocation",
                        "A system used by banks to process payments"
                ),
                1, 10,
                "The envelope budgeting system involves placing cash in physical envelopes labeled for different budget categories, helping to limit spending to the allocated amount in each category.",
                "Budgeting"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is a discretionary expense?",
                Arrays.asList(
                        "A bill that must be paid every month",
                        "An expense that can be reduced or eliminated if needed",
                        "A tax-deductible business expense",
                        "A recurring subscription service"
                ),
                1, 10,
                "A discretionary expense is one that can be reduced or eliminated if needed, such as entertainment, dining out, or non-essential shopping.",
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

        quiz.addQuestion(new QuizQuestion(
                "What is the Rule of 72 in savings?",
                Arrays.asList(
                        "A rule stating you should save 72% of your income",
                        "The minimum credit score needed for premium financial services",
                        "A formula to estimate how long it takes for an investment to double at a fixed interest rate",
                        "A rule stating you can withdraw money from retirement at age 72"
                ),
                2, 15,
                "The Rule of 72 is a simple way to determine how long an investment will take to double given a fixed annual rate of interest. You divide 72 by the annual rate of return to get the approximate years it takes for money to double.",
                "Saving"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is a High-Yield Savings Account?",
                Arrays.asList(
                        "A regular checking account with bonus features",
                        "A savings account that offers higher-than-average interest rates",
                        "An investment account with stocks and bonds",
                        "A retirement account with employer matching"
                ),
                1, 10,
                "A High-Yield Savings Account is a type of savings account that pays a higher interest rate than a standard savings account, allowing your money to grow faster while remaining liquid and FDIC-insured.",
                "Saving"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is the difference between simple interest and compound interest?",
                Arrays.asList(
                        "Simple interest is better than compound interest",
                        "Simple interest is calculated only on the principal, while compound interest is calculated on principal plus accumulated interest",
                        "Compound interest is only used for loans, while simple interest is used for savings",
                        "There is no difference between them"
                ),
                1, 12,
                "Simple interest is calculated only on the initial principal amount, while compound interest is calculated on the principal plus any previously earned interest, leading to exponential growth over time.",
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

        quiz.addQuestion(new QuizQuestion(
                "What is diversification in investing?",
                Arrays.asList(
                        "Investing in the same industry across different countries",
                        "Changing your investment strategy frequently",
                        "Spreading investments across various asset classes to reduce risk",
                        "Investing all your money in different cryptocurrencies"
                ),
                2, 15,
                "Diversification is the strategy of spreading investments across various asset classes, industries, and geographic regions to reduce risk, as losses in one area may be offset by gains in another.",
                "Investing"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is dollar-cost averaging?",
                Arrays.asList(
                        "Converting foreign currencies before investing",
                        "A technique of buying at the lowest price point each month",
                        "Investing a fixed dollar amount at regular intervals regardless of share price",
                        "A way to calculate the average cost of all your investments"
                ),
                2, 15,
                "Dollar-cost averaging is the practice of investing a fixed amount of money at regular intervals, regardless of the share price. This reduces the impact of volatility and eliminates the need to time the market.",
                "Investing"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is an ETF (Exchange-Traded Fund)?",
                Arrays.asList(
                        "A type of retirement account",
                        "A fund that trades on an exchange and typically tracks an index, sector, commodity, or asset",
                        "A high-interest savings account offered by brokerages",
                        "A loan used to purchase stocks on margin"
                ),
                1, 12,
                "An ETF (Exchange-Traded Fund) is an investment fund traded on stock exchanges that holds assets such as stocks, bonds, or commodities and typically aims to track the performance of a specific index.",
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

        quiz.addQuestion(new QuizQuestion(
                "What is a secured credit card?",
                Arrays.asList(
                        "A credit card with enhanced security features",
                        "A credit card backed by a cash deposit that serves as collateral",
                        "A credit card with a very high credit limit",
                        "A credit card that can only be used for certain purchases"
                ),
                1, 12,
                "A secured credit card requires a cash deposit that serves as collateral. It's often used by people with limited or poor credit history to help build or rebuild their credit score.",
                "Credit"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is the difference between a hard inquiry and a soft inquiry on your credit report?",
                Arrays.asList(
                        "There is no difference",
                        "Hard inquiries happen when you check your credit; soft inquiries occur when lenders check your credit",
                        "Soft inquiries don't affect your credit score, while hard inquiries may lower your score temporarily",
                        "Hard inquiries are only used for mortgage applications"
                ),
                2, 10,
                "Soft inquiries (like checking your own credit) don't affect your credit score, while hard inquiries (which occur when applying for new credit) may lower your score by a few points temporarily and remain on your report for about two years.",
                "Credit"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is debt consolidation?",
                Arrays.asList(
                        "A law that protects consumers from abusive debt collection practices",
                        "A process of combining multiple debts into a single, often lower-interest payment",
                        "The automatic cancellation of certain debts after bankruptcy",
                        "A service that repairs your credit score"
                ),
                1, 12,
                "Debt consolidation is the process of combining multiple debts (such as credit card balances, loans) into a single loan or payment, usually with a lower interest rate, making it easier to manage and potentially saving money on interest.",
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

        quiz.addQuestion(new QuizQuestion(
                "What is the difference between a marginal tax rate and an effective tax rate?",
                Arrays.asList(
                        "They are two names for the same tax rate",
                        "The marginal rate applies to your last dollar earned, while the effective rate is your average overall tax rate",
                        "Marginal rates apply to employees, while effective rates apply to self-employed individuals",
                        "The effective rate includes state taxes, while the marginal rate only refers to federal taxes"
                ),
                1, 15,
                "The marginal tax rate is the rate paid on your last dollar of income (the highest bracket you fall into), while the effective tax rate is the average rate you pay on your total income after accounting for all brackets.",
                "Taxes"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is a tax-advantaged account?",
                Arrays.asList(
                        "An offshore account used to evade taxes",
                        "Any bank account that earns interest",
                        "An account that offers tax benefits like tax-deferred or tax-free growth",
                        "A business checking account used for tax payments"
                ),
                2, 12,
                "A tax-advantaged account is a savings or investment account that offers tax benefits, such as 401(k)s and IRAs (tax-deferred growth) or Roth accounts (tax-free growth on qualified withdrawals).",
                "Taxes"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is the purpose of Form W-4?",
                Arrays.asList(
                        "To report your annual income to the IRS",
                        "To tell your employer how much tax to withhold from your paycheck",
                        "To claim tax deductions and credits",
                        "To report investment income"
                ),
                1, 12,
                "Form W-4 is used to tell your employer how much federal income tax to withhold from your paycheck based on your filing status, number of dependents, and other factors.",
                "Taxes"
        ));

        // Category: Financial Planning
        quiz.addQuestion(new QuizQuestion(
                "What is the purpose of a will in financial planning?",
                Arrays.asList(
                        "To minimize your current tax burden",
                        "To specify how your assets should be distributed after your death",
                        "To provide income in retirement",
                        "To create a monthly budget"
                ),
                1, 12,
                "A will is a legal document that specifies how you want your assets to be distributed after your death, names guardians for minor children, and appoints an executor to carry out your wishes.",
                "Financial Planning"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is asset allocation?",
                Arrays.asList(
                        "The process of distributing assets in a divorce",
                        "The division of an investment portfolio among different asset categories like stocks, bonds, and cash",
                        "A method for determining your net worth",
                        "The process of filing property taxes"
                ),
                1, 15,
                "Asset allocation is the strategy of dividing an investment portfolio among different asset categories (stocks, bonds, cash, real estate, etc.) to balance risk and reward according to an investor's goals, risk tolerance, and time horizon.",
                "Financial Planning"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is the difference between term life insurance and whole life insurance?",
                Arrays.asList(
                        "Term life provides coverage for a specific period, while whole life provides lifetime coverage and has a cash value component",
                        "Term life is only for seniors, while whole life is for anyone",
                        "Whole life only covers accidental death, while term life covers all causes",
                        "There is no difference except for the name"
                ),
                0, 15,
                "Term life insurance provides coverage for a specific period (e.g., 10, 20 years) with no cash value, while whole life insurance provides permanent coverage and includes a cash value component that grows over time.",
                "Financial Planning"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is a power of attorney in financial planning?",
                Arrays.asList(
                        "A document that gives someone the legal authority to act on your behalf",
                        "A type of investment account",
                        "A legal document for transferring property ownership",
                        "A form for reporting financial crimes"
                ),
                0, 12,
                "A power of attorney is a legal document that gives someone else the authority to make financial decisions on your behalf if you become unable to do so yourself.",
                "Financial Planning"
        ));

        quiz.addQuestion(new QuizQuestion(
                "What is estate planning?",
                Arrays.asList(
                        "The process of buying and selling real estate",
                        "The process of planning for the management and distribution of your assets after death",
                        "A method for calculating property taxes",
                        "A strategy for investing in real estate"
                ),
                1, 12,
                "Estate planning is the process of arranging for the management and disposal of your estate during your life and after death, including wills, trusts, and other legal documents.",
                "Financial Planning"
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
        // Create a new quiz instance
        Quiz newQuiz = new Quiz();
        
        if (category == null || category.isEmpty()) {
            // Reset to all questions by creating a new quiz and reloading default questions
            this.quiz = new Quiz();
            loadDefaultQuestions();
            this.quiz.reset();  // Reset the quiz state
        } else {
            // Add only questions from the selected category
            for (QuizQuestion question : this.quiz.getQuestions()) {
                if (question.getCategory().equals(category)) {
                    newQuiz.addQuestion(question);
                }
            }
            this.quiz = newQuiz;
            this.quiz.reset();  // Reset the quiz state
        }
    }

    /**
     * Ends a quiz and awards XP through QuestController
     * Should be called when a quiz is completed
     */
    public void completeQuiz() {
        if (userController == null) return;
        
        // Get the score to award XP
        int score = quiz.getScore();
        
        try {
            // Get current user ID from active session
            int userId = getCurrentUserId();
            
            if (userId > 0) {
                System.out.println("Processing quiz completion for user " + userId + " with score " + score);
                
                // Create quiz completion table if it doesn't exist
                if (questController != null) {
                    try {
                        questController.createQuizCompletionTableIfNotExists();
                    } catch (Exception e) {
                        System.out.println("Error creating quiz completion table: " + e.getMessage());
                    }
                    
                    // Award XP based on quiz score
                    boolean success = questController.addUserXP(userId, score);
                    
                    if (success) {
                        System.out.println("Successfully added " + score + " XP for completing quiz");
                        
                        // Record quiz completion for quest tracking
                        questController.recordQuizCompletion(userId, score);
                        
                        // Also explicitly check for quest completions
                        questController.checkAndCompleteQuests(userId);
                    } else {
                        System.out.println("Failed to add XP for completing quiz");
                    }
                } else {
                    System.out.println("QuestController not available, using UserController for points only");
                    // Create a new quest controller if not already initialized
                    try {
                        this.questController = new QuestController();
                        this.questController.addUserXP(userId, score);
                        this.questController.recordQuizCompletion(userId, score);
                        this.questController.checkAndCompleteQuests(userId);
                    } catch (Exception e) {
                        System.out.println("Error creating QuestController: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("Could not determine user ID, using only UserController for points");
            }
        } catch (Exception e) {
            System.out.println("Error updating XP after quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the current user ID from the active session
     * @return the user ID or -1 if not found
     */
    private int getCurrentUserId() {
        try {
            // First try to get from UserController if available
            if (userController != null) {
                int controllerUserId = userController.getUserId();
                if (controllerUserId > 0) {
                    return controllerUserId;
                }
            }
            
            // Then try from session manager
            return SessionManager.getInstance().getUserId();
        } catch (Exception e) {
            System.out.println("Could not get user ID: " + e.getMessage());
            // Fallback to default user ID if necessary
            return 1; // Default to first user
        }
    }
}