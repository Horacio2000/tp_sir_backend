package test;

import dao.*;
import dao.impl.*;
import entity.*;
import entity.enums.*;
import entity.user.*;
import dao.generic.EntityManagerHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DaoTest {

    public static void main(String[] args) {
        try {
            System.out.println("=== TEST DES DAOs ===\n");

            testClientDao();
            testEventDao();
            testOrganizerDao();
            testVenueDao();
            testCategoryDao();
            testOrderDao();
            testTicketDao();
            testPaymentDao();

            System.out.println("\n=== TOUS LES TESTS SONT PASSÉS ✅ ===");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            EntityManagerHelper.closeEntityManager();
            EntityManagerHelper.closeEntityManagerFactory();
        }
    }

    private static void testClientDao() {
        System.out.println(">>> Test ClientDao");
        IClientDao clientDao = new ClientDaoImpl();

        // Test JPQL
        clientDao.findByEmail("jean.dupont@email.com")
            .ifPresent(c -> System.out.println("  JPQL - Client trouvé: " + c.getFullName()));

        // Test Named Query
        System.out.println("  Named Query - Top clients: " + clientDao.findTopClients().size());

        // Test Criteria Query
        System.out.println("  Criteria - Recherche: " + 
            clientDao.findByCriteria("Jean", null, null).size());

        // Test méthodes métier
        if (!clientDao.findAll().isEmpty()) {
            Long clientId = clientDao.findAll().get(0).getId();
            System.out.println("  Métier - Total dépensé: " + 
                clientDao.getTotalSpent(clientId) + "€");
        }

        System.out.println("✅ ClientDao OK\n");
    }

    private static void testEventDao() {
        System.out.println(">>> Test EventDao");
        IEventDao eventDao = new EventDaoImpl();

        // Test JPQL
        System.out.println("  JPQL - Événements publiés: " + 
            eventDao.findByStatus(EventStatus.PUBLISHED).size());

        // Test Named Query
        System.out.println("  Named Query - Événements à venir: " + 
            eventDao.findUpcomingEvents().size());

        // Test Criteria Query
        System.out.println("  Criteria - Recherche Paris: " + 
            eventDao.findByCriteria("Paris", null, null, null).size());

        // Test méthodes métier
        if (!eventDao.findAll().isEmpty()) {
            Long eventId = eventDao.findAll().get(0).getId();
            System.out.println("  Métier - Tickets vendus: " + 
                eventDao.countTicketsSold(eventId));
            System.out.println("  Métier - Revenu: " + 
                eventDao.calculateRevenue(eventId) + "€");
        }

        System.out.println("✅ EventDao OK\n");
    }

    private static void testOrganizerDao() {
        System.out.println(">>> Test OrganizerDao");
        IOrganizerDao organizerDao = new OrganizerDaoImpl();

        // Test JPQL
        organizerDao.findByEmail("contact@musicprod.com")
            .ifPresent(o -> System.out.println("  JPQL - Organisateur trouvé: " + 
                o.getFullName()));

        // Test méthodes métier
        if (!organizerDao.findAll().isEmpty()) {
            Long orgId = organizerDao.findAll().get(0).getId();
            System.out.println("  Métier - Nombre d'événements: " + 
                organizerDao.getEventCount(orgId));
            System.out.println("  Métier - Revenu total: " + 
                organizerDao.getTotalRevenue(orgId) + "€");
        }

        System.out.println("✅ OrganizerDao OK\n");
    }

    private static void testVenueDao() {
        System.out.println(">>> Test VenueDao");
        IVenueDao venueDao = new VenueDaoImpl();

        // Test JPQL
        System.out.println("  JPQL - Venues à Paris: " + 
            venueDao.findByCity("Paris").size());

        // Test Named Query
        System.out.println("  Named Query - Venues capacité > 5000: " + 
            venueDao.findByMinCapacityNamed(5000).size());

        // Test Criteria Query
        System.out.println("  Criteria - Recherche France: " + 
            venueDao.findByCriteria(null, "France", null, null).size());

        System.out.println("✅ VenueDao OK\n");
    }

    private static void testCategoryDao() {
        System.out.println(">>> Test CategoryDao");
        ICategoryDao categoryDao = new CategoryDaoImpl();

        // Test JPQL
        categoryDao.findByName("Rock")
            .ifPresent(c -> System.out.println("  JPQL - Catégorie trouvée: " + c.getName()));

        // Test Named Query
        System.out.println("  Named Query - Catégories populaires: " + 
            categoryDao.findPopularCategories().size());

        // Test méthodes métier
        if (!categoryDao.findAll().isEmpty()) {
            Long catId = categoryDao.findAll().get(0).getId();
            System.out.println("  Métier - Nombre d'événements: " + 
                categoryDao.getEventCount(catId));
        }

        System.out.println("✅ CategoryDao OK\n");
    }

    private static void testOrderDao() {
        System.out.println(">>> Test OrderDao");
        IOrderDao orderDao = new OrderDaoImpl();

        // Test JPQL
        System.out.println("  JPQL - Commandes confirmées: " + 
            orderDao.findByStatus(OrderStatus.CONFIRMED).size());

        // Test Named Query
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        System.out.println("  Named Query - Commandes récentes: " + 
            orderDao.findRecentOrders(weekAgo).size());

        // Test méthodes métier
        System.out.println("  Métier - Revenu total: " + 
            orderDao.getTotalRevenue() + "€");

        System.out.println("✅ OrderDao OK\n");
    }

    private static void testTicketDao() {
        System.out.println(">>> Test TicketDao");
        ITicketDao ticketDao = new TicketDaoImpl();

        // Test JPQL
        System.out.println("  JPQL - Tickets vendus: " + 
            ticketDao.findByStatus(TicketStatus.SOLD).size());

        // Test Criteria Query
        System.out.println("  Criteria - Recherche VIP: " + 
            ticketDao.findByCriteria(null, TicketType.VIP, null, null, null).size());

        // Test méthodes métier
        if (!ticketDao.findAll().isEmpty() && 
            ticketDao.findAll().get(0).getEvent() != null) {
            Long eventId = ticketDao.findAll().get(0).getEvent().getId();
            System.out.println("  Métier - Tickets vendus pour événement: " + 
                ticketDao.countSoldTicketsByEvent(eventId));
        }

        System.out.println("✅ TicketDao OK\n");
    }

    private static void testPaymentDao() {
        System.out.println(">>> Test PaymentDao");
        IPaymentDao paymentDao = new PaymentDaoImpl();

        // Test JPQL
        System.out.println("  JPQL - Paiements complétés: " + 
            paymentDao.findByStatus(PaymentStatus.COMPLETED).size());

        // Test Criteria Query
        System.out.println("  Criteria - Paiements par carte: " + 
            paymentDao.findByCriteria(null, PaymentMethod.CARD, null, null).size());

        // Test méthodes métier
        System.out.println("  Métier - Montant total: " + 
            paymentDao.getTotalAmount() + "€");
        System.out.println("  Métier - Paiements par carte: " + 
            paymentDao.getTotalAmountByMethod(PaymentMethod.CARD) + "€");

        System.out.println("✅ PaymentDao OK\n");
    }
}