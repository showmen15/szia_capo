package pl.edu.agh.capo.hough;

import pl.edu.agh.capo.hough.common.Line;
import pl.edu.agh.capo.logic.robot.Measure;

import java.util.List;

public interface HoughTransform {
    void run(Measure measure, int threshold, int max);

    List<Line> getLines();
}
