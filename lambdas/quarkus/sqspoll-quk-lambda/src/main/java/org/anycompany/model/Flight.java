package org.anycompany.model;

import java.time.LocalDateTime;


public record Flight(String airline, String arrivalAirport, LocalDateTime arrivalDateTime,
                     String departureAirport, LocalDateTime departureDateTime, String flightNumber,
                     String status, int transitDurationInMin) {
}