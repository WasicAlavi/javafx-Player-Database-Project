# javafx-Player-Database-Project
Cricket Player Database Management

A JavaFX-based application to manage a cricket player database. The project implements a server-client architecture using threads, enabling efficient player management, such as adding, removing, and updating player information.
Features

    Player Database Management: Allows managing player data such as name, age, height, position, country, and more.
    Server-Client Communication: The system uses a multi-threaded server-client model for real-time updates and communication.
    UI with JavaFX: A sleek and modern JavaFX interface to view, search, and manage cricket players.
    Threaded Operations: The server and client communicate using threads to ensure smooth and responsive operations.
    Player Trading: The client can view players available for trade and add or remove them from a "to-sell" list.

    Server-side:
        Start the server to listen for client requests. The server handles all the operations related to the player database, such as adding and removing players, updating the database, and handling the "to-sell" list.
        The server can receive updates and sends information about the current player database to the client.

    Client-side:
        The client allows users to interact with the database. It has the following features:
            View Players: View the current player list with details.
            Search Players: Search for players by name.
            Buy/Sell Players: Add players to the "to-sell" list or buy players from it.
            Refresh: Refresh the player data from the server.

    The client communicates with the server in real-time to reflect changes in the player database.

Code Structure

    Server.java: Contains the server-side logic, including handling client connections, managing the player database, and handling the "to-sell" list.
    Client.java: Contains the client-side logic, including the user interface (JavaFX) and communication with the server.
    Player.java: A model class representing a cricket player, containing details such as name, age, height, position, etc.
    DashboardController.java: The controller for managing the JavaFX dashboard and updating player data.
    Database: The player database, where all player data is stored and managed.
