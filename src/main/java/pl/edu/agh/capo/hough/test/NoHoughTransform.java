package pl.edu.agh.capo.hough.test;


import pl.edu.agh.capo.hough.HoughTransform;
import pl.edu.agh.capo.hough.common.Line;
import pl.edu.agh.capo.logic.common.Vision;

import java.util.ArrayList;
import java.util.List;

public class NoHoughTransform implements HoughTransform {
    @Override
    public void run(List<Vision> visions) {

    }

    @Override
    public List<Line> getLines(int threshold, int max) {
        return new ArrayList<>();
    }
}
