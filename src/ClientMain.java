import java.io.IOException;
import java.net.InetAddress;

/**
 * The Main class represents a class that initializes a Client instance and runs it.
 */
public class ClientMain {
    /**
     * The main method is the entry point. Creates the instance of the Client class and starts it.
     *
     * @param args Command-line arguments which are not used in this.
     */
    public static void main(String[] args) {
        try {

            // Create new Client instance using local host address
            Client applicationClient = new Client(InetAddress.getLocalHost().getHostAddress());

            // Run the client
            applicationClient.run();

        } catch (IOException e) {
            // Handle exceptions related to an invalid client address or connection
            System.err.println("Invalid client address");
        }
    }
}
