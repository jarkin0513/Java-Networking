/**
 * The Main class represents a class that initializes a Server instance and starts the server.
 */
public class ServerMain {
    /**
     * The main method is the entry point. Creates the instance of the Server class and starts it.
     *
     * @param args Command-line arguments which are not used in this.
     */
    public static void main(String[] args) {

        // Create and start the server instance, listening on the given port
        Server server = new Server();
        server.startServer();

    }
}