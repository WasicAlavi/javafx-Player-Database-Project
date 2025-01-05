package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private ComboBox<String> searchOptionsDropdown; // For Search Players

    @FXML
    private ComboBox<String> searchClubsDropdown; // For Search Clubs

    @FXML
    private Button searchPlayersButton;



    @FXML
    private Button searchClubsButton;

    private Stage stage;
    private Scene previousScene;

    // This method is called when navigating to MainMenu
    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }


    @FXML
    public void initialize() {
        // Populate the search options dropdown for players
        searchOptionsDropdown.getItems().addAll(
                "1. By Player Name",
                "2. By Club and Country",
                "3. By Position",
                "4. By Salary Range",
                "5. Country-wise player count"
        );

        // Populate the search clubs dropdown with specific options
        searchClubsDropdown.getItems().addAll(
                "1. Player(s) with the maximum salary of a club",
                "2. Player(s) with the maximum age of a club",
                "3. Player(s) with the maximum height of a club",
                "4. Total yearly salary of a club"
        );
    }

    @FXML
    public void onSearchPlayers(ActionEvent event) {
        String selectedOption = searchOptionsDropdown.getValue();
        if (selectedOption != null) {
            try {
                FXMLLoader loader;
                Parent root;
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                switch (selectedOption) {
                    case "1. By Player Name":
                        loader = new FXMLLoader(getClass().getResource("/scenes/PlayerSearchInput.fxml"));
                        root = loader.load();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Search Player by Name");
                        break;

                    case "2. By Club and Country":
                        loader = new FXMLLoader(getClass().getResource("/scenes/SearchByClubAndCountry.fxml"));
                        root = loader.load();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Search Player by Club and Country");
                        break;

                    case "3. By Position":
                        loader = new FXMLLoader(getClass().getResource("/scenes/SearchByPosition.fxml"));
                        root = loader.load();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Search Player by Position");
                        break;

                    case "4. By Salary Range":
                        loader = new FXMLLoader(getClass().getResource("/scenes/SearchBySalary.fxml"));
                        root = loader.load();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Search Player by Salary Range");
                        break;

                    case "5. Country-wise player count":
                        loader = new FXMLLoader(getClass().getResource("/scenes/CountryPlayerCount.fxml"));
                        root = loader.load();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Country-wise Player Count");
                        break;

                    default:
                        System.out.println("Invalid option selected.");
                }
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No search option selected.");
        }
    }


    @FXML
    public void onSearchClubs(ActionEvent event) {
        String selectedOption = searchClubsDropdown.getValue();
        if (selectedOption != null) {
            try {
                FXMLLoader loader;
                Parent root;
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                switch (selectedOption) {
                    case "1. Player(s) with the maximum salary of a club":
                        loader = new FXMLLoader(getClass().getResource("/scenes/ClubMaximumSalary.fxml"));
                        root = loader.load();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Player(s) with the Maximum Salary of a Club");
                        break;

                    case "2. Player(s) with the maximum age of a club":
                        loader = new FXMLLoader(getClass().getResource("/scenes/ClubMaximumAge.fxml"));
                        root = loader.load();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Player(s) with the Maximum Age of a Club");
                        break;

                    case "3. Player(s) with the maximum height of a club":
                        loader = new FXMLLoader(getClass().getResource("/scenes/ClubMaximumHeight.fxml"));
                        root = loader.load();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Player(s) with the Maximum Height of a Club");
                        break;

                    case "4. Total yearly salary of a club":
                        loader = new FXMLLoader(getClass().getResource("/scenes/ClubYearlySalary.fxml"));
                        root = loader.load();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Total Yearly Salary of a Club");
                        break;

                    default:
                        System.out.println("Invalid option selected.");
                }
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No club search option selected.");
        }
    }


    @FXML
    public void onAddPlayer() {

    }

    @FXML
    public void onAddPlayer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/AddPlayer.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Player");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load AddPlayer.fxml");
        }
    }

    @FXML
    public void onExitSystem(ActionEvent event) {
        if (previousScene != null) {
            // Set the previous scene to the stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(previousScene);
            stage.show();
        } else {
            try {
                // Load the mainMenu.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/LoginPage.fxml"));
                Parent mainMenuRoot = loader.load();

                // Set the new scene
                Scene mainMenuScene = new Scene(mainMenuRoot);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(mainMenuScene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Optionally handle the error (e.g., show an alert)
            }
        }
    }


}
