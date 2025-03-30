package view;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import javax.swing.JPanel;
import java.sql.SQLException;
import java.util.List;

import controller.LeaderboardController;
import model.LeaderboardEntry;

public class LeaderboardFX extends JPanel {
    private LeaderboardController controller;
    private JFXPanel fxPanel;
    
    // Colors based on the provided design
    private static final String BACKGROUND_COLOR = "#2E1869"; // Deep purple background
    private static final String HEADER_COLOR = "#FFDF00"; // Gold/Yellow for header
    private static final String TEXT_COLOR = "#FFFFFF"; // White text
    private static final String GOLD_COLOR = "#F7B618"; // Gold podium
    private static final String SILVER_COLOR = "#7A7A7A"; // Silver podium
    private static final String BRONZE_COLOR = "#CC7832"; // Bronze podium
    private static final String PODIUM_1_COLOR = "#F7B618"; // First place
    private static final String PODIUM_2_COLOR = "#C0C0C0"; // Second place
    private static final String PODIUM_3_COLOR = "#CD7F32"; // Third place
    private static final String CONTENDERS_BG = "#271453"; // Darker purple for contenders panel
    
    public LeaderboardFX(int userId) {
        try {
            controller = new LeaderboardController(userId);
            initializeUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initializeUI() {
        setLayout(new java.awt.BorderLayout());
        fxPanel = new JFXPanel();
        add(fxPanel, java.awt.BorderLayout.CENTER);
        
        // Initialize JavaFX components on JavaFX thread
        Platform.runLater(() -> createScene());
    }
    
    private void createScene() {
        try {
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
            
            // Header section
            VBox headerBox = createHeader();
            root.setTop(headerBox);
            
            // Podium section
            StackPane podiumPane = createPodium();
            root.setCenter(podiumPane);
            
            // Contenders section
            VBox contendersBox = createContenders();
            root.setBottom(contendersBox);
            
            Scene scene = new Scene(root, 800, 600);
            fxPanel.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private VBox createHeader() {
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20, 0, 10, 0));
        
        // Trophy icon (You would need to replace this with an actual icon)
        Label trophyIcon = new Label("🏆");
        trophyIcon.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        trophyIcon.setTextFill(Color.web(HEADER_COLOR));
        
        // Leaderboard title
        Label titleLabel = new Label("Leaderboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web(HEADER_COLOR));
        
        // Subtitle
        Label subtitleLabel = new Label("Top players this week");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.web(TEXT_COLOR));
        
        headerBox.getChildren().addAll(trophyIcon, titleLabel, subtitleLabel);
        return headerBox;
    }
    
    private StackPane createPodium() throws SQLException {
        StackPane podiumStack = new StackPane();
        podiumStack.setPadding(new Insets(20));
        
        // Get leaderboard data
        List<LeaderboardEntry> entries = controller.getLeaderboard();
        
        // Create the podium layout
        HBox podiumLayout = new HBox(20);
        podiumLayout.setAlignment(Pos.BOTTOM_CENTER);
        
        // Create podium positions
        VBox secondPlace = entries.size() > 1 ? createPodiumPosition(entries.get(1), 2) : createEmptyPodiumPosition(2);
        VBox firstPlace = entries.size() > 0 ? createPodiumPosition(entries.get(0), 1) : createEmptyPodiumPosition(1);
        VBox thirdPlace = entries.size() > 2 ? createPodiumPosition(entries.get(2), 3) : createEmptyPodiumPosition(3);
        
        // Add all positions to the layout
        podiumLayout.getChildren().addAll(secondPlace, firstPlace, thirdPlace);
        
        podiumStack.getChildren().add(podiumLayout);
        return podiumStack;
    }
    
    private VBox createPodiumPosition(LeaderboardEntry entry, int position) {
        VBox positionBox = new VBox(10);
        positionBox.setAlignment(Pos.BOTTOM_CENTER);
        
        // Player avatar (circle with initials)
        StackPane avatarPane = new StackPane();
        Circle avatarCircle = new Circle(40);
        
        // Set different colors based on position
        if (position == 1) {
            avatarCircle.setFill(Color.web("#ff5722")); // Orange for 1st
            // Crown for first place
            Label crownLabel = new Label("👑");
            crownLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
            crownLabel.setTranslateY(-50);
            avatarPane.getChildren().add(crownLabel);
        } else if (position == 2) {
            avatarCircle.setFill(Color.web("#4caf50")); // Green for 2nd
            // Medal for second place
            Label medalLabel = new Label("🥈");
            medalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            medalLabel.setTranslateY(-45);
            avatarPane.getChildren().add(medalLabel);
        } else {
            avatarCircle.setFill(Color.web("#2196f3")); // Blue for 3rd
            // Medal for third place
            Label medalLabel = new Label("🥉");
            medalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            medalLabel.setTranslateY(-45);
            avatarPane.getChildren().add(medalLabel);
        }
        
        // Get initials from username
        String username = entry.getUserName();
        String initials = username.length() > 0 ? 
            String.valueOf(username.charAt(0)).toUpperCase() : "?";
        if (username.contains(" ") && username.indexOf(" ") + 1 < username.length()) {
            initials += String.valueOf(username.charAt(username.indexOf(" ") + 1)).toUpperCase();
        }
        
        Label initialsLabel = new Label(initials);
        initialsLabel.setTextFill(Color.WHITE);
        initialsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        avatarPane.getChildren().addAll(avatarCircle, initialsLabel);
        
        // Username
        Label usernameLabel = new Label(entry.getUserName());
        usernameLabel.setTextFill(Color.WHITE);
        usernameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        // Level
        Label levelLabel = new Label("Level " + entry.getLevel());
        levelLabel.setTextFill(Color.WHITE);
        levelLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        
        // XP
        Label xpLabel = new Label(entry.getXp() + " XP");
        xpLabel.setTextFill(Color.WHITE);
        xpLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        
        // Podium block
        Rectangle podiumBlock = new Rectangle();
        podiumBlock.setWidth(100);
        
        // Height based on position
        if (position == 1) {
            podiumBlock.setHeight(140);
            podiumBlock.setFill(Color.web(PODIUM_1_COLOR));
        } else if (position == 2) {
            podiumBlock.setHeight(100);
            podiumBlock.setFill(Color.web(PODIUM_2_COLOR));
        } else {
            podiumBlock.setHeight(70);
            podiumBlock.setFill(Color.web(PODIUM_3_COLOR));
        }
        
        // Position number
        Label positionLabel = new Label(String.valueOf(position));
        positionLabel.setTextFill(Color.WHITE);
        positionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        StackPane podiumPane = new StackPane(podiumBlock, positionLabel);
        
        positionBox.getChildren().addAll(avatarPane, usernameLabel, levelLabel, xpLabel, podiumPane);
        return positionBox;
    }
    
    private VBox createEmptyPodiumPosition(int position) {
        VBox positionBox = new VBox(10);
        positionBox.setAlignment(Pos.BOTTOM_CENTER);
        
        // Empty avatar circle
        Circle avatarCircle = new Circle(40);
        avatarCircle.setFill(Color.web("#9E9E9E")); // Gray for empty
        avatarCircle.setOpacity(0.5);
        
        Label questionMark = new Label("?");
        questionMark.setTextFill(Color.WHITE);
        questionMark.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        StackPane avatarPane = new StackPane(avatarCircle, questionMark);
        
        // Podium block
        Rectangle podiumBlock = new Rectangle();
        podiumBlock.setWidth(100);
        
        // Height based on position
        if (position == 1) {
            podiumBlock.setHeight(140);
            podiumBlock.setFill(Color.web(PODIUM_1_COLOR));
            podiumBlock.setOpacity(0.5);
        } else if (position == 2) {
            podiumBlock.setHeight(100);
            podiumBlock.setFill(Color.web(PODIUM_2_COLOR));
            podiumBlock.setOpacity(0.5);
        } else {
            podiumBlock.setHeight(70);
            podiumBlock.setFill(Color.web(PODIUM_3_COLOR));
            podiumBlock.setOpacity(0.5);
        }
        
        // Position number
        Label positionLabel = new Label(String.valueOf(position));
        positionLabel.setTextFill(Color.WHITE);
        positionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        StackPane podiumPane = new StackPane(podiumBlock, positionLabel);
        
        positionBox.getChildren().addAll(avatarPane, podiumPane);
        return positionBox;
    }
    
    private VBox createContenders() throws SQLException {
        VBox contendersBox = new VBox(10);
        contendersBox.setPadding(new Insets(20));
        contendersBox.setStyle("-fx-background-color: " + CONTENDERS_BG + "; -fx-background-radius: 10;");
        
        // Header with icon
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        // User icon (placeholder)
        Label userIcon = new Label("👥");
        userIcon.setFont(Font.font("Arial", 18));
        userIcon.setTextFill(Color.WHITE);
        
        Label contendersLabel = new Label("Other Contenders");
        contendersLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        contendersLabel.setTextFill(Color.WHITE);
        
        headerBox.getChildren().addAll(userIcon, contendersLabel);
        contendersBox.getChildren().add(headerBox);
        
        // Get contenders (players after 3rd place)
        List<LeaderboardEntry> entries = controller.getLeaderboard();
        if (entries.size() <= 3) {
            Label noContendersLabel = new Label("No other contenders yet");
            noContendersLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            noContendersLabel.setTextFill(Color.WHITE);
            noContendersLabel.setPadding(new Insets(20, 0, 0, 0));
            noContendersLabel.setTextAlignment(TextAlignment.CENTER);
            contendersBox.getChildren().add(noContendersLabel);
        } else {
            for (int i = 3; i < Math.min(entries.size(), 10); i++) {
                HBox contenderRow = createContenderRow(entries.get(i));
                contendersBox.getChildren().add(contenderRow);
            }
        }
        
        return contendersBox;
    }
    
    private HBox createContenderRow(LeaderboardEntry entry) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 15, 10, 15));
        
