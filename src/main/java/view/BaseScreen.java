package view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public abstract class BaseScreen extends JFrame {
    protected final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    protected final Color PANEL_COLOR = new Color(40, 24, 69);
    protected final Color TEXT_COLOR = new Color(255, 255, 255);
    protected final Color ACCENT_COLOR = new Color(128, 90, 213);
    protected final Color FIELD_BACKGROUND = new Color(50, 35, 80);
    protected final Color FIELD_BORDER = new Color(70, 50, 110);

    protected int fieldWidth;

    public BaseScreen(String title) {
        setUndecorated(true);
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setOpacity(0.5f);
        getContentPane().setBackground(BACKGROUND_COLOR);
        fieldWidth = Math.max(300, (int) (getWidth() * 0.3));
    }

    protected JPanel createExitButtonPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        JButton exitButton = new JButton("X");
        exitButton.setForeground(TEXT_COLOR);
        exitButton.setFont(new Font("Arial", Font.BOLD, 18));
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.addActionListener(e -> System.exit(0));
        topPanel.add(exitButton);
        return topPanel;
    }

    protected JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(FIELD_BACKGROUND);
        textField.setForeground(TEXT_COLOR);
        textField.setCaretColor(TEXT_COLOR);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        textField.setMaximumSize(new Dimension(fieldWidth, 40));
        textField.setPreferredSize(new Dimension(fieldWidth, 40));
        return textField;
    }

    protected JPasswordField createPasswordField() {
        JPasswordField passField = new JPasswordField();
        passField.setBackground(FIELD_BACKGROUND);
        passField.setForeground(TEXT_COLOR);
        passField.setCaretColor(TEXT_COLOR);
        passField.setHorizontalAlignment(JTextField.CENTER);
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        passField.setMaximumSize(new Dimension(fieldWidth, 40));
        passField.setPreferredSize(new Dimension(fieldWidth, 40));
        return passField;
    }

    protected JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_COLOR);
        JLabel label = new JLabel(labelText);
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fieldPanel.setBackground(PANEL_COLOR);
        fieldPanel.add(field);
        panel.add(fieldPanel);
        return panel;
    }

    protected JButton createGradientButton(String text, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(128, 90, 213),
                        getWidth(), getHeight(), new Color(90, 140, 255)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(TEXT_COLOR);
                String btnText = getText();
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(btnText)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(btnText, x, y);
                g2.dispose();
            }
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(fieldWidth, height);
            }
        };
        button.setForeground(TEXT_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    protected JLabel createShieldIcon() {
        return new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Ellipse2D circle = new Ellipse2D.Double(0, 0, 70, 70);
                g2d.setColor(ACCENT_COLOR);
                g2d.fill(circle);
                g2d.setColor(TEXT_COLOR);
                int shieldWidth = 30;
                int shieldHeight = 40;
                int x = (70 - shieldWidth) / 2;
                int y = (70 - shieldHeight) / 2;
                g2d.setStroke(new BasicStroke(2f));
                g2d.fillRoundRect(x, y, shieldWidth, shieldHeight, 10, 10);
                g2d.dispose();
            }
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(70, 70);
            }
        };
    }

    protected JPanel createIconPanel() {
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconPanel.setBackground(PANEL_COLOR);
        iconPanel.add(createShieldIcon());
        return iconPanel;
    }

    protected JPanel createWrapperPanel(JPanel mainPanel) {
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(BACKGROUND_COLOR);
        wrapperPanel.add(mainPanel);
        return wrapperPanel;
    }

    protected void fadeIn(float startOpacity) {
        setOpacity(startOpacity);
        Timer timer = new Timer(50, null);
        final float[] opacityValue = {startOpacity};
        timer.addActionListener(e -> {
            opacityValue[0] += 0.05f;
            if (opacityValue[0] >= 1f) {
                opacityValue[0] = 1f;
                timer.stop();
            }
            setOpacity(opacityValue[0]);
        });
        timer.start();
    }

    /**
     * Fades out the current screen to 0.5 opacity, then runs the provided switchAction.
     */
    protected void fadeOutAndSwitch(Runnable switchAction) {
        Timer timer = new Timer(50, null);
        final float[] opacityValue = {getOpacity()};
        timer.addActionListener(e -> {
            opacityValue[0] -= 0.05f;
            if (opacityValue[0] <= 0.5f) {
                opacityValue[0] = 0.5f;
                timer.stop();
                dispose();
                SwingUtilities.invokeLater(switchAction);
            }
            setOpacity(opacityValue[0]);
        });
        timer.start();
    }
}
