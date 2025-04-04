package com.example.phongvanrestaurant.components.Kitchen;

import com.example.phongvanrestaurant.daoimpl.StaffIdReceiver;
import com.example.phongvanrestaurant.models.DBConnection;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class KitchenHome implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        loadPage("kitchen-banner");
    }

    @FXML
    public void Orders(ActionEvent event) {
        loadPage("kitchen-orders");
    }

    @FXML
    public void ChangePassword(ActionEvent event) {
        loadPage("kitchen-change-password");
    }


    @FXML
    private void loadPage(String page) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/phongvanrestaurant/Kitchen/" + page + ".fxml"));
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

    public void doLogout(ActionEvent event) throws SQLException, ClassNotFoundException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn đăng xuất? Đăng xuất sẽ tự động điểm danh");

        if (alert.showAndWait().get().getText().equals("OK")) {
            LocalDateTime now = LocalDateTime.now();
            Timestamp currentTime = Timestamp.valueOf(now);
            DBConnection dbcn = DBConnection.getInstance();
            String insertSQL = "UPDATE timesheet SET timecheckout = ? WHERE staffID = ? AND timecheckout IS NULL";
            PreparedStatement pst = dbcn.cn.prepareStatement(insertSQL);
            pst.setTimestamp(1,currentTime);
            pst.setInt(2,staffId);
            pst.executeUpdate();

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
            loginStage.setScene(new Scene(root));
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