        // Profile circle with initials
        String username = entry.getUserName();
        String initials = username.length() > 0 ? 
            String.valueOf(username.charAt(0)).toUpperCase() : "?";
        if (username.contains(" ") && username.indexOf(" ") + 1 < username.length()) {
            initials += String.valueOf(username.charAt(username.indexOf(" ") + 1)).toUpperCase();
        }
        
        Circle profileCircle = new Circle(15);
        profileCircle.setFill(Color.web("#673AB7")); // Purple
        
        Label initialsLabel = new Label(initials);
        initialsLabel.setTextFill(Color.WHITE);
        initialsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        StackPane profilePane = new StackPane(profileCircle, initialsLabel);
        
        // Username
        Label usernameLabel = new Label(entry.getUserName());
        usernameLabel.setTextFill(Color.WHITE);
        usernameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        HBox.setHgrow(usernameLabel, Priority.ALWAYS);
        
        // Level
        Label levelLabel = new Label("Level " + entry.getLevel());
        levelLabel.setTextFill(Color.web("#B39DDB")); // Light purple
        levelLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        
        // XP
        Label xpLabel = new Label(entry.getXp() + " XP");
        xpLabel.setTextFill(Color.web("#B39DDB")); // Light purple
        xpLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        
        row.getChildren().addAll(profilePane, usernameLabel, levelLabel, xpLabel);
        return row;
    }
    
    public void refreshLeaderboard() {
        Platform.runLater(() -> {
            try {
                createScene();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
} 