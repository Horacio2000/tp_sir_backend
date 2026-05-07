# ConcertTickets — API Backend

**Projet SIR (Systèmes d'Information Répartis)** — Master 1 MIAGE, Semestre 2

**Binôme :** Abel-Horacio SOSSOU & Stephane BANKOLE

API REST pour une plateforme de billetterie de concerts, développée avec Jakarta EE, JPA/Hibernate et Jersey.

---

## Stack technique

| Technologie         | Version | Rôle                             |
|---------------------|---------|----------------------------------|
| Java                | 17+     | Langage principal                |
| Jakarta EE / Jersey | 3.x     | API REST (JAX-RS)                |
| Hibernate / JPA     | 6.4     | ORM (mapping objet-relationnel)  |
| HSQLDB              | 2.7     | Base de données relationnelle    |
| Jetty               | 11.x    | Serveur web embarqué             |
| C3P0                | 0.9.5   | Pool de connexions JDBC          |
| Swagger / OpenAPI   | 3.x     | Documentation API auto-générée   |
| Maven               | 3.8+    | Build et gestion des dépendances |

---

## Prérequis

- **Java 17+** — [Télécharger](https://adoptium.net/)
- **Maven 3.8+** — [Télécharger](https://maven.apache.org/)

---

## Lancement

### 1. Démarrer HSQLDB (base de données)

HSQLDB doit être lancé **en premier**, avant le backend.

```bash
# Depuis la racine du projet
java -cp ~/.m2/repository/org/hsqldb/hsqldb/2.7.2/hsqldb-2.7.2.jar \
  org.hsqldb.server.Server \
  --database.0 file:./data/concertdb \
  --dbname.0 .
```

> Les tables sont créées automatiquement au premier démarrage (`hibernate.hbm2ddl.auto=create`).

### 2. Démarrer le backend

Ouvrir un **nouveau terminal** :

```bash
mvn clean compile jetty:run
```

### URLs

| Ressource           | URL                                      |
|---------------------|------------------------------------------|
| API REST            | `http://localhost:8080/api/*`            |
| Swagger UI          | `http://localhost:8080/swagger-ui.html`  |
| Spec OpenAPI (JSON) | `http://localhost:8080/api/openapi.json` |

---

## Données de test & Tests JPA/DAO

Deux classes de test sont disponibles dans `src/main/java/` pour peupler la base et valider le code.

### `jpa/JpaTest.java` — Données de test + relations + JPQL

Lance directement depuis l'IDE (Run as Java Application) ou via Maven :

```bash
mvn exec:java -Dexec.mainClass="jpa.JpaTest"
```

**Ce que fait ce script :**
- Insère en base (seulement si la base est vide) :
  - 4 catégories : Rock, Pop, Jazz, Electro
  - 3 salles : Zénith Paris, Olympia, AccorHotels Arena
  - 3 clients, 2 organisateurs, 1 administrateur
  - 3 événements publiés avec catégories (ManyToMany)
  - 8 tickets (SIMPLE / PREMIUM / VIP / VVIP)
  - 3 commandes confirmées avec paiement
- Vérifie les relations bidirectionnelles (`Client ↔ Order`, `Organizer ↔ Event`, `Event ↔ Category`)
- Exécute des requêtes JPQL : statistiques, revenus, tickets par type, top clients

> Le script est **idempotent** : si des événements existent déjà, l'insertion est ignorée.

### `test/DaoTest.java` — Tests de toutes les DAOs

À exécuter **après** `JpaTest` (nécessite des données en base) :

```bash
mvn exec:java -Dexec.mainClass="test.DaoTest"
```

**Valide pour chaque DAO :**

| DAO | JPQL | Named Query | Criteria Query | Méthodes métier |
|-----|------|-------------|----------------|-----------------|
| `ClientDaoImpl` | `findByEmail` | `findTopClients` | `findByCriteria` | `getTotalSpent` |
| `EventDaoImpl` | `findByStatus` | `findUpcomingEvents` | `findByCriteria` | `countTicketsSold`, `calculateRevenue` |
| `OrganizerDaoImpl` | `findByEmail` | — | — | `getEventCount`, `getTotalRevenue` |
| `VenueDaoImpl` | `findByCity` | `findByMinCapacityNamed` | `findByCriteria` | — |
| `CategoryDaoImpl` | `findByName` | `findPopularCategories` | — | `getEventCount` |
| `OrderDaoImpl` | `findByStatus` | `findRecentOrders` | — | `getTotalRevenue` |
| `TicketDaoImpl` | `findByStatus` | — | `findByCriteria` | `countSoldTicketsByEvent` |

---

## Compte Administrateur

Le compte admin est **créé automatiquement au démarrage** par `DataInitializer.java` (`@WebListener`).

| Email | Mot de passe |
|---|---|
| `admin@concert.fr` | `admin123` |

Si l'admin existe déjà en base, le script le détecte et ne crée pas de doublon. Le résultat s'affiche dans les logs Jetty :

```
[DataInitializer] Compte admin créé : admin@concert.fr / admin123
# ou, si déjà présent :
[DataInitializer] Admin déjà présent : admin@concert.fr
```

---

## Modèle d'entités

**7 entités principales :**

```
User (abstract — héritage JOINED)
├── Client        (phone, loyaltyPoints)
├── Organizer     (companyName, siret, bankAccount)
└── Administrator (accessLevel, department)

Event ──> Venue       (lieu du concert)
      ──> Organizer   (créateur de l'événement)
      ──> Category    (genre musical)

Order ──> Client
      ──> Ticket[]

Ticket ──> Event
           type: SIMPLE / PREMIUM / VIP / VVIP

Payment (abstract — héritage)
├── CardPayment
├── PayPalPayment
└── MobilePayment
```

**Relations bidirectionnelles (`mappedBy`) :**
- `User` ↔ `Order`
- `Event` ↔ `Ticket`
- `Order` ↔ `Ticket`

---

## Structure du projet

```
src/main/java/
├── entity/
│   ├── user/
│   │   ├── User.java              # Classe parente (héritage JOINED)
│   │   ├── Client.java
│   │   ├── Organizer.java
│   │   └── Administrator.java
│   ├── Event.java
│   ├── Venue.java
│   ├── Order.java
│   ├── Ticket.java
│   ├── Category.java
│   ├── payment/
│   │   ├── Payment.java           # Héritage Payment
│   │   ├── CardPayment.java
│   │   ├── PayPalPayment.java
│   │   └── MobilePayment.java
│   └── enums/
│       ├── EventStatus.java
│       └── TicketType.java
│
├── dao/
│   ├── generic/
│   │   ├── IGenericDao.java       # Interface générique CRUD
│   │   ├── AbstractJpaDao.java    # Implémentation de base (ThreadLocal EM)
│   │   └── EntityManagerHelper.java
│   ├── IClientDao.java
│   ├── IOrganizerDao.java
│   ├── IEventDao.java
│   ├── IOrderDao.java
│   ├── ITicketDao.java
│   ├── ICategoryDao.java
│   ├── IVenueDao.java
│   └── impl/
│       ├── ClientDaoImpl.java     # JPQL + Criteria Query + méthodes métier
│       ├── OrganizerDaoImpl.java
│       ├── EventDaoImpl.java
│       ├── OrderDaoImpl.java
│       ├── TicketDaoImpl.java
│       ├── CategoryDaoImpl.java
│       └── VenueDaoImpl.java
│
├── dto/
│   ├── ClientDto.java
│   ├── OrganizerDto.java
│   ├── AdministratorDto.java
│   ├── EventDto.java
│   ├── OrderDto.java
│   ├── TicketDto.java
│   ├── LoginDto.java
│   └── mapper/
│       ├── ClientMapper.java
│       ├── OrganizerMapper.java
│       ├── EventMapper.java
│       └── OrderMapper.java
│
└── rest/
    ├── config/
    │   └── JerseyConfig.java
    ├── controller/
    │   ├── AuthController.java        # POST /auth/login (multi-rôle)
    │   ├── ClientController.java      # /clients
    │   ├── OrganizerController.java   # /organizers
    │   ├── EventController.java       # /events
    │   ├── OrderController.java       # /orders
    │   ├── TicketController.java      # /tickets
    │   └── CategoryController.java    # /categories
    ├── exception/
    │   ├── RestExceptionMapper.java
    │   ├── BadRequestException.java
    │   └── ResourceNotFoundException.java
    └── filter/
        ├── CorsFilter.java            # Headers CORS (accès depuis Angular)
        └── EntityManagerFilter.java   # Nettoyage EntityManager après chaque requête

servlet/
└── DataInitializer.java               # @WebListener — crée l'admin au démarrage

src/main/webapp/
└── WEB-INF/
    └── web.xml
```

---

## API REST — Endpoints principaux

Base URL : `http://localhost:8080/api`

### Authentification
| Méthode | Endpoint      | Description                                   |
|---------|---------------|-----------------------------------------------|
| `POST`  | `/auth/login` | Connexion unifiée — retourne `{ role, user }` |

### Clients
| Méthode  | Endpoint               | Description                    |
|----------|------------------------|--------------------------------|
| `GET`    | `/clients`             | Liste tous les clients         |
| `GET`    | `/clients/{id}`        | Détail d'un client             |
| `POST`   | `/clients`             | Créer un client (inscription)  |
| `PUT`    | `/clients/{id}`        | Modifier un client             |
| `DELETE` | `/clients/{id}`        | Supprimer un client            |
| `GET`    | `/clients/{id}/stats`  | Statistiques d'un client       |
| `POST`   | `/clients/{id}/loyalty`| Ajouter des points de fidélité |

### Organisateurs
| Méthode  | Endpoint                 | Description                         |
|----------|--------------------------|-------------------------------------|
| `GET`    | `/organizers`            | Liste tous les organisateurs        |
| `POST`   | `/organizers`            | Créer un organisateur (inscription) |
| `PUT`    | `/organizers/{id}`       | Modifier un organisateur            |
| `DELETE` | `/organizers/{id}`       | Supprimer un organisateur           |
| `GET`    | `/organizers/{id}/stats` | Statistiques d'un organisateur      |

### Événements
| Méthode   | Endpoint                 | Description                           |
|-----------|--------------------------|---------------------------------------|
| `GET`     | `/events`                | Liste tous les événements             |
| `GET`     | `/events/available`      | Événements avec billets disponibles   |
| `GET`     | `/events/{id}`           | Détail + statistiques d'un événement  |
| `GET`     | `/events/search`         | Recherche (ville, prix, date, statut) |
| `GET`     | `/events/organizer/{id}` | Événements d'un organisateur          |
| `POST`    | `/events`                | Créer un événement                    |
| `PUT`     | `/events/{id}`           | Modifier un événement                 |
| `DELETE`  | `/events/{id}`           | Supprimer un événement                |

### Commandes & Billets
| Méthode | Endpoint               | Description                         |
|---------|------------------------|-------------------------------------|
| `POST`  | `/orders`              | Passer une commande                 |
| `GET`   | `/orders/client/{id}`  | Commandes d'un client               |
| `GET`   | `/orders/stats`        | Statistiques globales des commandes |
| `GET`   | `/tickets/client/{id}` | Billets d'un client                 |
| `GET`   | `/tickets/order/{id}`  | Billets d'une commande              |

---

## Consignes de rendu — Vérification

### 1. Partie JPA
- [x] ORM fonctionnel (7 entités mappées)
- [x] Héritage implémenté — stratégie `JOINED` sur `User`, héritage sur `Payment`
- [x] Relation bidirectionnelle avec `mappedBy` — `User`/`Order`, `Event`/`Ticket`, `Order`/`Ticket`

### 2. Partie DAO
- [x] Une DAO par entité (7 DAOs + générique)
- [x] Requête JPQL — ex : `ClientDaoImpl.findByEmail()`, `EventDaoImpl.findByStatus()`
- [x] Requête nommée (`@NamedQuery`) — ex : `VenueDaoImpl.findByCityNamed()`
- [x] Criteria Query — ex : `OrganizerDaoImpl.findByCriteria()`, `EventDaoImpl.findByCriteria()`
- [x] Méthodes métier — ex : `ClientDaoImpl.getTotalSpent()`, `EventDaoImpl.countTicketsSold()`

### 3. Partie API REST
- [x] Un controller par entité (7 controllers + AuthController)
- [x] Documentation OpenAPI complète — `EventController` entièrement annoté avec Swagger
- [x] Endpoints métier — calcul revenus, stats clients, points fidélité, recherche avancée
- [x] Utilisation de DTOs — tous les controllers utilisent des DTOs (pas d'exposition directe des entités)
