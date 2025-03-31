package model;

/**
 * Represents a financial tip with title, content, category, and date.
 */
public class FinancialTip {
    private String title;
    private String content;
    private String category;
    private String date;

    /**
     * Creates a new financial tip.
     *
     * @param title The title of the tip
     * @param content The content/description of the tip
     * @param category The category of the tip
     * @param date The date the tip was added
     */
    public FinancialTip(String title, String content, String category, String date) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.date = date;
    }

    /**
     * Gets the title of the tip.
     *
     * @return The tip title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the content of the tip.
     *
     * @return The tip content
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the category of the tip.
     *
     * @return The tip category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Gets the date of the tip.
     *
     * @return The tip date
     */
    public String getDate() {
        return date;
    }
} 