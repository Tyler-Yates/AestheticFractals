package fractals;

public class Equation
{
    private String expression;

    public Equation(String expression)
    {
        this.expression = expression;
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

    public static double randomRange(double start, double end)
    {
        return Math.random() * (end - start) + start;
    }

    public String toString()
    {
        return expression;
    }
}
