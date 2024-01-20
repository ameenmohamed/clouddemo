package org.anycompany.model;

import java.time.LocalDate;


public record Nationality(String country, LocalDate expirationDate, String passportNumber) {
}