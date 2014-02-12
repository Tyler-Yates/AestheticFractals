package fractals;

import java.io.IOException;

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

    public static void renderImage(Fractal f) {
        new ImageGeneratorThread(f).start();
    }
}
