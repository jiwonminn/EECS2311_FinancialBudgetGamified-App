    package view;

    import controller.QuizController;
    import controller.UserController;
    import model.Quiz.QuizQuestion;

    import javax.swing.*;
    import javax.swing.border.EmptyBorder;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.util.ArrayList;
    import java.util.List;

    /**
     * UI component for displaying and interacting with quizzes.
     */
    public class QuizUI extends JPanel {
        // Define colors to match the application theme
        private final Color BACKGROUND_COLOR = new Color(24, 15, 41);
        private final Color PANEL_COLOR = new Color(40, 24, 69);
        private final Color TEXT_COLOR = new Color(255, 255, 255);
        private final Color ACCENT_COLOR = new Color(128, 90, 213);
        private final Color CORRECT_COLOR = new Color(39, 174, 96);
        private final Color INCORRECT_COLOR = new Color(215, 38, 61);
        private final Color FIELD_BACKGROUND = new Color(50, 35, 80);
        private final Color FIELD_BORDER = new Color(70, 50, 110);
        
        private QuizController quizController;
        private JPanel contentPanel;
        private JPanel questionPanel;
        private JPanel resultsPanel;
        private JPanel categoryPanel;
        private List<JRadioButton> answerButtons;
        private JButton submitButton;
        private JButton nextButton;
        private JButton newQuizButton;
        private JLabel questionLabel;
        private JLabel questionNumberLabel;
        private JLabel resultLabel;
        private JLabel explanationLabel;
        private JLabel scoreLabel;
        private CardLayout cardLayout;
        
        private int selectedAnswerIndex = -1;
        private boolean answerSubmitted = false;
        private boolean quizInProgress = false;
        
        /**
         * Creates a new QuizUI with the given QuizController.
         * 
         * @param quizController The controller for quiz functionality
         */
        public QuizUI(QuizController quizController) {
            this.quizController = quizController;
            this.answerButtons = new ArrayList<>();
            
            setupUI();
            showCategorySelection();
        }
        
        /**
         * Checks if there is currently a quiz in progress.
         * 
         * @return true if a quiz is in progress, false otherwise
         */
        public boolean isQuizInProgress() {
            return quizInProgress;
        }
        
        /**
         * Sets up the main UI components.
         */
        private void setupUI() {
            setLayout(new BorderLayout());
            setBackground(BACKGROUND_COLOR);
            
            // Create header with title
            JPanel headerPanel = createHeaderPanel();
            add(headerPanel, BorderLayout.NORTH);
            
            // Create card layout for different screens
            cardLayout = new CardLayout();
            contentPanel = new JPanel(cardLayout);
            contentPanel.setBackground(BACKGROUND_COLOR);
            
            // Create the three main panels
            categoryPanel = createCategoryPanel();
            questionPanel = createQuestionPanel();
            resultsPanel = createResultsPanel();
            
            // Add panels to card layout
            contentPanel.add(categoryPanel, "CATEGORY");
            contentPanel.add(questionPanel, "QUESTION");
            contentPanel.add(resultsPanel, "RESULTS");
            
            // Add content panel to main layout
            add(contentPanel, BorderLayout.CENTER);
        }
        
        /**
         * Creates the header panel with title.
         */
        private JPanel createHeaderPanel() {
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(BACKGROUND_COLOR);
            headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
            
            JLabel titleLabel = new JLabel("Financial Knowledge Quest");
            titleLabel.setForeground(TEXT_COLOR);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            headerPanel.add(titleLabel, BorderLayout.WEST);
            
            JLabel subtitleLabel = new JLabel("Test your financial literacy and earn XP");
            subtitleLabel.setForeground(new Color(180, 180, 180));
            subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
            
            return headerPanel;
        }
        
        /**
         * Creates the category selection panel.
         */
        private JPanel createCategoryPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(BACKGROUND_COLOR);
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            // Category selection title
            JLabel titleLabel = new JLabel("Choose a Quiz Category");
            titleLabel.setForeground(TEXT_COLOR);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Description
            JLabel descLabel = new JLabel("Select a category to test your knowledge and earn XP rewards");
            descLabel.setForeground(new Color(180, 180, 180));
            descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Create category cards grid
            JPanel categoriesGrid = createCategoryCardsGrid();
            
            // Add components with spacing
            panel.add(Box.createVerticalGlue());
            panel.add(titleLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(descLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 30)));
            panel.add(categoriesGrid);
            panel.add(Box.createVerticalGlue());
            
            return panel;
        }
        
        /**
         * Creates a grid of category cards for visual selection.
         */
        private JPanel createCategoryCardsGrid() {
            JPanel gridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
            gridPanel.setBackground(BACKGROUND_COLOR);
            gridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Add "All Categories" card first
            JPanel allCategoriesCard = createCategoryCard("All Categories");
            gridPanel.add(allCategoriesCard);
            
            // Add individual category cards
            List<String> categories = quizController.getCategories();
            for (String category : categories) {
                JPanel card = createCategoryCard(category);
                gridPanel.add(card);
            }
            
            return gridPanel;
        }
        
        /**
         * Creates a card for a quiz category.
         */
        private JPanel createCategoryCard(String category) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(PANEL_COLOR);
            card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Icon panel with circle background
            JPanel iconPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw circle background
                    Color iconColor = "All Categories".equals(category) ? ACCENT_COLOR : getCategoryColor(category);
                    g2d.setColor(iconColor);
                    int size = Math.min(getWidth(), getHeight()) - 20;
                    g2d.fillOval((getWidth() - size) / 2, (getHeight() - size) / 2, size, size);
                    
                    // Draw the category icon
                    drawCategoryIcon(g2d, category, getWidth(), getHeight());
                    
                    g2d.dispose();
                }
                
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(80, 80);
                }
                
                @Override
                public Dimension getMinimumSize() {
                    return new Dimension(80, 80);
                }
                
                @Override
                public Dimension getMaximumSize() {
                    return new Dimension(80, 80);
                }
            };
            iconPanel.setOpaque(false);
            iconPanel.setBackground(PANEL_COLOR);
            iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Category name
            JLabel nameLabel = new JLabel(category);
            nameLabel.setForeground(TEXT_COLOR);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Questions count
            String countText = "All Categories".equals(category) ? 
                "All questions" : countQuestionsInCategory(category) + " questions";
            JLabel countLabel = new JLabel(countText);
            countLabel.setForeground(new Color(180, 180, 180));
            countLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Add components with padding
            card.add(Box.createRigidArea(new Dimension(0, 15)));
            card.add(iconPanel);
            card.add(Box.createRigidArea(new Dimension(0, 10)));
            card.add(nameLabel);
            card.add(Box.createRigidArea(new Dimension(0, 5)));
            card.add(countLabel);
            card.add(Box.createRigidArea(new Dimension(0, 15)));
            
            // Add click handler
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (quizInProgress) {
                        int result = JOptionPane.showConfirmDialog(
                            QuizUI.this,
                            "You have an incomplete quiz. Are you sure you want to start a new one?",
                            "Incomplete Quiz",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                        );
                        
                        if (result == JOptionPane.NO_OPTION) {
                            return;
                        }
                    }
                    
                    // Reset all UI state first
                    selectedAnswerIndex = -1;
                    answerSubmitted = false;
                    
                    // Reset all answer buttons
                    for (JRadioButton button : answerButtons) {
                        button.setSelected(false);
                        button.setForeground(TEXT_COLOR);
                        button.setBackground(FIELD_BACKGROUND);
                    }
                    
                    // Reset result and explanation labels
                    resultLabel.setText("");
                    resultLabel.setVisible(false);
                    explanationLabel.setText("");
                    explanationLabel.setVisible(false);
                    
                    // Reset buttons
                    submitButton.setVisible(true);
                    nextButton.setVisible(false);
                    
                    // Filter and start new quiz
                    if ("All Categories".equals(category)) {
                        quizController.filterByCategory(null);
                    } else {
                        quizController.filterByCategory(category);
                    }
                    
                    // Ensure quiz is properly reset and started
                    quizController.startNewQuiz();
                    quizInProgress = true;
                    
                    // Update UI and show question panel
                    updateQuestionPanel();
                    cardLayout.show(contentPanel, "QUESTION");
                }
                
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    card.setBackground(new Color(60, 40, 100));
                    iconPanel.setBackground(new Color(60, 40, 100));
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    card.setBackground(PANEL_COLOR);
                    iconPanel.setBackground(PANEL_COLOR);
                }
            });
            
            return card;
        }
        
        /**
         * Draws the icon for a category inside the provided Graphics context.
         */
        private void drawCategoryIcon(Graphics2D g2d, String category, int width, int height) {
            g2d.setColor(Color.WHITE);
            
            int x = width / 2;
            int y = height / 2;
            int size = Math.min(width, height) - 30; // Keep consistent size
            
            switch (category) {
                case "Budgeting":
                    // Dollar sign in circle
                    g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
                    FontMetrics fm = g2d.getFontMetrics();
                    g2d.drawString("$", x - fm.stringWidth("$")/2, y + fm.getAscent()/2 - fm.getDescent()/2);
                    break;
                    
                case "Saving":
                    // Piggy bank icon
                    int pigSize = size / 2;
                    // Body
                    g2d.fillOval(x - pigSize, y - pigSize/2, pigSize*2, pigSize);
                    // Snout
                    g2d.fillOval(x + pigSize/2, y, pigSize/2, pigSize/3);
                    // Ear
                    g2d.fillOval(x - pigSize/2, y - pigSize/2 - pigSize/4, pigSize/3, pigSize/3);
                    // Leg
                    g2d.fillRect(x - pigSize/2, y + pigSize/3, pigSize/4, pigSize/3);
                    g2d.fillRect(x + pigSize/4, y + pigSize/3, pigSize/4, pigSize/3);
                    break;
                    
                case "Investing":
                    // Stock chart icon
                    int chartWidth = size/2;
                    int chartHeight = size/2;
                    
                    // Draw growing chart line
                    g2d.setStroke(new BasicStroke(3));
                    int[] xPoints = {x - chartWidth/2, x - chartWidth/4, x, x + chartWidth/2};
                    int[] yPoints = {y + chartHeight/3, y, y + chartHeight/4, y - chartHeight/3};
                    g2d.drawPolyline(xPoints, yPoints, 4);
                    
                    // Draw arrow head
                    g2d.drawLine(x + chartWidth/2, y - chartHeight/3, x + chartWidth/3, y - chartHeight/6);
                    g2d.drawLine(x + chartWidth/2, y - chartHeight/3, x + chartWidth/3, y - chartHeight/2);
                    break;
                    
                case "Credit":
                    // Credit card icon
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(x - size/3, y - size/4, 2*size/3, size/2, 10, 10);
                    
                    // Magnetic stripe
                    g2d.drawLine(x - size/3, y - size/8, x + size/3, y - size/8);
                    
                    // Card details
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawLine(x - size/4, y + size/8, x + size/6, y + size/8);
                    break;
                    
                case "Taxes":
                    // Tax form icon
                    g2d.setStroke(new BasicStroke(2));
                    // Form outline
                    g2d.drawRect(x - size/3, y - size/3, 2*size/3, 2*size/3);
                    
                    // Form lines
                    g2d.setStroke(new BasicStroke(1));
                    for (int i = 1; i <= 3; i++) {
                        int lineY = y - size/3 + i * (2*size/3)/4;
                        g2d.drawLine(x - size/4, lineY, x + size/4, lineY);
                    }
                    
                    // Dollar sign
                    g2d.setFont(new Font("Arial", Font.BOLD, size/6));
                    FontMetrics fm2 = g2d.getFontMetrics();
                    g2d.drawString("$", x - size/4 - fm2.stringWidth("$")/2, y - size/6);
                    break;
                    
                case "Financial Planning":
                    // Calendar or planner icon
                    g2d.setStroke(new BasicStroke(2));
                    // Calendar outline
                    g2d.drawRect(x - size/3, y - size/3, 2*size/3, 2*size/3);
                    
                    // Top bar for dates
                    g2d.drawLine(x - size/3, y - size/6, x + size/3, y - size/6);
                    
                    // Grid lines (3x3)
                    int cellSize = 2*size/9;
                    for (int i = 1; i < 3; i++) {
                        // Vertical lines
                        g2d.drawLine(x - size/3 + i*cellSize, y - size/6, x - size/3 + i*cellSize, y + size/3);
                        // Horizontal lines
                        g2d.drawLine(x - size/3, y - size/6 + i*cellSize, x + size/3, y - size/6 + i*cellSize);
                    }
                    break;
                    
                case "All Categories":
                    // Star or asterisk
                    g2d.setStroke(new BasicStroke(3));
                    int starSize = size/3;
                    
                    // Draw asterisk/star
                    g2d.drawLine(x, y - starSize, x, y + starSize); // Vertical
                    g2d.drawLine(x - starSize, y, x + starSize, y); // Horizontal
                    g2d.drawLine(x - (int)(starSize*0.7), y - (int)(starSize*0.7), 
                                x + (int)(starSize*0.7), y + (int)(starSize*0.7)); // Diagonal 1
                    g2d.drawLine(x - (int)(starSize*0.7), y + (int)(starSize*0.7), 
                                x + (int)(starSize*0.7), y - (int)(starSize*0.7)); // Diagonal 2
                    break;
                    
                default:
                    // Question mark
                    g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
                    FontMetrics fmDef = g2d.getFontMetrics();
                    g2d.drawString("?", x - fmDef.stringWidth("?")/2, y + fmDef.getAscent()/2 - fmDef.getDescent()/2);
                    break;
            }
        }
        
        /**
         * Gets the icon for a category.
         */
        private String getCategoryIcon(String category) {
            switch (category) {
                case "Budgeting": return "$";
                case "Saving": return "$";
                case "Investing": return "^";
                case "Credit": return "C";
                case "Taxes": return "T";
                case "Financial Planning": return "FP";
                case "All Categories": return "*";
                default: return "?";
            }
        }
        
        /**
         * Gets the color for a category.
         */
        private Color getCategoryColor(String category) {
            switch (category) {
                case "Budgeting": return new Color(90, 140, 255);
                case "Saving": return new Color(39, 174, 96);
                case "Investing": return new Color(155, 89, 182);
                case "Credit": return new Color(231, 76, 60);
                case "Taxes": return new Color(241, 196, 15);
                case "Financial Planning": return new Color(52, 152, 219);
                case "All Categories": return ACCENT_COLOR;
                default: return ACCENT_COLOR;
            }
        }
        
        /**
         * Counts the number of questions in a specific category.
         */
        private int countQuestionsInCategory(String category) {
            int count = 0;
            for (QuizQuestion question : quizController.getQuiz().getQuestions()) {
                if (question.getCategory().equals(category)) {
                    count++;
                }
            }
            return count;
        }
        
        /**
         * Creates the question display panel.
         */
        private JPanel createQuestionPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(BACKGROUND_COLOR);
            panel.setBorder(new EmptyBorder(20, 40, 20, 40));
            
            // Question number label
            questionNumberLabel = new JLabel("Question 1/10");
            questionNumberLabel.setForeground(new Color(180, 180, 180));
            questionNumberLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            questionNumberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Question text
            questionLabel = new JLabel("Question text goes here");
            questionLabel.setForeground(TEXT_COLOR);
            questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
            questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Answer options panel
            JPanel answersPanel = new JPanel();
            answersPanel.setLayout(new BoxLayout(answersPanel, BoxLayout.Y_AXIS));
            answersPanel.setBackground(BACKGROUND_COLOR);
            answersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Create answer buttons (will be populated in updateQuestionPanel)
            ButtonGroup group = new ButtonGroup();
            for (int i = 0; i < 4; i++) {
                JRadioButton radioButton = createAnswerRadioButton("");
                answerButtons.add(radioButton);
                group.add(radioButton);
                answersPanel.add(radioButton);
                answersPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                
                final int index = i;
                radioButton.addActionListener(e -> selectedAnswerIndex = index);
            }
            
            // Result label (initially hidden)
            resultLabel = new JLabel("");
            resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
            resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultLabel.setVisible(false);
            
            // Explanation label (initially hidden)
            explanationLabel = new JLabel("");
            explanationLabel.setForeground(new Color(180, 180, 180));
            explanationLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            explanationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            explanationLabel.setVisible(false);
            
            // Buttons panel
            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            buttonsPanel.setBackground(BACKGROUND_COLOR);
            buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Submit button
            submitButton = createGradientButton("Submit Answer");
            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Get the currently selected button
                    for (int i = 0; i < answerButtons.size(); i++) {
                        if (answerButtons.get(i).isSelected()) {
                            selectedAnswerIndex = i;
                            break;
                        }
                    }
                    
                    if (selectedAnswerIndex >= 0 && !answerSubmitted) {
                        boolean correct = quizController.submitAnswer(selectedAnswerIndex);
                        showAnswerResult(correct);
                        answerSubmitted = true;
                    } else if (selectedAnswerIndex < 0) {
                        JOptionPane.showMessageDialog(QuizUI.this, 
                            "Please select an answer", 
                            "No Answer Selected", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                }
            });
            buttonsPanel.add(submitButton);
            
            // Next button (initially hidden)
            nextButton = createGradientButton("Next Question");
            nextButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (quizController.nextQuestion()) {
                        updateQuestionPanel();
                    } else {
                        showResults();
                    }
                }
            });
            nextButton.setVisible(false);
            buttonsPanel.add(nextButton);
            
            // Add components to panel
            panel.add(questionNumberLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(questionLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 25)));
            panel.add(answersPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
            panel.add(resultLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(explanationLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 25)));
            panel.add(buttonsPanel);
            
            return panel;
        }
        
        /**
         * Creates the results panel shown at the end of the quiz.
         */
        private JPanel createResultsPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(BACKGROUND_COLOR);
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            // Results header
            JLabel headerLabel = new JLabel("Quiz Complete!");
            headerLabel.setForeground(TEXT_COLOR);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
            headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Score display
            scoreLabel = new JLabel("Your score: 0 / 0");
            scoreLabel.setForeground(TEXT_COLOR);
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
            scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // XP earned text
            JLabel xpLabel = new JLabel("You earned 0 XP!");
            xpLabel.setForeground(ACCENT_COLOR);
            xpLabel.setFont(new Font("Arial", Font.BOLD, 18));
            xpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Feedback message
            JLabel feedbackLabel = new JLabel("Keep learning to improve your financial knowledge!");
            feedbackLabel.setForeground(new Color(180, 180, 180));
            feedbackLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Return message
            JLabel returnLabel = new JLabel("Click on quiz tab to return back");
            returnLabel.setForeground(new Color(180, 180, 180));
            returnLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            returnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Trophy image
            JPanel trophyPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw trophy
                    int size = Math.min(getWidth(), getHeight()) - 40;
                    int x = (getWidth() - size) / 2;
                    int y = (getHeight() - size) / 2;
                    
                    // Trophy cup
                    g2d.setColor(new Color(241, 196, 15));
                    g2d.fillRoundRect(x + size/4, y, size/2, size/2, 10, 10);
                    
                    // Trophy handles
                    g2d.fillOval(x + size/8, y + size/6, size/4, size/6);
                    g2d.fillOval(x + 5*size/8, y + size/6, size/4, size/6);
                    
                    // Trophy stem
                    g2d.fillRect(x + size/2 - size/10, y + size/2, size/5, size/3);
                    
                    // Trophy base
                    g2d.fillRoundRect(x + size/3, y + size/2 + size/3, size/3, size/8, 5, 5);
                    
                    g2d.dispose();
                }
                
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(150, 150);
                }
            };
            trophyPanel.setBackground(BACKGROUND_COLOR);
            trophyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Add components with spacing
            panel.add(Box.createVerticalGlue());
            panel.add(headerLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
            panel.add(trophyPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
            panel.add(scoreLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(xpLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
            panel.add(feedbackLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 30)));
            panel.add(returnLabel);
            panel.add(Box.createVerticalGlue());
            
            return panel;
        }
        
        /**
         * Updates the question panel with the current question.
         */
        private void updateQuestionPanel() {
            QuizQuestion question = quizController.getQuiz().getCurrentQuestion();
            if (question == null) {
                showResults();
                return;
            }
            
            // Reset state
            answerSubmitted = false;
            
            // Update question text
            int currentIndex = quizController.getQuiz().getCurrentQuestionIndex() + 1;
            int totalQuestions = quizController.getQuiz().getQuestionCount();
            questionNumberLabel.setText("Question " + currentIndex + "/" + totalQuestions);
            questionLabel.setText("<html><div style='width: 500px'>" + question.getQuestion() + "</div></html>");
            
            // Shuffle answers for the current question
            question.shuffleAnswers();
            
            // Update answers
            List<String> answers = question.getAnswers();
            for (int i = 0; i < answerButtons.size(); i++) {
                if (i < answers.size()) {
                    answerButtons.get(i).setText("<html><div style='width: 450px'>" + answers.get(i) + "</div></html>");
                    answerButtons.get(i).setVisible(true);
                    answerButtons.get(i).setSelected(false);  // Reset selection for new question
                    answerButtons.get(i).setForeground(TEXT_COLOR);
                    answerButtons.get(i).setBackground(FIELD_BACKGROUND);
                } else {
                    answerButtons.get(i).setVisible(false);
                }
            }
            
            // Reset result and explanation
            resultLabel.setVisible(false);
            explanationLabel.setVisible(false);
            
            // Show submit button, hide next button
            submitButton.setVisible(true);
            nextButton.setVisible(false);
        }
        
        /**
         * Shows the result of the current answer.
         */
        private void showAnswerResult(boolean correct) {
            QuizQuestion question = quizController.getQuiz().getCurrentQuestion();
            if (question == null) return;
            
            // Update answer buttons
            for (int i = 0; i < answerButtons.size(); i++) {
                JRadioButton button = answerButtons.get(i);
                if (i == question.getCorrectAnswerIndex()) {
                    button.setForeground(CORRECT_COLOR);
                } else if (i == selectedAnswerIndex && !correct) {
                    button.setForeground(INCORRECT_COLOR);
                }
            }
            
            // Show result message
            resultLabel.setText(correct ? "✓ Correct! You earned " + question.getPoints() + " XP" : "✗ Incorrect");
            resultLabel.setForeground(correct ? CORRECT_COLOR : INCORRECT_COLOR);
            resultLabel.setVisible(true);
            
            // Show explanation
            explanationLabel.setText("<html><div style='width: 500px'>" + question.getExplanation() + "</div></html>");
            explanationLabel.setVisible(true);
            
            // Update buttons
            submitButton.setVisible(false);
            nextButton.setVisible(true);
        }
        
        /**
         * Shows the results panel at the end of the quiz.
         */
        private void showResults() {
            int score = quizController.getQuiz().getScore();
            int maxScore = quizController.getQuiz().getMaximumScore();
            
            System.out.println("Quiz completed with score: " + score + "/" + maxScore);
            
            // Complete the quiz - this adds XP and checks quests
            quizController.completeQuiz();
            
            // Update score text
            scoreLabel.setText("Your score: " + score + " / " + maxScore);
            
            // Find the XP label and update it
            Component[] components = resultsPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JLabel) {
                    JLabel label = (JLabel) component;
                    if (label.getText().contains("XP")) {
                        label.setText("You earned " + score + " XP!");
                    }
                }
            }
            
            // Reset quiz in progress flag
            quizInProgress = false;
            
            // Show the results panel
            cardLayout.show(contentPanel, "RESULTS");
        }
        
        /**
         * Shows the category selection panel.
         */
        private void showCategorySelection() {
            // Reset all UI state
            quizInProgress = false;
            selectedAnswerIndex = -1;
            answerSubmitted = false;
            
            // Reset all answer buttons
            for (JRadioButton button : answerButtons) {
                button.setSelected(false);
                button.setForeground(TEXT_COLOR);
                button.setBackground(FIELD_BACKGROUND);
            }
            
            // Reset result and explanation labels
            resultLabel.setText("");
            resultLabel.setVisible(false);
            explanationLabel.setText("");
            explanationLabel.setVisible(false);
            
            // Reset buttons
            submitButton.setVisible(true);
            nextButton.setVisible(false);
            
            // Reset quiz state
            quizController.startNewQuiz();
            
            // Show category selection
            cardLayout.show(contentPanel, "CATEGORY");
        }
        
        /**
         * Creates a styled radio button for answer options.
         */
        private JRadioButton createAnswerRadioButton(String text) {
            JRadioButton button = new JRadioButton(text);
            button.setBackground(FIELD_BACKGROUND);
            button.setForeground(TEXT_COLOR);
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            return button;
        }
        
        /**
         * Creates a gradient button with custom styling.
         */
        private JButton createGradientButton(String text) {
            JButton button = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Create gradient from purple to blue
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(128, 90, 213),
                        getWidth(), getHeight(), new Color(90, 140, 255)
                    );
                    
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    
                    g2.setColor(TEXT_COLOR);
                    String text = getText();
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(text, x, y);
                    g2.dispose();
                }
            };
            
            button.setForeground(TEXT_COLOR);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setPreferredSize(new Dimension(180, 40));
            button.setMaximumSize(new Dimension(180, 40));
            
            return button;
        }
    }
