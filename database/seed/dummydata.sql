USE grandpharm;
INSERT INTO Branch VALUES
(1, 'GrandPharm Al Sharafeh', 'Bireh', '022400111', 'Al Sharafeh - Bireh'),
(2, 'GrandPharm City Center', 'Ramallah', '022400222', 'Ramallah City Center');

INSERT INTO PaymentMethod VALUES
(1, 'Cash', 'Payment using cash'),
(2, 'Card', 'Payment using bank card');

INSERT INTO Warehouse VALUES
(1, 'Al Sharafeh Main Warehouse', 1, 'Al Sharafeh - Bireh', 5000),
(2, 'City Center Storage', 2, 'Ramallah City Center', 3500),
(3, 'Ramallah Backup Warehouse', 2, 'Ramallah Industrial Area', 7000);

INSERT INTO Employee VALUES
(1, 'Omar Khalil', 'Manager', '0599001001', '2023-01-15', 1800.00, 1),
(2, 'Ahmad Saleh', 'Pharmacist', '0599001002', '2023-03-10', 1500.00, 1),
(3, 'Lina Nasser', 'Cashier', '0599001003', '2024-02-20', 900.00, 1),
(4, 'Yousef Darwish', 'Manager', '0599001004', '2022-11-05', 1850.00, 2),
(5, 'Maya Haddad', 'Pharmacist', '0599001005', '2023-06-18', 1550.00, 2),
(6, 'Rami Mansour', 'Cashier', '0599001006', '2024-01-25', 850.00, 2);

INSERT INTO Customer VALUES
(1, 'Khaled Barghouti', '0599111001', '1998-04-12', 'Male'),
(2, 'Sara Abu Eid', '0599111002', '2001-09-21', 'Female'),
(3, 'Mahmoud Zaid', '0599111003', '1985-12-03', 'Male'),
(4, 'Rana Qasem', '0599111004', '1992-07-18', 'Female'),
(5, 'Tareq Hassan', '0599111005', '1979-02-10', 'Male'),
(6, 'Nour Khalil', '0599111006', '2003-05-28', 'Female'),
(7, 'Hani Daraghmeh', '0599111007', '1995-11-14', 'Male'),
(8, 'Dalia Saleh', '0599111008', '1988-08-30', 'Female');

INSERT INTO Supplier VALUES
(1, 'PalMed Supplies', '022410001', 'info@palmed.ps', 'Ramallah'),
(2, 'Al Quds Pharma', '022410002', 'sales@qudspharma.ps', 'Jerusalem'),
(3, 'HealthLine Distributors', '022410003', 'contact@healthline.ps', 'Bireh'),
(4, 'CarePlus Medical', '022410004', 'orders@careplus.ps', 'Nablus'),
(5, 'MedTrust Company', '022410005', 'support@medtrust.ps', 'Hebron');

INSERT INTO Category VALUES
(1, 'General Medicines', 'Common non-prescription medicines'),
(2, 'Prescription Medicines', 'Medicines that require a doctor prescription'),
(3, 'Vitamins', 'Vitamins and supplements'),
(4, 'Skin Care', 'Skin care and dermatology products'),
(5, 'Baby Care', 'Products for babies and children'),
(6, 'Medical Supplies', 'Medical tools and supplies');

INSERT INTO Product VALUES
(1, 'Panadol Extra Tablets', 3.50, 0, 1),
(2, 'Cough Syrup 100ml', 5.00, 0, 1),
(3, 'Amoxicillin 500mg Capsules', 8.00, 1, 2),
(4, 'Blood Pressure Tablets', 12.00, 1, 2),
(5, 'Vitamin C 1000mg', 7.50, 0, 3),
(6, 'Vitamin D3 Drops', 9.00, 0, 3),
(7, 'Moisturizing Cream', 10.00, 0, 4),
(8, 'Sunscreen SPF50', 15.00, 0, 4),
(9, 'Baby Diapers Small Pack', 11.00, 0, 5),
(10, 'Baby Wipes Pack', 4.00, 0, 5),
(11, 'Digital Thermometer', 13.00, 0, 6),
(12, 'Medical Face Masks Box', 6.00, 0, 6),
(13, 'Insulin Pen', 25.00, 1, 2),
(14, 'Allergy Tablets', 6.50, 0, 1),
(15, 'Wound Dressing Pack', 4.50, 0, 6);

