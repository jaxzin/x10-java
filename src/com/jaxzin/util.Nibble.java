/*
 * Nibble.java
 *
 * Created on July 9, 2002, 11:47 PM
 */

package com.jaxzin.util;

/** This class represent 4 bits. This is also known as half a byte, or the 
 * memory structure called a <code>nibble</code>.  Java has no primative that 
 * represents a <code>nibble</code>, but this class is very similar to 
 * {@link java.lang.Byte} which is the wrapper object for the Java primitive 
 * of <code>byte</code>. It should also be noted that counter to the Java 
 * standard of primatives that are signed (values are centered around 0 and 
 * can be negative), this Nibble is unsigned (values start at 0 and can not be 
 * negative).  This means that it ranges from decimal values of 0 to 
 * 7(2<sup>3</sup>-1) instead of -4(-2<sup>2</sup>) to  3(2<sup>2</sup>-1).  
 * There is a method to get the primitive value of the Nibble object as a 
 * byte(since there is no Java primitive <code>nibble</code>) and
 * since Nibble extends {@link java.lang.Number}, like {@link java.lang.Byte},
 * it has methods to get is value as the other primitives.  There are two
 * methods that will combine two Nibbles to form a byte, one that uses this
 * Nibble as the first 4 bits of the byte and vice versa.
 *
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public final class Nibble extends java.lang.Number implements java.lang.Comparable {
    
    /** A constant holding the maximum value a <code>nibble</code> can have, 2<sup>4</sup>-1 */
    public static final byte MAX_VALUE      = 0xF;
    /** A constant holding the minimum value a <code>nibble</code> can have, 0 */
    public static final byte MIN_VALUE      = 0x0;
    
    private byte value;
    
    /** Creates a new instance of Nibble 
     * @throws java.lang.IllegalArgumentException if the given value is outside the range of {@link #MIN_VALUE} to {@link #MAX_VALUE}, inclusive;
     */
    public Nibble(long value) {
        if((byte)value < MIN_VALUE || (byte)value > MAX_VALUE)
            throw new IllegalArgumentException("Nibble value of "+value+" is outside the valid range of Nibble.MIN_VALUE("+MIN_VALUE+") to Nibble.MAX_VALUE("+MAX_VALUE+"), inclusive");
        this.value = (byte)value;
    }
    
    /** Creates a new instance of Nibble 
     * @throws java.lang.IllegalArgumentException if the given value is outside the range of {@link #MIN_VALUE} to {@link #MAX_VALUE}, inclusive;
     */
    public Nibble(String s) throws NumberFormatException {
        this(Byte.parseByte(s));
    }
    
    /** Compares two Nibbles and returns a value of 1 if this Nibble is higher
     * in value than the given Nibble, 1 if they are equal and -1 if this
     * Nibble is lower.
     * @param obj the Nibble to compare this Nibble to.
     */
    public int compareTo(Object obj) {
        Nibble that = (Nibble) obj;
        if(this.value < that.value) {
            return -1;
        } else if(this.value > that.value) {
            return 1;
        } else {
            return 0;
        }
    }
    
    /** Returns true if this Nibble equals the given Nibble and false otherwise.
     * @param obj The Nibble to compare this Nibble to.
     */
    public boolean equals(Object obj) {
        if(compareTo(obj) == 0)
            return true;
        else
            return false;
    }
    
    /** Returns a value that causes Nibbles with the same value to be hashed
     * to the same spot in a {@link java.util.Map}
     */
    public int hashCode() {
        return value;
    }
    
    /** Returns the value of this Nibble as a double. */
    public double doubleValue() {
        return new Byte(this.value).doubleValue();
    }
    
    /** Returns the value of this Nibble as a float. */
    public float floatValue() {
        return new Byte(this.value).floatValue();
    }
    
    /** Returns the value of this Nibble as an int. */
    public int intValue() {
        return new Byte(this.value).intValue();
    }
    
    /** Returns the value of this Nibble as a long. */
    public long longValue() {
        return new Byte(this.value).longValue();
    }
    
    /** Returns the value of this Nibble as the first 4 bits of a byte. */
    public byte byteValueAsHigh() {
        return (byte)(this.value << 4); 
        // Equivalent but less efficient:
        // return combineAsHigh(new Nibble(0));
    }
    
    /** Returns the value of combining this Nibble as the first 4 bits
     * of a byte and the given Nibble as the last 4 bits. 
     */
    public byte combineAsHigh(Nibble low) {
        return (byte)((this.value << 4) | low.value);
    }
    
    /** Returns the value of combining this Nibble as the last 4 bits
     * of a byte and the given Nibble as the first 4 bits. 
     */
    public byte combineAsLow(Nibble high) {
        return high.combineAsHigh(this);
    }
    
}
