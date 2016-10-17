package pl.edu.agh.capo.simulation.simulation;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.capo.common.Vision;
import pl.edu.agh.capo.robot.Measure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.*;

public class MeasureFileReader implements Iterator<Measure> {

    private static final Logger logger = LoggerFactory.getLogger(MeasureFileReader.class);

    private Iterator<Measure> measures;

    private final List<Measure> list = new ArrayList<>();

    public MeasureFileReader(InputStream stream) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(stream));
            String line;
/*            line = br.readLine();
            try {
                list.add(createMeasure(line));
            } catch (ParseException e) {
                e.printStackTrace();
            }*/

            while ((line = br.readLine()) != null) {
                try {
                    list.add(createMeasure(line));
                } catch (ParseException e) {
                    logger.error("Wrong measure format", e);
                }
            }
            Collections.sort(list, (m1, m2) -> Long.compare(m1.getDatetime(), m2.getDatetime()));
            measures = list.iterator();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Measure createMeasure(String fileLine) throws ParseException {
        String[] data = fileLine.split(";");
        List<Vision> visions = new ArrayList<>();
        for (int i = 3; i < data.length; i += 2) {
            Vision vision = new Vision(Double.parseDouble(data[i + 1]), Double.parseDouble(data[i]) / 1000);
            visions.add(vision);
        }
        double rightVelocity = Double.parseDouble(data[1]);
        double leftVelocity = Double.parseDouble(data[2]);
        Date date = DateUtils.parseDate(data[0], new String[]{"yyyy-MM-dd HH:mm:ss.SSS"});
        return new Measure(date, rightVelocity, leftVelocity, visions);
    }

    @Override
    public boolean hasNext() {
        return measures.hasNext();
    }

    @Override
    public Measure next() {
        return measures.next();
    }

    public void reset() {
        measures = list.iterator();
    }

}
