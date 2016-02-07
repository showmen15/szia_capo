package pl.edu.agh.capo.scheduler;

import pl.edu.agh.capo.logic.Agent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FitnessTimeDivider {
    private final List<AgentInfo> agentInfos;
    private double fitnessSum = 0.0;
    protected double fitnessSumMedium = 0.0;
    protected int fitnessSumMediumCount = 0;
    private final int periodTime;
    private final int count;

    public FitnessTimeDivider(int periodTime, int count) {
        this.periodTime = periodTime;
        this.count = count;
        agentInfos = new ArrayList<>(count);
    }

    public void addAgent(Agent agent) {
        agentInfos.add(new AgentInfo(agent, periodTime / count));
    }

    private void updateFitness(AgentInfo info) {
        info.setFitness(info.getAgent().getFitness());
        fitnessSum += info.getFitness();
    }

    public void updateFitnesses() {
        fitnessSum = 0.0;
        agentInfos.forEach(this::updateFitness);
    }

    public List<AgentInfo> getTimes() {
        return agentInfos.stream()
                .filter(p -> p.getTime() > 0)
                .map(AgentInfo::copy)
                .collect(Collectors.toList());
    }

    public void recalculate() {
        //System.out.println("fitness sum");
        if (fitnessSum > 0.0) {
            final int timeToDivide = calculateTimeToDivide();
            agentInfos.stream().filter(a -> a.getFitness() > 0).forEach(i -> i.calculateTime(timeToDivide));
        } else {
            agentInfos.forEach(i -> i.setTime(periodTime / count));
        }

        fitnessSumMedium += fitnessSum;
        fitnessSumMediumCount++;
    }

    private int calculateTimeToDivide() {
        int time = periodTime;
        for (AgentInfo agentInfo : agentInfos) {
            if (agentInfo.getFitness() == 0) {
                time -= agentInfo.timeIfNeeded();
            }
        }
        return time;
    }

    public class AgentInfo {
        private static final int MAX_SLEPT_ITERATIONS = 5;

        private final Agent agent;
        private double fitness;
        private int time;
        private int sleptIterations = 0;

        public AgentInfo(Agent agent, int time) {
            this.agent = agent;
            this.time = time;
        }

        public void setFitness(double fitness) {
            this.fitness = fitness;
        }

        public Agent getAgent() {
            return agent;
        }

        public double getFitness() {
            return fitness;
        }

        public int timeIfNeeded() {
            if (++sleptIterations > AgentInfo.MAX_SLEPT_ITERATIONS) {
                setTime(time = periodTime / count);
            } else {
                time = 0;
            }
            return time;
        }

        public void calculateTime(int timeToDivide) {
            setTime((int) (timeToDivide * fitness / fitnessSum));
        }

        public void setTime(int time) {
            if (time > 0) {
                sleptIterations = 0;
            }
            this.time = time;
        }

        public int getTime() {
            return time;
        }

        public AgentInfo copy() {
            return new AgentInfo(agent, time);
        }
    }


}
