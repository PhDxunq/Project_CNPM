package com.example.phongvanrestaurant.components.Admin.Account;

import com.example.phongvanrestaurant.components.AlertExample;
import com.example.phongvanrestaurant.daoimpl.StaffIdReceiver;
import com.example.phongvanrestaurant.models.DBConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class AdminChangePasswordController implements Initializable, StaffIdReceiver {
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
            String selectQuery = "SELECT * FROM accounts WHERE staffId = ? AND username = ?";
            PreparedStatement selectStmt = dbcn.cn.prepareStatement(selectQuery);
            selectStmt.setInt(1, staffId);
            selectStmt.setString(2, username);

            ResultSet resultSet = selectStmt.executeQuery();
            if (resultSet.next()) {
                String hashedPass = resultSet.getString("password");
                if (!BCrypt.checkpw(oldPassword, hashedPass)) {
                    alert.showAlert("Lỗi","Sai tên đăng nhập hoặc mật khẩu!");
                    return;
                } else {
                    String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                    String updateQuery = "UPDATE accounts SET password = ?, DateUpdate = NOW() WHERE staffId = ?";
                    PreparedStatement updateStmt = dbcn.cn.prepareStatement(updateQuery);
                    updateStmt.setString(1, hashedNewPassword);
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
    public void initialize() {
    }

    public void doCancel() {
        Platform.exit();
    }

    public void doMinimize(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
