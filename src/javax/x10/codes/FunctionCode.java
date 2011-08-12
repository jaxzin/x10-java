/*
 * FunctionCode.java
 *
 * Created on July 9, 2002, 11:14 PM
 */

package javax.x10.codes;

/** This contains constants that represent X10 function codes.  
 *
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public class FunctionCode extends OtherCode {
    
    /** Creates a new instance of FunctionCode */
    private FunctionCode(String name) {
        super(name);
    }
    
    /** Subclass that represents X10 functions that need no extra information */
    public static final class Basic extends FunctionCode {
        private Basic(String name) {
            super(name);
        }
    }
    
    /** Subclass that represents X10 functions that need brightness information */
    public static final class Brightness extends FunctionCode {
        private Brightness(String name) {
            super(name);
        }
    }

    /** Subclass that represents X10 functions that need extended code information */
    public static final class Extended extends FunctionCode {
        private Extended(String name) {
            super(name);
        }
    }

    /**Turn off all X10 units */ 	
    public static final Basic ALL_UNITS_OFF             = new Basic("ALL_UNITS_OFF");
    /**Turn on all X10 lights */ 	
    public static final Basic ALL_LIGHTS_ON             = new Basic("ALL_LIGHTS_ON");
    /**Turn on all currently addressed X10 units*/		
    public static final Basic ON                        = new Basic("ON");
    /**Turn off all currently addressed X10 units*/	
    public static final Basic OFF                       = new Basic("OFF");
    /**Dim all currently addressed X10 units by the specified amount*/		
    public static final Brightness DIM                  = new Brightness("DIM");
    /**Brighten all currently addressed X10 units by the specified amount*/
    public static final Brightness BRIGHTEN             = new Brightness("BRIGHTEN");
    /**Turn off all X10 lights */
    public static final Basic ALL_LIGHTS_OFF            = new Basic("ALL_LIGHTS_OFF");
    /**Extra information is included*/			
    public static final Extended EXTENDED_CODE          = new Extended("EXTENDED_CODE");
    /**Hail all addressed units*/							
    public static final Basic HAIL_REQUEST              = new Basic("HAIL_REQUEST");
    /**Acknowledgement message to a hail*/						
    public static final Basic HAIL_ACKNOWLEDGE          = new Basic("HAIL_ACKNOWLEDGE");
    /**Set all currently addressed X10 lights to their first preset dim level*/		
    public static final Basic PRESET_DIM_1              = new Basic("PRESET_DIM_1");
    /**Set all currently addressed X10 lights to their second preset dim level*/
    public static final Basic PRESET_DIM_2              = new Basic("PRESET_DIM_2");
    /**Signals an extended data transfer*/						
    public static final Basic EXTENDED_DATA_TRANSFER    = new Basic("EXTENDED_DATA_TRANSFER");
    /**Set all currently addressed X10 units to respond to status requests*/		
    public static final Basic STATUS_ON                 = new Basic("STATUS_ON");
    /**Set all currently addressed X10 units to not respond to status requests*/	
    public static final Basic STATUS_OFF                = new Basic("STATUS_OFF");
    /**Send back status information for all addressed X10 units. */
    public static final Basic STATUS_REQUEST            = new Basic("STATUS_REQUEST");
}
