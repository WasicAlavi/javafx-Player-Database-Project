package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import search.Player;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SellPlayersDialogController {

    @FXML
    private TableView<Player> sellPlayerTable;
    @FXML
    private TableColumn<Object, Object> heightColumn;
    @FXML
    private TableColumn<Player, String> nameColumn;
    @FXML
    private TableColumn<Object, Object> jerseyNumberColumn;
    @FXML
    private TableColumn<Player, Integer> ageColumn;
    @FXML
    private TableColumn<Object, Object> countryColumn;
    @FXML
    private TableColumn<Player, String> positionColumn;
    @FXML
    private TableColumn<Object, Object> weeklySalaryColumn;
    @FXML
    private TableColumn<Player, String> clubColumn;

    private ObservableList<Player> players;
    private List<Player> selectedPlayers;
    private ObjectOutputStream oos;
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
        sellPlayerTable.setRowFactory(tv -> {
            TableRow<Player> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Player player = row.getItem();
                    if (selectedPlayers.contains(player)) {
                        selectedPlayers.remove(player);
                        row.setStyle(""); // Reset the style
                    } else {
                        selectedPlayers.add(player);
                        row.setStyle("-fx-background-color: lightcoral;"); // Highlight row
                    }
                }
            });
            return row;
        });
    }

    public void setPlayers(List<Player> players) {
        this.players = FXCollections.observableArrayList(players);
        sellPlayerTable.setItems(this.players);
    }

    public void setOutputStream(ObjectOutputStream oos) {
        this.oos = oos;
    }

    private void sellPlayersToServer(List<Player> playersToSell) {
        new Thread(() -> {
            try {
                synchronized (lock) {
                    for (Player player : playersToSell) {
                        String request = "sellPlayer," + player.getName();
                        oos.writeObject(request);
                        oos.flush();
                    }
                    System.out.println("Sell requests sent for players.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }




    @FXML
    private void onConfirmSelection() {
        System.out.println("Selected players for selling:");
        selectedPlayers.forEach(player -> System.out.println(player.getName()));

        sellPlayersToServer(selectedPlayers);
        // Close the dialog
        Stage stage = (Stage) sellPlayerTable.getScene().getWindow();
        stage.close();
    }


    public List<Player> getSelectedPlayers() {
        return selectedPlayers;
    }
}
