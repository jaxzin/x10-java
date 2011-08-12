/*
 * X10Exception.java
 *
 * Created on July 17, 2002, 8:58 PM
 */

package javax.x10;

/** An exception that provides information on an X10 connection error or other errors. 
 *
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public class X10Exception extends java.lang.Exception {
    
    private X10Exception nextException = null;
    
    /** Creates a new instance of X10Exception */
    public X10Exception() {
        super();
    }
    
    /** Creates a new instance of X10Exception with a reason*/
    public X10Exception(String reason) {
        super(reason);
    }
    
    /** Retrieves the exception chained to this <code>X10Exception</code> object. 
     * @return the next <code>X10Exception</code> object in the chain; <code>null</code> if there are none
     * @see #setNextException
     */
    public X10Exception getNextException() {
        return this.nextException;
    }
    
    /** Adds an <code>X10Exception</code> object to the end of the chain. 
     * @param extends the new exception that will be added to the end of the <code>X10Exception</code> chain
     * @see #getNextException
     */
    public void setNextException(X10Exception ex) {
        this.nextException = ex;
    }
    
}
