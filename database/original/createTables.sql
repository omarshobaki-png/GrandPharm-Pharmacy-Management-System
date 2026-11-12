use GrandPharm;
CREATE TABLE Branch (
    BID INT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    City VARCHAR(100) NOT NULL,
    Phone VARCHAR(20),
    Location VARCHAR(150)
);

CREATE TABLE Warehouse (
    WareID INT PRIMARY KEY,
    WareName VARCHAR(100) NOT NULL,
    BID INT NOT NULL,
    Location VARCHAR(150),
    Capacity INT,
    FOREIGN KEY (BID) REFERENCES Branch(BID)
);

CREATE TABLE Employee (
    EID INT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Role VARCHAR(50),
    Phone VARCHAR(20),
    HireDate DATE,
    Salary DECIMAL(10,2),
    BID INT NOT NULL,
    FOREIGN KEY (BID) REFERENCES Branch(BID)
);

CREATE TABLE Customer (
    CID INT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Phone VARCHAR(20),
    DOB DATE,
    Gender VARCHAR(10)
);

CREATE TABLE Supplier (
    SuppID INT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Phone VARCHAR(20),
    Email VARCHAR(100),
    Address VARCHAR(150)
);

CREATE TABLE Category (
    CatID INT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Description VARCHAR(255)
);

CREATE TABLE Product (
    ProdID INT PRIMARY KEY,
    ProdName VARCHAR(100) NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    Prescription_Requirement BOOLEAN NOT NULL,
    CatID INT NOT NULL,
    FOREIGN KEY (CatID) REFERENCES Category(CatID)
);

CREATE TABLE PaymentMethod (
    PayMethodID INT PRIMARY KEY,
    MethodName VARCHAR(50) NOT NULL,
    Description VARCHAR(255)
);

CREATE TABLE Sale (
    SID INT PRIMARY KEY,
    SaleDate DATE NOT NULL,
    SaleTime TIME NOT NULL,
    TotalAmount DECIMAL(10,2),
    CID INT NOT NULL,
    BID INT NOT NULL,
    EID INT NOT NULL,
    PayMethodID INT NOT NULL,
    FOREIGN KEY (CID) REFERENCES Customer(CID),
    FOREIGN KEY (BID) REFERENCES Branch(BID),
    FOREIGN KEY (EID) REFERENCES Employee(EID),
    FOREIGN KEY (PayMethodID) REFERENCES PaymentMethod(PayMethodID)
);

CREATE TABLE PurchaseOrder (
    PurchID INT PRIMARY KEY,
    PurchaseDate DATE NOT NULL,
    TotalCost DECIMAL(10,2),
    SuppID INT NOT NULL,
    BID INT NOT NULL,
    EID INT NOT NULL,
    FOREIGN KEY (SuppID) REFERENCES Supplier(SuppID),
    FOREIGN KEY (BID) REFERENCES Branch(BID),
    FOREIGN KEY (EID) REFERENCES Employee(EID)
);

CREATE TABLE Prescription (
    PrescID INT PRIMARY KEY,
    PrescriptionDate DATE NOT NULL,
    Doc_Name VARCHAR(100),
    CID INT NOT NULL,
    BID INT NOT NULL,
    EID INT NOT NULL,
    FOREIGN KEY (CID) REFERENCES Customer(CID),
    FOREIGN KEY (BID) REFERENCES Branch(BID),
    FOREIGN KEY (EID) REFERENCES Employee(EID)
);

CREATE TABLE SaleItem (
    SID INT NOT NULL,
    ProdID INT NOT NULL,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (SID, ProdID),
    FOREIGN KEY (SID) REFERENCES Sale(SID),
    FOREIGN KEY (ProdID) REFERENCES Product(ProdID)
);

CREATE TABLE PurchaseItem (
    PurchID INT NOT NULL,
    ProdID INT NOT NULL,
    Quantity INT NOT NULL,
    UnitCost DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (PurchID, ProdID),
    FOREIGN KEY (PurchID) REFERENCES PurchaseOrder(PurchID),
    FOREIGN KEY (ProdID) REFERENCES Product(ProdID)
);

CREATE TABLE PrescriptionItem (
    PrescID INT NOT NULL,
    ProdID INT NOT NULL,
    Dosage VARCHAR(100),
    Duration VARCHAR(100),
    Instructions VARCHAR(255),
    PRIMARY KEY (PrescID, ProdID),
    FOREIGN KEY (PrescID) REFERENCES Prescription(PrescID),
    FOREIGN KEY (ProdID) REFERENCES Product(ProdID)
);

CREATE TABLE Inventory (
    WareID INT NOT NULL,
    ProdID INT NOT NULL,
    QuantityAvailable INT NOT NULL,
    MinStock INT,
    LastUpdated DATE,
    PRIMARY KEY (WareID, ProdID),
    FOREIGN KEY (WareID) REFERENCES Warehouse(WareID),
    FOREIGN KEY (ProdID) REFERENCES Product(ProdID)
);

