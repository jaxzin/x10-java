/*
 * X10connection.java
 *
 * Created on July 16, 2002, 10:38 PM
 */

package javax.x10;

import javax.x10.util.*;

/** X10Connection defines the interface for objects that represent a connection 
 * to the physical medium that deals with X10Events and the X10 protocol.  
 * There are methods to find out if this connection can send X10Events, 
 * transmit X10Events, or both.
 *
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public interface X10Connection {
    
    /** Returns true if this connection can both transmit and receive X10Events.
     * If this returns true, isX10Transmitter and isX10Receiver should also
     * both return true.
     */
    public boolean isX10Transceiver();
    /** Returns true if this connection can transmit X10Events */
    public boolean isX10Transmitter();
    /** Returns true if this connection can receive X10Events */
    public boolean isX10Receiver();
    
    /** This method returns the X10Transceiver for this connection.
     * @return null if this connection cannot both transmit and receive 
     */
    public X10Transceiver getX10Transceiver();
    /** This method returns the X10Transmitter for this connection.
     * @return null if this connection cannot transmit X10Events 
     */
    public X10Transmitter getX10Transmitter();
    /** This method returns the X10Receiver for this connection.
     * @return null if this connection cannot receive X10Events
     */
    public X10Receiver getX10Receiver();
    
    /** Returns the closest brightness percent this connection can deliver.
     * Most X10 media can not transmit brightness levels at the full
     * 64-bit precision of Java <code>double</code>s.  This method will
     * return the brightness value it will deliver if the given value is
     * specified.  This will allow a programmer to maintain accurate records
     * of what brightness values are being broadcast. Note that this only
     * applies to <i>outbound</i> X10 events, as inbound events will have the
     * precision of the driver's media.  Do not assume the inbound and
     * outbound brightness level precision is the same.  A driver may be able
     * to get inbound brightness levels at a higher precision than outbound
     * events, or vice versa.
     * @param desired The desired brightness level percentage to broadcast with
     * a dim or brighten command.
     * @return The actual brightness level percentage that will be broadcast
     * by the driver.
     * @throws java.lang.IllegalArgumentException if <code>desired</code> is
     * not within the range of 0.0 to 1.0 (0% to 100%)
     */
    public double getClosestBrightnessLevel(double desired);
    
    /** Returns an {@link javax.x10.util.X10Monitor} which is responsible for 
     * listening to the 
     * X10Connection and maintains the state of which units are addressed,
     * which units are on, and what their brightness level is. Note that 
     * X10Connections are not monitored by default and must be told to
     * monitor themselves with a call to {@link #setMonitored(true)}.
     *<P>
     *<B>NOTE ON ACCURACY:</B> An {@link javax.x10.util.X10Monitor} is only as accurate as
     * the events it is able to see. If the X10Connection is only a transmitter,
     * the monitor will not see events that your program did not send.  Many X10
     * controlled wall switches do not send X10 events when their state changes,
     * that is, when you press the wall switch with your finger. Those state
     * changes will not be recorded in the monitor.  You can update the state
     * of the monitor.
     */ 
    public X10Monitor getMonitor();
    
    /** Returns the truth that there is a monitor recording the state of
     * events.
     */
    public boolean isMonitored();
    
    /** Turns monitoring of the connection on and off depending on the given 
     * value.
     * @param monitored If true, turns monitoring on, otherwise it turns monitoring off.
     */
    public void setMonitored(boolean monitored);
    
    /** Closes the connection to the X10 media. */
    public void close() throws X10Exception;
}
