package fractals;

import graphics.GraphicalInterface;
import graphics.PPMImageReader;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

public class Fractal {
	Equation x, y;
	BufferedImage img;
	static final PPMImageReader reader = new PPMImageReader(null);

	static {
		try {
			Files.createDirectories(Paths.get(("images")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static final String IMAGE_PATH = "images" + File.separator;

	String id = "Fractal_" + ((int) (Math.random() * Integer.MAX_VALUE));

	public Fractal() {
		x = Equation.generateRandomXEquation();
		y = Equation.generateRandomYEquation();
	}

	public Fractal(Equation x, Equation y) {
		this.x = x;
		this.y = y;
	}

	public Fractal cross(Fractal f) {
		Equation cloneX = x.clone();
		Equation cloneY = y.clone();
		Equation otherCloneX = f.x.clone();
		Equation otherCloneY = f.y.clone();

		cloneX.cross(otherCloneX);
		cloneY.cross(otherCloneY);

		return new Fractal(cloneX, cloneY);
	}

	public Fractal mutate() {
		Equation cloneX = x.clone();
		Equation cloneY = y.clone();
		cloneX.mutate();
		cloneY.mutate();

		return new Fractal(cloneX, cloneY);
	}

	public Fractal clone() {
		return new Fractal(x.clone(), y.clone());
	}

	public void generateImage() throws IOException, InterruptedException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		ProcessBuilder processBuilder = new ProcessBuilder(new String[] {
                        "C-Backend/aesthetics", "-save", "-p", "100000", "-s",
                        "" + screenSize.getHeight() / 3,
                        "" + screenSize.getWidth() / 3, IMAGE_PATH + id, x.toString(),
                        y.toString() });
		Process p = processBuilder.start();
		p.waitFor();
		p.destroy();

		File f = new File(IMAGE_PATH + id + ".ppm");
		ImageInputStream inputStream = new FileImageInputStream(f);
		img = reader.read(inputStream);
		inputStream.close();
		f.delete();
	}

	public void drawImage(Graphics g, int x, int y) {
		g.drawImage(img, x, y, GraphicalInterface.frame.getWidth() / 3,
				GraphicalInterface.frame.getHeight() / 3,
				GraphicalInterface.frame);
	}
}
