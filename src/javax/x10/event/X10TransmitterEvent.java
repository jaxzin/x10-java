/*
 * X10TransmitterEvent.java
 *
 * Created on July 13, 2002, 11:11 AM
 */

package javax.x10.event;

/** <code>X10TransmitterEvent</code> is an event created by an 
 * {@link javax.x10.X10Transmitter} that relates to it's internal 
 * {@link javax.x10.event.X10Event} queue.  They are delivered to 
 * {@link javax.x10.event.X10TransmitterListener}s that have registered with
 * {@link javax.x10.X10Transmitter#addX10TransmitterListener(X10TransmitterListener)}. 
 *
 * @see javax.x10.X10Transmitter
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public class X10TransmitterEvent extends java.util.EventObject {
    
    /** <code>X10TransmitterEvent.Type</code> is only used as an enumeration
     * pattern for the type of {@link javax.x10.event.X10TransmitterEvent}.
     * The only instances of this class that you can use are defined as
     * constants in {@link javax.x10.event.X10TransmitterEvent}.
     * @see javax.x10.event.X10TransmitterEvent#EVENT_DELIVERED
     * @see javax.x10.event.X10TransmitterEvent#EVENT_UNDELIVERED
     * @see javax.x10.event.X10TransmitterEvent#QUEUE_EMPTIED
     * @see javax.x10.event.X10TransmitterEvent#QUEUE_UPDATED
     */
    public static final class Type {
        private String name;
        private Type(String name) {
            this.name = name;
        }
        /** Returns a text representation of the Type. */
        public String toString() {
            return name;
        }
    }
    
    /** <code>X10TransmitterEvent</code>s of this type signify that the
     * {@link javax.x10.X10Transmitter}'s internal queue has been emptied.
     */
    public static final Type QUEUE_EMPTIED      = new Type("QUEUE_EMPTIED");
    /** <code>X10TransmitterEvent</code>s of this type signify that the
     * {@link javax.x10.X10Transmitter}'s internal queue has been updated.
     */
    public static final Type QUEUE_UPDATED      = new Type("QUEUE_UPDATED");
    /** <code>X10TransmitterEvent</code>s of this type signify that the
     * {@link javax.x10.X10Transmitter} was not able to deliver the related
     * {@link javax.x10.event.X10Event}.
     */
    public static final Type EVENT_UNDELIVERED  = new Type("EVENT_UNDELIVERED");
    /** <code>X10TransmitterEvent</code>s of this type signify that the
     * {@link javax.x10.X10Transmitter} successfully delivered the related
     * {@link javax.x10.event.X10Event}.
     */
    public static final Type EVENT_DELIVERED  = new Type("EVENT_DELIVERED");
    
    private X10Event x10Event;
    private Type type;
    
    /** Creates a new instance of X10TransmitterEvent
     * @param source An Object that is the source of this event.
     * @param relatedEvent A related X10Event, if there is one.
     */
    public X10TransmitterEvent(Object source, Type type, X10Event relatedEvent) {
        super(source);
        this.x10Event = relatedEvent;
        this.type = type;
    }
    
    /** Returns the X10Event that is related to this event, or null if there 
     * is no related event.<LI>For <code>X10TransmitterEvent</code>s of type 
     * {@link #QUEUE_EMPTIED}, the X10Event returned will be the last event
     * in the queue at the time it was emptied.</LI> <LI>For 
     * <code>X10TransmitterEvent</code>s of type {@link #QUEUE_UPDATED}, the 
     * X10Event returned will be the event that was added to the queue.</LI><LI>For
     * <code>X10TransmitterEvent</code>s of type {@link #EVENT_UNDELIVERED}, the 
     * X10Event returned will be the event that was not delivered.</LI><LI>For
     * <code>X10TransmitterEvent</code>s of type {@link #EVENT_DELIVERED}, the 
     * X10Event returned will be the event that was not delivered.</LI>
     */
    public X10Event getX10Event() {
        return this.x10Event;
    }
    
    /** Returns the type of the current event.  This will be one of the Type
     * constants {@link #QUEUE_EMPTIED}, {@link #QUEUE_UPDATED}, 
     * {@link #EVENT_UNDELIVERED}, or {@link #EVENT_DELIVERED}.
     */
    public Type getType() {
        return this.type;
    }
}
