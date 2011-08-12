/*
 * CM11A.java
 *
 * Created on July 20, 2002, 8:04 PM
 */

package com.jaxzin.x10.cm11a;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.comm.*;

import javax.x10.*;
import javax.x10.codes.*;
import javax.x10.event.*;

import com.jaxzin.util.Nibble;
import com.jaxzin.util.Daemon;

/** <code>CM11A</code> is an implementation of {@link javax.x10.X10Transceiver} for
 * the CM11A computer<->X10 bridge.  Although you can instantiate this class directly,
 * you should create this object with the following code:<BR>
 *<BLOCKQUOTE>
 *<code>Class.forName("com.jaxzin.x10.cm11a.CM11ADriver");<BR>
 * X10Connection conn = X10DriverManager.getX10Connection("x10:cm11a:<i>MY_COM_PORT</i>");<BR>
 * X10Transceiver x10 = conn.getTransceiver();<BR>
 *</code>
 *</BLOCKQUOTE>
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public class CM11A implements javax.x10.X10Transceiver {
    
    // Setup logging facilities
    private static final Logger LOG = 
            Logger.getLogger(CM11A.class.getName());
    
    private class CM11ASerialListener implements SerialPortEventListener {
        
        private CM11ASerialListener() {
        }
        
        public void serialEvent(SerialPortEvent event) {
            switch(event.getEventType()) {
                case SerialPortEvent.DATA_AVAILABLE:
                    handleDataAvailable();
                    break;
            }
        }
        
        private void handleDataAvailable() {
            // Lock the output stream
            synchronized(outputStream) {
                // Lock the input stream
                synchronized(inputStream) {
                    // Stop the listener from being notified of new data(not fail-safe but wastes less cycles at least)
                    serialPort.notifyOnDataAvailable(false);
                    try {
                        // If there really is data to read...
                        if(inputStream.available() > 0) {
                            // ...read the incoming data...
                            byte data = inputStream.readByte();
                            // ...handle incoming data... 
                            switch(data) {
                                case MSG_POWER_FAILURE:
                                    handlePowerFailure();
                                    break;
                                case MSG_EVENT_RECEIVED:
                                    handleEventReceived();
                                    break;
                                default:
                                    // Incoming data is not recognized!! Uh-oh!!
                                    clearInputStream();
                            }
                        }
                    } catch (IOException e) {
                    }
                    // Tell to start notifying the listener of new data again
                    serialPort.notifyOnDataAvailable(true);
                }
            }
        }
    }
    
    private class X10ListenerNotifier implements Runnable {
        X10Event event;
        private X10ListenerNotifier(X10Event event) {
            this.event = event;
        }
        public void run() {
            // Lock the list of X10Listeners
            synchronized(x10Listeners) {
                // Iterate through the list of X10Listeners
                for(Iterator i = x10Listeners.iterator();i.hasNext();) {
                    X10Listener listener = (X10Listener)i.next();
                    if(event.getType() == X10Event.ADDRESS) {
                            listener.address(event);
                    } else if(event.getType() == X10Event.FUNCTION) {
                        if(event.getOtherCode() == FunctionCode.ALL_LIGHTS_OFF) {
                            listener.allLightsOff(event);
                        } else if(event.getOtherCode() == FunctionCode.ALL_LIGHTS_ON) {
                            listener.allLightsOn(event);
                        } else if(event.getOtherCode() == FunctionCode.ALL_UNITS_OFF) {
                            listener.allUnitsOff(event);
                        } else if(event.getOtherCode() == FunctionCode.BRIGHTEN) {
                            listener.brighten(event);
                        } else if(event.getOtherCode() == FunctionCode.DIM) {
                            listener.dim(event);
                        } else if(event.getOtherCode() == FunctionCode.EXTENDED_CODE) {
                            listener.extendedCode(event);
                        } else if(event.getOtherCode() == FunctionCode.EXTENDED_DATA_TRANSFER) {
                            listener.extendedDataTransfer(event);
                        } else if(event.getOtherCode() == FunctionCode.HAIL_ACKNOWLEDGE) {
                            listener.hailAcknowledge(event);
                        } else if(event.getOtherCode() == FunctionCode.HAIL_REQUEST) {
                            listener.hailRequest(event);
                        } else if(event.getOtherCode() == FunctionCode.OFF) {
                            listener.off(event);
                        } else if(event.getOtherCode() == FunctionCode.ON) {
                            listener.on(event);
                        } else if(event.getOtherCode() == FunctionCode.PRESET_DIM_1) {
                            listener.presetDim1(event);
                        } else if(event.getOtherCode() == FunctionCode.PRESET_DIM_2) {
                            listener.presetDim2(event);
                        } else if(event.getOtherCode() == FunctionCode.STATUS_OFF) {
                            listener.statusOff(event);
                        } else if(event.getOtherCode() == FunctionCode.STATUS_ON) {
                            listener.statusOn(event);
                        } else if(event.getOtherCode() == FunctionCode.STATUS_REQUEST) {
                            listener.statusRequest(event);
                        }
                    }
                }
            }
        }
    }
    
    private class X10TransmitterListenerNotifier implements Runnable {
        X10TransmitterEvent event;
        private X10TransmitterListenerNotifier(X10TransmitterEvent event) {
            this.event = event;
        }
        public void run() {
            // Lock the list of X10TransmitterListeners
            synchronized(x10TransmitterListeners) {
                // Iterate through the list of X10TransmitterListeners
                for(Iterator i = x10TransmitterListeners.iterator();i.hasNext();) {
                    X10TransmitterListener listener = (X10TransmitterListener)i.next();
                    if(event.getType() == X10TransmitterEvent.QUEUE_EMPTIED) {
                            listener.queueEmptied(event);
                    } else if(event.getType() == X10TransmitterEvent.QUEUE_UPDATED) {
                            listener.queueUpdated(event);
                    } else if(event.getType() == X10TransmitterEvent.EVENT_UNDELIVERED) {
                            listener.eventUndelivered(event);
                    }
                }
            }
        }
    }
    
    private class X10Talker extends Daemon {
        /** This method is called at the beginning of each loop to test
         * if daemon should continue running.
         */
        public boolean isStopCondition() {
            synchronized(x10Events) {
                return x10Events.isEmpty();
            }
        }

        /** This method is called repetitively while the daemon is running.
         * If the <code>stop()</code> method of the daemon is called externally
         * while this method is running, it will complete and not run again.  It
         * is thread-safe.
         */
        public void runBody() {
            X10Event event;
            boolean isEmpty = false;
            synchronized(x10Events) {
                event = (X10Event)x10Events.removeFirst();
                if(x10Events.isEmpty())
                    isEmpty = true;
            }
            if(isEmpty)
                notifyX10TransmitterListeners(new X10TransmitterEvent(this,X10TransmitterEvent.QUEUE_EMPTIED,event));

            if(!writeToStream(event)) {
                notifyX10TransmitterListeners(new X10TransmitterEvent(this,X10TransmitterEvent.EVENT_UNDELIVERED,event));
            }
        }
    
        /** Writes the event to serial port's output stream.  This also 
         * auto recovers the CM11A if it is not in a state to send messages.
         * @param event The event to deliver
         * @returns the truth that the event was successfully delivered
         */
        private boolean writeToStream(X10Event event) {
            boolean success = false;

            // Convert the X10Event to bytes formatted for the CM11A
            byte[] bytes = getBytes(event);

            // Lock the output stream
            synchronized(outputStream) {
                // Lock the input stream
                synchronized(inputStream) {
                    // Stop the listener from being notified of new data(not fail-safe but wastes less cycles at least)
                    serialPort.notifyOnDataAvailable(false);
                    try {
                        // Keep looping until done
                        boolean done = false;
                        int attempt  = 0;
                        while(!done) {
                            // Begin by blindly writing the event to the CM11A
                            outputStream.write(bytes);
                            outputStream.flush();

                            // Read what should be the checksum
                            byte checksum = inputStream.readByte();
                            // Figure out if the checksum is correct
                            boolean checksum_correct = (checksum == getChecksum(bytes));
                            // If the checksum is correct...
                            if(checksum_correct) {
                                // Tell the CM11A the checksum was correct
                                outputStream.write(MSG_ACKNOWLEDGE);
                                outputStream.flush();
                                // Read the CM11A's reply
                                byte reply = inputStream.readByte();
                                // Figure out what to do from the reply
                                switch(reply) {
                                    case MSG_READY:
                                        // Event was sent successfully!!
                                        LOG.info("Event was successfully sent:"+event);
                                        success = true;
                                        done = true;
                                        break;
                                    case MSG_POWER_FAILURE:
                                        handlePowerFailure();
                                        break;
                                    case MSG_EVENT_RECEIVED:
                                        handleEventReceived();
                                        break;
                                    default:
                                        // Something's really wrong, because the reply
                                        // was something the CM11A couldn't possibly send, but try anyway
                                }
                            } else {
                                // Otherwise the checksum was incorrect...
                                // Figure out what to do from the checksum
                                switch(checksum) {
                                    case MSG_POWER_FAILURE:
                                        handlePowerFailure();
                                        break;
                                    case MSG_EVENT_RECEIVED:
                                        handleEventReceived();
                                        break;
                                    default:
                                        // Checksum was flat-out wrong, try again!!
                                }
                            }

                            // See if we reached our max number of attempts
                            if(!done && (attempt >= maximumAttempts)) {
                                success = false; // redundant, but done for clarity
                                done = true;
                            } else {
                                // Otherwise, try again
                                attempt++;
                            }
                        }
                    } catch (IOException e) {
                        success = false; // redundant, but done for clarity
                    }
                    // Tell to start notifying the listener of new data again
                    serialPort.notifyOnDataAvailable(true);
                }
            }
            return success;
        }
    
    }
    
    /** Vector holding the currently registered X10Listeners */
    private Vector x10Listeners;
    /** Vector holding the currently registered X10TransmitterListeners */
    private Vector x10TransmitterListeners;
    /** LinkedList holding the current queue of events to be transmitted.
     * I used a linked list because Vector doesn't seem to guarentee that
     * calls to add() will add the object to the end of the list. With
     * LinkedList's addLast() and removeFirst()  I can guarentee its doing
     * what I want.
     */
    private LinkedList x10Events;
    private X10Talker x10talker;
    
    private int maximumAttempts;
    private HouseCode monitoredHouseCode;
    
    // Properties associated with the serial port
    private String portName;
    private int baudRate;
    private int dataBits;
    private int stopBits;
    private int parity;
    
    private SerialPort serialPort;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    /** The precision of the outbound brightness levels. This
     * has package level access for {@link com.jaxzin.x10.cm11a.CM11AConnection#getClosestBrightnessLevel(double)} to use.
     */
    static final int OUTPUT_BRIGHTNESS_LEVELS = 22;
    /** The precision of the inbound brightness levels. This
     * has package level access for possible use in other <code>com.jaxzin.x10.cm11a</code> classes.
     */
    static final int INPUT_BRIGHTNESS_LEVELS = 210;
    
    /** Received from CM11 when it is ready to receive messages */
    private static final byte MSG_READY                 = (byte) 0x55;
    /** Received from CM11 when it has lost power and needs to have macros and time uploaded */
    private static final byte MSG_POWER_FAILURE         = (byte) 0xA5;
    /** Received from CM11 when it has received data into its internal buffer that needs to be downloaded */
    private static final byte MSG_EVENT_RECEIVED        = (byte) 0x5A;
    /** Sent to CM11 when computer is about to upload the current time to the CM11 */
    private static final byte MSG_TIME_UPLOAD           = (byte) 0x9B;
    /** Sent to CM11 when computer is about to upload 42 byte macro data */
    private static final byte MSG_MACRO_UPLOAD          = (byte) 0xFB;
    /** Sent to CM11 to request the download 10 byte data buffer */
    private static final byte MSG_REQUEST_BUFFER        = (byte) 0xC3;	
    /** Sent to CM11 to request the current status of the the CM11 */
    private static final byte MSG_REQUEST_STATUS        = (byte) 0x8B;
    /** Sent to CM11 to acknowledge that the checksum of the current message is correct */
    private static final byte MSG_ACKNOWLEDGE           = (byte) 0x00;
    
    /** Creates a new instance of CM11A */
    public CM11A(String portName) {
        this(   portName,
                4800,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
    }
    
    /** Creates a new instance of CM11A */
    public CM11A(   String portName, 
                    int baudRate,
                    int dataBits,
                    int stopBits,
                    int parity) 
    {
        // Initialize the CM11A object
        this();
        this.portName           = portName;
        this.baudRate           = baudRate;
        this.dataBits           = dataBits;
        this.stopBits           = stopBits;
        this.parity             = parity;
    }
    
    /** Initializes a CM11A object */
    private CM11A() {
        super();
        this.x10Listeners               = new Vector();
        this.x10TransmitterListeners    = new Vector();
        this.x10Events                  = new LinkedList();
        this.x10talker                  = new X10Talker();
        this.maximumAttempts            = 20;
        this.monitoredHouseCode         = HouseCode.A;
    }
    
    /** Attempts to open a connection to the CM11A via the serial port.
     * It uses the values passed in through one of the contructors or
     * set using the methods related to the serial port parameters.
     * @see #setPortName
     * @see #setBaudRate
     * @see #setDataBits
     * @see #setStopBits
     * @see #setParity
     * @throws javax.comm.NoSuchPortException The portName is not valid.
     * @throws javax.comm.PortInUseException The named serial port is already in use.
     * @throws javax.comm.UnsupportedCommOperationException One of the serial port parameters is invalid.
     * @throws java.io.IOException There was a problem obtaining the serial port's input and output streams.
     */
    protected void open()  throws   NoSuchPortException,
                                    PortInUseException,
                                    UnsupportedCommOperationException,
                                    IOException
    {
        // Attempt to get the port by name, throws NoSuchPortException
        CommPortIdentifier portId = 
            CommPortIdentifier.getPortIdentifier(portName);
        // Attempt to open the port, wait 2 seconds, throws PortInUseException
        serialPort = (SerialPort) portId.open("CM11A", 2000);
        // Attempt to get the streams, throws IOException
        outputStream = new DataOutputStream(
                        new BufferedOutputStream(
                            serialPort.getOutputStream()));
        inputStream  = new DataInputStream(
                        new BufferedInputStream(
                        serialPort.getInputStream() ));
        // Attempt to set serial port parameters, 
        //  throws UnsupportedCommOperationException
        serialPort.setSerialPortParams( baudRate,
                                        dataBits,
                                        stopBits,
                                        parity);
        
        // Attempt to register a new listener
        serialPort.notifyOnDataAvailable(true);
        try {
            serialPort.addEventListener(new CM11ASerialListener());
        } catch(TooManyListenersException ignored){}
    }
    
    /** Closes the connection to the serial port */
    public void close() {
        try {
            if(outputStream != null) outputStream.close();
            if(inputStream != null) inputStream.close();
            if(serialPort != null) {
                serialPort.removeEventListener();
                serialPort.close();
            }
        } catch(Exception ignored) {}
    }
    
    /** Only used with JVM garbage collection. <BR>
     * <I>Nothing to see here...move along...move along...</I>
     */
    public void finalize() {
        close();
    }
    
    /** Registers a new X10Listener to be notified of new
     * {@link com.jaxzin.x10.event.X10Event}s.
     */
    public void addX10Listener(X10Listener listener) {
        this.x10Listeners.add(listener);
    }
    
    /** Deregisters an X10Listener
     * @param listener the listener to be removed
     */
    public void removeX10Listener(X10Listener listener) {
        this.x10Listeners.remove(listener);
    }
    
    private void notifyX10Listeners(X10Event event) {
        // Spawn new Thread to notify all listeners
        Thread t = new Thread(new X10ListenerNotifier(event));
        t.start();
    }

    /** Registers {@link com.jaxzin.x10.event.X10TransmitterListener}s that
     * receive {@link com.jaxzin.x10.event.X10TransmitterEvent}s which relate
     * to the internal {@link com.jaxzin.x10.event.X10Event} queue of this
     * X10Transmitter.
     */
    public void addX10TransmitterListener(X10TransmitterListener listener) {
        this.x10TransmitterListeners.add(listener);
    }
    
    /** Deregisters an {@link com.jaxzin.x10.event.X10TransmitterListener}
     * @param listener the listener to be removed
     */
    public void removeX10TransmitterListener(X10TransmitterListener listener) {
        this.x10TransmitterListeners.remove(listener);
    }
    
    private void notifyX10TransmitterListeners(X10TransmitterEvent event) {
        // Spawn new Thread to notify all listeners
        Thread t = new Thread(new X10TransmitterListenerNotifier(event));
        t.start();
    }
    
    /** This method gives the X10 transmitter a new
     * {@link com.jaxzin.x10.event.X10Event} to deliver.
     */
    public void transmit(X10Event event) {
        // This is a Vector so I shouldn't have to synchronize this explicitly, 
        // but it doesn't hurt to be clear
        synchronized(x10Events) { 
            this.x10Events.addLast(event);
        }
        notifyX10TransmitterListeners(new X10TransmitterEvent(this,X10TransmitterEvent.QUEUE_UPDATED,event));
        // Start the talker that actually writes the events to the stream
        // Since X10Talker extends Daemon, start() will do nothing if the
        // talker is already started
        x10talker.start();
    }
    
    /** Sets the CM11A's internal clock to the given date and time.
     */    
    public void setInternalClock(Date date) throws IOException {
        setInternalClock(date,false,false,false);
    }
    
    /** Sets the CM11A's internal clock to the given date and time.
     */    
    private void setInternalClock(Date date, boolean clearBatteryTimer, boolean clearStatusData, boolean purgeTimer) throws IOException {
        // When this is called from handlePowerFailure(), it will
        // be run in the same Thread that previously locked the streams so
        // relocking the streams should not deadlock
        
        // Convert the date into the bytes formatted for the CM11A
        byte[] bytes = getBytes(date, monitoredHouseCode, clearBatteryTimer, clearStatusData, purgeTimer);
        
        // Lock the output stream
        synchronized(outputStream) {
            // Lock the input stream
            synchronized(inputStream) {
                // Stop the listener from being notified of new data(not fail-safe but wastes less cycles at least)
                serialPort.notifyOnDataAvailable(false);
                // Keep looping until done
                boolean done = false;
                int attempt  = 0;
                while(!done) {
                    // Start by clearing the input stream
                    clearInputStream();
                    // Write the new clock setting bytes
                    outputStream.write(bytes);
                    outputStream.flush();

                    // Read what should be the checksum
                    byte checksum = inputStream.readByte();
                    // Figure out if the checksum is correct
                    boolean checksum_correct = (checksum == getChecksum(bytes));
                    // If the checksum is correct...
                    if(checksum_correct) {
                        // Tell the CM11A the checksum was correct
                        outputStream.write(MSG_ACKNOWLEDGE);
                        outputStream.flush();
                        // Read the CM11A's reply
                        byte reply = inputStream.readByte();
                        // Figure out what to do from the reply
                        switch(reply) {
                            case MSG_READY:
                                // Time was successfully set!!
                                LOG.info("Successfully set internal clock to:"+date);
                                done = true;
                                break;
                            case MSG_POWER_FAILURE:
                                // try again!
                                break;
                            case MSG_EVENT_RECEIVED:
                                handleEventReceived();
                                break;
                            default:
                                // Something's really wrong, because the reply
                                // was something the CM11A couldn't possibly send, but try anyway
                        }
                    } else {
                        // Otherwise the checksum was incorrect...
                        // Figure out what to do from the reply
                        switch(checksum) {
                            case MSG_EVENT_RECEIVED:
                                handleEventReceived();
                                break;
                            case MSG_POWER_FAILURE:
                            default:
                                // Checksum was flat-out wrong, try again!!
                        }
                    }

                    // See if we reached our max number of attempts
                    if(!done && (attempt >= this.maximumAttempts)) {
                        done = true;
                    } else {
                        // Otherwise, try again
                        attempt++;
                    }
                }
                // Tell to start notifying the listener of new data again
                serialPort.notifyOnDataAvailable(true);
            }
        }
    }
    
    public Date getInternalClock() throws IOException {
        // For now return the current time
        return new Date();
    }
    
    public void setMonitoredHouseCode(HouseCode houseCode) throws IOException {
        this.monitoredHouseCode = houseCode;
        resetInternalMonitoringInfo();
    }
    
    public HouseCode getMonitoredHouseCode() {
        return this.monitoredHouseCode;
    }
    
    public void resetInternalBatteryTimer() throws IOException {
        setInternalClock(getInternalClock(),true,false,false);
    }
    
    public void resetInternalMonitoringInfo() throws IOException {
        setInternalClock(getInternalClock(),false,true,false);
    }
    
    public void resetInternalMacroTimers() throws IOException {
        setInternalClock(getInternalClock(),false,false,true);
    }
    
    public void setMaximumAttempts(int maximumAttempts) {
        this.maximumAttempts = maximumAttempts;
    }
    
    public int getMaximumAttempts() {
        return this.maximumAttempts;
    }
    
    private static byte[] getBytes(X10Event event) {
        // build the header byte
        byte header = 0;
        
        // bits 7 through 3 are the brightness levels
        header = (byte)(((int)(event.getBrightnessLevel()*OUTPUT_BRIGHTNESS_LEVELS)) << 3);
        
        // bit 2 is always 1
        header |= (byte)(1 << 2);
        
        // bit 1 is the address/command bit, 0 for address, 1 for function
        header |= (byte)((event.getType()==X10Event.ADDRESS?0:1) << 1);
        
        // bit 0 is the extended/standard transmission bit, 0 for standard, 1 for extended
        header |= (byte)(event.getOtherCode() == FunctionCode.EXTENDED_CODE?1:0);
        
        // Build the code byte
        Nibble houseBits = CodeMap.getNibble(event.getHouseCode());
        Nibble otherBits = CodeMap.getNibble(event.getOtherCode());
        byte code = houseBits.combineAsHigh(otherBits);
        
        // Build the byte array
        byte[] bytes;
        if(event.getOtherCode() == FunctionCode.EXTENDED_CODE) {
            bytes      = new byte[4];
            bytes[0]   = header;
            bytes[1]   = code;
            bytes[2]   = event.getData();
            bytes[3]   = event.getCommand();
        } else {
            bytes      = new byte[2];
            bytes[0]   = header;
            bytes[1]   = code;
        }
        
        return bytes;
    }
    
    private static byte[] getBytes(Date date, HouseCode houseCode, boolean clearBatteryTimer, boolean clearStatusData, boolean purgeTimer) {
        byte[] bytes = new byte[7];
        
        // Break the given date-time into its components
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(date);
        int second 	= cal.get(Calendar.SECOND);
        int minute	= cal.get(Calendar.MINUTE);
        int hour_of_day = cal.get(Calendar.HOUR_OF_DAY);
        int day_of_year = cal.get(Calendar.DAY_OF_YEAR);
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
        
        bytes[0]        = MSG_TIME_UPLOAD;                      // The header
        bytes[1]        = (byte) second;                        // second
        bytes[2]        = (byte) (60*(hour_of_day%2)+minute);   // minute(of 2hr cycle)
        bytes[3]        = (byte) (hour_of_day >> 2);            // hours (div by 2)
        bytes[4]        = (byte) (day_of_year);                 // day of year(missing MSB)
        bytes[5]        = (byte) ((day_of_year>255?128:0) +     // LSB of day of year plus
                                  (1 << (day_of_week-1)) );// weekday mask

        Nibble houseBits = CodeMap.getNibble(houseCode);
        Nibble flagBits  = new Nibble(
                                 //(0 << 3) +                   // Reserved bit
                                 (clearBatteryTimer?1:0 << 2) + // Battery timer clear flag
                                 (clearStatusData?1:0 << 1) +   // Monitored status clear flag
                                 (purgeTimer?1:0 << 0)          // Timer purge flag
                            );
        bytes[6]        = houseBits.combineAsHigh(flagBits);
        
        return bytes;
    }
    
    private static byte getChecksum(byte[] bytes) {
        long temp = 0;
        // Sum up all the byte values
        for(int i = 0; i < bytes.length; i++) {
            temp += bytes[i];
        }
        // Mask the value to only 1 byte length
        temp &= 0xFF;
        return (byte)temp;
    }
    
    /** Reads the CM11A's buffer, parses the events contained into X10Events,
     * and notifies the registered X10Listeners of those events.  
     * Assumes the serial port's input and output stream have been locked 
     * by the calling method.
     */
    private void handleEventReceived() throws IOException {
        // Start by clearing the input stream in case there are extra
        // MSG_EVENT_RECEIVED bytes
        clearInputStream();
        
        // Tell the CM11A to upload the buffer
        outputStream.writeByte(MSG_REQUEST_BUFFER);
        outputStream.flush();
        // The first byte is the number of bytes to follow
        int size = inputStream.read();
        // Size should always be > 1(because of address/function mask)
        int mask;
        if(size >= 2) {
            // Read the mask as an unsigned byte
            mask = inputStream.readUnsignedByte();
            size--;
        } else {
            // Something is wrong because we need at least two bytes in buffer
            clearInputStream();
            return;
        }
        // Read the data bytes
        byte[] data = new byte[size];
        inputStream.readFully(data);
        // For each data byte in the buffer(second byte is mask)
        for(int i = 0; i < data.length; i++) {
            byte code = data[i];
            // Figure out the nibbles
            Nibble houseNibble = new Nibble(code>>>4); // shift the high bits to low
            Nibble otherNibble = new Nibble(code&0xF); // mask out the high bits
            // Get the house and other code
            HouseCode houseCode = CodeMap.getHouseCode(houseNibble);
            OtherCode otherCode;
            // Get the other code based on the corresponding bit in the mask byte(1 for function, 0 for address)
            if(bitValue(mask,i))
                otherCode = CodeMap.getFunctionCode(otherNibble);
            else
                otherCode = CodeMap.getUnitCode(otherNibble);
            
            if(otherCode instanceof UnitCode) {
                notifyX10Listeners(new X10Event(this,houseCode,(UnitCode)otherCode));
            } else if(otherCode instanceof FunctionCode.Basic) {
                notifyX10Listeners(new X10Event(this,houseCode,(FunctionCode.Basic)otherCode));
            } else if(otherCode instanceof FunctionCode.Brightness) {
                // Get the brightness level(convert it to unsigned value)
                int rawBright = (int)data[++i] & 0xFF;
                double brightnessLevel = (double)rawBright/(double)INPUT_BRIGHTNESS_LEVELS;
                // Send the brightness event
                notifyX10Listeners(new X10Event(this,houseCode,(FunctionCode.Brightness)otherCode,brightnessLevel));                
            } else if(otherCode instanceof FunctionCode.Extended) {
                // Get the extended 'data' byte
                byte exData = data[++i];
                // Get the extended 'command' byte
                byte exCmd  = data[++i];
                // Send the extended event
                notifyX10Listeners(new X10Event(this,houseCode,(FunctionCode.Extended)otherCode,exData,exCmd));                
            }
        }
        
    }
    
    private boolean bitValue(int b, int bit) {
        b &= (1 << bit);
        b >>= bit;
        return b == 1;
    }
    
    /** Recovers the CM11A from a power failure.   
     * Assumes the serial port's input and output stream have been locked 
     * by the calling method.
     */
    private void handlePowerFailure() throws IOException {
        // Start by clearing the input stream in case there are extra
        // MSG_POWER_FAILURE bytes
        clearInputStream();

        // Set the CM11A's internal clock to the current time
        setInternalClock(new Date());
    }
    
    /** Reads all the available data in the serial port's input stream and
     * deletes it.  Assumes the serial port's input stream has been locked 
     * by the calling method.
     */
    private void clearInputStream() throws IOException {
        // Read all the available data
        while(inputStream.available() > 0) {
            inputStream.read(new byte[inputStream.available()]);
        }
    }
    
    public static void main(String[] args) {
        
        X10Connection conn = null;
        try {
            // Open connection
            Class.forName("com.jaxzin.x10.cm11a.CM11ADriver");           
            conn = X10DriverManager.getX10Connection("x10:cm11a:COM1");
            X10Transceiver x10 = conn.getX10Transceiver();
            
            // Add listener to print queue messages
            x10.addX10TransmitterListener(
                new X10TransmitterListener() {
                    public void queueUpdated(X10TransmitterEvent e) {
                        System.out.println("queue updated");
                    }
                    public void queueEmptied(X10TransmitterEvent e) {
                        System.out.println("queue emptied");
                    }
                    public void eventUndelivered(X10TransmitterEvent e) {
                        System.out.println("event undelivered");
                    }
                    public void eventDelivered(X10TransmitterEvent e) {
                        System.out.println("event delivered");
                    }
                }
            );
            
            // Add listener to print X10 Events
            x10.addX10Listener(
                new X10Listener() {
                    public void address(X10Event e){System.out.println("Received:"+e);}
                    public void allUnitsOff(X10Event e){System.out.println("Received:"+e);}
                    public void allLightsOff(X10Event e){System.out.println("Received:"+e);}
                    public void on(X10Event e){System.out.println("Received:"+e);}
                    public void off(X10Event e){System.out.println("Received:"+e);}
                    public void dim(X10Event e){System.out.println("Received:"+e);}
                    public void brighten(X10Event e){System.out.println("Received:"+e);}
                    public void allLightsOn(X10Event e){System.out.println("Received:"+e);}
                    public void extendedCode(X10Event e){System.out.println("Received:"+e);}
                    public void hailRequest(X10Event e){System.out.println("Received:"+e);}
                    public void hailAcknowledge(X10Event e){System.out.println("Received:"+e);}
                    public void presetDim1(X10Event e){System.out.println("Received:"+e);}
                    public void presetDim2(X10Event e){System.out.println("Received:"+e);}
                    public void extendedDataTransfer(X10Event e){System.out.println("Received:"+e);}
                    public void statusOn(X10Event e){System.out.println("Received:"+e);}
                    public void statusOff(X10Event e){System.out.println("Received:"+e);}
                    public void statusRequest(X10Event e){System.out.println("Received:"+e);}
                }
            );
            // Send some test events
            x10.transmit(new X10Event(x10,HouseCode.A,UnitCode.UNIT_1));
            x10.transmit(new X10Event(x10,HouseCode.A,FunctionCode.ON));
            x10.transmit(new X10Event(x10,HouseCode.A,FunctionCode.DIM,0.25));
            
        } catch(Exception e) {
            LOG.logp(Level.SEVERE,"CM11A","main","Exception caught",e);
            e.printStackTrace();
        } finally {
            //if(cm11a != null) cm11a.close();
        }
    }
}
