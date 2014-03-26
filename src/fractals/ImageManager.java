package fractals;

import java.util.ArrayList;

/**
 * ImageManager controls the generation of Fractal images.
 * This class uses Threads to ensure that images are rendered as fast
 * as possible.
 */
public class ImageManager {
    //Defines whether or not the ImageManager will perform filtering of generated Fractals
    private static final boolean PERFORM_FILTERING = true;

    //The list of currently running threads
    private static ArrayList<ImageGeneratorThread> threads = new ArrayList<>();

    // Threaded generator for creating the image
    static class ImageGeneratorThread extends Thread {
        private Fractal f;
        //Defines the maximum number of times an aesthetically unpleasing Fractal can be mutated to attempt to create
        // a more pleasing image
        private static final int MAX_RETRIES = 5;

        public ImageGeneratorThread(Fractal f) {
            this.f = f;
        }

        public void run() {
            try {
                //Generate the image for the Fractal
                f.generateImage();

                //If we should perform filtering make sure the Fractal is not sparse
                if (PERFORM_FILTERING) {
                    //Counts how many times the current Fractal has been mutated
                    int retries;
                    //Try to mutate a sparse Fractal to get a less sparse one. If that fails,
                    //then recreate the Fractal again.
                    do {
                        retries = 0;
                        //If the fractal is aesthetically unpleasing, mutate it
                        while (f.isSparseImage() && retries++ < MAX_RETRIES) {
                            f.discard();
                            f.inPlaceMutate();
                            f.generateImage();
                        }
                        //If the Fractal is still sparse after MAX_RETRIES attempts of mutation,
                        // recreate the Fractal again
                        if (f.isSparseImage()) {
                            f.discard();
                            f.redo();
                            f.generateImage();
                        }
                    } while (f.isSparseImage());
                }

                //Load the image file for the Fractal

                f.loadImage();
            } catch (InterruptedException e) {
                System.err.println("Interrupted");
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
        ImageGeneratorThread thread = new ImageGeneratorThread(f);
        threads.add(thread);
        thread.start();
    }

    /**
     * Halts all of the currently running threads
     */
    public static void interruptThreads() {
        for (ImageGeneratorThread thread : threads) {
            //Ensure that unfinished fractals will restart rendering when they are revisited
            thread.f.isGenerating = false;
            //Kill the thread
            thread.stop();
        }
        threads.clear();
    }
}
