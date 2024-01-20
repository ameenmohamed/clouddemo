package org.anycompany.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.anycompany.service.SaveBatch;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AnyCompanyModel {
    public static void main(String[] args) {
        PNR pnr = new PNR(
                "PNR1",
                new Booking("BKR1234345", "Business", "tkt123456", "Issued"),
                List.of(
                        new Flight("Emirates", "LHR", LocalDateTime.parse("2023-06-01T18:00:00"),
                                "JFK", LocalDateTime.parse("2023-06-01T10:00:00"),
                                "EI1234", "Confirmed", 120),
                        new Flight("Fly Dubai", "DUB", LocalDateTime.parse("2023-06-01T18:00:00"),
                                "LHR", LocalDateTime.parse("2023-06-01T10:00:00"),
                                "FD1234", "Confirmed", 540)
                ),
                List.of(
                        new Passenger(
                                new Contact("amin@amin.guru", "123456788"),
                                "22/06/76",
                                "male",
                                new Name("Amin", "Asif"),
                                List.of(generateRandomNationality()),
                                generateRandomSpecialServiceRequests()
                        )
                )
        );
        SaveBatch sb = new SaveBatch();
        System.out.println(pnr);
    }

    public static PNR generateRandomPNR() {
        Random random = new Random();

        // Generate random values for PNR fields
        String randomPNR = generateRandomString(6);

        // Generate random values for Booking fields
        String randomBookingReference = generateRandomString(8);
        String randomSeatClass = generateRandomSeatClass();
        String randomTicketNumber = generateRandomString(10);
        String randomTicketStatus = generateRandomTicketStatus();

        Booking booking = new Booking(randomBookingReference, randomSeatClass, randomTicketNumber, randomTicketStatus);

        // Generate random values for Flight fields
        List<Flight> flights = List.of(generateRandomFlight());

        // Generate random values for Passenger fields
        List<Passenger> passengers = generateRandomPassengers(random);

        return new PNR(randomPNR, booking, flights, passengers);
    }

    /*
    {
        "firstName": "Ameen",
        "lastName": "Mohamed",
        "email": "amin@email.com",
        "postcode": "S1W2CXE3",
        "country": "Ireland",
        "state": "Donegal",
        "address": "2222 Crievesmith", Brackenlea",
        "address2": "Letterkenny",
        "passportnumber": "123456789",
        "connectionId": "Fl1HQcYxjoECEag="
    }
    */
    public static PNR generateRandomPNR(String passanger) {
        Random random = new Random();
    
        JsonElement jsonElement = JsonParser.parseString(passanger);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String country = jsonObject.get("country").getAsString();
        LocalDate expirationDate = LocalDate.now().plusYears(random.nextInt(10) + 1);
        String passportNumber = jsonObject.get("passportnumber").getAsString();
                        

        Passenger passenger =new Passenger(
                new Contact(jsonObject.get("email").getAsString(), 
                    "123456788"),
                    "22/06/76",
                    "male",
                    new Name(jsonObject.get("firstName").getAsString(), jsonObject.get("lastName").getAsString()),
                    List.of( new Nationality(country, expirationDate, passportNumber)),
                    generateRandomSpecialServiceRequests()
                );                   

        // Generate random values for PNR fields
        String randomPNR = generateRandomString(6);

        // Generate random values for Booking fields
        String randomBookingReference = generateRandomString(8);
        String randomSeatClass = generateRandomSeatClass();
        String randomTicketNumber = generateRandomString(10);
        String randomTicketStatus = generateRandomTicketStatus();

        Booking booking = new Booking(randomBookingReference, randomSeatClass, randomTicketNumber, randomTicketStatus);

        // Generate random values for Flight fields
        List<Flight> flights = List.of(generateRandomFlight());

        // Generate random values for Passenger fields
        List<Passenger> passengers = List.of(passenger);

        return new PNR(randomPNR, booking, flights, passengers);
    }

    public static Map<String,String> generateRandomBooking(){
        Map<String,String> booking = new HashMap<String,String>();
         // Generate random values for Booking fields
         String randomBookingReference = generateRandomString(8);
         String randomSeatClass = generateRandomSeatClass();
         String randomTicketNumber = generateRandomString(10);
         String randomTicketStatus = generateRandomTicketStatus();
 
         booking.put("bookingReference", randomBookingReference);
         booking.put("seatClass", randomSeatClass);
         booking.put("ticketNumber", randomTicketNumber);
         booking.put("ticketStatus", randomTicketStatus);
         
         return booking;
    
    }


    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder randomString = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            randomString.append(characters.charAt(randomIndex));
        }

        return randomString.toString();
    }

    public static String generateRandomSeatClass() {
        String[] seatClasses = {"Economy", "Business", "First Class"};
        Random random = new Random();
        int randomIndex = random.nextInt(seatClasses.length);
        return seatClasses[randomIndex];
    }

    public static String generateRandomTicketStatus() {
        String[] ticketStatuses = {"Issued", "Confirmed", "Cancelled"};
        Random random = new Random();
        int randomIndex = random.nextInt(ticketStatuses.length);
        return ticketStatuses[randomIndex];
    }

    public static Flight generateRandomFlight() {
        Random random = new Random();

        // Generate random values for Flight fields
        String randomAirline = generateRandomAirline();
        String randomArrivalAirport = generateRandomAirport();
        LocalDateTime randomArrivalDateTime = generateRandomDateTime();
        String randomDepartureAirport = generateRandomAirport();
        LocalDateTime randomDepartureDateTime = generateRandomDateTime();
        String randomFlightNumber = generateRandomFlightNumber();
        String randomStatus = generateRandomStatus();
        int randomTransitDurationInMin = generateRandomTransitDuration();

        return new Flight(randomAirline, randomArrivalAirport, randomArrivalDateTime,
                randomDepartureAirport, randomDepartureDateTime, randomFlightNumber,
                randomStatus, randomTransitDurationInMin);
    }

    public static String generateRandomAirline() {
        String[] airlineNames = {"Emirates", "British Airways", "Lufthansa", "Air France", "Singapore Airlines"};
        Random random = new Random();
        int randomIndex = random.nextInt(airlineNames.length);
        return airlineNames[randomIndex];
    }

    public static String generateRandomAirport() {
        String[] airportCodes = {"LHR", "JFK", "CDG", "SIN", "DXB"};
        Random random = new Random();
        int randomIndex = random.nextInt(airportCodes.length);
        return airportCodes[randomIndex];
    }

    public static LocalDateTime generateRandomDateTime() {
        LocalDateTime now = LocalDateTime.now();
        Random random = new Random();
        int randomDay = random.nextInt(30) + 1;
        int randomHour = random.nextInt(24);
        int randomMinute = random.nextInt(60);
        return now.withDayOfMonth(randomDay).withHour(randomHour).withMinute(randomMinute);
    }

    public static String generateRandomFlightNumber() {
        Random random = new Random();
        int randomDigit1 = random.nextInt(10);
        int randomDigit2 = random.nextInt(10);
        int randomDigit3 = random.nextInt(10);
        int randomDigit4 = random.nextInt(10);
        return "FL" + randomDigit1 + randomDigit2 + randomDigit3 + randomDigit4;
    }

    public static String generateRandomStatus() {
        String[] statuses = {"Scheduled", "Delayed", "On Time", "Cancelled"};
        Random random = new Random();
        int randomIndex = random.nextInt(statuses.length);
        return statuses[randomIndex];
    }

    public static int generateRandomTransitDuration() {
        Random random = new Random();
        return random.nextInt(601) + 60; // Generate a random number between 60 and 660 (minutes)
    }

    public static Nationality generateRandomNationality() {
        String[] countries = {"USA", "Canada", "UK", "Germany", "France"};
        Random random = new Random();
        int randomIndex = random.nextInt(countries.length);
        String randomCountry = countries[randomIndex];
        LocalDate expirationDate = LocalDate.now().plusYears(random.nextInt(10) + 1);
        String passportNumber = generateRandomPassportNumber();
        return new Nationality(randomCountry, expirationDate, passportNumber);
    }

    public static List<Passenger> generateRandomPassengers(Random random) {
        int numPassengers = random.nextInt(4) + 1;  // Randomly generate 1-4 passengers

        return random.ints(numPassengers, 0, 26)
                .mapToObj(i -> {
                    String firstName = generateRandomString(5);
                    String lastName = generateRandomString(8);
                    String email = generateRandomString(10) + "@example.com";
                    String phone = generateRandomString(10);
                    String country = generateRandomString(10);
                    LocalDate expirationDate = LocalDate.now().plusYears(random.nextInt(5));
                    String passportNumber = generateRandomString(8);

                    Contact contact = new Contact(email, phone);
                    Name name = new Name(firstName, lastName);
                    Nationality nationality = new Nationality(country, expirationDate, passportNumber);

                    return new Passenger(contact, passportNumber, passportNumber, name, List.of(nationality), generateRandomSpecialServiceRequests());
                })
                .toList();
    }

   
    public static String generateRandomEmail() {
        String[] domains = {"example.com", "test.com", "domain.com"};
        String[] namePrefixes = {"john", "jane", "jim", "emma", "david"};
        Random random = new Random();
        String randomPrefix = namePrefixes[random.nextInt(namePrefixes.length)];
        String randomDomain = domains[random.nextInt(domains.length)];
        return randomPrefix + "@" + randomDomain;
    }

    public static String generateRandomPhone() {
        Random random = new Random();
        StringBuilder phoneNumber = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            phoneNumber.append(random.nextInt(10));
        }
        return phoneNumber.toString();
    }

    public static LocalDate generateRandomDateOfBirth() {
        Random random = new Random();
        int randomYear = random.nextInt(50) + 1950;
        int randomMonth = random.nextInt(12) + 1;
        int randomDay = random.nextInt(28) + 1;
        return LocalDate.of(randomYear, randomMonth, randomDay);
    }

    public static String generateRandomFirstName() {
        String[] firstNames = {"John", "Jane", "David", "Emma", "James"};
        Random random = new Random();
        int randomIndex = random.nextInt(firstNames.length);
        return firstNames[randomIndex];
    }

    public static String generateRandomLastName() {
        String[] lastNames = {"Smith", "Johnson", "Brown", "Lee", "Taylor"};
        Random random = new Random();
        int randomIndex = random.nextInt(lastNames.length);
        return lastNames[randomIndex];
    }

    public static String generateRandomPassportNumber() {
        StringBuilder passportNumber = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            passportNumber.append(random.nextInt(10));
        }
        return passportNumber.toString();
    }

    public static String generateRandomGender() {
        String[] genders = {"Male", "Female"};
        Random random = new Random();
        int randomIndex = random.nextInt(genders.length);
        return genders[randomIndex];
    }

    public static List<String> generateRandomSpecialServiceRequests() {
        Random random = new Random();
        String[] serviceRequests = {"Wheelchair assistance", "Vegetarian meal", "Diabetic Meal",
                "Extra legroom seat", "Child meal", "Lactose-free meal"};
        int numRequests = random.nextInt(4) + 1; // Randomly generate 1-4 requests

        List<String> specialServiceRequests = new ArrayList<>();
        for (int i = 0; i < numRequests; i++) {
            int randomIndex = random.nextInt(serviceRequests.length);
            specialServiceRequests.add(serviceRequests[randomIndex]);
        }
        return specialServiceRequests;
    }
}
