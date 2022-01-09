package edu.kit.valaris.threading;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import edu.kit.valaris.Metadata;
import org.lwjgl.Sys;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An {@link com.jme3.app.state.AppState} used to manage jobs that are executed in predefined seperate threads.
 *
 * @author Frederik Lingg
 */
public class JobManager extends AbstractAppState {

    private static JobManager m_instance;

    /**
     * Initializes a new Instance of the {@link JobManager}.
     * @param numThreads the number of threads to use.
     * @param ticks the maximum number of ticks a job gets per second.
     */
    public static void initInstance(int numThreads, int ticks) {
        m_instance = new JobManager(numThreads, ticks);
    }

    /**
     * The current instance of the {@link JobManager}.
     * @return the current {@link JobManager}.
     */
    public static JobManager getInstance() {
        return m_instance;
    }

    /**
     * The number of threads to use.
     */
    private int m_numThreads;

    /**
     * Used to terminate threads when the frame is done.
     */
    private Metadata m_metadata;

    /**
     * Newly added jobs.
     */
    private List<Job> m_pendingInit;
    private Lock m_initLock;

    /**
     * Jobs that need cleanup.
     */
    private List<Job> m_pendingCleanup;
    private Lock m_cleanupLock;

    /**
     * Currently active jobs.
     */
    private List<Job> m_active;
    private Lock m_activeLock;

    /**
     * Barrier used to await termination of the used threads
     */
    private CyclicBarrier m_barrier;

    /**
     * Whether the {@link JobManager} is currently running.
     */
    private boolean m_running;

    /**
     * the number of currently active jobs. This differs from {@link #m_active}.size() since jobs are continously added and removed from that list.
     */
    private int m_jobCount;

    /**
     * The number of updates a Job gets per second, if every Job stays inside its window that is.
     */
    private int m_ticks;

    /**
     * The amount of time a job is expected to use while updating.
     */
    private long m_jobWindow;

    /**
     * Creates a new {@link JobManager} using the given amount of threads.
     * @param threads the number of threads to use.
     */
    public JobManager(int threads, int ticks) {
        m_numThreads = threads;
        m_running = false;

        //init tick params
        m_jobCount = 0;
        m_ticks = ticks;

        //start with a tenth of a second
        m_jobWindow = 100000000;

        //init lists
        m_pendingInit = new LinkedList<>();
        m_pendingCleanup = new LinkedList<>();
        m_active = new LinkedList<>();

        //init locks
        m_initLock = new ReentrantLock();
        m_cleanupLock = new ReentrantLock();
        m_activeLock = new ReentrantLock();

        //init barrier
        m_barrier = new CyclicBarrier(m_numThreads + 1);
    }

    /**
     * Adds a job to the {@link JobManager}. Once added, a Job is only removed if it is dead.
     * @param job the job to add.
     */
    public void addJob(Job job) {
        m_initLock.lock();
        m_pendingInit.add(job);
        m_initLock.unlock();

        //update window
        m_jobCount++;
        m_jobWindow = (m_numThreads * 1000000000) / (m_ticks * m_jobCount);
    }

    /**
     * Acquires an active {@link Job} for processing.
     * @return an active job
     */
    private Job acquire() {
        Job result = null;
        if(m_active.size() > 0) {
            m_activeLock.lock();
            if(m_active.size() > 0) {
                result = m_active.remove(0);
            }
            m_activeLock.unlock();
        }

        return result;
    }

    /**
     * Submits a job after updating.
     * @param job the job to submit.
     */
    private void submit(Job job) {
        if(job.isDead()) {
            m_cleanupLock.lock();
            m_pendingCleanup.add(job);
            m_cleanupLock.unlock();

            //update window
            m_jobCount--;
            m_jobWindow = (m_numThreads * 1000000000) / (m_ticks * m_jobCount);
        } else {
            m_activeLock.lock();
            m_active.add(job);
            m_activeLock.unlock();
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        //get metadata
        m_metadata = stateManager.getState(Metadata.class);
        if(m_metadata == null) {
            throw new IllegalStateException("Metadata not found");
        }

        //init threads
        m_running = true;
        for(int i = 0; i < m_numThreads; i++) {
            new Thread(() -> {
                //update while state is active
                while(m_running && m_metadata.isRunning()) {

                    //update a job and update it
                    long nanoDelta = System.nanoTime();
                    Job job = acquire();
                    if(job != null) {
                        job.update();
                        submit(job);
                    }
                    nanoDelta = (System.nanoTime() - nanoDelta);

                    //wait for the end of the job window
                    if(nanoDelta > 0) {
                        try {
                            Thread.sleep(nanoDelta / 1000000, (int) (nanoDelta % 1000000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //if no jobs are available, sleep and then check again
                    while(!isEnabled() && m_running && m_metadata.isRunning()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //wait for other threads to terminate
                try {
                    m_barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @Override
    public void update(float tpf) {
        //init pending jobs
        m_initLock.lock();
        while(m_pendingInit.size() > 0) {
            Job job = m_pendingInit.remove(0);
            job.init();
            submit(job);
        }
        m_initLock.unlock();

        //cleanup pending jobs
        m_cleanupLock.lock();
        while(m_pendingCleanup.size() > 0) {
            m_pendingCleanup.remove(0).cleanup();
        }
        m_cleanupLock.unlock();
    }

    @Override
    public void cleanup() {
        //stop all threads and await their termination
        m_running = false;
        try {
            m_barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        //cleanup jobs after threads are terminated
        for(Job job : m_pendingCleanup) {
            job.cleanup();
        }

        //cleanup jobs that will no longer be executed
        for(Job job : m_active) {
            job.cleanup();
        }
    }
}
