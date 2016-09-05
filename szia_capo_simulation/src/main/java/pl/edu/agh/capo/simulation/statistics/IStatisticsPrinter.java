package pl.edu.agh.capo.simulation.statistics;

import pl.edu.agh.capo.logic.scheduler.divider.AbstractTimeDivider;

public interface IStatisticsPrinter {
    void printAndReset();

    void update(AbstractTimeDivider.AgentFactorInfo theBest, double intervalFactorSum, int agentCount);
}
