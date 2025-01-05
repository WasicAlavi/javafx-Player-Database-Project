package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import search.Player;
import server.ServerConnection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BuyPlayersDialogController {

    @FXML
    private TableView<Player> buyPlayerTable;

    @FXML
    private TableColumn<Player, String> nameColumn;
    @FXML
    private TableColumn<Object, Object> weeklySalaryColumn;

    @FXML
    private TableColumn<Player, Integer> ageColumn;
    @FXML
    private TableColumn<Object, Object> countryColumn;
    @FXML
    private TableColumn<Player, String> positionColumn;
    @FXML
    private TableColumn<Object, Object> heightColumn;
    @FXML
    private TableColumn<Object, Object> jerseyNumberColumn;

    @FXML
    private TableColumn<Player, String> clubColumn;

    private ObservableList<Player> players;
    private List<Player> selectedPlayers;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private final Object lock = new Object();

    @FXML
    private void initialize() {
        selectedPlayers = new ArrayList<>();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        jerseyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        clubColumn.setCellValueFactory(new PropertyValueFactory<>("club"));
        weeklySalaryColumn.setCellValueFactory(new PropertyValueFactory<>("weeklySalary"));
        // Add row click listener to toggle selection
        buyPlayerTable.setRowFactory(tv -> {
            TableRow<Player> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Player player = row.getItem();
                    if (selectedPlayers.contains(player)) {
                        selectedPlayers.remove(player);
                        row.setStyle(""); // Reset the style
                    } else {
                        selectedPlayers.add(player);
                        row.setStyle("-fx-background-color: lightgreen;"); // Highlight row
                    }
                }
            });
            return row;
        });
    }




        public void setStreams(ObjectInputStream ois, ObjectOutputStream oos) {
            this.ois = ois;
            this.oos = oos;
        }



    public void setPlayers(List<Player> players) {
        this.players = FXCollections.observableArrayList(players);
        buyPlayerTable.setItems(this.players);
    }

    public void setOutputStream(ObjectOutputStream oos) {
        this.oos = oos;
    }

    @FXML
    private void onConfirmSelection() {
        if (oos == null || ois == null) {
            showErrorAlert("Error", "Connection to the server is not established.");
            return;
        }

        new Thread(() -> {
            try {
                for (Player player : selectedPlayers) {
                    String request = "confirmBuyPlayer," + player.getName();
                    System.out.println("Sending request: " + request);
                    oos.writeObject(request);
                    oos.flush();

                    // Handle the server's response
                    Object response = ois.readObject();
                    if ("response:confirmBuyPlayerConfirmed".equals(response)) {
                        Platform.runLater(() -> showSuccessAlert("Purchase Successful", player.getName() + " has been purchased!"));
                    } else {
                        Platform.runLater(() -> showErrorAlert("Purchase Failed", "Failed to purchase " + player.getName() + "."));
                    }
                }

                // Close the dialog
                Platform.runLater(() -> {
                    Stage stage = (Stage) buyPlayerTable.getScene().getWindow();
                    stage.close();
                });
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                Platform.runLater(() -> showErrorAlert("Error", "An error occurred while processing the purchase requests."));
            }
        }).start();
    }


    public List<Player> getSelectedPlayers() {
        return selectedPlayers;
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