INSERT INTO Sale VALUES
(1, '2026-01-10', '10:15:00', 14.50, 1, 1, 2, 1),
(2, '2026-01-12', '12:30:00', 23.00, 2, 1, 3, 2),
(3, '2026-02-03', '09:45:00', 16.00, 3, 2, 5, 1),
(4, '2026-02-15', '14:20:00', 30.00, 4, 2, 6, 2),
(5, '2026-03-01', '11:10:00', 12.50, 5, 1, 2, 1),
(6, '2026-03-12', '16:05:00', 28.00, 6, 2, 5, 2),
(7, '2026-04-04', '13:25:00', 18.00, 7, 1, 3, 1),
(8, '2026-04-18', '17:40:00', 37.00, 8, 2, 6, 2),
(9, '2026-05-02', '10:55:00', 19.50, 1, 1, 2, 1),
(10, '2026-05-09', '15:35:00', 31.00, 2, 2, 5, 2);

INSERT INTO PurchaseOrder VALUES
(1, '2026-01-05', 350.00, 1, 1, 1),
(2, '2026-01-18', 420.00, 2, 2, 4),
(3, '2026-02-08', 280.00, 3, 1, 1),
(4, '2026-02-22', 500.00, 4, 2, 4),
(5, '2026-03-10', 390.00, 5, 1, 1),
(6, '2026-03-25', 610.00, 1, 2, 4),
(7, '2026-04-11', 455.00, 3, 1, 1),
(8, '2026-04-26', 530.00, 2, 2, 4);

INSERT INTO Prescription VALUES
(1, '2026-01-11', 'Dr. Samer Nasser', 1, 1, 2),
(2, '2026-01-20', 'Dr. Leen Ahmad', 3, 2, 5),
(3, '2026-02-14', 'Dr. Rami Khalil', 4, 2, 5),
(4, '2026-03-05', 'Dr. Huda Saleh', 5, 1, 2),
(5, '2026-04-08', 'Dr. Kareem Mansour', 7, 1, 2),
(6, '2026-05-03', 'Dr. Dina Qasem', 8, 2, 5);

INSERT INTO SaleItem VALUES
(1, 1, 2, 3.50),
(1, 10, 1, 4.00),
(1, 15, 1, 3.50),
(2, 5, 2, 7.50),
(2, 2, 1, 5.00),
(2, 1, 1, 3.00),
(3, 3, 2, 8.00),
(4, 8, 2, 15.00),
(5, 14, 1, 6.50),
(5, 12, 1, 6.00),
(6, 11, 1, 13.00),
(6, 8, 1, 15.00),
(7, 6, 2, 9.00),
(8, 13, 1, 25.00),
(8, 12, 2, 6.00),
(9, 7, 1, 10.00),
(9, 15, 1, 4.50),
(9, 1, 1, 5.00),
(10, 9, 2, 11.00),
(10, 14, 1, 6.50),
(10, 10, 1, 2.50);

INSERT INTO PurchaseItem VALUES
(1, 1, 50, 2.00),
(1, 2, 40, 3.00),
(1, 5, 30, 4.30),
(2, 3, 40, 5.00),
(2, 4, 20, 8.00),
(2, 13, 5, 12.00),
(3, 6, 25, 5.50),
(3, 7, 20, 6.00),
(3, 10, 30, 2.00),
(4, 8, 25, 10.00),
(4, 11, 15, 8.00),
(4, 12, 40, 3.00),
(5, 9, 30, 7.00),
(5, 15, 40, 2.50),
(5, 14, 25, 4.00),
(6, 1, 80, 2.00),
(6, 3, 50, 5.00),
(6, 13, 8, 12.50),
(7, 5, 50, 4.00),
(7, 6, 30, 5.50),
(7, 7, 25, 6.00),
(8, 8, 30, 10.00),
(8, 11, 20, 8.00),
(8, 12, 60, 3.00);