CREATE TABLE Batch (
    WareID INT NOT NULL,
    BNum INT NOT NULL,
    ProdID INT NOT NULL,
    ManufacturingDate DATE,
    ExpDate DATE,
    QuantityReceived INT,
    QuantityRemaining INT,
    PRIMARY KEY (WareID, BNum),
    FOREIGN KEY (WareID) REFERENCES Warehouse(WareID),
    FOREIGN KEY (ProdID) REFERENCES Product(ProdID)
);

CREATE TABLE SupplierProduct (
    SuppID INT NOT NULL,
    ProdID INT NOT NULL,
    SupplierPrice DECIMAL(10,2),
    DelivTime VARCHAR(50),
    Availability VARCHAR(50),
    PRIMARY KEY (SuppID, ProdID),
    FOREIGN KEY (SuppID) REFERENCES Supplier(SuppID),
    FOREIGN KEY (ProdID) REFERENCES Product(ProdID)
);
use grandpharm;
ALTER TABLE Branch MODIFY BID INT NOT NULL AUTO_INCREMENT;
ALTER TABLE Warehouse MODIFY WareID INT NOT NULL AUTO_INCREMENT;
ALTER TABLE Employee MODIFY EID INT NOT NULL AUTO_INCREMENT;
ALTER TABLE Customer MODIFY CID INT NOT NULL AUTO_INCREMENT;
ALTER TABLE Supplier MODIFY SuppID INT NOT NULL AUTO_INCREMENT;
ALTER TABLE Category MODIFY CatID INT NOT NULL AUTO_INCREMENT;
ALTER TABLE Product MODIFY ProdID INT NOT NULL AUTO_INCREMENT;
ALTER TABLE PaymentMethod MODIFY PayMethodID INT NOT NULL AUTO_INCREMENT;
ALTER TABLE Sale MODIFY SID INT NOT NULL AUTO_INCREMENT;
ALTER TABLE PurchaseOrder MODIFY PurchID INT NOT NULL AUTO_INCREMENT;
ALTER TABLE Prescription MODIFY PrescID INT NOT NULL AUTO_INCREMENT;
ALTER TABLE Branch MODIFY Location VARCHAR(150) NOT NULL;
ALTER TABLE Warehouse MODIFY Location VARCHAR(150) NOT NULL;
ALTER TABLE Supplier MODIFY Phone VARCHAR(20) NOT NULL;
ALTER TABLE Product ADD CONSTRAINT chk_price CHECK (Price >= 0);
ALTER TABLE Sale MODIFY TotalAmount DECIMAL(10,2) NOT NULL;
ALTER TABLE PurchaseOrder MODIFY TotalCost DECIMAL(10,2) NOT NULL;

USE grandpharm;


DROP TABLE IF EXISTS SupplierProduct;
DROP TABLE IF EXISTS Batch;
DROP TABLE IF EXISTS Inventory;
DROP TABLE IF EXISTS PrescriptionItem;
DROP TABLE IF EXISTS PurchaseItem;
DROP TABLE IF EXISTS SaleItem;
DROP TABLE IF EXISTS Prescription;
DROP TABLE IF EXISTS PurchaseOrder;
DROP TABLE IF EXISTS Sale;
DROP TABLE IF EXISTS PaymentMethod;
DROP TABLE IF EXISTS Product;
DROP TABLE IF EXISTS Category;
DROP TABLE IF EXISTS Supplier;
DROP TABLE IF EXISTS Customer;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Warehouse;
DROP TABLE IF EXISTS Branch;

CREATE TABLE Branch (
    BID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    City VARCHAR(100) NOT NULL,
    Phone VARCHAR(20),
    Location VARCHAR(150) NOT NULL
);

CREATE TABLE Warehouse (
    WareID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    WareName VARCHAR(100) NOT NULL,
    BID INT NOT NULL,
    Location VARCHAR(150) NOT NULL,
    Capacity INT,
    FOREIGN KEY (BID) REFERENCES Branch(BID),
    CHECK (Capacity IS NULL OR Capacity >= 0)
);

CREATE TABLE Employee (
    EID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Role VARCHAR(50),
    Phone VARCHAR(20),
    HireDate DATE,
    Salary DECIMAL(10,2),
    BID INT NOT NULL,
    FOREIGN KEY (BID) REFERENCES Branch(BID),
    CHECK (Salary IS NULL OR Salary >= 0)
);

CREATE TABLE Customer (
    CID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Phone VARCHAR(20),
    DOB DATE,
    Gender VARCHAR(10),
    CHECK (Gender IS NULL OR Gender IN ('Male', 'Female'))
);

CREATE TABLE Supplier (
    SuppID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Phone VARCHAR(20) NOT NULL,
    Email VARCHAR(100),
    Address VARCHAR(150)
);

CREATE TABLE Category (
    CatID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Description VARCHAR(255)
);

CREATE TABLE Product (
    ProdID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ProdName VARCHAR(100) NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    Prescription_Requirement BOOLEAN NOT NULL,
    CatID INT NOT NULL,
    FOREIGN KEY (CatID) REFERENCES Category(CatID),
    CHECK (Price >= 0),
    CHECK (Prescription_Requirement IN (0, 1))
);

