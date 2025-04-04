package com.example.phongvanrestaurant.components.Admin.Account;

import com.example.phongvanrestaurant.components.AlertExample;
import com.example.phongvanrestaurant.models.AccountData;
import com.example.phongvanrestaurant.models.DBConnection;
import com.example.phongvanrestaurant.models.TimesheetData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminAccountController implements Initializable {

    @FXML
    private Button account_addbtn;

    @FXML
    private Button account_btn;

    @FXML
    private Button account_clearbtn;

    @FXML
    private TableColumn<AccountData, String> account_col_datecreated;

    @FXML
    private TableColumn<AccountData, String> account_col_dateupdate;

    @FXML
    private TableColumn<AccountData, String> account_col_id;

    @FXML
    private TableColumn<AccountData, String> account_col_password;

    @FXML
    private TableColumn<AccountData, String> account_col_staffid;

    @FXML
    private TableColumn<AccountData, String> account_col_username;

    @FXML
    private TableColumn<AccountData, String> account_col_role;

    @FXML
    private TableColumn<AccountData, String> account_col_fullname;

    @FXML
    private Button account_delbtn;

    @FXML
    private TableView<AccountData> account_table;

    @FXML
    private Button account_updbtn;

    @FXML
    private Button customer_btn;

    @FXML
    private Button inventory_btn;

    @FXML
    private Button menu_btn;

    @FXML
    private Label username;

    @FXML
    private TextField searchField;

    @FXML
    private TextField account_username;

    @FXML
    private TextField account_password;

    @FXML
    private ComboBox<StaffData> account_staffname;

    private ObservableList<StaffData> staffList;

    private void loadStaffToComboBox() {
        staffList = FXCollections.observableArrayList();

        try {
            DBConnection dbcn = DBConnection.getInstance();
            String query = "SELECT id, fullname, role FROM staff";
            PreparedStatement pst = dbcn.cn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                StaffData staff = new StaffData(rs.getInt("id"), rs.getString("fullname"), rs.getString("role"));
                staffList.add(staff);
            }

            account_staffname.setItems(staffList);

            account_staffname.setConverter(new StringConverter<>() {
                @Override
                public String toString(StaffData staff) {
                    if (staff != null) {
                        return staff.getId() + " - " + staff.getFullName() + " - " + staff.getRole();
                    }
                    return "";
                }

                @Override
                public StaffData fromString(String string) {
                    return null;
                }
            });

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<AccountData> accountList;

    public ObservableList<AccountData> inventoryAccount() {
        accountList = FXCollections.observableArrayList();
        try {
            AccountData accountData;
            DBConnection dbcn = DBConnection.getInstance();
            String querySel = "SELECT accounts.*, staff.* FROM accounts " +
                    "JOIN staff ON accounts.staffid = staff.id";
            PreparedStatement pst = dbcn.cn.prepareStatement(querySel);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                accountData = new AccountData(rs.getInt("id")
                        , rs.getInt("staffid")
                        , rs.getString("fullname")
                        , rs.getString("role")
                        , rs.getString("username")
                        , rs.getString("password")
                        , rs.getDate("datecreate")
                        , rs.getDate("dateupdate"));

                accountList.add(accountData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountList;
    }

    public void showData() {
        account_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        account_col_staffid.setCellValueFactory(new PropertyValueFactory<>("staffid"));
        account_col_fullname.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        account_col_role.setCellValueFactory(new PropertyValueFactory<>("role"));
        account_col_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        account_col_password.setCellValueFactory(new PropertyValueFactory<>("password"));
        account_col_datecreated.setCellValueFactory(new PropertyValueFactory<>("datecreated"));
        account_col_dateupdate.setCellValueFactory(new PropertyValueFactory<>("dateupdate"));

        account_table.setItems(inventoryAccount());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterAccountList(newValue));
    }

    public void addAccount(ActionEvent event) {
        int staffId = 0;
        try {
            StaffData selectedStaff = account_staffname.getSelectionModel().getSelectedItem();
            if (selectedStaff != null) {
                staffId = selectedStaff.getId();
                System.out.println("Staff ID: " + staffId);
            }
            String usernameText = account_username.getText().trim();
            String passwordText = account_password.getText().trim();

            if (selectedStaff == null || usernameText.isEmpty() || passwordText.isEmpty()) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi thêm tài khoản", "Vui lòng điền đầy đủ thông tin");
                return;
            }

            DBConnection dbcn = DBConnection.getInstance();
            if (staffId != 0) {
                String queryInsert = "INSERT INTO accounts (staffid, username, password) VALUES (?, ?, ?)";
                PreparedStatement pst = dbcn.cn.prepareStatement(queryInsert);

                pst.setInt(1, staffId);
                pst.setString(2, usernameText);
                pst.setString(3, passwordText);

                int result = pst.executeUpdate();
                if (result > 0) {
                    AlertExample alert = new AlertExample();
                    alert.successAlert("Thêm tài khoản thành công!");
                } else {
                    AlertExample alert = new AlertExample();
                    alert.showAlert("Lỗi thêm tài khoản", "Thêm tài khoản thất bại");
                }
            } else {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi thêm tài khoản", "Không tìm thấy nhân viên");
            }

            showData();
            clearAccount(null);

        } catch (Exception e) {
            e.printStackTrace();
            AlertExample alert = new AlertExample();
            alert.showAlert("Lỗi thêm tài khoản", "An unexpected error occurred: " + e.getMessage());
        }
    }

    public void delAccount(ActionEvent event) {
        try {
            AccountData accountData = account_table.getSelectionModel().getSelectedItem();
            if (accountData == null) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi xóa tài khoản", "Vui lòng chọn 1 tài khoản để xóa");
                return;
            }
            int accountID = accountData.getId();
            DBConnection dbcn = DBConnection.getInstance();
            String queryDel = "DELETE FROM accounts WHERE id = ?";
            PreparedStatement pst = dbcn.cn.prepareStatement(queryDel);
            pst.setInt(1, accountID);
            int result = pst.executeUpdate();
            if (result > 0) {
                AlertExample alert = new AlertExample();
                alert.successAlert("Delete data success!");
                String queryUpdID = "Alter table accounts AUTO_INCREMENT = ?";
                PreparedStatement pstUpdID = dbcn.cn.prepareStatement(queryUpdID);
                pstUpdID.setInt(1, accountID);
                pstUpdID.executeUpdate();
            } else {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi xóa tài khoản", "Xóa tài khoản thất bại");
            }
            showData();
            clearAccount(null);
        } catch (Exception e) {
            e.printStackTrace();
            AlertExample alert = new AlertExample();
            alert.showAlert("Lỗi xóa tài khoản", "An unexpected error occurred: " + e.getMessage());
        }
    }

    public void updateAccount(ActionEvent event) {
        int staffId = 0;
        try {
            AccountData accountData = account_table.getSelectionModel().getSelectedItem();
            int accountID = accountData.getId();
            String usernameText = account_username.getText().trim();
            String passwordText = account_password.getText().trim();

            if (accountData == null) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi chỉnh sửa tài khoản", "Vui lòng chọn 1 tài khoản để chỉnh sửa");
                return;
            }

            if (usernameText.isEmpty() || passwordText.isEmpty()) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi chỉnh sửa tài khoản", "Vui lòng điền đầy đủ các thông tin");
                return;
            }

            StaffData selectedStaff = account_staffname.getSelectionModel().getSelectedItem();
            if (selectedStaff != null) {
                staffId = selectedStaff.getId();
            }

            String hashedPassword = BCrypt.hashpw(passwordText, BCrypt.gensalt());

            DBConnection dbcn = DBConnection.getInstance();
            String queryUpd = "UPDATE accounts SET staffid = ?, username = ?, password = ? WHERE id = ?";
            PreparedStatement pst = dbcn.cn.prepareStatement(queryUpd);

            pst.setInt(1, staffId);
            pst.setString(2, usernameText);
            pst.setString(3, hashedPassword);
            pst.setInt(4, accountID);

            int result = pst.executeUpdate();
            if (result > 0) {
                AlertExample alert = new AlertExample();
                alert.successAlert("Chỉnh sửa tài khoản thành công");
            } else {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi chỉnh sửa tài khoản", "Chỉnh sửa tài khoản thất bại");
            }
            showData();
            clearAccount(null);

        } catch (Exception e) {
            e.printStackTrace();
            AlertExample alert = new AlertExample();
            alert.showAlert("Lỗi chỉnh sửa tài khoản", "An unexpected error occurred: " + e.getMessage());
        }
    }

    public void clearAccount(ActionEvent event) {
        account_username.setText("");
        account_password.setText("");
        account_staffname.getSelectionModel().clearSelection();
    }

    private void filterAccountList(String query) {
        if (query == null || query.isEmpty()) {
            account_table.setItems(accountList);
            return;
        }

        ObservableList<AccountData> filteredList = FXCollections.observableArrayList();
        for (AccountData data : accountList) {
            if (String.valueOf(data.getStaffid()).contains(query) || data.getFullname().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(data);
            }
        }
        account_table.setItems(filteredList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showData();
        loadStaffToComboBox();


        account_table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                AccountData selectedAccount = account_table.getSelectionModel().getSelectedItem();

                account_username.setText(selectedAccount.getUsername());
                account_password.setText(selectedAccount.getPassword());
                StaffData selectedStaff = staffList.stream()
                        .filter(staff -> staff.getId() == selectedAccount.getStaffid())
                        .findFirst()
                        .orElse(null);
                account_staffname.getSelectionModel().select(selectedStaff);
            }
        });
    }
}
