package pl.edu.agh.capo.hough.jni;

import pl.edu.agh.capo.hough.common.Line;

import java.util.List;

public class JniKernelHough {

    static {
        System.out.println(Line.class);
        System.loadLibrary("kht-jni");
    }

    public native List<Line> kht(byte[] binary_image, long image_width, long image_height, long cluster_min_size,
                                 double cluster_min_deviation, double delta, double kernel_min_height, double n_sigmas);

}
