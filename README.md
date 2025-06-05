## Problem Statement
This program addresses the implementation of a simple client-server system using sockets in order to display a binary search tree (BST) to multiple clients concurrently. Each client will be able to interact with the BST to insert, remove, or search for a value in the tree, as well as print out a traversal of the tree in pre, post, and in order. Each client shares the same instance of the BST so changes made by one client gets reflected to all the other clients that connect to the server. 

> **Note**: This project was originally part of a private GitLab repository and has been migrated to GitHub as an independent repository. As a result, the commit history starts from the migration point.

## Developer Documentation
<dl>
<p>
<dt></dt>
<details>
<summary> Client </summary>

The 'Client' class in the program handles the graphical user interface (GUI) and asynchronous communication with the server. The GUI utilizes Swing to create a user-friendly interaction for them to easily interact with the binary search tree (BST). The client was designed to run in a separate thread, hence it implements the Runnable interface. This allows the client to perform tasks concurrently with the main application thread, improving the performance of the program.
The client extends JFrame for GUI functionality. This provided a window to easily manage the GUI components in a straightforward, easy to follow way. Using Action Listeners for the buttons allowed to manage the BST modification operations simply when a client enters input inside the JTextField that is set up. <br>

The Client establishes a connection to the server from the run() method. It first attempts to establish a connection to the server by creating a Socket instance. If connection is successful, it proceeds to initialize the input and output streams.
The getStreams() method is called to initialize the ObjectInputStream and ObjectOutputStream instances. These streams are used for receiving data from and sending data to the server, respectively.
After the streams are initialized, the processConnection() method is called. This method reads messages from the server and handles them based on their content. It uses a loop to continuously read messages until a specific exit signal is received from the server. Unfortunately, I was not able to get this exit signal to work like how I intentionally intended. I was considering adding a separate button for disconnecting from the server inside the GUI, but I ran out of time and had to focus my efforts on different problems. This is one thing I wish I got going, but I will need to come back at a later date. For now some code for this exit signal can still be seen, but the actual implementation and logic, however, are missing. <br>

Inside the processConnection() method, different types of messages from the server are handled. If the message indicates an update from the server, it is printed to the console. If the message is a traversal result, it is also printed. Any other message is handled as a general message. I chose this "SERVER>>>" style in front of the messages because I liked how it looked and gave a distinct look so users can tell easily what the messages are. 

After handling a message, the waitForServerConfirmation() method is called. This method waits for a confirmation message from the server indicating readiness for the next action. If the confirmation is received, it enables the input text field for user interaction. Similarly to the exit signal, I was not able to get this to function exactly like how I wanted it to. During the course of development, I found myself running into issues related to the BST getting updated correctly. To try and resolve this, I wanted to try and implement a method that would set the text field to no longer be editable until the confirmation message from the server was received by the client. The goal for doing was to limit other clients from being able to prematurely modify the BST before the previous action was completed. This was meant to ensure things were able to run smoothly when dealing with concurrent access to the BST. As of now, it is simply just for looks and the client-server program could most likely function without the use of it. I wanted to include it because I thought it just made it look cooler and gave it that feel of somewhat professionalism. <br>

The client sends requests to the server using the sendData(), sendBSTModificationRequest(), and sendTraversalRequest() methods. These methods write the request data to the ObjectOutputStream and flush it to ensure it is sent immediately. I wanted to include the flush() method with the output streams in order to ensure that data was being sent as soon as it was meant to be. I did not want to risk dealing with delays that could have potentially messed with the timing between the client/server waiting and/or receiving messages from each other. <br>

Finally, when the communication with the server is finished (indicated by the receipt of an exit signal which like I mentioned does not actually work the way I wanted it to), the closeConnection() method is called. This method closes the input and output streams and the client socket, effectively ending the connection to the server. Although the exit signal may not function as intended, the server is still able to handle clients disconnecting whenever the GUI is exited without dealing with any sort of performance issues or timing with messages being sent to and back. 
<br>
Exception handling for UnknownHostException and IOException during the connection process and stream initialization is used to ensure the the client is able to handle errors without shutting everything down and giving the user feedback about what might have occurred.

