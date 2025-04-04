package com.example.phongvanrestaurant.components.Admin.Orders;

import com.example.phongvanrestaurant.components.Admin.Orders.OrderDetails.AdminOrderDetailsController;
import com.example.phongvanrestaurant.components.AlertExample;
import com.example.phongvanrestaurant.models.DBConnection;
import com.example.phongvanrestaurant.models.Orders;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AdminOrdersController implements Initializable {

    @FXML
    private TableView<Orders> orders_table;

    @FXML
    private TableColumn<Orders, Integer> orders_col_id;

    @FXML
    private TableColumn<Orders, Integer> orders_col_tableId;

    @FXML
    private TableColumn<Orders, String> orders_col_status;

    @FXML
    private TableColumn<Orders, String> orders_col_note;

    @FXML
    private TableColumn<Orders, String> orders_col_dateCreate;

    @FXML
    private TableColumn<Orders, String> orders_col_select;

    @FXML
    private TextField orders_tableId;

    @FXML
    private TextField orders_status;

    @FXML
    private TextField orders_note;

    @FXML
    private DatePicker dateFromPicker;

    @FXML
    private DatePicker dateToPicker;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Button filterByDateBtn;

    @FXML
    private Button showDetailsBtn;

    private ObservableList<Orders> ordersList;


    public ObservableList<Orders> loadOrders() {
        ordersList = FXCollections.observableArrayList();
        try {
            DBConnection dbcn = DBConnection.getInstance();
            String query = "SELECT * FROM orders";
            PreparedStatement pst = dbcn.cn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Orders order = new Orders(
                        rs.getInt("id"),
                        rs.getInt("tableId"),
                        rs.getString("status"),
                        rs.getString("note"),
                        rs.getDate("dateCreate")
                );
                ordersList.add(order);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ordersList;
    }

    @FXML
    public void filterOrders() {
        LocalDate fromDate = dateFromPicker.getValue();
        LocalDate toDate = dateToPicker.getValue();
        String status = statusComboBox.getValue();

        ordersList = FXCollections.observableArrayList();
        try {
            DBConnection dbcn = DBConnection.getInstance();
            StringBuilder query = new StringBuilder("SELECT * FROM orders WHERE 1=1");

            if (fromDate != null && toDate != null) {
                if (fromDate.isEqual(toDate)) {
                    query.append(" AND DATE(dateCreate) = ?");
                } else {
                    // If different dates, use BETWEEN
                    query.append(" AND DATE(dateCreate) BETWEEN ? AND ?");
                }
            }

            if (status != null && !status.equals("All") && !status.isEmpty()) {
                query.append(" AND status = ?");
            }

            PreparedStatement pst = dbcn.cn.prepareStatement(query.toString());

            int paramIndex = 1;
            if (fromDate != null && toDate != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (fromDate.isEqual(toDate)) {
                    pst.setString(paramIndex++, fromDate.format(formatter));
                } else {
                    pst.setString(paramIndex++, fromDate.format(formatter));
                    pst.setString(paramIndex++, toDate.format(formatter));
                }
            }

            if (status != null && !status.equals("All") && !status.isEmpty()) {
                pst.setString(paramIndex, status);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Orders order = new Orders(
                        rs.getInt("id"),
                        rs.getInt("tableId"),
                        rs.getString("status"),
                        rs.getString("note"),
                        rs.getDate("dateCreate")
                );
                ordersList.add(order);
            }

            ordersList.sort((o1, o2) -> o2.getDateCreate().compareTo(o1.getDateCreate()));
            orders_table.setItems(ordersList);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void showData() {
        orders_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        orders_col_tableId.setCellValueFactory(new PropertyValueFactory<>("tableId"));
        orders_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        orders_col_note.setCellValueFactory(new PropertyValueFactory<>("note"));
        orders_col_select.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        orders_col_dateCreate.setCellValueFactory(new PropertyValueFactory<>("dateCreate"));

        orders_table.setItems(loadOrders());
    }

    public void showDetails(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/phongvanrestaurant/Admin/admin-order-details.fxml"));
            AnchorPane pane = loader.load();

            AdminOrderDetailsController controller = loader.getController();
            Orders selectedOrder = orders_table.getSelectionModel().getSelectedItem();
            controller.setOrderId(selectedOrder.getId());
            System.out.println(selectedOrder.getId());
            Scene scene = new Scene(pane);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void confirmOrders() {
        ObservableList<Orders> selectedOrders = FXCollections.observableArrayList();


        for (Orders order : ordersList){
            if (order.getCheckBox().isSelected()){
                selectedOrders.add(order);
            }
        }

        if (selectedOrders.isEmpty()) {
            AlertExample alert = new AlertExample();
            alert.showAlert("Thông báo","Chưa có đơn hàng nào được chọn, vui lòng chọn để tiếp tục.");
            return;
        }

        for (Orders order : selectedOrders) {
            String status = order.getStatus();
            if (!"Wait For Confirmation".equalsIgnoreCase(status)) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi","Vui lòng chỉ xác nhận những đơn hàng chưa được xác nhận!");
                return;
            }
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Xác nhận");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Bạn có chắc chắn muốn xác nhận các đơn hàng đã chọn?");

        ButtonType confirmButton = new ButtonType("Xác nhận");
        ButtonType cancelButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(confirmButton, cancelButton);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                DBConnection dbcn = null;
                try {
                    dbcn = DBConnection.getInstance();
                    if (dbcn == null || dbcn.cn == null) {
                        throw new SQLException("Không thể kết nối tới cơ sở dữ liệu.");
                    }

                    dbcn.cn.setAutoCommit(false);

                    String updateQuery = "UPDATE orders SET status = ? WHERE id = ?";
                    String updateOrderDetailsQuery = "UPDATE order_details SET status = ? WHERE orderid = ? AND status != 'Canceled'";

                    try (
                            PreparedStatement pst = dbcn.cn.prepareStatement(updateQuery);
                            PreparedStatement orderDetailsPst = dbcn.cn.prepareStatement(updateOrderDetailsQuery)
                    ) {
                        for (Orders order : selectedOrders) {
                            pst.setString(1, "On Going");
                            pst.setInt(2, order.getId());
                            pst.addBatch();

                            orderDetailsPst.setString(1, "On Going");
                            orderDetailsPst.setInt(2, order.getId());
                            orderDetailsPst.addBatch();
                        }

                        int[] updatedRows = pst.executeBatch();
                        int[] updatedOrderDetailsRows = orderDetailsPst.executeBatch();

                        boolean allOrdersUpdated = true;
                        for (int count : updatedRows) {
                            if (count == 0) {
                                allOrdersUpdated = false;
                                break;
                            }
                        }
                        boolean allDetailsUpdated = true;
                        for (int count : updatedOrderDetailsRows) {
                            if (count == 0) {
                                allDetailsUpdated = false;
                                break;
                            }
                        }

                        if (allOrdersUpdated && allDetailsUpdated) {
                            dbcn.cn.commit();
                            AlertExample alert = new AlertExample();
                            alert.successAlert("Đã xác nhận các đơn hàng thành công.");

                            orders_table.setItems(loadOrders());
                        } else {
                            dbcn.cn.rollback();
                            throw new SQLException("Không thể cập nhật tất cả các đơn hàng hoặc chi tiết đơn hàng.");
                        }
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    try {
                        if (dbcn != null && dbcn.cn != null) {
                            dbcn.cn.rollback();
                        }
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                    e.printStackTrace();
                    AlertExample alert = new AlertExample();
                    alert.showAlert("Lỗi", "Đã xảy ra lỗi khi cập nhật đơn hàng: " + e.getMessage());
                } finally {
                    try {
                        if (dbcn != null && dbcn.cn != null) {
                            dbcn.cn.setAutoCommit(true);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                confirmationAlert.close();
            }
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showData();
        orders_table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        statusComboBox.getSelectionModel().selectFirst();

        filterByDateBtn.setOnAction(event -> filterOrders());

        orders_table.setOnMouseClicked(event -> {
            int selectedCount = orders_table.getSelectionModel().getSelectedItems().size(); // Lấy số lượng mục được chọn
            if (selectedCount >= 2) {
                showDetailsBtn.setVisible(false); // Ẩn nút nếu >= 2 đơn
            } else {
                showDetailsBtn.setVisible(true);  // Hiển thị nút nếu < 2 đơn
            }
        });
    }
}
