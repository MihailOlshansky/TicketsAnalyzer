package org.example;

import org.example.ticket.TicketAnalyzer;

import java.util.Map;

public class Main {
    private final static String FILE_PATH = "/src/main/resources/tickets.json";
    private final static String ORIGIN = "VVO";
    private final static String DESTINATION = "TLV";
    public static void main(String[] args) {
        TicketAnalyzer ticketAnalyzer;
        try {
            ticketAnalyzer = new TicketAnalyzer(System.getProperty("user.dir") + FILE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Map<String, Integer> minTravelDurations = ticketAnalyzer.minTravelDuration(ORIGIN, DESTINATION);
        System.out.println("Minimum travel durations:");
        minTravelDurations.forEach((key, value) -> System.out.printf("  %s: %dh %dm%n", key, value / 60, value % 60));
        System.out.printf("Average and median prices difference: %s%n", ticketAnalyzer.avgAndMedianDifference(ORIGIN, DESTINATION));
    }
}