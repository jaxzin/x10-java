/*
 * HouseCode.java
 *
 * Created on July 9, 2002, 11:02 PM
 */

package javax.x10.codes;

/** This contains constants that represent X10 house codes.  
 *
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public final class HouseCode extends Code {

    /** This is private so no one can override these values */
    private HouseCode(String name) {
        super(name);
    }
    
    /**Represents the X10 house code - "A"*/ 
    public static final HouseCode A = new HouseCode("A"); 
    /**Represents the X10 house code - "B"*/ 
    public static final HouseCode B = new HouseCode("B");
    /**Represents the X10 house code - "C"*/ 
    public static final HouseCode C = new HouseCode("C");
    /**Represents the X10 house code - "D"*/ 
    public static final HouseCode D = new HouseCode("D");
    /**Represents the X10 house code - "E"*/ 
    public static final HouseCode E = new HouseCode("E");
    /**Represents the X10 house code - "F"*/ 
    public static final HouseCode F = new HouseCode("F");
    /**Represents the X10 house code - "G"*/ 
    public static final HouseCode G = new HouseCode("G");
    /**Represents the X10 house code - "H"*/ 
    public static final HouseCode H = new HouseCode("H");
    /**Represents the X10 house code - "I"*/ 
    public static final HouseCode I = new HouseCode("I");
    /**Represents the X10 house code - "J"*/ 
    public static final HouseCode J = new HouseCode("J");
    /**Represents the X10 house code - "K"*/ 
    public static final HouseCode K = new HouseCode("K");
    /**Represents the X10 house code - "L"*/ 
    public static final HouseCode L = new HouseCode("L");
    /**Represents the X10 house code - "M"*/ 
    public static final HouseCode M = new HouseCode("M");
    /**Represents the X10 house code - "N"*/ 
    public static final HouseCode N = new HouseCode("N");
    /**Represents the X10 house code - "O"*/ 
    public static final HouseCode O = new HouseCode("O");
    /**Represents the X10 house code - "P"*/ 
    public static final HouseCode P = new HouseCode("P");
    
    private static java.util.List allCodes;
    static {
        java.util.List temp = new java.util.Vector();
        temp.add(A);
        temp.add(B);
        temp.add(C);
        temp.add(D);
        temp.add(E);
        temp.add(F);
        temp.add(G);
        temp.add(H);
        temp.add(I);
        temp.add(J);
        temp.add(K);
        temp.add(L);
        temp.add(M);
        temp.add(N);
        temp.add(O);
        temp.add(P);
        allCodes = java.util.Collections.unmodifiableList(temp);
    }
    
    public static final java.util.Iterator iterator() {
        return allCodes.iterator();
    }
}
