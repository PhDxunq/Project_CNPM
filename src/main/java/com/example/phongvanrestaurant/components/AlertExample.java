package com.example.phongvanrestaurant.components;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;

public class AlertExample {

    public void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.getDialogPane().setStyle("-fx-background-color: white; -fx-font-size: 16px;");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/phongvanrestaurant/Image/Alert/warning.png")));
        alert.setTitle(title);
        
        alert.setHeaderText(null);
        alert.setContentText(message);
        Image image = new Image(getClass().getResourceAsStream("/com/example/phongvanrestaurant/Image/Alert/warning.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        alert.setGraphic(imageView);
        alert.showAndWait();
    }

    public void successAlert(String message){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.getDialogPane().setStyle("-fx-background-color: white; -fx-font-size: 16px;");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/phongvanrestaurant/Image/Alert/success.png")));
        alert.setTitle("Success");

        alert.setHeaderText(null);
        alert.setContentText(message);
        Image image = new Image(getClass().getResourceAsStream("/com/example/phongvanrestaurant/Image/Alert/success.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        alert.setGraphic(imageView);
        alert.showAndWait();
    }

    public void showConfirmationAlert(String title, String message, Runnable onConfirmAction) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.getDialogPane().setStyle("-fx-background-color: white; -fx-font-size: 16px;");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        alert.setTitle(title);

        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (onConfirmAction != null) {
                onConfirmAction.run();
            }
        }
    }

    public void onConfirmAction() {
        System.out.println("Action confirmed!");
    }
}