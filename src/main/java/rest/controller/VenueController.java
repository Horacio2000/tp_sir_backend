package rest.controller;

import dao.IVenueDao;
import dao.impl.VenueDaoImpl;
import dto.VenueDto;
import dto.mapper.VenueMapper;
import entity.Venue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import rest.exception.BadRequestException;
import rest.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Path("/venues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Venues", description = "API de gestion des lieux")
public class VenueController {

    private final IVenueDao venueDao;

    public VenueController() {
        this.venueDao = new VenueDaoImpl();
    }

    @GET
    @Operation(summary = "Liste tous les lieux")
    public Response getAllVenues() {
        List<Venue> venues = venueDao.findAll();
        List<VenueDto> dtos = venues.stream()
                .map(VenueMapper::toDto)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Récupère un lieu par ID")
    public Response getVenueById(@PathParam("id") Long id) {
        Venue venue = venueDao.findOne(id);
        if (venue == null) {
            throw new ResourceNotFoundException("Venue", "id", id);
        }
        
        VenueDto dto = VenueMapper.toDto(venue);
        dto.setEventCount(venueDao.getEventCount(id));
        
        return Response.ok(dto).build();
    }

    @POST
    @Operation(summary = "Crée un nouveau lieu")
    public Response createVenue(VenueDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Le nom est obligatoire");
        }
        if (dto.getCity() == null || dto.getCity().trim().isEmpty()) {
            throw new BadRequestException("La ville est obligatoire");
        }
        
        Venue venue = VenueMapper.toEntity(dto);
        venueDao.save(venue);
        
        return Response.status(Response.Status.CREATED)
                .entity(VenueMapper.toDto(venue))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Met à jour un lieu")
    public Response updateVenue(@PathParam("id") Long id, VenueDto dto) {
        Venue existing = venueDao.findOne(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Venue", "id", id);
        }
        
        existing.setName(dto.getName());
        existing.setAddress(dto.getAddress());
        existing.setCity(dto.getCity());
        existing.setCountry(dto.getCountry());
        existing.setCapacity(dto.getCapacity());
        existing.setDescription(dto.getDescription());
        
        Venue updated = venueDao.update(existing);
        return Response.ok(VenueMapper.toDto(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprime un lieu")
    public Response deleteVenue(@PathParam("id") Long id) {
        Venue venue = venueDao.findOne(id);
        if (venue == null) {
            throw new ResourceNotFoundException("Venue", "id", id);
        }
        venueDao.deleteById(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/city/{city}")
    @Operation(summary = "Lieux par ville")
    public Response getVenuesByCity(@PathParam("city") String city) {
        List<Venue> venues = venueDao.findByCity(city);
        List<VenueDto> dtos = venues.stream()
                .map(VenueMapper::toDto)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Recherche de lieux")
    public Response searchVenues(
            @QueryParam("city") String city,
            @QueryParam("country") String country,
            @QueryParam("minCapacity") Integer minCapacity,
            @QueryParam("maxCapacity") Integer maxCapacity) {
        
        List<Venue> venues = venueDao.findByCriteria(city, country, minCapacity, maxCapacity);
        List<VenueDto> dtos = venues.stream()
                .map(VenueMapper::toDto)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }
}