<br><br>

Inline comments are used to explain more in depth about how the Client class functions. These can be found within the source code.

</details>
</p>


<p>
<dt></dt>
<details>
<summary> Server </summary>
The Server class is designed to handle multiple client connections concurrently, and for each client to be able to modify a BST and have traversal methods displayed. This was able to be done with using a fixed thread pool which allows for the multiple SockServer instances to occur simultaneously. Each client handler is executed in a separate thread. This program was not meant for a lot of people to have access to at the same time. I have not been able to fully test anything besides 4 Client instances when developing this program. <br>

When a Server object is created, the constructor initializes the thread pool (executor), the array of client handlers (sockServer), and the shared Binary Search Tree (binarySearchTree). The startServer method is called to start the server. This method initializes the ServerSocket (server) and enters an infinite loop where it waits for client connections. When a client connects, a new SockServer instance is created and assigned to the sockServer array. The waitForClientConnection method of the SockServer instance is then called to wait for the client to connect. <br>
Once the client has connected, the SockServer instance is executed in a separate thread using the executor thread pool. This allows the server to handle multiple client connections concurrently. A message is displayed on the Server side confirming a client connection. The client is able to see the address being displayed of the server they connected to. Each client is assigned a unique identifier. The use for this does not go much further than being able to distinguish which client disconnected. When a client connects the server sends a message to the client letting them know of the successful connection alongside with their ID number that was assigned to them. Each SockServer instance manages the communication with a single client. It establishes input and output streams (getStreams method), processes the initial connection (processInitialConnection method), handles client requests (processUserInput method), and closes the connection when the client disconnects (closeConnection method). <br>
The processUserInput method reads the client's choice of operation and performs the corresponding BST operation. The result of the operation is then sent back to the client using the sendData method. Each modification or traversal is assigned an integer that is used inside a switch statement in order to distinguish which method should be processed by the server. The numbers are as follows:<br>
1. Insert Value
2. Remove Value
3. Search Value
4. In-Order Traversal 
5. Pre-Order Traversal
6. Post-Order Traversal <br>

The results of these operations are sent back to the client using the sendData method. <br><br>

When the client disconnects, the closeConnection method is called to close the input and output streams and the client connection. The goal was to set up the methods inside Server that allowed for an easy way to follow along throughout the process of handling client connections. I tried to limit methods to take care of one or two specific actions in order to help achieve this.
<br><br>

IOException handling is used through out the class in order to handle any potential input / output errors that may arise during the sending of back and forth data to and from the client/server. <br><br>

Inline comments are used to explain more in depth about how the Server class functions. These can be found within the source code.



</details>
</p>

<p>
<dt></dt>
<details>
<summary> Binary Search Tree</summary>
The BinarySearchTree class is an implementation of a binary search tree (BST). The BST is a tree data structure where each node has a value greater than or equal to the values in its left child and less than the values in its right child. <br>
The BST operations (insert, remove, search, and traverse) are implemented recursively. This seemed to be a good fit for this BST implementation, as each operation can be defined in terms of the same operation on a smaller subtree. <br>
The BST supports in-order, pre-order, and post-order traversal. These are standard traversal methods for a tree, and they are implemented using recursion. Each traversal method returns a list of values in the order they were visited. <br>
The BST supports left and right rotations. These operations are useful with balancing the tree, helping to ensure that the tree's height is minimized and that operations on the tree are efficient. <br>
The BST operations are implemented as public methods that call corresponding private recursive methods. The public methods provide a simple interface for interacting with the BST, while the private methods handle the recursive logic. I wanted to do it this way in order to follow good encapsulation practices. The recursive methods require additional parameters for their operation, such as a current node in a tree traversal. These parameters are part of the implementation detail and should ideally stay hidden.
<br>
The Node class is used as a private instance inside the BST class that represents a node in the BST. Each node has a value and references to its left and right children.

