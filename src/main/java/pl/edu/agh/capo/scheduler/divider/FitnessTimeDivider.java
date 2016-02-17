package pl.edu.agh.capo.scheduler.divider;

import pl.edu.agh.capo.logic.Agent;

import java.util.Comparator;

public class FitnessTimeDivider extends TimeDivider {

    public FitnessTimeDivider(int intervalTime, int agentCount) {
        super(intervalTime, agentCount);
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
