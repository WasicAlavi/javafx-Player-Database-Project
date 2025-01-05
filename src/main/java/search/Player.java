package search;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L; // Optional, but helps manage versioning during serialization

    private String name;
    private String country;
    private int age;
    private double height;
    private String club;
    private String position;
    private int number;
    private int weeklySalary;

    // Constructors, getters, and setters

    public Player(String name, String country, int age, double height, String club, String position, int number, int weeklySalary) {
        this.name = name;
        this.country = country;
        this.age = age;
        this.height = height;
        this.club = club;
        this.position = position;
        this.number = number;
        this.weeklySalary = weeklySalary;
    }

    public Player() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getWeeklySalary() {
        return weeklySalary;
    }

    public void setWeeklySalary(int weeklySalary) {
        this.weeklySalary = weeklySalary;
    }

    @Override
    public String toString() {
        if (number == -1) {
            return "--------------------------------------------------" +
                    "\nname = " + name + '\n' +
                    "country = " + country + '\n' +
                    "age = " + age + '\n' +
                    "height = " + height + '\n' +
                    "club = " + club + '\n' +
                    "position = " + position + '\n' +
                    "weeklySalary = " + weeklySalary + '\n' +
                    "--------------------------------------------------";
        } else {
            return "-------------------------------------------------- " +
                    "\nName = " + name + '\n' +
                    "Country = " + country + '\n' +
                    "Age = " + age + '\n' +
                    "Height = " + height + '\n' +
                    "Club = " + club + '\n' +
                    "Position = " + position + '\n' +
                    "Number = " + number + '\n' +
                    "WeeklySalary = " + weeklySalary + '\n' +
                    "--------------------------------------------------";
        }
    }
}
