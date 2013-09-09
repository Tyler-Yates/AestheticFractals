package fractals;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class Expression 
{
	String infixExpression;
	
	public Expression(String infixExpression)
	{
		this.infixExpression = infixExpression;
	}
	
	public double evaluate(double x, double y) throws UnknownFunctionException, UnparsableExpressionException
	{
		Calculable calc = new ExpressionBuilder(infixExpression)
        .withVariable("x", x)
        .withVariable("y", y)
        .build();
		
		double result = calc.calculate();

		return result;
	}
}