package pl.edu.agh.capo.simulation.files;

import java.io.InputStream;

public interface IMeasureFile {
    InputStream getMeasures();

    InputStream getPath();
}
