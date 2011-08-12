/*
 * X10Receiver.java
 *
 * Created on July 11, 2002, 12:44 AM
 */

package javax.x10;

import javax.x10.event.X10Listener;

/** X10Receiver defines the interface for an object that receives 
 * {@link javax.x10.event.X10Event}s and notifies listeners about
 * the events.
 *  
 * @see javax.x10.X10Transmitter
 * @see javax.x10.X10Transceiver
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public interface X10Receiver {
    
    /** Registers a new X10Listener to be notified of new 
     * {@link javax.x10.event.X10Event}s.
     * @param listener the listener to be added
     */
    public void addX10Listener(X10Listener listener);
    
    /** Deregisters an X10Listener
     * @param listener the listener to be removed
     */
    public void removeX10Listener(X10Listener listener);
}
