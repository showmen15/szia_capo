package pl.edu.agh.capo.simulation.files;

import java.io.InputStream;

public class SimpleMazeMeasureFile implements IMeasureFile {

    @Override
    public InputStream getMeasures() {
        return getInputStreamFromResources("DaneLabirynt3.csv");
    }

    @Override
    public InputStream getPath() {
        return getInputStreamFromResources("DaneLabirynt3-pozycje,bezHough.csv");
    }

    private InputStream getInputStreamFromResources(String fileName) {
        return getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
