/*
 * X10DriverManager.java
 *
 * Created on July 17, 2002, 9:57 PM
 */

package javax.x10;

import java.util.*;
import java.sql.*; // For javadoc only!!

/**
 * The basic service for managing a set of X10 drivers.
 * <P>
 * <B>NOTE:</B>The management of {@link javax.x10.X10Driver}s is
 * strongly based on the way Sun handles JDBC {@link java.sql.Driver}
 * management.  If you know how to use JDBC's {@link java.sql.DriverManager}
 * to get database {@link java.sql.Connection}s, then you know how to use 
 * <code>X10DriverManager</code> to get {@link javax.x10.X10Connection}s.
 * <P>
 * As part of its initialization, the <code>X10DriverManager</code> class will 
 * attempt to load the driver classes referenced in the "x10.drivers" system 
 * property. This allows a user to customize the X10 Drivers used by their 
 * applications. For example in your system properties file you might 
 * specify:<P>
 * <CODE> x10.drivers=com.example.Driver:org.example.x10.Driver:not.right.ourDriver</CODE>
 * <P>
 * A program can also explicitly load X10 drivers at any time. For example, 
 * the my.x10.Driver is loaded with the following statement:<P>
 * <CODE> Class.forName("my.x10.Driver");</CODE>
 * <P>
 * When the method <code>getX10Connection</code> is called, the 
 * <code>X10DriverManager</code> will attempt to locate a suitable driver 
 * from amongst those loaded at initialization and those loaded explicitly 
 * using the same classloader as the current applet or application. 
 *
 * @see javax.x10.X10Driver
 * @see javax.x10.X10Connection
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public class X10DriverManager {
    
    /** Holds the registered X10Driver objects */
    private static Vector registeredDrivers = new Vector(); 
    
    /** Contains all static methods, cannot instantiate */
    private X10DriverManager() {
    }
    
    /** The static initializer, loads drivers named in the
     * "x10.drivers" system property.
     */
    static {
        try {
            // Get the system property "x10.drivers"
            String driverList = System.getProperty("x10.drivers");

            // If we read the property...
            if(driverList != null) {
                // Split the driver class names
                String[] drivers = driverList.split(":");
                // Iterate through the driver class names
                for(int i = 0; i < drivers.length; i++) {
                    // Get the current driver name(ignore whitespace)
                    String driver = drivers[i].trim();
                    // If its not just whitespace, try to load the class
                    if(!driver.equals("")) {
                        try {
                            Class.forName(driver);
                        } catch(ClassNotFoundException e) {
                            System.err.println("X10DriverManager was unable to load the class \""+driver+"\" listed in the system property \"x10.drivers\"");
                        }
                    }
                }
            }
        } catch (SecurityException e) {
            System.err.println("X10DriverManager was unable to read system property \"x10.drivers\" due to SecurityException");
            e.printStackTrace(System.err);
        } catch (NoSuchMethodError e) { 
            // thrown on String.split for versions under 1.4
            System.err.println("javax.x10 required Java version 1.4 or higher");
            throw e;
        }
    }
    
    /** Registers the given driver with the <code>X10DriverManager</code>. 
     * A newly-loaded driver class should call the method 
     * <code>registerX10Driver</code> to make itself known to the 
     * <code>X10DriverManager</code>. 
     * @param driver the new X10Driver that is to be registered with the <code>X10DriverManager</code>
     * @throws X10Exception if an error occurs
     */
    public static void registerX10Driver(X10Driver driver) throws X10Exception {
        registeredDrivers.add(driver);
    }

    /** Drops a driver from the <code>X10DriverManager</code>'s list. Applets 
     * can only deregister drivers from their own classloaders. 
     * @param driver the X10 Driver to drop 
     * @throws X10Exception if an error occurs
     */
    public static void deregisterX10Driver(X10Driver driver) {
        registeredDrivers.remove(driver);
    }
    
    /** Attempts to establish a connection to the given database URL. 
     * The <code>X10DriverManager</code> attempts to select an appropriate 
     * driver from the set of registered X10 drivers.
     * @param url an X10 url of the form <code>x10:<i>subprotocol</i>:<i>subname</i></code>
     * @return a connection to the URL 
     * @throws X10Exception if an error occurs
     */
    public static X10Connection getX10Connection(String url) throws X10Exception {
        // Iterate through the drivers, try to get an X10Connection, 
        // and return if you find one
        for(Iterator i = getDrivers();i.hasNext();) {
            // Get the next registered driver
            X10Driver driver = (X10Driver)i.next();
            // Try to get an X10Connection which will throw an X10Exception 
            // if it understands the URL but is missing necessary info
            X10Connection conn = driver.connect(url, new Properties());
            // If we found one, return it!
            if(conn != null)
                return conn;
        }
        // We didn't find a suitable driver so..
        return null;
    }
    
    /** Attempts to establish a connection to the given database URL. 
     * The <code>X10DriverManager</code> attempts to select an appropriate 
     * driver from the set of registered X10 drivers.
     * @param url an X10 url of the form <code>x10:<i>subprotocol</i>:<i>subname</i></code>
     * @param info a list of arbitrary string tag/value pairs as connection arguments
     * @return a connection to the URL 
     * @throws X10Exception if an error occurs
     */
    public static X10Connection getX10Connection(String url, Properties info) throws X10Exception {
        if(info == null)
            info = new Properties();
        // Iterate through the drivers, try to get an X10Connection, 
        // and return if you find one
        for(Iterator i = getDrivers();i.hasNext();) {
            // Get the next registered driver
            X10Driver driver = (X10Driver)i.next();
            // Try to get an X10Connection; this will throw an X10Exception 
            // if it understands the URL but is missing necessary info
            X10Connection conn = driver.connect(url, info);
            // If we found one, return it!
            if(conn != null)
                return conn;
        }
        // We didn't find a suitable driver so..
        return null;
    }
    
    /** Attempts to locate a driver that understands the given URL. 
     * The <code>X10DriverManager</code> attempts to select an appropriate 
     * driver from the set of registered X10 drivers. 
     * @param url an X10 url of the form <code>x10:<i>subprotocol</i>:<i>subname</i></code>
     * @return an <code>X10Driver</code> object representing a driver that can connect to the given URL 
     * @throws X10Exception if an error occurs
     */
    public static X10Driver getX10Driver(String url) throws X10Exception {
        // Iterate through the drivers, find one that accepts the URL,
        // return that driver
        for(Iterator i = getDrivers();i.hasNext();) {
            // Get the next registered driver
            X10Driver driver = (X10Driver)i.next();
            // See if this driver accepts the URL, and return it
            // if it does
            if(driver.acceptsURL(url))
                return driver;
        }
        // We didn't find a suitable driver so..
        return null;
    }
    
    /** Retrieves an {@link java.util.Iterator} with all of the currently 
     * loaded X10 drivers to which the current caller has access.<P>
     * <B>Note:</B> The classname of a driver can be found using 
     * <code>d.getClass().getName()</code>
     * @return the list of {@link javax.x10.X10Driver}s loaded by the 
     * caller's class loader
     */
    public static Iterator getDrivers() {
        return registeredDrivers.iterator();
    }
    
}
