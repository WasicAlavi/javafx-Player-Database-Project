package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import search.CricketPlayerDb;
import search.Player;

import java.io.IOException;
import java.util.List;

public class PlayerSearchClubCountryController {

    @FXML
    private TextField clubField;

    @FXML
    private TextField countryField;

    @FXML
    private TableView<Player> resultsTable;

    @FXML
    private TableColumn<Player, Integer> numberColumn;

    @FXML
    private TableColumn<Player, String> nameColumn;

    @FXML
    private TableColumn<Player, Integer> ageColumn;

    @FXML
    private TableColumn<Player, String> heightColumn;

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

    public PlayerSearchClubCountryController() {
        cricketPlayerDb = new CricketPlayerDb();
        try {
            // Load players from a file (replace with your actual path)
            cricketPlayerDb.loadPlayers("target/classes/players.txt");
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

    @FXML
    public void onSearch(ActionEvent event) {
        String club = clubField.getText().trim();
        String country = countryField.getText().trim();

        if (club.isEmpty() && country.isEmpty()) {
            // Optionally show an error message if both inputs are empty
            System.out.println("Please enter at least a club or country.");
            return;
        }

        // Fetch search results
        List<Player> results = cricketPlayerDb.searchByCountryAndClub(country,club);

        // Update the table with search results
        playersList.setAll(results); // This updates the TableView directly
    }
}
