package com.example.phongvanrestaurant.components.Admin.Table;

import com.example.phongvanrestaurant.components.Admin.AdminHome;
import com.example.phongvanrestaurant.components.AlertExample;
import com.example.phongvanrestaurant.models.Cart;
import com.example.phongvanrestaurant.models.CartItem;
import com.example.phongvanrestaurant.models.DBConnection;
import com.example.phongvanrestaurant.models.Product;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AdminMenu {
    @FXML
    private Label tableIdLabel;

    private int tableId;

    public void setTableId(int tableId) {
        this.tableId = tableId;
        if (tableIdLabel != null) {
            tableIdLabel.setText("Bàn " + tableId);
        }
    }

    @FXML
    private VBox categoryContainer;

    @FXML
    private GridPane productGrid;
    @FXML
    private AnchorPane mainContainer;

    private Cart cart;

    private int category = 0;

    @FXML
    private StackPane overlay;
    @FXML
    private VBox cartPane;
    @FXML
    private Button closeCartButton;
    @FXML
    private Button confirmApplicationButton;

    @FXML
    private GridPane cartGrid;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        cart = new Cart();

        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-pref-width: 100%; -fx-pref-height: 100%;");
        overlay.setVisible(false);

        cartPane.setStyle("-fx-background-color: white; -fx-pref-width: 300px; -fx-pref-height: 100%;");
        cartPane.setVisible(false);

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

        try {
            loadCategories();
            loadProductsForCategory(category);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        overlay.setOnMouseClicked(event -> {
            overlay.setVisible(false);
            cartPane.setVisible(false);
        });

        closeCartButton.setOnAction(event -> {
            overlay.setVisible(false);
            cartPane.setVisible(false);
            loadProductsForCategory(category);
        });

        confirmApplicationButton.setOnAction(event -> {
            showConfirmOrderAlert(event);
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadProductsForCategory(0);
        });
    }

    private String getDisplayCategoryName(String categoryType) {
        switch (categoryType) {
            case "DoNhungLau":
                return "Đồ Nhúng Lẩu";
            case "DoUong":
                return "Đồ Uống";
            case "MonCaMucTom":
                return "Món Cá Mực Tôm";
            case "BanhTrang":
                return "Bánh Tráng";
            case "ComXoiChao":
                return "Cơm Xôi Cháo";
            case "MonSalad":
                return "Món Salad";
            case "MonAnChoi":
                return "Món Ăn Chơi";
            case "MonAnChinh":
                return "Món Ăn Chính";
            default:
                return categoryType;
        }
    }

    private void loadCategories() throws SQLException, ClassNotFoundException {
        Button defaultCategoryButton = null;
        DBConnection dbcn = DBConnection.getInstance();
        String categoryQuery = "SELECT id, categorytype FROM categories";
        PreparedStatement pst = dbcn.cn.prepareStatement(categoryQuery);
        ResultSet rs = pst.executeQuery();

        try {
            while (rs.next()) {
                int categoryId = rs.getInt("id");
                String categoryType = rs.getString("categorytype");
                String displayCategoryName = getDisplayCategoryName(categoryType);
                Button categoryButton = new Button(displayCategoryName);
                categoryButton.setStyle(
                        "-fx-font-size: 16;" +
                                "-fx-padding: 5;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: #ffffff;" +
                                "-fx-text-fill: #ffffff;" +
                                "-fx-background-color: #CC0033;" +
                                " -fx-pref-width: 190.0;" +
                                " -fx-pref-height: 50.0;"
                );
                if (categoryId == 0) {
                    defaultCategoryButton = categoryButton;
                }
                categoryButton.setOnMouseEntered(event -> {
                    categoryButton.setStyle(
                            "-fx-font-size: 16;" +
                                    "-fx-padding: 5;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-border-color: #ffffff;" +
                                    "-fx-text-fill: #ffffff;" +
                                    "-fx-background-color: #CC0033;" + "-fx-pref-width: 200;" +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 10, 0, 0, 0);" +
                                    "-fx-background-color: #FF0066;" +
                                    " -fx-pref-width: 190.0;" +
                                    " -fx-pref-height: 50.0;"
                    );
                });
                categoryButton.setOnMouseExited(event -> {
                    categoryButton.setStyle(
                            "-fx-font-size: 16;" +
                                    "-fx-padding: 5;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-border-color: #ffffff;" +
                                    "-fx-text-fill: #ffffff;" +
                                    "-fx-background-color: #CC0033;" +
                                    "-fx-pref-width: 200;" +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 10, 0, 0, 0);"+
                                    " -fx-pref-width: 190.0;" +
                                    " -fx-pref-height: 50.0;"
                    );
                });

                categoryContainer.setAlignment(Pos.CENTER);
                categoryContainer.getChildren().add(categoryButton);
                categoryButton.setOnAction(event -> {
                    productGrid.getChildren().clear();
                    loadProductsForCategory(categoryId);
                });
                if (defaultCategoryButton != null) {
                    defaultCategoryButton.fire();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadProductsForCategory(int categoryId) {
        category = categoryId;
        String searchText = searchField.getText().trim();

        try {
            PreparedStatement pst = null;
            if (!searchText.isEmpty() || categoryId == 0) {
                DBConnection dbcn = DBConnection.getInstance();
                String productQuery = """
                            SELECT items.id, items.itemname, items.quantity, items.price, items.discount, items.image, categories.categorytype
                            FROM items
                            JOIN categories ON items.categoryid = categories.id
                        """;
                pst = dbcn.cn.prepareStatement(productQuery);
            } else {
                DBConnection dbcn = DBConnection.getInstance();
                String productQuery = """
                            SELECT items.id, items.itemname, items.quantity, items.price, items.discount, items.image, categories.categorytype
                            FROM items
                            JOIN categories ON items.categoryid = categories.id
                            WHERE categoryid = ?
                        """;
                pst = dbcn.cn.prepareStatement(productQuery);
                pst.setInt(1, categoryId);
            }

            try (ResultSet rs = pst.executeQuery()) {
                int column = 0;
                int row = 0;
                productGrid.getChildren().clear();

                while (rs.next()) {
                    String itemName = rs.getString("itemname");
                    float price = rs.getFloat("price");
                    float discount = rs.getFloat("discount");
                    String displayPrice = "Price: " + price + " VND";
                    String discountText = discount > 0 ? "Discount: " + discount + "%" : "";
                    String imageUrl = rs.getString("image");
                    String categoryType = rs.getString("categorytype");

                    Product product = new Product(rs.getInt("id"), rs.getString("itemname"), rs.getInt("quantity"), rs.getDouble("price"), rs.getDouble("discount"), rs.getString("categorytype"),rs.getString("image"));
                    int cartQuantity = getProductQuantityFromCart(product.getID());

                    VBox productCard = new VBox();
                    productCard.setSpacing(10);
                    productCard.setStyle("""
                                -fx-padding: 10; 
                                -fx-background-color: #ffffff; 
                                -fx-border-radius: 10; 
                                -fx-background-radius: 10;
                                -fx-border-color: #dcdcdc; 
                                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 3);
                            """);

                    productCard.setAlignment(Pos.CENTER);
                    ImageView productImage = new ImageView();
                    productImage.setFitHeight(200);
                    productImage.setPreserveRatio(true);

                    try {
                        if (imageUrl != null && !imageUrl.isEmpty()) {

                            String resourcePath = "/com/example/phongvanrestaurant/Image/Menu/" + categoryType + "/" + imageUrl;
                            URL imageUrlResource = getClass().getResource(resourcePath);

                            if (imageUrlResource != null) {
                                Image image = new Image(imageUrlResource.toExternalForm());
                                productImage.setImage(image);
                            } else {
                                System.err.println("Không thể tìm thấy tài nguyên: " + resourcePath);
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Lỗi khi tải ảnh: " + e.getMessage());
                    }

                    Label nameLabel = new Label(itemName);
                    nameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #333333;");

                    Label priceLabel = new Label(displayPrice);
                    priceLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #555555;");

                    Label discountLabel = new Label(discountText);
                    discountLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #ff0000;");

                    Label quantityLabel = new Label("Số lượng: " + cartQuantity);
                    Button increaseButton = new Button("+");
                    Button decreaseButton = new Button("-");
                    increaseButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    decreaseButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                    int[] quantity = {cartQuantity};

                    increaseButton.setOnAction(event -> {
                        quantity[0]++;
                        quantityLabel.setText("Số lượng: " + quantity[0]);
                        addToCart(product, quantity[0]);
                    });

                    decreaseButton.setOnAction(event -> {
                        if (quantity[0] > 0) {
                            quantity[0]--;
                            addToCart(product, quantity[0]);
                        }
                        quantityLabel.setText("Số lượng: " + quantity[0]);
                    });

                    HBox quantityBox = new HBox(10);
                    quantityBox.setStyle("-fx-spacing: 10;");
                    quantityBox.setAlignment(Pos.CENTER_RIGHT);
                    quantityBox.getChildren().addAll(decreaseButton, quantityLabel, increaseButton);

                    productCard.getChildren().addAll(productImage, nameLabel, priceLabel);
                    if (!discountText.isEmpty()) {
                        productCard.getChildren().add(discountLabel);
                    }
                    productCard.getChildren().add(quantityBox);

                    if (!searchText.isEmpty()) {
                        if (!itemName.toLowerCase().contains(searchText.toLowerCase())) {
                            continue;
                        }
                    }

                    productGrid.add(productCard, column, row);
                    column++;

                    if (column == 4) {
                        column = 0;
                        row++;
                    }
                }

                ScrollPane scrollPane = new ScrollPane(productGrid);
                scrollPane.setFitToWidth(true);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

                StackPane centeredPane = new StackPane(scrollPane);
                centeredPane.setStyle("-fx-padding: 20;");
                StackPane.setAlignment(scrollPane, Pos.CENTER);

                AnchorPane centerWrapper = new AnchorPane();
                centerWrapper.getChildren().add(centeredPane);
                AnchorPane.setTopAnchor(centeredPane, 0.0);
                AnchorPane.setBottomAnchor(centeredPane, 0.0);
                AnchorPane.setLeftAnchor(centeredPane, 0.0);
                AnchorPane.setRightAnchor(centeredPane, 0.0);

                mainContainer.getChildren().clear();
                mainContainer.getChildren().add(centeredPane);
                Button cartButton = new Button("Giỏ hàng");
                cartButton.setStyle("-fx-background-color: #CC0033; -fx-text-fill: white; -fx-font-weight: bold;");
                cartButton.setPrefSize(100, 40);
                AnchorPane.setBottomAnchor(cartButton, 20.0);
                AnchorPane.setRightAnchor(cartButton, 20.0);
                mainContainer.getChildren().add(cartButton);

                cartButton.setOnAction(event -> {
                    overlay.setVisible(true);
                    cartPane.setVisible(true);
                });
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addToCart(Product product, int quantity) {
        boolean found = false;

        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct().getID() == product.getID()) {
                if (quantity == 0) {
                    cart.removeItem(product);
                } else {
                    cartItem.setQuantity(quantity);
                }
                found = true;
                updateCartView();
                break;
            }
        }

        if (!found && quantity > 0) {
            cart.addItem(product, quantity);
        }
        updateCartView();
    }


    private int getProductQuantityFromCart(int productId) {
        int quantity = 0;
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct().getID() == productId) {
                quantity = cartItem.getQuantity();
                break;
            }
        }
        return quantity;
    }

    private void updateCartView() {
        Platform.runLater(() -> {
            cartGrid.getChildren().clear();
            AtomicInteger row = new AtomicInteger();
            AtomicInteger col = new AtomicInteger();
            AtomicReference<Double> totalAmount = new AtomicReference<>(0.0);
            cart.getItems().forEach(item -> {
                Product product = item.getProduct();
                String itemName = product.getItemName();
                int quantity = item.getQuantity();
                double discount = product.getDiscount();
                String displayPrice = "Price: " + product.getPrice() + " VND";
                String imageUrl = product.getImage();
                String categoryType = product.getCategoryType();

                double itemTotal = (product.getPrice() * quantity) - (product.getPrice() * quantity * discount / 100);
                totalAmount.set(totalAmount.get() + itemTotal);

                VBox productCard = new VBox();
                productCard.setSpacing(10);
                productCard.setStyle("""
                            -fx-padding: 10; 
                            -fx-background-color: #ffffff; 
                            -fx-border-radius: 10; 
                            -fx-background-radius: 10;
                            -fx-border-color: #dcdcdc; 
                            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 3);
                        """);

                productCard.setAlignment(Pos.CENTER);
                ImageView productImage = new ImageView();
                productImage.setFitHeight(150);
                productImage.setPreserveRatio(true);
                productImage.setSmooth(true);

                try {
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        String resourcePath = "/com/example/phongvanrestaurant/Image/Menu/" + categoryType + "/" + imageUrl;
                        URL imageUrlResource = getClass().getResource(resourcePath);

                        if (imageUrlResource != null) {
                            Image image = new Image(imageUrlResource.toExternalForm());
                            productImage.setImage(image);
                        } else {
                            System.err.println("Không thể tìm thấy tài nguyên: " + resourcePath);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Lỗi khi tải ảnh: " + e.getMessage());
                }

                Label nameLabel = new Label(itemName);
                nameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #333333;");
                Label priceLabel = new Label(displayPrice);
                priceLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #555555;");

                Label quantityLabel = new Label("Số lượng: " + quantity);
                Button increaseButton = new Button("+");
                Button decreaseButton = new Button("-");
                Button removeButton = new Button("Xóa");

                increaseButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                decreaseButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                removeButton.setStyle("-fx-background-color: #CC0033; -fx-text-fill: white;");

                increaseButton.setOnAction(event -> {
                    item.setQuantity(item.getQuantity() + 1);
                    quantityLabel.setText("Số lượng: " + item.getQuantity());
                    addToCart(product, item.getQuantity());
                });

                decreaseButton.setOnAction(event -> {
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                        quantityLabel.setText("Số lượng: " + item.getQuantity());
                        addToCart(product, item.getQuantity());
                    }
                });

                removeButton.setOnAction(event -> {
                    cart.removeItem(product);
                    updateCartView();
                    loadProductsForCategory(category);
                });

                Label itemTotalLabel = new Label("Tổng tiền: " + itemTotal + " VND");
                itemTotalLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

                HBox quantityBox = new HBox(10);
                quantityBox.setAlignment(Pos.CENTER_RIGHT);
                quantityBox.getChildren().addAll(decreaseButton, quantityLabel, increaseButton);

                HBox removeBox = new HBox(10);
                removeBox.setAlignment(Pos.CENTER_RIGHT);
                removeBox.getChildren().add(removeButton);

                productCard.getChildren().addAll(productImage, nameLabel, priceLabel, quantityBox, removeBox, itemTotalLabel);

                cartGrid.add(productCard,  col.get(), row.get());
                col.getAndIncrement();
                if (col.get() > 3) {
                    col.set(0);
                    row.getAndIncrement();
                }
            });
            Label totalAmountLabel = new Label("Tổng tiền: " + totalAmount.get() + " VND");
            totalAmountLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #333333;");
            cartGrid.add(totalAmountLabel, 0, row.get() + 1, 3, 1);
        });
    }

    private void showConfirmOrderAlert(ActionEvent event) {
        if (cart.getItems().isEmpty()) {
            AlertExample errorAlert = new AlertExample();
            errorAlert.showAlert("Chưa có sản phẩm nào trong giỏ hàng", "Vui lòng thêm sản phẩm vào giỏ để tiếp tục");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đơn hàng");
        alert.setHeaderText("Bạn có muốn xác nhận đơn hàng không?");
        alert.setContentText("Nhấn 'Xác nhận' để tiếp tục.");
        alert.initModality(Modality.NONE);

        ButtonType confirmButton = new ButtonType("Xác nhận");
        ButtonType cancelButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == confirmButton) {
            try {
                DBConnection dbcn = DBConnection.getInstance();
                dbcn.cn.setAutoCommit(false);

                String checkStatusQuery = "SELECT Status FROM `table` WHERE id = ?";
                PreparedStatement pstCheckStatus = dbcn.cn.prepareStatement(checkStatusQuery);
                pstCheckStatus.setInt(1, tableId);
                ResultSet rs = pstCheckStatus.executeQuery();

                if (rs.next()) {
                    String currentStatus = rs.getString("Status");

                    if ("active".equalsIgnoreCase(currentStatus)) {
                        String updateStatusQuery = "UPDATE `table` SET Status = 'inactive' WHERE id = ?";
                        PreparedStatement pstUpdateStatus = dbcn.cn.prepareStatement(updateStatusQuery);
                        pstUpdateStatus.setInt(1, tableId);
                        pstUpdateStatus.executeUpdate();
                    }

                    String insertOrderQuery = "INSERT INTO Orders (TableId, Status, Note, DateCreate) VALUES (?, ?, ?, NOW())";
                    PreparedStatement pstOrder = dbcn.cn.prepareStatement(insertOrderQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                    pstOrder.setInt(1, tableId);
                    pstOrder.setString(2, "Wait For Confirmation");
                    pstOrder.setString(3, null);
                    int rowsAffected = pstOrder.executeUpdate();

                    if (rowsAffected > 0) {
                        ResultSet generatedKeys = pstOrder.getGeneratedKeys();
                        int orderId = -1;
                        if (generatedKeys.next()) {
                            orderId = generatedKeys.getInt(1);
                        }

                        boolean hasInsufficientStock = false;
                        for (CartItem cartItem : cart.getItems()) {
                            int itemId = cartItem.getProduct().getID();
                            int quantity = cartItem.getQuantity();

                            String checkStockQuery = "SELECT Quantity FROM items WHERE id = ?";
                            PreparedStatement pstCheckStock = dbcn.cn.prepareStatement(checkStockQuery);
                            pstCheckStock.setInt(1, itemId);
                            ResultSet rsStock = pstCheckStock.executeQuery();

                            if (rsStock.next()) {
                                int stockQuantity = rsStock.getInt("Quantity");

                                if (stockQuantity < quantity) {
                                    hasInsufficientStock = true;
                                    AlertExample newAlert = new AlertExample();
                                    newAlert.showAlert("Lỗi tồn kho","Sản phẩm " + cartItem.getProduct().getItemName() + "chỉ còn" + stockQuantity + " trong kho, không đủ để đặt hàng!");
                                    break;
                                }
                            } else {
                                hasInsufficientStock = true;
                                AlertExample newAlert = new AlertExample();
                                newAlert.showAlert( "Lỗi sản phẩm","Không tìm thấy thông tin sản phẩm trong cơ sở dữ liệu!");
                                break;
                            }
                        }

                        if (!hasInsufficientStock) {
                            for (CartItem cartItem : cart.getItems()) {
                                int itemId = cartItem.getProduct().getID();
                                int quantity = cartItem.getQuantity();
                                double totalAmount = cartItem.getTotalPrice();

                                String updateStockQuery = "UPDATE items SET Quantity = Quantity - ? WHERE id = ?";
                                PreparedStatement pstUpdateStock = dbcn.cn.prepareStatement(updateStockQuery);
                                pstUpdateStock.setInt(1, quantity);
                                pstUpdateStock.setInt(2, itemId);
                                pstUpdateStock.executeUpdate();

                                String insertOrderDetailQuery = "INSERT INTO order_details (OrderID, ItemID, Quantity, TotalAmount) VALUES (?, ?, ?, ?)";
                                PreparedStatement pstOrderDetail = dbcn.cn.prepareStatement(insertOrderDetailQuery);
                                pstOrderDetail.setInt(1, orderId);
                                pstOrderDetail.setInt(2, itemId);
                                pstOrderDetail.setInt(3, quantity);
                                pstOrderDetail.setDouble(4, totalAmount);
                                pstOrderDetail.executeUpdate();
                            }

                            dbcn.cn.commit();
                            cart.clearCart();
                            cartGrid.getChildren().clear();
                            AlertExample alert2 = new AlertExample();
                            alert2.successAlert("Thêm đơn hàng thành công");
                        } else {
                            dbcn.cn.rollback();
                        }


                        dbcn.cn.commit();
                        cart.clearCart();
                        cartGrid.getChildren().clear();
                        AlertExample alert2 = new AlertExample();
                        alert2.successAlert("Thêm đơn hàng thành công");
                    } else {
                        dbcn.cn.rollback();
                        System.out.println("Không thể tạo đơn hàng!");
                    }
                } else {
                    System.out.println("Không tìm thấy thông tin trạng thái của bàn!");
                }

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                try {
                    DBConnection.getInstance().cn.rollback();
                } catch (SQLException | ClassNotFoundException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                System.out.println("Có lỗi xảy ra khi thêm đơn hàng vào database.");
            } finally {
                try {
                    DBConnection.getInstance().cn.setAutoCommit(true);
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (result.isPresent() && result.get() == cancelButton) {
            alert.close();
        }
    }


    public void doCancel() {
        Platform.exit();
    }

    public void doMinimize(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    public void doLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        if (alert.showAndWait().get().getText().equals("OK")) {
            System.out.println("User logged out!");

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
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/phongvanrestaurant/Admin/admin-home.fxml"));
            Parent tableView = loader.load();
            Scene tableScene = new Scene(tableView);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(tableScene);
            AdminHome adminHomeController = loader.getController();
            adminHomeController.Table(event);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Admin Home FXML file.");
        }
    }
}
