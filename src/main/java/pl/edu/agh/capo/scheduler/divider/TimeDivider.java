package pl.edu.agh.capo.scheduler.divider;

import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.Room;
import pl.edu.agh.capo.statistics.IStatisticsPrinter;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public abstract class TimeDivider {
    private final int intervalTime;

    private final List<Room> rooms;
    private final List<Agent> agents = new CopyOnWriteArrayList<>();
    private final List<AgentFactorInfo> agentFactorInfos = new LinkedList<>();
    private final IStatisticsPrinter statisticsPrinter;
    private int agentCount;
    // Current interval
    private int[] currentIntervalTimes;
    private double intervalFactorSum;
    private boolean newInterval;

    public TimeDivider(List<Room> rooms, int agentCount, int intervalTime, IStatisticsPrinter statisticsPrinter) {
        this.rooms = rooms;
        this.agentCount = agentCount;
        this.intervalTime = intervalTime;
        this.statisticsPrinter = statisticsPrinter;
        reinitializeCurrentIntervalTimes();
    }

    private void reinitializeCurrentIntervalTimes() {
        currentIntervalTimes = new int[agentCount];
    }

    public void printAndResetStatistics() {
        statisticsPrinter.printAndReset();
        newInterval = true;
    }

    protected abstract AgentFactorInfo createAgentInfo(int index, Agent agent);

    public void addAgent(Agent agent) {
        agents.add(agent);
        AgentFactorInfo agentFactorInfo = createAgentInfo(agentFactorInfos.size(), agent);
        agentFactorInfos.add(agentFactorInfo);
    }

    private void updateFactor(AgentFactorInfo info) {
        info.updateFactor();
        intervalFactorSum += info.getFactor();
    }

    public List<AgentFactorInfo> getAgentFactorInfos() {
        return agentFactorInfos;
    }

    public void updateFactors() {
        removeExcessAgents();
        addAgentInEmptyRooms();
        updateIndexes();
        intervalFactorSum = 0.0;
        agentFactorInfos.forEach(this::updateFactor);
        newInterval = false;
    }

    protected void addAgentInEmptyRooms() {
        List<Room> filledRooms = agentFactorInfos.stream().map(agentFactorInfo -> agentFactorInfo.getAgent().getRoom()).collect(Collectors.toList());
        rooms.stream().filter(room -> !filledRooms.contains(room)).forEach(emptyRoom -> {
            addAgent(new Agent(emptyRoom));
            agentCount++;
        });
        reinitializeCurrentIntervalTimes();
    }

    private void updateIndexes() {
        for (int i = 0; i < agentCount; i++) {
            agentFactorInfos.get(i).index = i;
        }
    }

    private void removeExcessAgents() {
        Map<Room, List<AgentFactorInfo>> agentsByRoom = agentFactorInfos.stream().collect(
                Collectors.groupingBy(agentFactorInfo -> agentFactorInfo.getAgent().getRoom()));
        agentsByRoom.forEach((room, list) -> removeAgentsIfNeeded(list));
    }

    private void removeAgentsIfNeeded(List<AgentFactorInfo> agentFactorInfos) {
        if (agentFactorInfos.size() > 1) {
            AgentFactorInfo max = agentFactorInfos.stream().max((a1, a2) -> Double.compare(a1.getFactor(), a2.getFactor())).get();
            agentFactorInfos.stream().filter(agentFactorInfo -> agentFactorInfo.getFactor() * 2 <= max.getFactor() || max.followsSameHyphotesis(agentFactorInfo)).forEach(this::removeAgent);
        }
    }

    private void removeAgent(AgentFactorInfo agentFactorInfoToRemove) {
        agents.remove(agentFactorInfoToRemove.getAgent());
        agentFactorInfos.remove(agentFactorInfoToRemove);
        agentCount--;
    }

    public int[] getTimes() {
        return currentIntervalTimes;
    }

    public void recalculate() {
        //System.out.println("fitness sum");
        if (intervalFactorSum > 0.0) {
            final int timeToDivide = intervalTime - distributedTimeToStarvingAgents();
            agentFactorInfos.stream().filter(a -> a.getFactor() > 0).forEach(i -> setTime(i, i.calculateTime(timeToDivide)));
        } else {
            agentFactorInfos.forEach(i -> setTime(i, intervalTime / agentCount));
        }
        statisticsPrinter.update(getTheBest(), intervalFactorSum, agentCount);
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

    private AgentFactorInfo getTheBest() {
        return agentFactorInfos.stream()
                .max((a1, a2) -> Double.compare(a1.getFactor(), a2.getFactor()))
                .get();
    }

    public Agent updateTheBest() {
        Agent best = agents.stream()
                .max(createAgentComparator())
                .get();
        agents.forEach(agent -> agent.setIsTheBest(false));
        best.setIsTheBest(true);
        return best;
    }

    public abstract double getFactor(Agent agent);

    public abstract class AgentFactorInfo {
        private static final int MAX_SLEPT_ITERATIONS = 5;
        private final Agent agent;
        protected double factor;
        private int index;
        private int sleptIterations = MAX_SLEPT_ITERATIONS;

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

        public double getFactor() {
            return factor;
        }

        private boolean isStarved() {
            return ++sleptIterations > AgentFactorInfo.MAX_SLEPT_ITERATIONS;
        }

        private int calculateTime(int timeToDivide) {
            return (int) (timeToDivide * factor / intervalFactorSum);
        }

        private void updateFactor() {
            if (newInterval) {
                resetFactor();
            }
            this.factor = estimatedFactor();
        }

        protected abstract void resetFactor();

        protected abstract double estimatedFactor();

        public boolean followsSameHyphotesis(AgentFactorInfo agentFactorInfo) {
            return !equals(agentFactorInfo) && getAgent().getLocation().inNeighbourhoodOf(agentFactorInfo.getAgent().getLocation());
        }


    }
}
