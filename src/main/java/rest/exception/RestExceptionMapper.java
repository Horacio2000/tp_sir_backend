package rest.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper global pour gérer toutes les exceptions et retourner des réponses JSON cohérentes
 */
@Provider
public class RestExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        
        // ResourceNotFoundException → 404
        if (exception instanceof ResourceNotFoundException) {
            return buildErrorResponse(
                Response.Status.NOT_FOUND,
                "Resource Not Found",
                exception.getMessage()
            );
        }
        
        // BadRequestException → 400
        if (exception instanceof BadRequestException) {
            return buildErrorResponse(
                Response.Status.BAD_REQUEST,
                "Bad Request",
                exception.getMessage()
            );
        }
        
        // Toutes les autres exceptions → 500
        return buildErrorResponse(
            Response.Status.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred: " + exception.getMessage()
        );
    }

    /**
     * Construit une réponse d'erreur JSON standardisée
     */
    private Response buildErrorResponse(Response.Status status, String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        errorResponse.put("status", status.getStatusCode());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        
        return Response
                .status(status)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}