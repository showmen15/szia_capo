package pl.edu.agh.capo.simulation.simulation.files;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class SimpleMazeMeasureFile implements IMeasureFile {

    private final String measureFile;
    private String idealPathFile;

    public SimpleMazeMeasureFile(String measureFile) {
        this.measureFile = measureFile;
    }

    public SimpleMazeMeasureFile(String measureFile, String idealPathFile) {
        this(measureFile);
        this.idealPathFile = idealPathFile;
        assertLines();
    }

    private void assertLines() {
        try {
            int measureLineCount = lineCount(getMeasures());
            int idealPathLineCount = lineCount(getIdealPath());
            if (measureLineCount == idealPathLineCount) {
                return;
            }
            LoggerFactory.getLogger(getClass()).error(String.format("Measure file [%d] and ideal path [%d] file line count is different",
                    measureLineCount, idealPathLineCount));
        } catch (IOException ignored) {
            LoggerFactory.getLogger(getClass()).error("Could not read measure file");
        }
    }

    private int lineCount(InputStream inputStream) throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(inputStream));
        lineNumberReader.skip(Long.MAX_VALUE);
        int lines = lineNumberReader.getLineNumber() - 1;
        return lines;
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
