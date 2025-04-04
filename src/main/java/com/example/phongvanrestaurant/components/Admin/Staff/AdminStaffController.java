package com.example.phongvanrestaurant.components.Admin.Staff;

import com.example.phongvanrestaurant.components.AlertExample;
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
import java.util.ResourceBundle;

public class AdminStaffController implements Initializable {

    @FXML
    private TableView<StaffData> staff_table;

    @FXML
    private TableColumn<StaffData, String> staff_col_id;

    @FXML
    private TableColumn<StaffData, String> staff_col_fullname;

    @FXML
    private TableColumn<StaffData, String> staff_col_phone;

    @FXML
    private TableColumn<StaffData, String> staff_col_role;

    @FXML
    private TableColumn<StaffData, String> staff_col_datecreated;

    @FXML
    private TableColumn<StaffData, String> staff_col_dateupdate;

    @FXML
    private TextField staff_fullname;

    @FXML
    private TextField staff_phone;

    @FXML
    private ComboBox<String> staff_role;

    @FXML
    private Button add_btn;

    @FXML
    private Button update_btn;

    @FXML
    private Button delete_btn;

    @FXML
    private TextField searchField;


    public ObservableList<StaffData> staffList;

    public ObservableList<StaffData> loadStaffData() {
        staffList = FXCollections.observableArrayList();
        try {
            DBConnection dbcn = DBConnection.getInstance();
            String query = "SELECT * FROM staff";
            PreparedStatement pst = dbcn.cn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                StaffData staffData = new StaffData(rs.getInt("id"),
                        rs.getString("fullname"),
                        rs.getString("phone"),
                        rs.getString("role"),
                        rs.getDate("datecreate"),
                        rs.getDate("dateupdate"));

                staffList.add(staffData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staffList;
    }

    public void showStaffData() {
        staff_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        staff_col_fullname.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        staff_col_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        staff_col_role.setCellValueFactory(new PropertyValueFactory<>("role"));
        staff_col_datecreated.setCellValueFactory(new PropertyValueFactory<>("datecreate"));
        staff_col_dateupdate.setCellValueFactory(new PropertyValueFactory<>("dateupdate"));

        staff_table.setItems(loadStaffData());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterStaffList(newValue));
    }

    public void addStaff(ActionEvent event) {
        try {
            String fullnameText = staff_fullname.getText().trim();
            String phoneText = staff_phone.getText().trim();
            String roleText = staff_role.getValue();

            if (fullnameText.isEmpty() || phoneText.isEmpty() || roleText.isEmpty()) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi thêm nhân viên", "Vui lòng điền đầy đủ thông tin");
                return;
            }

            DBConnection dbcn = DBConnection.getInstance();
            String query = "INSERT INTO staff (fullname, phone, role) VALUES (?, ?, ?)";
            PreparedStatement pst = dbcn.cn.prepareStatement(query);

            pst.setString(1, fullnameText);
            pst.setString(2, phoneText);
            pst.setString(3, roleText);

            int result = pst.executeUpdate();
            if (result > 0) {
                AlertExample alert = new AlertExample();
                alert.successAlert("Thêm nhân viên thành công!");
            } else {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi thêm nhân viên", "Thêm nhân viên thất bại");
            }

            showStaffData();
        } catch (Exception e) {
            e.printStackTrace();
            AlertExample alert = new AlertExample();
            alert.showAlert("Lỗi thêm nhân viên", "An unexpected error occurred: " + e.getMessage());
        }
    }

    public void updateStaff(ActionEvent event) {
        try {
            StaffData selectedStaff = staff_table.getSelectionModel().getSelectedItem();
            if (selectedStaff == null) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi chỉnh sửa nhân viên", "Vui lòng chọn một nhân viên để chỉnh sửa");
                return;
            }

            String fullnameText = staff_fullname.getText().trim();
            String phoneText = staff_phone.getText().trim();
            String roleText = staff_role.getValue();

            if (fullnameText.isEmpty() || phoneText.isEmpty() || roleText.isEmpty()) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi chỉnh sửa nhân viên", "Vui lòng điền đầy đủ thông tin");
                return;
            }

            DBConnection dbcn = DBConnection.getInstance();
            String query = "UPDATE staff SET fullname = ?, phone = ?, role = ? WHERE id = ?";
            PreparedStatement pst = dbcn.cn.prepareStatement(query);

            pst.setString(1, fullnameText);
            pst.setString(2, phoneText);
            pst.setString(3, roleText);
            pst.setInt(4, selectedStaff.getId());

