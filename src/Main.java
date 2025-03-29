
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import Check.CheckStock;
import Deletion.*;
import Display.*;
import Insertion.*;
import Order.PlaceOrder;
import Updation.*;

public class Main extends Application {

    // Database connection
    private Connection connection;
    
    // Main components
    private TabPane tabPane;
    private BorderPane mainLayout;
    private Label statusLabel;
    
    // Tabs
    private Tab productsTab;
    private Tab suppliersTab;
    private Tab employeesTab;
    private Tab stockTab;
    private Tab ordersTab;
    private Tab reportsTab;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Set up database connection
            setupDatabaseConnection();
            
            // Create main layout
            mainLayout = new BorderPane();
            
            // Create header
            VBox header = createHeader();
            mainLayout.setTop(header);
            
            // Create tab pane for different sections
            tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            
            // Create individual tabs
            createProductsTab();
            createSuppliersTab();
            createEmployeesTab();
            createStockTab();
            createOrdersTab();
            createReportsTab();
            
            // Add tabs to tab pane
            tabPane.getTabs().addAll(productsTab, suppliersTab, employeesTab, stockTab, ordersTab, reportsTab);
            mainLayout.setCenter(tabPane);
            
            // Create status bar
            HBox statusBar = createStatusBar();
            mainLayout.setBottom(statusBar);
            
