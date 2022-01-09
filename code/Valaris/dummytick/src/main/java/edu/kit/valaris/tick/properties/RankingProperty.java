package edu.kit.valaris.tick.properties;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the property, that contains the m_ranking of the vehicle and checkpoint timestamps.
 */
public class RankingProperty implements IProperty {

    /**
     * The timestamps, when the vehicles drove through the checkpoint.
     * The timestamps are saved in seconds.
     * To get the current time in seconds, use {@code com.jme3.system.Timer.getTimeInSeconds()}.
     */
    private List<Float> m_checkpointTimes;

    /**
     * The m_ranking of the vehicle.
     */
    private int m_ranking;

    /**
     * Creates a new m_ranking property with the given checkpoint timestamps and the m_ranking.
     *
     * @param checkpointTimes The checkpoint timestamps in seconds.
     * @param ranking The m_ranking of the vehicle.
     */
    public RankingProperty(List<Float> checkpointTimes, int ranking) {
        this.m_checkpointTimes = checkpointTimes;
        this.m_ranking = ranking;
    }

    /**
     * Returns the checkpoint times in seconds.
     *
     * @return The list of checkpoint times.
     */
    public List<Float> getCheckpointTimes() {
        return m_checkpointTimes;
    }

    /**
     * Adds a checkpoint time for the next checkpoint.
     *
     * @param time the timestamp to add.
     */
    public void addCheckpointTime(Float time) {
        m_checkpointTimes.add(time);
    }

    /**
     * Accesses the Ranking.
     * @return The m_ranking of the player.
     */
    public int getRanking() {
        return m_ranking;
    }

    /**
     * Sets the m_ranking of the player.
     *
     * @param ranking the m_ranking.
     */
    public void setRanking(int ranking) {
        this.m_ranking = ranking;
    }

    /**
     * Creates a deep copy of the property, copying all attributes.
     *
     * @return A deep copy of the property.
     */
    @Override
    public RankingProperty deepCopy() {
        return new RankingProperty(new ArrayList<>(m_checkpointTimes), m_ranking);
    }

    /**
     * Checks, if the property can sync with the specified target.
     *
     * @param other The target property to sync with.
     * @return true, if this property can be synced with the specified target property.
     */
    @Override
    public boolean canSync(IProperty other) {
        return (other instanceof RankingProperty);
    }

    /**
     * Makes this property a deep copy of the specified target.
     *
     * @param other The target to sync with.
     */
    @Override
    public void sync(IProperty other) {
        if (canSync(other)) {
            RankingProperty otherProperty = (RankingProperty) other;
            this.m_ranking = otherProperty.m_ranking;
            this.m_checkpointTimes = new ArrayList<>(otherProperty.m_checkpointTimes);
        } else {
            throw new IllegalStateException("Cannot sync with the given property.");
        }
    }
}
