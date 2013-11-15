package fractals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import de.congrace.exp4j.ExpressionBuilder;


public class Equation
{
    private String expression;
    private Node root;
    private ArrayList<Node> nodes = new ArrayList<Node>();

    public Equation(String expression)
    {
        this.expression = expression;
        
        try {
            Calculable calc = new ExpressionBuilder(expression).withVariableNames("x","y").build();
            String postfixExpression = calc.getExpression();
            
            String arr[] = postfixExpression.split(" ");
            
            Stack<Node> stack = new Stack<Node>();
            
            for(int i=0; i<arr.length; i++)
            {
                if(arr[i].equals("'"))
                {
                    Node n = stack.pop();
                    n.setValue("-"+n.getValue());
                    stack.push(n);
                    continue;
                }
                
                Node n = new Node(arr[i]);
                nodes.add(n);
                
                if (!n.isOperator()) {
                    stack.push(n);
                } else if(n.isBinaryOperator()){
                    Node right = stack.pop();
                    Node left = stack.pop();
                    
                    n.setLeft(left);
                    n.setRight(right);
                    left.setParent(n);
                    right.setParent(n);
                    
                    stack.push(n);
                } else {
                    Node right = stack.pop();
                    n.setRight(right);
                    right.setParent(n);
                    
                    stack.push(n);
                }
            }
            root = stack.pop();
            System.out.print("Creating new equation: ");
            printTree(root);
            System.out.println();
            trace();
        } catch (UnknownFunctionException e) {
            e.printStackTrace();
        } catch (UnparsableExpressionException e) {
            e.printStackTrace();
        }
    }
    
    public void trace() {
        nodes.clear();
        trace(root);
    }
    
    public void trace(Node current) {
        if (current == null)
            return;
        
        nodes.add(current);
        trace(current.getLeft());
        trace(current.getRight());
    }

    public String getExpression()
    {
        return expression;
    }

    public static Equation generateRandomXEquation()
    {
        double a, c;

        a = randomRange(-2, 2);
        c = randomRange(-2, 2);

        return new Equation("sin( " + a + " * y ) + " + c + " * cos( " + a
                + " * x )");
    }

    public static Equation generateRandomYEquation()
    {
        double b, d;

        b = randomRange(-2, 2);
        d = randomRange(-2, 2);

        return new Equation("sin( " + b + " * y ) + " + d + " * cos( " + b
                + " * x )");
    }

    public Node getRandomNode() {
        return nodes.get((int) (Math.random()*(nodes.size()-1) + 1));
    }
    
    public void cross(Equation other) {
        Node swap = getRandomNode();
        Node swapOther = other.getRandomNode();
        
        Node parent, parentOther;
        parent = swap.getParent();
        parentOther = swapOther.getParent();
        
        boolean isLeft, otherIsLeft;
        isLeft = (swap == parent.getLeft());
        otherIsLeft = (swapOther == parentOther.getLeft());
        
        if (isLeft)
            parent.setLeft(swapOther);
        else
            parent.setRight(swapOther);

        if (otherIsLeft)
            parentOther.setLeft(swap);
        else
            parentOther.setRight(swap);

        swap.setParent(parentOther);
        swapOther.setParent(parent);
        
        swap.setParent(parentOther);
        swapOther.setParent(parent);
        trace();
        updateExpression();
    }
    
    public Equation clone() {
        Equation eq = new Equation(new String(expression));
        eq.root = root.clone();
        eq.trace();
        return eq;
    }
    
    public static double randomRange(double start, double end)
    {
        return Math.random() * (end - start) + start;
    }

    public String toString()
    {
        return expression;
    }
    
    public void updateExpression() {
        expression = updateExpression(root);
    }
    
    private String updateExpression(Node current) {
        if (current==null)
            return "";
        
        if (current.isUnaryOperator()) {
            return current + "(" + updateExpression(current.getRight()) + ")";
        } else {
            return updateExpression(current.getLeft()) 
                    + current
                    + updateExpression(current.getRight());
        }
    }
    
