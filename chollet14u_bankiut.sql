-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : devbdd.iutmetz.univ-lorraine.fr
-- Généré le : mar. 25 nov. 2025 à 16:07
-- Version du serveur : 10.3.39-MariaDB
-- Version de PHP : 8.2.20

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `chollet14u_bankiut`
--

-- --------------------------------------------------------

--
-- Structure de la table `Compte`
--

CREATE TABLE `Compte` (
  `numeroCompte` varchar(50) NOT NULL,
  `userId` varchar(50) NOT NULL,
  `solde` double NOT NULL,
  `avecDecouvert` varchar(5) NOT NULL,
  `decouvertAutorise` decimal(10,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Déchargement des données de la table `Compte`
--

INSERT INTO `Compte` (`numeroCompte`, `userId`, `solde`, `avecDecouvert`, `decouvertAutorise`) VALUES
('AB7328887341', 'client2', -97, 'AVEC', 123),
('BD4242424242', 'client1', 150, 'SANS', NULL),
('FF5050500202', 'client1', 705, 'SANS', NULL),
('IO1010010001', 'client2', 6868, 'SANS', NULL),
('LA1021931215', 'client1', 150, 'SANS', NULL),
('MD8694030938', 'client1', 70, 'SANS', NULL),
('PP1285735733', 'a', 37, 'SANS', NULL),
('TD0398455576', 'client2', 34, 'AVEC', 700),
('XD1829451029', 'client2', -93, 'AVEC', 100),
('XX7788778877', 'client2', 90, 'SANS', NULL),
('XX9999999999', 'client2', 0, 'AVEC', 500);

-- --------------------------------------------------------

--
-- Structure de la table `Utilisateur`
--

CREATE TABLE `Utilisateur` (
  `userId` varchar(50) NOT NULL,
  `nom` varchar(45) NOT NULL,
  `prenom` varchar(45) NOT NULL,
  `adresse` varchar(100) NOT NULL,
  `userPwd` varchar(100) DEFAULT NULL,
  `male` bit(1) NOT NULL,
  `type` varchar(10) NOT NULL,
  `numClient` varchar(45) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `resetToken` varchar(255) DEFAULT NULL,
  `tokenExpiry` DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Déchargement des données de la table `Utilisateur`
--

INSERT INTO `Utilisateur` (`userId`, `nom`, `prenom`, `adresse`, `userPwd`, `male`, `type`, `numClient`, `email`, `resetToken`, `tokenExpiry`) VALUES
('a', 'a', 'a', 'a', '$2a$12$aeCTnGBRXS4AImV6O.teyuaw/Q6BGDOQUsMeEPn7CEa6h1QQZogEi', b'1', 'MANAGER', NULL, NULL, NULL, NULL),
('admin', 'Smith', 'Joe', '123, grande rue, Metz', '$2a$12$Wc5GIYfuNxn3IkuyHwO5tukb7u2Kjj8h1XZw7MOZmOxN505mnmVxG', b'1', 'MANAGER', '', 'thomaschollet.pro@gmail.com', NULL, NULL),
('client1', 'client1', 'Jane', '45, grand boulevard, Brest', '$2a$12$DaWbhOd9qtCHCcsTOJDmM.p6XDUUuRCTP2gajzpWifa.Cb2R/TKOa', b'1', 'CLIENT', '123456789', NULL, NULL, NULL),
('client2', 'client2', 'Jane', '45, grand boulevard, Brest', '$2a$12$jJxgY2pShuuQrr4efRd2leeq9ynV0fuJj94t2G6.ae8zYsFn3hc5m', b'1', 'CLIENT', '123456788', NULL, NULL, NULL),
('t.hash', 'Test', 'Hash', 'Hash, Metz', '$2a$12$6SmJQ6n8ralCAwTgUmXps./AjxUaI.jdv5Ck/OyRiEfNhFbE7nljS', b'1', 'MANAGER', NULL, NULL, NULL, NULL),
('t.manag1', 'mang', 'test', 'Saulcy', '$2a$12$0UxivWTCSge8.GycxC3g3evmKAkx7SKnLUakT3Dmd5BrD19f55E7S', b'1', 'MANAGER', NULL, NULL, NULL, NULL);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `Compte`
--
ALTER TABLE `Compte`
  ADD PRIMARY KEY (`numeroCompte`),
  ADD KEY `index_userClient` (`userId`);

--
-- Index pour la table `Utilisateur`
--
ALTER TABLE `Utilisateur`
  ADD PRIMARY KEY (`userId`),
  ADD UNIQUE KEY `numClient_UNIQUE` (`numClient`);

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `Compte`
--
ALTER TABLE `Compte`
  ADD CONSTRAINT `fk_Compte_userId` FOREIGN KEY (`userId`) REFERENCES `Utilisateur` (`userId`) ON DELETE NO ACTION ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
