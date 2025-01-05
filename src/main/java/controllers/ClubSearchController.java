package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class ClubSearchController {

    @FXML
    public void onMaximumSalary(ActionEvent event) {
        // Logic for handling the "Player(s) with the maximum salary of a club" button
        System.out.println("Finding player(s) with the maximum salary of a club...");
        // Add your logic here
    }

    @FXML
    public void onMaximumAge(ActionEvent event) {
        // Logic for handling the "Player(s) with the maximum age of a club" button
        System.out.println("Finding player(s) with the maximum age of a club...");
        // Add your logic here
    }

    @FXML
    public void onMaximumHeight(ActionEvent event) {
        // Logic for handling the "Player(s) with the maximum height of a club" button
        System.out.println("Finding player(s) with the maximum height of a club...");
        // Add your logic here
    }

    @FXML
    public void onTotalYearlySalary(ActionEvent event) {
        // Logic for handling the "Total yearly salary of a club" button
        System.out.println("Calculating the total yearly salary of a club...");
        // Add your logic here
    }

    @FXML
    public void onBack(ActionEvent event) {
        // Logic for navigating back to the main menu
        try {
            // Load the MainMenu.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/MainMenu.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("Main Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
