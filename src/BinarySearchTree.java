import java.util.ArrayList;
import java.util.List;

/**
 * The BinarySearchTree class represents an implementation of a binary search tree (BST).
 */
public class BinarySearchTree {

    /** Root of tree */
    private Node root;

    /**
     * Initializes an empty binary search tree.
     * Root is set to null so that the BST is empty at the start.
     */
    public BinarySearchTree() {
        root = null;
    }

    /**
     * Inserts a new value into the BST.
     *
     * @param value The value to be inserted.
     */
    public void insert(int value) {
        root = insertRec(root, value);
    }

    /**
     * Recursively inserts a value into the BST.
     *
     * @param root  The root of the current subtree.
     * @param value The value to be inserted.
     * @return The updated root of the subtree.
     */
    private Node insertRec(Node root, int value) {
        // If root null, empty spot is reached and can insert the new node
        if (root == null) {
            return new Node(value);
        }

        // If value to be inserted is less than value of root, go up left subtree
        if (value < root.value) {
            root.left = insertRec(root.left, value);

            // Value is greater than value of root, go up right subtree
        } else if (value > root.value) {
            root.right = insertRec(root.right, value);
        }

        // Avoid repeats if value is already in tree
        return root;
    }

    /**
     * Recursively removes a value from the BST.
     *
     * @param root  The root of the current subtree.
     * @param value The value to be removed.
     * @return The updated root of the subtree.
     */
    private Node removeRec(Node root, int value) {
        // Tree empty
        if (root == null) {
            return null;
        }

        // If value is less than root value, go left
        if (value < root.value) {
            root.left = removeRec(root.left, value);
            // If value is greater than, go right
        } else if (value > root.value) {
            root.right = removeRec(root.right, value);
            // Else if value is equal to root value, remove node
        } else {
            // If node does not have left child, replace with its right child
            if (root.left == null) {
                return root.right;
                // Node has no right child, replace with lefts child
            } else if (root.right == null) {
                return root.left;
            }

            // If node has two children, replace its value with the max value in left side subtree
            root.value = maxValue(root.left);

            // Remove node that had the max value
            root.left = removeRec(root.left, root.value);
        }

        // Balance tree after removing node
        return balanceRec(root);
    }

    /**
     * Finds the maximum value in a subtree. Method used within removeRec.
     *
     * @param node The root of the subtree.
     * @return The maximum value in the subtree.
     */
    private int maxValue(Node node) {
        int maxValue = node.value;

        // Traverse right subtree until right most node is found
        while (node.right != null) {

            // Update maxValue with the value of the right child of current node
            maxValue = node.right.value;

            // Move to right child of current node
            node = node.right;
        }

        return maxValue;    // Return max value
    }

