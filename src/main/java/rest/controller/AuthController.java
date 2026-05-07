package rest.controller;

import dao.IAdministratorDao;
import dao.IClientDao;
import dao.IOrganizerDao;
import dao.impl.AdministratorDaoImpl;
import dao.impl.ClientDaoImpl;
import dao.impl.OrganizerDaoImpl;
import dto.AdministratorDto;
import dto.ClientDto;
import dto.LoginDto;
import dto.OrganizerDto;
import dto.mapper.AdministratorMapper;
import dto.mapper.ClientMapper;
import dto.mapper.OrganizerMapper;
import entity.user.Administrator;
import entity.user.Client;
import entity.user.Organizer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import rest.exception.BadRequestException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Auth", description = "Authentification unifiée multi-rôle")
public class AuthController {

    private final IClientDao clientDao;
    private final IOrganizerDao organizerDao;
    private final IAdministratorDao adminDao;

    public AuthController() {
        this.clientDao = new ClientDaoImpl();
        this.organizerDao = new OrganizerDaoImpl();
        this.adminDao = new AdministratorDaoImpl();
    }

    @POST
    @Path("/login")
    @Operation(summary = "Connexion unifiée — détecte le rôle automatiquement")
    public Response login(LoginDto dto) {
        if (dto.getEmail() == null || dto.getPassword() == null) {
            throw new BadRequestException("Email et mot de passe obligatoires");
        }

        // 1. Admin
        Optional<Administrator> adminOpt = adminDao.findByEmail(dto.getEmail());
        if (adminOpt.isPresent()) {
            if (!dto.getPassword().equals(adminOpt.get().getPassword())) {
                return unauthorized();
            }
            AdministratorDto userDto = AdministratorMapper.toDto(adminOpt.get());
            return Response.ok(wrap("ADMIN", userDto)).build();
        }

        // 2. Organisateur
        Optional<Organizer> orgOpt = organizerDao.findByEmail(dto.getEmail());
        if (orgOpt.isPresent()) {
            if (!dto.getPassword().equals(orgOpt.get().getPassword())) {
                return unauthorized();
            }
            Organizer org = orgOpt.get();
            OrganizerDto userDto = OrganizerMapper.toDto(org);
            userDto.setEventCount(organizerDao.getEventCount(org.getId()));
            userDto.setTotalRevenue(organizerDao.getTotalRevenue(org.getId()));
            return Response.ok(wrap("ORGANIZER", userDto)).build();
        }

        // 3. Client
        Optional<Client> clientOpt = clientDao.findByEmail(dto.getEmail());
        if (clientOpt.isPresent()) {
            if (!dto.getPassword().equals(clientOpt.get().getPassword())) {
                return unauthorized();
            }
            Client client = clientOpt.get();
            ClientDto userDto = ClientMapper.toDto(client);
            userDto.setTotalOrders(clientDao.getTotalOrdersCount(client.getId()));
            userDto.setTotalSpent(clientDao.getTotalSpent(client.getId()));
            return Response.ok(wrap("CLIENT", userDto)).build();
        }

        return unauthorized();
    }

    private Map<String, Object> wrap(String role, Object userDto) {
        Map<String, Object> result = new HashMap<>();
        result.put("role", role);
        result.put("user", userDto);
        return result;
    }

    private Response unauthorized() {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(Map.of("error", "Email ou mot de passe incorrect"))
                .build();
    }
}
