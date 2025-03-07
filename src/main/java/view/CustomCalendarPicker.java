package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * A custom calendar picker component that provides a dropdown calendar for date selection.
 */
public class CustomCalendarPicker extends JPanel {
    // Colors to match the dark theme in the image
    private final Color BACKGROUND_COLOR = new Color(18, 18, 18);
    private final Color PANEL_COLOR = new Color(25, 25, 25);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color ACCENT_COLOR = new Color(128, 90, 213);
    private final Color FIELD_BACKGROUND = new Color(35, 35, 35);
    private final Color FIELD_BORDER = new Color(50, 50, 50);
    private final Color TODAY_HIGHLIGHT = new Color(39, 174, 96);
    private final Color SELECTED_HIGHLIGHT = new Color(128, 90, 213);
    private final Color DISABLED_TEXT = new Color(120, 120, 120);
    
    private JTextField displayField;
    private JButton calendarButton;
    private JDialog calendarDialog;
    private Calendar selectedDate;
    private Calendar currentMonthCalendar;
    private JPanel monthYearPanel;
    private JTable calendarTable;
    private SimpleDateFormat dateFormat;
    private List<ChangeListener> changeListeners;
    
    private String[] monthNames = {"January", "February", "March", "April", "May", "June", 
                                   "July", "August", "September", "October", "November", "December"};
    private String[] dayNames = {"S", "M", "T", "W", "T", "F", "S"};
    
    // Two-dimensional array to store date information for each cell
    private Calendar[][] dateCells = new Calendar[6][7];
    
    /**
     * Creates a new CustomCalendarPicker with the current date selected.
     */
    public CustomCalendarPicker() {
        this(Calendar.getInstance());
    }
    