            int result = pst.executeUpdate();
            if (result > 0) {
                AlertExample alert = new AlertExample();
                alert.successAlert("Chỉnh sửa nhân viên thành công");
            } else {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi chỉnh sửa nhân viên", "Chỉnh sửa nhân viên thất bại");
            }

            showStaffData();
        } catch (Exception e) {
            e.printStackTrace();
            AlertExample alert = new AlertExample();
            alert.showAlert("Lỗi chỉnh sửa nhân viên", "An unexpected error occurred: " + e.getMessage());
        }
    }

    public void deleteStaff(ActionEvent event) {
        try {
            StaffData selectedStaff = staff_table.getSelectionModel().getSelectedItem();
            if (selectedStaff == null) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi xóa nhân viên", "Vui lòng chọn một nhân viên để xóa");
                return;
            }

            int staffId = selectedStaff.getId();
            DBConnection dbcn = DBConnection.getInstance();

            String checkTimesheetQuery = "SELECT COUNT(*) FROM timesheet WHERE StaffID = ?";
            PreparedStatement pstCheck = dbcn.cn.prepareStatement(checkTimesheetQuery);
            pstCheck.setInt(1, staffId);
            ResultSet rs = pstCheck.executeQuery();

            boolean hasTimesheet = false;
            if (rs.next()) {
                hasTimesheet = rs.getInt(1) > 0;
            }

            if (hasTimesheet) {
                try {
                    dbcn.cn.setAutoCommit(false);

                    String query1 = "DELETE FROM timesheet WHERE StaffID = ?";
                    PreparedStatement pst1 = dbcn.cn.prepareStatement(query1);
                    pst1.setInt(1, staffId);
                    int result1 = pst1.executeUpdate();

                    String query2 = "DELETE FROM staff WHERE ID = ?";
                    PreparedStatement pst2 = dbcn.cn.prepareStatement(query2);
                    pst2.setInt(1, staffId);
                    int result2 = pst2.executeUpdate();

                    if (result1 > 0 && result2 > 0) {
                        dbcn.cn.commit();
                        AlertExample alert = new AlertExample();
                        alert.successAlert("Xóa nhân viên thành công");
                    } else {
                        dbcn.cn.rollback();
                        AlertExample alert = new AlertExample();
                        alert.showAlert("Lỗi xóa nhân viên", "Xóa nhân viên thất bại");
                    }

                } catch (SQLException e) {
                    try {
                        dbcn.cn.rollback();
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                    e.printStackTrace();
                    AlertExample alert = new AlertExample();
                    alert.showAlert("Lỗi hệ thống", "Có lỗi xảy ra trong quá trình xóa nhân viên.");
                } finally {
                    try {
                        dbcn.cn.setAutoCommit(true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                String query = "DELETE FROM staff WHERE ID = ?";
                PreparedStatement pst = dbcn.cn.prepareStatement(query);
                pst.setInt(1, staffId);
                int result = pst.executeUpdate();

                if (result > 0) {
                    AlertExample alert = new AlertExample();
                    alert.successAlert("Xóa nhân viên thành công");
                } else {
                    AlertExample alert = new AlertExample();
                    alert.showAlert("Lỗi xóa nhân viên", "Xóa nhân viên thất bại");
                }
            }

            showStaffData();

        } catch (Exception e) {
            e.printStackTrace();
            AlertExample alert = new AlertExample();
            alert.showAlert("Lỗi xóa nhân viên", "An unexpected error occurred: " + e.getMessage());
        }
    }

    public void clearFields(ActionEvent event) {
        staff_fullname.clear();
        staff_phone.clear();
        staff_role.setValue(null);
    }

    private void filterStaffList(String query) {
        if (query == null || query.isEmpty()) {
            staff_table.setItems(staffList);
            return;
        }

        ObservableList<StaffData> filteredList = FXCollections.observableArrayList();
        for (StaffData data : staffList) {
            if (data.getFullname().toLowerCase().contains(query.toLowerCase()) || data.getPhone().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(data);
            }
        }
        staff_table.setItems(filteredList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> roleList = FXCollections.observableArrayList("Admin", "NormalStaff","Cashier","Kitchen");
        staff_role.setItems(roleList);
        showStaffData();
        staff_table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                StaffData selectedStaff = staff_table.getSelectionModel().getSelectedItem();
                staff_fullname.setText(selectedStaff.getFullname());
                staff_phone.setText(selectedStaff.getPhone());
                staff_role.setValue(selectedStaff.getRole());
            }
        });
    }
}
