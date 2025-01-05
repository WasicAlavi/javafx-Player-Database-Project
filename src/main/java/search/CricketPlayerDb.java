package search;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CricketPlayerDb {
    private List<Player> players;

    public CricketPlayerDb() {
        players = new ArrayList<>();
    }

    public void loadPlayers(String fileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            String name = parts[0];
            String country = parts[1];
            int age = Integer.parseInt(parts[2]);
            double height = Double.parseDouble(parts[3]);
            String club = parts[4];
            String position = parts[5];
            int number = parts[6].isEmpty() ? -1 : Integer.parseInt(parts[6]);
            int weeklySalary = Integer.parseInt(parts[7]);
            players.add(new Player(name, country, age, height, club, position, number, weeklySalary));
        }
        br.close();
    }

    public void savePlayers(String fileName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (Player player : players) {
            bw.write(player.getName() + "," + player.getCountry() + "," + player.getAge() + "," +
                    player.getHeight() + "," + player.getClub() + "," + player.getPosition() + "," +
                    (player.getNumber() == -1 ? "" : player.getNumber()) + "," + player.getWeeklySalary());
            bw.newLine();
        }
        bw.close();
    }

    public boolean checkName(String name){
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) {
                System.out.println("Player with this name already exists.");
                return true;
            }
        }
        return false;
    }
    public void addPlayer(Player player) {
        players.add(player);
        System.out.println("Player added successfully.");
    }

    public List<Player> searchByName(String name) {
        List<Player> result = new ArrayList<>();
        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(name)) {
                result.add(player);
            }
        }
        return result;
    }

    public List<Player> getAllPlayers() {
        List<Player> result = new ArrayList<>();
        for (Player player : players) {
                result.add(player);
        }
        return result;
    }

    public ArrayList<Player> searchByClub(String club) {
        ArrayList<Player> result = new ArrayList<>();
        for (Player player : players) {
            if (player.getClub().equalsIgnoreCase(club)) {
                result.add(player);
            }
        }
        return result;
    }

    public List<Player> searchByCountryAndClub(String country, String club) {
        List<Player> result = new ArrayList<>();
        for (Player player : players) {
            if (player.getCountry().equalsIgnoreCase(country) && (club.equalsIgnoreCase("ANY") || player.getClub().equalsIgnoreCase(club))) {
                result.add(player);
            }
        }
        return result;
    }

    public List<Player> searchByPosition(String position) {
        List<Player> result = new ArrayList<>();
        for (Player player : players) {
            if (player.getPosition().equalsIgnoreCase(position)) {
                result.add(player);
            }
        }
        return result;
    }

    public List<Player> searchBySalaryRange(double minSalary, double maxSalary) {
        List<Player> result = new ArrayList<>();
        for (Player player : players) {
            if(player.getWeeklySalary() >= minSalary && player.getWeeklySalary() <= maxSalary) {
                result.add(player);
            }
        }
        return result;
    }

    // In CricketPlayerDb class
    public Map<String, Long> countByCountry() {
        Map<String, Long> countryPlayerCount = new HashMap<>();

        // Count players by country
        for (Player player : players) {
            String country = player.getCountry();
            countryPlayerCount.put(country, countryPlayerCount.getOrDefault(country, 0L) + 1);
        }

        return countryPlayerCount; // Return Map instead of StringBuilder
    }



    public List<Player> searchMaxSalaryPlayer(String club) {
        double maxSalary = -1;
        List<Player> result = new ArrayList<>();
        for (Player player : players) {
            if (player.getClub().equalsIgnoreCase(club)) {
                double salary = player.getWeeklySalary();
                if (salary > maxSalary) {
                    maxSalary = salary;
                    result.clear();
                    result.add(player);
                }else if(salary == maxSalary){
                    result.add(player);
                }
            }
        }
        if(result.isEmpty()){
            System.out.println("No such club with this name");
        }
        return result;
    }


    public List<Player> searchMaxAgePlayer(String club) {
        double maxAge= -1;
        List<Player> result = new ArrayList<>();
        for (Player player : players) {
            if (player.getClub().equalsIgnoreCase(club)) {
                double age = player.getAge();
                if (age > maxAge) {
                    maxAge = age;
                    result.clear();
                    result.add(player);
                }else if(age == maxAge){
                    result.add(player);
                }
            }
        }
        if(result.isEmpty()){
            System.out.println("No such club with this name");
        }
        return result;
    }

    public List<Player> searchMaxHeightPlayer(String club) {
        double maxHeight = -1;
        List<Player> result = new ArrayList<>();
        for (Player player : players) {
            if (player.getClub().equalsIgnoreCase(club)) {
                double height = player.getHeight();
                if (height > maxHeight) {
                    maxHeight = height;
                    result.clear();
                    result.add(player);
                }else if(height == maxHeight){
                    result.add(player);
                }
            }
        }
        if(result.isEmpty()){
            System.out.println("No such club with this name");
        }
        return result;
    }


    public double totalYearlySalary(String club){
        double result = 0;
        for (Player player : players) {
            if (player.getClub().equalsIgnoreCase(club)) {
                result += player.getWeeklySalary();
            }
        }
        return result;
    }


    public void setClub(String name, String club) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                player.setClub(club);
            }
        }
    }
}
