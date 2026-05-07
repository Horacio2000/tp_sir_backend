package dto.mapper;

import dto.AdministratorDto;
import entity.user.Administrator;

public class AdministratorMapper {

    public static AdministratorDto toDto(Administrator a) {
        AdministratorDto dto = new AdministratorDto();
        dto.setId(a.getId());
        dto.setEmail(a.getEmail());
        dto.setFirstName(a.getFirstName());
        dto.setLastName(a.getLastName());
        dto.setFullName(a.getFirstName() + " " + a.getLastName());
        dto.setAccessLevel(a.getAccessLevel());
        dto.setDepartment(a.getDepartment());
        return dto;
    }
}
