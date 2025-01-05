package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;

public class ServerConnection {
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private static volatile ServerConnection instance; // Thread-safe singleton

    private ServerConnection() {
    }

    // Double-checked locking for thread safety
    public static ServerConnection getInstance() {
        if (instance == null) {
            synchronized (ServerConnection.class) {
                if (instance == null) {
                    instance = new ServerConnection();
                }
            }
        }
        return instance;
    }

    public void setStreams(ObjectOutputStream output, ObjectInputStream input) {
        if (output == null || input == null) {
            throw new IllegalArgumentException("Input and output streams must not be null.");
        }
        this.output = output;
        this.input = input;
        System.out.println("Streams successfully set.");
    }

    public boolean isStreamSet() {
        return output != null && input != null;
    }

    public void writeObject(Object... objects) throws IOException {
        if (output == null) {
            throw new IllegalStateException("Output stream is not set.");
        }
        for (Object obj : objects) {
            try {
                output.writeObject(obj);
                output.flush(); // Ensure data is sent immediately
            } catch (IOException e) {
                System.err.println("Error writing object: " + obj + " - " + e.getMessage());
                throw e; // Rethrow the exception to notify the caller
            }
        }
    }

    public synchronized void writeObjectSynchronized(Object... objects) throws IOException {
        writeObject(objects); // Reuse the existing writeObject method
    }

    public Optional<Object> readObject() {
        if (input == null) {
            throw new IllegalStateException("Input stream is not set.");
        }
        try {
            return Optional.ofNullable(input.readObject());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading object: " + e.getMessage());
            return Optional.empty();
        }
    }

    public synchronized Optional<Object> readObjectSynchronized() {
        return readObject(); // Reuse the existing readObject method
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void close() {
        System.out.println("Closing streams...");
        try {
            if (input != null) {
                input.close();
                input = null;
                System.out.println("Input stream closed.");
            }
        } catch (IOException e) {
            System.err.println("Error closing input stream: " + e.getMessage());
        }
        try {
            if (output != null) {
                output.close();
                output = null;
                System.out.println("Output stream closed.");
            }
        } catch (IOException e) {
            System.err.println("Error closing output stream: " + e.getMessage());
        }
    }
}