    public void printTree(Node current)
    {
        if(current==null)
            return;
        
        printTree(current.getLeft());
        System.out.print(current+" ");
        printTree(current.getRight());
    }
    
    public String toPostfixString() {
        return toPostfixString(root);
    }
    
    public String toPostfixString(Node current) {
        if (current==null)
            return "";
        
        return toPostfixString(current.getLeft())
                + current + " "
                + toPostfixString(current.getRight());
    }
    
    public double evaluate(double x, double y) throws UnknownFunctionException, UnparsableExpressionException
    {
        return evaluateParseTree(root, x, y);
    }
    
    public double evaluateParseTree(Node current, double x, double y)
    {
        if(current==null)
            return -1.0;
        
        if(current.isLeaf())
        {
            if(current.getValue().equals("x"))
                return x;
            if(current.getValue().equals("y"))
                return y;
            return Double.parseDouble(current.getValue());
        }
        
        switch(current.getValue())
        {
            case "+": return evaluateParseTree(current.getLeft(), x, y) + evaluateParseTree(current.getRight(), x, y);
            case "-": return evaluateParseTree(current.getLeft(), x, y) - evaluateParseTree(current.getRight(), x, y);
            case "*": return evaluateParseTree(current.getLeft(), x, y) * evaluateParseTree(current.getRight(), x, y);
            case "/": return evaluateParseTree(current.getLeft(), x, y) / evaluateParseTree(current.getRight(), x, y);
            case "^": return Math.pow(evaluateParseTree(current.getLeft(), x, y), evaluateParseTree(current.getRight(), x, y));
            case "sin": return Math.sin(evaluateParseTree(current.getRight(), x, y));
            case "cos": return Math.cos(evaluateParseTree(current.getRight(), x, y));
            case "tan": return Math.tan(evaluateParseTree(current.getRight(), x, y));
            default: return 0.0;
        }
    }

    public void mutate()
    {
        mutate(root);
        trace();
        updateExpression();
    }
    
    public void mutate(Node n)
    {
        if (n == null) return;
        
        if(n.isNumber() && Math.random()<0.25)
        {
            double change = randomRange(-0.5,0.5);
            double val = Double.parseDouble(n.getValue())+change;
            val%=2;
            n.setValue(""+val);
        }
        
        mutate(n.getLeft());
        mutate(n.getRight());
    }
}


class Node
{
    private String value;
    private Node left, right, parent;
    
    private static final HashSet<String> unaryOperators = new HashSet<String>(Arrays.asList("sin", "cos", "tan", "abs"));
    private static final HashSet<String> binaryOperators = new HashSet<String>(Arrays.asList("*", "+", "-", "/", "^"));
    
    public Node(String value)
    {
        this.value=value;
    }
    
    public boolean isNumber()
    {
        try {
            Double.parseDouble(value);
        } catch(Exception e) {
            return false;
        } return true;
    }

    public Node getParent()
    {
        return parent;
    }

    public Node setParent(Node parent) {
        Node ans = this.parent;
        this.parent = parent;
        return ans;
    }
    
    public boolean isLeaf()
    {
        return left==null && right==null;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setValue(String value)
    {
        this.value=value;
    }
    
    public Node setLeft(Node newNode)
    {
        Node oldNode=left;
        left=newNode;
        return oldNode;
    }
    
    public boolean isOperator()
    {
        return isUnaryOperator() || isBinaryOperator();
    }
    
    public boolean isUnaryOperator()
    {
        return unaryOperators.contains(value);
    }
    
    public boolean isBinaryOperator()
    {
        return binaryOperators.contains(value);
    }
    
    public Node setRight(Node newNode)
    {
        Node oldNode=right;
        right=newNode;
        return oldNode;
    }
    
    public Node getLeft()
    {
        return left;
    }
    
    public Node getRight()
    {
        return right;
    }
    
    public String toString() {
        return value;
    }
    
    public Node clone() {
        Node n = new Node(value);
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
