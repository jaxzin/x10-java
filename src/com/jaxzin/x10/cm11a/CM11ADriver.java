/*
 * CM11ADriver.java
 *
 * Created on July 25, 2002, 10:12 PM
 */

package com.jaxzin.x10.cm11a;

import java.util.*;

import javax.x10.*;
import javax.comm.*;

/**
 *
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public class CM11ADriver implements javax.x10.X10Driver {
    
    public static final String PROP_PORTNAME    = "portName";
    public static final String PROP_BAUDRATE    = "baudRate";
    public static final String PROP_DATABITS    = "dataBits";
    public static final String PROP_STOPBITS    = "stopBits";
    public static final String PROP_PARITY      = "parity";
    public static final String PROP_MAXATTEMPTS = "maxAttempts";
    
    
    /** On loading the class, 
     * register the driver with the X10DriverManager 
     */
    static {
        try {
            X10DriverManager.registerX10Driver(new CM11ADriver());
        } catch(X10Exception e) {
            System.err.println("Unable to register CM11ADriver with X10DriverManager.");
            e.printStackTrace(System.err);
        }
    }
    
    /** Creates a new instance of CM11ADriver */
    public CM11ADriver() {
    }
    
    /** Retrieves whether the driver thinks that it can open a connection
     * to the given URL. Typically drivers will return <code>true</code> if
     * they understand the subprotocol specified in the URL and
     * <code>false</code> if they do not.
     * @param url the URL of the X10 connection
     * @return <code>true</code> if this driver understands the given URL;
     * <code>false</code> otherwise
     * @throws X10Exception if an error occurs
     */
    public boolean acceptsURL(String url) throws X10Exception {
        return url.toUpperCase().startsWith("X10:CM11A:");
    }
    
    /** Attempts to make an X10 connection to the given URL(via serial, web,
     * etc.). The driver should return <code>null</code> if it realizes it is the wrong
     * kind of driver to connect to the given URL. This will be common, as
     * when the X10 driver manager is asked to connect to a given URL it passes
     * the URL to each loaded driver in turn.<P>
     * The driver should throw an X10Exception if it is the right driver to
     * connect to the given URL but has trouble connecting to the X10 network.<P>
     * The {@link java.util.Properties} argument can be used to pass arbitrary
     * string tag/value pairs as connection arguments.
     * @param url the URL of the X10 connection to which to connect
     * @param info a list of arbitrary string tag/value pairs as connection arguments.
     * @return an X10Connection object that represents a connection to the URL
     * @throws X10Exception if an error occurs
     */
    public X10Connection connect(String url, Properties info) throws X10Exception {
        if(!acceptsURL(url))
            return null;
        // Initialize the properties
        
        
        // Get port name, first try from url, then from info
        String portName = getPortName(url,info);
        if(portName == null)
            throw new X10Exception("Port name not specified in URL or property \""+PROP_PORTNAME+"\"");
        
        // Get baud rate
        int baudRate = parseBaudRate(getBaudRate(url,info));
        
        // Get data bits
        int dataBits = parseDataBits(getDataBits(url,info));
        
        // Get stop bits
        int stopBits = parseStopBits(getStopBits(url,info));
        
        // Get parity
        int parity = parseParity(getParity(url,info));
        
        // Get max attempts
        int maxAttempts = parseMaxAttempts(getMaxAttempts(url,info));
        
        // Instantiate the CM11A driver with the values read
        CM11A cm11a = new CM11A(portName,
                                baudRate,
                                dataBits,
                                stopBits,
                                parity);
        cm11a.setMaximumAttempts(maxAttempts);
        // Open the serial port
        try {
            cm11a.open();
        } catch (Exception e) {
            throw new X10Exception(e.getMessage());
        }
        return new CM11AConnection(cm11a);
    }
    
    /** Retrieves the driver's major version number. Initially this should be 1.
     * @return this driver's major version number
     */
    public int getMajorVersion() {
        return 1;
    }
    
    /** Retrieves the driver's minor version number. Initially this should be 0.
     * @return this driver's minor version number
     */
    public int getMinorVersion() {
        return 0;
    }
    
    /** Gets information about the possible properties for this driver.<P>
     * The <code>getPropertyInfo</code> method is intended to allow a generic
     * GUI tool to discover what properties it should prompt a human for in
     * order to get enough information to connect to the X10 network. Note that
     * depending on the values the human has supplied so far, additional
     * values may become necessary, so it may be necessary to iterate though
     * several calls to the <code>getPropertyInfo</code> method
     * @param url the URL of the X10 connection to which to connect
     * @param info a proposed list of tag/value pairs that will be sent on connect open
     * @return an array of <code>X10DriverPropertyInfo</code> objects describing possible properties. This array may be an empty array if no properties are required
     * @throws X10Exception if an error occurs
     */
    public X10DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws X10Exception {
        // Initialize the array
        X10DriverPropertyInfo[] arr = new X10DriverPropertyInfo[6];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = new X10DriverPropertyInfo(null,null);
        }
        
        // Build port name
        arr[0].name         = PROP_PORTNAME;
        arr[0].value        = getPortName(url,info);
        arr[0].required     = true;
        arr[0].description  = "Serial port name";
        arr[0].choices      = getPortNameChoices();
        
        // Build baud rate
        arr[1].name         = PROP_BAUDRATE;
        arr[1].value        = getBaudRate(url,info);
        arr[1].required     = false;
        arr[1].description  = "Serial port baud rate";
        arr[1].choices      = getBaudRateChoices();
        
        // Build data bits
        arr[2].name         = PROP_DATABITS;
        arr[2].value        = getDataBits(url,info);
        arr[2].required     = false;
        arr[2].description  = "Serial port data bits";
        arr[2].choices      = getDataBitsChoices();
        
        // Build stop bits
        arr[3].name         = PROP_STOPBITS;
        arr[3].value        = getStopBits(url,info);
        arr[3].required     = false;
        arr[3].description  = "Serial port stop bits";
        arr[3].choices      = getStopBitsChoices();
        
        // Build parity
        arr[4].name         = PROP_PARITY;
        arr[4].value        = getParity(url,info);
        arr[4].required     = false;
        arr[4].description  = "Serial port parity";
        arr[4].choices      = getParityChoices();
        
        // Build maximum attempts
        arr[5].name         = PROP_MAXATTEMPTS;
        arr[5].value        = getMaxAttempts(url,info);
        arr[5].required     = false;
        arr[5].description  = "Maximum number of times the driver will attempt to deliver a message to the CM11A.";
        arr[5].choices      = getMaxAttemptsChoices();
        
        return arr;
    }
    
    private String getPortName(String url, Properties info) {
        String portName = null;
        if(url.length() > 10)
            portName = url.substring(10);
        if(portName == null)
            portName = info.getProperty(PROP_PORTNAME);
        return portName;
    }
    
    private String getBaudRate(String url, Properties info) {
        return info.getProperty(PROP_BAUDRATE, "4800");
    }
    
    private String getDataBits(String url, Properties info) {
        return info.getProperty(PROP_DATABITS, "8");
    }
    
    private String getStopBits(String url, Properties info) {
        return info.getProperty(PROP_STOPBITS, "1");
    }
    
    private String getParity(String url, Properties info) {
        return info.getProperty(PROP_PARITY, "None");
    }
    
    private String getMaxAttempts(String url, Properties info) {
        return info.getProperty(PROP_MAXATTEMPTS, "20");
    }
    
    private String[] getPortNameChoices() throws X10Exception {
        Vector portNamesV = new Vector();
        try {
            CommPortIdentifier portId;
            Enumeration portList = CommPortIdentifier.getPortIdentifiers();

            while (portList.hasMoreElements()) {
                portId = (CommPortIdentifier) portList.nextElement();
                if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    portNamesV.add(portId.getName());
                }
            }
        } catch(Exception e) {
            throw new X10Exception(e.getMessage());
        }
           
        if(portNamesV.size() == 0)
            return new String[]{""};
            
        String[] portNames = new String[portNamesV.size()];
        int index = 0;
        for(Iterator i = portNamesV.iterator();i.hasNext();index++) {
            String portName = (String)i.next();
            portNames[index] = portName;
        }
        
        return portNames;
    }
    
    private String[] getBaudRateChoices() {
        return new String[] {
            "2400",
            "4800",
            "9600",
            "14400",
            "28800",
            "57600",
            "115200"
        };
    }
    
    private String[] getDataBitsChoices() {
        return new String[] {
            "5",
            "6",
            "7",
            "8"
        };
    }
    
    private String[] getStopBitsChoices() {
        return new String[] {
            "1",
            "1.5",
            "2"
        };
    }
    
    private String[] getParityChoices() {
        return new String[] {
            "None",
            "Even",
            "Odd",
            "Mark",
            "Space"
        };
    }
    
    private String[] getMaxAttemptsChoices() {
        return null;
    }
    
    private int parseBaudRate(String str) throws X10Exception {
        try {
            return Integer.parseInt(str);
        } catch(NumberFormatException e) {
            throw new X10Exception("The baud rate of '"+str+"' is not an integer.");
        }
    }
    
    private int parseDataBits(String str) throws X10Exception {
        str = str.trim();
        if("5".equalsIgnoreCase(str))
            return SerialPort.DATABITS_5;
        else if("6".equalsIgnoreCase(str))
            return SerialPort.DATABITS_6;
        else if("7".equalsIgnoreCase(str))
            return SerialPort.DATABITS_7;
        else if("8".equalsIgnoreCase(str))
            return SerialPort.DATABITS_8;
        else
            throw new X10Exception("The data bits value of '"+str+"' is not valid.");
    }
    
    private int parseStopBits(String str) throws X10Exception {
        str = str.trim();
        if("1".equalsIgnoreCase(str))
            return SerialPort.STOPBITS_1;
        else if("1.5".equalsIgnoreCase(str))
            return SerialPort.STOPBITS_1_5;
        else if("2".equalsIgnoreCase(str))
            return SerialPort.STOPBITS_2;
        else
            throw new X10Exception("The stop bits value of '"+str+"' is not valid.");
    }
    
    private int parseParity(String str) throws X10Exception {
        str = str.trim();
        if("None".equalsIgnoreCase(str))
            return SerialPort.PARITY_NONE;
        else if("Even".equalsIgnoreCase(str))
            return SerialPort.PARITY_EVEN;
        else if("Odd".equalsIgnoreCase(str))
            return SerialPort.PARITY_ODD;
        else if("Mark".equalsIgnoreCase(str))
            return SerialPort.PARITY_MARK;
        else if("Space".equalsIgnoreCase(str))
            return SerialPort.PARITY_SPACE;
        else
            throw new X10Exception("The parity value of '"+str+"' is not valid.");
    }
    
    private int parseMaxAttempts(String str) throws X10Exception {
        try {
            return Integer.parseInt(str);
        } catch(NumberFormatException e) {
            throw new X10Exception("The maximum attempts of '"+str+"' is not an integer.");
        }
    }
}
