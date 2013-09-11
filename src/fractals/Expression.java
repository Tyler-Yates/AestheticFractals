package fractals;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class Expression 
{
	Node root;
	
	public Expression(String infixExpression)
	{
		try {
			Calculable calc = new ExpressionBuilder(infixExpression).withVariableNames("x","y").build();
			String postfixExpression = calc.getExpression();
			
			String arr[] = postfixExpression.split(" ");
			
			Stack<Node> stack = new Stack<Node>();
			
			for(int i=0; i<arr.length; i++)
			{
				Node n = new Node(arr[i]);
				if (!n.isOperator()) {
					stack.push(n);
				} else if(n.isBinaryOperator()){
					Node right = stack.pop();
					Node left = stack.pop();
					
					n.setLeft(left);
					n.setRight(right);
					
					stack.push(n);
				} else {
					Node right = stack.pop();
					n.setRight(right);
					stack.push(n);
				}
			}
			root = stack.pop();
			printTree(root);
			System.out.println();
			
		} catch (UnknownFunctionException e) {
			e.printStackTrace();
		} catch (UnparsableExpressionException e) {
			e.printStackTrace();
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
}

class Node
{
	private String value;
	private Node left, right;
	
	private static final HashSet<String> unaryOperators = new HashSet<String>(Arrays.asList("sin", "cos", "tan", "abs"));
	private static final HashSet<String> binaryOperators = new HashSet<String>(Arrays.asList("*", "+", "-", "/", "^"));
	
	public Node(String value)
	{
		this.value=value;
	}
	
	public boolean isLeaf()
	{
		return left==null && right==null;
	}
	
	public String getValue()
	{
		return value;
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
}