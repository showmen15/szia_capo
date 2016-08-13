package pl.edu.agh.capo.hough.jni;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.capo.hough.common.Line;

import java.util.List;

public class JniKernelHough {
    private static final Logger logger = LoggerFactory.getLogger(JniKernelHough.class);

    static {
        try {
            System.loadLibrary("kht-jni");
            logger.info("Loaded kth-jni library");
        } catch (UnsatisfiedLinkError error) {
            logger.error("Could not load kth-jni library, did you run make.exe --file=Makefile.win inside lib directory?", error);
            System.exit(-1);
        }
    }

    public native List<Line> kht(byte[] binary_image, long image_width, long image_height, long cluster_min_size,
                                 double cluster_min_deviation, double delta, double kernel_min_height, double n_sigmas);

}
