import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;


/**
 * The Client class represents a graphical client application that connects to a server and interacts with it.
 * This class extends `JFrame` for GUI functionality and implements `Runnable` to run the client in a separate thread.
 * <p>
 * The client communicates with the server using sockets and streams, sending and receiving messages.
 * It includes graphical elements for user interaction, such as buttons for inserting, removing, and searching values
 * in a Binary Search Tree (BST), as well as buttons for different BST traversal types.
 * <p>
 * The client can send requests to the server for modifying the BST or performing different traversals. It receives
 * updates and traversal results from the server and displays them in the console.
 */
public class Client extends JFrame implements Runnable {

    /** Host server for this application */
    private final String host;

    /** Socket to communicate with server */
    private Socket client;

    /** Input stream for receiving data from the server */
    private ObjectInputStream inputStream;

    /** Output stream for sending data to server */
    private ObjectOutputStream outputStream;

    /** To store messages sent from server */
    private String messageFromServer = "";

    /** The main GUI frame */
    private final JFrame frame = new JFrame();

    /** Panel for user input */
    private final JPanel inputFieldPanel = new JPanel();


    /** Text field for user input */
    private final JTextField inputTextField = new JTextField(10);

    /** Text field for user input */
    private final JButton insertButton = new JButton("Insert");

    /** Button for remove action */
    private final JButton removeButton = new JButton("Remove");

    /** Button for search action */
    private final JButton searchButton = new JButton("Search");

    /** Button for in-order traversal */
    private final JButton inOrderButton = new JButton("In-Order Traversal");

    /** Button for pre-order traversal */
    private final JButton preOrderButton = new JButton("Pre-Order Traversal");

    /** Button for post-order traversal */
    private final JButton postOrderButton = new JButton("Post-Order Traversal");


    /**
     * Constructor for Client instance with specified host
     *
     * @param host The host server for the client.
     */
    public Client(String host) {
        // Call constructor of superclass JFrame to set title
        super("Client");

        // Initialize frame components
        setFrame();

        // Set the host server address for the client
        this.host = host;
    }

    /**
     * Configures and sets up the main frame for the Client GUI.
     * Initializes components such as buttons, input fields, and panels.
     */
    private void setFrame() {
        // Set default close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set input text field to be non-editable initially
        inputTextField.setEditable(false);

        // Set buttons
        setButtons();

        // Add components to the input field panel
        inputFieldPanel.add(inputTextField);
        inputFieldPanel.add(insertButton);
        inputFieldPanel.add(removeButton);
        inputFieldPanel.add(searchButton);
        inputFieldPanel.add(inOrderButton);
        inputFieldPanel.add(preOrderButton);
        inputFieldPanel.add(postOrderButton);

        // Add the input field panel to the main frame
        frame.add(inputFieldPanel, BorderLayout.NORTH);

        // Pack the components and set frame's size
        frame.pack();
        frame.setSize(1080, 300);

        // Make frame visible
        frame.setVisible(true);
    }

