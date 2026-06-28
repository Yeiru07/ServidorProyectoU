CREATE DATABASE  IF NOT EXISTS `batallas_preguntas` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `batallas_preguntas`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: proyecto-u-mysql-vpnaprueba121-b9f7.d.aivencloud.com    Database: batallas_preguntas
-- ------------------------------------------------------
-- Server version	8.4.8

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '12e5c2fd-5cb0-11f1-aa41-2a744fcc0cbc:1-84,
221dd446-7275-11f1-8896-368604c43b42:1-35,
48143eb3-5ec1-11f1-b5bc-c69e3511ace6:1-270,
e10ea4f8-5def-11f1-8b6d-be2960f2fecf:1-17';

--
-- Table structure for table `partidas`
--

DROP TABLE IF EXISTS `partidas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `partidas` (
  `idPartida` int NOT NULL AUTO_INCREMENT,
  `fk_codigoSala` varchar(6) DEFAULT NULL,
  PRIMARY KEY (`idPartida`),
  KEY `fk_codigoSala_sala` (`fk_codigoSala`),
  CONSTRAINT `fk_codigoSala_sala` FOREIGN KEY (`fk_codigoSala`) REFERENCES `sala` (`codigoSala`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `partidas`
--

LOCK TABLES `partidas` WRITE;
/*!40000 ALTER TABLE `partidas` DISABLE KEYS */;
/*!40000 ALTER TABLE `partidas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `preguntas`
--

DROP TABLE IF EXISTS `preguntas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `preguntas` (
  `idpreguntas` int NOT NULL AUTO_INCREMENT,
  `enunciado` varchar(85) NOT NULL,
  `respuesta1` varchar(45) NOT NULL,
  `respuesta2` varchar(45) NOT NULL,
  `respuesta3` varchar(45) NOT NULL,
  `respuesta4` varchar(45) NOT NULL,
  `codigoSala` varchar(6) NOT NULL,
  `respuestaCorrecta` int NOT NULL,
  PRIMARY KEY (`idpreguntas`),
  KEY `fk_preguntas_sala` (`codigoSala`),
  CONSTRAINT `fk_preguntas_sala` FOREIGN KEY (`codigoSala`) REFERENCES `sala` (`codigoSala`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preguntas`
--

LOCK TABLES `preguntas` WRITE;
/*!40000 ALTER TABLE `preguntas` DISABLE KEYS */;
INSERT INTO `preguntas` VALUES (118,'Ejemplo base datos','Excel','Todas','Word','Bloc de notas','463149',2),(119,'Un SABG es una base de datos','Verdadero','Falso','','','463149',2);
/*!40000 ALTER TABLE `preguntas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ranking`
--

DROP TABLE IF EXISTS `ranking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ranking` (
  `idranking` int NOT NULL AUTO_INCREMENT,
  `fk_idUsuarios` int DEFAULT NULL,
  `fk_idPartida` int DEFAULT NULL,
  `posicion` int DEFAULT NULL,
  `puntaje` int DEFAULT NULL,
  PRIMARY KEY (`idranking`),
  KEY `fk_idUsuario_usuario` (`fk_idUsuarios`),
  KEY `fk_idPartida_partida` (`fk_idPartida`),
  CONSTRAINT `fk_idPartida_partida` FOREIGN KEY (`fk_idPartida`) REFERENCES `partidas` (`idPartida`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_idUsuario_usuario` FOREIGN KEY (`fk_idUsuarios`) REFERENCES `usuarios` (`idusuarios`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ranking`
--

LOCK TABLES `ranking` WRITE;
/*!40000 ALTER TABLE `ranking` DISABLE KEYS */;
/*!40000 ALTER TABLE `ranking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sala`
--

DROP TABLE IF EXISTS `sala`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sala` (
  `codigoSala` varchar(6) NOT NULL,
  `nombreSala` varchar(100) NOT NULL,
  `cantidadJugadore` int NOT NULL,
  `estado` tinyint(1) DEFAULT '0',
  `fk_idUsuario` int DEFAULT NULL,
  PRIMARY KEY (`codigoSala`),
  KEY `fk_idUsuaio_usuario` (`fk_idUsuario`),
  CONSTRAINT `fk_idUsuaio_usuario` FOREIGN KEY (`fk_idUsuario`) REFERENCES `usuarios` (`idusuarios`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sala`
--

LOCK TABLES `sala` WRITE;
/*!40000 ALTER TABLE `sala` DISABLE KEYS */;
INSERT INTO `sala` VALUES ('463149','BASEDATOS',1,1,6);
/*!40000 ALTER TABLE `sala` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `idusuarios` int NOT NULL AUTO_INCREMENT,
  `nombreUsuario` varchar(20) NOT NULL,
  `correo` varchar(20) NOT NULL,
  `contraseña` varchar(16) NOT NULL,
  `puntuajeAcumulado` double DEFAULT NULL,
  PRIMARY KEY (`idusuarios`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (6,'21','Solano','21',0),(7,'Rus','rucr','1',0),(8,'Rei','21','21',0),(9,'p','p@pruebasrus','2',0),(10,'alex','alex.com','12',0),(11,'Paul','la','p2',0),(12,'alex','alex.com','12',0),(13,'alex','alex.com','159',0),(14,'alex','alex.com','10',0),(15,'Justin','Solano','12',0),(16,'Rus','ajajs21@gmail','2121',0);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'batallas_preguntas'
--
/*!50003 DROP PROCEDURE IF EXISTS `ObtenerPreguntasDeSala` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'REAL_AS_FLOAT,PIPES_AS_CONCAT,ANSI_QUOTES,IGNORE_SPACE,ONLY_FULL_GROUP_BY,ANSI,STRICT_ALL_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER="avnadmin"@"%" PROCEDURE "ObtenerPreguntasDeSala"(
    IN p_codigoSala INT -- PIN de la sala que ingresa el estudiante desde JavaFX
)
BEGIN
    -- 2. Seleccionamos las columnas haciendo el acoplamiento directo en el WHERE
    SELECT sala.nombreSala, 
           preguntas.enunciado, 
           preguntas.respuesta1, 
           preguntas.respuesta2, 
           preguntas.respuesta3, 
           preguntas.respuesta4
    FROM sala, preguntas
    WHERE sala.codigoSala = preguntas.codigoSala
      AND sala.codigoSala = p_codigoSala;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `ObtenerSalasDeUsuario` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'REAL_AS_FLOAT,PIPES_AS_CONCAT,ANSI_QUOTES,IGNORE_SPACE,ONLY_FULL_GROUP_BY,ANSI,STRICT_ALL_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER="avnadmin"@"%" PROCEDURE "ObtenerSalasDeUsuario"(
    IN p_nombreUsuario VARCHAR(50) 
)
BEGIN
SELECT s.codigoSala, s.nombreSala, s.cantidadJugadore
FROM sala s
INNER JOIN usuarios u ON s.fk_idUsuario = u.idusuarios
WHERE u.nombreUsuario = p_nombreUsuario;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-27 18:19:07
