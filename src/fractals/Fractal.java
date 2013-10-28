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

    static final String IMAGE_PATH = "images" + File.pathSeparator;

    int id = (int) (Math.random() * Integer.MAX_VALUE);

    public Fractal()
    {
        x = Equation.generateRandomXEquation();
        y = Equation.generateRandomYEquation();
        try
        {
            generateImage();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public Fractal(Equation x, Equation y)
    {
        this.x = x;
        this.y = y;
        try
        {
            generateImage();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void generateImage() throws IOException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(new String[] {
                "C-Backend/aesthetics", IMAGE_PATH + id, x.toString(),
                y.toString() });
        processBuilder.start();

        File f = new File(IMAGE_PATH + id + ".ppm");
        ImageInputStream inputStream = new FileImageInputStream(f);
        img = reader.read(inputStream);
    }

    public void drawImage(Graphics g, int x, int y)
    {
        g.drawImage(img, x, y, null);
    }
}
