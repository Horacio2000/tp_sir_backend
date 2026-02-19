package jpa;

import entity.*;
import entity.enums.*;
import entity.user.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class JpaTest {
    
    private EntityManager manager;

    public JpaTest(EntityManager manager) {
        this.manager = manager;
    }

    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("dev");
        EntityManager manager = factory.createEntityManager();
        JpaTest test = new JpaTest(manager);
        
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        
        try {
            // Créer les données de test
            test.createTestData();
            
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        }
        
        tx.commit();
        
        // Afficher les données créées
        test.displayAllData();
        
        // Tests des relations
        test.testBidirectionalRelations();
        
        // Tests des requêtes JPQL
        test.testJPQLQueries();
        
        manager.close();
        factory.close();
        System.out.println("\n.. done");
    }

    /**
     * Création de toutes les données de test
     */
    private void createTestData() throws ParseException {
        System.out.println("=== CRÉATION DES DONNÉES DE TEST ===\n");
        
        // Vérifier si des données existent déjà
        int numEvents = manager.createQuery("SELECT e FROM Event e", Event.class)
            .getResultList().size();
        
        if (numEvents == 0) {
            System.out.println("Base de données vide, création des données...\n");
            
            // 1. Créer des catégories
            Category rock = new Category("Rock", "Musique rock et variantes");
            Category pop = new Category("Pop", "Musique pop internationale");
            Category jazz = new Category("Jazz", "Jazz classique et moderne");
            Category electro = new Category("Electro", "Musique électronique");
            
            manager.persist(rock);
            manager.persist(pop);
            manager.persist(jazz);
            manager.persist(electro);
            System.out.println("4 Catégories créées");

            // 2. Créer des venues
            Venue zenith = new Venue(
                "Zénith Paris",
                "211 Avenue Jean Jaurès",
                "Paris",
                "France",
                6300
            );
            
            Venue olympia = new Venue(
                "Olympia",
                "28 Boulevard des Capucines",
                "Paris",
                "France",
                2000
            );
            
            Venue accor = new Venue(
                "AccorHotels Arena",
                "8 Boulevard de Bercy",
                "Paris",
                "France",
                20300
            );
            
            manager.persist(zenith);
            manager.persist(olympia);
            manager.persist(accor);
            System.out.println("3 Venues créés");

            // 3. Créer des clients
            Client client1 = new Client(
                "jean.dupont@email.com",
                "password123",
                "Jean",
                "Dupont",
                "+33612345678"
            );
            
            Client client2 = new Client(
                "marie.martin@email.com",
                "password456",
                "Marie",
                "Martin",
                "+33698765432"
            );
            
            Client client3 = new Client(
                "paul.bernard@email.com",
                "password789",
                "Paul",
                "Bernard"
            );
            
            manager.persist(client1);
            manager.persist(client2);
            manager.persist(client3);
            System.out.println("3 Clients créés");

            // 4. Créer des organisateurs
            Organizer organizer1 = new Organizer(
                "contact@musicprod.com",
                "orgpass123",
                "Pierre",
                "Leroy",
                "Music Production SARL",
                "12345678901234"
            );
            organizer1.setBankAccount("FR7612345678901234567890123");
            
            Organizer organizer2 = new Organizer(
                "info@concertmania.com",
                "orgpass456",
                "Sophie",
                "Dubois",
                "Concert Mania",
                "98765432109876"
            );
            organizer2.setBankAccount("FR7698765432109876543210987");
            
            manager.persist(organizer1);
            manager.persist(organizer2);
            System.out.println("2 Organisateurs créés");

            // 5. Créer un administrateur
            Administrator admin = new Administrator(
                "admin@ticketapp.com",
                "adminpass",
                "Admin",
                "System",
                "FULL",
                "IT Department"
            );
            
            manager.persist(admin);
            System.out.println("1 Administrateur créé");

            // 6. Créer des événements
            Event event1 = new Event(
                "Festival Rock Legends 2026",
                LocalDate.parse("2026-06-15"),
                LocalTime.parse("20:00"),
                50.0,
                5000,
                zenith,
                organizer1
            );
            event1.setDescription("Le plus grand festival de rock de l'année");
            event1.setEndTime(LocalTime.parse("23:30"));
            event1.setStatus(EventStatus.PUBLISHED);
            event1.addCategory(rock);
            event1.addCategory(pop);
            
            Event event2 = new Event(
                "Soirée Jazz Intimiste",
                LocalDate.parse("2026-07-20"),
                LocalTime.parse("19:30"),
                35.0,
                1500,
                olympia,
                organizer2
            );
            event2.setDescription("Jazz acoustique dans un cadre intimiste");
            event2.setEndTime(LocalTime.parse("22:30"));
            event2.setStatus(EventStatus.PUBLISHED);
            event2.addCategory(jazz);
            
            Event event3 = new Event(
                "Electro Night Party",
                LocalDate.parse("2026-08-10"),
                LocalTime.parse("21:00"),
                60.0,
                15000,
                accor,
                organizer1
            );
            event3.setDescription("La plus grande soirée électro de Paris");
            event3.setEndTime(LocalTime.parse("04:00"));
            event3.setStatus(EventStatus.PUBLISHED);
            event3.addCategory(electro);
            
            manager.persist(event1);
            manager.persist(event2);
            manager.persist(event3);
            System.out.println("3 Événements créés");

            // 7. Créer des tickets
            Ticket ticket1 = new Ticket(TicketType.SIMPLE, 50.0, event1);
            Ticket ticket2 = new Ticket(TicketType.VIP, 100.0, event1);
            Ticket ticket3 = new Ticket(TicketType.PREMIUM, 75.0, event1);
            Ticket ticket4 = new Ticket(TicketType.SIMPLE, 35.0, event2);
            Ticket ticket5 = new Ticket(TicketType.VIP, 70.0, event2);
            Ticket ticket6 = new Ticket(TicketType.VVIP, 150.0, event3);
            Ticket ticket7 = new Ticket(TicketType.VIP, 90.0, event3);
            Ticket ticket8 = new Ticket(TicketType.SIMPLE, 60.0, event3);
            
            manager.persist(ticket1);
            manager.persist(ticket2);
            manager.persist(ticket3);
            manager.persist(ticket4);
            manager.persist(ticket5);
            manager.persist(ticket6);
            manager.persist(ticket7);
            manager.persist(ticket8);
            System.out.println("8 Tickets créés");

            // 8. Créer des commandes
            // Commande 1 - Client 1
            Order order1 = new Order(client1, 0.0);
            order1.addTicket(ticket1);
            order1.addTicket(ticket2);
            order1.calculateTotalAmount();
            
            Payment payment1 = new Payment(
                order1.getTotalAmount(),
                PaymentMethod.CARD,
                "TXN-" + System.currentTimeMillis()
            );
            payment1.markAsCompleted();
            order1.setPayment(payment1);
            order1.confirm();
            
            ticket1.sell();
            ticket2.sell();
            event1.decreaseAvailableTickets(2);
            client1.addLoyaltyPoints(15);
            
            manager.persist(order1);
            
            // Commande 2 - Client 2
            Order order2 = new Order(client2, 0.0);
            order2.addTicket(ticket3);
            order2.addTicket(ticket4);
            order2.calculateTotalAmount();
            
            Payment payment2 = new Payment(
                order2.getTotalAmount(),
                PaymentMethod.PAYPAL,
                "TXN-" + System.currentTimeMillis()
            );
            payment2.markAsCompleted();
            order2.setPayment(payment2);
            order2.confirm();
            
            ticket3.sell();
            ticket4.sell();
            event1.decreaseAvailableTickets(1);
            event2.decreaseAvailableTickets(1);
            client2.addLoyaltyPoints(11);
            
            manager.persist(order2);
            
            // Commande 3 - Client 3
            Order order3 = new Order(client3, 0.0);
            order3.addTicket(ticket6);
            order3.addTicket(ticket7);
            order3.calculateTotalAmount();
            
            Payment payment3 = new Payment(
                order3.getTotalAmount(),
                PaymentMethod.MOBILE_MONEY,
                "TXN-" + System.currentTimeMillis()
            );
            payment3.markAsCompleted();
            order3.setPayment(payment3);
            order3.confirm();
            
            ticket6.sell();
            ticket7.sell();
            event3.decreaseAvailableTickets(2);
            client3.addLoyaltyPoints(24);
            
            manager.persist(order3);
            
            System.out.println("3 Commandes créées");
            
            System.out.println("\n Toutes les données ont été créées avec succès!\n");
            
        } else {
            System.out.println("Des données existent déjà (" + numEvents + " événements)\n");
        }
    }

    /**
     * Affichage de toutes les données
     */
    private void displayAllData() {
        System.out.println("\n=== AFFICHAGE DES DONNÉES ===\n");
        
        // Clients
        List<Client> clients = manager.createQuery("SELECT c FROM Client c", Client.class)
            .getResultList();
        System.out.println("--- CLIENTS (" + clients.size() + ") ---");
        for (Client client : clients) {
            System.out.println("  • " + client.getFullName() + 
                " - " + client.getEmail() + 
                " (Points: " + client.getLoyaltyPoints() + 
                ", Commandes: " + client.getOrders().size() + ")");
        }
        
        // Organisateurs
        List<Organizer> organizers = manager.createQuery("SELECT o FROM Organizer o", Organizer.class)
            .getResultList();
        System.out.println("\n--- ORGANISATEURS (" + organizers.size() + ") ---");
        for (Organizer org : organizers) {
            System.out.println("  • " + org.getFullName() + 
                " - " + org.getCompanyName() + 
                " (Événements: " + org.getEvents().size() + ")");
        }
        
        // Événements
        List<Event> events = manager.createQuery("SELECT e FROM Event e", Event.class)
            .getResultList();
        System.out.println("\n--- ÉVÉNEMENTS (" + events.size() + ") ---");
        for (Event event : events) {
            System.out.println("  • " + event.getTitle());
            System.out.println("    Lieu: " + event.getVenue().getName() + " (" + event.getVenue().getCity() + ")");
            System.out.println("    Date: " + event.getEventDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            System.out.println("    Prix de base: " + event.getBasePrice() + "€");
            System.out.println("    Tickets: " + event.getAvailableTickets() + "/" + event.getTotalTickets() + " disponibles");
            System.out.println("    Statut: " + event.getStatus());
        }
        
        // Commandes
        List<Order> orders = manager.createQuery("SELECT o FROM Order o", Order.class)
            .getResultList();
        System.out.println("\n--- COMMANDES (" + orders.size() + ") ---");
        for (Order order : orders) {
            System.out.println("  • " + order.getOrderNumber());
            System.out.println("    Client: " + order.getClient().getFullName());
            System.out.println("    Montant: " + order.getTotalAmount() + "€");
            System.out.println("    Tickets: " + order.getTickets().size());
            System.out.println("    Statut: " + order.getStatus());
            System.out.println("    Paiement: " + order.getPayment().getMethod() + " - " + order.getPayment().getStatus());
        }
    }

    /**
     * Test des relations bidirectionnelles
     */
    private void testBidirectionalRelations() {
        System.out.println("\n=== TEST DES RELATIONS BIDIRECTIONNELLES ===\n");
        
        // Test 1: Client ↔ Order
        System.out.println("--- Test Client ↔ Order ---");
        List<Client> clients = manager.createQuery("SELECT c FROM Client c", Client.class)
            .getResultList();
        
        if (!clients.isEmpty()) {
            Client client = clients.get(0);
            System.out.println("Client: " + client.getFullName());
            System.out.println("Ses commandes:");
            for (Order order : client.getOrders()) {
                System.out.println("  → " + order.getOrderNumber() + 
                    " (Montant: " + order.getTotalAmount() + "€)");
                System.out.println("    Client de la commande: " + order.getClient().getFullName());
            }
        }
        
        // Test 2: Organizer ↔ Event
        System.out.println("\n--- Test Organizer ↔ Event ---");
        List<Organizer> organizers = manager.createQuery("SELECT o FROM Organizer o", Organizer.class)
            .getResultList();
        
        if (!organizers.isEmpty()) {
            Organizer organizer = organizers.get(0);
            System.out.println("Organisateur: " + organizer.getFullName());
            System.out.println("Ses événements:");
            for (Event event : organizer.getEvents()) {
                System.out.println("  → " + event.getTitle());
                System.out.println("    Organisateur de l'événement: " + event.getOrganizer().getFullName());
            }
        }
        
        // Test 3: Event ↔ Category (ManyToMany)
        System.out.println("\n--- Test Event ↔ Category (ManyToMany) ---");
        List<Event> events = manager.createQuery("SELECT e FROM Event e", Event.class)
            .getResultList();
        
        if (!events.isEmpty()) {
            Event event = events.get(0);
            System.out.println("Événement: " + event.getTitle());
            System.out.println("Catégories:");
            for (Category category : event.getCategories()) {
                System.out.println("  → " + category.getName());
                System.out.println("    Nombre d'événements dans cette catégorie: " + category.getEvents().size());
            }
        }
        
        // Test 4: Order ↔ Payment (OneToOne)
        System.out.println("\n--- Test Order ↔ Payment (OneToOne) ---");
        List<Order> orders = manager.createQuery("SELECT o FROM Order o", Order.class)
            .getResultList();
        
        if (!orders.isEmpty()) {
            Order order = orders.get(0);
            System.out.println("Commande: " + order.getOrderNumber());
            System.out.println("Paiement: " + order.getPayment().getMethod() + 
                " - " + order.getPayment().getAmount() + "€");
            System.out.println("Commande du paiement: " + order.getPayment().getOrder().getOrderNumber());
        }
        
        System.out.println("\n Relations bidirectionnelles vérifiées!");
    }

    /**
     * Tests de requêtes JPQL
     */
    private void testJPQLQueries() {
        System.out.println("\n=== TESTS REQUÊTES JPQL ===\n");
        
        // 1. Statistiques utilisateurs
        System.out.println("--- Statistiques Utilisateurs ---");
        Long nbClients = manager.createQuery("SELECT COUNT(c) FROM Client c", Long.class)
            .getSingleResult();
        Long nbOrganizers = manager.createQuery("SELECT COUNT(o) FROM Organizer o", Long.class)
            .getSingleResult();
        Long nbAdmins = manager.createQuery("SELECT COUNT(a) FROM Administrator a", Long.class)
            .getSingleResult();
        
        System.out.println("Clients: " + nbClients);
        System.out.println("Organisateurs: " + nbOrganizers);
        System.out.println("Administrateurs: " + nbAdmins);
        
        // 2. Revenus totaux
        System.out.println("\n--- Revenus ---");
        Double totalRevenue = manager.createQuery(
            "SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status", Double.class)
            .setParameter("status", OrderStatus.CONFIRMED)
            .getSingleResult();
        
        System.out.println("Revenu total des commandes confirmées: " + 
            (totalRevenue != null ? totalRevenue : 0) + "€");
        
        // 3. Statistiques tickets par type
        System.out.println("\n--- Tickets par Type ---");
        List<Object[]> ticketStats = manager.createQuery(
            "SELECT t.ticketType, COUNT(t) FROM Ticket t GROUP BY t.ticketType", Object[].class)
            .getResultList();
        
        for (Object[] stat : ticketStats) {
            System.out.println(stat[0] + ": " + stat[1] + " tickets");
        }
        
        // 4. Tickets vendus par événement
        System.out.println("\n--- Tickets Vendus par Événement ---");
        List<Object[]> soldTickets = manager.createQuery(
            "SELECT e.title, COUNT(t) FROM Event e JOIN e.tickets t " +
            "WHERE t.status = :status GROUP BY e.title ORDER BY COUNT(t) DESC",
            Object[].class)
            .setParameter("status", TicketStatus.SOLD)
            .getResultList();
        
        for (Object[] result : soldTickets) {
            System.out.println(result[0] + ": " + result[1] + " tickets vendus");
        }
        
        // 5. Top clients par nombre de commandes
        System.out.println("\n--- Top Clients ---");
        List<Object[]> topClients = manager.createQuery(
            "SELECT c.firstName, c.lastName, c.loyaltyPoints, COUNT(o) " +
            "FROM Client c JOIN c.orders o " +
            "GROUP BY c.id, c.firstName, c.lastName, c.loyaltyPoints " +
            "ORDER BY COUNT(o) DESC",
            Object[].class)
            .getResultList();
        
        for (Object[] result : topClients) {
            System.out.println(result[0] + " " + result[1] + 
                " - " + result[2] + " points - " + result[3] + " commandes");
        }
        
        // 6. Événements par catégorie
        System.out.println("\n--- Événements par Catégorie ---");
        List<Object[]> eventsByCategory = manager.createQuery(
            "SELECT c.name, COUNT(e) FROM Category c JOIN c.events e " +
            "GROUP BY c.name ORDER BY COUNT(e) DESC",
            Object[].class)
            .getResultList();
        
        for (Object[] result : eventsByCategory) {
            System.out.println(result[0] + ": " + result[1] + " événements");
        }
        
        System.out.println("\n Requêtes JPQL exécutées avec succès!");
    }
}