            // Set up scene
            Scene scene = new Scene(mainLayout, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            // Configure stage
            primaryStage.setTitle("Inventory Management System");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            updateStatus("System ready");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Application Error", "Failed to start application", e.getMessage());
        }
    }
    
    private void setupDatabaseConnection() throws SQLException {
        // Replace with your actual database connection info
        String url = "jdbc:oracle:thin:@localhost:1521:XE";
        String user = "sys as sysdba";
        String password = "Pr270903";
        
        connection = DriverManager.getConnection(url, user, password);
        System.out.println("Connected to database");
    }
    
    private VBox createHeader() {
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);
        header.setSpacing(10);
        header.setPadding(new Insets(20, 0, 20, 0));
        header.setStyle("-fx-background-color: #2c3e50;");
        
        Label title = new Label("Inventory Management System");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        
        header.getChildren().add(title);
        return header;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #ecf0f1;");
        
        statusLabel = new Label("Ready");
        statusBar.getChildren().add(statusLabel);
        
        return statusBar;
    }
    
    // ===== PRODUCTS TAB =====
    private void createProductsTab() {
        productsTab = new Tab("Products");
        
        // Create split pane for list and form
        SplitPane splitPane = new SplitPane();
        
        // Products table view
        TableView<ProductViewModel> productsTable = new TableView<>();
        setupProductsTableView(productsTable);
        
        // Product form
        VBox productForm = createProductForm(productsTable);
        
        // Add to split pane
        splitPane.getItems().addAll(productsTable, productForm);
        splitPane.setDividerPositions(0.7);
        
        productsTab.setContent(splitPane);
    }
    
    private void setupProductsTableView(TableView<ProductViewModel> table) {
        // Create columns
        TableColumn<ProductViewModel, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().productIdProperty().asObject());
        
        TableColumn<ProductViewModel, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        
        TableColumn<ProductViewModel, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        
        TableColumn<ProductViewModel, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        
        TableColumn<ProductViewModel, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        
        // Add columns to table
        table.getColumns().addAll(idColumn, nameColumn, categoryColumn, quantityColumn, priceColumn);
        
        // Load data
        loadProductsData(table);
    }
    
    private void loadProductsData(TableView<ProductViewModel> table) {
        try {
            // Clear existing items
            table.getItems().clear();
            
            // Create display products instance
            DisplayProducts displayProducts = new DisplayProducts();
            
            // Get products from database
            List<?> products = displayProducts.displayProducts(connection);
            
            // Convert and add to table
            ObservableList<ProductViewModel> data = FXCollections.observableArrayList();
            
            for (Object product : products) {
                // Reflection to access the product properties
                Integer productId = (Integer) product.getClass().getMethod("getProductId").invoke(product);
                String productName = (String) product.getClass().getMethod("getProductName").invoke(product);
                String category = (String) product.getClass().getMethod("getCategory").invoke(product);
                Integer quantity = (Integer) product.getClass().getMethod("getQuantity").invoke(product);
                Double price = (Double) product.getClass().getMethod("getPrice").invoke(product);
                
                data.add(new ProductViewModel(productId, productName, category, quantity, price));
            }
            
            table.setItems(data);
            updateStatus("Loaded " + data.size() + " products");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Loading Error", "Failed to load products", e.getMessage());
        }
    }
    
    private VBox createProductForm(TableView<ProductViewModel> productsTable) {
        VBox form = new VBox();
        form.setSpacing(10);
        form.setPadding(new Insets(20));
        
        // Form title
        Label formTitle = new Label("Product Management");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Form fields
        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");
        
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        
        // Product ID field for updates/deletion
        TextField productIdField = new TextField();
        productIdField.setPromptText("Product ID (for update/delete)");
        
        // Action buttons
        Button addButton = new Button("Add Product");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.getStyleClass().add("action-button");
        
        Button updatePriceButton = new Button("Update Price");
        updatePriceButton.setMaxWidth(Double.MAX_VALUE);
        
        Button updateQuantityButton = new Button("Update Quantity");
        updateQuantityButton.setMaxWidth(Double.MAX_VALUE);
        
        Button deleteButton = new Button("Delete Product");
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.getStyleClass().add("delete-button");
        
        Button refreshButton = new Button("Refresh List");
        refreshButton.setMaxWidth(Double.MAX_VALUE);
        
        // Add handlers
        addButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String category = categoryField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                
                InsertProduct insertProduct = new InsertProduct();
                String result = insertProduct.insert(connection, name, category, quantity, price);
                
                updateStatus(result);
                clearForm(nameField, categoryField, quantityField, priceField);
                loadProductsData(productsTable);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format", 
                          "Please enter valid numbers for quantity and price.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not add product", ex.getMessage());
            }
        });
        
        updatePriceButton.setOnAction(e -> {
            try {
                int productId = Integer.parseInt(productIdField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                
                UpdateProductPrice updatePrice = new UpdateProductPrice();
                String result = updatePrice.update(connection, productId, price);
                
                updateStatus(result);
                loadProductsData(productsTable);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format", 
                          "Please enter valid numbers for product ID and price.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not update price", ex.getMessage());
            }
        });
        
        updateQuantityButton.setOnAction(e -> {
            try {
                int productId = Integer.parseInt(productIdField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());
                
                UpdateProductQuantity updateQuantity = new UpdateProductQuantity();
                String result = updateQuantity.update(connection, productId, quantity);
                
                updateStatus(result);
                loadProductsData(productsTable);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format", 
                          "Please enter valid numbers for product ID and quantity.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not update quantity", ex.getMessage());
            }
        });
        
        deleteButton.setOnAction(e -> {
            try {
                int productId = Integer.parseInt(productIdField.getText().trim());
                
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Deletion");
                confirmAlert.setHeaderText("Delete Product");
                confirmAlert.setContentText("Are you sure you want to delete product with ID " + productId + "?");
                
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        DeleteProduct deleteProduct = new DeleteProduct();
                        String result = deleteProduct.delete(connection, productId);
                        
                        updateStatus(result);
                        loadProductsData(productsTable);
                    }
                });
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid product ID", 
                          "Please enter a valid product ID.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not delete product", ex.getMessage());
            }
        });
        
        refreshButton.setOnAction(e -> loadProductsData(productsTable));
        
        // Table selection handler
        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                productIdField.setText(String.valueOf(newSelection.getProductId()));
                nameField.setText(newSelection.getProductName());
                categoryField.setText(newSelection.getCategory());
                quantityField.setText(String.valueOf(newSelection.getQuantity()));
                priceField.setText(String.valueOf(newSelection.getPrice()));
            }
        });
        
        // Add components to form
        form.getChildren().addAll(
            formTitle,
            new Label("Product Name:"),
            nameField,
            new Label("Category:"),
            categoryField,
            new Label("Quantity:"),
            quantityField,
            new Label("Price:"),
            priceField,
            new Label("Product ID (for update/delete):"),
            productIdField,
            new Separator(),
            addButton,
            updatePriceButton,
            updateQuantityButton,
            deleteButton,
            refreshButton
        );
        
        return form;
    }
    
    // ===== SUPPLIERS TAB =====
    private void createSuppliersTab() {
        suppliersTab = new Tab("Suppliers");
        
        // Create split pane for list and form
        SplitPane splitPane = new SplitPane();
        
        // Suppliers table view
        TableView<SupplierViewModel> suppliersTable = new TableView<>();
        setupSuppliersTableView(suppliersTable);
        
        // Supplier form
        VBox supplierForm = createSupplierForm(suppliersTable);
        
        // Add to split pane
        splitPane.getItems().addAll(suppliersTable, supplierForm);
        splitPane.setDividerPositions(0.7);
        
        suppliersTab.setContent(splitPane);
    }
    
    private void setupSuppliersTableView(TableView<SupplierViewModel> table) {
        // Create columns
        TableColumn<SupplierViewModel, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().supplierIdProperty().asObject());
        
        TableColumn<SupplierViewModel, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().supplierNameProperty());
        
        TableColumn<SupplierViewModel, String> contactColumn = new TableColumn<>("Contact");
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().contactNumberProperty());
        
        TableColumn<SupplierViewModel, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        
        TableColumn<SupplierViewModel, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        
        // Add columns to table
        table.getColumns().addAll(idColumn, nameColumn, contactColumn, emailColumn, addressColumn);
        
        // Load data
        loadSuppliersData(table);
    }
    
    private void loadSuppliersData(TableView<SupplierViewModel> table) {
        try {
            // Clear existing items
            table.getItems().clear();
            
            // Create display suppliers instance
            DisplaySuppliers displaySuppliers = new DisplaySuppliers();
            
            // Get suppliers from database
            List<?> suppliers = displaySuppliers.displaySuppliers(connection);
            
            // Convert and add to table
            ObservableList<SupplierViewModel> data = FXCollections.observableArrayList();
            
            for (Object supplier : suppliers) {
                // Reflection to access the supplier properties
                Integer supplierId = (Integer) supplier.getClass().getMethod("getSupplierId").invoke(supplier);
                String supplierName = (String) supplier.getClass().getMethod("getSupplierName").invoke(supplier);
                String contactNumber = (String) supplier.getClass().getMethod("getContactNumber").invoke(supplier);
                String email = (String) supplier.getClass().getMethod("getEmail").invoke(supplier);
                String address = (String) supplier.getClass().getMethod("getAddress").invoke(supplier);
                
                data.add(new SupplierViewModel(supplierId, supplierName, contactNumber, email, address));
            }
            
            table.setItems(data);
            updateStatus("Loaded " + data.size() + " suppliers");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Loading Error", "Failed to load suppliers", e.getMessage());
        }
    }
    
    private VBox createSupplierForm(TableView<SupplierViewModel> suppliersTable) {
        VBox form = new VBox();
        form.setSpacing(10);
        form.setPadding(new Insets(20));
        
        // Form title
        Label formTitle = new Label("Supplier Management");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Form fields
        TextField nameField = new TextField();
        nameField.setPromptText("Supplier Name");
        
        TextField contactField = new TextField();
        contactField.setPromptText("Contact Number");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        
        // Supplier ID field for updates/deletion
        TextField supplierIdField = new TextField();
        supplierIdField.setPromptText("Supplier ID (for update/delete)");
        
        // Action buttons
        Button addButton = new Button("Add Supplier");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.getStyleClass().add("action-button");
        
        Button updateContactButton = new Button("Update Contact");
        updateContactButton.setMaxWidth(Double.MAX_VALUE);
        
        Button updateEmailButton = new Button("Update Email");
        updateEmailButton.setMaxWidth(Double.MAX_VALUE);
        
        Button updateAddressButton = new Button("Update Address");
        updateAddressButton.setMaxWidth(Double.MAX_VALUE);
        
        Button deleteButton = new Button("Delete Supplier");
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.getStyleClass().add("delete-button");
        
        Button refreshButton = new Button("Refresh List");
        refreshButton.setMaxWidth(Double.MAX_VALUE);
        
        // Add handlers
        addButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String contact = contactField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressField.getText().trim();
                
                InsertSupplier insertSupplier = new InsertSupplier();
                String result = insertSupplier.insert(connection, name, contact, email, address);
                
                updateStatus(result);
                clearForm(nameField, contactField, emailField, addressField);
                loadSuppliersData(suppliersTable);
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not add supplier", ex.getMessage());
            }
        });
        
        updateContactButton.setOnAction(e -> {
            try {
                int supplierId = Integer.parseInt(supplierIdField.getText().trim());
                String contact = contactField.getText().trim();
                
                UpdateSupplierContactNumber updateContact = new UpdateSupplierContactNumber();
                String result = updateContact.update(connection, supplierId, contact);
                
                updateStatus(result);
                loadSuppliersData(suppliersTable);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid supplier ID", 
                          "Please enter a valid supplier ID.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not update contact", ex.getMessage());
            }
        });
        
        updateEmailButton.setOnAction(e -> {
            try {
                int supplierId = Integer.parseInt(supplierIdField.getText().trim());
                String email = emailField.getText().trim();
                
                UpdateSupplierEmail updateEmail = new UpdateSupplierEmail();
                String result = updateEmail.update(connection, supplierId, email);
                
                updateStatus(result);
                loadSuppliersData(suppliersTable);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid supplier ID", 
                          "Please enter a valid supplier ID.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not update email", ex.getMessage());
            }
        });
        
        updateAddressButton.setOnAction(e -> {
            try {
                int supplierId = Integer.parseInt(supplierIdField.getText().trim());
                String address = addressField.getText().trim();
                
                UpdateSupplierAddress updateAddress = new UpdateSupplierAddress();
                String result = updateAddress.update(connection, supplierId, address);
                
                updateStatus(result);
                loadSuppliersData(suppliersTable);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid supplier ID", 
                          "Please enter a valid supplier ID.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not update address", ex.getMessage());
            }
        });
        
        deleteButton.setOnAction(e -> {
            try {
                int supplierId = Integer.parseInt(supplierIdField.getText().trim());
                
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Deletion");
                confirmAlert.setHeaderText("Delete Supplier");
                confirmAlert.setContentText("Are you sure you want to delete supplier with ID " + supplierId + "?");
                
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        DeleteSupplier deleteSupplier = new DeleteSupplier();
                        String result = deleteSupplier.delete(connection, supplierId);
                        
                        updateStatus(result);
                        loadSuppliersData(suppliersTable);
                    }
                });
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid supplier ID", 
                          "Please enter a valid supplier ID.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not delete supplier", ex.getMessage());
            }
        });
        
        refreshButton.setOnAction(e -> loadSuppliersData(suppliersTable));
        
        // Table selection handler
        suppliersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                supplierIdField.setText(String.valueOf(newSelection.getSupplierId()));
                nameField.setText(newSelection.getSupplierName());
                contactField.setText(newSelection.getContactNumber());
                emailField.setText(newSelection.getEmail());
                addressField.setText(newSelection.getAddress());
            }
        });
        
        // Add components to form
        form.getChildren().addAll(
            formTitle,
            new Label("Supplier Name:"),
            nameField,
            new Label("Contact Number:"),
            contactField,
            new Label("Email:"),
            emailField,
            new Label("Address:"),
            addressField,
            new Label("Supplier ID (for update/delete):"),
            supplierIdField,
            new Separator(),
            addButton,
            updateContactButton,
            updateEmailButton,
            updateAddressButton,
            deleteButton,
            refreshButton
        );
        
        return form;
    }
    
    // ===== EMPLOYEES TAB =====
    private void createEmployeesTab() {
        employeesTab = new Tab("Employees");
        
        // Create split pane for list and form
        SplitPane splitPane = new SplitPane();
        
        // Employees table view
        TableView<EmployeeViewModel> employeesTable = new TableView<>();
        setupEmployeesTableView(employeesTable);
        
        // Employee form
        VBox employeeForm = createEmployeeForm(employeesTable);
        
        // Add to split pane
        splitPane.getItems().addAll(employeesTable, employeeForm);
        splitPane.setDividerPositions(0.7);
        
        employeesTab.setContent(splitPane);
    }
    
    private void setupEmployeesTableView(TableView<EmployeeViewModel> table) {
        // Create columns
        TableColumn<EmployeeViewModel, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().employeeIdProperty().asObject());
        
        TableColumn<EmployeeViewModel, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());
        
        TableColumn<EmployeeViewModel, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        
        TableColumn<EmployeeViewModel, String> contactColumn = new TableColumn<>("Contact");
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        
        // Add columns to table
        table.getColumns().addAll(idColumn, nameColumn, roleColumn, contactColumn);
        
        // Load data
        loadEmployeesData(table);
    }
    
    private void loadEmployeesData(TableView<EmployeeViewModel> table) {
        try {
            // Clear existing items
            table.getItems().clear();
            
            // Create display employees instance
            DisplayEmployees displayEmployees = new DisplayEmployees();
            
            // Get employees from database
            List<?> employees = displayEmployees.displayEmployees(connection);
            
            // Convert and add to table
            ObservableList<EmployeeViewModel> data = FXCollections.observableArrayList();
            
            for (Object employee : employees) {
                // Reflection to access the employee properties
                Integer employeeId = (Integer) employee.getClass().getMethod("getEmployeeId").invoke(employee);
                String employeeName = (String) employee.getClass().getMethod("getEmployeeName").invoke(employee);
                String role = (String) employee.getClass().getMethod("getRole").invoke(employee);
                String contact = (String) employee.getClass().getMethod("getContact").invoke(employee);
                
                data.add(new EmployeeViewModel(employeeId, employeeName, role, contact));
            }
            
            table.setItems(data);
            updateStatus("Loaded " + data.size() + " employees");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Data Loading Error", "Failed to load employees", e.getMessage());
        }
    }
    
    private VBox createEmployeeForm(TableView<EmployeeViewModel> employeesTable) {
        VBox form = new VBox();
        form.setSpacing(10);
        form.setPadding(new Insets(20));
        
        // Form title
        Label formTitle = new Label("Employee Management");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Form fields
        TextField nameField = new TextField();
        nameField.setPromptText("Employee Name");
        
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Manager", "Stock Handler", "Sales");
        roleComboBox.setPromptText("Select Role");
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        
        TextField contactField = new TextField();
        contactField.setPromptText("Contact Number");
        
        // Employee ID field for updates/deletion
        TextField employeeIdField = new TextField();
        employeeIdField.setPromptText("Employee ID (for update/delete)");
        
        // Action buttons
        Button addButton = new Button("Add Employee");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.getStyleClass().add("action-button");
        
        Button updateRoleButton = new Button("Update Role");
        updateRoleButton.setMaxWidth(Double.MAX_VALUE);
        
        Button updateContactButton = new Button("Update Contact");
        updateContactButton.setMaxWidth(Double.MAX_VALUE);
        
        Button deleteButton = new Button("Delete Employee");
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.getStyleClass().add("delete-button");
        
        Button refreshButton = new Button("Refresh List");
        refreshButton.setMaxWidth(Double.MAX_VALUE);
        
        // Add handlers
        addButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String role = roleComboBox.getValue();
                String contact = contactField.getText().trim();
                
                if (name.isEmpty() || role == null || contact.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Missing Information", "All fields are required", 
                              "Please fill in all fields.");
                    return;
                }
                
                InsertEmployee insertEmployee = new InsertEmployee();
                String result = insertEmployee.insert(connection, name, role, contact);
                
                updateStatus(result);
                clearForm(nameField, contactField);
                roleComboBox.setValue(null);
                loadEmployeesData(employeesTable);
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not add employee", ex.getMessage());
            }
        });
        
        // Continuing from where the code was cut off in the update role button handler

