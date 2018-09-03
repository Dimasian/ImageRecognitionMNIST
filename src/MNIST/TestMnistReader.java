package MNIST;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dimasian
 */
import static java.lang.Math.min;
import java.util.List;
import MNIST.MnistReader;



public class TestMnistReader {

public static void test() throws RuntimeException {
        String currentDirectory;
        currentDirectory = System.getProperty("user.dir")+"\\resources\\";
        String LABEL_FILE = currentDirectory+"t10k-labels.idx1-ubyte";
        String IMAGE_FILE = currentDirectory+"t10k-images.idx3-ubyte";

        int[] labels = MnistReader.getLabels(LABEL_FILE);
        List<int[][]> images = MnistReader.getImages(IMAGE_FILE);

       
        // Print some images for testing
        for (int i = 0; i < min(3, labels.length); i++) {
                printf("================= LABEL %d\n", labels[i]);
                printf("%s", MnistReader.renderImage(images.get(i)));
        }

}

    public static void printf(String format, Object... args) {
            System.out.printf(format, args);
    }
}
