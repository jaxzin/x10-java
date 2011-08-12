/*
 * X10Event.java
 *
 * Created on July 9, 2002, 11:01 PM
 */

package javax.x10.event;

import javax.x10.codes.*;

/**  <p>This class is the direct wrapper around a message in the X10 protocol.
 * The constructors of X10Event are fool-proof so you can't create a malformed
 * X10Event without getting compile errors or, in one case, a runtime 
 * exception of an {@link java.lang.IllegalArgumentException} if your value for 
 * <code>brightnessLevel</code> is out of a valid range.
 * <P>
 * Addressing events are created with 
 *{@link #X10Event(Object,HouseCode,UnitCode)}. Here's an example:
 * <pre>
 * X10Event eventA1 = new X10Event(this, HouseCode.A, UnitCode.UNIT_1);
 *</pre>
 * This creates an event that addresses the X10 unit A1.  As you can see, you use
 * one of the constants defined in {@link javax.x10.codes.HouseCode} to
 * specify the house code of the unit, and constants in 
 * {@link javax.x10.codes.UnitCode} to specify the unit code of the unit.
 * Java events are attached to a source Object so you can specify anything you
 * want here from <code>this</code> to a new object or even <code>null</code>.
 * Read more about Java Events if the {@link #source} field confuses you.</P>
 * <P>
 * Basic X10 function events that contain no extra data, like brightness info
 * or extra bytes, are created with 
 * {@link #X10Event(Object,HouseCode,FunctionCode.Basic)}. Here's an example:
 * <pre>
 * X10Event eventAallOff = new X10Event(this, HouseCode.A, FunctionCode.ALL_UNITS_OFF);
 *</pre>
 * This event will turn off every unit with a house code of A.  Again the house
 * code is a constant from {@link javax.x10.codes.HouseCode} and the
 * function code is a constant from {@link javax.x10.codes.FunctionCode}.
 * Function codes are different from unit codes though in that some function
 * codes need extra information, so for this constructor, only constants in
 * {@link javax.x10.codes.FunctionCode} that are an instance of
 * {@link javax.x10.codes.FunctionCode.Basic} are accepted.</P>
 * <P>
 * Function events that contain brightness data are created with
 * {@link #X10Event(Object,HouseCode,FunctionCode.Brightness,double)}.  
 * Here's an example:
 * <pre>
 * X10Event eventADim = new X10Event(this, HouseCode.A, FunctionCode.DIM, 0.16);
 *</pre>
 * This event will dim all of the addressed units on house code A by 16 percent.
 * Once again the house code is a constant from 
 * {@link javax.x10.codes.HouseCode} and the function is a constant from
 * {@link javax.x10.codes.FunctionCode}. This time the function code must
 * be one of the constants that is an instance of 
 * {@link javax.x10.codes.FunctionCode.Brightness}.  The last parameter
 * is a number, between {@link #MIN_BRIGHTNESS_LEVEL} and 
 * {@link #MAX_BRIGHTNESS_LEVEL}, that represents the relative level of 
 * brightness to which to change.  
 *</P>
 * <P>
 * Function events that contain extended data are created with
 * {@link #X10Event(Object,HouseCode,FunctionCode.Extended,byte,byte)}.
 * Here's an example:
 * <pre>
 * X10Event eventAextra = new X10Event(this, HouseCode.A, FunctionCode.EXTENDED_CODE, (byte)0x00, (byte)0x00);
 *</pre>
 * This creates an extended event for address units on house code A with a
 * data byte of 0 and a command byte of 0.  Honestly, at the time of writing
 * this documentation, I don't have any examples of extended codes so
 * you'll need to figure out what values for the data and command byte you
 * need. Once again the house code is a constant from 
 * {@link javax.x10.codes.HouseCode} and the function is a constant from
 * {@link javax.x10.codes.FunctionCode}. This time the function code must
 * be one of the constants that is an instance of 
 * {@link javax.x10.codes.FunctionCode.Extended}.
 *</P>
 *
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public class X10Event extends java.util.EventObject {
    
    /** <code>X10Event.Type</code> is only used as an enumeration
     * pattern for the type of {@link javax.x10.event.X10Event}.
     * The only instances of this class that you can use are defined as
     * constants in {@link javax.x10.event.X10Event}.
     * @see javax.x10.event.X10Event#ADDRESS
     * @see javax.x10.event.X10Event#FUNCTION
     */
    public static final class Type {
        private String name;
        private Type(String name) {
            this.name = name;
        }
        public String toString() {
            return name;
        }
    }
    
    /** <code>X10Events</code> of this type contain addressing 
     * information. <code>X10Events</code> of this type will 
     * return one of the constants defined in 
     * {@link javax.x10.codes.UnitCode} from the method
     * {@link #getOtherCode}
     */
    public static final Type ADDRESS        = new Type("ADDRESS");
    /** <code>X10Events</code> of this type contain command 
     * information. <code>X10Events</code> of this type will 
     * return one of the constants defined in 
     * {@link javax.x10.codes.FunctionCode} from the method 
     * {@link #getOtherCode}
     */
    public static final Type FUNCTION       = new Type("FUNCTION");
    
    private HouseCode houseCode;
    private OtherCode otherCode;
    private Type type;
    private double brightnessLevel;
    private byte data;
    private byte command;
    /*private byte[] bytes;
    private byte checksum;*/
    
    /** A constant holding the maximum value the property <code>brightnessLevel</code> can have, 1.0 aka 100% */
    public static final double MAX_BRIGHTNESS_LEVEL       = 1.0;
    /** A constant holding the minimum value the property <code>brightnessLevel</code> can have, 0.0 aka 0% */
    public static final double MIN_BRIGHTNESS_LEVEL       = 0.0;
    
    /** Creates a new instance of X10Event that is of the type 
     * {@link #ADDRESS}.
     * @param source The source of this event.
     * @param houseCode The house code for this event.
     * @param unitCode The unit code for this event.
     */
    public X10Event(Object source, HouseCode houseCode, UnitCode unitCode) {
        this(source, houseCode, unitCode, MIN_BRIGHTNESS_LEVEL, (byte) 0, (byte) 0);
    }
    
    /** Creates a new instance of X10Event that is of the type 
     * {@link #FUNCTION} and needs no
     * extra information.
     * @param source The source of this event.
     * @param houseCode The house code for this event.
     * @param functionCode The function code for this event.
     */
    public X10Event(Object source, HouseCode houseCode, FunctionCode.Basic functionCode) {
        this(source, houseCode, functionCode, MIN_BRIGHTNESS_LEVEL, (byte) 0, (byte) 0);
    }
    
    /** Creates a new instance of X10Event that is of the type 
     * {@link #FUNCTION} and has brightness
     * information.
     * @param source The source of this event.
     * @param houseCode The house code for this event.
     * @param functionCode The function code for this event.
     * @param percent The level to either brighten or dim.
     */
    public X10Event(Object source, HouseCode houseCode, FunctionCode.Brightness functionCode, double percent) {
        this(source, houseCode, functionCode, percent, (byte) 0, (byte) 0);
    }
    
    /** Creates a new instance of X10Event that is of the type 
     * {@link #FUNCTION} that contains extended data.
     * @param source The source of this event.
     * @param houseCode The house code for this event.
     * @param data The data byte for this extended code event.
     * @param command The command byte for this extended code event.
     */
    public X10Event(Object source, HouseCode houseCode, FunctionCode.Extended functionCode, byte data, byte command) {
        this(source, houseCode, functionCode, MIN_BRIGHTNESS_LEVEL, data, command);
    }
    
    private X10Event(Object source, HouseCode houseCode, OtherCode otherCode, double brightnessLevel, byte data, byte command) {
        // Set the values of the properties
        super(source);
        this.houseCode = houseCode;
        this.otherCode = otherCode;
        this.brightnessLevel = brightnessLevel;
        this.data = data;
        this.command = command;

        // Check the brightnessLevels input
        if(brightnessLevel < MIN_BRIGHTNESS_LEVEL || brightnessLevel > MAX_BRIGHTNESS_LEVEL)
            throw new IllegalArgumentException("The specified brightness level("+brightnessLevel+") is out of the valid range of X10Event.MIN_BRIGHTNESS_LEVEL("+MIN_BRIGHTNESS_LEVEL+") to X10Event.MAX_BRIGHTNESS_LEVEL("+MAX_BRIGHTNESS_LEVEL+"), inclusive.");
        
        // Figure out the type
        if(otherCode instanceof UnitCode) {
            this.type = ADDRESS;
        } else { // if otherCode instanceof FunctionCode
            this.type = FUNCTION;
        }
        
        // Calculate the X10 protocol bytes
        //buildBytes();
    }
    /*
    private void buildBytes() {
        // build the header byte
        byte header = 0;
        
        // bits 7 through 3 are the brightness levels
        header = (byte)(this.brightnessLevel << 3);
        
        // bit 2 is always 1
        header |= (byte)(1 << 2);
        
        // bit 1 is the address/command bit, 0 for address, 1 for function
        header |= (byte)(this.type.getValue() << 1);
        
        // bit 0 is the extended/standard transmission bit, 0 for standard, 1 for extended
        header |= (byte)(this.otherCode == FunctionCode.EXTENDED_CODE?1:0);
        
        // Build the code byte
        byte code;
        code = this.houseCode.getNibble().combineAsHigh(this.otherCode.getNibble());
        
        // Build the byte array
        if(this.otherCode == FunctionCode.EXTENDED_CODE) {
            this.bytes      = new byte[4];
            this.bytes[0]   = header;
            this.bytes[1]   = code;
            this.bytes[2]   = this.data;
            this.bytes[3]   = this.command;
        } else {
            this.bytes      = new byte[2];
            this.bytes[0]   = header;
            this.bytes[1]   = code;
        }
        
        buildChecksum();
    }
    
    private void buildChecksum() {
        long temp = 0;
        // Sum up all the byte values
        for(int i = 0; i < this.bytes.length; i++) {
            temp += this.bytes[i];
        }
        // Mask the value to only 1 byte length
        temp &= 0xFF;
        this.checksum = (byte)temp;
    }
    */
    /** Returns the house code for this event which is one of the constants
     * defined in {@link javax.x10.codes.HouseCode}.
     */
    public HouseCode getHouseCode() {
        return this.houseCode;
    }
    
    /** Returns the other code for this event, which is one of the constants
     * defined in {@link javax.x10.codes.UnitCode} or 
     * {@link javax.x10.codes.FunctionCode}.
     */
    public OtherCode getOtherCode() {
        return this.otherCode;
    }
    
    /** Returns the type of this event, which is one of the constants
     * defined in <code>X10Event</code>.
     */
    public Type getType() {
        return this.type;
    }
    
    /** Returns the brightness level, in percent, for this event. This property 
     * is only used for X10Events that have a functionCode that is an instance 
     * of {@link javax.x10.codes.FunctionCode.Brightness}.
     */
    public double getBrightnessLevel() {
        return this.brightnessLevel;
    }
    
    /** Returns the data byte for this event. This property is only 
     * used for X10Events that have a functionCode that is an instance of 
     * {@link javax.x10.codes.FunctionCode.Extended}.
     */
    public byte getData() {
        return this.data;
    }
    
    /** Returns the command byte for this event. This property is only 
     * used for X10Events that have a functionCode that is an instance of 
     * {@link javax.x10.codes.FunctionCode.Extended}.
     */
    public byte getCommand() {
        return this.command;
    }
    
    
    /** Returns the bytes that make up this event in the X10 protocol. */
    /*public byte[] getBytes() {
        return (byte[])this.bytes.clone();
    }*/
    
    /** Returns the checksum of the <code>bytes</code> property. */
    /*public byte getChecksum() {
        return this.checksum;
    }*/
    
    public String toString() {
        return "X10Event["+houseCode+" "+otherCode+","+brightnessLevel+","+data+","+command+"]";
    }
}
