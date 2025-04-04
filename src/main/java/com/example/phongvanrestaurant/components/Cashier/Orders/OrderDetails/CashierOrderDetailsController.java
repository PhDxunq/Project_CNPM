package com.example.phongvanrestaurant.components.Cashier.Orders.OrderDetails;

import com.example.phongvanrestaurant.components.AlertExample;
import com.example.phongvanrestaurant.models.DBConnection;
import com.example.phongvanrestaurant.models.OrderItems;
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
import java.util.ResourceBundle;

public class CashierOrderDetailsController implements Initializable {

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
    private Button saveButton;

    private ObservableList<OrderItems> itemsList;

    private int orderId;

    public void setOrderId(int orderId) {
        this.orderId = orderId;
        showItem();
    }

    public ObservableList<OrderItems> inventoryItem()  {
        itemsList = FXCollections.observableArrayList();
        try {
            OrderItems orderItems;
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
                orderItems = new OrderItems(
                        rs.getInt("id"),
                        rs.getString("ItemName"),
                        rs.getString("ItemId"),
                        rs.getInt("Quantity"),
                        rs.getDouble("TotalAmount"),
                        FXCollections.observableArrayList("Pending", "Delivered","Cancelled")
                );
                orderItems.getStatusCombo().setValue(rs.getString("Status"));
                itemsList.add(orderItems);
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
        itemStatusColumn.setCellValueFactory(new PropertyValueFactory<>("statusCombo"));
        itemsTable.setItems(inventoryItem());
    }

    public void loadItem() {
        itemsTable.setItems(inventoryItem());
    }

    public void updStatus(ActionEvent event) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Xác nhận");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Bạn có chắc chắn muốn xác nhận trạng thái cho các món hàng đã chọn?");

        ButtonType confirmButton = new ButtonType("Xác nhận");
        ButtonType cancelButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(confirmButton, cancelButton);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                try {
                    DBConnection dbcn = DBConnection.getInstance();
                    String qryUpdate = "UPDATE order_details SET Status = ? WHERE id = ?";
                    PreparedStatement pst = dbcn.cn.prepareStatement(qryUpdate);

                    for (OrderItems orderItems : itemsList) {
                        if (orderItems.getStatusCombo().getValue() != null) {
                            pst.setString(1, orderItems.getStatusCombo().getValue().toString());
                            pst.setInt(2, orderItems.getId());
                            pst.executeUpdate();
                        } else {
                            AlertExample alert = new AlertExample();
                            alert.showAlert("Lỗi", "Vui lòng chọn trạng thái cho món hàng " + orderItems.getItemName());
                            return;
                        }
                    }

                    AlertExample alert = new AlertExample();
                    alert.successAlert("Đã xác nhận trạng thái cho các món hàng.");
                    loadItem();

                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    AlertExample alert = new AlertExample();
                    alert.showAlert("Lỗi", "Đã xảy ra lỗi khi cập nhật trạng thái món hàng. Vui lòng thử lại.");
                }
            }
        });
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showItem();
        loadItem();
    }
}
