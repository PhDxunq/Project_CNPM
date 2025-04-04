package com.example.phongvanrestaurant.components.Admin.Timesheet;

import com.example.phongvanrestaurant.models.DBConnection;
import com.example.phongvanrestaurant.models.TimesheetData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class AdminTimesheetController implements Initializable {

    @FXML
    private TableView<TimesheetData> timesheet_table;

    @FXML
    private TableColumn<TimesheetData, Integer> timesheet_col_id;

    @FXML
    private TableColumn<TimesheetData, Integer> timesheet_col_staffid;

    @FXML
    private TableColumn<TimesheetData, String> timesheet_col_fullname;

    @FXML
    private TableColumn<TimesheetData, String> timesheet_col_timecheckin;

    @FXML
    private TableColumn<TimesheetData, String> timesheet_col_timecheckout;

    @FXML
    private TextField searchField;

    public ObservableList<TimesheetData> timesheetList;

    public ObservableList<TimesheetData> loadTimesheetData() {
        timesheetList = FXCollections.observableArrayList();
        try {
            DBConnection dbcn = DBConnection.getInstance();
            String query = "SELECT t.ID, t.StaffID, s.FullName, t.TimeCheckin, t.TimeCheckout " +
                    "FROM timesheet t JOIN staff s ON t.StaffID = s.ID";
            PreparedStatement pst = dbcn.cn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                TimesheetData timesheetData = new TimesheetData(
                        rs.getInt("ID"),
                        rs.getInt("StaffID"),
                        rs.getString("FullName"),
                        rs.getString("TimeCheckin"),
                        rs.getString("TimeCheckout")
                );

                timesheetList.add(timesheetData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timesheetList;
    }

    public void showTimesheetData() {
        timesheet_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        timesheet_col_staffid.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        timesheet_col_fullname.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        timesheet_col_timecheckin.setCellValueFactory(new PropertyValueFactory<>("timeCheckin"));
        timesheet_col_timecheckout.setCellValueFactory(new PropertyValueFactory<>("timeCheckout"));

        timesheet_table.setItems(loadTimesheetData());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTimesheetList(newValue));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showTimesheetData();
    }

    private void filterTimesheetList(String query) {
        if (query == null || query.isEmpty()) {
            timesheet_table.setItems(timesheetList);
            return;
        }

        ObservableList<TimesheetData> filteredList = FXCollections.observableArrayList();
        for (TimesheetData data : timesheetList) {
            if (String.valueOf(data.getStaffId()).contains(query) || data.getFullName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(data);
            }
        }
        timesheet_table.setItems(filteredList);
    }
}