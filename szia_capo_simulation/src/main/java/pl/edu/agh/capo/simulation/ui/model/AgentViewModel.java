package pl.edu.agh.capo.simulation.ui.model;

import math.geom2d.Point2D;
import pl.edu.agh.capo.common.Location;
import pl.edu.agh.capo.common.Vision;
import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.Room;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AgentViewModel {
    private final Room room;
    private final double factor;
    private final Location location;
    private final boolean isHighlighted;
    private final List<Vision> visions;
    private Map<Point2D[], Double> sectionsAwards;

    public AgentViewModel(Agent agent, double factor, boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
        this.visions = agent.getMeasure() == null ? Collections.emptyList() : agent.getMeasure().getVisions();
        this.room = agent.getRoom();
        this.location = agent.getLocation();
        this.factor = factor;
        // this.sectionsAwards = agent.sectionAward;
    }

    public Room getRoom() {
        return room;
    }

    public double getFactor() {
        return factor;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public List<Vision> getVisions() {
        return visions;
    }

    public Map<Point2D[], Double> getSectionsAwards() {
        return sectionsAwards;
    }
}
