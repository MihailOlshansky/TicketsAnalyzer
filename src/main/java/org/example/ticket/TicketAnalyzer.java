package org.example.ticket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.lang.Math;

public class TicketAnalyzer {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");

    private final List<Ticket> tickets;

    public TicketAnalyzer(String filePath) throws IOException {
        InputStream inputStream = TicketAnalyzer.class.getClassLoader().getResourceAsStream(filePath);

        if (inputStream == null) {
            throw new RuntimeException("Resource not found: " + filePath);
        }

        ObjectMapper mapper = new ObjectMapper();

        tickets = mapper.readValue(inputStream, new TypeReference<TicketList>(){}).getTickets();
    }

    public Map<String, Integer> minTravelDuration(String origin, String destination) {
        Map<String, Integer> result = new HashMap<>();
        tickets.stream()
                .filter(ticket -> Objects.equals(ticket.getOrigin(), origin)
                        && Objects.equals(ticket.getDestination(), destination))
                .forEach(ticket -> {
                    int flightDuration;
                    try {
                        Date departure = dateFormat.parse(ticket.getDepartureDate() + " " + ticket.getDepartureTime());
                        Date arrival = dateFormat.parse(ticket.getArrivalDate() + " " + ticket.getArrivalTime());
                        flightDuration = (int) Duration.between(departure.toInstant(), arrival.toInstant()).toMinutes();
                    } catch (ParseException ignored) {
                        return;
                    }
                    String carrier = ticket.getCarrier();
                    result.put(carrier, Math.min(flightDuration, result.getOrDefault(carrier, flightDuration)));
                });

        return result;
    }

    public BigDecimal avgAndMedianDifference(String origin, String destination) {
        List<BigDecimal> sortedPrices = tickets.stream()
                .filter(ticket -> Objects.equals(ticket.getOrigin(), origin)
                        && Objects.equals(ticket.getDestination(), destination))
                .map(Ticket::getPrice)
                .sorted()
                .toList();

        BigDecimal avgPrice = sortedPrices.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(sortedPrices.size()), 3, RoundingMode.HALF_UP);

        BigDecimal medianPrice = sortedPrices.get(sortedPrices.size() / 2)
                .add(sortedPrices.get((sortedPrices.size() - 1) / 2))
                .divide(BigDecimal.valueOf(2), 3, RoundingMode.HALF_UP);
        return avgPrice.subtract(medianPrice).abs();
    }
}
