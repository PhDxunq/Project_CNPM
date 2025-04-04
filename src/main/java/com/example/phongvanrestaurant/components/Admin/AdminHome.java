package com.example.phongvanrestaurant.components.Admin;

import com.example.phongvanrestaurant.daoimpl.StaffIdReceiver;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminHome implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("AdminHome");
    }

    @FXML
    private AnchorPane ap;

    @FXML
    private AnchorPane ap_home;
    private int staffId;

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }


    @FXML
    public void Home(ActionEvent event) {
        loadPage("admin-banner");
    }

    @FXML
    public void Account(ActionEvent event) {
        loadPage("admin-account");
    }

    @FXML
    public void Staff(ActionEvent event) {
        loadPage("admin-staff");
    }

    @FXML
    public void Inventory(ActionEvent event) {
        loadPage("admin-inventory");
    }

    @FXML
    public void Table(ActionEvent event) {
        loadPage("admin-table");
    }

    @FXML
    public void Timesheet(ActionEvent event) {
        loadPage("admin-timesheet");
    }

    @FXML
    public void Orders(ActionEvent event) {
        loadPage("admin-orders");
    }

    @FXML
    public void ChangePassword(ActionEvent event) {
        loadPage("admin-change-password");
    }


    @FXML
    private void loadPage(String page) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/phongvanrestaurant/Admin/" + page + ".fxml"));
            root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof StaffIdReceiver) {
                ((StaffIdReceiver) controller).setStaffId(this.staffId);
            }

            ap.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void doLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn đăng xuất không?");

        if (alert.showAndWait().get().getText().equals("OK")) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            openLoginScreen();
        }
    }

    private void openLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/phongvanrestaurant/login-view.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(new Scene(root,600,400));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doCancel(){
        Platform.exit();
    }
    public void doMinimize(ActionEvent actionEvent)
    { Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setIconified(true); }
}
