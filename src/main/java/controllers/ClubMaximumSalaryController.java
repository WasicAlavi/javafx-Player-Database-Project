package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

public class ClubMaximumSalaryController {

    @FXML
    private TextField clubNameField;

    @FXML
    private TableView<Player> clubMaximumSalaryTable;

    @FXML
    private TableColumn<Player, String> playerNameColumn;

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

    public ClubMaximumSalaryController() {
        cricketPlayerDb = new CricketPlayerDb();
        try {
            cricketPlayerDb.loadPlayers("target/classes/players.txt"); // Replace with the actual file path
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Set up table columns
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        jerseyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        clubColumn.setCellValueFactory(new PropertyValueFactory<>("club"));
        weeklySalaryColumn.setCellValueFactory(new PropertyValueFactory<>("weeklySalary"));
    }

    @FXML
    public void onSearch(ActionEvent event) {
        String clubName = clubNameField.getText().trim();

        if (clubName.isEmpty()) {
            // Handle empty input (optional: show an alert to the user)
            return;
        }

        // Get the players with the maximum salary in the club
        List<Player> maxSalaryPlayers = cricketPlayerDb.searchMaxSalaryPlayer(clubName);

        // Convert to ObservableList for TableView
        ObservableList<Player> tableData = FXCollections.observableArrayList(maxSalaryPlayers);
        clubMaximumSalaryTable.setItems(tableData);
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
