package dto.mapper;

import dto.OrganizerDto;
import entity.user.Organizer;

/**
 * Mapper pour Organizer <-> OrganizerDto
 */
public class OrganizerMapper {

    public static OrganizerDto toDto(Organizer organizer) {
        if (organizer == null) {
            return null;
        }

        OrganizerDto dto = new OrganizerDto();
        dto.setId(organizer.getId());
        dto.setEmail(organizer.getEmail());
        dto.setFirstName(organizer.getFirstName());
        dto.setLastName(organizer.getLastName());
        dto.setCompanyName(organizer.getCompanyName());
        dto.setSiret(organizer.getSiret());
        dto.setBankAccount(organizer.getBankAccount());
        dto.setCreatedAt(organizer.getCreatedAt());

        return dto;
    }

    public static Organizer toEntity(OrganizerDto dto) {
        if (dto == null) {
            return null;
        }

        Organizer organizer = new Organizer();
        organizer.setId(dto.getId());
        organizer.setEmail(dto.getEmail());
        organizer.setFirstName(dto.getFirstName());
        organizer.setLastName(dto.getLastName());
        organizer.setCompanyName(dto.getCompanyName());
        organizer.setSiret(dto.getSiret());
        organizer.setBankAccount(dto.getBankAccount());

        return organizer;
    }
}