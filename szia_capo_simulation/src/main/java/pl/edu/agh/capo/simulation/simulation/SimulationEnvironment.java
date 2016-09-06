package pl.edu.agh.capo.simulation.simulation;

import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.Room;
import pl.edu.agh.capo.logic.scheduler.Scheduler;
import pl.edu.agh.capo.logic.scheduler.divider.AbstractTimeDivider;
import pl.edu.agh.capo.maze.MazeMap;
import pl.edu.agh.capo.maze.helper.MazeHelper;
import pl.edu.agh.capo.robot.CapoRobotConstants;
import pl.edu.agh.capo.simulation.logic.scheduler.divider.StatisticsEnergyTimeDivider;
import pl.edu.agh.capo.simulation.simulation.files.IMeasureFile;
import pl.edu.agh.capo.simulation.simulation.files.SimpleMazeMeasureFile;
import pl.edu.agh.capo.simulation.statistics.IStatisticsPrinter;
import pl.edu.agh.capo.simulation.statistics.StatisticsPrinter;
import pl.edu.agh.capo.simulation.ui.model.AgentViewModel;

import java.util.Collections;
import java.util.List;

public class SimulationEnvironment {
    private final Scheduler scheduler;
    private final MeasureSimulator simulator;
    private final AbstractTimeDivider timeDivider;

    private boolean showTheBest;
    private Agent currentAgent;

    private SimulationEnvironment(Scheduler scheduler, MeasureSimulator simulator, AbstractTimeDivider timeDivider) {
        this.scheduler = scheduler;
        this.simulator = simulator;
        this.timeDivider = timeDivider;
        currentAgent = timeDivider.getAgents().get(0);
    }

    public static SimulationEnvironment build(MazeMap map) {
        IMeasureFile measureFile = new SimpleMazeMeasureFile("DaneLabirynt3.csv", "DaneLabirynt3-pozycje,bezHough.csv");
        IStatisticsPrinter statisticsPrinter = new StatisticsPrinter(measureFile.getIdealPath());
        List<Room> rooms = MazeHelper.buildRooms(map);
        AbstractTimeDivider timeDivider = new StatisticsEnergyTimeDivider(rooms, CapoRobotConstants.FITNESS_ESTIMATOR_CLASS,
                CapoRobotConstants.INTERVAL_TIME, statisticsPrinter);

        Scheduler scheduler = new Scheduler(timeDivider);
        MeasureSimulator simulator = new MeasureSimulator(new MeasureFileReader(measureFile.getMeasures()), scheduler);
        simulator.start();
        return new SimulationEnvironment(scheduler, simulator, timeDivider);
    }

    public void setUpdateMeasures(boolean selected) {
        simulator.setUpdateMeasures(selected);
    }

    public List<AgentViewModel> getViewAgents() {
        if (showTheBest) {
            currentAgent = timeDivider.getBest().getAgent();
        }
//        return timeDivider.getAgents().stream()
//                .map(agent -> new AgentViewModel(agent, timeDivider.getFactor(agent), currentAgent.equals(agent)))
//                .collect(Collectors.toList());
        return Collections.singletonList(new AgentViewModel(currentAgent, timeDivider.getFactor(currentAgent), true));
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
        if (index == 0) {
            index = timeDivider.getAgents().size();
        }
        currentAgent = timeDivider.getAgents().get((index - 1) % timeDivider.getAgents().size());
    }

    public void setUpdateListener(Scheduler.UpdateMeasureListener updateView) {
        scheduler.setListener(updateView);
    }
}
