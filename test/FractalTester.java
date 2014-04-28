import fractals.Equation;
import fractals.Fractal;
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

    public void testNonSparse() {
        Fractal nonSparseFractal = new Fractal(nonsparseX, nonsparseY, new Equation("1"));
        nonSparseFractal.generateImage();
        assertEquals(false, nonSparseFractal.isSparseImage());
    }

    public void testSparse() {
        Fractal nonSparseFractal = new Fractal(sparseX, sparseY, new Equation("1"));
        nonSparseFractal.generateImage();
        assertEquals(true, nonSparseFractal.isSparseImage());
    }
}
