package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import search.CricketPlayerDb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CountryPlayerCountController {

    @FXML
    private TableView<Map<String, Object>> countryPlayerCountTable;

    @FXML
    private TableColumn<Map<String, Object>, String> countryColumn;

    @FXML
    private TableColumn<Map<String, Object>, Long> playerCountColumn;

    private CricketPlayerDb cricketPlayerDb;

    public CountryPlayerCountController() {
        cricketPlayerDb = new CricketPlayerDb();
        try {
            cricketPlayerDb.loadPlayers("target/classes/players.txt"); // Replace with actual file path
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Set up columns to use the map values
        countryColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().get("country").toString()));

        playerCountColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleLongProperty((Long) cellData.getValue().get("playerCount")).asObject());

        // Load the data into the TableView
        loadCountryPlayerCountData();
    }

    private void loadCountryPlayerCountData() {
        Map<String, Long> countryPlayerCounts = cricketPlayerDb.countByCountry(); // Get player count by country
        ObservableList<Map<String, Object>> tableData = FXCollections.observableArrayList();

        for (Map.Entry<String, Long> entry : countryPlayerCounts.entrySet()) {
            Map<String, Object> row = new HashMap<>();
            row.put("country", entry.getKey());
            row.put("playerCount", entry.getValue());
            tableData.add(row);
        }

        countryPlayerCountTable.setItems(tableData);
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
