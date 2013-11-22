package fractals;

import graphics.GraphicalInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class ImageManager {
	// Threaded generator for creating the image
	static class ImageGeneratorThread extends Thread {
		Fractal f;
		int index;

		public ImageGeneratorThread(Fractal f, int index) {
			this.f = f;
			this.index = index;
		}

		public void run() {
			try {
				f.generateImage();
				imagesReady.set(index, 1);
				if (GraphicalInterface.frame != null)
					GraphicalInterface.frame.getContentPane().repaint();
			} catch (IOException | InterruptedException e) {
				imagesReady.set(index, -1);
				e.printStackTrace();
			}
		}
	}

	static AtomicIntegerArray imagesReady = new AtomicIntegerArray(9);

	public static void generateNewImages(ArrayList<Fractal> fractals) {
		for (int i = 0; i < imagesReady.length(); i++) {
			imagesReady.set(i, 0);
		}
		for (int i = 0; i < fractals.size() && i < 9; i++) {
			ImageGeneratorThread thread = new ImageGeneratorThread(
					fractals.get(i), i);
			thread.start();
		}
	}

	public static boolean indexIsReady(int i) {
		return imagesReady.get(i) == 1;
	}
}
