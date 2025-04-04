package com.example.phongvanrestaurant.components.Admin;

import com.example.phongvanrestaurant.HelloApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminLoginSucess {

    @FXML
    private Button continueButton;

    @FXML
    private Label welcomeLabel;

    private int staffId;

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    @FXML
    void onContinueButtonClicked(ActionEvent event) {

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/phongvanrestaurant/Admin/admin-home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
            AdminHome controller = fxmlLoader.getController();
            controller.setStaffId(staffId);
            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setTitle("Phong Vân Restaurant");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void setFullName(String fullName) {
        welcomeLabel.setText("Xin chào, " + fullName + "!");
    }

    @FXML
    public void initialize() {
    }

    public void doCancel(){
        Platform.exit();
    }
    public void doMinimize(ActionEvent actionEvent)
    { Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setIconified(true); }
}