INSERT INTO PrescriptionItem VALUES
(1, 3, '500mg', '7 days', 'Take one capsule every 8 hours after meals'),
(1, 14, '10mg', '5 days', 'Take one tablet before sleep'),
(2, 4, '1 tablet', '30 days', 'Take one tablet every morning'),
(3, 13, 'As directed', '30 days', 'Use according to doctor instructions'),
(4, 3, '500mg', '5 days', 'Take one capsule every 12 hours'),
(5, 4, '1 tablet', '14 days', 'Take one tablet daily'),
(6, 13, 'As directed', '60 days', 'Use before meals according to doctor instructions');

INSERT INTO Inventory VALUES
(1, 1, 120, 20, '2026-05-01'),
(1, 2, 70, 15, '2026-05-01'),
(1, 3, 60, 10, '2026-05-01'),
(1, 5, 85, 20, '2026-05-01'),
(1, 7, 45, 10, '2026-05-01'),
(1, 10, 100, 25, '2026-05-01'),
(1, 14, 55, 15, '2026-05-01'),
(1, 15, 90, 20, '2026-05-01'),
(2, 4, 40, 10, '2026-05-01'),
(2, 6, 75, 15, '2026-05-01'),
(2, 8, 50, 10, '2026-05-01'),
(2, 9, 65, 15, '2026-05-01'),
(2, 11, 35, 8, '2026-05-01'),
(2, 12, 150, 30, '2026-05-01'),
(2, 13, 18, 5, '2026-05-01'),
(3, 1, 200, 30, '2026-05-01'),
(3, 3, 100, 15, '2026-05-01'),
(3, 8, 80, 15, '2026-05-01'),
(3, 12, 220, 40, '2026-05-01'),
(3, 15, 110, 20, '2026-05-01');

INSERT INTO Batch VALUES
(1, 1, 1, '2025-09-10', '2027-09-10', 150, 120),
(1, 2, 2, '2025-08-15', '2027-08-15', 90, 70),
(1, 3, 3, '2025-10-01', '2026-10-01', 80, 60),
(1, 4, 5, '2025-06-20', '2027-06-20', 100, 85),
(1, 5, 7, '2025-07-05', '2027-07-05', 60, 45),
(2, 1, 4, '2025-05-12', '2026-08-12', 50, 40),
(2, 2, 6, '2025-11-01', '2027-11-01', 90, 75),
(2, 3, 8, '2025-04-18', '2026-07-18', 70, 50),
(2, 4, 9, '2025-12-09', '2027-12-09', 80, 65),
(2, 5, 13, '2025-03-01', '2026-06-01', 25, 18),
(3, 1, 1, '2025-10-20', '2027-10-20', 250, 200),
(3, 2, 3, '2025-09-01', '2026-09-01', 120, 100),
(3, 3, 8, '2025-08-12', '2027-08-12', 100, 80),
(3, 4, 12, '2025-12-01', '2027-12-01', 250, 220),
(3, 5, 15, '2025-07-30', '2027-07-30', 130, 110);

INSERT INTO SupplierProduct VALUES
(1, 1, 2.00, '3 days', 'Available'),
(1, 2, 3.00, '3 days', 'Available'),
(1, 3, 5.00, '4 days', 'Available'),
(1, 13, 12.50, '5 days', 'Limited'),
(2, 3, 5.20, '4 days', 'Available'),
(2, 4, 8.00, '5 days', 'Available'),
(2, 8, 10.00, '4 days', 'Available'),
(3, 5, 4.00, '2 days', 'Available'),
(3, 6, 5.50, '2 days', 'Available'),
(3, 7, 6.00, '3 days', 'Available'),
(4, 9, 7.00, '3 days', 'Available'),
(4, 10, 2.00, '2 days', 'Available'),
(4, 11, 8.00, '5 days', 'Limited'),
(5, 12, 3.00, '2 days', 'Available'),
(5, 14, 4.00, '3 days', 'Available'),
(5, 15, 2.50, '3 days', 'Available');

SELECT p.ProdID, p.ProdName, c.Name AS CategoryName, p.Price
FROM Product p
JOIN Category c ON p.CatID = c.CatID
Order by p.ProdID ASC;
