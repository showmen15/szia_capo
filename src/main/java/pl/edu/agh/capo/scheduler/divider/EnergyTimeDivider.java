package pl.edu.agh.capo.scheduler.divider;

import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.Room;
import pl.edu.agh.capo.statistics.IStatisticsPrinter;

import java.util.Comparator;
import java.util.List;

public class EnergyTimeDivider extends TimeDivider {
    public EnergyTimeDivider(List<Room> rooms, int agentCount, int intervalTime, IStatisticsPrinter statisticsPrinter) {
        super(rooms, agentCount, intervalTime, statisticsPrinter);
    }

    @Override
    protected AgentFactorInfo createAgentInfo(int index, Agent agent) {
        return new AgentFactorInfo(index, agent) {
            @Override
            protected void resetFactor() {
                agent.resetEnergy();
            }

            @Override
            protected double estimatedFactor() {
                agent.recalculateEnergy();
                return agent.getEnergy();
            }
        };
    }

    @Override
    public Comparator<Agent> createAgentComparator() {
        return (a1, a2) -> Double.compare(a1.getEnergy(), a2.getEnergy());
    }

    @Override
    public double getFactor(Agent agent) {
        return agent.getEnergy();
    }
}
