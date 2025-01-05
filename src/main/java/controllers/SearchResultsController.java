package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import search.Player;

import java.util.List;

public class SearchResultsController {

    @FXML
    private TableView<Player> resultsTable;

    @FXML
    private TableColumn<Player, String> nameColumn;

    @FXML
    private TableColumn<Player, String> positionColumn;

    @FXML
    private TableColumn<Player, Double> salaryColumn;

    private ObservableList<Player> playersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Bind the table columns to Player properties
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("weeklySalary"));

        // Link the playersList to the table
        resultsTable.setItems(playersList);
    }

    public void displayResults(List<Player> players) {
        // Update the observable list, automatically refreshing the table
        playersList.setAll(players);
    }
}
