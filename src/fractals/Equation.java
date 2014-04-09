package fractals;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;

/**
 * Representation of an equation involving x and y. Uses the EXP4J library to convert an infix representation of the
 * equation:
 * x*sin(y+1)
 * into a postfix representation:
 * x sin y 1 + *
 *
 * This postfix expression is easy to convert to a binary expression tree.
 */
public class Equation implements Serializable {
    //Determines the chance of mutating each constant for a call to mutate()
    //0 = no chance to mutate
    //1 = every constant is mutated
    private static final double MUTATE_CHANCE = 0.25;

    //Determines the range in which constants can be altered:
    //If a constant is zero, after mutation it can be in the range:
    //[-MUTATE_RANGE,MUTATE_RANGE]
    private static final double MUTATE_RANGE = 0.5;

    //The infix representation of the expression
    private String expression;
    //The root of the expression tree
    private Node root;
    //The list of all Nodes in the tree
    private ArrayList<Node> nodes = new ArrayList<>();
    //The list of all leaves in the tree
    private ArrayList<Node> leaves = new ArrayList<>();

    /**
     * Creates an Equation Object from a given infix expression.
     *
     * @param expression
     */
    public Equation(String expression) {
        this.expression = expression;

        try {
            //Turn the infix expression into a postfix expression using the EXP4J library
            Calculable calc = new ExpressionBuilder(expression)
                    .withVariableNames("x", "y", "z").build();
            String postfixExpression = calc.getExpression();

            //Get an array of the individual components of the postfix expression
            String arr[] = postfixExpression.split(" ");

            //The stack that will be used to build the expression tree
            Stack<Node> stack = new Stack<>();

            //Loop through each component of the postfix expression. A component can be either a number, variable,
            // or operator
            for (int i = 0; i < arr.length; i++) {
                /*
                EXP4J represents negatives using the apostrophe:
                "-5" becomes ["5", "'"]
                Therefore, if you reach an apostrophe, pop off the last component from the stack and negate it
                to create an expression tree with negatives and not apostrophes.
                 */
                if (arr[i].equals("'")) {
                    Node n = stack.pop();
                    n.setValue("-" + n.getValue());
                    stack.push(n);
                    continue;
                }

                //Create a Node representing the component currently being looked at
                Node n = new Node(arr[i]);

                //If the component is not an operator, we can just push it onto the stack
                if (!n.isOperator()) {
                    stack.push(n);
                }
                else if (n.isTernaryOperator()) {
                    //Ternary operators operators have left, middle, and right children.
                    Node right = stack.pop();
                    Node left = stack.pop();
                    Node middle = stack.pop();

                    n.setLeft(left);
                    n.setRight(right);
                    n.setMiddle(middle);
                    left.setParent(n);
                    right.setParent(n);
                    middle.setParent(n);

                    stack.push(n);
                }
                else if (n.isBinaryOperator()) {
                    //Binary operators (+-*/ ...) have left and right children.
                    Node right = stack.pop();
                    Node left = stack.pop();

                    n.setLeft(left);
                    n.setRight(right);
                    left.setParent(n);
                    right.setParent(n);

                    stack.push(n);
                }
                else {
                    //Unary operators (sin cos tan...) have only one child. We define single children to be the right
                    // child of a Node.
                    Node right = stack.pop();
                    n.setRight(right);
                    right.setParent(n);

                    stack.push(n);
                }
            }
            //The root of the expression tree is the last Node on the stack after all components have been examined
            root = stack.pop();

            /*
            System.out.print("Creating new equation: ");
            printTree(root);
            System.out.println();
            */

            //Create a list of all Nodes in the expression tree
            trace();
        } catch (UnknownFunctionException | UnparsableExpressionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Traverses the expression using pre-order to create a list of all Nodes in the expression tree
     */
    public void trace() {
        //Clear the current lists of Nodes to prevent duplicates
        nodes.clear();
        leaves.clear();
        trace(root);
    }

    /**
     * Helper method used by trace()
     *
     * @param current
     */
    public void trace(Node current) {
        //Stop if we have gone past a leaf
        if (current == null) {
            return;
        }

        nodes.add(current);

        //If the node is a leaf add it to the list of leaves
        if (current.isLeaf()) {
            leaves.add(current);
        }

        //Recursively trace down the left and right children
        trace(current.getLeft());
        trace(current.getRight());
        trace(current.getMiddle());
    }

    /**
     * Returns the infix expression of the Equation
     *
     * @return
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Returns a randomly generated equation in the general form of the Clifford Attractors
     *
     * @return
     */
    public static Equation generateRandomXEquation() {
        double a, c;

        //The ranges for the constants defined by Clifford
        a = randomRange(-2, 2);
        c = randomRange(-2, 2);

        return new Equation("sin( " + a + " * y ) + " + c + " * cos( " + a
                + " * x )");
    }

    /**
     * Returns a randomly generated equation in the general form of the Clifford Attractors
     *
     * @return
     */
    public static Equation generateRandomYEquation() {
        double b, d;

        //The ranges for the constants defined by Clifford
        b = randomRange(-2, 2);
        d = randomRange(-2, 2);

        return new Equation("sin( " + b + " * y ) + " + d + " * cos( " + b
                + " * x )");
    }

    /**
     * Returns a randomly generated equation in the general form of the Clifford Attractors
     *
     * @return
     */
    public static Equation generateRandomZEquation() {
        double a, b;

        //The ranges for the constants defined by Clifford
        a = randomRange(-2, 2);
        b = randomRange(-2, 2);

        return new Equation(a + " * x + " + b + " * y");
    }

    /**
     * Returns a random Node from the expression tree
     *
     * @return
     */
    public Node getRandomNode() {
        return nodes.get((int) (Math.random() * (nodes.size() - 1) + 1));
    }

    /**
     * Crosses the expression trees of the current Equation and the one in the parameter. The original Equations are
     * modified.
     *
     * @param other
     */
    public void cross(Equation other) {
        //Pick random Nodes from both equations to serve as the roots of the cross
        Node swap = getRandomNode();
        Node swapOther = other.getRandomNode();

        //Get the parents of the swap Nodes in order
        Node parent, parentOther;
        parent = swap.getParent();
        parentOther = swapOther.getParent();

        //Determine if the swap Nodes are the left, middle, or right children of their parents in order to update the
        // left/middle/right pointers of the parents
        boolean isLeft, otherIsLeft;
        isLeft = (swap == parent.getLeft());
        otherIsLeft = (swapOther == parentOther.getLeft());
        boolean isMiddle, otherIsMiddle;
        isMiddle = (swap == parent.getMiddle());
        otherIsMiddle = (swapOther == parentOther.getMiddle());
        if (isLeft) {
            parent.setLeft(swapOther);
        }
        else if (isMiddle) {
            parent.setMiddle(swapOther);
        }
        else {
            parent.setRight(swapOther);
        }

        if (otherIsLeft) {
            parentOther.setLeft(swap);
        }
        else if (otherIsMiddle) {
            parentOther.setMiddle(swap);
        }
        else {
            parentOther.setRight(swap);
        }

        //Swap the parents of the two swap Nodes
        swap.setParent(parentOther);
        swapOther.setParent(parent);

        //Update the list of Nodes in the expression tree
        trace();
        //Update the infix expression of the modified expression tree
        updateExpression();
    }

    /**
     * Returns a copy of the current Equation. Modifications to the returned Equation will not affect the original
     * Equation.
     *
     * @return
     */
    public Equation clone() {
        Equation eq = new Equation(new String(expression));
        eq.root = root.clone();
        eq.trace();
        return eq;
    }

    /**
     * Returns a random double in the range [start,end)
     *
     * @param start
     * @param end
     *
     * @return
     */
    public static double randomRange(double start, double end) {
        return Math.random() * (end - start) + start;
    }

    /**
     * Prints out the infix expression for the current Equation
     *
     * @return
     */
    public String toString() {
        return expression;
    }

    /**
     * Updates the infix expression by traversing the expression tree
     */
    public void updateExpression() {
        expression = updateExpression(root);
    }

    /**
     * Helper method used by updateExpression()
     *
     * @param current
     *
     * @return
     */
    private String updateExpression(Node current) {
        //Stop when we have gone past a leaf
        if (current == null) {
            return "";
        }

        //Unary operators are in the form: abc( ... )
        if (current.isUnaryOperator()) {
            return current + "(" + updateExpression(current.getRight()) + ")";
        }
        //Ternary operators are in the form:
        // if (expA) { expB } { expC }
        else if (current.isTernaryOperator()) {
            return current + "(" + updateExpression(current.getMiddle()) + ")" + "{" + updateExpression(current
                    .getLeft()) + "}{" + updateExpression(current.getRight()) + "}";
        }
        else {
            //Binary operators, constants, and variables are in the form ... abc ...
            return updateExpression(current.getLeft()) + current
                    + updateExpression(current.getRight());
        }
    }

    /**
     * Prints an in-order traversal of the expression tree
     *
     * @param current
     */
    public void printTree(Node current) {
        if (current == null) {
            return;
        }

        System.out.print(current + " # ");
        printTree(current.getMiddle());
        System.out.print(" # ");
        printTree(current.getLeft());
        System.out.print(" # ");
        printTree(current.getRight());
    }

	/*public String toPostfixString() {
        return toPostfixString(root);
	}

	public String toPostfixString(Node current) {
		if (current == null)
			return "";

		return toPostfixString(current.getLeft()) + current + " "
				+ toPostfixString(current.getRight());
	}*/

    /**
     * Mutates the current Equation by altering the constants of the expression tree. Each constant in the expression
     * tree is visited and has a random chance of being altered by a set amount.
     */
    public void mutate() {
        mutate(root);
        trace();
        updateExpression();
    }

    /**
     * Helper method used by mutate()
     *
     * @param n
     */
    public void mutate(Node n) {
        //Don't mutate non-existent Nodes
        if (n == null) {
            return;
        }

        //Only mutate constants and only with a random chance
        if (n.isNumber() && Math.random() < MUTATE_CHANCE) {
            //Compute a random amount to alter the constant
            double change = randomRange(-MUTATE_RANGE, MUTATE_RANGE);
            double val = Double.parseDouble(n.getValue()) + change;
            //Make sure the final value does not get too far away from zero as this can produce lots of sparse fractals
            val %= 2;
            //Update the Node's values
            n.setValue("" + val);
        }

        //Keep working down the expression tree to examine all of the constants
        mutate(n.getLeft());
        mutate(n.getRight());
    }

    /**
     * Introduces a new subtree into the expression tree of the current Equation.
     */
    public void introduce() {
        //Pick a random Node to serve as the "root" of the introduced subtree
        Node subtreeRoot = leaves.get((int) (Math.random() * leaves.size()));
        introduce(subtreeRoot);

        //Update the Equation to reflect the new changes
        trace();
        updateExpression();

        System.out.println(expression);
    }

    /**
     * Helper method used by introduce().
     *
     * @param n
     */
    public void introduce(Node n) {
        //Don't perform introduction on non-existent Nodes
        if (n == null) {
            return;
        }

        //Create a new expression tree
        Node subtree = createRandomExpressionTree();

        //Change the given Node into the created subtree
        n.setValue(subtree.getValue());
        n.setLeft(subtree.getLeft());
        n.setRight(subtree.getRight());
        n.setMiddle(subtree.getMiddle());
    }

    /**
     * Creates a random expression tree and returns the root Node of that tree.
     *
     * @return
     */
    private Node createRandomExpressionTree() {
        //Create a random Node to serve as the root of the new expression tree
        Node treeRoot = createRandomNode();
        //Fill out the expression tree
        fillExpressionTree(treeRoot);

        return treeRoot;
    }

    /**
     * Fills out the expression tree rooted at the given Node. This method checks whether the given Node is an operator
     * and if so will create random nodes to serve as the children to that Node. This method will then recursively fill
     * out the expression trees for each of the children until only constants are at the leaves.
     *
     * @param n
     */
    private void fillExpressionTree(Node n) {
        //Don't modify a null Node
        if (n == null) {
            return;
        }

        if (n.isTernaryOperator()) {
            //Create random Nodes for the left, middle, and right children
            Node left = createRandomNode();
            left.setParent(n);
            n.setLeft(left);
            Node right = createRandomNode();
            right.setParent(n);
            n.setRight(right);
            Node middle = createRandomNode();
            middle.setParent(n);
            n.setMiddle(middle);

            //Fill each of the left, middle, and right subtrees
            fillExpressionTree(n.getLeft());
            fillExpressionTree(n.getRight());
            fillExpressionTree(n.getMiddle());
        }
        else if (n.isBinaryOperator()) {
            //Create random Nodes for the left and right children
            Node left = createRandomNode();
            left.setParent(n);
            n.setLeft(left);
            Node right = createRandomNode();
            right.setParent(n);
            n.setRight(right);

            //Fill each of the left and right subtrees
            fillExpressionTree(n.getLeft());
            fillExpressionTree(n.getRight());
        }
        else if (n.isUnaryOperator()) {
            //Unary operators have only a single child which is the right Node
            Node right = createRandomNode();
            right.setParent(n);
            n.setRight(right);
            fillExpressionTree(n.getRight());
        }
        //If the Node is just a constant we no longer need to do any filling
    }

    /**
     * Creates a Node with a random value.
     *
     * @return
     */
    private Node createRandomNode() {
        return new Node(Node.getRandomValue());
    }
}

/**
 * Represents a single node in the expression tree of an equation
 */
class Node implements Serializable {
    //The chance of choosing an operator as a random node
    private static final double OPERATOR_CHANCE = 0.35;

    //Represents the value of the node
    private String value;

    //Represents the links for the current node
    private Node left, right, middle, parent;

    //Defines the set of unary operators supported by Node
    private static final HashSet<String> unaryOperators = new HashSet<>(
            Arrays.asList("sin", "cos", "abs"));
    //Defines the set of binary operators supported by Node
    private static final HashSet<String> binaryOperators = new HashSet<>(
            Arrays.asList("*", "+", "-", "/", "^"));
    //Defines the set of ternary operators supported by Node
    private static final HashSet<String> ternaryOperators = new HashSet<>(Arrays.asList("if"));

    /**
     * Creates a Node with the given value
     *
     * @param value
     */
    public Node(String value) {
        this.value = value;
    }

    /**
     * Returns a random value. This value can be either an operator or a constant.
     *
     * @return
     */
    public static String getRandomValue() {
        //Create a list of all recognized operators
        ArrayList<String> operators = new ArrayList<>();
        operators.addAll(unaryOperators);
        operators.addAll(binaryOperators);
        operators.addAll(ternaryOperators);

        //Create a list of possible leaf values
        ArrayList<String> leaves = new ArrayList<>();
        //A constant between -2 to 2
        leaves.add(""+Equation.randomRange(-2,2));
        //The position
        leaves.add("x");
        leaves.add("y");
        leaves.add("z");
        //The color values
        leaves.add("r");
        leaves.add("b");
        leaves.add("g");

        //Determine if the method will return an operator or a constant
        if (Math.random() < OPERATOR_CHANCE) {
            return operators.get((int) (Math.random() * operators.size()));
        }
        else {
            return "" + leaves.get((int) (Math.random() * leaves.size()));
        }
    }

    /**
     * Returns whether the Node represents a mathematical constant
     *
     * @return
     */
    public boolean isNumber() {
        //Try parsing the value as a double
        try {
            Double.parseDouble(value);
        } catch (Exception e) {
            //Failure to parse the value as a double implies that the value is not a constant
            return false;
        }
        return true;
    }

    /**
     * Returns the parent of the current Node
     *
     * @return
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Sets the parent pointer of the current Node to the given Node. The child pointers of the current Node are not
     * altered.
     * The old parent Node is returned.
     *
     * @param parent
     *
     * @return
     */
    public Node setParent(Node parent) {
        Node ans = this.parent;
        this.parent = parent;
        return ans;
    }

    /**
     * Returns whether the current Node has no children
     *
     * @return
     */
    public boolean isLeaf() {
        return left == null && right == null && middle == null;
    }

    /**
     * Returns the value of the current Node
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the current Node to the given value
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Sets the left child pointer for the current Node to the given Node.
     * Returns the old left child Node.
     *
     * @param newNode
     *
     * @return
     */
    public Node setLeft(Node newNode) {
        Node oldNode = left;
        left = newNode;
        return oldNode;
    }

    /**
     * Returns whether the current Node represents an operator
     *
     * @return
     */
    public boolean isOperator() {
        return isUnaryOperator() || isBinaryOperator() || isTernaryOperator();
    }

    /**
     * Returns whether the current Node represents a unary operator
     *
     * @return
     */
    public boolean isUnaryOperator() {
        return unaryOperators.contains(value);
    }

    /**
     * Returns whether the current Node represents a binary operator
     *
     * @return
     */
    public boolean isBinaryOperator() {
        return binaryOperators.contains(value);
    }

    /**
     * Returns whether the current Node represents a ternary operator
     *
     * @return
     */
    public boolean isTernaryOperator() {
        return ternaryOperators.contains(value);
    }

    /**
     * Sets the right child pointer of the current Node to the given Node.
     * The old right child Node is returned.
     *
     * @param newNode
     *
     * @return
     */
    public Node setRight(Node newNode) {
        Node oldNode = right;
        right = newNode;
        return oldNode;
    }

    /**
     * Sets the middle child pointer of the current Node to the given Node.
     * The old middle child Node is returned.
     *
     * @param newNode
     *
     * @return
     */
    public Node setMiddle(Node newNode) {
        Node oldNode = middle;
        middle = newNode;
        return oldNode;
    }

    /**
     * Returns the left child of the current Node
     *
     * @return
     */
    public Node getLeft() {
        return left;
    }

    /**
     * Returns the right child of the current Node
     *
     * @return
     */
    public Node getRight() {
        return right;
    }

    /**
     * Returns the middle child of the current Node
     *
     * @return
     */
    public Node getMiddle() {
        return middle;
    }

    public String toString() {
        return value;
    }

    /**
     * Returns a cloned copy of the current Node. Any changes made to a cloned copy will not affect the original Node.
     *
     * Cloning a Node will also clone all of its descendants, thus creating a cloned subtree rooted at the current
     * Node.
     *
     * @return
     */
    public Node clone() {
        Node n = new Node(value);
        //Clone the left, middle, and right subtrees if they exist
        if (left != null) {
            n.left = left.clone();
            n.left.setParent(n);
        }
        if (right != null) {
            n.right = right.clone();
            n.right.setParent(n);
        }
        if (middle != null) {
            n.middle = middle.clone();
            n.middle.setParent(n);
        }
        return n;
    }
}
