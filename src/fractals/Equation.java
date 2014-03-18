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
    private ArrayList<Node> nodes = new ArrayList<Node>();

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
            Stack<Node> stack = new Stack<Node>();

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
        } catch (UnknownFunctionException e) {
            e.printStackTrace();
        } catch (UnparsableExpressionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Traverses the expression using pre-order to create a list of all Nodes in the expression tree
     */
    public void trace() {
        nodes.clear();
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
        trace(current.getLeft());
        trace(current.getRight());
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

        //Determine if the swap Nodes are the left or right children of their parents in order to update the
        // left/right pointers of the parents
        boolean isLeft, otherIsLeft;
        isLeft = (swap == parent.getLeft());
        otherIsLeft = (swapOther == parentOther.getLeft());
        if (isLeft) {
            parent.setLeft(swapOther);
        }
        else {
            parent.setRight(swapOther);
        }

        if (otherIsLeft) {
            parentOther.setLeft(swap);
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

        printTree(current.getLeft());
        System.out.print(current + " ");
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
     * Evaluates the Equation by plugging in the given values for x, y, and z in the expression tree
     *
     * @param x
     * @param y
     *
     * @return
     *
     * @throws UnknownFunctionException
     * @throws UnparsableExpressionException
     */
    public double evaluate(double x, double y, double z) throws UnknownFunctionException,
            UnparsableExpressionException {
        return evaluateParseTree(root, x, y, z);
    }

    /**
     * Helper method used by evaluate(x,y)
     *
     * @param current
     * @param x
     * @param y
     *
     * @return
     */
    public double evaluateParseTree(Node current, double x, double y, double z) {
        //Don't evaluate non-existent Nodes
        if (current == null) {
            return -1.0;
        }

        //Leaf Nodes can be either variables or constants
        if (current.isLeaf()) {
            if (current.getValue().equals("x")) {
                return x;
            }
            if (current.getValue().equals("y")) {
                return y;
            }
            if (current.getValue().equals("z")) {
                return z;
            }
            return Double.parseDouble(current.getValue());
        }

        //Operators
        switch (current.getValue()) {
            case "+":
                return evaluateParseTree(current.getLeft(), x, y, z)
                        + evaluateParseTree(current.getRight(), x, y, z);
            case "-":
                return evaluateParseTree(current.getLeft(), x, y, z)
                        - evaluateParseTree(current.getRight(), x, y, z);
            case "*":
                return evaluateParseTree(current.getLeft(), x, y, z)
                        * evaluateParseTree(current.getRight(), x, y, z);
            case "/":
                return evaluateParseTree(current.getLeft(), x, y, z)
                        / evaluateParseTree(current.getRight(), x, y, z);
            case "^":
                return Math.pow(evaluateParseTree(current.getLeft(), x, y, z),
                        evaluateParseTree(current.getRight(), x, y, z));
            case "sin":
                return Math.sin(evaluateParseTree(current.getRight(), x, y, z));
            case "cos":
                return Math.cos(evaluateParseTree(current.getRight(), x, y, z));
            case "tan":
                return Math.tan(evaluateParseTree(current.getRight(), x, y, z));
            default:
                return 0.0;
        }
    }

    /**
     * Mutates the current Equation by altering the constants of the expression tree. Each constant in the expression
     * tree is
     * visited and has a random chance of being altered by a set amount.
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
}

/**
 * Represents a single node in the expression tree of an equation
 */
class Node implements Serializable {
    //Represents the value of the node
    private String value;

    //Represents the links for the current node
    private Node left, right, parent;

    //Defines the set of unary operators supported by Node
    private static final HashSet<String> unaryOperators = new HashSet<String>(
            Arrays.asList("sin", "cos", "tan", "abs"));
    //Defines the set of binary operators supported by Node
    private static final HashSet<String> binaryOperators = new HashSet<String>(
            Arrays.asList("*", "+", "-", "/", "^"));

    /**
     * Creates a Node with the given value
     *
     * @param value
     */
    public Node(String value) {
        this.value = value;
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
        return left == null && right == null;
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
        return isUnaryOperator() || isBinaryOperator();
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
        //Clone the left and right subtrees if they exist
        if (left != null) {
            n.left = left.clone();
            n.left.setParent(n);
        }
        if (right != null) {
            n.right = right.clone();
            n.right.setParent(n);
        }
        return n;
    }
}
