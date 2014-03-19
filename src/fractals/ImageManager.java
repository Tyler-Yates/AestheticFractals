package fractals;

/**
 * ImageManager controls the generation of Fractal images.
 * This class uses Threads to ensure that images are rendered as fast
 * as possible.
 */
public class ImageManager {
    // Threaded generator for creating the image
    static class ImageGeneratorThread extends Thread {
        private Fractal f;
        //Defines the maximum number of times an aesthetically unpleasing Fractal can be mutated to attempt to create
        // a more pleasing image
        private static final int MAX_RETRIES = 8;

        public ImageGeneratorThread(Fractal f) {
            this.f = f;
        }

        public void run() {
            f.generateImage();
            int retries = 0;

            //Try to mutate a sparse Fractal to get a less sparse one. If that fails, then recreate the Fractal again.
            do {
                //If the fractal is aesthetically unpleasing, mutate it
                while (f.isSparseImage() && retries++ < MAX_RETRIES) {
                    f.discard();
                    f.inPlaceMutate();
                    f.generateImage();
                }
                //If the Fractal is still sparse after MAX_RETRIES attempts of mutation, recreate the Fractal again
                if (f.isSparseImage()) {
                    f.discard();
                    f.redo();
                    f.generateImage();
                }
            } while (f.isSparseImage());

            //Load the image file for the Fractal
            try {
                f.loadImage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Renders the image of the Fractal f. This method calls f's own generateImage() method
     * but in a threaded fashion.
     *
     * @param f
     */
    public static void renderImage(Fractal f) {
        new ImageGeneratorThread(f).start();
    }
}
