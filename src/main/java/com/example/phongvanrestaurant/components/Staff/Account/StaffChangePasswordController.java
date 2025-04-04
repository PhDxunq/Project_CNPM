package com.example.phongvanrestaurant.components.Staff.Account;

import com.example.phongvanrestaurant.components.AlertExample;
import com.example.phongvanrestaurant.components.Staff.Orders.StaffOrdersController;
import com.example.phongvanrestaurant.components.Staff.Table.StaffOrderSelectTableController;
import com.example.phongvanrestaurant.models.DBConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class StaffChangePasswordController {
    private int staffId;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtOldPassword;

    @FXML
    private PasswordField txtNewPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    @FXML
    public void handleChangePassword(ActionEvent event) {
        AlertExample alert = new AlertExample();
        String username = txtUsername.getText();
        String oldPassword = txtOldPassword.getText();
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            alert.showAlert("Lỗi","Vui lòng điền toàn bộ các thông tin!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            alert.showAlert("Lỗi","Mật khẩu mới và nhập lại không khớp!");
            return;
        }

        try {
            DBConnection dbcn = DBConnection.getInstance();
            String selectQuery = "SELECT * FROM accounts WHERE staffId = ? AND username = ? AND password = ?";
            PreparedStatement selectStmt = dbcn.cn.prepareStatement(selectQuery);
            selectStmt.setInt(1, staffId);
            selectStmt.setString(2, username);
            selectStmt.setString(3, oldPassword);

            ResultSet resultSet = selectStmt.executeQuery();
            if (resultSet.next()) {
                String updateQuery = "UPDATE accounts SET password = ?, DateUpdate = NOW() WHERE staffId = ?";
                PreparedStatement updateStmt = dbcn.cn.prepareStatement(updateQuery);
                updateStmt.setString(1, newPassword);
                updateStmt.setInt(2, staffId);

                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    alert.successAlert("Đổi mật khẩu thành công!");
                    txtUsername.clear();
                    txtOldPassword.clear();
                    txtNewPassword.clear();
                    txtConfirmPassword.clear();
                } else {
                    alert.showAlert("Lỗi","Xảy ra lỗi trong quá trình đổi mật khẩu, vui lòng thử lại!");
                }
            } else {
                alert.showAlert("Lỗi","Sai tên đăng nhập hoặc mật khẩu!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            alert.showAlert("Lỗi","Xảy ra lỗi trong quá trình đổi mật khẩu, vui lòng thử lại!");
        }
    }

    @FXML
    public void changeOrdersView(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/phongvanrestaurant/Staff/staff-orders-view.fxml"));
        Scene scene = new Scene(loader.load());
        StaffOrdersController controller = loader.getController();
        controller.setStaffId(staffId);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(scene);
        currentStage.setX(0);
        currentStage.setY(0);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        currentStage.setWidth(screenBounds.getWidth());
        currentStage.setHeight(screenBounds.getHeight());

        currentStage.setResizable(true);
        currentStage.show();
    }

    @FXML
    public void changeTableView(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/phongvanrestaurant/Staff/staff-order-select-table-view.fxml"));
        Scene scene = new Scene(loader.load());
        StaffOrderSelectTableController controller = loader.getController();
        controller.setStaffId(staffId);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(scene);
        currentStage.setMaximized(true);
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
            loginStage.setScene(new Scene(root,600,400));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doCancel() {
        Platform.exit();
    }

    public void doMinimize(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
}
