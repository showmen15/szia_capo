package pl.edu.agh.capo.simulation.simulation;

import pl.edu.agh.capo.common.Location;
import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.Room;
import pl.edu.agh.capo.logic.scheduler.Scheduler;
import pl.edu.agh.capo.logic.scheduler.divider.AbstractTimeDivider;
import pl.edu.agh.capo.maze.MazeMap;
import pl.edu.agh.capo.maze.helper.MazeHelper;
import pl.edu.agh.capo.robot.CapoRobotConstants;
import pl.edu.agh.capo.simulation.logic.scheduler.divider.StatisticsEnergyTimeDivider;
import pl.edu.agh.capo.simulation.logic.scheduler.divider.StatisticsFitnessTimeDivider;
import pl.edu.agh.capo.simulation.simulation.files.IMeasureFile;
import pl.edu.agh.capo.simulation.simulation.files.SimpleMazeMeasureFile;
import pl.edu.agh.capo.simulation.statistics.IStatisticsPrinter;
import pl.edu.agh.capo.simulation.statistics.IdealPathWriter;
import pl.edu.agh.capo.simulation.statistics.StatisticsPrinter;
import pl.edu.agh.capo.simulation.ui.model.AgentViewModel;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SimulationEnvironment {
    private final Scheduler scheduler;
    private final MeasureSimulator simulator;
    private final AbstractTimeDivider timeDivider;

    private boolean showTheBest;
    private Agent currentAgent;
    private boolean showAll;

    private SimulationEnvironment(Scheduler scheduler, MeasureSimulator simulator, AbstractTimeDivider timeDivider) {
        this.scheduler = scheduler;
        this.simulator = simulator;
        this.timeDivider = timeDivider;
        currentAgent = timeDivider.getAgents().get(0);
    }

    public static SimulationEnvironment buildPerfectPath(MazeMap map) {
        IMeasureFile measureFile = new SimpleMazeMeasureFile("DaneLabirynt2.csv");
        IStatisticsPrinter statisticsPrinter = new IdealPathWriter("DaneLabirynt2-pozycje.csv");
        List<Room> rooms = MazeHelper.buildRooms(map);
        AbstractTimeDivider timeDivider = new StatisticsFitnessTimeDivider(rooms, CapoRobotConstants.FITNESS_ESTIMATOR_CLASS,
                CapoRobotConstants.INTERVAL_TIME, statisticsPrinter);

        MeasureFileReader reader = new MeasureFileReader(measureFile.getMeasures());
        MeasureSimulator simulator = new MeasureSimulator(reader);
        Scheduler scheduler = new Scheduler(timeDivider, simulator);
        scheduler.setOnFinishListener(() -> {
            reader.reset();
            timeDivider.reset();
            scheduler.start();
        });
        new Thread(scheduler::start).start();
        return new SimulationEnvironment(scheduler, simulator, timeDivider);
    }

    public static SimulationEnvironment build(MazeMap map) {
        IMeasureFile measureFile = new SimpleMazeMeasureFile("DaneLabirynt1.csv", "DaneLabirynt1-pozycje-pop.csv");
        IStatisticsPrinter statisticsPrinter = new StatisticsPrinter(measureFile.getIdealPath());
        List<Room> rooms = MazeHelper.buildRooms(map);
        AbstractTimeDivider timeDivider = new StatisticsEnergyTimeDivider(rooms, CapoRobotConstants.FITNESS_ESTIMATOR_CLASS,
                CapoRobotConstants.INTERVAL_TIME, statisticsPrinter);

        MeasureFileReader reader = new MeasureFileReader(measureFile.getMeasures());
        MeasureSimulator simulator = new MeasureSimulator(reader);
        Scheduler scheduler = new Scheduler(timeDivider, simulator);
        scheduler.setOnFinishListener(() -> {
            reader.reset();
            timeDivider.reset();
            scheduler.start();
        });
        new Thread(scheduler::start).start();
        return new SimulationEnvironment(scheduler, simulator, timeDivider);
    }

    public void setUpdateMeasures(boolean selected) {
        simulator.setUpdateMeasures(selected);
    }

    public List<AgentViewModel> getViewAgents() {
        if (showTheBest) {
            currentAgent = timeDivider.getBest().getAgent();
        }
        if (showAll) {
            return timeDivider.getAgents().stream()
                    .map(agent -> new AgentViewModel(agent, timeDivider.getFactor(agent), currentAgent.equals(agent)))
                    .sorted((a1, a2) -> Double.compare(-a1.getFactor(), -a2.getFactor()))
                    .limit(3)
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList(new AgentViewModel(currentAgent, timeDivider.getFactor(currentAgent), true));
        }
    }

    public Agent getCurrentAgent() {
        return currentAgent;
    }

    public void setShowBestAgent(boolean showBestAgent) {
        this.showTheBest = showBestAgent;
    }

    public void nextAgent() {
        int index = timeDivider.getAgents().indexOf(currentAgent);
        currentAgent = timeDivider.getAgents().get((index + 1) % timeDivider.getAgents().size());
    }

    public void previousAgent() {
        int index = timeDivider.getAgents().indexOf(currentAgent);
        currentAgent.getFitnessEstimator().printDebug();
        printLocation(currentAgent.getLocation());
        int agentCount = timeDivider.getAgents().size();
        currentAgent = timeDivider.getAgents().get((index - 1 + agentCount) % agentCount);
    }

    private void printLocation(Location location) {
        System.out.println(
                String.format(Locale.US, "estimator.estimateFitness(buildCoordinates(%f, %f).toPoint2D(), %f)",
                        location.positionX, location.positionY, location.alpha)
        );
    }

    public void setUpdateListener(Scheduler.UpdateMeasureListener updateView) {
        scheduler.setListener(updateView);
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }
}
