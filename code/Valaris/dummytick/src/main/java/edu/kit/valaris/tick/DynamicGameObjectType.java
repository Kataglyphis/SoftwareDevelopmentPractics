package edu.kit.valaris.tick;

/**
 * Enum containing all different types of {@link DynamicGameObject}s.
 *
 * @author Frederik Lingg, Tobias Knorr, Jonas Frey
 * @version 1.0
 */
public enum DynamicGameObjectType {
    /**
     * {@link DynamicGameObjectType} for vehicles.
     */
    VEHICLE,

    /**
     * {@link DynamicGameObjectType} for items.
     */
    ITEM_BOX,

    /**
     * {@link DynamicGameObjectType} for missiles.
     */
    HOMING_MISSILE,

    /**
     * {@link DynamicGameObjectType} for gravitation-traps.
     */
    GRAVITATION_TRAP,

    /**
     * {@link DynamicGameObject} for Dummy-Objects.
     */
    DUMMY,
}
