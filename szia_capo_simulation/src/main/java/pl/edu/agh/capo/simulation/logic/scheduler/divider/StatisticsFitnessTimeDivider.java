package pl.edu.agh.capo.simulation.logic.scheduler.divider;

import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.Room;
import pl.edu.agh.capo.logic.fitness.AbstractFitnessEstimator;
import pl.edu.agh.capo.logic.scheduler.divider.AbstractTimeDivider;
import pl.edu.agh.capo.simulation.statistics.IStatisticsPrinter;

import java.util.List;

public class StatisticsFitnessTimeDivider extends StatisticsEnergyTimeDivider {

    public StatisticsFitnessTimeDivider(List<Room> rooms, Class<? extends AbstractFitnessEstimator> agentCount, int intervalTime, IStatisticsPrinter statisticsPrinter) {
        super(rooms, agentCount, intervalTime, statisticsPrinter);
    }

    @Override
    protected AbstractTimeDivider.AgentFactorInfo createAgentInfo(int index, Agent agent) {
        return new AgentFactorInfo(index, agent) {
            @Override
            protected void resetFactor() {

            }

            @Override
            protected double estimatedFactor() {
                return agent.getFitness();
            }
        };
    }

    @Override
    public double getFactor(Agent agent) {
        return agent.getFitness();
    }
}
