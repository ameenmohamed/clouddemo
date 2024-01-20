package org.anycompany.model;

import java.util.List;

public record Passenger(Contact contact, String dateOfBirth, String gender, Name name,
                        List<Nationality> nationality, List<String> specialServiceRequests) {
}