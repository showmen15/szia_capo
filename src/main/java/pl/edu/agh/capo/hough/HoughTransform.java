package pl.edu.agh.capo.hough;

import pl.edu.agh.capo.hough.common.Line;
import pl.edu.agh.capo.logic.common.Vision;

import java.util.List;

public interface HoughTransform {
    void run(List<Vision> visions);

    List<Line> getLines(int threshold, int max);
}
