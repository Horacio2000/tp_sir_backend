package dto.mapper;

import dto.ClientDto;
import entity.user.Client;

/**
 * Mapper pour Client <-> ClientDto
 */
public class ClientMapper {

    public static ClientDto toDto(Client client) {
        if (client == null) {
            return null;
        }

        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setEmail(client.getEmail());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setPhone(client.getPhone());
        dto.setLoyaltyPoints(client.getLoyaltyPoints());
        dto.setCreatedAt(client.getCreatedAt());

        return dto;
    }

    public static Client toEntity(ClientDto dto) {
        if (dto == null) {
            return null;
        }

        Client client = new Client();
        client.setId(dto.getId());
        client.setEmail(dto.getEmail());
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setPhone(dto.getPhone());
        client.setLoyaltyPoints(dto.getLoyaltyPoints());

        return client;
    }
}