    /**
     * Removes a value from the BST.
     *
     * @param value The value to be removed.
     * @return The number of nodes in the updated tree.
     */
    public boolean remove(int value) {
        // If value is found in tree, call removeRec to remove node and return true
        if (search(value)) {
            root = removeRec(root, value);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Searches for a value in the BST.
     *
     * @param value The value to search for.
     * @return True if value is found, false if not found.
     */
    public boolean search(int value) {
        return searchRec(root, value);
    }

    /**
     * Recursively searches for a value in the BST.
     *
     * @param root  The root of the current subtree.
     * @param value The value to search for.
     * @return True if value is found, false if not found.
     */
    private boolean searchRec(Node root, int value) {
        // If root null, tree is either empty or have reached leaf node
        if (root == null) {
            return false;
        }

        // If value to search for equal to root value, the value to search for has been found
        if (value == root.value) {
            return true;
        }

        // If value is less than root value, search left subtree
        if (value < root.value) {
            return searchRec(root.left, value);
            // If greater than root value, search right subtree
        } else {
            return searchRec(root.right, value);
        }
    }

    /**
     * Recursively balances the BST by performing rotations.
     * Rotations are necessary in order to maintain that for every node in a BST, all the elements in the left subtree
     * are less than the root and all elements in right subtree are greater than root.
     *
     * @param root The root of the current subtree.
     * @return The updated root of the subtree.
     */
    private Node balanceRec(Node root) {
        // If root null
        if (root == null) {
            return null;
        }

        // Calculate balance factor of current node
        int balance = getBalance(root);

        // If node is left heavy
        if (balance > 1) {

            // If left child left heavy, or balanced do a right rotation
            if (getBalance(root.left) >= 0) {
                return rightRotate(root);
                // If left child right heavy
            } else {
                // Do a left rotation
                root.left = leftRotate(root.left);
                // Do a right rotation
                return rightRotate(root);
            }

            // If node is right heavy
        } else if (balance < -1) {

            // If right child is right heavy or balanced, do a left rotation
            if (getBalance(root.right) <= 0) {
                return leftRotate(root);
                // If left heavy
            } else {
                // Do a right rotation
                root.right = rightRotate(root.right);
                // Do a left rotation
                return leftRotate(root);
            }
        }

        // If node balanced, return node
        return root;
    }

    /**
     * Gets the balance factor of a node.
     * The balance factor is the height of its left child's subtree minus the height of the right child's subtree.
     * If result returned is positive, left subtree is taller.
     * If result returned is negative, right subtree is taller.
     * If result is zero, both subtrees have same height.
     *
     * @param node The node to calculate the balance factor for.
     * @return The balance factor of the node.
     */
    private int getBalance(Node node) {
        if (node == null) {
            return 0;
        }
        return getHeight(node.left) - getHeight(node.right);
    }

    /**
     * Gets the height of a node.
     * Height of a node is the number of edges on the longest path from the node to a leaf.
     * Returns zero if node is null which means it does not exist.
     * If not zero, the height is calculated by taking the maximum height of the left and right subtrees and adding
     * 1.
     * The 1 is for the edge to the parent node.
     *
     * @param node The node to calculate the height for.
     * @return The height of the node.
     */
    private int getHeight(Node node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    /**
     * Public method to retrieve height using private getHeight method.
     *
     * @return Height of tree.
     */
    public int height() {
        return getHeight(root);
    }

    /**
     * Performs a right rotation on a node.
     *
     * @param y The node to be rotated.
     * @return The updated node after the rotation.
     */
    private Node rightRotate(Node y) {
        // Store y's left child in x
        Node x = y.left;

        // Store y's right child in z
        Node z = y.right;

        // Make y right child of x
        x.right = y;

        // Make z left child of y
        y.left = z;

        // Return new root of subtree
        return x;
    }

    /**
     * Performs a left rotation on a node.
     *
     * @param x The node to be rotated.
     * @return The updated node after the rotation.
     */
    private Node leftRotate(Node x) {
        // Store x's right child in y
        Node y = x.right;

        // Store y's left child in z
        Node z = y.left;

        // Make x left child of y
        y.left = x;

        // Make z right child of x
        x.right = z;

        // Return new root of subtree
        return y;
    }

    /**
     * Method to return in-order traversal of BST.
     *
     * @return A list containing the values from in-order traversal.
     */
    public List<Integer> inOrderTraversal() {
        // Initialize list to store traversal result
        List<Integer> result = new ArrayList<>();

        // Begin traversal from root of tree
        inOrderTraversal(root, result);
        return result;
    }

    /**
     * Helper method for in-order traversal of the BST.
     * In-order traversal visits nodes in ascending order. The order goes left child to current node to right child.
     *
     * @param node   The current node being visited.
     * @param result The list to store the traversal results.
     */
    private void inOrderTraversal(Node node, List<Integer> result) {
        // If node is null, does not exist
        if (node == null) {
            return;
        }

        // Do recursive call on left child
        inOrderTraversal(node.left, result);

        // Add current node's value to list
        result.add(node.value);

        // Do recursive call on right child
        inOrderTraversal(node.right, result);
    }

    /**
     * Method to return pre-order traversal of the BST.
     *
     * @return A list containing the values from pre-order traversal.
     */
    public List<Integer> preOrderTraversal() {
        // Initialize list to store traversal result
        List<Integer> result = new ArrayList<>();

        // Begin traversal from root of tree
        preOrderTraversal(root, result);
        return result;
    }

    /**
     * Helper method for pre-order traversal of the BST.
     * Pre-order traversal visits the current node before its children.
     * The order is root to left child to right child.
     *
     * @param node   The current node being visited.
     * @param result The list to store the traversal results.
     */
    private void preOrderTraversal(Node node, List<Integer> result) {
        // If node is null, does not exist
        if (node == null) {
            return;
        }

        // Add current node value to list
        result.add(node.value);

        // Do recursive call on left child
        preOrderTraversal(node.left, result);

        // Do recursive call on right child
        preOrderTraversal(node.right, result);
    }

    /**
     * Perform a post-order traversal of the BST and return the result.
     *
     * @return A list containing the values from post-order traversal.
     */
    public List<Integer> postOrderTraversal() {
        // Initialize list to store traversal result
        List<Integer> result = new ArrayList<>();

        // Begin traversal from root of tree
        postOrderTraversal(root, result);
        return result;
    }

    /**
     * Helper method for post-order traversal of the BST.
     * Post-order traversal order goes left child to right child to root.
     *
     * @param node   The current node being visited.
     * @param result The list to store the traversal results.
     */
    private void postOrderTraversal(Node node, List<Integer> result) {
        // If node is null, does not exist
        if (node == null) {
            return;
        }

        // Do recursive call on left child
        postOrderTraversal(node.left, result);

        // Do recursive call on right child
        postOrderTraversal(node.right, result);

        // Add current node to list after visiting children
        result.add(node.value);
    }

}
