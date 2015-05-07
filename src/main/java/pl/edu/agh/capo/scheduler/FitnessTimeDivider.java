package pl.edu.agh.capo.scheduler;

import pl.edu.agh.capo.logic.Agent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FitnessTimeDivider {
    private final List<AgentInfo> agentInfos;
    private double fitnessSum = 0.0;
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
                .collect(Collectors.toList());
    }

    public void recalculate() {
        System.out.println("Recalculating...");
        synchronized (agentInfos) {
            if (fitnessSum > 0.0) {
                agentInfos.forEach(i -> i.setTime((int) (periodTime * i.getFitness() / fitnessSum)));
            }
        }
    }

    public class AgentInfo {
        private final Agent agent;
        private double fitness;
        private int time;

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

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }
    }


}
