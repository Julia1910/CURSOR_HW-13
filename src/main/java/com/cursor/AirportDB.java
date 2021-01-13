package com.cursor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirportDB {

    private static final String userName = "postgres";
    private static final String userPassword = "12345678";
    private static final String url = "jdbc:postgresql://localhost/airport";
    public static AirportDB airportDB;

    private AirportDB() {
    }

    public static AirportDB getInstance() {
        if (airportDB == null) {
            airportDB = new AirportDB();
        }
        return airportDB;
    }


    public List<Pilot> getPilots() {
        List<Pilot> pilots = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(url, userName, userPassword);
                Statement statement = connection.createStatement();
        ) {
            ResultSet resultSet;
            String input = "select id_pilot, name, age from pilots";
            resultSet = statement.executeQuery(input);
            while (resultSet.next()) {
                int pilotId = resultSet.getInt(1);
                String pilotName = resultSet.getString(2);
                int pilotAge = resultSet.getInt(3);
                Pilot pilot = new Pilot(pilotId, pilotName, pilotAge);
                pilots.add(pilot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pilots;
    }

    public String getAirportStatistics() {
        return "Airport statistics:\n" + getPlaneStatistics() + getPilotStatistics();
    }

    private String getPlaneStatistics() {
        int i = 0;
        String stingPlanes = "";
        try (
                Connection connection = DriverManager.getConnection(url, userName, userPassword);
                Statement statement = connection.createStatement();
        ) {
            String query =
                    "SELECT model, serial_number, amount FROM airport_data" +
                            " FULL OUTER JOIN planes p on p.id_plane = airport_data.plane_id;";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String model = resultSet.getString(1);
                String serialNumber = resultSet.getString(2);
                int amount = resultSet.getInt(3);
                stingPlanes += " Plane " + model + " " + serialNumber + " - " + amount + "pcs\n";
                i++;
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return "There are " + i + " different models of planes: \n" + stingPlanes;
    }

    private String getPilotStatistics() {
        List<Pilot> pilots = getPilots();
        String stingPilots = "";
        for (Pilot pilot : pilots) {
            stingPilots += " Pilot " + pilot.getName() + " are " + pilot.getAge() + " \n";
        }
        return "There are " + pilots.size() + " pilots:\n" + stingPilots;
    }

    public int getTotalSeats() {
        List<Plane> assignPlanes = getAssignPlanes();
        int n = 0;
        for (Plane plane : assignPlanes) {
            n += plane.getSeats();
        }
        return n;
    }

    private List<Plane> getAssignPlanes() {
        List<Pilot> pilots = getPilots();
        List<Plane> assignedPlanes = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(url, userName, userPassword);
                Statement statement = connection.createStatement();
        ) {
            String query =
                    "SELECT pilot_id, plane_id FROM control";
            ResultSet resultSet = statement.executeQuery(query);
            int i = 0;
            while (resultSet.next()) {
                int pilot_id = resultSet.getInt(1);
                int plane_id = resultSet.getInt(2);
                Pilot pilot = getPilotById(pilot_id);
                if (pilots.contains(pilot)) {
                    if (i < 2) {
                        pilots.remove(pilot);
                        i++;
                    }
                    if (i == 2) {
                        assignedPlanes.add(getPlaneById(plane_id));
                        i = 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignedPlanes;
    }

    private Pilot getPilotById(int id) {
        Pilot pilot = new Pilot();
        try (
                Connection connection = DriverManager.getConnection(url, userName, userPassword);
                Statement statement = connection.createStatement();
        ) {
            String query =
                    "SELECT * FROM pilots WHERE id_pilot = '" + id + "'";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int idPilot = resultSet.getInt(1);
                String name = resultSet.getString(2);
                int age = resultSet.getInt(3);
                pilot.setId(idPilot);
                pilot.setName(name);
                pilot.setAge(age);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pilot;
    }

    private Plane getPlaneById(int id) {
        Plane plane = new Plane();
        try (
                Connection connection = DriverManager.getConnection(url, userName, userPassword);
                Statement statement = connection.createStatement();
        ) {
            String query =
                    "SELECT * FROM planes WHERE id_plane = '" + id + "'";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int idPlane = resultSet.getInt(1);
                String model = resultSet.getString(2);
                String serialNumber = resultSet.getString(3);
                int seats = resultSet.getInt(4);
                plane.setId(idPlane);
                plane.setModel(model);
                plane.setSerialNumber(serialNumber);
                plane.setSeats(seats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plane;
    }
}

