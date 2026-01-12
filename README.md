# 🏥 Cabinet Médical – Java Application (Local & Network Modes)

## 📌 Objectif
Développer une **application Java desktop** pour la gestion d’un **cabinet médical**
(patients, médecins, rendez-vous, traitements) en utilisant **JDBC** et le **pattern DAO**.

---

## ⚙️ Modes de Fonctionnement
L’application propose **deux modes au démarrage** :

- 💻 **Mode Local**  
  Connexion directe à la base de données via JDBC.

- 🔗 **Mode Réseau**  
  Architecture **Client / Serveur** utilisant les **Sockets Java** pour permettre
  l’accès à une base de données centralisée.

---

## 🧠 Pourquoi deux modes ?
- Le **mode local** est adapté aux tests et à une utilisation sur un seul poste.
- Le **mode réseau** permet à plusieurs clients d’accéder au système de manière sécurisée
  et cohérente.

---

## 🧱 Architecture
- Pattern **DAO (Data Access Object)**
- Connexion JDBC en **Singleton**
- Communication réseau via **Request / Response**
- Interface graphique en **Swing ou JavaFX**

---

## 🛠 Technologies
- Java 
- JDBC
- Java Sockets
- Swing / JavaFX
- MySQL (ou autre SGBD relationnel)

---
