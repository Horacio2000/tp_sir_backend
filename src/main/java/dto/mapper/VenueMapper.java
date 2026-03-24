package dto.mapper;

import dto.VenueDto;
import entity.Venue;

public class VenueMapper {
    
    public static VenueDto toDto(Venue venue) {
        if (venue == null) return null;
        
        VenueDto dto = new VenueDto();
        dto.setId(venue.getId());
        dto.setName(venue.getName());
        dto.setAddress(venue.getAddress());
        dto.setCity(venue.getCity());
        dto.setCountry(venue.getCountry());
        dto.setCapacity(venue.getCapacity());
        dto.setDescription(venue.getDescription());
        
        return dto;
    }
    
    public static Venue toEntity(VenueDto dto) {
        if (dto == null) return null;
        
        Venue venue = new Venue();
        venue.setId(dto.getId());
        venue.setName(dto.getName());
        venue.setAddress(dto.getAddress());
        venue.setCity(dto.getCity());
        venue.setCountry(dto.getCountry());
        venue.setCapacity(dto.getCapacity());
        venue.setDescription(dto.getDescription());
        
        return venue;
    }
}