/*
 * Code.java
 *
 * Created on July 10, 2002, 12:37 AM
 */

package javax.x10.codes;

/** Subclasses of <code>Code</code> represent X10 codes.
 *
 * @see javax.x10.codes.HouseCode
 * @see javax.x10.codes.UnitCode
 * @see javax.x10.codes.FunctionCode
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public abstract class Code {
    
    private String name;
    
    /** This has package access so only classes in this package can 
     * call this. 
     */
    Code(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
