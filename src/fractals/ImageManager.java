package fractals;

import java.io.IOException;

/**
 * ImageManager controls the generation of Fractal images.
 * This class uses Threads to ensure that images are rendered as fast
 * as possible.
 */
public class ImageManager {
	// Threaded generator for creating the image
	static class ImageGeneratorThread extends Thread {
		Fractal f;

		public ImageGeneratorThread(Fractal f) {
			this.f = f;
		}

		public void run() {
			try {
				f.generateImage();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * Renders the image of the Fractal f. This method calls f's own generateImage() method
     * but in a threaded fashion.
     * @param f
     */
    public static void renderImage(Fractal f) {
        new ImageGeneratorThread(f).start();
    }
}
