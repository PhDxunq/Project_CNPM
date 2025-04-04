package com.example.phongvanrestaurant.components.Cashier;

import com.example.phongvanrestaurant.HelloApplication;
import com.example.phongvanrestaurant.components.Admin.AdminHome;
import com.example.phongvanrestaurant.components.Staff.Table.StaffOrderSelectTableController;
import com.example.phongvanrestaurant.models.DBConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CashierLoginSucess {
    @FXML
    private Button continueButton;

    @FXML
    private Label welcomeLabel;

    private int staffId;

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public void setFullName(String fullName) {
        welcomeLabel.setText("Xin chào, " + fullName + "!");
    }

    @FXML
    void onContinueButtonClicked(ActionEvent actionEvent) {
        try {
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            Timestamp timeCheckin = Timestamp.valueOf(now);

            DBConnection dbcn = DBConnection.getInstance();
            String insertQuery = """
                        INSERT INTO timesheet (staffid, timecheckin)
                        VALUES (?, ?)
                    """;

            PreparedStatement pst = dbcn.cn.prepareStatement(insertQuery);
            pst.setInt(1, staffId);
            pst.setTimestamp(2, timeCheckin);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Thành công", "Điểm danh thành công!", Alert.AlertType.INFORMATION);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/phongvanrestaurant/Cashier/cashier-home.fxml"));
                Scene scene = new Scene(loader.load());
                CashierHome controller = loader.getController();
                controller.setStaffId(staffId);
                Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                currentStage.setScene(scene);
                currentStage.setMaximized(true);
            } else {
                showAlert("Lỗi", "Không thể lưu thông tin check-in!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Có lỗi xảy ra khi ghi vào cơ sở dữ liệu!", Alert.AlertType.ERROR);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void doCancel(){
        Platform.exit();
    }
    public void doMinimize(ActionEvent actionEvent)
    { Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setIconified(true); }
}
