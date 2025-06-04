/**
 * The Node class represents a node in a binary search tree.
 * Each node has a value and two children, to the left and right of it.
 */
public class Node {

    /** Value stored in the node */
    int value;

    /** Left child of node */
    Node left;

    /** Right child of node */
    Node right;

    /**
     * Constructor for Node class.
     * Constructs a new Node with the given value.
     * The left and right children are initially set to be null.
     *
     * @param value The value to store in the node.
     */
    public Node(int value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}
