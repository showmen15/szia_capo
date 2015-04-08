package pl.edu.agh.capo.logic.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MeasurementReader {

    private static final int READ_JUMP = 1;     //24 for 30 readings

    private Map<Integer, Measure> measurements = new HashMap<Integer, Measure>();

    public MeasurementReader(String filePath) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource(filePath).getFile()));
            String line;
            while ((line = br.readLine()) != null) {
                addMeasurement(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

    private void addMeasurement(String fileLine) {
        String[] data = fileLine.split(";");
        Map<Double, Double> vision = new HashMap<Double, Double>();
        for (int i = 3; i < data.length; i += READ_JUMP * 2){
            vision.put(Double.parseDouble(data[i + 1]), Double.parseDouble(data[i]));
        }
        vision.put(Double.parseDouble(data[data.length - 1]), Double.parseDouble(data[data.length - 2]));
        double leftVelocity = Double.parseDouble(data[1]);
        double rightVelocity = Double.parseDouble(data[2]);
        Measure measure = new Measure(leftVelocity, rightVelocity, vision);
        measurements.put(measurements.size(), measure);
    }

    public Measure getMeasure(int index){
        return measurements.get(index);
    }

    public int getSize(){
        return measurements.size();
    }

    public class Measure{
        private double leftVelocity;
        private double rightVelocity;
        private Map<Double, Double> vision;

        public Measure(double leftVelocity, double rightVelocity, Map<Double, Double> vision) {
            this.leftVelocity = leftVelocity;
            this.rightVelocity = rightVelocity;
            this.vision = vision;
        }

        public double getLeftVelocity() {
            return leftVelocity;
        }

        public double getRightVelocity() {
            return rightVelocity;
        }

        public Map<Double, Double> getVision() {
            return vision;
        }
    }
}
