package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIUtils {
    // Common colors
    public static final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    public static final Color PANEL_COLOR = new Color(40, 24, 69);
    public static final Color TEXT_COLOR = new Color(255, 255, 255);
    public static final Color ACCENT_COLOR = new Color(128, 90, 213);
    public static final Color FIELD_BACKGROUND = new Color(50, 35, 80);
    public static final Color FIELD_BORDER = new Color(70, 50, 110);
    public static final Color HOVER_COLOR = new Color(60, 40, 100);

    // Common fonts
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font HEADING_FONT = new Font("Arial", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 12);

    /**
     * Creates a styled label with the given text and font.
     */
    public static JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    /**
     * Creates a styled label with the given text, font, and color.
     */
    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    /**
     * Creates a styled button with the given text.
     */
    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        styleButton(button);
        return button;
    }

    /**
     * Styles a button with common properties.
     */
    public static void styleButton(JButton button) {
        button.setBackground(ACCENT_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(BODY_FONT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Creates a styled text field.
     */
    public static JTextField createTextField() {
        JTextField textField = new JTextField();
        styleTextField(textField);
        return textField;
    }

    /**
     * Styles a text field with common properties.
     */
    public static void styleTextField(JTextField textField) {
        textField.setBackground(FIELD_BACKGROUND);
        textField.setForeground(TEXT_COLOR);
        textField.setCaretColor(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        textField.setFont(BODY_FONT);
    }

    /**
     * Creates a styled panel with the given layout.
     */
    public static JPanel createPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(PANEL_COLOR);
        return panel;
    }

    /**
     * Creates a styled card panel with hover effect.
     */
    public static JPanel createCardPanel(MouseAdapter clickListener) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (clickListener != null) {
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBackground(HOVER_COLOR);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBackground(PANEL_COLOR);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    clickListener.mouseClicked(e);
                }
            });
        }

        return card;
    }

    /**
     * Creates a styled header panel with title and subtitle.
     */
    public static JPanel createHeaderPanel(String title, String subtitle) {
        JPanel headerPanel = createPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = createLabel(title, TITLE_FONT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        if (subtitle != null) {
            JLabel subtitleLabel = createLabel(subtitle, SUBTITLE_FONT, new Color(180, 180, 180));
            headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        }

        return headerPanel;
    }

    /**
     * Creates a styled scroll pane.
     */
    public static JScrollPane createScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 80, 140);
                this.trackColor = BACKGROUND_COLOR;
            }
        });
        return scrollPane;
    }

    /**
     * Creates a styled combo box.
     */
    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setBackground(FIELD_BACKGROUND);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setFont(BODY_FONT);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ACCENT_COLOR : FIELD_BACKGROUND);
                setForeground(TEXT_COLOR);
                return this;
            }
        });
        return comboBox;
    }
} 