package dto.mapper;

import dto.OrderDto;
import entity.Order;
import java.util.stream.Collectors;

/**
 * Mapper pour Order <-> OrderDto
 */
public class OrderMapper {

    public static OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());

        if (order.getClient() != null) {
            dto.setClientId(order.getClient().getId());
            dto.setClientName(order.getClient().getFullName());
            dto.setClientEmail(order.getClient().getEmail());
        }

        if (order.getPayment() != null) {
            dto.setPaymentId(order.getPayment().getId());
            dto.setPaymentMethod(order.getPayment().getMethod().toString());
            dto.setPaymentStatus(order.getPayment().getStatus().toString());
        }

        if (order.getTickets() != null) {
            dto.setTicketIds(
                order.getTickets().stream()
                    .map(ticket -> ticket.getId())
                    .collect(Collectors.toList())
            );
            dto.setTicketCount(order.getTickets().size());
        }

        return dto;
    }

    public static Order toEntity(OrderDto dto) {
        if (dto == null) {
            return null;
        }

        Order order = new Order();
        order.setId(dto.getId());
        order.setOrderNumber(dto.getOrderNumber());
        order.setTotalAmount(dto.getTotalAmount());
        order.setStatus(dto.getStatus());

        return order;
    }
}