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
