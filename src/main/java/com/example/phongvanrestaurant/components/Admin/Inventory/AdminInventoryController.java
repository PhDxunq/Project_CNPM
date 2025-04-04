package com.example.phongvanrestaurant.components.Admin.Inventory;

import com.example.phongvanrestaurant.components.Admin.Staff.StaffData;
import com.example.phongvanrestaurant.components.AlertExample;
import com.example.phongvanrestaurant.models.CategoryData;
import com.example.phongvanrestaurant.models.DBConnection;
import com.example.phongvanrestaurant.models.InventoryData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminInventoryController {

    @FXML
    private TableColumn<InventoryData, String> inventory_col_dateupdate;

    @FXML
    private Button inventory_addbtn;

    @FXML
    private ComboBox<CategoryData> inventory_choosecategoryid;

    @FXML
    private Button inventory_clearbtn;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_categoryid;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_datecreate;

    @FXML
    private TableView<InventoryData> inventory_table;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_discount;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_id;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_image;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_itemname;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_price;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_quantity;

    @FXML
    private Button inventory_delbtn;

    @FXML
    private TextField inventory_discount;

    @FXML
    private Button inventory_import;

    @FXML
    private TextField inventory_itemname;

    @FXML
    private TextField inventory_price;

    @FXML
    private TextField inventory_quantity;

    @FXML
    private Button inventory_updbtn;

    @FXML
    private AnchorPane inventory_form;

    @FXML
    private AnchorPane inventory_image;

    @FXML
    private TextField searchField;


    public ObservableList<InventoryData> inventoryList;
    public ObservableList<CategoryData> categoryList;



    public ObservableList<InventoryData> inventoryData() {
        inventoryList = FXCollections.observableArrayList();
        try {
            InventoryData inventoryData;
            DBConnection dbcn = DBConnection.getInstance();
            String query = "SELECT * FROM items";
            PreparedStatement pst = dbcn.cn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                inventoryData = new InventoryData(
                        rs.getInt("id"),
                        rs.getString("itemname"),
                        rs.getInt("quantity"),
                        rs.getInt("categoryid"),
                        rs.getDouble("price"),
                        rs.getDouble("discount"),
                        rs.getString("image"),
                        rs.getDate("datecreate"),
                        rs.getDate("dateupdate"));
                inventoryList.add(inventoryData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inventoryList;
    }

    public void inventoryCategoryID() {
        categoryList = FXCollections.observableArrayList();
        try {
            CategoryData categoryData;
            DBConnection dbcn = DBConnection.getInstance();
            String query = "SELECT * FROM categories";
            PreparedStatement pst = dbcn.cn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                categoryData = new CategoryData(
                        rs.getInt("id"),
                        rs.getString("categorytype"));
                categoryList.add(categoryData);
            }
            inventory_choosecategoryid.setItems(categoryList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inventoryImport() {
        FileChooser openFile = new FileChooser();
        openFile.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = openFile.showOpenDialog(inventory_form.getScene().getWindow());

        if (file != null) {
            try {
                if (inventory_choosecategoryid.getSelectionModel().getSelectedItem() == null) {
                    AlertExample alert = new AlertExample();
                    alert.showAlert("Lỗi thêm vật phẩm", "Vui lòng chọn danh mục.");
                    return;
                }

                String categoryName = inventory_choosecategoryid.getSelectionModel().getSelectedItem().getCategoryname();

                Path resourceDir = Path.of("target/classes/com/example/phongvanrestaurant/Image/Menu/" + categoryName);
                if (!Files.exists(resourceDir)) {
                    Files.createDirectories(resourceDir);
                }

                Path destination = resourceDir.resolve(file.getName());
                Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                inventory_image.setStyle("-fx-background-image: url('" + destination.toUri().toString() + "'); -fx-background-size: cover;");


            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Không thể lưu ảnh.");
            }
        }
    }




    public void addInventory(ActionEvent event) {
        try {
            DBConnection dbcn = DBConnection.getInstance();
            String queryCheck = "SELECT * FROM items WHERE itemname = ?";
            PreparedStatement pstCheck = dbcn.cn.prepareStatement(queryCheck);
            pstCheck.setString(1, inventory_itemname.getText());
            ResultSet rs = pstCheck.executeQuery();
            if (rs.next()) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi thêm vật phẩm", "Vật phẩm đã tồn tại.");
                clearInventory(null);
                return;
            }

            if (inventory_choosecategoryid.getSelectionModel().getSelectedItem() == null) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi thêm vật phẩm", "Vui lòng chọn danh mục.");
                return;
            }

            String imagePath = inventory_image.getStyle();
            if (imagePath == null || !imagePath.contains("url")) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi thêm vật phẩm", "Hình ảnh chưa được chọn.");
                return;
            }

            int quantity;
            double price, discount;
            try {
                quantity = Integer.parseInt(inventory_quantity.getText());
                price = Double.parseDouble(inventory_price.getText());
                discount = Double.parseDouble(inventory_discount.getText());
            } catch (NumberFormatException e) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi thêm vật phẩm", "Vui lòng nhập số hợp lệ cho số lượng, giá và giảm giá.");
                return;
            }

            String[] path = imagePath.split("'");
            String imageName = path[1].substring(path[1].lastIndexOf("/") + 1);

            String query = "INSERT INTO items (itemname, quantity, categoryid, price, discount, image) VALUES (?,?,?,?,?,?)";
            PreparedStatement pst = dbcn.cn.prepareStatement(query);
            pst.setString(1, inventory_itemname.getText());
            pst.setInt(2, quantity);
            pst.setInt(3, inventory_choosecategoryid.getSelectionModel().getSelectedItem().getId());
            pst.setDouble(4, price);
            pst.setDouble(5, discount);
            pst.setString(6, imageName);
            int result = pst.executeUpdate();

            if (result > 0) {
                AlertExample alert = new AlertExample();
                alert.successAlert("Thêm vật phẩm thành công!");
            } else {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi thêm vật phẩm", "Thêm vật phẩm thất bại.");
            }

            showData();
            clearInventory(null);

        } catch (SQLException e) {
            AlertExample alert = new AlertExample();
            alert.showAlert("Lỗi cơ sở dữ liệu", "Không thể thêm vật phẩm. Vui lòng thử lại sau.");
            e.printStackTrace();
        } catch (Exception e) {
            AlertExample alert = new AlertExample();
            alert.showAlert("Lỗi không xác định", "Đã xảy ra lỗi. Vui lòng thử lại.");
            e.printStackTrace();
        }
    }


    public void updInventory(ActionEvent event){
        try {
            InventoryData selectedInventory = inventory_table.getSelectionModel().getSelectedItem();
            if (selectedInventory == null) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi cập nhật vật phẩm", "Vui lòng chọn vật phẩm cần cập nhật.");
                return;
            }
            String imagePath = inventory_image.getStyle();
            String[] path = imagePath.split("'");
            String imageName = path[1].substring(path[1].lastIndexOf("/") + 1);
            DBConnection dbcn = DBConnection.getInstance();
            String query = "UPDATE items SET itemname = ?, quantity = ?, categoryid = ?, price = ?, discount = ?, image = ? WHERE id = ?";
            PreparedStatement pst = dbcn.cn.prepareStatement(query);
            pst.setString(1, inventory_itemname.getText());
            pst.setInt(2, Integer.parseInt(inventory_quantity.getText()));
            pst.setInt(3, inventory_choosecategoryid.getSelectionModel().getSelectedItem().getId());
            pst.setDouble(4, Double.parseDouble(inventory_price.getText()));
            pst.setDouble(5, Double.parseDouble(inventory_discount.getText()));
            pst.setString(6, imageName);
            pst.setInt(7, selectedInventory.getId());
            int result = pst.executeUpdate();
            if (result > 0) {
                AlertExample alert = new AlertExample();
                alert.successAlert("Cập nhật vật phẩm thanh cong!");
            } else {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi cập nhật vật phẩm", "Cập nhật vật phẩm thất bại");
            }
            showData();
            clearInventory(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearInventory(ActionEvent event){
        inventory_itemname.clear();
        inventory_quantity.clear();
        inventory_choosecategoryid.getSelectionModel().clearSelection();
        inventory_price.clear();
        inventory_discount.clear();
        inventory_image.setStyle("");
    }

    public void deleteInventory(ActionEvent event) {
        try {
            InventoryData selectedInventory = inventory_table.getSelectionModel().getSelectedItem();
            if (selectedInventory == null) {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi xóa vật phẩm", "Vui lòng chọn vật phẩm cần xóa.");
                return;
            }
            DBConnection dbcn = DBConnection.getInstance();
            String query = "DELETE FROM items WHERE id = ?";
            PreparedStatement pst = dbcn.cn.prepareStatement(query);
            pst.setInt(1, selectedInventory.getId());
            int result = pst.executeUpdate();
            if (result > 0) {
                AlertExample alert = new AlertExample();
                alert.successAlert("Xóa vật phẩm thành công!");
            } else {
                AlertExample alert = new AlertExample();
                alert.showAlert("Lỗi xóa vật phẩm", "Xóa vật phẩm thất bại");
            }
            showData();
            clearInventory(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showData(){
        inventory_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        inventory_col_itemname.setCellValueFactory(new PropertyValueFactory<>("itemname"));
        inventory_col_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        inventory_col_categoryid.setCellValueFactory(new PropertyValueFactory<>("categoryid"));
        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        inventory_col_discount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        inventory_col_image.setCellValueFactory(new PropertyValueFactory<>("image"));
        inventory_col_datecreate.setCellValueFactory(new PropertyValueFactory<>("datecreate"));
        inventory_col_dateupdate.setCellValueFactory(new PropertyValueFactory<>("dateupdate"));

        inventory_table.setItems(inventoryData());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterInventoryList(newValue));
    }

    private void filterInventoryList(String query) {
        if (query == null || query.isEmpty()) {
            inventory_table.setItems(inventoryList);
            return;
        }

        ObservableList<InventoryData> filteredList = FXCollections.observableArrayList();
        for (InventoryData data : inventoryList) {
            if (data.getItemname().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(data);
            }
        }
        inventory_table.setItems(filteredList);
    }


    @FXML
    private void initialize() {
        try {
            DBConnection dbcn = DBConnection.getInstance();
            if (dbcn.cn != null && !dbcn.cn.isClosed()) {
                System.out.println("Kết nối cơ sở dữ liệu thành công.");
            } else {
                System.out.println("Không thể kết nối cơ sở dữ liệu.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

        showData();
        inventoryCategoryID();

        inventory_table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {
                InventoryData selectedInvetory = inventory_table.getSelectionModel().getSelectedItem();
                inventory_choosecategoryid.getSelectionModel().select(
                        inventory_choosecategoryid.getItems()
                                .stream()
                                .filter(categoryData -> categoryData.getId() == newValue.getCategoryid())
                                .findFirst()
                                .orElse(null)
                );

                String categoryType = inventory_choosecategoryid.getSelectionModel().getSelectedItem().getCategoryname();

                String imageName = selectedInvetory.getImage();
                try {

                    String imagePath = "/com/example/phongvanrestaurant/Image/Menu/" + categoryType + '/' + imageName;
                    URL imageUrl = getClass().getResource(imagePath);

                    if (imageUrl != null) {
                        inventory_image.setStyle("-fx-background-image: url('" + imageUrl.toExternalForm() + "'); -fx-background-size: cover;");
                    } else {
                        System.out.println("Ảnh tại đường dẫn: " + imagePath);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }



                inventory_itemname.setText(selectedInvetory.getItemname());
                inventory_quantity.setText(String.valueOf(selectedInvetory.getQuantity()));
                inventory_price.setText(String.valueOf(selectedInvetory.getPrice()));
                inventory_discount.setText(String.valueOf(selectedInvetory.getDiscount()));
            } else {
                inventory_itemname.clear();
                inventory_quantity.clear();
                inventory_price.clear();
                inventory_discount.clear();
                inventory_image.setStyle("");
            }
        });

        inventory_choosecategoryid.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                inventory_choosecategoryid.getSelectionModel().select(
                        inventory_choosecategoryid.getItems()
                                .stream()
                                .filter(categoryData -> categoryData.getId() == newValue.getId())
                                .findFirst()
                                .orElse(null)
                );

            } else {
                inventory_choosecategoryid.getSelectionModel().clearSelection();
            }
        });
    }

}
