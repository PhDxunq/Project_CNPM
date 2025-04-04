package com.example.phongvanrestaurant.components.Staff.Orders.OrderDetails;

import com.example.phongvanrestaurant.models.OrderItems;
import com.example.phongvanrestaurant.models.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class StaffOrderDetailsController implements Initializable {
    @FXML
    private TableView<OrderItems> itemsTable;

    @FXML
    private TableColumn<OrderItems, Integer> itemIdColumn;

    @FXML
    private TableColumn<OrderItems, String> itemNameColumn;

    @FXML
    private TableColumn<OrderItems, Integer> itemQuantityColumn;

    @FXML
    private TableColumn<OrderItems, Double> itemTotalAmountColumn;

    @FXML
    private TableColumn<OrderItems, String> itemStatusColumn;

    @FXML
    private TableColumn<OrderItems, Button> actionColumn;

    private ObservableList<OrderItems> itemsList;

    private int orderId;

    public void setOrderId(int orderId) {
        this.orderId = orderId;
        showItem();
    }

    public ObservableList<OrderItems> inventoryItem() {
        itemsList = FXCollections.observableArrayList();
        try {
            DBConnection dbcn = DBConnection.getInstance();
            String qrySelect = "SELECT order_details.*, items.ItemName AS ItemName " +
                    "FROM order_details " +
                    "JOIN items ON order_details.ItemID = items.id " +
                    "WHERE order_details.OrderID = ?";
            PreparedStatement pst = dbcn.cn.prepareStatement(qrySelect);
            pst.setInt(1, orderId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                OrderItems orderItem = new OrderItems(
                        rs.getInt("id"),
                        rs.getString("ItemName"),
                        rs.getString("ItemId"),
                        rs.getInt("Quantity"),
                        rs.getDouble("TotalAmount"),
                        rs.getString("Status")
                );

                if ("Done".equals(rs.getString("Status"))) {
                    Button confirmButton = new Button("Xác nhận");
                    confirmButton.setOnAction(e -> confirmDelivery(orderItem));
                    orderItem.setActionButton(confirmButton);
                }

                if ("Pending".equals(rs.getString("Status"))) {
                    Button manageButton = new Button("Hủy/Thay đổi số lượng");
                    manageButton.setOnAction(e -> managePendingItem(orderItem));
                    orderItem.setActionButton(manageButton);
                }

                itemsList.add(orderItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemsList;
    }

    public void showItem() {
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("ItemName"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        itemTotalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("TotalAmount"));
        itemStatusColumn.setCellValueFactory(new PropertyValueFactory<>("Status"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("actionButton"));

        itemsTable.setItems(inventoryItem());
    }

    private void confirmDelivery(OrderItems orderItem) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Xác nhận");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Bạn có chắc chắn xác nhận đã mang món ra cho khách hàng không?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                DBConnection dbcn = DBConnection.getInstance();

                String updateOrderDetails = "UPDATE order_details SET Status = 'Delivered' WHERE id = ?";
                PreparedStatement pstDetails = dbcn.cn.prepareStatement(updateOrderDetails);
                pstDetails.setInt(1, orderItem.getId());
                pstDetails.executeUpdate();

                String checkDetailsStatus = "SELECT Status FROM order_details WHERE OrderID = ?";
                PreparedStatement pstCheck = dbcn.cn.prepareStatement(checkDetailsStatus);
                pstCheck.setInt(1, orderId);
                ResultSet rs = pstCheck.executeQuery();

                boolean allDeliveredOrCanceled = true;
                while (rs.next()) {
                    String status = rs.getString("Status");
                    if (!"Delivered".equals(status) && !"Canceled".equals(status)) {
                        allDeliveredOrCanceled = false;
                        break;
                    }
                }

                if (allDeliveredOrCanceled) {
                    String updateOrder = "UPDATE orders SET Status = 'Unpaid' WHERE id = ?";
                    PreparedStatement pstOrder = dbcn.cn.prepareStatement(updateOrder);
                    pstOrder.setInt(1, orderId);
                    pstOrder.executeUpdate();
                }

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Thành công");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Đã cập nhật trạng thái thành công!");
                successAlert.show();

                showItem();

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Lỗi");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Có lỗi xảy ra khi cập nhật trạng thái!");
                errorAlert.show();
            }
        }
    }

    private void managePendingItem(OrderItems orderItem) {
        Alert manageAlert = new Alert(Alert.AlertType.CONFIRMATION);
        manageAlert.setTitle("Quản lý món ăn");
        manageAlert.setHeaderText(null);
        manageAlert.setContentText("Bạn muốn làm gì với món này?");

        ButtonType cancelItemButton = new ButtonType("Hủy món");
        ButtonType changeQuantityButton = new ButtonType("Thay đổi số lượng");
        ButtonType closeButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);

        manageAlert.getButtonTypes().setAll(cancelItemButton, changeQuantityButton, closeButton);

        Optional<ButtonType> result = manageAlert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == cancelItemButton) {
                cancelItem(orderItem);
            } else if (result.get() == changeQuantityButton) {
                changeQuantity(orderItem);
            }
        }
    }

    private void cancelItem(OrderItems orderItem) {
        try {
            DBConnection dbcn = DBConnection.getInstance();
            String updateStatus = "UPDATE order_details SET Status = 'Canceled' WHERE id = ?";
            PreparedStatement pst = dbcn.cn.prepareStatement(updateStatus);
            pst.setInt(1, orderItem.getId());
            pst.executeUpdate();

            String updateItemQuantity = "UPDATE items SET Quantity = Quantity + ? WHERE id = ?";
            PreparedStatement pstItemQuantity = dbcn.cn.prepareStatement(updateItemQuantity);
            pstItemQuantity.setInt(1, orderItem.getQuantity());
            pstItemQuantity.setString(2, orderItem.getItemId());
            pstItemQuantity.executeUpdate();

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Thành công");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Món ăn đã được hủy thành công!");
            successAlert.show();

            showItem();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Lỗi");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Có lỗi xảy ra khi hủy món ăn!");
            errorAlert.show();
        }
    }

    private void changeQuantity(OrderItems orderItem) {
        TextInputDialog quantityDialog = new TextInputDialog(String.valueOf(orderItem.getQuantity()));
        quantityDialog.setTitle("Thay đổi số lượng");
        quantityDialog.setHeaderText(null);
        quantityDialog.setContentText("Nhập số lượng mới:");

        Optional<String> result = quantityDialog.showAndWait();
        if (result.isPresent()) {
            try {
                int newQuantity = Integer.parseInt(result.get());
                if (newQuantity > 0) {
                    DBConnection dbcn = DBConnection.getInstance();
                    dbcn.cn.setAutoCommit(false);
                    String updateQuantity = "UPDATE order_details SET Quantity = ? WHERE id = ?";
                    PreparedStatement pst = dbcn.cn.prepareStatement(updateQuantity);
                    pst.setInt(1, newQuantity);
                    pst.setInt(2, orderItem.getId());
                    pst.executeUpdate();

                    String updateItemQuantity = "UPDATE items SET Quantity = Quantity - ? WHERE id = ?";
                    PreparedStatement pstItemQuantity = dbcn.cn.prepareStatement(updateItemQuantity);
                    pstItemQuantity.setInt(1, newQuantity - orderItem.getQuantity());
                    pstItemQuantity.setString(2, orderItem.getItemId());
                    pstItemQuantity.executeUpdate();

                    String checkItemQuantity = "SELECT Quantity FROM items WHERE id = ?";
                    PreparedStatement pstCheck = dbcn.cn.prepareStatement(checkItemQuantity);
                    pstCheck.setString(1, orderItem.getItemId());
                    ResultSet rs = pstCheck.executeQuery();
                    if (rs.next()) {
                        int itemQuantity = rs.getInt("Quantity");
                        if (itemQuantity < 0) {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Lỗi");
                            errorAlert.setHeaderText(null);
                            errorAlert.setContentText("Số lượng trong kho không đủ để cung cấp!");
                            errorAlert.show();
                            dbcn.cn.rollback();
                            return;
                        }
                    }
                    dbcn.cn.commit();

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Thành công");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Số lượng đã được cập nhật thành công!");
                    successAlert.show();

                    showItem();
                } else {
                    throw new NumberFormatException("Số lượng phải lớn hơn 0.");
                }
            } catch (NumberFormatException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Lỗi nhập liệu");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Vui lòng nhập một số nguyên hợp lệ lớn hơn 0!");
                errorAlert.show();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Lỗi");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Có lỗi xảy ra khi cập nhật số lượng!");
                errorAlert.show();
            }
        }
    }

    @FXML
    private void cancelOrder(ActionEvent event) {
        Alert cancelOrderAlert = new Alert(Alert.AlertType.CONFIRMATION);
        cancelOrderAlert.setTitle("Hủy đơn hàng");
        cancelOrderAlert.setHeaderText(null);
        cancelOrderAlert.setContentText("Bạn có chắc chắn muốn hủy toàn bộ đơn hàng này không?");

        Optional<ButtonType> result = cancelOrderAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                DBConnection dbcn = DBConnection.getInstance();

                String checkDetailsStatus = "SELECT Status FROM order_details WHERE OrderID = ?";
                PreparedStatement pstCheck = dbcn.cn.prepareStatement(checkDetailsStatus);
                pstCheck.setInt(1, orderId);
                ResultSet rs = pstCheck.executeQuery();

                boolean canCancel = true;
                while (rs.next()) {
                    String status = rs.getString("Status");
                    if (!"Pending".equals(status) &&
                            !"Wait For Confirmation".equals(status) &&
                            !"Canceled".equals(status)) {
                        canCancel = false;
                        break;
                    }
                }

                if (canCancel) {
                    String updateDetails = "UPDATE order_details SET Status = 'Canceled' WHERE OrderID = ?";
                    PreparedStatement pstDetails = dbcn.cn.prepareStatement(updateDetails);
                    pstDetails.setInt(1, orderId);
                    pstDetails.executeUpdate();

                    String updateOrder = "UPDATE orders SET Status = 'Canceled' WHERE id = ?";
                    PreparedStatement pstOrder = dbcn.cn.prepareStatement(updateOrder);
                    pstOrder.setInt(1, orderId);
                    pstOrder.executeUpdate();

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Thành công");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Đơn hàng đã được hủy thành công!");
                    successAlert.show();

                    showItem();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Không thể hủy");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Không thể hủy đơn hàng vì có món đang được chuẩn bị!");
                    errorAlert.show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Lỗi");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Có lỗi xảy ra khi hủy đơn hàng!");
                errorAlert.show();
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showItem();
    }
}
