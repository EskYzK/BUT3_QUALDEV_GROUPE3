-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : devbdd.iutmetz.univ-lorraine.fr
-- Généré le : dim. 01 fév. 2026 à 15:03
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
-- Base de données : `chollet14u_bankiuttest`
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
('AB7328887341', 'j.doe2', 4242, 'AVEC', 123),
('AV1011011011', 'g.descomptes', 5, 'AVEC', 100),
('BD4242424242', 'j.doe1', 100, 'SANS', NULL),
('CADNV00000', 'j.doe1', 42, 'AVEC', 42),
('CADV000000', 'j.doe1', 0, 'AVEC', 42),
('CSDNV00000', 'j.doe1', 42, 'SANS', NULL),
('CSDV000000', 'j.doe1', 0, 'SANS', NULL),
('IO1010010001', 'j.doe2', 6868, 'SANS', NULL),
('KL4589219196', 'g.descomptesvides', 0, 'AVEC', 150),
('KO7845154956', 'g.descomptesvides', 0, 'SANS', NULL),
('LA1021931215', 'j.doe1', 100, 'SANS', NULL),
('MD8694030938', 'j.doe1', 500, 'SANS', NULL),
('PP1285735733', 'a.lidell1', 37, 'SANS', NULL),
('SA1011011011', 'g.descomptes', 10, 'SANS', 0),
('TD0398455576', 'j.doe1', 23, 'AVEC', 500),
('XD1829451029', 'j.doe1', -48, 'AVEC', 100);

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
  `tokenExpiry` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Déchargement des données de la table `Utilisateur`
--

INSERT INTO `Utilisateur` (`userId`, `nom`, `prenom`, `adresse`, `userPwd`, `male`, `type`, `numClient`, `email`, `resetToken`, `tokenExpiry`) VALUES
('a.lidell1', 'Lidell', 'Alice', '789, grande rue, Metz', '$2a$12$bguUFgfv4h08SRL4625Zu.Yoh2Og4gBjWKxOR1yla86ScUJz.zu8G', b'1', 'CLIENT', '9865432100', NULL, NULL, NULL),
('admin', 'Smith', 'Joe', '123, grande rue, Metz', '$2a$12$oACCM5c.PDfTBZCJcbUGy./ZBuUA5O19quY2dHkCqvPRvq6jQ04Ca', b'1', 'MANAGER', '', 'admin@test.com  ', NULL, NULL),
('c.exist', 'TEST NOM', 'TEST PRENOM', 'TEST ADRESSE', '$2a$12$9NZ4TRZEI.Mr.4qh/IZVE.r3e9EyzCx7.SkQCAlaFfT40GcTEISUa', b'1', 'CLIENT', '0101010101', NULL, NULL, NULL),
('g.descomptes', 'TEST NOM', 'TEST PRENOM', 'TEST ADRESSE', '$2a$12$1Tj6qR0EWvb2Oo0AW7e1POmLTyNjKY0s.tlzVIK1becDjFz1aw8f2', b'1', 'CLIENT', '1000000001', NULL, NULL, NULL),
('g.descomptesvides', 'TEST NOM', 'TEST PRENOM', 'TEST ADRESSE', '$2a$12$XufTmZ/Gcrm.1yVV16WobeVEFLM1GGz5LONZFU9QlTALKQoPoCFIK', b'1', 'CLIENT', '0000000002', NULL, NULL, NULL),
('g.exist', 'TEST NOM', 'TEST PRENOM', 'TEST ADRESSE', '$2a$12$24bXIFPYWQho4gyQ9EqzmuLh3QBUj4a2EsQ867aqa/MikoAkqbM8m', b'1', 'CLIENT', '1010101010', NULL, NULL, NULL),
('g.pasdecompte', 'TEST NOM', 'TEST PRENOM', 'TEST ADRESSE', '$2a$12$0XwdIY4Xa9BRVY2fCSpcLO3yX6dfvuT6ktGtOwWlsA14QMN3lWTdu', b'1', 'CLIENT', '5544554455', NULL, NULL, NULL),
('j.doe1', 'Doe', 'Jane', '456, grand boulevard, Brest', '$2a$12$LtVZ3i2cQdwjJo4avabACegnamMS.H64NlQY34enCM9ywhHF8eXTu', b'1', 'CLIENT', '1234567890', NULL, NULL, NULL),
('j.doe2', 'Doe', 'John', '457, grand boulevard, Perpignan', '$2a$12$K3HjpoqOF.krT8K/am2le.tuGF3hwHwg6WQz0ltMsfrtpDUB1C3ze', b'1', 'CLIENT', '0000000001', NULL, NULL, NULL);