    /**
     * Creates a new CustomCalendarPicker with the specified date selected.
     * @param initialDate The initial date to select
     */
    public CustomCalendarPicker(Calendar initialDate) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder());
        setBackground(PANEL_COLOR);
        
        changeListeners = new ArrayList<>();
        selectedDate = initialDate;
        currentMonthCalendar = (Calendar) initialDate.clone();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        
        // Initialize UI components
        displayField = new JTextField();
        displayField.setEditable(false);
        displayField.setBackground(FIELD_BACKGROUND);
        displayField.setForeground(TEXT_COLOR);
        displayField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(FIELD_BORDER, 1),
                new EmptyBorder(8, 10, 8, 10)));
        displayField.setText(dateFormat.format(selectedDate.getTime()));
        
        calendarButton = new JButton("📅");
        calendarButton.setFocusPainted(false);
        calendarButton.setBorderPainted(false);
        calendarButton.setContentAreaFilled(false);
        calendarButton.setForeground(TEXT_COLOR);
        calendarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calendarButton.addActionListener(e -> showCalendarDialog());
        
        // Add components to panel
        add(displayField, BorderLayout.CENTER);
        add(calendarButton, BorderLayout.EAST);
        
        // Initialize the calendar dialog
        initCalendarDialog();
    }
    
    /**
     * Initialize the calendar dialog that pops up when the calendar button is clicked.
     */
    private void initCalendarDialog() {
        calendarDialog = new JDialog((Frame) null, "Select Date", true);
        calendarDialog.setUndecorated(true);
        calendarDialog.setResizable(false);
        calendarDialog.getContentPane().setBackground(BACKGROUND_COLOR);
        calendarDialog.getContentPane().setLayout(new BorderLayout());
        
        // Month and Year panel with navigation arrows
        monthYearPanel = new JPanel(new BorderLayout());
        monthYearPanel.setBackground(BACKGROUND_COLOR);
        monthYearPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
        
        JPanel monthYearContent = new JPanel(new BorderLayout(10, 0));
        monthYearContent.setBackground(BACKGROUND_COLOR);
        
        JLabel monthYearLabel = new JLabel("", JLabel.CENTER);
        monthYearLabel.setForeground(TEXT_COLOR);
        monthYearLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JPanel arrowsPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        arrowsPanel.setBackground(BACKGROUND_COLOR);
        
        // Left arrow button
        JButton prevMonthButton = createNavButton("<");
        prevMonthButton.addActionListener(e -> {
            currentMonthCalendar.add(Calendar.MONTH, -1);
            updateCalendarPanel();
        });
        
        // Right arrow button
        JButton nextMonthButton = createNavButton(">");
        nextMonthButton.addActionListener(e -> {
            currentMonthCalendar.add(Calendar.MONTH, 1);
            updateCalendarPanel();
        });
        
        arrowsPanel.add(prevMonthButton);
        arrowsPanel.add(nextMonthButton);
        
        monthYearContent.add(monthYearLabel, BorderLayout.CENTER);
        monthYearContent.add(arrowsPanel, BorderLayout.EAST);
        
        monthYearPanel.add(monthYearContent, BorderLayout.CENTER);
        
        // Calendar days table - match the grid layout in the image
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add column names (days of week)
        for (String day : dayNames) {
            model.addColumn(day);
        }
        
        // Add empty rows for the dates
        for (int i = 0; i < 6; i++) {
            model.addRow(new Object[7]);
        }
        
        calendarTable = new JTable(model);
        calendarTable.setRowHeight(40); // Taller rows to match image
        calendarTable.setFillsViewportHeight(true);
        calendarTable.setBackground(BACKGROUND_COLOR);
        calendarTable.setForeground(TEXT_COLOR);
        calendarTable.setGridColor(new Color(40, 40, 40)); // Subtle grid
        calendarTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        calendarTable.setRowSelectionAllowed(false);
        calendarTable.setCellSelectionEnabled(true);
        calendarTable.setShowGrid(false); // No grid lines, cleaner look
        calendarTable.setIntercellSpacing(new Dimension(5, 5)); // Space between cells
        
        // Remove the table header (we'll create our own)
        calendarTable.setTableHeader(null);
        
        // Custom cell renderer for the dates (to show circular selection)
        calendarTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = new JLabel();
                if (value != null) {
                    // Only use the integer value for display
                    if (value instanceof Integer) {
                        label.setText(value.toString());
                    } else if (value instanceof String) {
                        try {
                            // Try to parse as integer for display
                            label.setText(String.valueOf(Integer.parseInt(value.toString())));
                        } catch (NumberFormatException e) {
                            // Just display the string value if it's not a number
                            label.setText(value.toString());
                        }
                    }
                }
                
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setVerticalAlignment(JLabel.CENTER);
                label.setForeground(TEXT_COLOR);
                label.setBackground(BACKGROUND_COLOR);
                label.setOpaque(true);
                
                // Get the date for this cell
                Calendar cellDate = dateCells[row][column];
                if (cellDate != null) {
                    // Check if this is the selected date (draw circle around it)
                    if (isSameDay(cellDate, selectedDate)) {
                        label = new JLabel(label.getText()) {
                            @Override
                            protected void paintComponent(Graphics g) {
                                Graphics2D g2 = (Graphics2D) g.create();
                                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                
                                // Draw the circle for selected date
                                g2.setColor(SELECTED_HIGHLIGHT);
                                int size = Math.min(getWidth(), getHeight()) - 8;
                                int x = (getWidth() - size) / 2;
                                int y = (getHeight() - size) / 2;
                                g2.draw(new Ellipse2D.Double(x, y, size, size));
                                
                                // Draw the text
                                g2.setColor(TEXT_COLOR);
                                super.paintComponent(g2);
                                g2.dispose();
                            }
                        };
                        label.setHorizontalAlignment(JLabel.CENTER);
                        label.setOpaque(false);
                    }
                    
                    // Today's date (could add a subtle indicator)
                    if (isSameDay(cellDate, Calendar.getInstance())) {
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                    }
                    
                    // Dates from other months are grayed out
                    if (cellDate.get(Calendar.MONTH) != currentMonthCalendar.get(Calendar.MONTH)) {
                        label.setForeground(DISABLED_TEXT);
                    }
                }
                
                return label;
            }
        });
        
        // Handle date selection
        calendarTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = calendarTable.getSelectedRow();
                int column = calendarTable.getSelectedColumn();
                
                // Ensure a valid cell was clicked
                if (row >= 0 && column >= 0 && calendarTable.getValueAt(row, column) != null) {
                    // Get the selected date
                    Calendar newDate = dateCells[row][column];
                    if (newDate != null) {
                        selectedDate = newDate;
                        displayField.setText(dateFormat.format(selectedDate.getTime()));
                        calendarDialog.setVisible(false);
                        
                        // Notify listeners
                        notifyChangeListeners();
                    }
                }
            }
        });
        
        // Create custom header panel for days of week
        JPanel headerPanel = new JPanel(new GridLayout(1, 7, 5, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(5, 5, 10, 5));
        
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, JLabel.CENTER);
            dayLabel.setForeground(TEXT_COLOR);
            dayLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            headerPanel.add(dayLabel);
        }
        
        // Create a main panel for the calendar
        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBackground(BACKGROUND_COLOR);
        calendarPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        
        // Add components to the calendar panel
        calendarPanel.add(monthYearPanel, BorderLayout.NORTH);
        calendarPanel.add(headerPanel, BorderLayout.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(calendarTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        
        calendarPanel.add(scrollPane, BorderLayout.SOUTH);
        
        // Add the calendar panel to the dialog
        calendarDialog.getContentPane().add(calendarPanel, BorderLayout.CENTER);
        calendarDialog.pack();
        
        // Set initial month/year label
        monthYearLabel.setText(getMonthYearString());
        
        // Initialize the date cells array
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                dateCells[i][j] = null;
            }
        }
    }
    
    /**
     * Create a navigation button with consistent styling
     */
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(TEXT_COLOR);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    /**
     * Shows the calendar dialog to select a date.
     */
    private void showCalendarDialog() {
        updateCalendarPanel();
        
        // Position the dialog below the date field
        Point location = displayField.getLocationOnScreen();
        calendarDialog.setLocation(
            location.x, 
            location.y + displayField.getHeight()
        );
        
        calendarDialog.setVisible(true);
    }
    
    /**
     * Updates the calendar panel to show the current month.
     */
    private void updateCalendarPanel() {
        // Update month-year label
        for (Component comp : monthYearPanel.getComponents()) {
            if (comp instanceof JPanel) {
                for (Component innerComp : ((JPanel)comp).getComponents()) {
                    if (innerComp instanceof JLabel) {
                        ((JLabel) innerComp).setText(getMonthYearString());
                        break;
                    }
                }
            }
        }
        
        // Clear the table
        DefaultTableModel model = (DefaultTableModel) calendarTable.getModel();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                model.setValueAt(null, i, j);
                dateCells[i][j] = null;
            }
        }
        
        // Get the first day of the month
        Calendar calendar = (Calendar) currentMonthCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        
        // Determine the day of week for the first day
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Adjust for Sunday = 0
        
        // Add days from the previous month
        Calendar prevMonthCal = (Calendar) calendar.clone();
        prevMonthCal.add(Calendar.MONTH, -1);
        int daysInPrevMonth = prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        for (int i = 0; i < firstDayOfWeek; i++) {
            int day = daysInPrevMonth - firstDayOfWeek + i + 1;
            model.setValueAt(day, 0, i);
            
            Calendar date = (Calendar) prevMonthCal.clone();
            date.set(Calendar.DAY_OF_MONTH, day);
            dateCells[0][i] = date;
        }
        
        // Add days for the current month
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int row = 0;
        int column = firstDayOfWeek;
        
        for (int day = 1; day <= daysInMonth; day++) {
            model.setValueAt(day, row, column);
            
            Calendar date = (Calendar) calendar.clone();
            date.set(Calendar.DAY_OF_MONTH, day);
            dateCells[row][column] = date;
            
            column++;
            if (column > 6) {
                column = 0;
                row++;
            }
        }
        
        // Add days for the next month
        Calendar nextMonthCal = (Calendar) calendar.clone();
        nextMonthCal.add(Calendar.MONTH, 1);
        int nextMonthDay = 1;
        
        while (row < 6) {
            model.setValueAt(nextMonthDay, row, column);
            
            Calendar date = (Calendar) nextMonthCal.clone();
            date.set(Calendar.DAY_OF_MONTH, nextMonthDay);
            dateCells[row][column] = date;
            
            nextMonthDay++;
            column++;
            if (column > 6) {
                column = 0;
                row++;
            }
        }
        
        calendarTable.repaint();
    }
    
    /**
     * Gets the month and year string for the current month.
     */
    private String getMonthYearString() {
        return monthNames[currentMonthCalendar.get(Calendar.MONTH)] + " " + currentMonthCalendar.get(Calendar.YEAR);
    }
    
    /**
     * Checks if two Calendar instances represent the same day.
     */
    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
    
    /**
     * Gets the selected date.
     */
    public Date getDate() {
        return selectedDate.getTime();
    }
    
    /**
     * Sets the selected date.
     */
    public void setDate(Date date) {
        selectedDate.setTime(date);
        currentMonthCalendar.setTime(date);
        displayField.setText(dateFormat.format(date));
        
        // Notify listeners
        notifyChangeListeners();
    }
    
    /**
     * Adds a change listener that will be notified when the date is changed.
     */
    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }
    
    /**
     * Removes a change listener.
     */
    public void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }
    
    /**
     * Notifies all change listeners that the date has changed.
     */
    private void notifyChangeListeners() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(event);
        }
    }
    
    /**
     * Sets the date format to use for displaying dates.
     */
    public void setDateFormat(String pattern) {
        this.dateFormat = new SimpleDateFormat(pattern);
        displayField.setText(dateFormat.format(selectedDate.getTime()));
    }
} 