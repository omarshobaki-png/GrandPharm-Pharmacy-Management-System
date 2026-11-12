use grandpharm;
select * from Customer;
SELECT * FROM Branch;
SELECT * FROM Warehouse;
SELECT * FROM Employee;
SELECT * FROM Customer;
SELECT * FROM Supplier;
SELECT * FROM Category;
SELECT * FROM Product;
SELECT * FROM Inventory;
SELECT * FROM Sale;
SELECT * FROM PaymentMethod;
SELECT * FROM PurchaseOrder;
SELECT * FROM Prescription;
SELECT * FROM SaleItem;
SELECT * FROM PurchaseItem;
SELECT * FROM PrescriptionItem;
SELECT * FROM Batch;
SELECT * FROM SupplierProduct;


SELECT 
p.ProdID,
p.ProdName,
c.Name AS CategoryName,
p.Price,
p.Prescription_Requirement
FROM Product p
JOIN Category c ON p.CatID = c.CatID
ORDER BY p.ProdID ASC;

SELECT 
e.EID,
e.Name AS EmployeeName,
e.Role,
e.Salary,
b.Name AS BranchName,
b.City
FROM Employee e
JOIN Branch b ON e.BID = b.BID
ORDER BY e.EID ASC;

SELECT 
p.ProdID,
p.ProdName,
c.Name AS CategoryName,
w.WareName,
i.QuantityAvailable,
i.MinStock,
i.LastUpdated
FROM Inventory i
JOIN Product p ON i.ProdID = p.ProdID
JOIN Category c ON p.CatID = c.CatID
JOIN Warehouse w ON i.WareID = w.WareID
ORDER BY p.ProdID ASC;

SELECT 
s.SID,
s.SaleDate,
s.SaleTime,
c.Name AS CustomerName,
b.Name AS BranchName,
e.Name AS EmployeeName,
pm.MethodName,
s.TotalAmount
FROM Sale s
JOIN Customer c ON s.CID = c.CID
JOIN Branch b ON s.BID = b.BID
JOIN Employee e ON s.EID = e.EID
JOIN PaymentMethod pm ON s.PayMethodID = pm.PayMethodID
ORDER BY s.SID ASC;

SELECT 
s.SID,
s.SaleDate,
c.Name AS CustomerName,
p.ProdName,
si.Quantity,
si.UnitPrice,
si.Quantity * si.UnitPrice AS LineTotal
FROM SaleItem si
JOIN Sale s ON si.SID = s.SID
JOIN Customer c ON s.CID = c.CID
JOIN Product p ON si.ProdID = p.ProdID
ORDER BY s.SID ASC;

SELECT 
b.WareID,
w.WareName,
b.BNum,
p.ProdName,
b.ManufacturingDate,
b.ExpDate,
b.QuantityRemaining
FROM Batch b
JOIN Warehouse w ON b.WareID = w.WareID
JOIN Product p ON b.ProdID = p.ProdID
WHERE b.ExpDate <= '2026-12-31'
ORDER BY b.ExpDate ASC;

use grandpharm;
SELECT * FROM Employee;