--
-- Création de la table CarteBancaire
--
CREATE TABLE `CarteBancaire` (
  `numeroCarte` varchar(16) NOT NULL, 
  `plafond` double NOT NULL,
  `bloquee` tinyint(1) NOT NULL DEFAULT 0,
  `supprimee` tinyint(1) NOT NULL DEFAULT 0,
  `typeDebit` varchar(20) NOT NULL, -- C'est la colonne Discriminator (IMMEDIAT/DIFFERE)
  `numeroCompte` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Déchargement des données de la table `CarteBancaire`
--

INSERT INTO `CarteBancaire` (`numeroCarte`, `plafond`, `bloquee`, `supprimee`, `typeDebit`, `numeroCompte`) VALUES
('1111222233334444', 1500, 0, 0, 'IMMEDIAT', 'BD4242424242'),
('5555666677778888', 2000, 0, 0, 'DIFFERE', 'CADV000000'),
('9999000011112222', 500, 1, 0, 'IMMEDIAT', 'AB7328887341'),
('1234567890123456', 1000, 0, 1, 'DIFFERE', 'TD0398455576');

--
-- Création de la table Operation
--
CREATE TABLE `Operation` (
  `idOperation` int(11) NOT NULL,
  `libelle` varchar(100) DEFAULT NULL,
  `montant` double NOT NULL,
  `dateOperation` datetime NOT NULL,
  `typeOperation` varchar(20) DEFAULT NULL,
  `numeroCompte` varchar(50) NOT NULL,
  `numeroCarte` varchar(16) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Déchargement des données de la table `Operation`
--

INSERT INTO `Operation` (`idOperation`, `libelle`, `montant`, `dateOperation`, `typeOperation`, `numeroCompte`, `numeroCarte`) VALUES
(1, 'Achat Supermarche', -50.50, '2026-02-15 10:00:00', 'CB', 'BD4242424242', '1111222233334444'),
(2, 'Paiement en ligne', -120.00, '2026-02-18 14:30:00', 'CB', 'CADV000000', '6666777788889999'),
(3, 'Remboursement', 20.00, '2026-02-10 09:15:00', 'VIREMENT', 'BD4242424242', NULL);

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
-- Index pour la table `CarteBancaire`
--
ALTER TABLE `CarteBancaire`
  ADD PRIMARY KEY (`numeroCarte`),
  ADD KEY `fk_CarteBancaire_Compte` (`numeroCompte`);

--
-- Index pour la table `Operation`
--
ALTER TABLE `Operation`
  ADD PRIMARY KEY (`idOperation`),
  ADD KEY `fk_Operation_Compte` (`numeroCompte`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `Operation`
--
ALTER TABLE `Operation`
  MODIFY `idOperation` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `Compte`
--
ALTER TABLE `Compte`
  ADD CONSTRAINT `fk_Compte_userId` FOREIGN KEY (`userId`) REFERENCES `Utilisateur` (`userId`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contrainte pour la table CarteBancaire
--
ALTER TABLE `CarteBancaire`
  ADD CONSTRAINT `fk_CarteBancaire_Compte` FOREIGN KEY (`numeroCompte`) REFERENCES `Compte` (`numeroCompte`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contrainte pour la table Operation
--
ALTER TABLE `Operation`
  ADD CONSTRAINT `fk_Operation_Compte` FOREIGN KEY (`numeroCompte`) REFERENCES `Compte` (`numeroCompte`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_Operation_Carte` FOREIGN KEY (`numeroCarte`) REFERENCES `CarteBancaire` (`numeroCarte`) ON DELETE SET NULL ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