    /**
     * Configures and sets up action listeners for the buttons in the Client GUI.
     * Defines actions to be performed when specific buttons are clicked.
     */
    private void setButtons() {
        // Action listeners for the input text field
        inputTextField.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Send user input to server and clear the text field
                        sendData(e.getActionCommand());
                        inputTextField.setText("");

                    }
                });

        // Action listener for insert button
        insertButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Get user input, convert to integer, and send insert request to server with given value
                        String userInput = inputTextField.getText();
                        if (!userInput.isEmpty()) {
                            int value = Integer.parseInt(userInput);
                            sendBSTModificationRequest(1, value);
                            inputTextField.setText("");
                        }


                    }

                }
        );

        // Action listener for remove button
        removeButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Get user input, convert to integer, and send remove request to the server
                        String userInput = inputTextField.getText();
                        if (!userInput.isEmpty()) {
                            int value = Integer.parseInt(userInput);
                            sendBSTModificationRequest(2, value);
                            inputTextField.setText("");
                        }
                    }
                }
        );

        // Action listener for search button
        searchButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Get user input, convert to integer, and send search request to the server
                        String userInput = inputTextField.getText();
                        if (!userInput.isEmpty()) {
                            int value = Integer.parseInt(userInput);
                            sendBSTModificationRequest(3, value);
                            inputTextField.setText("");
                        }
                    }
                }
        );

        // Action listener for in-order traversal button
        inOrderButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Request traversal
                        sendTraversalRequest(4);
                    }
                }
        );
        // Action listener for pre-order traversal button
        preOrderButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Request traversal
                        sendTraversalRequest(5);
                    }
                }
        );
        // Action listener for post-order traversal button
        postOrderButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Request traversal
                        sendTraversalRequest(6);
                    }
                }
        );
    }

    /**
     * Establishes a connection to the server, initializes input and output streams,
     * and processes the communication with the server.
     */
    @Override
    public void run() {
        try {
            // Attempt to create a socket and connect to server, display successful connection message
            client = new Socket(InetAddress.getByName(host), 23612);
            System.out.println("Successfully connected to: " + client.getInetAddress().getHostAddress() + "\n");

            // Initialize input and output streams
            getStreams();

            // Process connection with server
            processConnection();

        } catch (UnknownHostException e) {
            // Handle unknown host exception
            System.out.println("Unknown host: " + host);
            e.printStackTrace();

        } catch (IOException e) {
            // Handle exceptions related to streams and establishing successful connection
            System.out.println("Error establishing connection or initializing streams ");
            e.printStackTrace();

        } finally {
            // Close the connection when finished
            closeConnection();
        }
    }

    /**
     * Initializes the input and output streams for communication with the server.
     * This method creates ObjectInputStream and ObjectOutputStream instances using the
     * server's socket InputStream and OutputStream, respectively.
     * Method will be called after a successful connection is established.
     *
     * @throws IOException if input / output error occurs when initializing streams
     */
    private void getStreams() throws IOException {

        // Create ObjectInputStream to read data from the server
        outputStream = new ObjectOutputStream(client.getOutputStream());
        outputStream.flush();   // Flush so that buffered data is sent

        // Create ObjectOutputStream to send data to the client
        inputStream = new ObjectInputStream(client.getInputStream());
    }

    /**
     * Processes the communication with the server after a successful connection.
     * Reads messages from the server and prints update messages sent from server.
     *
     * @throws IOException if an input / output error occurs during communication with the server.
     */
    private void processConnection() throws IOException {

        // Enable the input field to be editable
        setTextFieldEditable();

        do {
            try {

                // Read an object / message from the server
                messageFromServer = (String) inputStream.readObject();

                // Print out update message after performing insert, remove, or search
                if (messageFromServer.startsWith("SERVER>>> BST_UPDATE>>>")) {
                    System.out.println(messageFromServer);

                    // Print out the traversal user requests from server
                } else if (messageFromServer.startsWith("BST_TRAVERSAL>>>")) {
                    System.out.println(messageFromServer);

                    // Handle any other message sent from server
                } else {
                    System.out.println(messageFromServer);
                }

                // Wait for confirmation from the server before proceeding to the next action
                waitForServerConfirmation();

            } catch (ClassNotFoundException e) {
                // Handle the case where an unknown object type is received
                System.out.println("Unknown object type received");
            }
        } while (!messageFromServer.equals("SERVER>>> EXIT_SIGNAL"));
        // Close the connection when the server signals the client to exit (NOTE: Inactive currently)
        closeConnection();
    }

    /**
     * Waits for a confirmation message from the server indicating readiness for the next action.
     * If the confirmation is received, it enables the input text field for user interaction.
     */
    private void waitForServerConfirmation() {
        try {

            // Check if there is data available in the input stream
            if (inputStream.available() > 0) {

                // Read the confirmation message from server
                String confirmation = (String) inputStream.readObject();

                // Check if confirmation indicates server is ready for next action from client
                if (confirmation.equals("SERVER>>> READY_FOR_NEXT_ACTION")) {
                    System.out.println("Server is ready for the next action");

                    // Enable input text field to be editable again
                    setTextFieldEditable();
                }
            }
            // Throw if an input / output error occurs while reading from the server or if the received object's class is not found.
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a request to the server for a binary search tree (BST) modification.
     * The modification choice and associated value are sent as integers to the server.
     * The modification is read by the server and determines which action was requested by client.
     *
     * @param modificationChoice The type of BST modification operation (1 for insert, 2 for remove, 3 for search).
     * @param value              The value associated with the BST modification operation.
     */
    private void sendBSTModificationRequest(int modificationChoice, int value) {
//        setTextFieldEditable(false);

        try {

            // Send modification choice to server
            outputStream.writeInt(modificationChoice);

            // Send the value to be used for BST modification to the server
            outputStream.writeInt(value);

            // Flush to make sure it is sent immediately
            outputStream.flush();

        } catch (IOException e) {
            // Handle an error that occurs while writing the object to the output stream
            System.out.println("Error writing BST modification request");
        }
    }

    /**
     * Sends a traversal request to the server.
     *
     * @param traversalChoice The type of traversal operation to be performed on the server
     *                        (4 for in-order, 5 for pre-order, 6 for post-order).
     */
    private void sendTraversalRequest(int traversalChoice) {
//        setTextFieldEditable(false);

        try {

            // Write the traversal choice to the output stream
            outputStream.writeInt(traversalChoice);

            // Flush to make sure it is sent immediately
            outputStream.flush();

        } catch (IOException e) {
            System.out.println("Error writing BST traversal request");
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param messageFromClient The message to be sent to the server from the client.
     */
    private void sendData(String messageFromClient) {
        try {

            // Write the message to the server using ObjectOutputStream
            outputStream.writeObject("CLIENT>>> " + messageFromClient);

            // Flush to make sure it is sent immediately
            outputStream.flush();

        } catch (IOException e) {
            // Handle IOException if an error occurs while writing the object
            System.out.println("Error writing object");
        }
    }

    /**
     * Sets the input text field to be editable.
     */
    private void setTextFieldEditable() {
        inputTextField.setEditable(true);
    }

    /**
     * Closes the connection to the server, including input and output streams.
     * This method would be called when the client is done interacting with the server.
     */
    private void closeConnection() {

        // Print message indicating disconnection process
        System.out.println("Disconnecting from server");

        try {

            // Close input / output streams and client socket
            outputStream.close();
            inputStream.close();
            client.close();

        } catch (IOException e) {
            // Print the stack trace if an IOException occurs during the disconnection process
            e.printStackTrace();
        }
    }


}
