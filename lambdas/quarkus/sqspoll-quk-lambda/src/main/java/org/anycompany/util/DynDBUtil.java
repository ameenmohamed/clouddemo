package org.anycompany.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.anycompany.model.Flight;
import org.anycompany.model.Nationality;
import org.anycompany.model.Passenger;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynDBUtil {

    //Flight(String airline, String arrivalAirport, LocalDateTime arrivalDateTime,
    //String departureAirport, LocalDateTime departureDateTime, String flightNumber,
    //String status, int transitDurationInMin) 

    public static List<AttributeValue> getDynFlights(List<Flight> journey){
        List<AttributeValue> dynFlightList = new ArrayList<AttributeValue>();
        Map<String, AttributeValue> flightMap = new HashMap<>();
        for (Flight flight : journey) { 
            flightMap.put("airline", AttributeValue.fromS(flight.airline()));
            flightMap.put("arrivalAirport", AttributeValue.fromS(flight.arrivalAirport()));
         
            flightMap.put("arrivalDateTime", AttributeValue.fromS(String.valueOf(flight.arrivalDateTime())));
            flightMap.put("departureAirport", AttributeValue.fromS(String.valueOf(flight.departureAirport())));
            flightMap.put("departureDateTime", AttributeValue.fromS(String.valueOf(flight.departureDateTime())));
            flightMap.put("flightNumber", AttributeValue.fromS(flight.flightNumber())); 
            flightMap.put("status", AttributeValue.fromS(flight.status()));
            flightMap.put("transitDurationInMin", AttributeValue.fromS(String.valueOf(flight.transitDurationInMin())));
        }
        dynFlightList.add(AttributeValue.fromM(flightMap));
      
        return dynFlightList;
    }


    public static List<AttributeValue> getDynPassengers(List<Passenger> _passenger){  
        List<AttributeValue> dynPassengerList = new ArrayList<AttributeValue>();
        Map<String, AttributeValue> passengerMap = new HashMap<>();

        for (Passenger passenger : _passenger) {
            Map<String, AttributeValue> passNameMap = new HashMap<>();
                passNameMap.put("firstName", AttributeValue.fromS(passenger.name().firstName()));
                passNameMap.put("lastName", AttributeValue.fromS(passenger.name().lastName()));
            passengerMap.put("name", AttributeValue.fromM(passNameMap));
            passengerMap.put("contact", AttributeValue.fromS(String.valueOf(passenger.contact())));
            passengerMap.put("dateOfBirth", AttributeValue.fromS(String.valueOf(passenger.dateOfBirth())));
            passengerMap.put("gender", AttributeValue.fromS(String.valueOf(passenger.gender())));

            List<AttributeValue> dynNationalityList = new ArrayList<AttributeValue>();
                List<Nationality> nationality =passenger.nationality();
                for (Nationality eachNationality : nationality) {
                    Map<String, AttributeValue> passNationalityMap = new HashMap<>();
                    passNationalityMap.put("country", AttributeValue.fromS(eachNationality.country()));
                    passNationalityMap.put("expirationDate", AttributeValue.fromS(String.valueOf(eachNationality.expirationDate())));
                    passNationalityMap.put("passportNumber", AttributeValue.fromS(eachNationality.passportNumber()));
                    dynNationalityList.add(AttributeValue.fromM(passNationalityMap));
                }
            passengerMap.put("nationality", AttributeValue.fromL(dynNationalityList));
            passengerMap.put("specialServiceRequests", AttributeValue.fromL(getDynSpecialRequests(passenger.specialServiceRequests())));
        }
        dynPassengerList.add(AttributeValue.fromM(passengerMap));
        return dynPassengerList;
    }
    
    public static List<AttributeValue> getDynSpecialRequests(List<String> spRequests){     
        List<AttributeValue> dynSpecialRequestList = new ArrayList<AttributeValue>();     
        for (String spReq : spRequests) {
            dynSpecialRequestList.add(AttributeValue.fromS(spReq));
        }
        return dynSpecialRequestList;
    }
   
    
}
