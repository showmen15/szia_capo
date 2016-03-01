package pl.edu.agh.capo.hough.jni;

import org.apache.log4j.Logger;
import pl.edu.agh.capo.hough.HoughTransform;
import pl.edu.agh.capo.hough.common.Line;
import pl.edu.agh.capo.logic.common.Vision;
import pl.edu.agh.capo.logic.robot.CapoRobotConstants;
import pl.edu.agh.capo.logic.robot.Measure;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KernelBasedHoughTransform implements HoughTransform {

    private static final Logger logger = Logger.getLogger(JniKernelHough.class);

    private VisionImage visionImage;
    private List<Line> lines;


    @Override
    public void run(Measure measure) {
        visionImage = new VisionImage(feasibleVisions(measure.getVisions()), CapoRobotConstants.VISION_IMAGE_SIZE);
        try {
            lines = new JniKernelHough().kht(visionImage.toByteArray(),
                    CapoRobotConstants.VISION_IMAGE_SIZE,
                    CapoRobotConstants.VISION_IMAGE_SIZE,
                    CapoRobotConstants.KHT_CLUSTER_MIN_SIZE,
                    CapoRobotConstants.KHT_CLUSTER_MIN_DEVIATION,
                    CapoRobotConstants.KHT_DELTA,
                    CapoRobotConstants.KHT_KERNEL_MIN_HEIGHT,
                    CapoRobotConstants.KHT_N_SIGMAS);
            //lines = lines.stream().filter(distinctByKey(Line::getTheta)).collect(Collectors.toList());
            //List<Line> tmp = new ArrayList<>();
            //lines.forEach(line -> tmp.add(new Line(line.getTheta(), line.getRho())));
            //lines = tmp;
            // System.out.println(lines.size());
            // visionImage.writeToFile("withline.jpg");
        } catch (IOException e) {
            logger.error("Could not extraxt lines", e);
        }
    }

    private List<Vision> feasibleVisions(List<Vision> visions) {
        return visions.stream()
                .filter(vision -> vision.getDistance() < CapoRobotConstants.MAX_VISION_DISTANCE)
                .collect(Collectors.toList());

    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Override
    public List<Line> getLines(int threshold, int max) {
        // return new ArrayList<>();
        if (lines.size() > max) {
            return lines.subList(0, max);
        }
        return lines;
    }
}
