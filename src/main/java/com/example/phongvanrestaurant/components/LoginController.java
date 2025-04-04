package com.example.phongvanrestaurant.components;

import com.example.phongvanrestaurant.HelloApplication;
import com.example.phongvanrestaurant.components.Admin.AdminLoginSucess;
import com.example.phongvanrestaurant.components.Cashier.CashierLoginSucess;
import com.example.phongvanrestaurant.components.Kitchen.KitchenLoginSucess;
import com.example.phongvanrestaurant.components.Staff.StaffLoginSucess;
import com.example.phongvanrestaurant.models.DBConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class LoginController {
    public Button btnLogin;
    public TextField username;
    public PasswordField password;

    public void initialize() {
        try {
            DBConnection dbcn = DBConnection.getInstance();
            if (dbcn.cn != null && !dbcn.cn.isClosed()) {
                System.out.println("Kết nối cơ sở dữ liệu thành công.");
            } else {
                System.out.println("Không thể kết nối cơ sở dữ liệu.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doLogin(ActionEvent actionEvent) throws ClassNotFoundException, SQLException, IOException {
        String usn = username.getText();
        String psw = password.getText();

        DBConnection dbcn = DBConnection.getInstance();
        String query = """
                    SELECT staff.id AS staffid, staff.role, staff.fullname , accounts.password
                    FROM accounts
                    JOIN staff ON accounts.staffid = staff.id
                    WHERE accounts.username = ?
                """;
        PreparedStatement pst = dbcn.cn.prepareStatement(query);
        pst.setString(1, usn);

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            String hashed = rs.getString("password");
            if (!BCrypt.checkpw(psw, hashed)) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi đăng nhập", "Sai tên đăng nhập hoặc mật khẩu!");
                return;
            }

            String role = rs.getString("role");
            FXMLLoader fxmlLoader = null;
            Scene scene = null;
            if ("Admin".equalsIgnoreCase(role)) {
                fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/phongvanrestaurant/Admin/admin-login-sucess-view.fxml"));
                scene = new Scene(fxmlLoader.load(), 600, 400);
                AdminLoginSucess controller = fxmlLoader.getController();
                if (controller != null) {
                    controller.setStaffId(rs.getInt("staffid"));
                    controller.setFullName(rs.getString("fullname"));
                } else {
                    System.out.println("Error: controller not initialized!");
                }
            } else if ("NormalStaff".equalsIgnoreCase(role)) {
                fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/phongvanrestaurant/Staff/staff-login-sucess-view.fxml"));
                scene = new Scene(fxmlLoader.load(), 600, 400);
                StaffLoginSucess controller = fxmlLoader.getController();
                if (controller != null) {
                    controller.setStaffId(rs.getInt("staffid"));
                    controller.setFullName(rs.getString("fullname"));
                } else {
                    System.out.println("Error: controller not initialized!");
                }
            } else if ("Cashier".equalsIgnoreCase(role)) {
                fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/phongvanrestaurant/Cashier/cashier-login-sucess-view.fxml"));
                scene = new Scene(fxmlLoader.load(), 600, 400);
                CashierLoginSucess controller = fxmlLoader.getController();
                if (controller != null) {
                    controller.setStaffId(rs.getInt("staffid"));
                    controller.setFullName(rs.getString("fullname"));
                } else {
                    System.out.println("Error: controller not initialized!");
                }
            } else if ("Kitchen".equalsIgnoreCase(role)) {
                fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/phongvanrestaurant/Kitchen/kitchen-login-sucess.fxml"));
                scene = new Scene(fxmlLoader.load(), 600, 400);
                KitchenLoginSucess controller = fxmlLoader.getController();
                if (controller != null) {
                    controller.setStaffId(rs.getInt("staffid"));
                    controller.setFullName(rs.getString("fullname"));
                }
            } else {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi đăng nhập", "Không nhận diện được vai trò của bạn, vui lòng liên hệ quản lý!");
                return;
            }

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } else {
            AlertExample alert = new AlertExample();
            alert.showAlert("Lỗi đăng nhập", "Sai tên đăng nhập hoặc mật khẩu!");
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

