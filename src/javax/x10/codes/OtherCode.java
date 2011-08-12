/*
 * OtherCode.java
 *
 * Created on July 10, 2002, 12:40 AM
 */

package javax.x10.codes;

/** Subclasses of this represent codes other than HouseCodes.  An 
 * {@link javax.x10.event.X10Event} is either an 
 * {@link javax.x10.event.X10Event#ADDRESS} event or a 
 * {@link javax.x10.event.X10Event#FUNCTION} event. The 
 * <code>otherCode</code> of the event will be either a constant from
 * {@link javax.x10.codes.UnitCode} or a constant from
 * {@link javax.x10.codes.FunctionCode}, respectively.  These
 * two types of codes are implementations of OtherCode.
 *
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public abstract class OtherCode extends Code {
    
    /** This has package access so only classes in this package can 
     * call this. 
     */
    OtherCode(String name) {
        super(name);
    }

}
