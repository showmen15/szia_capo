package pl.edu.agh.capo.simulation.logic.scheduler.divider;

import pl.edu.agh.capo.logic.Room;
import pl.edu.agh.capo.logic.fitness.AbstractFitnessEstimator;
import pl.edu.agh.capo.logic.scheduler.divider.EnergyTimeDivider;
import pl.edu.agh.capo.simulation.statistics.IStatisticsPrinter;

import java.util.List;

public class StatisticsEnergyTimeDivider extends EnergyTimeDivider {
    private final IStatisticsPrinter statisticsPrinter;

    public StatisticsEnergyTimeDivider(List<Room> rooms, Class<? extends AbstractFitnessEstimator> estimator,
                                       int intervalTime, IStatisticsPrinter statisticsPrinter) {
        super(rooms, estimator, intervalTime);
        this.statisticsPrinter = statisticsPrinter;
    }

    @Override
    public void reset() {
        statisticsPrinter.printAndReset();
        super.reset();
    }

    @Override
    public void recalculate() {
        try {
            super.recalculate();
        } finally {
            statisticsPrinter.update(getBest(), intervalFactorSum, agentCount);
        }
    }
}
