package com.example.triviagame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.Random;

public class HelloApplication extends Application {

    // Array of 5 Trivia objects
    private Trivia[] triviaArray = new Trivia[5];

    // Each question has 4  answers
    private String[][] choices = new String[5][4];

    // Random order for displaying questions
    private int[] order = new int[5];

    private int currentIndex = 0;   // which question in order[]
    private int totalScore = 0;     // total points
    private String[] userAnswers = new String[5]; // store player's answers

    // UI components
    private Label questionLabel;
    private Label[] choiceLabels = new Label[4];
    private TextField answerField;
    private Label feedbackLabel;
    private Label scoreLabel;

    private Button submitButton;
    private boolean answeredCurrent = false;  //

    @Override
    public void start(Stage stage) {

        // Create and store 5 Trivia objects in a binary file
        writeTriviaFile();

        // Read Trivia objects into array
        readTriviaFile();

        // Initialize the four possible answers for each question
        initChoices();

        // Set up random question order
        initRandomOrder();

        // Build the UI
        StackPane root = buildUI();

        // Show first question
        showQuestion();

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Sarah Nasser's Trivia Game");
        stage.setScene(scene);
        stage.show();
    }

    // ===================== FILE OPERATIONS ===================== //


    private void writeTriviaFile() {
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream("trivia.dat"));

            // answers are kept to 1–2 words maximum
            out.writeObject(new Trivia("Who was the first U.S. president?", "George Washington", 1));
            out.writeObject(new Trivia("What is the largest planet in our solar system?", "Jupiter", 1));
            out.writeObject(new Trivia("Which ocean is the largest?", "Pacific Ocean", 2));
            out.writeObject(new Trivia("How many sides does an octagon have?", "Eight", 1));
            out.writeObject(new Trivia("What is the largest organ in the body?", "Skin", 1));