CREATE TABLE PaymentMethod (
    PayMethodID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    MethodName VARCHAR(50) NOT NULL,
    Description VARCHAR(255),
    CHECK (MethodName IN ('Cash', 'Card'))
);

CREATE TABLE Sale (
    SID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    SaleDate DATE NOT NULL,
    SaleTime TIME NOT NULL,
    TotalAmount DECIMAL(10,2) NOT NULL,
    CID INT NOT NULL,
    BID INT NOT NULL,
    EID INT NOT NULL,
    PayMethodID INT NOT NULL,
    FOREIGN KEY (CID) REFERENCES Customer(CID),
    FOREIGN KEY (BID) REFERENCES Branch(BID),
    FOREIGN KEY (EID) REFERENCES Employee(EID),
    FOREIGN KEY (PayMethodID) REFERENCES PaymentMethod(PayMethodID),
    CHECK (TotalAmount >= 0)
);

CREATE TABLE PurchaseOrder (
    PurchID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    PurchaseDate DATE NOT NULL,
    TotalCost DECIMAL(10,2) NOT NULL,
    SuppID INT NOT NULL,
    BID INT NOT NULL,
    EID INT NOT NULL,
    FOREIGN KEY (SuppID) REFERENCES Supplier(SuppID),
    FOREIGN KEY (BID) REFERENCES Branch(BID),
    FOREIGN KEY (EID) REFERENCES Employee(EID),
    CHECK (TotalCost >= 0)
);

CREATE TABLE Prescription (
    PrescID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    PrescriptionDate DATE NOT NULL,
    Doc_Name VARCHAR(100),
    CID INT NOT NULL,
    BID INT NOT NULL,
    EID INT NOT NULL,
    FOREIGN KEY (CID) REFERENCES Customer(CID),
    FOREIGN KEY (BID) REFERENCES Branch(BID),
    FOREIGN KEY (EID) REFERENCES Employee(EID)
);

CREATE TABLE SaleItem (
    SID INT NOT NULL,
    ProdID INT NOT NULL,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (SID, ProdID),
    FOREIGN KEY (SID) REFERENCES Sale(SID),
    FOREIGN KEY (ProdID) REFERENCES Product(ProdID),
    CHECK (Quantity > 0),
    CHECK (UnitPrice >= 0)
);

CREATE TABLE PurchaseItem (
    PurchID INT NOT NULL,
    ProdID INT NOT NULL,
    Quantity INT NOT NULL,
    UnitCost DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (PurchID, ProdID),
    FOREIGN KEY (PurchID) REFERENCES PurchaseOrder(PurchID),
    FOREIGN KEY (ProdID) REFERENCES Product(ProdID),
    CHECK (Quantity > 0),
    CHECK (UnitCost >= 0)
);

CREATE TABLE PrescriptionItem (
    PrescID INT NOT NULL,
    ProdID INT NOT NULL,
    Dosage VARCHAR(100),
    Duration VARCHAR(100),
    Instructions VARCHAR(255),
    PRIMARY KEY (PrescID, ProdID),
    FOREIGN KEY (PrescID) REFERENCES Prescription(PrescID),
    FOREIGN KEY (ProdID) REFERENCES Product(ProdID)
);

CREATE TABLE Inventory (
    WareID INT NOT NULL,
    ProdID INT NOT NULL,
    QuantityAvailable INT NOT NULL,
    MinStock INT,
    LastUpdated DATE,
    PRIMARY KEY (WareID, ProdID),
    FOREIGN KEY (WareID) REFERENCES Warehouse(WareID),
    FOREIGN KEY (ProdID) REFERENCES Product(ProdID),
    CHECK (QuantityAvailable >= 0),
    CHECK (MinStock IS NULL OR MinStock >= 0)
);

CREATE TABLE Batch (
    WareID INT NOT NULL,
    BNum INT NOT NULL,
    ProdID INT NOT NULL,
    ManufacturingDate DATE,
    ExpDate DATE,
    QuantityReceived INT,
    QuantityRemaining INT,
    PRIMARY KEY (WareID, BNum),
    FOREIGN KEY (WareID) REFERENCES Warehouse(WareID),
    FOREIGN KEY (ProdID) REFERENCES Product(ProdID),
    CHECK (QuantityReceived IS NULL OR QuantityReceived >= 0),
    CHECK (QuantityRemaining IS NULL OR QuantityRemaining >= 0),
    CHECK (ExpDate IS NULL OR ManufacturingDate IS NULL OR ExpDate > ManufacturingDate)
);

CREATE TABLE SupplierProduct (
    SuppID INT NOT NULL,
    ProdID INT NOT NULL,
    SupplierPrice DECIMAL(10,2),
    DelivTime VARCHAR(50),
    Availability VARCHAR(50),
    PRIMARY KEY (SuppID, ProdID),
    FOREIGN KEY (SuppID) REFERENCES Supplier(SuppID),
    FOREIGN KEY (ProdID) REFERENCES Product(ProdID),
    CHECK (SupplierPrice IS NULL OR SupplierPrice >= 0),
    CHECK (Availability IS NULL OR Availability IN ('Available', 'Unavailable', 'Limited'))
);



