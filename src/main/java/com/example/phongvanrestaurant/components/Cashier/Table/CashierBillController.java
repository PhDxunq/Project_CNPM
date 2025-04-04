package com.example.phongvanrestaurant.components.Cashier.Table;

import com.example.phongvanrestaurant.models.BillItems;
import com.example.phongvanrestaurant.models.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CashierBillController {
    private int tableId;

    private CashierTableController cashierTableController;


    public void setTableId(int tableId) {
        this.tableId = tableId;
        loadItem();
    }

    public void setTableController(CashierTableController cashierTableController) {
        this.cashierTableController = cashierTableController;
    }

    @FXML
    private TableView<BillItems> itemsTable;

    @FXML
    private TableColumn<BillItems, Integer> orderIdCol;

    @FXML
    private TableColumn<BillItems, String> orderStatusCol;

    @FXML
    private TableColumn<BillItems, Integer> quantityCol;

    @FXML
    private TableColumn<BillItems, Integer> totalAmountCol;

    @FXML
    private TableColumn<BillItems, String> itemNameCol;

    @FXML
    private TableColumn<BillItems, Integer> priceCol;

    @FXML
    private TableColumn<BillItems, Integer> discountCol;

    @FXML
    private Button saveButton;

    private ObservableList<BillItems> billList;

    @FXML
    public void initialize() {
        showItem();
        loadItem();
        saveButton.setOnAction(event -> updStatus());
    }

    public void loadItem() {
        billList = getOrdersByTableId();
        itemsTable.setItems(billList);
    }

    public void showItem(){
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderStatusCol.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalAmountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        discountCol.setCellValueFactory(new PropertyValueFactory<>("discount"));

        billList = getOrdersByTableId();
        itemsTable.setItems(billList);
    }

    public ObservableList<BillItems> getOrdersByTableId() {
        ObservableList<BillItems> itemsList = FXCollections.observableArrayList();
        try {
            DBConnection dbcn = DBConnection.getInstance();

                String qrySelect = "SELECT orders.ID AS OrderID, orders.Status AS OrderStatus, " +
                        "order_details.ID AS OrderDetailID, order_details.ItemID, order_details.Quantity, " +
                        "order_details.Status AS OrderDetailStatus, order_details.TotalAmount, " +
                        "items.ItemName, items.Price, items.Discount " +
                        "FROM orders " +
                        "JOIN order_details ON orders.ID = order_details.OrderID " +
                        "JOIN items ON order_details.ItemID = items.ID " +
                        "WHERE orders.TableID = ? AND orders.Status = 'Unpaid' AND order_details.Status = 'Delivered'";

            PreparedStatement pst = dbcn.cn.prepareStatement(qrySelect);
            pst.setInt(1, tableId);
            pst.execute();
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                BillItems item = new BillItems(
                        rs.getInt("OrderID"),
                        rs.getString("OrderStatus"),
                        rs.getInt("OrderDetailID"),
                        rs.getInt("ItemID"),
                        rs.getInt("Quantity"),
                        rs.getString("OrderDetailStatus"),
                        rs.getInt("TotalAmount"),
                        rs.getString("ItemName"),
                        rs.getInt("Discount"),
                        rs.getInt("Price")
                );
                itemsList.add(item);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return itemsList;
    }

    public void updStatus() {
        int totalAmount = 0;
        for (BillItems item : billList) {
            totalAmount += item.getTotalAmount();
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận thanh toán");
        alert.setHeaderText(null);
        alert.setContentText("Tổng tiền của quý khách là: " + totalAmount + "\nBạn có muốn xác nhận thanh toán không?");
        ButtonType confirmButton = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                try {
                    DBConnection dbcn = DBConnection.getInstance();
                    dbcn.cn.setAutoCommit(false);

                    String updateOrderStatus = "UPDATE orders SET Status = 'Paid' WHERE ID = ?";
                    PreparedStatement orderPst = dbcn.cn.prepareStatement(updateOrderStatus);

                    for (BillItems item : billList) {
                        orderPst.setInt(1, item.getOrderId());
                        orderPst.addBatch();
                    }

                    String updateTableStatus = "UPDATE `table` SET Status = 'Active' WHERE ID = ?";
                    PreparedStatement tablePst = dbcn.cn.prepareStatement(updateTableStatus);
                    tablePst.setInt(1, tableId);
                    tablePst.addBatch();

                    orderPst.executeBatch();
                    tablePst.executeBatch();
                    dbcn.cn.commit();

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Thành công");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Đã thanh toán thành công!");
                    successAlert.showAndWait();

                    loadItem();

                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    try {
                        DBConnection dbcn = DBConnection.getInstance();
                        dbcn.cn.rollback();
                    } catch (SQLException | ClassNotFoundException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }

                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Lỗi");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Đã xảy ra lỗi khi thanh toán. Vui lòng thử lại.");
                    errorAlert.showAndWait();
                } finally {
                    try {
                        DBConnection dbcn = DBConnection.getInstance();
                        dbcn.cn.setAutoCommit(true);
                    } catch (SQLException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                alert.close();
            }
        });
    }

}
