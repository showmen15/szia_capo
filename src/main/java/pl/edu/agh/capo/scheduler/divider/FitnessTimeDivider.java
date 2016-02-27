package pl.edu.agh.capo.scheduler.divider;

import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.Room;

import java.util.Comparator;
import java.util.List;

public class FitnessTimeDivider extends TimeDivider {

    public FitnessTimeDivider(List<Room> rooms, int agentCount, int intervalTime) {
        super(rooms, agentCount, intervalTime);
    }

    @Override
    protected AgentFactorInfo createAgentInfo(int index, Agent agent) {
        return new AgentFactorInfo(index, agent) {
            @Override
            protected double estimatedFactor() {
                return agent.getFitness();
            }
        };
    }

    @Override
    public Comparator<Agent> createAgentComparator() {
        return (a1, a2) -> Double.compare(a1.getFitness(), a2.getFitness());
    }

    @Override
    public double getFactor(Agent agent) {
        return agent.getFitness();
    }
}
