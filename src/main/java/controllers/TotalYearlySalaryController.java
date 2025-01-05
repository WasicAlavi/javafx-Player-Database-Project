package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import search.CricketPlayerDb;
import search.Player;

import java.io.IOException;
import java.util.List;

public class TotalYearlySalaryController {

    @FXML
    private TextField clubNameField;

    @FXML
    private Label resultLabel;

    @FXML
    private TableView<Player> resultsTable;

    @FXML
    private TableColumn<Player, Integer> numberColumn;

    @FXML
    private TableColumn<Player, String> nameColumn;

    @FXML
    private TableColumn<Player, Integer> ageColumn;

    @FXML
    private TableColumn<Player, Double> heightColumn;

    @FXML
    private TableColumn<Player, String> positionColumn;

    @FXML
    private TableColumn<Player, Integer> jerseyNumberColumn;

    @FXML
    private TableColumn<Player, String> countryColumn;

    @FXML
    private TableColumn<Player, String> clubColumn;

    @FXML
    private TableColumn<Player, Double> weeklySalaryColumn;

    private CricketPlayerDb cricketPlayerDb;

    private ObservableList<Player> playersList = FXCollections.observableArrayList();

    public TotalYearlySalaryController() {
        cricketPlayerDb = new CricketPlayerDb();
        try {
            cricketPlayerDb.loadPlayers("target/classes/players.txt"); // Replace with the actual file path
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Bind the table columns to Player properties
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        jerseyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        clubColumn.setCellValueFactory(new PropertyValueFactory<>("club"));
        weeklySalaryColumn.setCellValueFactory(new PropertyValueFactory<>("weeklySalary"));

        // Bind the number column dynamically to generate indices
        numberColumn.setCellValueFactory(cellData -> {
            int index = resultsTable.getItems().indexOf(cellData.getValue()) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index).asObject();
        });

        // Set the items in the TableView
        resultsTable.setItems(playersList);
    }

    @FXML
    public void onCalculate(ActionEvent event) {
        String clubName = clubNameField.getText().trim();

        if (clubName.isEmpty()) {
            resultLabel.setText("Please enter a club name.");
            return;
        }

        double totalYearlySalary = cricketPlayerDb.totalYearlySalary(clubName);
        List<Player> results = cricketPlayerDb.searchByClub(clubName);
        playersList.setAll(results);
        if (totalYearlySalary == 0) {
            resultLabel.setText("No players found for the club: " + clubName);
        } else {
            // Convert weekly salary to yearly salary (52 weeks in a year)
            totalYearlySalary *= 52;
            resultLabel.setText("Yearly Salary for " + clubName + ": $" + String.format("%.2f", totalYearlySalary));
        }
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
}
