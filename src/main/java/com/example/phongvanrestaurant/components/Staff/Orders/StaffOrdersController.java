package com.example.phongvanrestaurant.components.Staff.Orders;

import com.example.phongvanrestaurant.models.Orders;
import com.example.phongvanrestaurant.components.Staff.Account.StaffChangePasswordController;
import com.example.phongvanrestaurant.components.Staff.Orders.OrderDetails.StaffOrderDetailsController;
import com.example.phongvanrestaurant.models.DBConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class StaffOrdersController implements Initializable {

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

    private int staffId;
    public void setStaffId(int staffId){
        this.staffId = staffId;
    }

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/phongvanrestaurant/Staff/staff-order-details.fxml"));
            AnchorPane pane = loader.load();
            StaffOrderDetailsController controller = loader.getController();
            Orders selectedOrder = orders_table.getSelectionModel().getSelectedItem();
            controller.setOrderId(selectedOrder.getId());
            System.out.println(selectedOrder.getId());
            Scene scene = new Scene(pane);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setOnCloseRequest(e -> {
                loadOrders();
                showData();
            });
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showData();
        orders_table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        statusComboBox.getSelectionModel().selectFirst();
        filterByDateBtn.setOnAction(event -> filterOrders());

        orders_table.setOnMouseClicked(event -> {
            int selectedCount = orders_table.getSelectionModel().getSelectedItems().size();
            if (selectedCount >= 2) {
                showDetailsBtn.setVisible(false);
            } else {
                showDetailsBtn.setVisible(true);
            }
        });
    }

    public void doCancel() {
        Platform.exit();
    }

    public void doMinimize(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    public void changeTableView(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/phongvanrestaurant/Staff/staff-order-select-table-view.fxml"));
        Scene scene = new Scene(loader.load());
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

    @FXML
    private void changePassword(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/phongvanrestaurant/Staff/staff-change-password-view.fxml"));
        Scene scene = new Scene(loader.load());
        StaffChangePasswordController controller = loader.getController();
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
}
