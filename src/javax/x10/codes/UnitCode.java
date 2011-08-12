/*
 * UnitCode.java
 *
 * Created on July 9, 2002, 11:03 PM
 */

package javax.x10.codes;

/** This contains constants that represent X10 unit codes.
 *
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public final class UnitCode extends OtherCode {

    /** This is private so no one can override these values */
    private UnitCode(String name) {
        super(name);
    }
    
    /**Represents the X10 unit code - "1"*/ 
    public static final UnitCode UNIT_1 = new UnitCode("1");
    /**Represents the X10 unit code - "2"*/ 
    public static final UnitCode UNIT_2 = new UnitCode("2");
    /**Represents the X10 unit code - "3"*/ 
    public static final UnitCode UNIT_3 = new UnitCode("3");
    /**Represents the X10 unit code - "4"*/ 
    public static final UnitCode UNIT_4 = new UnitCode("4");
    /**Represents the X10 unit code - "5"*/ 
    public static final UnitCode UNIT_5 = new UnitCode("5");
    /**Represents the X10 unit code - "6"*/ 
    public static final UnitCode UNIT_6 = new UnitCode("6");
    /**Represents the X10 unit code - "7"*/ 
    public static final UnitCode UNIT_7 = new UnitCode("7");
    /**Represents the X10 unit code - "8"*/ 
    public static final UnitCode UNIT_8 = new UnitCode("8");
    /**Represents the X10 unit code - "9"*/ 
    public static final UnitCode UNIT_9 = new UnitCode("9");
    /**Represents the X10 unit code - "10"*/ 
    public static final UnitCode UNIT_10 = new UnitCode("10");
    /**Represents the X10 unit code - "11"*/ 
    public static final UnitCode UNIT_11 = new UnitCode("11");
    /**Represents the X10 unit code - "12"*/ 
    public static final UnitCode UNIT_12 = new UnitCode("12");
    /**Represents the X10 unit code - "13"*/ 
    public static final UnitCode UNIT_13 = new UnitCode("13");
    /**Represents the X10 unit code - "14"*/ 
    public static final UnitCode UNIT_14 = new UnitCode("14");
    /**Represents the X10 unit code - "15"*/ 
    public static final UnitCode UNIT_15 = new UnitCode("15");
    /**Represents the X10 unit code - "16"*/ 
    public static final UnitCode UNIT_16 = new UnitCode("16");

    private static java.util.List allCodes;
    static {
        java.util.List temp = new java.util.Vector();
        temp.add(UNIT_1);
        temp.add(UNIT_2);
        temp.add(UNIT_3);
        temp.add(UNIT_4);
        temp.add(UNIT_5);
        temp.add(UNIT_6);
        temp.add(UNIT_7);
        temp.add(UNIT_8);
        temp.add(UNIT_9);
        temp.add(UNIT_10);
        temp.add(UNIT_11);
        temp.add(UNIT_12);
        temp.add(UNIT_13);
        temp.add(UNIT_14);
        temp.add(UNIT_15);
        temp.add(UNIT_16);
        allCodes = java.util.Collections.unmodifiableList(temp);
    }
    
    public static final java.util.Iterator iterator() {
        return allCodes.iterator();
    }
}
