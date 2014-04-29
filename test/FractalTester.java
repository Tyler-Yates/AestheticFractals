import fractals.Equation;
import junit.framework.TestCase;

public class FractalTester extends TestCase {
    private Equation sparseX, sparseY, nonsparseX, nonsparseY;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        nonsparseX = new Equation("sin(-1.4 * y) + cos(-1.4 * x)");
        nonsparseY = new Equation("sin(1.6 * x) + 0.7 * cos(1.6 * y)");
        sparseX = new Equation("1");
        sparseY = new Equation("1");
    }

    public void testRandomRange() {
        double start = -2.0;
        double end = 2.0;
        //Get a lot of random numbers for data points
        int i = 100;
        boolean correct = true;
        while (i-- > 0) {
            double rand = Equation.randomRange(start, end);
            correct &= start <= rand && rand < end;
        }
        assertTrue(correct);
    }
}
