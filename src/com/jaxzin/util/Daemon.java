/*
 * Daemon.java
 *
 * Created on August 6, 2001, 10:20 PM
 */

package com.jaxzin.util;

/** This class replaces {@link java.lang.Thread} which does not implement 
 * thread-safe starting and stopping methods.  After a <code>Daemon</code> has 
 * been started, it will run a loop that, first, tests if 
 * {@link #isStopCondition} is <code>true</code>. If it is, it stops the 
 * <code>Daemon</code> without calling the {@link #runBody} method.  
 * Otherwise, it calls the {@link #runBody} method. It will 
 * continue this loop until the {@link #stop} method is called.
 * <P>
 * <B>NOTE:</B> Although <code>Daemon</code> implements 
 * {@link com.lang.Runnable}, it is not necessary to pass it into the 
 * constructor of a {@link com.lang.Thread}.  It instantiates its own 
 * {@link com.lang.Thread} internally and should be treated like a 
 * {@link com.lang.Thread} by calling <code>Daemon</code>'s {@link #start} 
 * method directly.<P>
 * <B>NOTE 2:</B> The internal {@link java.lang.Thread} is a daemon so the JVM will
 * not exit until it stops. 
 *
 * @see java.lang.Thread
 * @author  Brian Jackson {brian@jaxzin.com}
 * @version 1.0.0, 2001-08-06
 */
public abstract class Daemon implements java.lang.Runnable {

    private volatile Thread talker;
    
    /** Daemon is an abstract class and unable to be instantiated */
    protected Daemon(){}
    
    /** Starts Daemon in a new Thread */
    public final void start() {
        if(Thread.currentThread() != talker) { // If it isn't already started
            talker = new Thread(this);
            talker.start();
        } // END: if...
    } // END: start

    /** Safely stops the Daemon thread.*/
    public final void stop() {
        talker = null;
    }

    /** Safely resumes the Daemon thread.*/
    public final void resume() {
        start();
    }
    
    /** Safely pauses the Daemon thread.*/
    public final void pause() {
        stop();
    }
    
    /** Tells if the Daemon is running */
    public final boolean isAlive() {
        return talker == Thread.currentThread();
    }
    
    /** Executes the daemon loop */
    public final void run() {
            while(isAlive()) { 
                if(isStopCondition()) {
                    stop();
                } else {
                    runBody();
                } // END: if...
            } // END: while ...
    }

    /** This method is called at the beginning of each loop to test
     * if daemon should continue running.  
     */
    abstract public boolean isStopCondition();
    /** This method is called repetitively while the daemon is running.
     * If the <code>stop()</code> method of the daemon is called externally 
     * while this method is running, it will complete and not run again.  It 
     * is thread-safe.
     */
    abstract public void runBody();
    
}
