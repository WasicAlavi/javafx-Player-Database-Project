package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import search.CricketPlayerDb;
import search.Player;

import java.io.IOException;

public class AddPlayerController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField countryField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField heightField;

    @FXML
    private TextField clubField;

    @FXML
    private TextField positionField;

    @FXML
    private TextField jerseyNumberField;

    @FXML
    private TextField weeklySalaryField;

    @FXML
    private TableView<Player> salaryTable;

    @FXML
    private TableColumn<Player, String> nameColumn;

    @FXML
    private TableColumn<Player, String> countryColumn;

    @FXML
    private TableColumn<Player, Integer> ageColumn;

    @FXML
    private TableColumn<Player, Double> heightColumn;

    @FXML
    private TableColumn<Player, String> clubColumn;

    @FXML
    private TableColumn<Player, String> positionColumn;

    @FXML
    private TableColumn<Player, Integer> jerseyNumberColumn;

    @FXML
    private TableColumn<Player, Integer> weeklySalaryColumn;




    private CricketPlayerDb cricketPlayerDb;

    private ObservableList<Player> playerList;

    public AddPlayerController() {
        cricketPlayerDb = new CricketPlayerDb();
        try {
            cricketPlayerDb.loadPlayers("target/classes/players.txt"); // Adjust path as needed
        } catch (Exception e) {
            e.printStackTrace();
        }
        playerList = FXCollections.observableArrayList(cricketPlayerDb.getAllPlayers());
    }

    @FXML
    public void initialize() {
        // Set up the TableView columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        clubColumn.setCellValueFactory(new PropertyValueFactory<>("club"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        jerseyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        weeklySalaryColumn.setCellValueFactory(new PropertyValueFactory<>("weeklySalary"));

        salaryTable.setItems(playerList);
    }

    @FXML
    public void onAddPlayer(ActionEvent event) {
        try {
            // Retrieve input fields
            String name = nameField.getText().trim();
            String country = countryField.getText().trim();
            String club = clubField.getText().trim();
            String position = positionField.getText().trim();

            // Check if any required fields are empty
            if (name.isEmpty() || country.isEmpty() || club.isEmpty() || position.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill out all required fields.");
                return;
            }

            // Parse numeric fields and validate format only
            int age = Integer.parseInt(ageField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());
            int jerseyNumber = Integer.parseInt(jerseyNumberField.getText().trim());
            int weeklySalary = Integer.parseInt(weeklySalaryField.getText().trim());

            // Check for duplicate player name
            if (cricketPlayerDb.checkName(name)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Entry", "A player with this name already exists.");
                return;
            }

            // Add the new player
            Player newPlayer = new Player(name, country, age, height, club, position, jerseyNumber, weeklySalary);
            cricketPlayerDb.addPlayer(newPlayer);
            playerList.add(newPlayer); // Update TableView
            cricketPlayerDb.savePlayers("target/classes/players.txt"); // Persist changes to file

            showAlert(Alert.AlertType.INFORMATION, "Success", "Player added successfully!");
            clearInputFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format Error", "Please enter valid numbers for Age, Height, Jersey Number, and Weekly Salary.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred while adding the player.");
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    public void onBack(ActionEvent event) {
        try {
            // Load the Main Menu scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/mainMenu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearInputFields() {
        nameField.clear();
        countryField.clear();
        ageField.clear();
        heightField.clear();
        clubField.clear();
        positionField.clear();
        jerseyNumberField.clear();
        weeklySalaryField.clear();
    }
}
