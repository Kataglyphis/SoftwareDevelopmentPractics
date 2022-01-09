package edu.kit.valaris.threading;

/**
 * A job that is executed using the {@link JobManager}.
 */
public abstract class Job {

    /**
     * The timestamp when the last update was started
     */
    private long m_lastNanos;

    /**
     * Initializes this Job for use by the {@link JobManager}.
     */
    public void init() {
        jobInit();
        m_lastNanos = System.nanoTime();
    }

    /**
     * Updates this {@link Job}.
     */
    public void update() {
        long nanos = System.nanoTime();
        float tpf = ((float) nanos - m_lastNanos) / 1000000000f;
        m_lastNanos = nanos;

        jobUpdate(tpf);
    }

    /**
     * Releases all aquired Ressources.
     */
    public void cleanup() {
        jobCleanup();
    }

    /**
     * initializes used resources.
     */
    protected abstract void jobInit();

    /**
     * Updates this {@link Job}.
     * @param tpf the time since the last update in seconds
     */
    protected abstract void jobUpdate(float tpf);

    /**
     * Destroys used resources.
     */
    protected abstract void jobCleanup();

    /**
     * Checks whether this {@link Job} should no longer be executed.
     * @return whether the {@link Job} should no longer be executed.
     */
   public abstract boolean isDead();
}
