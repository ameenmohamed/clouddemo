package org.anycompany.model;

import java.util.List;


public record PNR(String pnr, Booking booking, List<Flight> journey, List<Passenger> passengers) {
   
}