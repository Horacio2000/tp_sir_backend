# Projet SIR

# Binome : Abel-Horacio SOSSOU  & Stephane BANKOLE


## Modèle d'entités proposé ##
Entités principales (7 entités)

1. User (avec roles : CLIENT, ORGANIZER, ADMIN)
2. Event
3. Order (Commande)
4. Ticket
5. Payment (avec héritage !)
6. Category (Genre musical)
7. Venue (Lieu)

# Relations bidirectionnelles :

1. User et Order (un user a plusieurs commandes, une commande appartient à un user)
2. Event et Ticket (un événement a plusieurs tickets, un ticket pour un événement)
3. Order et Ticket (une commande contient plusieurs tickets, un ticket dans une commande)

# Relation d'héritage - Payment :
Payment (classe abstraite)
   ├── CardPayment (paiement par carte)
   ├── PayPalPayment (paiement PayPal)
   └── MobilePayment (paiement mobile - optionnel)

Diagramme de classe à la racine du projet
--------


## Structure du projet
```
src/main/java/
├── entity/              
│   ├── user/
│   ├── Event.java
│   ├── Order.java
│   └── ...
│
├── dao/                 
│   ├── generic/
│   ├── impl/
│   └── I*Dao.java
│
├── dto/                 
│   ├── ClientDto.java
│   ├── EventDto.java
│   ├── OrderDto.java
│   └── ...
│
├── rest/                
│   ├── config/
│   │   └── JerseyConfig.java
│   ├── controller/
│   │   ├── ClientController.java
│   │   ├── EventController.java
│   │   └── ...
│   └── exception/
│       └── RestExceptionMapper.java
│
└── servlet/             
    └── ...

src/main/webapp/
└── WEB-INF/
    └── web.xml     
```
------

## Pour compiler et lancer l'app

mvn clean compile jetty:run

## URLs importantes

Page d'accueil :        http://localhost:8080/
Swagger UI :            http://localhost:8080/swagger-ui.html
Spécification OpenAPI : http://localhost:8080/api/openapi.json
API :                   http://localhost:8080/api/*

-----------

## Consignes de rendu pour la version finalisée de votre back end
## 1. sur la partie JPA :
 -MOR (les entités) fonctionnel (Fait)
 -contient au moins un héritage (Fait)
 -contient au moins une relation bidirectionel (mappedBy) (Fait)
## 2. sur les DAO :
 -une DAO par entité (Fait)
 -présence d'au moins une requête JPQL, une requête nommée, une criteria query (Fait)
 -au moins une de ces DAO doit contenir quelques méthodes métier (pas juste CRUD) (Fait)
## 3. sur l'API Rest :
-un controller par entité (Fait)
-documentation openAPI complète sur au moins un controller (Fait)
-au moins un de ces controllers doit contenir des endpoints métiers (pas juste CRUD) (Fait)
-au moins un de ces controllers doit utiliser un DTO (Fait)

```