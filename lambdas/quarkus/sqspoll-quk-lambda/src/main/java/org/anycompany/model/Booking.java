package org.anycompany.model;

public record Booking(String bookingReference, String seatClass, String ticketNumber,
                      String ticketStatus) {
}