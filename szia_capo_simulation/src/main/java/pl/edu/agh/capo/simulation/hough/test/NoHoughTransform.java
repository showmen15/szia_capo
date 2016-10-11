package pl.edu.agh.capo.simulation.hough.test;


import pl.edu.agh.capo.common.Line;
import pl.edu.agh.capo.common.Section;
import pl.edu.agh.capo.hough.HoughTransform;
import pl.edu.agh.capo.robot.Measure;

import java.util.ArrayList;
import java.util.List;

public class NoHoughTransform implements HoughTransform {
    @Override
    public void run(Measure measure, int threshold, int max) {

    }

    @Override
    public List<Line> getLines() {
        return new ArrayList<>();
    }

    @Override
    public List<Section> getSections() {
        return new ArrayList<>();
    }
}
