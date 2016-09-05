package pl.edu.agh.capo.logic.scheduler.divider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.Room;
import pl.edu.agh.capo.logic.fitness.AbstractFitnessEstimator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public abstract class AbstractTimeDivider {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTimeDivider.class);
    private final int intervalTime;

    private final List<Room> rooms;
    private final List<Agent> agents = new CopyOnWriteArrayList<>();
    private final List<AgentFactorInfo> agentFactorInfos = new LinkedList<>();
    private final Class<? extends AbstractFitnessEstimator> estimatorClass;
    protected int agentCount = 0;
    protected double intervalFactorSum;
    // Current interval
    private int[] currentIntervalTimes;
    private boolean newInterval;
    private AgentFactorInfo best;

    public AbstractTimeDivider(List<Room> rooms, Class<? extends AbstractFitnessEstimator> estimator, int intervalTime) {
        this.rooms = rooms;
        this.intervalTime = intervalTime;
        this.estimatorClass = estimator;
        this.rooms.forEach(this::buildAgent);
        reinitializeCurrentIntervalTimes();
        updateTheBest();
    }

    private void buildAgent(Room room) {
        addAgent(new Agent(estimatorClass, room));
    }

    private void reinitializeCurrentIntervalTimes() {
        currentIntervalTimes = new int[agentCount];
    }

    public void reset() {
        newInterval = true;
    }

    protected abstract AgentFactorInfo createAgentInfo(int index, Agent agent);

    private void addAgent(Agent agent) {
        agents.add(agent);
        AgentFactorInfo agentFactorInfo = createAgentInfo(agentFactorInfos.size(), agent);
        agentFactorInfos.add(agentFactorInfo);
        agentCount++;
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
        rooms.stream().filter(room -> !filledRooms.contains(room)).forEach(this::buildAgent);
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
        updateTheBest();
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

    public List<Agent> getAgents() {
        return agents;
    }

    public AgentFactorInfo getBest() {
        return best;
    }

    private void updateTheBest() {
        AgentFactorInfo best = agentFactorInfos.stream()
                .max((a1, a2) -> Double.compare(a1.getFactor(), a2.getFactor()))
                .get();
        if (best == null) {
            best = agentFactorInfos.stream().findAny().get();
        }
        agents.forEach(agent -> agent.setIsTheBest(false));
        best.agent.setIsTheBest(true);
        this.best = best;
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

        @Override
        public String toString() {
            return "AgentFactorInfo{" +
                    "agent=" + agent +
                    ", factor=" + factor +
                    ", index=" + index +
                    ", sleptIterations=" + sleptIterations +
                    '}';
        }
    }
}