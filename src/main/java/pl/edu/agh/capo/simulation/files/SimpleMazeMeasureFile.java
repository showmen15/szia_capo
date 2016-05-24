package pl.edu.agh.capo.simulation.files;

import java.io.File;

public class SimpleMazeMeasureFile implements IMeasureFile {


    @Override
    public File getMeasures() {
        return getFileFromResources("DaneLabirynt3.csv");
    }

    @Override
    public File getPath() {
        return getFileFromResources("DaneLabirynt3-pozycje,bezHough.csv");
    }

    private File getFileFromResources(String fileName) {
        return new File(getClass().getClassLoader().getResource(fileName).getFile());
    }
}
