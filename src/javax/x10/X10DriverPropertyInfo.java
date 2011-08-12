/*
 * X10DriverPropertyInfo.java
 *
 * Created on July 17, 2002, 9:12 PM
 */

package javax.x10;

/** X10Driver properties for making a connection. The X10DriverPropertyInfo 
 * class is of interest only to advanced programmers who need to interact 
 * with an X10Driver via the method getDriverProperties to discover and 
 * supply properties for connections. 
 *
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public class X10DriverPropertyInfo {
    
    /** An array of possible values if the value for the field 
     * <code>X10DriverPropertyInfo.value</code> may be selected from a 
     * particular set of values; otherwise null. 
     */
    public String[] choices = null;
    
    /** A brief description of the property, which may be null. */
    public String description = "";
    
    /** The name of the property. */
    public String name = "";
    
    /** The required field is <code>true</code> if a value must be supplied 
     * for this property during <code>X10Driver.getX10Connection</code> and 
     * <code>false</code> otherwise. 
     */
    public boolean required = false;
    
    /** The <cdoe>value</code> field specifies the current value of the 
     * property, based on a combination of the information supplied to the 
     * method <code>getPropertyInfo</code>, the Java environment, and the 
     * driver-supplied default values. This field may be null if no value is 
     * known.
     */ 
    public String value = "";
    
    /** Constructs a <code>X10DriverPropertyInfo</code> object with a name and 
     * value; other members default to their initial values. 
     * @param name the name of the property
     * @param value the current value, which may be null
     */
    public X10DriverPropertyInfo(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
}