            out.close();
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }


    private void readTriviaFile() {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream("trivia.dat"));

            for (int i = 0; i < triviaArray.length; i++) {
                triviaArray[i] = (Trivia) in.readObject();
            }

            in.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // ===================== GAME SETUP ===================== //

    // Four possible answers per question
    private void initChoices() {
        // Question 0
        choices[0][0] = "Abraham Lincoln";
        choices[0][1] = "George Washington";        // correct
        choices[0][2] = "Thomas Jefferson";
        choices[0][3] = "John Adams";

        // Question 1
        choices[1][0] = "Neptune";
        choices[1][1] = "Earth";
        choices[1][2] = "Jupiter";      // correct
        choices[1][3] = "Saturn";

        // Question 2
        choices[2][0] = "Pacific Ocean";   // correct
        choices[2][1] = "Atlantic Ocean";
        choices[2][2] = "Indian Ocean";
        choices[2][3] = "Arctic Ocean";

        // Question 3
        choices[3][0] = "Nine";
        choices[3][1] = "Eight";        // correct
        choices[3][2] = "Seven";
        choices[3][3] = "Five";

        // Question 4
        choices[4][0] = "Lungs";
        choices[4][1] = "Liver";
        choices[4][2] = "Heart";
        choices[4][3] = "Skin";     // correct
    }

    //  random order for the questions
    private void initRandomOrder() {
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }

        Random rand = new Random();
        for (int i = order.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = order[i];
            order[i] = order[j];
            order[j] = temp;
        }
    }

    // ==========================================

    private StackPane buildUI() {

        // MAIN GridPane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setAlignment(Pos.TOP_CENTER);

        grid.setStyle(
                "-fx-background-color: #f0f6ff;" +
                        "-fx-border-color: #c7d9ff;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;"
        );

        // QUESTION  STYLING
        questionLabel = new Label("Question");
        questionLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #003366;"
        );
        questionLabel.setWrapText(true);

        grid.add(questionLabel, 0, 0, 2, 1);

        // MULTIPLE-CHOICE LABELS
        for (int i = 0; i < 4; i++) {
            choiceLabels[i] = new Label("Choice " + (i + 1));
            choiceLabels[i].setStyle("-fx-font-size: 15px; -fx-text-fill: #003366;");
            grid.add(choiceLabels[i], 0, i + 1, 2, 1);
        }

        // USER INPUT
        Label promptLabel = new Label("Type your answer (1–2 words):");
        promptLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #003366;");

        answerField = new TextField();
        answerField.setStyle(
                "-fx-background-radius: 8px;" +
                        "-fx-padding: 6px;"
        );

        // SUBMIT BUTTON
        submitButton = new Button("Submit");
        submitButton.setStyle(
                "-fx-background-color: #4fa3ff;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-padding: 8px 16px;"
        );
        submitButton.setOnAction(e -> handleSubmit());

        // FEEDBACK & SCORE
        feedbackLabel = new Label();
        feedbackLabel.setStyle("-fx-text-fill: #cc3300; -fx-font-size: 14px;");

        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #003366;");

        // PUT INPUT & FEEDBACK
        VBox bottomBox = new VBox(12, promptLabel, answerField, submitButton, feedbackLabel, scoreLabel);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));

        // MAIN LAYOUT
        VBox mainBox = new VBox(15, grid, bottomBox);
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.setStyle("-fx-background-color: #e9f1ff;");  // soft solid background color

        // STACKPANE WRAPPER
        StackPane root = new StackPane(mainBox);
        root.setPadding(new Insets(15));

        return root;
    }

    // Show current question (using the random order)
    private void showQuestion() {
        if (currentIndex >= order.length) {
            endGame();
            return;
        }

        int qIndex = order[currentIndex];
        Trivia t = triviaArray[qIndex];

        questionLabel.setText("Q" + (currentIndex + 1) + ": " + t.getQuestion());

        for (int i = 0; i < 4; i++) {
            char letter = (char) ('A' + i);
            choiceLabels[i].setText(letter + ") " + choices[qIndex][i]);
        }

        feedbackLabel.setText("");
        answerField.clear();

        answeredCurrent = false;
        submitButton.setText("Submit");
    }

    // ===================== GAMEPLAY ===================== //

    private void handleSubmit() {
        if (currentIndex >= order.length) {
            return;
        }

        //  already graded this question, this click means "Next"
        if (answeredCurrent) {
            currentIndex++;
            showQuestion();
            return;
        }

        // Otherwise, we're grading the answer now
        String userText = answerField.getText().trim();

        if (userText.isEmpty()) {
            feedbackLabel.setText("Please enter an answer.");
            return;
        }

        // Restrict to one or two words
        String[] words = userText.split("\\s+");
        if (words.length > 2) {
            feedbackLabel.setText("Answer must be one or two words only.");
            return;
        }

        int qIndex = order[currentIndex];
        Trivia current = triviaArray[qIndex];

        userAnswers[qIndex] = userText; // store answer

        if (userText.equalsIgnoreCase(current.getAnswer())) {
            totalScore += current.getPoints();
            feedbackLabel.setText("Correct! You earned " + current.getPoints() + " point(s).");
        } else {
            feedbackLabel.setText("Incorrect. Correct answer: " + current.getAnswer());
        }

        scoreLabel.setText("Score: " + totalScore);

        // Mark this question as graded and switch button text to "Next"
        answeredCurrent = true;
        submitButton.setText("Next");
    }

    // ===================== GAME END ===================== //

    private void endGame() {
        // Build summary for alert
        StringBuilder sb = new StringBuilder();
        sb.append("Game over!\n");
        sb.append("Your total score: ").append(totalScore).append("\n\n");

        for (int i = 0; i < triviaArray.length; i++) {
            sb.append("Q").append(i + 1).append(": ").append(triviaArray[i].getQuestion()).append("\n");
            sb.append("Your answer: ")
                    .append(userAnswers[i] == null ? "(no answer)" : userAnswers[i])
                    .append("\n");
            sb.append("Correct answer: ").append(triviaArray[i].getAnswer()).append("\n\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Trivia Summary");
        alert.setHeaderText("Trivia Game Finished");
        alert.setContentText(sb.toString());
        alert.showAndWait();


        questionLabel.setText("Game over! Final score: " + totalScore);
    }

    public static void main(String[] args) {
        launch();
    }
}