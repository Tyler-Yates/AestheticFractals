package fractals;

import graphics.PPMImageReader;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

public class Fractal
{
    Equation x, y;
    BufferedImage img;
    static final PPMImageReader reader = new PPMImageReader(null);

    static final String IMAGE_PATH = "";

    String id = "Fractal_"+((int)(Math.random() * Integer.MAX_VALUE));

    public Fractal()
    {
        x = Equation.generateRandomXEquation();
        y = Equation.generateRandomYEquation();
    }

    public Fractal(Equation x, Equation y)
    {
        this.x = x;
        this.y = y;
    }

    public void generateImage() throws IOException, InterruptedException
    {
        System.out.println(x.toString()+"\t|\t"+y.toString());
        ProcessBuilder processBuilder = new ProcessBuilder(new String[] {
                "C-Backend/aesthetics","-s","200","100", IMAGE_PATH + id, x.toString(),
                y.toString() });
        Process p = processBuilder.start();
        p.waitFor();

        File f = new File(IMAGE_PATH + id + ".ppm");
        ImageInputStream inputStream = new FileImageInputStream(f);
        img = reader.read(inputStream);
    }

    public void drawImage(Graphics g, int x, int y)
    {
        g.drawImage(img, x, y, null);
    }
}
