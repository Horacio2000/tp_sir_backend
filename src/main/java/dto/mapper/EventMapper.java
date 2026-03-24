package dto.mapper;

import dto.EventDto;
import entity.Event;

/**
 * Utilitaire pour convertir Event <-> EventDto
 */
public class EventMapper {

    /**
     * Convertit une entité Event en EventDto
     */
    public static EventDto toDto(Event event) {
        if (event == null) {
            return null;
        }

        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setBasePrice(event.getBasePrice());
        dto.setTotalTickets(event.getTotalTickets());
        dto.setAvailableTickets(event.getAvailableTickets());
        dto.setStatus(event.getStatus());

        // Informations du venue (si présent)
        if (event.getVenue() != null) {
            dto.setVenueName(event.getVenue().getName());
            dto.setVenueCity(event.getVenue().getCity());
            dto.setVenueCapacity(event.getVenue().getCapacity());
        }

        // Informations de l'organisateur (si présent)
        if (event.getOrganizer() != null) {
            dto.setOrganizerName(event.getOrganizer().getFullName());
            dto.setOrganizerCompany(event.getOrganizer().getCompanyName());
        }

        return dto;
    }

    /**
     * Convertit un EventDto en entité Event (pour création/modification)
     * Note: Ne gère pas les relations (venue, organizer)
     */
    public static Event toEntity(EventDto dto) {
        if (dto == null) {
            return null;
        }

        Event event = new Event();
        event.setId(dto.getId());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setBasePrice(dto.getBasePrice());
        event.setTotalTickets(dto.getTotalTickets());
        event.setAvailableTickets(dto.getAvailableTickets());
        event.setStatus(dto.getStatus());

        return event;
    }
}