-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: pahanaedu
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categoriesaudit`
--

DROP TABLE IF EXISTS `categoriesaudit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoriesaudit` (
  `AuditID` int(11) NOT NULL AUTO_INCREMENT,
  `CategoryID` int(11) NOT NULL,
  `ChangedBy` varchar(255) NOT NULL,
  `ChangeTimestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`AuditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoriesaudit`
--

LOCK TABLES `categoriesaudit` WRITE;
/*!40000 ALTER TABLE `categoriesaudit` DISABLE KEYS */;
/*!40000 ALTER TABLE `categoriesaudit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `categoryID` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`categoryID`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Fiction'),(2,'Science'),(3,'Computer/Technology'),(4,'Psychology'),(5,'Biography'),(6,'Business'),(7,'Children'),(8,'Fantasy'),(9,'History'),(10,'Romance'),(11,'Mystery/Thriller'),(12,'Health & Fitness'),(13,'Travel'),(14,'Cooking'),(15,'Poetry'),(16,'Religion'),(17,'Art'),(18,'Economics'),(19,'Language learning'),(20,'Environment & Nature'),(77,'Classic Literature'),(78,'Young Adult/Fantacy'),(79,'Horror'),(80,'Stationary'),(81,'Notebooks'),(82,'Paper Products');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_category_after_insert
AFTER INSERT ON category
FOR EACH ROW
BEGIN
    INSERT INTO CategoryAudit (CategoryID, Action, NewData, ChangedBy)
    VALUES (NEW.categoryID, 'INSERT', JSON_OBJECT('type', NEW.type), USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_category_after_update
AFTER UPDATE ON category
FOR EACH ROW
BEGIN
    INSERT INTO CategoryAudit (CategoryID, Action, OldData, NewData, ChangedBy)
    VALUES (OLD.categoryID, 'UPDATE', JSON_OBJECT('type', OLD.type), JSON_OBJECT('type', NEW.type), USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_category_after_delete
AFTER DELETE ON category
FOR EACH ROW
BEGIN
    INSERT INTO CategoryAudit (CategoryID, Action, OldData, ChangedBy)
    VALUES (OLD.categoryID, 'DELETE', JSON_OBJECT('type', OLD.type), USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `categoryaudit`
--

DROP TABLE IF EXISTS `categoryaudit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoryaudit` (
  `AuditID` int(11) NOT NULL AUTO_INCREMENT,
  `CategoryID` int(11) NOT NULL,
  `Action` varchar(10) NOT NULL,
  `OldData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`OldData`)),
  `NewData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`NewData`)),
  `ChangedBy` varchar(255) NOT NULL,
  `ChangeTimestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`AuditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoryaudit`
--

LOCK TABLES `categoryaudit` WRITE;
/*!40000 ALTER TABLE `categoryaudit` DISABLE KEYS */;
/*!40000 ALTER TABLE `categoryaudit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `customerID` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(50) NOT NULL,
  `lastName` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `contactNo` varchar(15) DEFAULT NULL,
  `loyaltyPoints` int(11) DEFAULT 0,
  PRIMARY KEY (`customerID`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1005 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (2,'Emma','Smith','emma.smith2@example.com','0722345678',100),(3,'Noah','Williams','noah.williams3@example.com','0733456789',50),(4,'Olivia','Brown','olivia.brown4@example.com','0744567890',200),(5,'Elijah','Jones','elijah.jones5@example.com','0755678901',2000),(6,'Sophia','Garcia','sophia.garcia6@example.com','0766789012',90),(7,'James','Martinez','james.martinez7@example.com','0777890123',60),(8,'Isabella','Rodriguez','isabella.rodriguez8@example.com','0788901234',150),(9,'Benjamin','Hernandez','benjamin.hernandez9@example.com','0799012345',30),(10,'Mia','Lopez','mia.lopez10@example.com','0710123456',110),(11,'Lucas','Gonzalez','lucas.gonzalez11@example.com','0721234567',95),(12,'Charlotte','Wilson','charlotte.wilson12@example.com','0732345678',70),(13,'Henry','Anderson','henry.anderson13@example.com','0743456789',830),(14,'Amelia','Thomas','amelia.thomas14@example.com','0754567890',125),(15,'Alexander','Taylor','alexander.taylor15@example.com','0765678901',741),(16,'Ava','Moore','ava.moore16@example.com','0776789012',60),(17,'William','Jackson','william.jackson17@example.com','0787890123',45),(18,'Evelyn','Martin','evelyn.martin18@example.com','0798901234',100),(19,'Michael','Lee','michael.lee19@example.com','0719012345',55),(20,'Harper','Perez','harper.perez20@example.com','0720123456',140),(21,'Daniel','White','daniel.white21@example.com','0731234567',65),(22,'Ella','Harris','ella.harris22@example.com','0742345678',175),(23,'Matthew','Clark','matthew.clark23@example.com','0753456789',35),(24,'Scarlett','Lewis','scarlett.lewis24@example.com','0764567890',115),(25,'David','Robinson','david.robinson25@example.com','0775678901',90),(26,'Sam','Aradhyaa','Praveenj.djcrock@gmail.com','0712233451',1000),(27,'Thevi','San','san@gmail.com','0872233456',1000),(28,'Sathira','James','sath@gmail.com','0785566321',8),(29,'Karan','Vijekumar','karanv@gmail.com','0796565632',500),(30,'T','Muhandiramge','thevindisanara1999@gmail.com','0713242424',1),(31,'Sanara','Vindy','s@gmail.com','0712233451',50),(32,'Rohan','Thusitha','r@gmail.com','066221345',10600),(33,'Ravi','Madara','ravi@gmail.com','0723344569',1010),(52,'Test','Test','test@gmail,com','0712345432',15),(53,'test1','test1','t1@gmail.com','0786544564',0),(1001,'Monika','Geller','moni.gell@gmail.com','0448796854',0),(1003,'Rose','Geller','rosegeller@gmail.com','044787878',0),(1004,'dsfsfff','sdsrfsfs','sfsfdd@gmail.com','1212121212',0);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_customer_after_insert
AFTER INSERT ON customer
FOR EACH ROW
BEGIN
    INSERT INTO CustomerAudit (CustomerID, Action, NewData, ChangedBy)
    VALUES (NEW.customerID, 'INSERT', JSON_OBJECT('firstName', NEW.firstName, 'lastName', NEW.lastName, 'email', NEW.email, 'contactNo', NEW.contactNo, 'loyaltyPoints', NEW.loyaltyPoints), USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_customer_after_update
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
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_customer_after_delete
AFTER DELETE ON customer
FOR EACH ROW
BEGIN
    INSERT INTO CustomerAudit (CustomerID, Action, OldData, ChangedBy)
    VALUES (OLD.customerID, 'DELETE', JSON_OBJECT('firstName', OLD.firstName, 'lastName', OLD.lastName, 'email', OLD.email, 'contactNo', OLD.contactNo, 'loyaltyPoints', OLD.loyaltyPoints), USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `customeraudit`
--

DROP TABLE IF EXISTS `customeraudit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customeraudit` (
  `AuditID` int(11) NOT NULL AUTO_INCREMENT,
  `CustomerID` int(11) NOT NULL,
  `Action` varchar(10) NOT NULL,
  `OldData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`OldData`)),
  `NewData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`NewData`)),
  `ChangedBy` varchar(255) NOT NULL,
  `ChangeTimestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`AuditID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customeraudit`
--

LOCK TABLES `customeraudit` WRITE;
/*!40000 ALTER TABLE `customeraudit` DISABLE KEYS */;
INSERT INTO `customeraudit` VALUES (1,1003,'INSERT',NULL,'{\"firstName\": \"Rose\", \"lastName\": \"Geller\", \"email\": \"rosegeller@gmail.com\", \"contactNo\": \"044787878\", \"loyaltyPoints\": 0}','root@localhost','2025-08-20 18:12:16'),(2,28,'UPDATE','{\"firstName\": \"Sathira\", \"lastName\": \"James\", \"email\": \"sath@gmail.com\", \"contactNo\": \"0785566321\", \"loyaltyPoints\": 5}','{\"firstName\": \"Sathira\", \"lastName\": \"James\", \"email\": \"sath@gmail.com\", \"contactNo\": \"0785566321\", \"loyaltyPoints\": 8}','root@localhost','2025-08-20 18:13:10'),(3,2,'UPDATE','{\"firstName\": \"Emma\", \"lastName\": \"Smith\", \"email\": \"emma.smith2@example.com\", \"contactNo\": \"0722345678\", \"loyaltyPoints\": 80}','{\"firstName\": \"Emma\", \"lastName\": \"Smith\", \"email\": \"emma.smith2@example.com\", \"contactNo\": \"0722345678\", \"loyaltyPoints\": 100}','root@localhost','2025-08-21 02:41:27'),(4,1004,'INSERT',NULL,'{\"firstName\": \"dsfsfff\", \"lastName\": \"sdsrfsfs\", \"email\": \"sfsfdd@gmail.com\", \"contactNo\": \"1212121212\", \"loyaltyPoints\": 0}','root@localhost','2025-08-21 03:39:16');
/*!40000 ALTER TABLE `customeraudit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customersaudit`
--

DROP TABLE IF EXISTS `customersaudit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customersaudit` (
  `AuditID` int(11) NOT NULL AUTO_INCREMENT,
  `CustomerID` int(11) NOT NULL,
  `ChangedBy` varchar(255) NOT NULL,
  `ChangeTimestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`AuditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customersaudit`
--

LOCK TABLES `customersaudit` WRITE;
/*!40000 ALTER TABLE `customersaudit` DISABLE KEYS */;
/*!40000 ALTER TABLE `customersaudit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice`
--

LOCK TABLES `invoice` WRITE;
/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
INSERT INTO `invoice` VALUES (1,36,'2025-08-19',3500.00,0.00,'Cash'),(2,37,'2025-08-19',250.00,0.00,'Online'),(3,38,'2025-08-19',500.00,0.00,'Cash'),(4,39,'2025-08-19',1600.00,0.00,'Online'),(5,40,'2025-08-19',1600.00,0.00,'Online'),(15,78,'2025-08-20',150.00,0.00,'Cash'),(17,1,'2025-08-20',10.00,0.00,'Credit Card'),(18,83,'2025-08-20',2100.00,0.00,'Online'),(19,84,'2025-08-20',5850.00,650.00,'Online'),(20,85,'2025-08-20',2200.00,0.00,'Online'),(21,86,'2025-08-20',7920.00,880.00,'Online'),(22,88,'2025-08-20',7560.00,840.00,'Cash'),(23,89,'2025-08-20',5000.00,0.00,'Card'),(24,95,'2025-08-20',6075.00,675.00,'Cash'),(25,96,'2025-08-20',7290.00,810.00,'Cash'),(26,106,'2025-08-20',4845.00,855.00,'Cash'),(27,107,'2025-08-21',900.00,0.00,'Cash');
/*!40000 ALTER TABLE `invoice` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_invoice_after_insert
AFTER INSERT ON invoice
FOR EACH ROW
BEGIN
    INSERT INTO InvoiceAudit (InvoiceID, Action, NewData, ChangedBy)
    VALUES (NEW.invoiceID, 'INSERT', JSON_OBJECT('orderID', NEW.orderID, 'invoiceDate', NEW.invoiceDate, 'totalAmount', NEW.totalAmount, 'discount', NEW.discount, 'paymentType', NEW.paymentType), USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_invoice_after_update
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
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_invoice_after_delete
AFTER DELETE ON invoice
FOR EACH ROW
BEGIN
    INSERT INTO InvoiceAudit (InvoiceID, Action, OldData, ChangedBy)
    VALUES (OLD.invoiceID, 'DELETE', JSON_OBJECT('orderID', OLD.orderID, 'invoiceDate', OLD.invoiceDate, 'totalAmount', OLD.totalAmount, 'discount', OLD.discount, 'paymentType', OLD.paymentType), USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `invoiceaudit`
--

DROP TABLE IF EXISTS `invoiceaudit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoiceaudit` (
  `AuditID` int(11) NOT NULL AUTO_INCREMENT,
  `InvoiceID` int(11) NOT NULL,
  `Action` varchar(10) NOT NULL,
  `OldData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`OldData`)),
  `NewData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`NewData`)),
  `ChangedBy` varchar(255) NOT NULL,
  `ChangeTimestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`AuditID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoiceaudit`
--

LOCK TABLES `invoiceaudit` WRITE;
/*!40000 ALTER TABLE `invoiceaudit` DISABLE KEYS */;
INSERT INTO `invoiceaudit` VALUES (1,22,'INSERT',NULL,'{\"orderID\": 88, \"invoiceDate\": \"2025-08-20\", \"totalAmount\": 7560.00, \"discount\": 840.00, \"paymentType\": \"Cash\"}','root@localhost','2025-08-20 18:20:00'),(2,23,'INSERT',NULL,'{\"orderID\": 89, \"invoiceDate\": \"2025-08-20\", \"totalAmount\": 5000.00, \"discount\": 0.00, \"paymentType\": \"Card\"}','root@localhost','2025-08-20 18:21:10'),(3,24,'INSERT',NULL,'{\"orderID\": 95, \"invoiceDate\": \"2025-08-20\", \"totalAmount\": 6075.00, \"discount\": 675.00, \"paymentType\": \"Cash\"}','root@localhost','2025-08-20 18:32:39'),(4,25,'INSERT',NULL,'{\"orderID\": 96, \"invoiceDate\": \"2025-08-20\", \"totalAmount\": 7290.00, \"discount\": 810.00, \"paymentType\": \"Cash\"}','root@localhost','2025-08-20 18:35:44'),(5,26,'INSERT',NULL,'{\"orderID\": 106, \"invoiceDate\": \"2025-08-20\", \"totalAmount\": 4845.00, \"discount\": 855.00, \"paymentType\": \"Cash\"}','root@localhost','2025-08-20 18:45:59'),(6,27,'INSERT',NULL,'{\"orderID\": 107, \"invoiceDate\": \"2025-08-21\", \"totalAmount\": 900.00, \"discount\": 0.00, \"paymentType\": \"Cash\"}','root@localhost','2025-08-21 13:03:57');
/*!40000 ALTER TABLE `invoiceaudit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (128,'The Great Gatsby by F. Scott Fitzgerald','ISBN9780743273565',1250.00,30,19),(129,'A Brief History of Time by Stephen Hawking','ISBN9780553380163',1500.00,22,2),(130,'Clean Code: A Handbook of Agile Software Craftsmanship','ISBN9780132350884',3500.00,9,3),(131,'Thinking, Fast and Slow by Daniel Kahneman','ISBN9780374533557',1100.00,25,4),(132,'The Diary of a Young Girl by Anne Frank','ISBN9780553296983',900.00,20,5),(133,'The Lean Startup by Eric Ries','ISBN9780307887894',1800.00,12,6),(134,'Where the Wild Things Are by Maurice Sendak','ISBN9780064431781',700.00,35,7),(135,'Harry Potter and the Sorcerer\'s Stone by J.K. Rowling','ISBN9780590353427',2200.00,60,8),(136,'Guns, Germs, and Steel by Jared Diamond','ISBN9780393354324',1600.00,13,9),(137,'Pride and Prejudice by Jane Austen','ISBN9780141439518',950.00,35,10),(138,'Gone Girl by Gillian Flynn','ISBN9780307588371',1100.00,22,11),(139,'The Power of Habit by Charles Duhigg','ISBN9780812981605',1300.00,30,12),(140,'Lonely Planet: Italy Travel Guide','ISBN9781786575152',2000.00,18,13),(141,'Salt, Fat, Acid, Heat by Samin Nosrat','ISBN9781476753836',2100.00,20,14),(142,'The Sun and Her Flowers by Rupi Kaur','ISBN9781449474256',900.00,27,15),(143,'The Bible: King James Version','ISBN9781853260091',600.00,40,16),(144,'The Story of Art by E.H. Gombrich','ISBN9780714832470',2500.00,10,17),(145,'Freakonomics by Steven D. Levitt','ISBN9780060731335',1200.00,14,18),(146,'Fluent Forever: How to Learn Any Language Fast','ISBN9780385348113',1400.00,12,19),(147,'Silent Spring by Rachel Carson','ISBN9780618249060',1300.00,20,20),(148,'The Hobbit by J.R.R. Tolkien','ISBN9780547928227',1800.00,50,8),(149,'The Catcher in the Rye by J.D. Salinger','ISBN9780316769488',1000.00,20,1),(150,'Deep Work by Cal Newport','ISBN9781455586691',1200.00,10,4),(151,'Steve Jobs by Walter Isaacson','ISBN9781451648546',1400.00,15,5),(152,'Meditations by Marcus Aurelius','ISBN9780812968255',850.00,25,16),(153,'The Art of War by Sun Tzu','ISBN9781599869773',900.00,40,9),(154,'Programming','ISBN 2211234425544',1800.00,7,3),(156,'dqdddss','wqwqsqsqsss',1111.00,22222,12),(158,'The Great Gatsby by F. Scott Fitzgerald','ISBN9780743273565',1250.00,30,77),(159,'To Kill a Mockingbird by Harper Lee','ISBN9780061120084',1100.00,55,77),(160,'1984 by George Orwell','ISBN9780451524935',950.00,40,77),(161,'Harry Potter and the Sorcerer\'s Stone by J.K. Rowling','ISBN9780439708180',1500.00,75,78),(162,'The Lord of the Rings by J.R.R. Tolkien','ISBN9780547928227',2500.00,20,8),(163,'The Alchemist by Paulo Coelho','ISBN9780061122415',850.00,60,5),(164,'A Brief History of Time by Stephen Hawking','ISBN9780553386760',1800.00,25,2),(165,'Sapiens: A Brief History of Humankind by Yuval Noah Harari','ISBN9780062316097',1700.00,35,9),(166,'Dune by Frank Herbert','ISBN9780441172719',1400.00,45,8),(167,'The Catcher in the Rye by J.D. Salinger','ISBN9780316769488',1050.00,50,77),(168,'The Hobbit by J.R.R. Tolkien','ISBN9780547928227',1350.00,65,78),(169,'Pride and Prejudice by Jane Austen','ISBN9780141439518',900.00,70,77),(170,'The Diary of a Young Girl by Anne Frank','ISBN9780141315188',800.00,80,5),(171,'Fahrenheit 451 by Ray Bradbury','ISBN9781451673319',1150.00,35,1),(172,'Brave New World by Aldous Huxley','ISBN9780060850524',1000.00,45,1),(173,'The Shining by Stephen King','ISBN9780307743657',1600.00,25,79),(174,'It by Stephen King','ISBN9781501175466',2200.00,15,79),(175,'The Da Vinci Code by Dan Brown','ISBN9780307474278',1200.00,50,11),(176,'Angels & Demons by Dan Brown','ISBN9780743493724',1150.00,45,11),(177,'The Girl with the Dragon Tattoo by Stieg Larsson','ISBN9780307949516',1300.00,30,11),(178,'Gone Girl by Gillian Flynn','ISBN9780307588371',1100.00,40,11),(179,'The Hunger Games by Suzanne Collins','ISBN9780439023481',1250.00,65,78),(180,'Divergent by Veronica Roth','ISBN9780062024039',1100.00,55,78),(181,'The Fault in Our Stars by John Green','ISBN9780525478812',900.00,70,78),(182,'Paper Towns by John Green','ISBN9780142414934',850.00,60,78),(183,'The Road by Cormac McCarthy','ISBN9780307387893',1400.00,20,1),(184,'No Country for Old Men by Cormac McCarthy','ISBN9780307387879',1300.00,25,1),(185,'The Kite Runner by Khaled Hosseini','ISBN9781594480003',1000.00,50,1),(186,'A Thousand Splendid Suns by Khaled Hosseini','ISBN9781594489501',1050.00,45,1),(187,'Educated by Tara Westover','ISBN9780399590504',1600.00,30,5),(188,'Becoming by Michelle Obama','ISBN9781524763138',1800.00,40,5),(189,'Where the Crawdads Sing by Delia Owens','ISBN9780735219090',1350.00,55,11),(190,'Little Fires Everywhere by Celeste Ng','ISBN9780735224308',1200.00,45,1),(191,'The Martian by Andy Weir','ISBN9780553418027',1500.00,35,1),(192,'Project Hail Mary by Andy Weir','ISBN9780593135204',1700.00,25,1),(193,'Ready Player One by Ernest Cline','ISBN9780307887443',1100.00,60,1),(194,'Ready Player Two by Ernest Cline','ISBN9781524761332',1200.00,40,1),(195,'Circe by Madeline Miller','ISBN9780316556347',1400.00,30,8),(196,'The Song of Achilles by Madeline Miller','ISBN9780062060624',1300.00,35,8),(197,'Normal People by Sally Rooney','ISBN9781984822185',900.00,50,1),(198,'Conversations with Friends by Sally Rooney','ISBN9780451493606',850.00,45,1),(199,'The Secret History by Donna Tartt','ISBN9780679727767',1100.00,30,11),(200,'The Goldfinch by Donna Tartt','ISBN9780316055437',1500.00,25,1),(201,'The Nightingale by Kristin Hannah','ISBN9780312577240',1350.00,40,1),(202,'The Four Agreements by Don Miguel Ruiz','ISBN9781878424310',700.00,90,4),(203,'Mindset: The New Psychology of Success by Carol S. Dweck','ISBN9780345472322',950.00,50,4),(204,'Thinking, Fast and Slow by Daniel Kahneman','ISBN9780374533557',1600.00,30,4),(205,'The Power of Habit by Charles Duhigg','ISBN9780812981605',1200.00,45,4),(206,'The Silent Patient by Alex Michaelides','ISBN9781250301697',1100.00,50,11),(207,'The Woman in Cabin 10 by Ruth Ware','ISBN9781501132957',1050.00,40,11),(208,'Stationery Set - Blue Pen, Pencil, Eraser','SKU-PEN-BLUE',350.00,150,80),(209,'A4 Lined Notebook - 100 Pages','SKU-NOTE-A4',250.00,199,81),(210,'HB Pencils (Pack of 12)','SKU-PENCIL-HB',400.00,120,80),(211,'Correction Tape','SKU-CORRECT-TAPE',150.00,80,80),(212,'Geometry Box Set','SKU-GEO-BOX',600.00,75,80),(213,'A5 Sketchbook - 50 Pages','SKU-SKETCH-A5',300.00,90,81),(214,'Permanent Markers (Black, Blue, Red)','SKU-MARKER-PERM',500.00,59,80),(215,'Highlighters (Pack of 4 - Assorted)','SKU-HIGHLIGHT',450.00,100,80),(216,'Stapler with Staples','SKU-STAPLER',750.00,50,80),(217,'Paper Clips (Box of 100)','SKU-PAPER-CLIPS',100.00,250,82),(218,'Post-it Notes (Assorted Colors)','SKU-POSTIT-MULTI',200.00,180,82),(219,'File Folder (Pack of 5)','SKU-FILE-FOLDER',350.00,110,82),(220,'Glue Stick','SKU-GLUE-STICK',80.00,200,80),(221,'Ballpoint Pens (Pack of 10)','SKU-PEN-BALL-10',500.00,130,80),(222,'Ring Binder A4','SKU-BINDER-A4',450.00,70,80),(223,'The Odyssey by Homer','ISBN9780140268867',1150.00,45,77),(224,'Moby Dick by Herman Melville','ISBN9780142437247',1000.00,30,77),(225,'War and Peace by Leo Tolstoy','ISBN9780679600115',2800.00,15,77),(226,'Crime and Punishment by Fyodor Dostoevsky','ISBN9780486415871',1200.00,40,77),(227,'Don Quixote by Miguel de Cervantes','ISBN9780060935269',1400.00,35,77),(228,'Frankenstein by Mary Shelley','ISBN9780486282114',900.00,50,79),(229,'Dracula by Bram Stoker','ISBN9780486277912',950.00,60,79),(230,'The Picture of Dorian Gray by Oscar Wilde','ISBN9780486278070',850.00,70,77),(231,'Jane Eyre by Charlotte Brontë','ISBN9780486424422',1050.00,55,77),(232,'Wuthering Heights by Emily Brontë','ISBN9780486407005',980.00,45,77),(233,'Lord of the Flies by William Golding','ISBN9780399529207',850.00,75,77),(234,'The Old Man and the Sea by Ernest Hemingway','ISBN9780684801223',750.00,85,77),(235,'The Grapes of Wrath by John Steinbeck','ISBN9780143039433',1100.00,40,77),(236,'Of Mice and Men by John Steinbeck','ISBN9780140177398',800.00,60,77),(237,'Brave New World by Aldous Huxley','ISBN9780060850524',1000.00,45,1),(238,'Do Androids Dream of Electric Sheep? by Philip K. Dick','ISBN9780345404477',1300.00,30,1),(239,'Slaughterhouse-Five by Kurt Vonnegut','ISBN9780385333849',1150.00,40,1),(240,'The Hitchhiker\'s Guide to the Galaxy by Douglas Adams','ISBN9780345391803',1050.00,50,1),(241,'Neuromancer by William Gibson','ISBN9780441569595',1400.00,25,1),(242,'Ender\'s Game by Orson Scott Card','ISBN9780765342203',1250.00,55,1),(243,'The Stand by Stephen King','ISBN9780307947307',2000.00,18,79),(244,'Misery by Stephen King','ISBN9780671891152',1500.00,28,79),(245,'Pet Sematary by Stephen King','ISBN9781501156649',1400.00,33,79),(246,'The Name of the Wind by Patrick Rothfuss','ISBN9780756404741',1800.00,22,8),(247,'The Way of Kings by Brandon Sanderson','ISBN9780765326357',2500.00,15,8),(248,'Mistborn: The Final Empire by Brandon Sanderson','ISBN9780765311780',1600.00,30,8),(249,'A Game of Thrones by George R.R. Martin','ISBN9780553593716',1900.00,20,8),(250,'The Fellowship of the Ring by J.R.R. Tolkien','ISBN9780547928210',1800.00,25,8),(251,'The Two Towers by J.R.R. Tolkien','ISBN9780547928203',1800.00,25,8),(252,'The Return of the King by J.R.R. Tolkien','ISBN9780547928234',1800.00,25,8),(253,'The Handmaid\'s Tale by Margaret Atwood','ISBN9780385490815',1300.00,45,1),(254,'On the Road by Jack Kerouac','ISBN9780140042597',950.00,50,1),(255,'Walden by Henry David Thoreau','ISBN9780486284958',800.00,60,5),(256,'The 7 Habits of Highly Effective People by Stephen R. Covey','ISBN9780743269513',1500.00,35,6),(257,'Rich Dad Poor Dad by Robert Kiyosaki','ISBN9780446677457',1200.00,50,6),(258,'Atomic Habits by James Clear','ISBN9780735211292',1100.00,65,4),(259,'The Subtle Art of Not Giving a F*ck by Mark Manson','ISBN9780062457714',900.00,80,4),(260,'Think and Grow Rich by Napoleon Hill','ISBN9781593375837',850.00,70,6),(261,'Man\'s Search for Meaning by Viktor E. Frankl','ISBN9780807014295',750.00,90,4),(262,'The Power of Now by Eckhart Tolle','ISBN9781577314806',1000.00,55,4),(263,'The Monk Who Sold His Ferrari by Robin Sharma','ISBN9780007243970',800.00,75,4),(264,'The Lean Startup by Eric Ries','ISBN9780307887894',1300.00,40,6),(265,'Zero to One by Peter Thiel','ISBN9780804139298',1100.00,50,6),(266,'Elon Musk by Ashlee Vance','ISBN9780062301239',1600.00,30,5),(267,'Steve Jobs by Walter Isaacson','ISBN9781451648539',1900.00,25,5),(268,'Leonardo da Vinci by Walter Isaacson','ISBN9781501139161',2100.00,20,5),(269,'A Short History of Nearly Everything by Bill Bryson','ISBN9780767908184',1500.00,35,2),(270,'Astrophysics for People in a Hurry by Neil deGrasse Tyson','ISBN9780393609394',1000.00,45,2),(271,'Cosmos by Carl Sagan','ISBN9780345539434',1200.00,50,2),(272,'The Demon-Haunted World by Carl Sagan','ISBN9780345409465',1150.00,40,2),(273,'The Selfish Gene by Richard Dawkins','ISBN9780198767935',1300.00,30,2),(274,'Brief Answers to the Big Questions by Stephen Hawking','ISBN9781984819772',1400.00,25,2),(275,'Pencil Case (Fabric - Assorted)','SKU-CASE-FAB',250.00,180,80),(276,'Eraser (Pack of 5)','SKU-ERASE-5',120.00,250,80),(277,'Sharpener (Single Hole)','SKU-SHARP-1',90.00,300,80),(278,'Gel Pens (Pack of 5 - Assorted)','SKU-PEN-GEL-5',400.00,140,80),(279,'Graph Paper Notebook A4','SKU-NOTE-GRAPH',280.00,100,81),(280,'A3 Drawing Block','SKU-BLOCK-A3',600.00,60,81),(281,'Colouring Pencils (Pack of 24)','SKU-PENCIL-COLOR',700.00,90,80),(282,'Watercolour Paint Set','SKU-PAINT-WATER',850.00,50,80),(283,'Scissors (Small - Student)','SKU-SCISSORS-S',150.00,119,80),(284,'Craft Glue Bottle','SKU-GLUE-CRAFT',180.00,138,80);
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_item_after_insert
AFTER INSERT ON item
FOR EACH ROW
BEGIN
    INSERT INTO ItemAudit (ItemID, Action, NewData, ChangedBy)
    VALUES (NEW.itemID, 'INSERT', JSON_OBJECT('description', NEW.description, 'identificationCode', NEW.identificationCode, 'unitPrice', NEW.unitPrice, 'quantity', NEW.quantity, 'categoryID', NEW.categoryID), USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_item_after_update
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
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_item_after_delete
AFTER DELETE ON item
FOR EACH ROW
BEGIN
    INSERT INTO ItemAudit (ItemID, Action, OldData, ChangedBy)
    VALUES (OLD.itemID, 'DELETE', JSON_OBJECT('description', OLD.description, 'identificationCode', OLD.identificationCode, 'unitPrice', OLD.unitPrice, 'quantity', OLD.quantity, 'categoryID', OLD.categoryID), USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `itemaudit`
--

DROP TABLE IF EXISTS `itemaudit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itemaudit` (
  `AuditID` int(11) NOT NULL AUTO_INCREMENT,
  `ItemID` int(11) NOT NULL,
  `Action` varchar(10) NOT NULL,
  `OldData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`OldData`)),
  `NewData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`NewData`)),
  `ChangedBy` varchar(255) NOT NULL,
  `ChangeTimestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`AuditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itemaudit`
--

LOCK TABLES `itemaudit` WRITE;
/*!40000 ALTER TABLE `itemaudit` DISABLE KEYS */;
/*!40000 ALTER TABLE `itemaudit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `itemsaudit`
--

DROP TABLE IF EXISTS `itemsaudit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itemsaudit` (
  `AuditID` int(11) NOT NULL AUTO_INCREMENT,
  `ItemID` int(11) NOT NULL,
  `ChangedBy` varchar(255) NOT NULL,
  `ChangeTimestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`AuditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itemsaudit`
--

LOCK TABLES `itemsaudit` WRITE;
/*!40000 ALTER TABLE `itemsaudit` DISABLE KEYS */;
/*!40000 ALTER TABLE `itemsaudit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `orderID` int(11) NOT NULL AUTO_INCREMENT,
  `customerID` int(11) NOT NULL,
  `totalAmount` decimal(10,2) NOT NULL,
  `orderDate` datetime NOT NULL,
  `discount` decimal(10,2) NOT NULL,
  PRIMARY KEY (`orderID`),
  KEY `customerID` (`customerID`),
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`customerID`) REFERENCES `customer` (`customerID`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (1,16,99.00,'0000-00-00 00:00:00',0.00),(2,14,600.00,'2025-08-18 11:48:05',0.00),(5,14,900.00,'2025-08-18 14:39:49',0.00),(6,12,2100.00,'2025-08-18 15:47:01',0.00),(7,12,8560.00,'2025-08-18 15:48:05',0.00),(8,5,7400.00,'2025-08-18 17:17:26',0.00),(9,5,600.00,'2025-08-18 17:56:01',0.00),(10,5,600.00,'2025-08-18 18:26:09',0.00),(14,13,2500.00,'2025-08-18 19:15:24',0.00),(15,33,8850.00,'2025-08-18 19:17:06',0.00),(16,32,2800.00,'2025-08-18 19:37:38',0.00),(17,5,20000.00,'2025-08-19 03:37:14',0.00),(24,15,900.00,'2025-08-19 05:01:18',0.00),(25,13,5400.00,'2025-08-19 05:10:21',0.00),(26,15,2160.00,'2025-08-19 05:13:59',0.00),(36,15,3500.00,'2025-08-19 07:06:51',0.00),(37,33,250.00,'2025-08-19 07:12:04',0.00),(38,31,500.00,'2025-08-19 07:16:08',0.00),(39,32,1600.00,'2025-08-19 07:17:14',0.00),(40,32,1600.00,'2025-08-19 07:22:51',0.00),(51,52,75.00,'2025-08-20 00:22:34',0.00),(78,52,150.00,'2025-08-20 06:40:10',0.00),(83,14,2100.00,'2025-08-20 17:00:47',0.00),(84,14,6500.00,'2025-08-20 17:01:14',0.00),(85,14,2200.00,'2025-08-20 17:37:25',0.00),(86,14,8800.00,'2025-08-20 17:38:03',0.00),(88,12,8400.00,'2025-08-20 18:20:00',0.00),(89,15,5000.00,'2025-08-20 18:21:09',0.00),(95,14,6750.00,'2025-08-20 18:32:39',0.00),(96,29,8100.00,'2025-08-20 18:35:44',0.00),(106,15,5700.00,'2025-08-20 18:45:59',0.00),(107,14,900.00,'2025-08-21 13:03:57',0.00);
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_order_after_insert
AFTER INSERT ON `order`
FOR EACH ROW
BEGIN
    INSERT INTO OrderAudit (OrderID, Action, NewData, ChangedBy)
    VALUES (NEW.orderID, 'INSERT', JSON_OBJECT('customerID', NEW.customerID, 'totalAmount', NEW.totalAmount, 'orderDate', NEW.orderDate), USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_order_after_update
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
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_order_after_delete
AFTER DELETE ON `order`
FOR EACH ROW
BEGIN
    INSERT INTO OrderAudit (OrderID, Action, OldData, ChangedBy)
    VALUES (OLD.orderID, 'DELETE', JSON_OBJECT('customerID', OLD.customerID, 'totalAmount', OLD.totalAmount, 'orderDate', OLD.orderDate), USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=165 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (1,5,142,1),(2,6,141,1),(3,7,141,1),(4,7,142,1),(5,7,188,1),(6,7,284,1),(7,7,280,1),(8,7,269,1),(9,7,279,1),(10,7,271,1),(11,8,144,1),(12,8,139,1),(13,8,133,2),(14,9,143,1),(15,10,143,1),(16,14,144,1),(17,15,142,6),(18,15,137,1),(19,15,162,1),(20,16,142,1),(21,16,139,1),(22,16,143,1),(23,17,144,8),(32,24,142,1),(33,25,143,9),(34,26,284,12),(44,36,130,1),(45,37,209,1),(46,38,214,1),(47,39,136,1),(48,40,136,1),(66,78,283,1),(74,83,141,1),(75,84,141,1),(76,84,135,2),(77,85,135,1),(78,86,135,4),(80,88,141,4),(81,89,144,2),(105,95,144,3),(106,96,137,1),(107,96,131,1),(108,96,129,1),(109,96,128,1),(110,96,141,2),(159,106,142,1),(160,106,138,1),(161,106,131,1),(162,106,135,1),(163,106,134,1),(164,107,142,1);
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_order_item_after_insert
AFTER INSERT ON order_item
FOR EACH ROW
BEGIN
    INSERT INTO OrderItemAudit (OrderItemID, Action, NewData, ChangedBy)
    VALUES (NEW.orderItemID, 'INSERT', JSON_OBJECT('orderID', NEW.orderID, 'itemID', NEW.itemID, 'quantity', NEW.quantity), USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_order_item_after_update
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
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER trg_order_item_after_delete
AFTER DELETE ON order_item
FOR EACH ROW
BEGIN
    INSERT INTO OrderItemAudit (OrderItemID, Action, OldData, ChangedBy)
    VALUES (OLD.orderItemID, 'DELETE', JSON_OBJECT('orderID', OLD.orderID, 'itemID', OLD.itemID, 'quantity', OLD.quantity), USER());
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `orderaudit`
--

DROP TABLE IF EXISTS `orderaudit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderaudit` (
  `AuditID` int(11) NOT NULL AUTO_INCREMENT,
  `OrderID` int(11) NOT NULL,
  `Action` varchar(10) NOT NULL,
  `OldData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`OldData`)),
  `NewData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`NewData`)),
  `ChangedBy` varchar(255) NOT NULL,
  `ChangeTimestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`AuditID`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderaudit`
--

LOCK TABLES `orderaudit` WRITE;
/*!40000 ALTER TABLE `orderaudit` DISABLE KEYS */;
INSERT INTO `orderaudit` VALUES (2,88,'INSERT',NULL,'{\"customerID\": 12, \"totalAmount\": 8400.00, \"orderDate\": \"2025-08-20 18:20:00\"}','root@localhost','2025-08-20 18:20:00'),(3,89,'INSERT',NULL,'{\"customerID\": 15, \"totalAmount\": 5000.00, \"orderDate\": \"2025-08-20 18:21:09\"}','root@localhost','2025-08-20 18:21:10'),(9,95,'INSERT',NULL,'{\"customerID\": 14, \"totalAmount\": 6750.00, \"orderDate\": \"2025-08-20 18:32:39\"}','root@localhost','2025-08-20 18:32:39'),(10,96,'INSERT',NULL,'{\"customerID\": 29, \"totalAmount\": 8100.00, \"orderDate\": \"2025-08-20 18:35:44\"}','root@localhost','2025-08-20 18:35:44'),(20,106,'INSERT',NULL,'{\"customerID\": 15, \"totalAmount\": 5700.00, \"orderDate\": \"2025-08-20 18:45:59\"}','root@localhost','2025-08-20 18:45:59'),(21,107,'INSERT',NULL,'{\"customerID\": 14, \"totalAmount\": 900.00, \"orderDate\": \"2025-08-21 13:03:57\"}','root@localhost','2025-08-21 13:03:57');
/*!40000 ALTER TABLE `orderaudit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderitemaudit`
--

DROP TABLE IF EXISTS `orderitemaudit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderitemaudit` (
  `AuditID` int(11) NOT NULL AUTO_INCREMENT,
  `OrderItemID` int(11) NOT NULL,
  `Action` varchar(10) NOT NULL,
  `OldData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`OldData`)),
  `NewData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`NewData`)),
  `ChangedBy` varchar(255) NOT NULL,
  `ChangeTimestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`AuditID`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderitemaudit`
--

LOCK TABLES `orderitemaudit` WRITE;
/*!40000 ALTER TABLE `orderitemaudit` DISABLE KEYS */;
INSERT INTO `orderitemaudit` VALUES (2,80,'INSERT',NULL,'{\"orderID\": 88, \"itemID\": 141, \"quantity\": 4}','root@localhost','2025-08-20 18:20:00'),(3,81,'INSERT',NULL,'{\"orderID\": 89, \"itemID\": 144, \"quantity\": 2}','root@localhost','2025-08-20 18:21:10'),(27,105,'INSERT',NULL,'{\"orderID\": 95, \"itemID\": 144, \"quantity\": 3}','root@localhost','2025-08-20 18:32:39'),(28,106,'INSERT',NULL,'{\"orderID\": 96, \"itemID\": 137, \"quantity\": 1}','root@localhost','2025-08-20 18:35:44'),(29,107,'INSERT',NULL,'{\"orderID\": 96, \"itemID\": 131, \"quantity\": 1}','root@localhost','2025-08-20 18:35:44'),(30,108,'INSERT',NULL,'{\"orderID\": 96, \"itemID\": 129, \"quantity\": 1}','root@localhost','2025-08-20 18:35:44'),(31,109,'INSERT',NULL,'{\"orderID\": 96, \"itemID\": 128, \"quantity\": 1}','root@localhost','2025-08-20 18:35:44'),(32,110,'INSERT',NULL,'{\"orderID\": 96, \"itemID\": 141, \"quantity\": 2}','root@localhost','2025-08-20 18:35:44'),(81,159,'INSERT',NULL,'{\"orderID\": 106, \"itemID\": 142, \"quantity\": 1}','root@localhost','2025-08-20 18:45:59'),(82,160,'INSERT',NULL,'{\"orderID\": 106, \"itemID\": 138, \"quantity\": 1}','root@localhost','2025-08-20 18:45:59'),(83,161,'INSERT',NULL,'{\"orderID\": 106, \"itemID\": 131, \"quantity\": 1}','root@localhost','2025-08-20 18:45:59'),(84,162,'INSERT',NULL,'{\"orderID\": 106, \"itemID\": 135, \"quantity\": 1}','root@localhost','2025-08-20 18:45:59'),(85,163,'INSERT',NULL,'{\"orderID\": 106, \"itemID\": 134, \"quantity\": 1}','root@localhost','2025-08-20 18:45:59'),(86,164,'INSERT',NULL,'{\"orderID\": 107, \"itemID\": 142, \"quantity\": 1}','root@localhost','2025-08-21 13:03:57');
/*!40000 ALTER TABLE `orderitemaudit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `roleID` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`roleID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'Admin'),(2,'User');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `userID` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(255) NOT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `roleID` int(11) NOT NULL,
  PRIMARY KEY (`userID`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Thevi','SanV','thevindisanara1999@gmail.com',1),(2,'Rohan','Thusitha','r@gmail.com',2),(3,'Ravi','Madara','ravimuhandiramge@gmail.com',2),(4,'Sam ','Pukket','sam@gmail.com',2),(5,'cat','Valentine','cat@gmail.com',2),(7,'wqwqwqw','qwwqqww','qwqwqwqwqwq@gmail.com',2);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userlogin`
--

DROP TABLE IF EXISTS `userlogin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userlogin`
--

LOCK TABLES `userlogin` WRITE;
/*!40000 ALTER TABLE `userlogin` DISABLE KEYS */;
INSERT INTO `userlogin` VALUES (3,1,'Admin','jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=',1),(4,3,'User','BPiZbadjt6lpsQKO4wB1aerzpjVIbdqyEdUSyFud+Ps=',1),(5,2,'ICBT','MD/Aq4VU35WMLvP8nXo3fK+ApTVnqRSsNdKqFrXUUQE=',0),(6,5,'testuser','Gq+lB4wH0rG2E+FjXn7vK6pSjN/jI2qWjY4rN9dG8kY=',1);
/*!40000 ALTER TABLE `userlogin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usersaudit`
--

DROP TABLE IF EXISTS `usersaudit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usersaudit` (
  `AuditID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) NOT NULL,
  `ChangedBy` varchar(255) NOT NULL,
  `ChangeTimestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`AuditID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usersaudit`
--

LOCK TABLES `usersaudit` WRITE;
/*!40000 ALTER TABLE `usersaudit` DISABLE KEYS */;
/*!40000 ALTER TABLE `usersaudit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'pahanaedu'
--
/*!50003 DROP PROCEDURE IF EXISTS `GetMonthlySalesSummary` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
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

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `GetSalesMetrics` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `GetTopCategoriesDistribution` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `GetTopCustomersAndItemSummary` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
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

END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `GetTopCustomersByLoyaltyPoints` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-21 18:37:22
