import fractals.Equation;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EquationTest {
    @Before
    public void init() {

    }

    @Test
    public void testCreation() {
        final String infix = "x*sin(y+1)";
        final Equation equation = new Equation(infix);

        assertEquals(infix, equation.getExpression());
    }
}