</details>
</p>

![image](https://github.com/user-attachments/assets/2eed1f9f-1a47-47a3-8fa2-ed4cf1552520)


</dL>

## JavaDocs
Java Documents are visible using a local server on the machine. You must have at least the doc folder on your local machine. To access them:
1. Open a terminal
2. Navigate to the `doc/` directory
   ```cd path\to\the\project\doc```
3. Start a local server:
   ```python -m http.server 8000```
4. Open your browser and vist:
   ```http://localhost:8000/package-summary.html```
 
[Java Docs can be accessed here!](https://github.com/jarkin0513/Java-Networking/blob/main/doc/package-summary.html) 
 

## User Documentation

### Launching the Program 
   
To start the program, first navigate to the ServerMain.java file and click the run button from your IDE. The run button should look like a green arrow, or something similar to that. <br>
In the console, you will see a message that says the server is now running. <br>
Next, navigate to the ClientMain.java file and start it the same way you did for the ServerMain file. If successful, after running the client file, you will see a message in the client console that says you successfully connected to the server at its IP address. You will also see a message from the server confirming your connection with your connection ID. Similarly, in the server console you should see a message that appeared saying a client connected and their IP address. <br>
Note: This program is meant to handle multiple clients concurrently. So, once you have the server running, you may run the ClientMain file again and again to pull up multiple instances of the client. <br> <br>
***IMPORTANT: YOU MUST START THE SERVER BEFORE RUNNING THE CLIENT***
  
 ### Using the Program 
As soon as you run the client and get a successful connection to the server, you will be presented with a graphical user interface (GUI) that you will use to interact with the binary search tree (BST). You will see six different options to choose from. <br> 
The first one is the insert button. This is what you will use to insert values into the BST. To insert a value, enter an integer in the text field and then click the insert button. If successful you will see a message from the server letting you know in the client console.<br>
The remove button is used to remove values from the BST. In order to remove a value, follow the same instructions as inserting, but instead click the remove button. If a value was actually in the BST, it will be removed and you will see a corresponding message in the client console. If a value you tried to remove was not in the BST, you will see a message in the console letting you know that value was not found. <br>
The search button is used to search for a value in the BST. Follow the same instructions as you did for inserting and removing, but instead click the search button. Very similar to the remove button, you will see messages in the console that will depend on whether or not the value was actually in the BST or not. <br> 
- The In-Order Traversal button, when clicked will display the BST being traversed "in-order" in the client console. 
- The Pre-Order Traversal button, when clicked will display the BST being traversed "pre-order" in the client console. 
- The Post-Order Traversal button, when clicked will display the BST being traversed "post-order" in the client console. 
For all three of these options, the traversal method will be displayed in list form. <br> <br><br>
Like I mentioned previously, this program can handle multiple clients at once. For the purpose of this program each client that connects all shares the same instance of the BST. This means that if you were to run the program make changes to it and then run another client instance and perform one of the traversal methods, for example, you will see the preexisting BST listed out even though you just connected to the server and did not perform any actions yet. When an insertion or removal is performed by one client, it gets reflected for all the other clients that are connected or choose to connect to the server. <br><br>

You will notice messages appear in both the client and server consoles that I did not mention previously in the User Documentation. For the purpose of using and interacting with the BST, these messages are not important to understand. If you care to read more and understand what these messages are, please refer to the Developer Documentation above. <br> <br>

For more information about binary search trees, please refer to this [link](https://en.wikipedia.org/wiki/Binary_search_tree#:~:text=A%20binary%20search%20tree%20is%20a%20rooted%20binary%20tree%20in,to%20A%2C%20satisfying%20the%20binary).


### Exiting the Program 
 

## Source Code
[Click here to view the source code](https://github.com/jarkin0513/Java-Networking/tree/main/src)
