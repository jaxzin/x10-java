/*
 * X10Listener.java
 *
 * Created on July 9, 2002, 11:02 PM
 */

package javax.x10.event;

/** X10Listener defines the interface for an object that listens
 * for messages received by an {@link javax.x10.X10Receiver}
 *
 * @see javax.x10.event.X10Event
 * @see javax.x10.event.X10Adapter
 * @see javax.x10.X10Receiver
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public interface X10Listener extends java.util.EventListener {
    
    /** An addressing event was received. Use <code>e.getHouseCode()</code> and 
     * <code>e.getUnitCode()</code> to discover the unit that was addressed.
     */
    public void address(X10Event e);
    /** An "all units off" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for.
     */
    public void allUnitsOff(X10Event e);
    /** An "all lights off" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for.
     */
    public void allLightsOff(X10Event e);
    /** An "on" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for.
     */
    public void on(X10Event e);
    /** An "off" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for.
     */
    public void off(X10Event e);
    /** A "dim" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for and
     * <code>e.getBrightnessLevels()</code> to discover the amount dimmed.
     */
    public void dim(X10Event e);
    /** A "brighten" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for and
     * <code>e.getBrightnessLevels()</code> to discover the amount brightened.
     */
    public void brighten(X10Event e);
    /** An "all lights on" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for.
     */
    public void allLightsOn(X10Event e);
    /** An "extended code" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for,
     * <code>e.getData()</code> to get the data byte of the extended event and
     * <code>e.getCommand()</code> to get the command byte of the extended event.
     */
    public void extendedCode(X10Event e);
    /** A "hail request" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for.
     */
    public void hailRequest(X10Event e);
    /** A "hail acknowledge" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for.
     */
    public void hailAcknowledge(X10Event e);
    /** A "preset dim 1" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for.
     */
    public void presetDim1(X10Event e);
    /** A "preset dim 2" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for.
     */
    public void presetDim2(X10Event e);
    /** An "extended data transfer" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for.
     */
    public void extendedDataTransfer(X10Event e);
    /** A "status on" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for.
     */
    public void statusOn(X10Event e);
    /** A "status off" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for.
     */
    public void statusOff(X10Event e);
    /** A "status request" event was received. Use <code>e.getHouseCode()</code>
     * to discover which house code the event was for.
     */
    public void statusRequest(X10Event e);
}
