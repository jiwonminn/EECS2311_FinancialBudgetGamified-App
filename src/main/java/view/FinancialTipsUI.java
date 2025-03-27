package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

/**
 * UI component for displaying financial tips.
 */
public class FinancialTipsUI extends JPanel {
    // Define colors to match the application theme
    private final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    private final Color PANEL_COLOR = new Color(40, 24, 69);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color ACCENT_COLOR = new Color(128, 90, 213);
    private final Color FIELD_BACKGROUND = new Color(50, 35, 80);
    private final Color FIELD_BORDER = new Color(70, 50, 110);
    
    private JPanel mainPanel;
    private Random random;
    private Map<String, String[]> tipsByCategory;
    
    /**
     * Creates a new FinancialTipsUI.
     */
    public FinancialTipsUI() {
        this.random = new Random();
        initializeTips();
        setupUI();
    }
    
    /**
     * Initializes the tips for each category.
     */
    private void initializeTips() {
        tipsByCategory = new HashMap<>();
        
        // Budgeting tips
        tipsByCategory.put("Budgeting", new String[] {
            "Create a monthly budget and stick to it. Track your expenses to identify areas where you can cut back.",
            "Use the 50/30/20 rule: 50% for needs, 30% for wants, and 20% for savings and debt repayment.",
            "Review your subscriptions regularly and cancel any that you don't use frequently.",
            "Plan your meals and grocery shopping to avoid impulse purchases and reduce food waste.",
            "Set up automatic bill payments to avoid late fees and maintain a good credit score.",
            "Use cash-back or rewards credit cards for everyday purchases, but pay in full each month.",
            "Create a sinking fund for irregular expenses like car maintenance or holiday gifts.",
            "Track your spending habits for at least a month to understand your financial patterns.",
            "Use budgeting apps to automate expense tracking and categorization.",
            "Set realistic spending limits for discretionary categories like entertainment and dining out."
        });
        
        // Saving tips
        tipsByCategory.put("Saving", new String[] {
            "Start saving early! Even small amounts can grow significantly over time thanks to compound interest.",
            "Build an emergency fund with 3-6 months of living expenses to handle unexpected financial challenges.",
            "Set up automatic transfers to your savings account on payday to make saving a priority.",
            "Use separate savings accounts for different goals (emergency fund, vacation, down payment, etc.).",
            "Take advantage of high-yield savings accounts to earn more interest on your savings.",
            "Save windfalls like tax refunds or bonuses instead of spending them.",
            "Use the 'pay yourself first' principle by saving before paying other expenses.",
            "Create a savings challenge to make saving more fun and engaging.",
            "Review and adjust your savings goals regularly based on your changing needs.",
            "Consider opening a certificate of deposit (CD) for longer-term savings goals."
        });
        
        // Investing tips
        tipsByCategory.put("Investing", new String[] {
            "Diversify your investments to spread risk across different asset classes and sectors.",
            "Start investing early to take advantage of compound growth over time.",
            "Consider low-cost index funds for long-term investment growth.",
            "Don't try to time the market - focus on time in the market instead.",
            "Regularly rebalance your investment portfolio to maintain your desired asset allocation.",
            "Take advantage of tax-advantaged accounts like 401(k)s and IRAs.",
            "Understand your risk tolerance before making investment decisions.",
            "Keep investment costs low by avoiding unnecessary trading and high-fee funds.",
            "Stay informed about market trends but avoid making emotional investment decisions.",
            "Consider working with a financial advisor for personalized investment guidance."
        });
        
        // Credit tips
        tipsByCategory.put("Credit", new String[] {
            "Pay off high-interest debt first using the 'debt avalanche' method to save on interest payments.",
            "Keep your credit utilization below 30% to maintain a good credit score.",
            "Review your credit report annually to check for errors and maintain a good credit score.",
            "Consider using cash-back or rewards credit cards for everyday purchases, but pay in full each month.",
            "Avoid opening too many credit accounts at once, as this can negatively impact your credit score.",
            "Set up automatic payments to avoid late fees and maintain a good payment history.",
            "Understand the terms and conditions of your credit cards and loans.",
            "Use credit cards responsibly and avoid carrying a balance when possible.",
            "Monitor your credit score regularly to track your progress.",
            "Consider debt consolidation if you have multiple high-interest debts."
        });
        
        // Financial Planning tips
        tipsByCategory.put("Financial Planning", new String[] {
            "Set specific financial goals with timelines to stay motivated and track progress.",
            "Create a comprehensive financial plan that includes short-term and long-term goals.",
            "Regularly review and adjust your financial plan as your circumstances change.",
            "Consider working with a financial advisor for personalized guidance.",
            "Plan for major life events (marriage, children, retirement) well in advance.",
            "Create a will and update it regularly to protect your assets.",
            "Build a diversified portfolio that aligns with your financial goals.",
            "Plan for unexpected expenses by maintaining adequate insurance coverage.",
            "Set up a system for tracking and managing your financial documents.",
            "Regularly review your financial progress and celebrate your achievements."
        });
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
        
        // Create main content panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Add category cards
        addCategoryCards();
        
        // Create scroll pane for main content
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 80, 140);
                this.trackColor = new Color(24, 15, 41);
            }
        });
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Creates the header panel with title.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Financial Wisdom");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel subtitleLabel = new JLabel("Explore tips and advice for different aspects of personal finance");
        subtitleLabel.setForeground(new Color(180, 180, 180));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return headerPanel;
    }
    
    /**
     * Adds category cards to the main panel.
     */
    private void addCategoryCards() {
        // Create a grid panel for categories
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        gridPanel.setBackground(BACKGROUND_COLOR);
        
        // Add a card for each category
        for (String category : tipsByCategory.keySet()) {
            JPanel card = createCategoryCard(category);
            gridPanel.add(card);
        }
        
        mainPanel.add(gridPanel);
    }
    
    /**
     * Creates a card for a category.
     */
    private JPanel createCategoryCard(String category) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Category icon
        JLabel iconLabel = new JLabel(getCategoryIcon(category));
        iconLabel.setFont(new Font("Arial", Font.BOLD, 32));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Category name
        JLabel nameLabel = new JLabel(category);
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Description
        JLabel descLabel = new JLabel("<html><div style='width: 200px; text-align: center'>" + 
            getCategoryDescription(category) + "</div></html>");
        descLabel.setForeground(new Color(180, 180, 180));
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components
        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(descLabel);
        
        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(50, 35, 90));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(PANEL_COLOR);
            }
            
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showCategoryTips(category);
            }
        });
        
        return card;
    }
    
    /**
     * Shows tips for a specific category.
     */
    private void showCategoryTips(String category) {
        // Remove all components from main panel
        mainPanel.removeAll();
        
        // Create back button
        JButton backButton = createGradientButton("← Back to Categories");
        backButton.addActionListener(e -> {
            mainPanel.removeAll();
            addCategoryCards();
            mainPanel.revalidate();
            mainPanel.repaint();
        });
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel(getCategoryIcon(category) + " " + category + " Tips");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
        
        mainPanel.add(headerPanel);
        
        // Add tips
        String[] tips = tipsByCategory.get(category);
        for (String tip : tips) {
            JPanel tipCard = createTipCard(tip);
            mainPanel.add(tipCard);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    /**
     * Creates a card for a single tip.
     */
    private JPanel createTipCard(String tip) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel tipLabel = new JLabel("<html><div style='width: 500px'>" + tip + "</div></html>");
        tipLabel.setForeground(TEXT_COLOR);
        tipLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        card.add(tipLabel);
        
        return card;
    }
    
    /**
     * Gets the icon for a category.
     */
    private String getCategoryIcon(String category) {
        switch (category) {
            case "Budgeting": return "💰";
            case "Saving": return "🏦";
            case "Investing": return "📈";
            case "Credit": return "💳";
            case "Financial Planning": return "🎯";
            default: return "💡";
        }
    }
    
    /**
     * Gets the description for a category.
     */
    private String getCategoryDescription(String category) {
        switch (category) {
            case "Budgeting": return "Learn how to create and maintain an effective budget";
            case "Saving": return "Discover strategies to build and grow your savings";
            case "Investing": return "Understand the basics of investing and portfolio management";
            case "Credit": return "Master credit management and improve your credit score";
            case "Financial Planning": return "Plan for your financial future with comprehensive strategies";
            default: return "General financial advice and tips";
        }
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