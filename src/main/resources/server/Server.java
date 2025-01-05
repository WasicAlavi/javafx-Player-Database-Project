package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import search.CricketPlayerDb;
import search.Player;

public class Server implements Runnable {
    private ServerSocket server;
    private HashMap<String, String> map;
    private CricketPlayerDb dataBase;
    public static ArrayList<Player> tosell;  // Corrected to ArrayList<Player>
    private ArrayList<ReadClient> connectedClients = new ArrayList<>();



    Server() {
        map = new HashMap<>();
        tosell = new ArrayList<>();  // Initialize as ArrayList
        dataBase = new CricketPlayerDb();
        String filename = "target/classes/players.txt";


        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0];
                String country = data[1];
                int age = Integer.parseInt(data[2]);
                double height = Double.parseDouble(data[3]);
                String club = data[4];
                String position = data[5];
                int number = data[6].isEmpty() ? -1 : Integer.parseInt(data[6]);
                int weeklySalary = Integer.parseInt(data[7]);

                Player p = new Player(name, country, age, height, club, position, number, weeklySalary);
                dataBase.addPlayer(p);
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }

        try {
            server = new ServerSocket(2092);
            System.out.println("Server waiting...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadCredentials();
        Thread t1 = new Thread(this);
        t1.start();
    }

    private void loadCredentials() {
        try (BufferedReader br = new BufferedReader(new FileReader("target/classes/credentials.txt"))) {
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                String[] credentials = line.split(",");
                map.put(credentials[0], credentials[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket s1 = server.accept();
                System.out.println("Server connected...");
                new ReadClient(s1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }

    class ReadClient implements Runnable {
        private Socket sc1;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private String club;

        public ReadClient(Socket s1) {
            sc1 = s1;
            try {
                oos = new ObjectOutputStream(sc1.getOutputStream());
                ois = new ObjectInputStream(sc1.getInputStream());
                synchronized (connectedClients) {
                    connectedClients.add(this); // Register the client
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(this).start();
        }


        @Override
        public void run() {
            try {
                String username = (String) ois.readObject();
                String password = (String) ois.readObject();
                System.out.println("Received credentials - Username: " + username + ", Password: " + password);

                if (map.containsKey(username) && map.get(username).equals(password)) {
                    oos.writeObject("Correct");
                    listenToClientRequest(username);
                } else {

                    oos.writeObject("Incorrect");
                }
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        private void listenToClientRequest(String name) {
            club = name;
            while (true) {
                try {
                    String request;
                    synchronized (ois) { // Synchronize reading from the input stream
                        request = (String) ois.readObject();
                    }

                    System.out.println("Received request: " + request + " from club: " + club);

                    synchronized (this) { // Synchronize processing of the request
                        if ("showPlayer".equals(request)) {
                            handleShowPlayerRequest(oos);
                        } else if ("buyPlayer".equals(request)) {
                            handleBuyPlayerRequest(oos);
                        } else {
                            handleOtherRequests(request, name);
                        }
                    }

                } catch (EOFException e) {
                    System.out.println("Client disconnected: " + club);
                    break; // Exit the loop if the client disconnects
                } catch (Exception e) {
                    System.out.println("Error processing client request.");
                    e.printStackTrace();
                    break; // Exit the loop to prevent infinite retries
                }
            }
        }


        private void handleShowPlayerRequest(ObjectOutputStream oos) throws IOException {
            List<Player> playerList = dataBase.searchByClub(club); // Example retrieval
            if (playerList == null || playerList.isEmpty()) {
                System.out.println("No players found for the club: " + club);
                oos.writeObject("response:playerList");
                oos.writeObject(new Player[0]); // Send an empty array
            } else {
                System.out.println("Sending player list to client: " + playerList.size() + " players.");
                oos.writeObject("response:playerList");
                oos.writeObject(playerList.toArray(new Player[0])); // Send the player list
            }
            oos.flush();
        }



        private void handleBuyPlayerRequest(ObjectOutputStream oos) throws IOException {
            synchronized (tosell) {
                if (tosell.isEmpty()) {
                    System.out.println("Sell list is empty.");
                } else {
                    System.out.println("Sending sell list with " + tosell.size() + " players.");
                }

                oos.writeObject("response:sellList"); // Response type
                oos.writeObject(tosell.toArray(new Player[0])); // The actual sell list
                oos.flush();
            }
        }



        private void broadcastUpdatedPlayerList() {
            synchronized (connectedClients) {
                for (ReadClient client : connectedClients) {
                    try {
                        synchronized (client.oos) {
                            client.oos.writeObject("response:playerListUpdate");
                            List<Player> clubPlayers = dataBase.searchByClub(client.club); // Fetch players for the specific club
                            client.oos.writeObject(clubPlayers.toArray(new Player[0]));
                            client.oos.flush();
                        }
                    } catch (IOException e) {
                        System.err.println("Failed to send player list update to client: " + e.getMessage());
                    }
                }
            }
        }


        private void handleOtherRequests(String request, String clubName) {
            try {
                String[] arr = request.split(",");
                if ("sellPlayer".equals(arr[0])) {
                    if (arr.length > 1) {
                        handleSellPlayerRequest(arr[1], clubName); // Pass the player name
                    } else {
                        System.err.println("Invalid sellPlayer request format: " + request);
                        oos.writeObject("response:error:InvalidSellPlayerRequest");
                        oos.flush();
                    }
                } else if ("confirmBuyPlayer".equals(arr[0])) {
                    if (arr.length > 1) {
                        handleConfirmBuyPlayerRequest(arr[1], clubName);
                    } else {
                        System.err.println("Invalid confirmBuyPlayer request format: " + request);
                        oos.writeObject("response:error:InvalidBuyPlayerRequest");
                        oos.flush();
                    }
                } else {
                    System.err.println("Unknown request type: " + arr[0]);
                    oos.writeObject("response:error:UnknownRequest");
                    oos.flush();
                }
            } catch (IOException e) {
                System.err.println("Error handling request: " + request);
                e.printStackTrace();
            }
        }


        private void handleSellPlayerRequest(String playerName, String clubName) throws IOException {
            System.out.println(clubName + " wants to sell player: " + playerName);
            Player player = dataBase.searchByName(playerName.trim()).get(0);

            if (player != null && player.getClub().equalsIgnoreCase(clubName)) {
                synchronized (tosell) {
                    tosell.add(player);
                    System.out.println(player.getName() + " added to sell list.");
                }
            } else {
                System.err.println("Sell player request failed for: " + playerName);
                oos.writeObject("response:error:SellPlayerFailed");
                oos.flush();
            }
        }

        private void handleConfirmBuyPlayerRequest(String playerName, String clubName) throws IOException {
            System.out.println(clubName + " wants to confirm purchase of: " + playerName);

            synchronized (tosell) {
                for (Player p : tosell) {
                    if (p.getName().equalsIgnoreCase(playerName.trim())) {
                        p.setClub(clubName);
                        dataBase.setClub(p.getName(), clubName);
                        tosell.remove(p);
                        System.out.println(p.getName() + " club updated to " + clubName);
                        break;
                    }
                }
            }

            dataBase.savePlayers("target/classes/players.txt");

            oos.writeObject("response:confirmBuyPlayerConfirmed");
            oos.flush();
            //broadcastUpdatedPlayerList(); // Broadcast updated player list to all clients
        }

    }
}
