-- 1. Products Table
CREATE TABLE Products (
    Product_ID NUMBER PRIMARY KEY,
    Product_Name VARCHAR2(100) NOT NULL,
    Category VARCHAR2(50),
    Quantity NUMBER NOT NULL CHECK (Quantity >= 0),
    Price NUMBER(10,2) NOT NULL CHECK (Price >= 0)
);

-- 2. Suppliers Table
CREATE TABLE Suppliers (
    Supplier_ID NUMBER PRIMARY KEY,
    Supplier_Name VARCHAR2(100) NOT NULL,
    Contact_Number VARCHAR2(15),
    Email VARCHAR2(100),
    Address VARCHAR2(255)
);

-- 3. Stock Table
CREATE TABLE Stock (
    Stock_ID NUMBER PRIMARY KEY,
    Product_ID NUMBER,
    Supplier_ID NUMBER,
    Date_Added DATE NOT NULL,
    Quantity_Added NUMBER NOT NULL CHECK (Quantity_Added > 0),
    FOREIGN KEY (Product_ID) REFERENCES Products(Product_ID) ON DELETE CASCADE,
    FOREIGN KEY (Supplier_ID) REFERENCES Suppliers(Supplier_ID) ON DELETE SET NULL
);

-- 4. Orders Table
CREATE TABLE Orders (
    Order_ID NUMBER PRIMARY KEY,
    Customer_Name VARCHAR2(100) NOT NULL,
    Product_ID NUMBER,
    Order_Date DATE NOT NULL,
    Quantity_Ordered NUMBER NOT NULL CHECK (Quantity_Ordered > 0),
    Total_Amount NUMBER(10,2) NOT NULL CHECK (Total_Amount >= 0),
    FOREIGN KEY (Product_ID) REFERENCES Products(Product_ID) ON DELETE CASCADE
);

-- 5. Employees Table
CREATE TABLE Employees (
    Employee_ID NUMBER PRIMARY KEY,
    Employee_Name VARCHAR2(100) NOT NULL,
    Role VARCHAR2(50) CHECK (Role IN ('Manager', 'Stock Handler', 'Sales')),
    Contact VARCHAR2(15)
);
