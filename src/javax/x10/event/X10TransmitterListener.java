/*
 * X10TransmitterListener.java
 *
 * Created on July 12, 2002, 5:51 PM
 */

package javax.x10.event;

/** X10TransmitterListener defines the interface for an object that listens
 * for messages related to an {@link javax.x10.X10Transmitter}'s
 * internal {@link javax.x10.event.X10Event} queue.
 *
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public interface X10TransmitterListener extends java.util.EventListener {
    
    /** This method is called to notify a listener that the 
     * {@link javax.x10.X10Transmitter}
     * it has registered with has no more 
     * {@link javax.x10.event.X10Event}s to send.
     * <code>e.getX10Event()</code> will return null.
     */
    public void queueEmptied(X10TransmitterEvent e);
    
    /** This method is called to notify a listener that the
     * {@link javax.x10.X10Transmitter}
     * it has registered with has a new {@link javax.x10.event.X10Event} 
     * to send.
     * <code>e.getX10Event()</code> will return the 
     * {@link javax.x10.event.X10Event} that was added.
     */
    public void queueUpdated(X10TransmitterEvent e);
    
    /** This method is called to notify a listener that the
     * {@link javax.x10.X10Transmitter}
     * it has registered with was unable to deliver an 
     * {@link javax.x10.event.X10Event}. 
     * <code>e.getX10Event()</code> will return the 
     * {@link javax.x10.event.X10Event} that was undelivered.
     */
    public void eventUndelivered(X10TransmitterEvent e);

    /** This method is called to notify a listener that the
     * {@link javax.x10.X10Transmitter}
     * it has registered with was able to successfully deliver an 
     * {@link javax.x10.event.X10Event}. 
     * <code>e.getX10Event()</code> will return the 
     * {@link javax.x10.event.X10Event} that was delivered.
     */
    public void eventDelivered(X10TransmitterEvent e);
}
