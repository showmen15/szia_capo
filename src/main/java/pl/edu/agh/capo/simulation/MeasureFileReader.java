package pl.edu.agh.capo.simulation;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import pl.edu.agh.capo.logic.common.Vision;
import pl.edu.agh.capo.logic.robot.Measure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class MeasureFileReader implements Iterator<Measure> {

    private static final int READ_JUMP = 24;     //24 for 30 readings
    private static final Logger logger = Logger.getLogger(MeasureFileReader.class);

    private Iterator<Measure> measures;

    private List<Measure> list = new ArrayList<>();

    public MeasureFileReader(String filePath) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource(filePath).getFile()));
            String line;
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
        for (int i = 3; i < data.length; i += READ_JUMP * 2) {
            Vision vision = new Vision(Double.parseDouble(data[i + 1]), Double.parseDouble(data[i]) / 1000);
            visions.add(vision);
        }
        double leftVelocity = Double.parseDouble(data[1]);
        double rightVelocity = Double.parseDouble(data[2]);
        Date date = DateUtils.parseDate(data[0], new String[]{"yyyy-MM-dd HH:mm:ss.SSS"});
        return new Measure(date, rightVelocity, leftVelocity, visions);
    }

    @Override
    public boolean hasNext() {
        return measures.hasNext();
    }

    @Override
    public Measure next() {
        if (!measures.hasNext()) {
            measures = list.iterator();
            return null;
        }
        return measures.next();
    }

}
