package pl.edu.agh.capo.statistics;

import pl.edu.agh.capo.scheduler.divider.TimeDivider;

public interface IStatisticsPrinter {
    void printAndReset();

    void update(TimeDivider.AgentFactorInfo theBest, double intervalFactorSum, int agentCount);
}
