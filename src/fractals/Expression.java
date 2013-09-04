package fractals;

import java.util.Stack;

public class Expression 
{
	String postfixExpression;
	
	public Expression(String infixExpression)
	{
		postfixExpression = posterizeInfixExpression(infixExpression);
	}
	
	private String posterizeInfixExpression(String infixExpression) 
	{
		infixExpression = infixExpression.replaceAll(" ", "");
		
		Stack<String> postfixStack = new Stack<String>();
		
		for(int i = 0; i < infixExpression.length(); i++)
		{
			
		}
		
		return null;
	}

	public int evaluate(double x, double y)
	{
		return 0;
	}
}
