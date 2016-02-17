package pl.edu.agh.capo.scheduler.divider;

import pl.edu.agh.capo.logic.Agent;

import java.util.Comparator;

public class EnergyTimeDivider extends TimeDivider {
    public EnergyTimeDivider(int intervalTime, int agentCount) {
        super(intervalTime, agentCount);
    }

    @Override
    protected AgentFactorInfo createAgentInfo(int index, Agent agent) {
        return new AgentFactorInfo(index, agent) {
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