updateRoleButton.setOnAction(e -> {
    try {
        int employeeId = Integer.parseInt(employeeIdField.getText().trim());
        String role = roleComboBox.getValue();
        
        if (role == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Role is required", 
                      "Please select a role.");
            return;
        }
        
        UpdateEmployeeRole updateRole = new UpdateEmployeeRole();
        String result = updateRole.update(connection, employeeId, role);
        
        updateStatus(result);
        loadEmployeesData(employeesTable);
    } catch (NumberFormatException ex) {
        showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid employee ID", 
                  "Please enter a valid employee ID.");
    } catch (Exception ex) {
        showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not update role", ex.getMessage());
    }
});

updateContactButton.setOnAction(e -> {
    try {
        int employeeId = Integer.parseInt(employeeIdField.getText().trim());
        String contact = contactField.getText().trim();
        
        if (contact.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Contact is required", 
                      "Please enter a contact number.");
            return;
        }
        
        UpdateEmployeeContact updateContact = new UpdateEmployeeContact();
        String result = updateContact.update(connection, employeeId, contact);
        
        updateStatus(result);
        loadEmployeesData(employeesTable);
    } catch (NumberFormatException ex) {
        showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid employee ID", 
                  "Please enter a valid employee ID.");
    } catch (Exception ex) {
        showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not update contact", ex.getMessage());
    }
});

