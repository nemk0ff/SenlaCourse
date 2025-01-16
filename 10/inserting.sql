USE homework;

INSERT INTO product (maker, model, type) VALUES
('A', 'MacBook Air', 'Laptop'),
('A', 'MacBook Pro', 'Laptop'),
('A', 'MacBook Pro 16', 'Laptop'),
('B', 'ThinkPad X1 Carbon', 'Laptop'),
('B', 'ThinkPad T14', 'Laptop'),
('B', 'ThinkPad P1', 'Laptop'),
('Xiaomi', 'RedmiBook 14 pro', 'Laptop'),
('Xiaomi', 'RedmiBook 15', 'Laptop'),
('Xiaomi', 'RedmiBook 16 mini', 'Laptop'),

('A', 'OptiPlex 3050', 'PC'),
('A', 'OptiPlex 7050', 'PC'),
('B', 'ProDesk 400 G3', 'PC'),
('B', 'ProDesk 600 G3', 'PC'),
('B', 'ThinkCentre M720q', 'PC'),
('B', 'ThinkCentre M920q', 'PC'),
('Acer', 'Aspire TC-895', 'PC'),
('Acer', 'Aspire TC-895-BK', 'PC'),
('A', 'ROG Strix G531GT', 'PC'),

('Canon', 'PIXMA TS6320', 'Printer'),
('Canon', 'PIXMA TS6340', 'Printer'),
('HP', 'LaserJet Pro M404dn', 'Printer'),
('HP', 'LaserJet Pro M404dw', 'Printer'),
('Epson', 'EcoTank ET-2760', 'Printer'),
('Epson', 'EcoTank ET-2770', 'Printer'),
('B', 'HL-L2350DW', 'Printer'),
('B', 'HL-L2360DW', 'Printer'),
('Xerox', 'Phaser 6510', 'Printer');


INSERT INTO laptop (code, model, speed, ram, hd, price, screen) VALUES
(1, 'MacBook Air', 200, 8, 256, 999.99, 13),
(2, 'MacBook Pro', 800, 16, 512, 1999.99, 16),
(3, 'MacBook Pro 16', 360, 32, 1024, 1999.99, 16),
(4, 'ThinkPad X1 Carbon', 600, 8, 56, 1499.99, 14),
(5, 'ThinkPad T14', 400, 16, 512, 1799.99, 14),
(6, 'ThinkPad P1', 500, 32, 1024, 1499.99, 15),
(7, 'RedmiBook 14 pro', 450, 8, 256, 499.99, 14),
(8, 'RedmiBook 15', 650, 16, 512, 699.99, 15),
(9, 'RedmiBook 16 mini', 700, 32, 1024, 899.99, 16);

INSERT INTO pc (code, model, speed, ram, hd, cd, price) VALUES
(1, 'OptiPlex 3050', 800, 8, 500, '4x', 489.99),
(2, 'OptiPlex 7050', 300, 16, 1000, '8x', 699.99),
(3, 'ProDesk 400 G3', 360, 8, 512, '12x', 499.99),
(4, 'ProDesk 600 G3', 400, 16, 1024, '24x', 99.99),
(5, 'ThinkCentre M720q', 500, 4, 256, '4x', 399.99),
(6, 'ThinkCentre M920q', 650, 8, 512, '12x', 799.99),
(7, 'Aspire TC-895', 360, 8, 512, '8x', 499.99),
(8, 'Aspire TC-895-BK', 400, 16, 1024, '24x', 899.99),
(9, 'ROG Strix G531GT', 500, 16, 1024, '48x', 1999.99);

INSERT INTO printer (code, model, color, type, price) VALUES
(1, 'PIXMA TS6320', 'y', 'Jet', 149.99),
(2, 'PIXMA TS6340', 'n', 'Jet', 249.99),
(3, 'LaserJet Pro M404dn', 'y', 'Laser', 499.99),
(4, 'LaserJet Pro M404dw', 'n', 'Laser', 349.99),
(5, 'EcoTank ET-2760', 'y', 'Matrix', 299.99),
(6, 'EcoTank ET-2770', 'n', 'Matrix', 349.99),
(7, 'HL-L2350DW', 'y', 'Laser', 149.99),
(8, 'HL-L2360DW', 'n', 'Laser', 199.99),
(9, 'Phaser 6510', 'y', 'Matrix', 499.99);