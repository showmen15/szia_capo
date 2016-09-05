package pl.edu.agh.capo.hough.jni;

import org.slf4j.LoggerFactory;
import pl.edu.agh.capo.common.Line;

import java.util.List;

public class JniKernelHough {

    static {
        try {
            System.loadLibrary("kht-jni");
            LoggerFactory.getLogger(JniKernelHough.class).info("Loaded kth-jni library");
        } catch (UnsatisfiedLinkError error) {
            LoggerFactory.getLogger(JniKernelHough.class).error("Could not load kth-jni library, did you run make.exe --file=Makefile.win inside lib directory?", error);
            System.exit(-1);
        }
    }

    public native List<Line> kht(byte[] binary_image, long image_width, long image_height, long cluster_min_size,
                                 double cluster_min_deviation, double delta, double kernel_min_height, double n_sigmas);

}
