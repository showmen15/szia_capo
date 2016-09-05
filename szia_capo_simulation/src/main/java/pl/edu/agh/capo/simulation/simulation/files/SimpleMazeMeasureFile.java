package pl.edu.agh.capo.simulation.simulation.files;

import java.io.InputStream;

public class SimpleMazeMeasureFile implements IMeasureFile {

    private final String measureFile;
    private final String idealPathFile;

    public SimpleMazeMeasureFile(String measureFile, String idealPathFile) {
        this.measureFile = measureFile;
        this.idealPathFile = idealPathFile;
    }

    @Override
    public InputStream getMeasures() {
        return getInputStreamFromResources(measureFile);
    }

    @Override
    public InputStream getIdealPath() {
        return getInputStreamFromResources(idealPathFile);
    }

    private InputStream getInputStreamFromResources(String fileName) {
        return getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