deleteButton.setOnAction(e -> {
    try {
        int employeeId = Integer.parseInt(employeeIdField.getText().trim());
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Employee");
        confirmAlert.setContentText("Are you sure you want to delete employee with ID " + employeeId + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DeleteEmployee deleteEmployee = new DeleteEmployee();
                String result = deleteEmployee.delete(connection, employeeId);
                
                updateStatus(result);
                loadEmployeesData(employeesTable);
            }
        });
    } catch (NumberFormatException ex) {
        showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid employee ID", 
                  "Please enter a valid employee ID.");
    } catch (Exception ex) {
        showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not delete employee", ex.getMessage());
    }
});

refreshButton.setOnAction(e -> loadEmployeesData(employeesTable));

// Table selection handler
employeesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
    if (newSelection != null) {
        employeeIdField.setText(String.valueOf(newSelection.getEmployeeId()));
        nameField.setText(newSelection.getEmployeeName());
        roleComboBox.setValue(newSelection.getRole());
        contactField.setText(newSelection.getContact());
    }
});

// Add components to form
form.getChildren().addAll(
    formTitle,
    new Label("Employee Name:"),
    nameField,
    new Label("Role:"),
    roleComboBox,
    new Label("Contact Number:"),
    contactField,
    new Label("Employee ID (for update/delete):"),
    employeeIdField,
    new Separator(),
    addButton,
    updateRoleButton,
    updateContactButton,
    deleteButton,
    refreshButton
);

return form;
}

// ===== STOCK TAB =====
private void createStockTab() {
    stockTab = new Tab("Stock");
    
    VBox content = new VBox(20);
    content.setPadding(new Insets(20));
    
    Label title = new Label("Stock Management");
    title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    
    // Low stock items section
    TitledPane lowStockPane = createLowStockSection();
    
    // Stock check section
    TitledPane stockCheckPane = createStockCheckSection();
    
    content.getChildren().addAll(title, lowStockPane, stockCheckPane);
    stockTab.setContent(content);
}

private TitledPane createLowStockSection() {
    VBox content = new VBox(10);
    content.setPadding(new Insets(10));
    
    Label sectionTitle = new Label("Low Stock Items");
    sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
    
    TableView<ProductViewModel> lowStockTable = new TableView<>();
    
    // Create columns
    TableColumn<ProductViewModel, Integer> idColumn = new TableColumn<>("ID");
    idColumn.setCellValueFactory(cellData -> cellData.getValue().productIdProperty().asObject());
    
    TableColumn<ProductViewModel, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
    
    TableColumn<ProductViewModel, String> categoryColumn = new TableColumn<>("Category");
    categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
    
    TableColumn<ProductViewModel, Integer> quantityColumn = new TableColumn<>("Quantity");
    quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
    
    // Add columns to table
    lowStockTable.getColumns().addAll(idColumn, nameColumn, categoryColumn, quantityColumn);
    
    // Set threshold spinner
    HBox thresholdBox = new HBox(10);
    thresholdBox.setAlignment(Pos.CENTER_LEFT);
    
    Label thresholdLabel = new Label("Low Stock Threshold:");
    Spinner<Integer> thresholdSpinner = new Spinner<>(1, 100, 10);
    thresholdSpinner.setEditable(true);
    
    Button checkButton = new Button("Check Low Stock");
    checkButton.setOnAction(e -> loadLowStockData(lowStockTable, thresholdSpinner.getValue()));
    
    thresholdBox.getChildren().addAll(thresholdLabel, thresholdSpinner, checkButton);
    
    content.getChildren().addAll(sectionTitle, thresholdBox, lowStockTable);
    
    TitledPane titledPane = new TitledPane("Low Stock Items", content);
    titledPane.setExpanded(true);
    
    return titledPane;
}

private void loadLowStockData(TableView<ProductViewModel> table, int threshold) {
    try {
        // Clear existing items
        table.getItems().clear();
        
        // Create display products instance
        DisplayProducts displayProducts = new DisplayProducts();
        
        // Get products from database
        List<?> products = displayProducts.displayProducts(connection);
        
        // Convert and add to table
        ObservableList<ProductViewModel> data = FXCollections.observableArrayList();
        
        for (Object product : products) {
            // Reflection to access the product properties
            Integer productId = (Integer) product.getClass().getMethod("getProductId").invoke(product);
            String productName = (String) product.getClass().getMethod("getProductName").invoke(product);
            String category = (String) product.getClass().getMethod("getCategory").invoke(product);
            Integer quantity = (Integer) product.getClass().getMethod("getQuantity").invoke(product);
            Double price = (Double) product.getClass().getMethod("getPrice").invoke(product);
            
            // Add only if below threshold
            if (quantity <= threshold) {
                data.add(new ProductViewModel(productId, productName, category, quantity, price));
            }
        }
        
        table.setItems(data);
        updateStatus("Found " + data.size() + " products below threshold of " + threshold);
        
    } catch (Exception e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Data Loading Error", "Failed to load low stock products", e.getMessage());
    }
}

