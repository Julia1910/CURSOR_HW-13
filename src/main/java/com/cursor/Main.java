package com.cursor;

public class Main {

    public static void main(String[] args) {
        AirportDB airportDB = AirportDB.getInstance();
        System.out.println(airportDB.getAirportStatistics());
        System.out.println("Max total seats: " + airportDB.getTotalSeats());
    }
}
