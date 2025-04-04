package com.example.phongvanrestaurant.components.Kitchen.Orders.OrderDetails;

import com.example.phongvanrestaurant.models.DBConnection;
import com.example.phongvanrestaurant.models.OrderItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class KitchenOrderDetailsController implements Initializable {

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

    public ObservableList<OrderItems> inventoryItem()  {
        itemsList = FXCollections.observableArrayList();
        try {
            DBConnection dbcn = DBConnection.getInstance();
            String qrySelect = "SELECT order_details.*, items.ItemName AS ItemName " +
                    "FROM order_details " +
                    "JOIN items ON order_details.ItemID =  items.id " +
                    "WHERE order_details.OrderID = ?";
            PreparedStatement pst = dbcn.cn.prepareStatement(qrySelect);
            pst.setInt(1, orderId);
            pst.execute();
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

                if ("On Going".equals(rs.getString("Status"))) {
                    Button confirmButton = new Button("Hoàn thành");
                    confirmButton.setOnAction(e -> confirmDone(orderItem));
                    orderItem.setActionButton(confirmButton);
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

    public void loadItem() {
        itemsTable.setItems(inventoryItem());
    }

    private void confirmDone(OrderItems orderItem) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Xác nhận");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Xác nhận đã hoàn thành món ăn?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                DBConnection dbcn = DBConnection.getInstance();

                String updateOrderDetails = "UPDATE order_details SET Status = 'Done' WHERE id = ?";
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
                    if (!"Done".equals(status) && !"Canceled".equals(status) && !"Delivered".equals(status)) {
                        allDeliveredOrCanceled = false;
                        break;
                    }
                }
                if (allDeliveredOrCanceled) {
                    String updateOrder = "UPDATE orders SET Status = 'Kitchen Done' WHERE id = ?";
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showItem();
        loadItem();
    }
}
