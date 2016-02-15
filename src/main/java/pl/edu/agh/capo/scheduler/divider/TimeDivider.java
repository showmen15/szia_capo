package pl.edu.agh.capo.scheduler.divider;

import pl.edu.agh.capo.logic.Agent;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public abstract class TimeDivider {
    private final int intervalTime;
    private final List<Agent> agents = new LinkedList<>();

    private final List<AgentFactorInfo> agentFactorInfos = new LinkedList<>();
    // Next interval
    private final List<Agent> agentsToAddInNextInterval = new LinkedList<>();
    private int agentCount;
    // Current interval
    private int[] currentIntervalTimes;
    private double intervalFactorSum;

    //Statistics
    private double factorMedium = 0.0;
    private long intervalCount = 0;

    public TimeDivider(int intervalTime, int agentCount) {
        this.agentCount = agentCount;
        this.intervalTime = intervalTime;
        reinitializeCurrentIntervalTimes();
    }

    private void reinitializeCurrentIntervalTimes() {
        currentIntervalTimes = new int[agentCount];
    }

    public void printAndResetStatistics() {
        System.out.println(Double.toString(factorMedium).replace('.', ','));
        factorMedium = 0.0;
        intervalCount = 0;
    }

    protected abstract AgentFactorInfo createAgentInfo(int index, Agent agent);

    public void addAgent(Agent agent) {
        agents.add(agent);
        AgentFactorInfo agentFactorInfo = createAgentInfo(agentFactorInfos.size(), agent);
        agentFactorInfos.add(agentFactorInfo);
        setTime(agentFactorInfo, intervalTime / agentCount);
    }

    private void updateFactor(AgentFactorInfo info) {
        info.updateFactor();
        intervalFactorSum += info.getFactor();
    }

    public List<AgentFactorInfo> getAgentFactorInfos() {
        return agentFactorInfos;
    }

    public void updateFactors() {
        addRequestedAgents();
        intervalFactorSum = 0.0;
        agentFactorInfos.forEach(this::updateFactor);
    }

    public int[] getTimes() {
        return currentIntervalTimes;
    }

    public void addAgentInNextInterval(Agent agent) {
        synchronized (agentsToAddInNextInterval) {
            agentsToAddInNextInterval.add(agent);
        }
    }

    private void addRequestedAgents() {
        synchronized (agentsToAddInNextInterval) {
            if (agentsToAddInNextInterval.size() > 0) {
                for (Agent agent : agentsToAddInNextInterval) {
                    addAgent(agent);
                    agentCount++;
                }
                reinitializeCurrentIntervalTimes();
                agentsToAddInNextInterval.clear();
            }
        }
    }

    public void recalculate() {
        //System.out.println("fitness sum");
        if (intervalFactorSum > 0.0) {
            final int timeToDivide = intervalTime - distributedTimeToStarvingAgents();
            agentFactorInfos.stream().filter(a -> a.getFactor() > 0).forEach(i -> setTime(i, i.calculateTime(timeToDivide)));
        } else {
            agentFactorInfos.forEach(i -> setTime(i, intervalTime / agentCount));
        }
        updateStatistics();
    }

    private void updateStatistics() {
        intervalCount++;
        double intervalFactorMedium = intervalFactorSum / agentCount;
        factorMedium += (intervalFactorMedium - factorMedium) / intervalCount;
    }

    private int distributedTimeToStarvingAgents() {
        int distributedTime = 0;
        for (AgentFactorInfo agentInfo : agentFactorInfos) {
            if (agentInfo.isStarved()) {
                int time = intervalTime / agentCount;
                setTime(agentInfo, time);
                distributedTime += time;
            }
        }
        return distributedTime;
    }

    private void setTime(AgentFactorInfo agentFactorInfo, int time) {
        if (time > 0) {
            agentFactorInfo.sleptIterations = 0;
        }
        currentIntervalTimes[agentFactorInfo.index] = time;
    }

    public abstract Comparator<Agent> createAgentComparator();

    public List<Agent> getAgents() {
        return agents;
    }

    public int updateTheBest() {
        Agent best = agents.stream()
                .max(createAgentComparator())
                .get();
        agents.forEach(agent -> agent.setIsTheBest(false));
        best.setIsTheBest(true);
        return agents.indexOf(best);
    }

    public int getAgentCount() {
        return agentCount;
    }

    public abstract class AgentFactorInfo {
        private static final int MAX_SLEPT_ITERATIONS = 5;

        private final int index;
        private final Agent agent;
        protected double factor;
        private int sleptIterations = 0;

        public AgentFactorInfo(int index, Agent agent) {
            this.index = index;
            this.agent = agent;
        }

        public Agent getAgent() {
            return agent;
        }

        public int getIndex() {
            return index;
        }

        private double getFactor() {
            return factor;
        }

        private boolean isStarved() {
            return ++sleptIterations > AgentFactorInfo.MAX_SLEPT_ITERATIONS;
        }

        private int calculateTime(int timeToDivide) {
            return (int) (timeToDivide * factor / intervalFactorSum);
        }

        private void updateFactor() {
            this.factor = estimatedFactor();
        }

        protected abstract double estimatedFactor();
    }
}
