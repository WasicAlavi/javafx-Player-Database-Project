package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Callback;
import search.CricketPlayerDb;
import search.Player;
import server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class DashboardController {

    @FXML
    private TableColumn<Player, Integer> numberColumn;

    @FXML
    private Label clubNameLabel;

    @FXML
    private TableView<Player> playerTable;

    @FXML
    private TableColumn<Player, String> nameColumn;

    @FXML
    private TableColumn<Player, Integer> ageColumn;

    @FXML
    private TableColumn<Player, Double> heightColumn;

    @FXML
    private TableColumn<Player, String> positionColumn;

    @FXML
    private TableColumn<Player, String> clubColumn;

    @FXML
    private TableColumn<Player, String> countryColumn;

    @FXML
    private TableColumn<Player, Integer> weeklySalaryColumn;
    @FXML
    TableColumn<Object, Object> jerseyNumberColumn;

    @FXML
    private Button buyButton;

    @FXML
    private Button sellButton;

    @FXML
    private Button refreshButton;

    @FXML
    private TextField searchField;

    @FXML
    private VBox dashboardRoot;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;
    private ObservableList<Player> players;
    private Timer updateTimer;
    private final Object lock = new Object();
    private boolean allowPeriodicUpdates = true;
    private String clubName;
    private String password;
    public boolean successfulLogin = true;


    @FXML
    private void initialize() {
        // Set up columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        jerseyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        clubColumn.setCellValueFactory(new PropertyValueFactory<>("club"));
        weeklySalaryColumn.setCellValueFactory(new PropertyValueFactory<>("weeklySalary"));

        numberColumn.setCellValueFactory(cellData -> {
            int index = playerTable.getItems().indexOf(cellData.getValue()) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index).asObject();
        });

        players = FXCollections.observableArrayList();
        playerTable.setItems(players);

        //startPeriodicUpdates();

    }


    private void connectToServer() {
        try {
            System.out.println("Connecting to server...");
            socket = new Socket("localhost", 2092); // Ensure server is accessible
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            // Send credentials
            System.out.println(clubName + "connectToServer " + password);
            oos.writeObject(clubName);
            oos.writeObject(password);
            oos.flush();

            // Read server response
            Object response = ois.readObject();
            System.out.println("Server response: " + response);

            if (!"Correct".equals(response)) {
                showAlert("Login Failed", "Invalid club name or password. Please try again.");
                successfulLogin = false;
                //loadLoginPage();
                return;
            }

            System.out.println("Server connection successful.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            showAlert("Connection Error", "Failed to connect to the server. Please try again.");
        }
    }
    private void loadLoginPage() {
        try {
            // Load the Login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/loginPage.fxml"));
            Parent root = loader.load();

            // Create a new scene and set it to the current stage
            Scene scene = new Scene(root);

            // If there's an existing stage (i.e., the app window), reuse it; otherwise, create a new stage
            Stage stage = (Stage) (scene.getWindow()); // Get the stage from the scene

            if (stage == null) {
                // If no existing stage, create a new one
                stage = new Stage();
            }

            // Set the scene to the stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the login page.");
        }
    }


    private void startPeriodicUpdates() {
        updateTimer = new Timer(true);
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (allowPeriodicUpdates) {
                    requestPlayers();
                }
            }
        }, 0, 5000); // Update every 5 seconds
    }

    @FXML
    private void requestPlayers() {
        new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("Sending request: showPlayer");
                    oos.writeObject("showPlayer");
                    oos.flush();
                    System.out.println("Request sent successfully.");

                    // Read the server's response
                    Object response = ois.readObject();
                    if ("response:playerList".equals(response)) {
                        Object data = ois.readObject();
                        if (data instanceof Player[] playerList) {
                            Platform.runLater(() -> {
                                players.setAll(playerList);

                                // Ensure Server.tosell is initialized
                                /*if (Server.tosell == null) {
                                    Server.tosell = new ArrayList<>(); // Initialize if null
                                }

                                // Print all players in the tosell list
                                for (Player player : Server.tosell) {
                                    System.out.println(player);
                                }
                                System.out.println("HIIII");

                                // Set row factory for styling rows
                                playerTable.setRowFactory(tv -> new TableRow<Player>() {
                                    @Override
                                    protected void updateItem(Player player, boolean empty) {
                                        super.updateItem(player, empty);

                                        if (player == null || empty) {
                                            setStyle(""); // Reset style for empty rows or null items
                                        } else if (Server.tosell.contains(player)) {
                                            setStyle("-fx-background-color: rgba(255, 0, 0, 0.2);"); // Light red background
                                        } else {
                                            setStyle(""); // Reset style if not in the toSell list
                                        }
                                    }
                                });*/

                                System.out.println("Dashboard updated with player data.");
                            });
                        }

                    } else if ("response:confirmBuyPlayerConfirmed".equals(response)) {
                        Platform.runLater(() -> showSuccessAlert("Purchase Confirmed", "The player purchase has been confirmed."));
                    } else {
                        throw new IOException("Unexpected server response: " + response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showErrorAlert("Error", "Failed to retrieve player data."));
                }
            }
        }).start();
    }

    public void setCredentials(String clubName, String password) {

        System.out.println("Setting Credentials in DashboardController - Club Name: " + clubName + ", Password: " + password);
        this.clubName = clubName;
        this.password = password;

        clubNameLabel.setText(clubName + " Dashboard");

        connectToServer();
        System.out.println(clubName + " " + password);
        requestPlayers();

        String backgroundImage = getBackgroundImageForClub(clubName);
        if (backgroundImage != null) {
            dashboardRoot.setStyle(
                    "-fx-background-image: url('" + backgroundImage + "'); " +
                            "-fx-background-size: cover; " +
                            "-fx-background-color: rgba(255, 255, 255, 0.8);" // Optional overlay
            );
        }

        //fetchPlayersFromDatabase(clubName.trim());
    }

    private String getBackgroundImageForClub(String clubName) {
        switch (clubName.toLowerCase()) {
            case "mumbai indians":
                return "/mumbai_indians.png";
            case "delhi capitals":
                return "/dc.png";
            case "chennai super kings":
                return "/csk.jpg";
            case "royal challengers bangalore":
                return "/rcb.png";
            case "rajasthan royals":
                return "/rr.png";
            case "gujarat titans":
                return "/gt.png";
            case "kolkata knight riders":
                return "/kkr.png";
            case "lucknow super giants":
                return "/lsg.png";
            case "punjab kings":
                return "/pk.png";
            case "sunrisers hyderabad":
                return "/sh.png";
            default:
                return "/images/default_background.jpg";
        }
    }

    /*private void fetchPlayersFromDatabase(String clubName) {
        // Simulate fetching players from the database

        ArrayList<Player> playerList = db.searchByClub(clubName); // Query the database

        if (playerList == null ) {
            Platform.runLater(() -> showErrorAlert("No Data", "No players found for your club."));
            return;
        }else if(playerList.isEmpty()){
            Platform.runLater(() -> showErrorAlert("No Data", "Empty players found for your club."));
            return;
        }

        // Update the UI with the player list
        players.setAll(playerList); // Ensure thread-safe UI updates
    }*/



    @FXML
    private void onBuyPlayers() {
        new Thread(() -> {
            synchronized (lock) {
                try {
                    allowPeriodicUpdates = false;

                    // Send the buyPlayer request to the server
                    oos.writeObject("buyPlayer");
                    oos.flush();
                    System.out.println("Request sent: buyPlayer");

                    // Read the server's response
                    Object response = ois.readObject();
                    System.out.println("Response received: " + response);

                    // Handle the sellList response
                    if ("response:sellList".equals(response)) {
                        Object data = ois.readObject();
                        if (data instanceof Player[] sellList) {

                            // Check if the sell list is empty
                            if (sellList.length == 0) {
                                Platform.runLater(() -> showErrorAlert("No Players Available", "There are no players available to buy."));
                                return;
                            }

                            // Open the Buy Players dialog with the sell list
                            Platform.runLater(() -> openBuyPlayersDialog(sellList));
                        } else {
                            Platform.runLater(() -> showErrorAlert("Data Error", "Unexpected data type received from server."));
                        }
                    } else {
                        // Handle unexpected responses
                        Platform.runLater(() -> showErrorAlert("Unexpected Response", "Received an unexpected response from the server."));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showErrorAlert("Error", "An error occurred while processing the request."));
                } finally {
                    allowPeriodicUpdates = true;
                }
            }
        }).start();
    }

    private void confirmBuyPlayer(String playerName) {
        new Thread(() -> {
            synchronized (lock) {
                try {
                    String request = "confirmBuyPlayer," + playerName;
                    System.out.println("Sending request: " + request);
                    oos.writeObject(request);
                    oos.flush();

                    // Read the server's response
                    Object response = ois.readObject();
                    if ("response:confirmBuyPlayerConfirmed".equals(response)) {
                        Platform.runLater(() -> showSuccessAlert("Purchase Confirmed", playerName + " has been purchased successfully!"));
                    } else {
                        Platform.runLater(() -> showErrorAlert("Unexpected Response", "Received unexpected response: " + response));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showErrorAlert("Error", "Failed to confirm player purchase."));
                }
            }
        }).start();
    }


    private void openBuyPlayersDialog(Player[] sellList) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/buyPlayersDialog.fxml"));
            Parent root = loader.load();

            BuyPlayersDialogController controller = loader.getController();
            controller.setStreams(ois, oos); // Pass ObjectInputStream and ObjectOutputStream
            controller.setPlayers(Arrays.asList(sellList));

            Stage stage = new Stage();
            stage.setTitle("Buy Players");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Dialog Error", "Failed to open the Buy Players dialog.");
        }
    }

    @FXML
    private void onSellPlayers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/SellPlayersDialog.fxml"));
            Parent root = loader.load();

            SellPlayersDialogController controller = loader.getController();
            controller.setOutputStream(oos);
            controller.setPlayers(players);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Sell Players");
            dialogStage.setScene(new Scene(root));
            dialogStage.initOwner(playerTable.getScene().getWindow());
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load Sell Players dialog.");
        }
    }

    @FXML
    private void onRefreshDashboard() {
        requestPlayers();
        playerTable.setItems(players);
    }

    @FXML
    private void onSearchPlayer() {
        String searchText = searchField.getText().toLowerCase();
        ObservableList<Player> filteredPlayers = players.filtered(player ->
                player.getName().toLowerCase().contains(searchText) ||
                        player.getPosition().toLowerCase().contains(searchText));
        playerTable.setItems(filteredPlayers);
    }

    @FXML
    private void onBackToLogin() {
        try {
            if (updateTimer != null) {
                updateTimer.cancel();
            }

            Stage currentStage = (Stage) playerTable.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/LoginPage.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void onViewPlayerDatabase(ActionEvent event) {
        try {
            // Load the FXML and the scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/MainMenu.fxml"));
            Parent mainMenuRoot = loader.load();
            Scene mainMenuScene = new Scene(mainMenuRoot);

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Access the controller of the loaded scene
            MainMenuController mainMenuController = loader.getController();

            // Set the previous scene (LoginPage or wherever it was accessed from)
            mainMenuController.setPreviousScene(stage.getScene());

            // Set and show the new scene
            stage.setScene(mainMenuScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Unable to load the requested scene.");
        }
    }




}
