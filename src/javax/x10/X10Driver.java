/*
 * X10Driver.java
 *
 * Created on July 16, 2002, 11:07 PM
 */

package javax.x10;

import java.util.Properties;
import java.sql.*; // For javadoc only!!

/**
 * X10Driver defines the interface for objects that are responsible for
 * registering with the {@link javax.x10.X10DriverManager} and
 * allowing a programmer to get a reference to an
 * {@link javax.x10.X10Connection}.<P>
 * The X10DriverManager system is based on the JDBC DriverManager system and
 * works the same way.<P>
 * The X10DriverManager will try to load as many drivers as it can find and then
 * for any given connection request, it will ask each driver in turn to try to
 * connect to the target URL.<P>
 * It is strongly recommended that each X10Driver class should be small and
 * standalone so that the X10Driver class can be loaded and queried without
 * bringing in vast quantities of supporting code.<P>
 * When an X10Driver class is loaded, it should create an instance of itself
 * and register it with the X10DriverManager. This means that a user can load
 * and register a driver by calling<P>
 * <BLOCKQUOTE><code>Class.forName("my.x10.Driver")</code></BLOCKQUOTE>
 *
 * @see javax.x10.X10DriverManager
 * @see javax.x10.X10Connection
 * @see java.sql.Driver
 * @see java.sql.DriverManager
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public interface X10Driver {

    /**
     * Retrieves whether the driver thinks that it can open a connection
     * to the given URL. Typically drivers will return <code>true</code> if 
     * they understand the subprotocol specified in the URL and 
     * <code>false</code> if they do not. 
     * @param url the URL of the X10 connection
     * @return <code>true</code> if this driver understands the given URL; 
     * <code>false</code> otherwise
     * @throws X10Exception if an error occurs
     */
    public boolean acceptsURL(String url) throws X10Exception;
    
    /**
     * Attempts to make an X10 connection to the given URL(via serial, web,
     * etc.). The driver should return <code>null</code> if it realizes it is the wrong 
     * kind of driver to connect to the given URL. This will be common, as 
     * when the X10 driver manager is asked to connect to a given URL it passes 
     * the URL to each loaded driver in turn.<P>
     * The driver should throw an X10Exception if it is the right driver to 
     * connect to the given URL but has trouble connecting to the database.<P>
     * The {@link java.util.Properties} argument can be used to pass arbitrary 
     * string tag/value pairs as connection arguments. 
     * @param url the URL of the X10 connection to which to connect
     * @param info a list of arbitrary string tag/value pairs as connection arguments. 
     * @return an X10Connection object that represents a connection to the URL
     * @throws X10Exception if an error occurs
     */
    public X10Connection connect(String url, Properties info) throws X10Exception;
    
    /**
     * Retrieves the driver's major version number. Initially this should be 1.
     * @return this driver's major version number
     */
    public int getMajorVersion();
    
    /**
     * Retrieves the driver's minor version number. Initially this should be 0.
     * @return this driver's minor version number
     */
    public int getMinorVersion();
    
    /**
     * Gets information about the possible properties for this driver.<P> 
     * The <code>getPropertyInfo</code> method is intended to allow a generic 
     * GUI tool to discover what properties it should prompt a human for in 
     * order to get enough information to connect to a database. Note that 
     * depending on the values the human has supplied so far, additional 
     * values may become necessary, so it may be necessary to iterate though 
     * several calls to the <code>getPropertyInfo</code> method
     * @param url the URL of the X10 connection to which to connect
     * @param info a proposed list of tag/value pairs that will be sent on connect open 
     * @return an array of <code>X10DriverPropertyInfo</code> objects describing possible properties. This array may be an empty array if no properties are required
     * @throws X10Exception if an error occurs
     */
    public X10DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws X10Exception;
    
}