private TitledPane createStockCheckSection() {
    VBox content = new VBox(10);
    content.setPadding(new Insets(10));
    
    Label sectionTitle = new Label("Check Specific Product Stock");
    sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
    
    HBox inputBox = new HBox(10);
    inputBox.setAlignment(Pos.CENTER_LEFT);
    
    Label productIdLabel = new Label("Product ID:");
    TextField productIdField = new TextField();
    productIdField.setPromptText("Enter product ID");
    
    Button checkButton = new Button("Check Stock");
    
    TextArea resultArea = new TextArea();
    resultArea.setEditable(false);
    resultArea.setPrefHeight(100);
    
    checkButton.setOnAction(e -> {
        try {
            int productId = Integer.parseInt(productIdField.getText().trim());
            
            CheckStock checkStock = new CheckStock();
            String result = checkStock.check(connection, productId);
            
            resultArea.setText(result);
            updateStatus("Stock checked for product ID " + productId);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid product ID", 
                      "Please enter a valid product ID.");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not check stock", ex.getMessage());
        }
    });
    
    inputBox.getChildren().addAll(productIdLabel, productIdField, checkButton);
    
    content.getChildren().addAll(sectionTitle, inputBox, resultArea);
    
    TitledPane titledPane = new TitledPane("Check Stock", content);
    titledPane.setExpanded(true);
    
    return titledPane;
}

// ===== ORDERS TAB =====
private void createOrdersTab() {
    ordersTab = new Tab("Orders");
    
    // Create split pane for list and form
    SplitPane splitPane = new SplitPane();
    
    // Orders table view
    TableView<OrderViewModel> ordersTable = new TableView<>();
    setupOrdersTableView(ordersTable);
    
    // Order form
    VBox orderForm = createOrderForm(ordersTable);
    
    // Add to split pane
    splitPane.getItems().addAll(ordersTable, orderForm);
    splitPane.setDividerPositions(0.7);
    
    ordersTab.setContent(splitPane);
}

private void setupOrdersTableView(TableView<OrderViewModel> table) {
    // Create columns
    TableColumn<OrderViewModel, Integer> orderIdColumn = new TableColumn<>("Order ID");
    orderIdColumn.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty().asObject());
    
    TableColumn<OrderViewModel, Integer> productIdColumn = new TableColumn<>("Product ID");
    productIdColumn.setCellValueFactory(cellData -> cellData.getValue().productIdProperty().asObject());
    
    TableColumn<OrderViewModel, String> productNameColumn = new TableColumn<>("Product");
    productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
    
    TableColumn<OrderViewModel, Integer> supplierIdColumn = new TableColumn<>("Supplier ID");
    supplierIdColumn.setCellValueFactory(cellData -> cellData.getValue().supplierIdProperty().asObject());
    
    TableColumn<OrderViewModel, Integer> quantityColumn = new TableColumn<>("Quantity");
    quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
    
    TableColumn<OrderViewModel, String> orderDateColumn = new TableColumn<>("Order Date");
    orderDateColumn.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());
    
    TableColumn<OrderViewModel, String> statusColumn = new TableColumn<>("Status");
    statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    
    // Add columns to table
    table.getColumns().addAll(orderIdColumn, productIdColumn, productNameColumn, supplierIdColumn, 
                             quantityColumn, orderDateColumn, statusColumn);
    
    // Load data
    loadOrdersData(table);
}

private void loadOrdersData(TableView<OrderViewModel> table) {
    try {
        // Clear existing items
        table.getItems().clear();
        
        // Create display orders instance
        DisplayOrders displayOrders = new DisplayOrders();
        
        // Get orders from database
        List<?> orders = displayOrders.displayOrders(connection);
        
        // Convert and add to table
        ObservableList<OrderViewModel> data = FXCollections.observableArrayList();
        
        for (Object order : orders) {
            // Reflection to access the order properties
            Integer orderId = (Integer) order.getClass().getMethod("getOrderId").invoke(order);
            Integer productId = (Integer) order.getClass().getMethod("getProductId").invoke(order);
            String productName = (String) order.getClass().getMethod("getProductName").invoke(order);
            Integer supplierId = (Integer) order.getClass().getMethod("getSupplierId").invoke(order);
            Integer quantity = (Integer) order.getClass().getMethod("getQuantity").invoke(order);
            String orderDate = (String) order.getClass().getMethod("getOrderDate").invoke(order);
            String status = (String) order.getClass().getMethod("getStatus").invoke(order);
            
            data.add(new OrderViewModel(orderId, productId, productName, supplierId, quantity, orderDate, status));
        }
        
        table.setItems(data);
        updateStatus("Loaded " + data.size() + " orders");
        
    } catch (Exception e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Data Loading Error", "Failed to load orders", e.getMessage());
    }
}

