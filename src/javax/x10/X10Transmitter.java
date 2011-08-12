/*
 * X10Deliverer.java
 *
 * Created on July 9, 2002, 10:31 PM
 */

package javax.x10;

import javax.x10.event.X10Event;
import javax.x10.event.X10TransmitterListener;

/** Classes that implement this interface are responsible for delivering
 * {@link javax.x10.event.X10Event}s.  
 * {@link javax.x10.event.X10Event}s are delivered with the 
 * {@link #transmit(X10Event)} method.  X10Transmitter also creates 
 * {@link javax.x10.event.X10TransmitterEvent}s and notifies 
 * {@link javax.x10.event.X10TransmitterListener}s that have registered 
 * with the {@link #addX10TransmitterListener(X10TransmitterListener)}.  These events relate to the 
 * internal {@link javax.x10.event.X10Event} queue that events are added 
 * to via the {@link #transmit(X10Event)} method.
 *
 * @see javax.x10.X10Receiver
 * @see javax.x10.X10Transceiver
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public interface X10Transmitter {
    
    /** This method gives the X10 transmitter a new 
     * {@link javax.x10.event.X10Event} to deliver. 
     */
    public void transmit(X10Event event);
    
    /** Registers {@link javax.x10.event.X10TransmitterListener}s that
     * receive {@link javax.x10.event.X10TransmitterEvent}s which relate
     * to the internal {@link javax.x10.event.X10Event} queue of this 
     * X10Transmitter.
     * @param listener the listener to be added
     */
    public void addX10TransmitterListener(X10TransmitterListener listener);
    
    /** Deregisters an {@link javax.x10.event.X10TransmitterListener}
     * @param listener the listener to be removed
     */
    public void removeX10TransmitterListener(X10TransmitterListener listener);
}
