package pl.edu.agh.capo.simulation.statistics;

import pl.edu.agh.capo.logic.scheduler.divider.AbstractTimeDivider;

import java.io.*;

public class IdealPathWriter implements IStatisticsPrinter {

    private final FileWriter writer;
    private final String fileName;

    public IdealPathWriter(String fileName) {
        this.fileName = fileName;
        try {
            writer = new FileWriter(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się zapisać do pliku");
        }
    }

    @Override
    public void printAndReset() {
        System.exit(1);
    }

    @Override
    public void update(AbstractTimeDivider.AgentFactorInfo theBest, double intervalFactorSum, int agentCount) {

        try {
            writer.write(theBest.getAgent().getLocation().toString() + "," + theBest.getAgent().getFitness() + "\n");
            writer.flush();
            //System.out.println("Zapisano pozycję");
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się zapisać lokalizacji do pliku");
        }
    }

    private int lineCount(InputStream inputStream) throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(inputStream));
        lineNumberReader.skip(Long.MAX_VALUE);
        int lines = lineNumberReader.getLineNumber() - 1;
        return lines;
    }
}
