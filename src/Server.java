import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * The Server class represents a multi-client server that handles requests related to interacting with a shared
 * BinarySearchTree class instance.
 */
public class Server {
    /** Thread pool for handling client connections */
    private ExecutorService executor;

    /** Server socket for accepting client connections */
    private ServerSocket server;

    /** Array to hold individual client instances */
    private SockServer[] sockServer;

    /** Counter for assigning unique IDs for clients */
    private int counter = 1;

    /** Number of currently connected clients */
    private int nClientsActive = 0;

    /** Port on which server listens */
    private final int port = 23612;

    /** Shared binary search tree instance */
    private BinarySearchTree binarySearchTree;

    /**
     * Constructor for the Server class.
     * Initializes necessary components, including the thread pool and the shared Binary Search Tree.
     * The constructor creates an array to hold individual client handler instances,
     * initializes a fixed-size thread pool for handling client connections concurrently,
     * and creates a new instance of the Binary Search Tree for shared data storage.
     */
    public Server() {
        sockServer = new SockServer[100];
        executor = Executors.newFixedThreadPool(100);
        binarySearchTree = new BinarySearchTree();
    }

    /**
     * Starts the server, listens for incoming client connections, and handles client requests.
     * This method initializes a ServerSocket on the specified port, accepts incoming client connections in a loop,
     * and assigns each connection to a new thread for concurrent processing using a fixed-size thread pool.
     * The method runs indefinitely, continuously accepting and handling new client connections.
     */
    public void startServer() {
        try {
            // Initialize the ServerSocket to listen for client connections
            server = new ServerSocket(port, 100);
            System.out.println("Server is running");

            while (true) {
                try {

                    // Create new SockServer instance for the current client connection
                    sockServer[counter] = new SockServer(counter, binarySearchTree);

                    // Wait for a client to connect
                    sockServer[counter].waitForClientConnection();

                    // Increment number of clients connected counter
                    nClientsActive++;

                    // Execute client handler in a separate thread
                    executor.execute(sockServer[counter]);

                } catch (EOFException e) {
                    System.out.println("\nServer terminated connection");
                } finally {
                    ++counter;  // Increment unique ID counter for next client

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inner class representing an individual client handler.
     * Each SockServer instance manages the communication with a single client,
     * handling requests related to the shared Binary Search Tree.
     */
    private class SockServer implements Runnable {
        private ObjectOutputStream outputStream;    // Output stream for sending data to the client
        private ObjectInputStream inputStream;   // Input stream for receiving data from the client
        private Socket clientConnection;    // Socket representing the connection with the client
        private BinarySearchTree binarySearchTree;  // Shared Binary Search Tree instance
        private int myConID;        // Unique ID assigned to the client connection

        /**
         * Constructor for the SockServer class.
         *
         * @param counterIn The unique ID assigned to the client.
         * @param bst       The shared Binary Search Tree.
         */
        public SockServer(int counterIn, BinarySearchTree bst) {
            myConID = counterIn;
            binarySearchTree = bst;
        }

        /**
         * Runs the client handler, processing the initial connection and handling client requests.
         * This method manages a client connection, from establishing streams to
         * handling client requests and closing the connection.
         */
        public void run() {
            try {

                // Establishes input and output streams for communication
                getStreams();

                // Processes the initial connection with the client
                processInitialConnection();

                // Client disconnects when this point is reached, decrement number of connected clients counter
                nClientsActive--;

            } catch (IOException e) {
                // IOException may occur during stream operations or initial connection
                System.out.println("\nServer " + myConID + " terminated connection");
            } finally {
                try {
                    closeConnection();
                } catch (IOException e) {
                    // Exception may occur when attempting to close the connection
                    System.out.println("Failed to properly close connection");
                    e.printStackTrace();
                }
            }
        }

        /**
         * Initializes the input and output streams for communication with the client.
         * This method creates ObjectInputStream and ObjectOutputStream instances using the
         * client's socket InputStream and OutputStream, respectively.
         * It also flushes the output stream to ensure that any buffered data is sent immediately.
         */
        private void getStreams() {
            try {

                // Create ObjectInputStream to read data from the client
                inputStream = new ObjectInputStream(clientConnection.getInputStream());

                // Create ObjectOutputStream to send data to the client
                outputStream = new ObjectOutputStream(clientConnection.getOutputStream());

                // Flush output stream
                outputStream.flush();

            } catch (IOException e) {
                // Handle IOException if an error occurs during the stream initialization
                e.printStackTrace();
            }
        }

        /**
         * Waits for and accepts an incoming client connection on the server socket.
         * This method blocks until a client connects to the server, at which point it
         * accepts the connection and sets the clientConnection field to the connected socket.
         *
         * @throws IOException if an I/O error occurs while waiting for a client connection.
         */
        private void waitForClientConnection() throws IOException {

            // Accept an incoming client connection and set clientConnection to the connected socket
            clientConnection = server.accept();

            // Print information about the connected client's IP address
            System.out.println("Client connected: " + clientConnection.getInetAddress().getHostAddress());

        }

        /**
         * Processes the initial connection with the client by sending a success message
         * and initiating the processing of user input.
         *
         * @throws IOException if an I/O error occurs while processing the initial connection.
         */
        private void processInitialConnection() throws IOException {

            // Create successful connection message for client
            String message = "Connection " + myConID + " successful\n";

            // Send that message
            sendData(message);

            // Begin processing user input from client
            processUserInput();
        }

        /**
         * Processes user input received from the connected client.
         * Processes what to do for every action a client can perform when interacting with the binary search tree.
         */
        private void processUserInput() {

            while (true)
                try {

                    // Read the choice of operation sent from client side
                    int choice = inputStream.readInt();

                    // Print information about client request
                    System.out.println("Client ID " + myConID + " REQ>>> " + choice);

                    switch (choice) {
                        // Insert value
                        case 1:
                            // Read value to be inserted
                            int valueToInsert = inputStream.readInt();

                            // Perform insert on binary search tree instance
                            binarySearchTree.insert(valueToInsert);

                            // Send message to client that confirms server is ready for another action
                            sendData("BST_UPDATE Successfully inserted value: " + valueToInsert);
                            sendConfirmationToClient();
                            break;

                        // Remove value
                        case 2:
                            // Read value to be removed
                            int valueToRemove = inputStream.readInt();

                            // Perform remove, if true value existed in bst, if false it does not exist in bst
                            boolean removed = binarySearchTree.remove(valueToRemove);

                            // If remove was successful, send corresponding message to client
                            if (removed) {
                                sendData("BST_UPDATE Successfully removed value: " + valueToRemove
                                        + "\nNew size of Binary Search Tree: " + binarySearchTree.height());

                                // Else send message that value was not found
                            } else {
                                sendData("BST_UPDATE Value was not found");
                            }

                            // Send message to client that confirms server is ready for another action
                            sendConfirmationToClient();
                            break;

                        // Search for value
                        case 3:
                            // Read value to be searched for
                            int valueToSearchFor = inputStream.readInt();

                            // Perform search, if true value existed in bst, if false it does not exist in bst
                            boolean found = binarySearchTree.search(valueToSearchFor);

                            // If value was found, send corresponding message to client
                            if (found) {
                                sendData("BST_UPDATE Value: " + valueToSearchFor + " was found");

                                // Else send message that value was not found
                            } else {
                                sendData("BST_UPDATE Value: " + valueToSearchFor + " was not found");
                            }

                            // Send message to client that confirms server is ready for another action
                            sendConfirmationToClient();
                            break;

                        // Traverse in order
                        case 4:
                            // Call send traversal method to get the in order traversal from the BST along with sending a message to client
                            sendBSTTraversalToClients(binarySearchTree.inOrderTraversal(), " In-order");

                            // Send message to client that confirms server is ready for another action
                            sendConfirmationToClient();
                            break;

                        // Traverse pre order
                        case 5:
                            // Call send traversal method to get the pre-order traversal from the BST along with sending a message to client
                            sendBSTTraversalToClients(binarySearchTree.preOrderTraversal(), " Pre-order");

                            // Send message to client that confirms server is ready for another action
                            sendConfirmationToClient();
                            break;

                        // Traverse post order
                        case 6:
                            // Call send traversal method to get the post-order traversal from the BST along with sending a message to client
                            sendBSTTraversalToClients(binarySearchTree.postOrderTraversal(), " Post-order");

                            // Send message to client that confirms server is ready for another action
                            sendConfirmationToClient();
                            break;

                        // Send exit signal indicating client wants to quit (NOTE: Inactive currently)
                        case 0:
                            sendData("SERVER>>> EXIT_SIGNAL");
                            break;

                    }
                } catch (IOException e) {
                    System.out.println("Error processing user input");
                    break;
                }

        }

        /**
         * Sends a confirmation message to the connected client, indicating that the server
         * is ready for the next user action.
         */
        private void sendConfirmationToClient() {
            try {
                // Write confirmation message to client
                outputStream.writeObject("SERVER>>> READY_FOR_NEXT_ACTION\n");
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Sends the result of a binary search tree traversal to all connected clients.
         *
         * @param traversalResult The result of the binary search tree traversal.
         * @param traversalType   The type of traversal (e.g., In-order, Pre-order, Post-order).
         */
        private void sendBSTTraversalToClients(List<Integer> traversalResult, String traversalType) {

            // Format the traversal message that sends to client
            String bstUpdate = String.format("BST_TRAVERSAL%s: %s", traversalType, traversalResult);

            // Send the message to connected clients
            sendData(bstUpdate);
        }

        /**
         * Sends a message to the connected client.
         *
         * @param messageToClient The message to be sent to the client.
         */
        private void sendData(String messageToClient) {
            try {

                // Write the message to the client using ObjectOutputStream
                outputStream.writeObject("SERVER>>>" + messageToClient);

                // Flush to make sure it is sent immediately
                outputStream.flush();

            } catch (IOException e) {
                // Handle IOException if an error occurs while writing the object
                System.out.println("Error writing object");
            }
        }

        /**
         * Closes the connection with the current client including input and output streams.
         *
         * @throws IOException if an I/O error occurs while closing the connection.
         */
        private void closeConnection() throws IOException {

            // Display the closing connection information
            System.out.println("\nTerminating connection " + myConID + "\n");
            System.out.println("\nNumber of connections = " + nClientsActive + "\n");

            // Close the input/output streams and the client connection
            outputStream.close();
            inputStream.close();
            clientConnection.close();

        }
    }
}