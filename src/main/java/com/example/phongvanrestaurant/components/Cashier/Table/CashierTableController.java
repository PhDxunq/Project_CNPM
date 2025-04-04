package com.example.phongvanrestaurant.components.Cashier.Table;

import com.example.phongvanrestaurant.components.AlertExample;
import com.example.phongvanrestaurant.components.Staff.Menu.MenuController;
import com.example.phongvanrestaurant.models.DBConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CashierTableController {
    @FXML
    private GridPane gridPaneId;

    private int staffId;

    @FXML
    public void initialize() {
        loadTableData();
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    private void loadTableData() {
        try {
            DBConnection dbcn = DBConnection.getInstance();
            String query = """
                        SELECT id, status, numberofchair, note
                        FROM `table`
                    """;
            PreparedStatement pst = dbcn.cn.prepareStatement(query);

            ResultSet rs = pst.executeQuery();

            int column = 0;
            int row = 0;

            while (rs.next()) {
                int id = rs.getInt("id");
                String status = rs.getString("status");
                int numberOfChair = rs.getInt("numberofchair");
                String note = rs.getString("note");

                String labelContent = String.format(
                        "Bàn %d\nSố ghế: %d\nGhi chú: %s",
                        id, numberOfChair, note != null ? note : "Không có"
                );

                Label idLabel = new Label(labelContent);
                idLabel.setStyle("""
                            -fx-border-radius: 10;
                            -fx-background-radius: 10;
                            -fx-border-color: #ddd;
                            -fx-border-width: 2;
                            -fx-padding: 10;
                            -fx-font-size: 14;
                            -fx-font-weight: bold;
                            -fx-min-width: 300;
                        """);

                idLabel.setTextAlignment(TextAlignment.CENTER);
                idLabel.setAlignment(Pos.CENTER);
                idLabel.setWrapText(true);

                idLabel.setOnMouseEntered(e -> idLabel.setStyle(idLabel.getStyle() + "-fx-border-color: #000;"));
                idLabel.setOnMouseExited(e -> idLabel.setStyle(idLabel.getStyle().replace("-fx-border-color: #000;", "")));

                ImageView imageView = new ImageView(new Image(getClass().getResource("/com/example/phongvanrestaurant/Image/table.jpg").toExternalForm()));
                imageView.setFitHeight(80);
                imageView.setFitWidth(80);
                idLabel.setGraphic(imageView);
                idLabel.setContentDisplay(ContentDisplay.TOP);

                if ("Active".equals(status)) {
                    idLabel.setStyle(idLabel.getStyle() + "-fx-background-color: lightgreen;");
                } else if ("Inactive".equals(status)) {
                    idLabel.setStyle(idLabel.getStyle() + "-fx-background-color: lightcoral;");
                } else if ("Unpaid".equals(status)) {
                    idLabel.setStyle(idLabel.getStyle() + "-fx-background-color: purple;");
                } else {
                    idLabel.setStyle(idLabel.getStyle() + "-fx-background-color: lightyellow;");
                }

                idLabel.setOnMouseClicked(event -> {
                    if ("Inactive".equals(status)) {
                        showInactiveTableAlert(id, event);
                    } else if ("Wait".equals(status)) {
                        showWaitTableConfirm(id, event);
                    } else if ("Unpaid".equals(status)) {
                        showUnpaidTableAlert(id,event);
                    } else {
                        showActiveTableOptions(id, event);
                    }
                });

                gridPaneId.add(idLabel, column, row);

                column++;
                if (column == 4) {
                    column = 0;
                    row++;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadMenuView(int tableId, javafx.scene.input.MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/phongvanrestaurant/Cashier/cashier-menu-view.fxml"));
            Parent menuView = loader.load();

            CashierMenuController menuController = loader.getController();
            menuController.setTableId(tableId);


            Scene scene = new Scene(menuView);

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            currentStage.setScene(scene);

            currentStage.setX(0);
            currentStage.setY(0);

            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            currentStage.setWidth(screenBounds.getWidth());
            currentStage.setHeight(screenBounds.getHeight());

            currentStage.setResizable(true);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showInactiveTableAlert(int tableId, javafx.scene.input.MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bàn đã được sử dụng");
        alert.setHeaderText("Bàn này hiện đang được sử dụng. Bạn muốn làm gì?");
        alert.setContentText("Chọn hành động:");

        ButtonType continueButton = new ButtonType("Đặt tiếp");
        ButtonType paymentButton = new ButtonType("Hoàn thành");

        alert.getButtonTypes().setAll(continueButton, paymentButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == continueButton) {
                loadMenuView(tableId, event);
            } else if (result.get() == paymentButton) {
                showPaymentConfirmationAlert(tableId, event);
            } else {
                alert.close();
            }
        }
    }
    private void showUnpaidTableAlert(int tableId, javafx.scene.input.MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/phongvanrestaurant/Cashier/cashier-bill.fxml"));
            AnchorPane pane = loader.load();

            CashierBillController controller = loader.getController();
            controller.setTableController(this);
            controller.setTableId(tableId);
            Scene scene = new Scene(pane);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setOnCloseRequest(e -> {
                loadTableData();
            });
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPaymentConfirmationAlert(int tableId, javafx.scene.input.MouseEvent event) {
        Alert paymentAlert = new Alert(Alert.AlertType.CONFIRMATION);
        paymentAlert.setTitle("Xác nhận hoàn thành");
        paymentAlert.setHeaderText("Bạn có chắc chắn muốn hoàn thành cho bàn này?");
        paymentAlert.setContentText("Nếu bấm có, bàn sẽ được chuyển trạng thái thành 'Chờ thanh toán'.");

        ButtonType yesButton = new ButtonType("Có");
        ButtonType noButton = new ButtonType("Không");

        paymentAlert.getButtonTypes().setAll(yesButton, noButton);
        Optional<ButtonType> result = paymentAlert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            if (checkOrdersAndOrderDetails(tableId)) {
                updateTableStatusToUnpaid(tableId, event);
            } else {
                AlertExample alert = new AlertExample();
                alert.showAlert("Có đơn hàng chưa hoàn thành","Vui lòng kiểm tra lại các đơn hàng trước khi hoàn thành.");
            }
        } else {
            paymentAlert.close();
        }
    }

    private boolean checkOrdersAndOrderDetails(int tableId) {
        String query = "SELECT o.ID, o.Status, od.Status as orderDetailStatus " +
                "FROM Orders o " +
                "JOIN Order_Details od ON o.ID = od.OrderID " +
                "WHERE o.TableID = ?";


        try {
            DBConnection dbcn = DBConnection.getInstance();
            PreparedStatement pst = dbcn.cn.prepareStatement(query);
            pst.setInt(1, tableId);

            ResultSet rs = pst.executeQuery();
            boolean allCompleted = true;

            while (rs.next()) {
                String orderStatus = rs.getString("Status");
                String orderDetailStatus = rs.getString("orderDetailStatus");

                if (!("Unpaid".equals(orderStatus) || "Paid".equals(orderStatus) || "Canceled".equals(orderStatus)) ||
                        !("Delivered".equals(orderDetailStatus) || "Canceled".equals(orderDetailStatus))) {
                    allCompleted = false;
                    break;
                }
            }
            return allCompleted;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void showWaitTableConfirm(int tableId, javafx.scene.input.MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Chọn hành động:");
        alert.setContentText("Khách đã đến, hủy  lịch hẹn hay hủy?");

        ButtonType arrivedButton = new ButtonType("Khách đã đến");
        ButtonType cancelBookingButton = new ButtonType("Hủy lịch hẹn");
        ButtonType cancelButton = new ButtonType("Hủy");

        alert.getButtonTypes().setAll(arrivedButton, cancelBookingButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == arrivedButton) {
                updateTableStatusToInactive(tableId, event);
            } else if (result.get() == cancelBookingButton) {
                showCancelBookingConfirmAlert(tableId, event);
            } else {
                alert.close();
            }
        }
    }

    private void showCancelBookingConfirmAlert(int tableId, javafx.scene.input.MouseEvent event) {
        Alert confirmCancelAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmCancelAlert.setTitle("Xác nhận hủy lịch hẹn");
        confirmCancelAlert.setHeaderText("Bạn có chắc chắn muốn hủy lịch hẹn?");
        confirmCancelAlert.setContentText("Nếu bấm có, bàn sẽ chuyển thành trạng thái Active.");

        ButtonType yesButton = new ButtonType("Có");
        ButtonType noButton = new ButtonType("Không");

        confirmCancelAlert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = confirmCancelAlert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            updateTableStatusToActive(tableId, event);
        } else {
            confirmCancelAlert.close();
        }
    }

    private void updateTableStatusToActive(int tableId, javafx.scene.input.MouseEvent event) {
        try {
            DBConnection dbcn = DBConnection.getInstance();
            String updateQuery = "UPDATE `table` SET status = 'Active', note = NULL WHERE id = ?";
            PreparedStatement pst = dbcn.cn.prepareStatement(updateQuery);
            pst.setInt(1, tableId);
            int rowsUpdated = pst.executeUpdate();

            if (rowsUpdated > 0) {
                loadTableData();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateTableStatusToUnpaid(int tableId, javafx.scene.input.MouseEvent event) {
        try {
            DBConnection dbcn = DBConnection.getInstance();
            String updateQuery = "UPDATE `table` SET status = 'Unpaid', note = NULL WHERE id = ?";
            PreparedStatement pst = dbcn.cn.prepareStatement(updateQuery);
            pst.setInt(1, tableId);
            int rowsUpdated = pst.executeUpdate();

            if (rowsUpdated > 0) {
                loadTableData();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateTableStatusToInactive(int tableId, javafx.scene.input.MouseEvent event) {
        try {
            DBConnection dbcn = DBConnection.getInstance();
            String updateQuery = "UPDATE `table` SET status = 'Inactive', note = NULL WHERE id = ?";
            PreparedStatement pst = dbcn.cn.prepareStatement(updateQuery);
            pst.setInt(1, tableId);
            int rowsUpdated = pst.executeUpdate();

            if (rowsUpdated > 0) {
                loadMenuView(tableId, event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void showActiveTableOptions(int tableId, javafx.scene.input.MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Lựa chọn hành động");
        alert.setHeaderText(null);
        alert.setContentText("Chọn hành động:");

        ButtonType menuButton = new ButtonType("Menu");
        ButtonType bookingButton = new ButtonType("Đặt lịch");
        ButtonType cancelButton = new ButtonType("Hủy");

        alert.getButtonTypes().setAll(menuButton, bookingButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == menuButton) {
                loadMenuView(tableId, event);
            } else if (result.get() == bookingButton) {
                showBookingNoteAlert(tableId, event);
            } else {
                alert.close();
            }
        }
    }

    private void showBookingNoteAlert(int tableId, javafx.scene.input.MouseEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Đặt lịch");
        dialog.setHeaderText("Nhập ghi chú cho bàn:");
        dialog.setContentText("Ghi chú:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(note -> updateTableStatusToWait(tableId, note, event));
    }

    private void updateTableStatusToWait(int tableId, String note, javafx.scene.input.MouseEvent event) {
        try {
            DBConnection dbcn = DBConnection.getInstance();
            String updateQuery = "UPDATE `table` SET status = 'Wait', note = ? WHERE id = ?";
            PreparedStatement pst = dbcn.cn.prepareStatement(updateQuery);
            pst.setString(1, note);
            pst.setInt(2, tableId);
            int rowsUpdated = pst.executeUpdate();

            if (rowsUpdated > 0) {
                loadTableData();
            }
        } catch (SQLException | ClassNotFoundException e) {
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

    public void doLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        if (alert.showAndWait().get().getText().equals("OK")) {
            System.out.println("User logged out!");

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

}