private VBox createOrderForm(TableView<OrderViewModel> ordersTable) {
    VBox form = new VBox();
    form.setSpacing(10);
    form.setPadding(new Insets(20));
    
    // Form title
    Label formTitle = new Label("Place New Order");
    formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    
    // Product selection
    Label productLabel = new Label("Select Product:");
    ComboBox<String> productComboBox = new ComboBox<>();
    productComboBox.setMaxWidth(Double.MAX_VALUE);
    
    // Load products into combo box
    try {
        DisplayProducts displayProducts = new DisplayProducts();
        List<?> products = displayProducts.displayProducts(connection);
        
        for (Object product : products) {
            Integer productId = (Integer) product.getClass().getMethod("getProductId").invoke(product);
            String productName = (String) product.getClass().getMethod("getProductName").invoke(product);
            productComboBox.getItems().add(productId + " - " + productName);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    // Supplier selection
    Label supplierLabel = new Label("Select Supplier:");
    ComboBox<String> supplierComboBox = new ComboBox<>();
    supplierComboBox.setMaxWidth(Double.MAX_VALUE);
    
    // Load suppliers into combo box
    try {
        DisplaySuppliers displaySuppliers = new DisplaySuppliers();
        List<?> suppliers = displaySuppliers.displaySuppliers(connection);
        
        for (Object supplier : suppliers) {
            Integer supplierId = (Integer) supplier.getClass().getMethod("getSupplierId").invoke(supplier);
            String supplierName = (String) supplier.getClass().getMethod("getSupplierName").invoke(supplier);
            supplierComboBox.getItems().add(supplierId + " - " + supplierName);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    // Quantity field
    Label quantityLabel = new Label("Quantity:");
    Spinner<Integer> quantitySpinner = new Spinner<>(1, 1000, 10);
    quantitySpinner.setEditable(true);
    quantitySpinner.setMaxWidth(Double.MAX_VALUE);
    
    // Action buttons
    Button placeOrderButton = new Button("Place Order");
    placeOrderButton.setMaxWidth(Double.MAX_VALUE);
    placeOrderButton.getStyleClass().add("action-button");
    
    Button refreshButton = new Button("Refresh Order List");
    refreshButton.setMaxWidth(Double.MAX_VALUE);
    
    // Place order handler
    placeOrderButton.setOnAction(e -> {
        try {
            String selectedProduct = productComboBox.getValue();
            String selectedSupplier = supplierComboBox.getValue();
            
            if (selectedProduct == null || selectedSupplier == null) {
                showAlert(Alert.AlertType.WARNING, "Missing Information", 
                         "Product and supplier selection required", 
                         "Please select both a product and a supplier.");
                return;
            }
            
            int productId = Integer.parseInt(selectedProduct.split(" - ")[0]);
            int supplierId = Integer.parseInt(selectedSupplier.split(" - ")[0]);
            int quantity = quantitySpinner.getValue();
            
            PlaceOrder placeOrder = new PlaceOrder();
            String result = placeOrder.placeOrder(connection, productId, supplierId, quantity);
            
            updateStatus(result);
            loadOrdersData(ordersTable);
            
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Operation Failed", "Could not place order", ex.getMessage());
        }
    });
    
    refreshButton.setOnAction(e -> loadOrdersData(ordersTable));
    
    // Add components to form
    form.getChildren().addAll(
        formTitle,
        productLabel,
        productComboBox,
        supplierLabel,
        supplierComboBox,
        quantityLabel,
        quantitySpinner,
        new Separator(),
        placeOrderButton,
        refreshButton
    );
    
    return form;
}

// ===== REPORTS TAB =====
private void createReportsTab() {
    reportsTab = new Tab("Reports");
    
    VBox content = new VBox(20);
    content.setPadding(new Insets(20));
    
    Label title = new Label("Reports & Analytics");
    title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    
    // Product stock report
    TitledPane productStockPane = createProductStockReport();
    
    // Order history report
    TitledPane orderHistoryPane = createOrderHistoryReport();
    
    content.getChildren().addAll(title, productStockPane, orderHistoryPane);
    reportsTab.setContent(content);
}

private TitledPane createProductStockReport() {
    VBox content = new VBox(10);
    content.setPadding(new Insets(10));
    
    // Create report table
    TableView<ProductViewModel> reportTable = new TableView<>();
    
    // Create columns
    TableColumn<ProductViewModel, Integer> idColumn = new TableColumn<>("ID");
    idColumn.setCellValueFactory(cellData -> cellData.getValue().productIdProperty().asObject());
    
    TableColumn<ProductViewModel, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
    
    TableColumn<ProductViewModel, String> categoryColumn = new TableColumn<>("Category");
    categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
    
    TableColumn<ProductViewModel, Integer> quantityColumn = new TableColumn<>("Quantity");
    quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
    
    TableColumn<ProductViewModel, Double> priceColumn = new TableColumn<>("Price");
    priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
    
    // Value column (calculated: quantity * price)
    TableColumn<ProductViewModel, Double> valueColumn = new TableColumn<>("Total Value");
    valueColumn.setCellValueFactory(cellData -> {
        double value = cellData.getValue().getQuantity() * cellData.getValue().getPrice();
        return javafx.beans.property.SimpleDoubleProperty(value).asObject();
    });
    
    // Add columns to table
    reportTable.getColumns().addAll(idColumn, nameColumn, categoryColumn, quantityColumn, priceColumn, valueColumn);
    
    // Add filter options
    HBox filterBox = new HBox(10);
    filterBox.setAlignment(Pos.CENTER_LEFT);
    
    Label categoryLabel = new Label("Filter by Category:");
    ComboBox<String> categoryComboBox = new ComboBox<>();
    categoryComboBox.setPromptText("All Categories");
    
    Button generateButton = new Button("Generate Report");
    Button exportButton = new Button("Export to CSV");
    
    filterBox.getChildren().addAll(categoryLabel, categoryComboBox, generateButton, exportButton);
    
    // Load categories
    try {
        DisplayProducts displayProducts = new DisplayProducts();
        List<?> products = displayProducts.displayProducts(connection);
        
        java.util.Set<String> categories = new java.util.HashSet<>();
        
        for (Object product : products) {
            String category = (String) product.getClass().getMethod("getCategory").invoke(product);
            categories.add(category);
        }
        
        categoryComboBox.getItems().add("All Categories");
        categoryComboBox.getItems().addAll(categories);
        categoryComboBox.setValue("All Categories");
        
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    // Generate report button handler
    generateButton.setOnAction(e -> {
        String selectedCategory = categoryComboBox.getValue();
        loadProductReportData(reportTable, selectedCategory);
    });
    
    // Export button handler
    exportButton.setOnAction(e -> {
        try {
            String fileName = "product_stock_report_" + java.time.LocalDate.now().toString() + ".csv";
            java.io.FileWriter writer = new java.io.FileWriter(fileName);
            
            // Write header
            writer.write("ID,Name,Category,Quantity,Price,Total Value\n");
            
            // Write data
            for (ProductViewModel product : reportTable.getItems()) {
                double totalValue = product.getQuantity() * product.getPrice();
                writer.write(String.format("%d,%s,%s,%d,%.2f,%.2f\n", 
                    product.getProductId(), 
                    product.getProductName().replace(",", ";"), 
                    product.getCategory().replace(",", ";"),
                    product.getQuantity(),
                    product.getPrice(),
                    totalValue));
            }
            
            writer.close();
            updateStatus("Report exported to " + fileName);
            
            showAlert(Alert.AlertType.INFORMATION, "Export Successful", 
                     "Report has been exported", "Saved to " + fileName);
            
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Export Failed", 
                     "Could not export report", ex.getMessage());
        }
    });
    
    // Add summary section
    HBox summaryBox = new HBox(20);
    summaryBox.setPadding(new Insets(10));
    summaryBox.setAlignment(Pos.CENTER);
    
    Label totalItemsLabel = new Label("Total Items: 0");
    Label totalValueLabel = new Label("Total Inventory Value: $0.00");
    Label averagePriceLabel = new Label("Average Price: $0.00");
    
    summaryBox.getChildren().addAll(totalItemsLabel, totalValueLabel, averagePriceLabel);
    
    // Update summary when report is generated
    generateButton.setOnAction(e -> {
        String selectedCategory = categoryComboBox.getValue();
        loadProductReportData(reportTable, selectedCategory);
        
        // Calculate summary statistics
        int totalItems = 0;
        double totalValue = 0.0;
        double totalPrice = 0.0;
        
        for (ProductViewModel product : reportTable.getItems()) {
            totalItems += product.getQuantity();
            totalValue += product.getQuantity() * product.getPrice();
            totalPrice += product.getPrice();
        }
        
        double averagePrice = reportTable.getItems().size() > 0 ? totalPrice / reportTable.getItems().size() : 0;
        
        totalItemsLabel.setText("Total Items: " + totalItems);
        totalValueLabel.setText("Total Inventory Value: $" + String.format("%.2f", totalValue));
        averagePriceLabel.setText("Average Price: $" + String.format("%.2f", averagePrice));
    });
    
    content.getChildren().addAll(filterBox, reportTable, summaryBox);
    
    TitledPane titledPane = new TitledPane("Product Stock Report", content);
    titledPane.setExpanded(true);
    
    return titledPane;
}

private void loadProductReportData(TableView<ProductViewModel> table, String category) {
    try {
        // Clear existing items
        table.getItems().clear();
        
        // Create display products instance
        DisplayProducts displayProducts = new DisplayProducts();
        
        // Get products from database
        List<?> products = displayProducts.displayProducts(connection);
        
        // Convert and add to table
        ObservableList<ProductViewModel> data = FXCollections.observableArrayList();
        
        for (Object product : products) {
            // Reflection to access the product properties
            Integer productId = (Integer) product.getClass().getMethod("getProductId").invoke(product);
            String productName = (String) product.getClass().getMethod("getProductName").invoke(product);
            String productCategory = (String) product.getClass().getMethod("getCategory").invoke(product);
            Integer quantity = (Integer) product.getClass().getMethod("getQuantity").invoke(product);
            Double price = (Double) product.getClass().getMethod("getPrice").invoke(product);
            
            // Filter by category if specified
            if (category == null || category.equals("All Categories") || productCategory.equals(category)) {
                data.add(new ProductViewModel(productId, productName, productCategory, quantity, price));
            }
        }
        
        table.setItems(data);
        updateStatus("Generated report with " + data.size() + " products");
        
    } catch (Exception e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Report Generation Error", "Failed to generate product report", e.getMessage());
    }
}

private TitledPane createOrderHistoryReport() {
    VBox content = new VBox(10);
    content.setPadding(new Insets(10));
    
    // Date range selection
    HBox dateRangeBox = new HBox(10);
    dateRangeBox.setAlignment(Pos.CENTER_LEFT);
    
    Label fromLabel = new Label("From:");
    DatePicker fromDatePicker = new DatePicker();
    
    Label toLabel = new Label("To:");
    DatePicker toDatePicker = new DatePicker();
    
    Button generateButton = new Button("Generate Report");
    
    dateRangeBox.getChildren().addAll(fromLabel, fromDatePicker, toLabel, toDatePicker, generateButton);
    
    // Create report table
    TableView<OrderViewModel> reportTable = new TableView<>();
    
    // Create columns
    TableColumn<OrderViewModel, Integer> orderIdColumn = new TableColumn<>("Order ID");
    orderIdColumn.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty().asObject());
    
    TableColumn<OrderViewModel, String> productNameColumn = new TableColumn<>("Product");
    productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
    
    TableColumn<OrderViewModel, Integer> quantityColumn = new TableColumn<>("Quantity");
    quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
    
    TableColumn<OrderViewModel, String> orderDateColumn = new TableColumn<>("Order Date");
    orderDateColumn.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());
    
    TableColumn<OrderViewModel, String> statusColumn = new TableColumn<>("Status");
    statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    
    // Add columns to table
    reportTable.getColumns().addAll(orderIdColumn, productNameColumn, quantityColumn, orderDateColumn, statusColumn);
    
    // Generate report button handler
    generateButton.setOnAction(e -> {
        try {
            // Get date range
            java.time.LocalDate fromDate = fromDatePicker.getValue();
            java.time.LocalDate toDate = toDatePicker.getValue();
            
            if (fromDate == null || toDate == null) {
                showAlert(Alert.AlertType.WARNING, "Missing Information", 
                         "Date range is required", 
                         "Please select both from and to dates.");
                return;
            }
            
            if (fromDate.isAfter(toDate)) {
                showAlert(Alert.AlertType.WARNING, "Invalid Date Range", 
                         "From date must be before to date", 
                         "Please select a valid date range.");
                return;
            }
            
            // Clear existing items
            reportTable.getItems().clear();
            
            // Create display orders instance
            DisplayOrders displayOrders = new DisplayOrders();
            
            // Get orders from database
            List<?> orders = displayOrders.displayOrders(connection);
            
            // Convert and add to table
            ObservableList<OrderViewModel> data = FXCollections.observableArrayList();
            
            for (Object order : orders) {
                // Reflection to access the order properties
                Integer orderId = (Integer) order.getClass().getMethod("getOrderId").invoke(order);
                Integer productId = (Integer) order.getClass().getMethod("getProductId").invoke(order);
                String productName = (String) order.getClass().getMethod("getProductName").invoke(order);
                Integer supplierId = (Integer) order.getClass().getMethod("getSupplierId").invoke(order);
                Integer quantity = (Integer) order.getClass().getMethod("getQuantity").invoke(order);
                String orderDate = (String) order.getClass().getMethod("getOrderDate").invoke(order);
                String status = (String) order.getClass().getMethod("getStatus").invoke(order);
                
                // Parse date from string (assuming format is YYYY-MM-DD)
                java.time.LocalDate date = java.time.LocalDate.parse(orderDate);
                
                // Filter by date range
                if (!date.isBefore(fromDate) && !date.isAfter(toDate)) {
                    data.add(new OrderViewModel(orderId, productId, productName, supplierId, quantity, orderDate, status));
                }
            }
            
            reportTable.setItems(data);
            updateStatus("Generated order report with " + data.size() + " orders");
            
        } } catch (Exception ex) {
        showAlert(Alert.AlertType.ERROR, "Report Generation Error", 
                 "Failed to generate order report", ex.getMessage());
    }
});

// Add export button
Button exportButton = new Button("Export to CSV");
exportButton.setOnAction(e -> {
    try {
        String fileName = "order_history_report_" + java.time.LocalDate.now().toString() + ".csv";
        java.io.FileWriter writer = new java.io.FileWriter(fileName);
        
        // Write header
        writer.write("Order ID,Product,Quantity,Order Date,Status\n");
        
        // Write data
        for (OrderViewModel order : reportTable.getItems()) {
            writer.write(String.format("%d,%s,%d,%s,%s\n", 
                order.getOrderId(), 
                order.getProductName().replace(",", ";"), 
                order.getQuantity(),
                order.getOrderDate(),
                order.getStatus()));
        }
        
        writer.close();
        updateStatus("Order report exported to " + fileName);
        
        showAlert(Alert.AlertType.INFORMATION, "Export Successful", 
                 "Report has been exported", "Saved to " + fileName);
        
    } catch (Exception ex) {
        showAlert(Alert.AlertType.ERROR, "Export Failed", 
                 "Could not export report", ex.getMessage());
    }
});

// Add summary section
HBox summaryBox = new HBox(20);
summaryBox.setPadding(new Insets(10));
summaryBox.setAlignment(Pos.CENTER);

Label totalOrdersLabel = new Label("Total Orders: 0");
Label pendingOrdersLabel = new Label("Pending Orders: 0");
Label completedOrdersLabel = new Label("Completed Orders: 0");

summaryBox.getChildren().addAll(totalOrdersLabel, pendingOrdersLabel, completedOrdersLabel);

// Update summary when report is generated
generateButton.addEventHandler(ActionEvent.ACTION, e -> {
    int totalOrders = reportTable.getItems().size();
    int pendingOrders = 0;
    int completedOrders = 0;
    
    for (OrderViewModel order : reportTable.getItems()) {
        if ("Completed".equals(order.getStatus())) {
            completedOrders++;
        } else if ("Pending".equals(order.getStatus())) {
            pendingOrders++;
        }
    }
    
    totalOrdersLabel.setText("Total Orders: " + totalOrders);
    pendingOrdersLabel.setText("Pending Orders: " + pendingOrders);
    completedOrdersLabel.setText("Completed Orders: " + completedOrders);
});

dateRangeBox.getChildren().add(exportButton);
content.getChildren().addAll(dateRangeBox, reportTable, summaryBox);

TitledPane titledPane = new TitledPane("Order History Report", content);
titledPane.setExpanded(true);

return titledPane;
}

// ===== UTILITY METHODS =====
private void showAlert(Alert.AlertType type, String title, String header, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
}

private void updateStatus(String message) {
    statusBar.setText(message);
}

// ===== VIEW MODELS =====
private static class EmployeeViewModel {
    private final SimpleIntegerProperty employeeId;
    private final SimpleStringProperty employeeName;
    private final SimpleStringProperty role;
    private final SimpleStringProperty contact;
    
    public EmployeeViewModel(int employeeId, String employeeName, String role, String contact) {
        this.employeeId = new SimpleIntegerProperty(employeeId);
        this.employeeName = new SimpleStringProperty(employeeName);
        this.role = new SimpleStringProperty(role);
        this.contact = new SimpleStringProperty(contact);
    }
    
    public IntegerProperty employeeIdProperty() {
        return employeeId;
    }
    
    public StringProperty employeeNameProperty() {
        return employeeName;
    }
    
    public StringProperty roleProperty() {
        return role;
    }
    
    public StringProperty contactProperty() {
        return contact;
    }
    
    public int getEmployeeId() {
        return employeeId.get();
    }
    
    public String getEmployeeName() {
        return employeeName.get();
    }
    
    public String getRole() {
        return role.get();
    }
    
    public String getContact() {
        return contact.get();
    }
}

private static class ProductViewModel {
    private final SimpleIntegerProperty productId;
    private final SimpleStringProperty productName;
    private final SimpleStringProperty category;
    private final SimpleIntegerProperty quantity;
    private final SimpleDoubleProperty price;
    
    public ProductViewModel(int productId, String productName, String category, int quantity, double price) {
        this.productId = new SimpleIntegerProperty(productId);
        this.productName = new SimpleStringProperty(productName);
        this.category = new SimpleStringProperty(category);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
    }
    
    public IntegerProperty productIdProperty() {
        return productId;
    }
    
    public StringProperty productNameProperty() {
        return productName;
    }
    
    public StringProperty categoryProperty() {
        return category;
    }
    
    public IntegerProperty quantityProperty() {
        return quantity;
    }
    
    public DoubleProperty priceProperty() {
        return price;
    }
    
    public int getProductId() {
        return productId.get();
    }
    
    public String getProductName() {
        return productName.get();
    }
    
    public String getCategory() {
        return category.get();
    }
    
    public int getQuantity() {
        return quantity.get();
    }
    
    public double getPrice() {
        return price.get();
    }
}

private static class OrderViewModel {
    private final SimpleIntegerProperty orderId;
    private final SimpleIntegerProperty productId;
    private final SimpleStringProperty productName;
    private final SimpleIntegerProperty supplierId;
    private final SimpleIntegerProperty quantity;
    private final SimpleStringProperty orderDate;
    private final SimpleStringProperty status;
    
    public OrderViewModel(int orderId, int productId, String productName, int supplierId, 
                         int quantity, String orderDate, String status) {
        this.orderId = new SimpleIntegerProperty(orderId);
        this.productId = new SimpleIntegerProperty(productId);
        this.productName = new SimpleStringProperty(productName);
        this.supplierId = new SimpleIntegerProperty(supplierId);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.orderDate = new SimpleStringProperty(orderDate);
        this.status = new SimpleStringProperty(status);
    }
    
    public IntegerProperty orderIdProperty() {
        return orderId;
    }
    
    public IntegerProperty productIdProperty() {
        return productId;
    }
    
    public StringProperty productNameProperty() {
        return productName;
    }
    
    public IntegerProperty supplierIdProperty() {
        return supplierId;
    }
    
    public IntegerProperty quantityProperty() {
        return quantity;
    }
    
    public StringProperty orderDateProperty() {
        return orderDate;
    }
    
    public StringProperty statusProperty() {
        return status;
    }
    
    public int getOrderId() {
        return orderId.get();
    }
    
    public int getProductId() {
        return productId.get();
    }
    
    public String getProductName() {
        return productName.get();
    }
    
    public int getSupplierId() {
        return supplierId.get();
    }
    
    public int getQuantity() {
        return quantity.get();
    }
    
    public String getOrderDate() {
        return orderDate.get();
    }
    
    public String getStatus() {
        return status.get();
    }
}

// Main method - Application entry point
public static void main(String[] args) {
    launch(args);
}