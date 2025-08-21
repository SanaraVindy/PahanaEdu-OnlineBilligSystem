CREATE TABLE `category` (
  `categoryID` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`categoryID`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `customer` (
  `customerID` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(50) NOT NULL,
  `lastName` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `contactNo` varchar(15) DEFAULT NULL,
  `loyaltyPoints` int(11) DEFAULT 0,
  PRIMARY KEY (`customerID`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `invoice` (
  `invoiceID` int(11) NOT NULL AUTO_INCREMENT,
  `orderID` int(11) NOT NULL,
  `invoiceDate` date NOT NULL,
  `totalAmount` decimal(10,2) NOT NULL,
  `discount` decimal(5,2) DEFAULT 0.00,
  `paymentType` varchar(45) NOT NULL,
  PRIMARY KEY (`invoiceID`),
  UNIQUE KEY `orderID_UNIQUE` (`orderID`),
  KEY `orderID` (`orderID`),
  CONSTRAINT `invoice_ibfk_1` FOREIGN KEY (`orderID`) REFERENCES `order` (`orderID`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `item` (
  `itemID` int(11) NOT NULL AUTO_INCREMENT,
  `description` text DEFAULT NULL,
  `identificationCode` varchar(50) NOT NULL,
  `unitPrice` decimal(10,2) NOT NULL,
  `quantity` int(11) NOT NULL,
  `categoryID` int(11) NOT NULL,
  PRIMARY KEY (`itemID`),
  KEY `categoryID` (`categoryID`),
  CONSTRAINT `item_ibfk_1` FOREIGN KEY (`categoryID`) REFERENCES `category` (`categoryID`)
) ENGINE=InnoDB AUTO_INCREMENT=889 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `order` (
  `orderID` int(11) NOT NULL AUTO_INCREMENT,
  `customerID` int(11) NOT NULL,
  `totalAmount` decimal(10,2) NOT NULL,
  `orderDate` datetime NOT NULL,
  PRIMARY KEY (`orderID`),
  KEY `customerID` (`customerID`),
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`customerID`) REFERENCES `customer` (`customerID`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `order_item` (
  `orderItemID` int(11) NOT NULL AUTO_INCREMENT,
  `orderID` int(11) NOT NULL,
  `itemID` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  PRIMARY KEY (`orderItemID`),
  KEY `orderID` (`orderID`),
  KEY `itemID` (`itemID`),
  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`orderID`) REFERENCES `order` (`orderID`),
  CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`itemID`) REFERENCES `item` (`itemID`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `role` (
  `roleID` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`roleID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `user` (
  `userID` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(255) NOT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `roleID` int(11) NOT NULL,
  PRIMARY KEY (`userID`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `userlogin` (
  `loginID` int(11) NOT NULL AUTO_INCREMENT,
  `userID` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(44) NOT NULL,
  `passwordChanged` tinyint(1) NOT NULL,
  PRIMARY KEY (`loginID`),
  UNIQUE KEY `username` (`username`),
  KEY `userID` (`userID`),
  CONSTRAINT `userlogin_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `user` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `GetTopCustomersAndItemSummary`(
    IN p_startDate DATE,
    IN p_endDate DATE,
    IN p_customerLimit INT
)
BEGIN
    -- Temporary table to store aggregated order data for each customer.
    -- This helps simplify the main query and improve readability.
    DROP TEMPORARY TABLE IF EXISTS TempCustomerSpending;

    CREATE TEMPORARY TABLE TempCustomerSpending
    SELECT
        c.customerID,
        c.firstName,
        c.lastName,
        c.email,
        c.loyaltyPoints,
        COUNT(DISTINCT o.orderID) AS TotalOrders,
        SUM(o.totalAmount) AS TotalSpent,
        -- Use a subquery to find the most purchased category for each customer.
        -- This subquery is optimized by running per customer.
        (
            SELECT
                ct.type
            FROM
                `order` AS o_inner
            JOIN
                order_item AS oi_inner ON o_inner.orderID = oi_inner.orderID
            JOIN
                item AS i_inner ON oi_inner.itemID = i_inner.itemID
            JOIN
                category AS ct ON i_inner.categoryID = ct.categoryID
            WHERE
                o_inner.customerID = c.customerID
                AND o_inner.orderDate BETWEEN p_startDate AND p_endDate
            GROUP BY
                ct.type
            ORDER BY
                SUM(oi_inner.quantity) DESC
            LIMIT 1
        ) AS TopCategory
    FROM
        customer AS c
    JOIN
        `order` AS o ON c.customerID = o.customerID
    WHERE
        o.orderDate BETWEEN p_startDate AND p_endDate
    GROUP BY
        c.customerID
    ORDER BY
        TotalSpent DESC
    LIMIT p_customerLimit;

    -- Final selection to present the report.
    -- We select from the temporary table and add a rank for better visualization.
    SELECT
        RANK() OVER (ORDER BY TotalSpent DESC) AS Rank,
        firstName,
        lastName,
        email,
        TotalOrders,
        TotalSpent,
        loyaltyPoints,
        TopCategory
    FROM
        TempCustomerSpending;

    -- Clean up the temporary table.
    DROP TEMPORARY TABLE TempCustomerSpending;

END$$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `GetMonthlySalesSummary`(
    IN p_year VARCHAR(4)
)
BEGIN
    -- Step 1: Create a temporary table containing all 12 months.
    -- This is crucial for ensuring months with zero sales are included in the report.
    DROP TEMPORARY TABLE IF EXISTS TempMonths;

    CREATE TEMPORARY TABLE TempMonths (
        MonthNumber INT,
        MonthName VARCHAR(20)
    );

    -- Insert the 12 months with their corresponding names and numbers.
    INSERT INTO TempMonths (MonthNumber, MonthName) VALUES
    (1, 'January'), (2, 'February'), (3, 'March'),
    (4, 'April'), (5, 'May'), (6, 'June'),
    (7, 'July'), (8, 'August'), (9, 'September'),
    (10, 'October'), (11, 'November'), (12, 'December');

    -- Step 2: Select the data, joining the months table with sales data.
    SELECT
        tm.MonthName AS Month,
        -- Use IFNULL to show 0 for months with no sales.
        IFNULL(SUM(o.totalAmount), 0) AS TotalRevenue,
        IFNULL(COUNT(o.orderID), 0) AS TransactionCount
    FROM
        TempMonths AS tm
    -- Use a LEFT JOIN to keep all months from TempMonths, even if there's no matching order.
    LEFT JOIN
        `order` AS o ON tm.MonthNumber = MONTH(o.orderDate)
    WHERE
        -- Filter by the requested year. The WHERE clause must be on the joined table.
        YEAR(o.orderDate) = p_year OR o.orderID IS NULL
    GROUP BY
        tm.MonthNumber, tm.MonthName
    ORDER BY
        tm.MonthNumber;

    -- Step 3: Clean up the temporary table.
    DROP TEMPORARY TABLE TempMonths;

END$$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `GetSalesMetrics`(
    IN startDate DATE,
    IN endDate DATE
)
BEGIN
    SELECT
        SUM(TotalAmount) AS TotalSales,
        COUNT(OrderID) AS TotalOrders,
        AVG(TotalAmount) AS AverageOrderValue
    FROM
        Orders
    WHERE
        OrderDate >= startDate AND OrderDate <= endDate;
END$$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `GetTopCategoriesDistribution`(
    IN startDate DATE,
    IN endDate DATE,
    IN categoryLimit INT
)
BEGIN
    SELECT
        c.CategoryName,
        COUNT(DISTINCT oi.OrderID) AS TotalOrders
    FROM
        Categories c
    JOIN
        Products p ON c.CategoryID = p.CategoryID
    JOIN
        OrderItems oi ON p.ProductID = oi.ProductID
    JOIN
        Orders o ON oi.OrderID = o.OrderID
    WHERE
        o.OrderDate >= startDate AND o.OrderDate <= endDate
    GROUP BY
        c.CategoryName
    ORDER BY
        TotalOrders DESC
    LIMIT categoryLimit;
END$$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `GetTopCustomersAndItemSummary`(
    IN p_startDate DATE,
    IN p_endDate DATE,
    IN p_customerLimit INT
)
BEGIN
    -- Temporary table to store aggregated order data for each customer.
    -- This helps simplify the main query and improve readability.
    DROP TEMPORARY TABLE IF EXISTS TempCustomerSpending;

    CREATE TEMPORARY TABLE TempCustomerSpending
    SELECT
        c.customerID,
        c.firstName,
        c.lastName,
        c.email,
        c.loyaltyPoints,
        COUNT(DISTINCT o.orderID) AS TotalOrders,
        SUM(o.totalAmount) AS TotalSpent,
        -- Use a subquery to find the most purchased category for each customer.
        -- This subquery is optimized by running per customer.
        (
            SELECT
                ct.type
            FROM
                `order` AS o_inner
            JOIN
                order_item AS oi_inner ON o_inner.orderID = oi_inner.orderID
            JOIN
                item AS i_inner ON oi_inner.itemID = i_inner.itemID
            JOIN
                category AS ct ON i_inner.categoryID = ct.categoryID
            WHERE
                o_inner.customerID = c.customerID
                AND o_inner.orderDate BETWEEN p_startDate AND p_endDate
            GROUP BY
                ct.type
            ORDER BY
                SUM(oi_inner.quantity) DESC
            LIMIT 1
        ) AS TopCategory
    FROM
        customer AS c
    JOIN
        `order` AS o ON c.customerID = o.customerID
    WHERE
        o.orderDate BETWEEN p_startDate AND p_endDate
    GROUP BY
        c.customerID
    ORDER BY
        TotalSpent DESC
    LIMIT p_customerLimit;

    -- Final selection to present the report.
    -- We select from the temporary table and add a rank for better visualization.
    SELECT
        RANK() OVER (ORDER BY TotalSpent DESC) AS Rank,
        firstName,
        lastName,
        email,
        TotalOrders,
        TotalSpent,
        loyaltyPoints,
        TopCategory
    FROM
        TempCustomerSpending;

    -- Clean up the temporary table.
    DROP TEMPORARY TABLE TempCustomerSpending;

END$$
DELIMITER ;

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `GetTopCustomersByLoyaltyPoints`(
    IN customerLimit INT
)
BEGIN
    SELECT
        CustomerID,
        FirstName,
        LastName,
        Email,
        LoyaltyPoints
    FROM
        Customers
    ORDER BY
        LoyaltyPoints DESC
    LIMIT customerLimit;
END$$
DELIMITER ;


-- Table to log changes on the `item` table.
CREATE TABLE ItemAudit (
    AuditID INT AUTO_INCREMENT PRIMARY KEY,
    ItemID INT NOT NULL,
    Action VARCHAR(10) NOT NULL, -- 'INSERT', 'UPDATE', 'DELETE'
    OldData JSON,               -- Stores the old row data for UPDATE and DELETE
    NewData JSON,               -- Stores the new row data for INSERT and UPDATE
    ChangedBy VARCHAR(255) NOT NULL,
    ChangeTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table to log changes on the `customer` table.
CREATE TABLE CustomerAudit (
    AuditID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT NOT NULL,
    Action VARCHAR(10) NOT NULL,
    OldData JSON,
    NewData JSON,
    ChangedBy VARCHAR(255) NOT NULL,
    ChangeTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table to log changes on the `category` table.
CREATE TABLE CategoryAudit (
    AuditID INT AUTO_INCREMENT PRIMARY KEY,
    CategoryID INT NOT NULL,
    Action VARCHAR(10) NOT NULL,
    OldData JSON,
    NewData JSON,
    ChangedBy VARCHAR(255) NOT NULL,
    ChangeTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table to log changes on the `invoice` table.
CREATE TABLE InvoiceAudit (
    AuditID INT AUTO_INCREMENT PRIMARY KEY,
    InvoiceID INT NOT NULL,
    Action VARCHAR(10) NOT NULL,
    OldData JSON,
    NewData JSON,
    ChangedBy VARCHAR(255) NOT NULL,
    ChangeTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table to log changes on the `order` table.
CREATE TABLE OrderAudit (
    AuditID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT NOT NULL,
    Action VARCHAR(10) NOT NULL,
    OldData JSON,
    NewData JSON,
    ChangedBy VARCHAR(255) NOT NULL,
    ChangeTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table to log changes on the `order_item` table.
CREATE TABLE OrderItemAudit (
    AuditID INT AUTO_INCREMENT PRIMARY KEY,
    OrderItemID INT NOT NULL,
    Action VARCHAR(10) NOT NULL,
    OldData JSON,
    NewData JSON,
    ChangedBy VARCHAR(255) NOT NULL,
    ChangeTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--
-- Triggers
--

-- Trigger for 'item' table - AFTER INSERT
DELIMITER $$
CREATE TRIGGER trg_item_after_insert
AFTER INSERT ON item
FOR EACH ROW
BEGIN
    INSERT INTO ItemAudit (ItemID, Action, NewData, ChangedBy)
    VALUES (NEW.itemID, 'INSERT', JSON_OBJECT('description', NEW.description, 'identificationCode', NEW.identificationCode, 'unitPrice', NEW.unitPrice, 'quantity', NEW.quantity, 'categoryID', NEW.categoryID), USER());
END$$
DELIMITER ;

-- Trigger for 'item' table - AFTER UPDATE
DELIMITER $$
CREATE TRIGGER trg_item_after_update
AFTER UPDATE ON item
FOR EACH ROW
BEGIN
    INSERT INTO ItemAudit (ItemID, Action, OldData, NewData, ChangedBy)
    VALUES (
        OLD.itemID,
        'UPDATE',
        JSON_OBJECT('description', OLD.description, 'identificationCode', OLD.identificationCode, 'unitPrice', OLD.unitPrice, 'quantity', OLD.quantity, 'categoryID', OLD.categoryID),
        JSON_OBJECT('description', NEW.description, 'identificationCode', NEW.identificationCode, 'unitPrice', NEW.unitPrice, 'quantity', NEW.quantity, 'categoryID', NEW.categoryID),
        USER()
    );
END$$
DELIMITER ;

-- Trigger for 'item' table - AFTER DELETE
DELIMITER $$
CREATE TRIGGER trg_item_after_delete
AFTER DELETE ON item
FOR EACH ROW
BEGIN
    INSERT INTO ItemAudit (ItemID, Action, OldData, ChangedBy)
    VALUES (OLD.itemID, 'DELETE', JSON_OBJECT('description', OLD.description, 'identificationCode', OLD.identificationCode, 'unitPrice', OLD.unitPrice, 'quantity', OLD.quantity, 'categoryID', OLD.categoryID), USER());
END$$
DELIMITER ;

-- Trigger for 'customer' table - AFTER INSERT
DELIMITER $$
CREATE TRIGGER trg_customer_after_insert
AFTER INSERT ON customer
FOR EACH ROW
BEGIN
    INSERT INTO CustomerAudit (CustomerID, Action, NewData, ChangedBy)
    VALUES (NEW.customerID, 'INSERT', JSON_OBJECT('firstName', NEW.firstName, 'lastName', NEW.lastName, 'email', NEW.email, 'contactNo', NEW.contactNo, 'loyaltyPoints', NEW.loyaltyPoints), USER());
END$$
DELIMITER ;

-- Trigger for 'customer' table - AFTER UPDATE
DELIMITER $$
CREATE TRIGGER trg_customer_after_update
AFTER UPDATE ON customer
FOR EACH ROW
BEGIN
    INSERT INTO CustomerAudit (CustomerID, Action, OldData, NewData, ChangedBy)
    VALUES (
        OLD.customerID,
        'UPDATE',
        JSON_OBJECT('firstName', OLD.firstName, 'lastName', OLD.lastName, 'email', OLD.email, 'contactNo', OLD.contactNo, 'loyaltyPoints', OLD.loyaltyPoints),
        JSON_OBJECT('firstName', NEW.firstName, 'lastName', NEW.lastName, 'email', NEW.email, 'contactNo', NEW.contactNo, 'loyaltyPoints', NEW.loyaltyPoints),
        USER()
    );
END$$
DELIMITER ;

-- Trigger for 'customer' table - AFTER DELETE
DELIMITER $$
CREATE TRIGGER trg_customer_after_delete
AFTER DELETE ON customer
FOR EACH ROW
BEGIN
    INSERT INTO CustomerAudit (CustomerID, Action, OldData, ChangedBy)
    VALUES (OLD.customerID, 'DELETE', JSON_OBJECT('firstName', OLD.firstName, 'lastName', OLD.lastName, 'email', OLD.email, 'contactNo', OLD.contactNo, 'loyaltyPoints', OLD.loyaltyPoints), USER());
END$$
DELIMITER ;

-- Trigger for 'category' table - AFTER INSERT
DELIMITER $$
CREATE TRIGGER trg_category_after_insert
AFTER INSERT ON category
FOR EACH ROW
BEGIN
    INSERT INTO CategoryAudit (CategoryID, Action, NewData, ChangedBy)
    VALUES (NEW.categoryID, 'INSERT', JSON_OBJECT('type', NEW.type), USER());
END$$
DELIMITER ;

-- Trigger for 'category' table - AFTER UPDATE
DELIMITER $$
CREATE TRIGGER trg_category_after_update
AFTER UPDATE ON category
FOR EACH ROW
BEGIN
    INSERT INTO CategoryAudit (CategoryID, Action, OldData, NewData, ChangedBy)
    VALUES (OLD.categoryID, 'UPDATE', JSON_OBJECT('type', OLD.type), JSON_OBJECT('type', NEW.type), USER());
END$$
DELIMITER ;

-- Trigger for 'category' table - AFTER DELETE
DELIMITER $$
CREATE TRIGGER trg_category_after_delete
AFTER DELETE ON category
FOR EACH ROW
BEGIN
    INSERT INTO CategoryAudit (CategoryID, Action, OldData, ChangedBy)
    VALUES (OLD.categoryID, 'DELETE', JSON_OBJECT('type', OLD.type), USER());
END$$
DELIMITER ;

-- Trigger for 'invoice' table - AFTER INSERT
DELIMITER $$
CREATE TRIGGER trg_invoice_after_insert
AFTER INSERT ON invoice
FOR EACH ROW
BEGIN
    INSERT INTO InvoiceAudit (InvoiceID, Action, NewData, ChangedBy)
    VALUES (NEW.invoiceID, 'INSERT', JSON_OBJECT('orderID', NEW.orderID, 'invoiceDate', NEW.invoiceDate, 'totalAmount', NEW.totalAmount, 'discount', NEW.discount, 'paymentType', NEW.paymentType), USER());
END$$
DELIMITER ;

-- Trigger for 'invoice' table - AFTER UPDATE
DELIMITER $$
CREATE TRIGGER trg_invoice_after_update
AFTER UPDATE ON invoice
FOR EACH ROW
BEGIN
    INSERT INTO InvoiceAudit (InvoiceID, Action, OldData, NewData, ChangedBy)
    VALUES (
        OLD.invoiceID,
        'UPDATE',
        JSON_OBJECT('orderID', OLD.orderID, 'invoiceDate', OLD.invoiceDate, 'totalAmount', OLD.totalAmount, 'discount', OLD.discount, 'paymentType', OLD.paymentType),
        JSON_OBJECT('orderID', NEW.orderID, 'invoiceDate', NEW.invoiceDate, 'totalAmount', NEW.totalAmount, 'discount', NEW.discount, 'paymentType', NEW.paymentType),
        USER()
    );
END$$
DELIMITER ;

-- Trigger for 'invoice' table - AFTER DELETE
DELIMITER $$
CREATE TRIGGER trg_invoice_after_delete
AFTER DELETE ON invoice
FOR EACH ROW
BEGIN
    INSERT INTO InvoiceAudit (InvoiceID, Action, OldData, ChangedBy)
    VALUES (OLD.invoiceID, 'DELETE', JSON_OBJECT('orderID', OLD.orderID, 'invoiceDate', OLD.invoiceDate, 'totalAmount', OLD.totalAmount, 'discount', OLD.discount, 'paymentType', OLD.paymentType), USER());
END$$
DELIMITER ;

-- Trigger for 'order' table - AFTER INSERT
DELIMITER $$
CREATE TRIGGER trg_order_after_insert
AFTER INSERT ON `order`
FOR EACH ROW
BEGIN
    INSERT INTO OrderAudit (OrderID, Action, NewData, ChangedBy)
    VALUES (NEW.orderID, 'INSERT', JSON_OBJECT('customerID', NEW.customerID, 'totalAmount', NEW.totalAmount, 'orderDate', NEW.orderDate), USER());
END$$
DELIMITER ;

-- Trigger for 'order' table - AFTER UPDATE
DELIMITER $$
CREATE TRIGGER trg_order_after_update
AFTER UPDATE ON `order`
FOR EACH ROW
BEGIN
    INSERT INTO OrderAudit (OrderID, Action, OldData, NewData, ChangedBy)
    VALUES (
        OLD.orderID,
        'UPDATE',
        JSON_OBJECT('customerID', OLD.customerID, 'totalAmount', OLD.totalAmount, 'orderDate', OLD.orderDate),
        JSON_OBJECT('customerID', NEW.customerID, 'totalAmount', NEW.totalAmount, 'orderDate', NEW.orderDate),
        USER()
    );
END$$
DELIMITER ;

-- Trigger for 'order' table - AFTER DELETE
DELIMITER $$
CREATE TRIGGER trg_order_after_delete
AFTER DELETE ON `order`
FOR EACH ROW
BEGIN
    INSERT INTO OrderAudit (OrderID, Action, OldData, ChangedBy)
    VALUES (OLD.orderID, 'DELETE', JSON_OBJECT('customerID', OLD.customerID, 'totalAmount', OLD.totalAmount, 'orderDate', OLD.orderDate), USER());
END$$
DELIMITER ;

-- Trigger for 'order_item' table - AFTER INSERT
DELIMITER $$
CREATE TRIGGER trg_order_item_after_insert
AFTER INSERT ON order_item
FOR EACH ROW
BEGIN
    INSERT INTO OrderItemAudit (OrderItemID, Action, NewData, ChangedBy)
    VALUES (NEW.orderItemID, 'INSERT', JSON_OBJECT('orderID', NEW.orderID, 'itemID', NEW.itemID, 'quantity', NEW.quantity), USER());
END$$
DELIMITER ;

-- Trigger for 'order_item' table - AFTER UPDATE
DELIMITER $$
CREATE TRIGGER trg_order_item_after_update
AFTER UPDATE ON order_item
FOR EACH ROW
BEGIN
    INSERT INTO OrderItemAudit (OrderItemID, Action, OldData, NewData, ChangedBy)
    VALUES (
        OLD.orderItemID,
        'UPDATE',
        JSON_OBJECT('orderID', OLD.orderID, 'itemID', OLD.itemID, 'quantity', OLD.quantity),
        JSON_OBJECT('orderID', NEW.orderID, 'itemID', NEW.itemID, 'quantity', NEW.quantity),
        USER()
    );
END$$
DELIMITER ;

-- Trigger for 'order_item' table - AFTER DELETE
DELIMITER $$
CREATE TRIGGER trg_order_item_after_delete
AFTER DELETE ON order_item
FOR EACH ROW
BEGIN
    INSERT INTO OrderItemAudit (OrderItemID, Action, OldData, ChangedBy)
    VALUES (OLD.orderItemID, 'DELETE', JSON_OBJECT('orderID', OLD.orderID, 'itemID', OLD.itemID, 'quantity', OLD.quantity), USER());
END$$
DELIMITER ;




