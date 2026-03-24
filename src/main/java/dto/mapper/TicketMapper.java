package dto.mapper;

import dto.TicketDto;
import entity.Ticket;

/**
 * Mapper pour Ticket <-> TicketDto
 */
public class TicketMapper {

    public static TicketDto toDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());
        dto.setTicketType(ticket.getTicketType());
        dto.setPrice(ticket.getPrice());
        dto.setQrCode(ticket.getQrCode());
        dto.setStatus(ticket.getStatus());
        dto.setIssueDate(ticket.getIssueDate());

        // Informations de l'événement
        if (ticket.getEvent() != null) {
            dto.setEventId(ticket.getEvent().getId());
            dto.setEventTitle(ticket.getEvent().getTitle());
            if (ticket.getEvent().getVenue() != null) {
                dto.setEventCity(ticket.getEvent().getVenue().getCity());
            }
        }

        // Informations de la commande
        if (ticket.getOrder() != null) {
            dto.setOrderId(ticket.getOrder().getId());
            dto.setOrderNumber(ticket.getOrder().getOrderNumber());
        }

        return dto;
    }

    public static Ticket toEntity(TicketDto dto) {
        if (dto == null) {
            return null;
        }

        Ticket ticket = new Ticket();
        ticket.setId(dto.getId());
        ticket.setTicketType(dto.getTicketType());
        ticket.setPrice(dto.getPrice());
        ticket.setQrCode(dto.getQrCode());
        ticket.setStatus(dto.getStatus());

        return ticket;
    }
}