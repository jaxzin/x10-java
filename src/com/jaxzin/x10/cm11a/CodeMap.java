/*
 * CodeMap.java
 *
 * Created on July 23, 2002, 12:17 AM
 */

package com.jaxzin.x10.cm11a;

import java.util.*;
import javax.x10.codes.*;

import com.jaxzin.util.Nibble;

/**
 *
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public final class CodeMap {
    
    private static Map houseCodeMap;
    private static Map otherCodeMap;

    private static Map reverseHouseCodeMap;
    private static Map reverseUnitCodeMap;
    private static Map reverseFunctionCodeMap;
    
    // Initialize static fields
    static {
        houseCodeMap = new Hashtable();
        houseCodeMap.put(HouseCode.A,                           new Nibble(0x6));
        houseCodeMap.put(HouseCode.B,                           new Nibble(0xE));
        houseCodeMap.put(HouseCode.C,                           new Nibble(0x2));
        houseCodeMap.put(HouseCode.D,                           new Nibble(0xA));
        houseCodeMap.put(HouseCode.E,                           new Nibble(0x1));
        houseCodeMap.put(HouseCode.F,                           new Nibble(0x9));
        houseCodeMap.put(HouseCode.G,                           new Nibble(0x5));
        houseCodeMap.put(HouseCode.H,                           new Nibble(0xD));
        houseCodeMap.put(HouseCode.I,                           new Nibble(0x7));
        houseCodeMap.put(HouseCode.J,                           new Nibble(0xF));
        houseCodeMap.put(HouseCode.K,                           new Nibble(0x3));
        houseCodeMap.put(HouseCode.L,                           new Nibble(0xB));
        houseCodeMap.put(HouseCode.M,                           new Nibble(0x0));
        houseCodeMap.put(HouseCode.N,                           new Nibble(0x8));
        houseCodeMap.put(HouseCode.O,                           new Nibble(0x4));
        houseCodeMap.put(HouseCode.P,                           new Nibble(0xC));
        
        otherCodeMap = new Hashtable();
        otherCodeMap.put(UnitCode.UNIT_1,                       new Nibble(0x6));
        otherCodeMap.put(UnitCode.UNIT_2,                       new Nibble(0xE));
        otherCodeMap.put(UnitCode.UNIT_3,                       new Nibble(0x2));
        otherCodeMap.put(UnitCode.UNIT_4,                       new Nibble(0xA));
        otherCodeMap.put(UnitCode.UNIT_5,                       new Nibble(0x1));
        otherCodeMap.put(UnitCode.UNIT_6,                       new Nibble(0x9));
        otherCodeMap.put(UnitCode.UNIT_7,                       new Nibble(0x5));
        otherCodeMap.put(UnitCode.UNIT_8,                       new Nibble(0xD));
        otherCodeMap.put(UnitCode.UNIT_9,                       new Nibble(0x7));
        otherCodeMap.put(UnitCode.UNIT_10,                      new Nibble(0xF));
        otherCodeMap.put(UnitCode.UNIT_11,                      new Nibble(0x3));
        otherCodeMap.put(UnitCode.UNIT_12,                      new Nibble(0xB));
        otherCodeMap.put(UnitCode.UNIT_13,                      new Nibble(0x0));
        otherCodeMap.put(UnitCode.UNIT_14,                      new Nibble(0x8));
        otherCodeMap.put(UnitCode.UNIT_15,                      new Nibble(0x4));
        otherCodeMap.put(UnitCode.UNIT_16,                      new Nibble(0xC));

        otherCodeMap.put(FunctionCode.ALL_UNITS_OFF,            new Nibble(0x0));
        otherCodeMap.put(FunctionCode.ALL_LIGHTS_ON,            new Nibble(0x1));
        otherCodeMap.put(FunctionCode.ON,                       new Nibble(0x2));
        otherCodeMap.put(FunctionCode.OFF,                      new Nibble(0x3));
        otherCodeMap.put(FunctionCode.DIM,                      new Nibble(0x4));
        otherCodeMap.put(FunctionCode.BRIGHTEN,                 new Nibble(0x5));
        otherCodeMap.put(FunctionCode.ALL_LIGHTS_OFF,           new Nibble(0x6));
        otherCodeMap.put(FunctionCode.EXTENDED_CODE,            new Nibble(0x7));
        otherCodeMap.put(FunctionCode.HAIL_REQUEST,             new Nibble(0x8));
        otherCodeMap.put(FunctionCode.HAIL_ACKNOWLEDGE,         new Nibble(0x9));
        otherCodeMap.put(FunctionCode.PRESET_DIM_1,             new Nibble(0xA));
        otherCodeMap.put(FunctionCode.PRESET_DIM_2,             new Nibble(0xB));
        otherCodeMap.put(FunctionCode.EXTENDED_DATA_TRANSFER,   new Nibble(0xC));
        otherCodeMap.put(FunctionCode.STATUS_ON,                new Nibble(0xD));
        otherCodeMap.put(FunctionCode.STATUS_OFF,               new Nibble(0xE));
        otherCodeMap.put(FunctionCode.STATUS_REQUEST,           new Nibble(0xF));
        
        reverseHouseCodeMap = new Hashtable();
        reverseHouseCodeMap.put(new Nibble(0x6),                HouseCode.A);
        reverseHouseCodeMap.put(new Nibble(0xE),                HouseCode.B);
        reverseHouseCodeMap.put(new Nibble(0x2),                HouseCode.C);
        reverseHouseCodeMap.put(new Nibble(0xA),                HouseCode.D);
        reverseHouseCodeMap.put(new Nibble(0x1),                HouseCode.E);
        reverseHouseCodeMap.put(new Nibble(0x9),                HouseCode.F);
        reverseHouseCodeMap.put(new Nibble(0x5),                HouseCode.G);
        reverseHouseCodeMap.put(new Nibble(0xD),                HouseCode.H);
        reverseHouseCodeMap.put(new Nibble(0x7),                HouseCode.I);
        reverseHouseCodeMap.put(new Nibble(0xF),                HouseCode.J);
        reverseHouseCodeMap.put(new Nibble(0x3),                HouseCode.K);
        reverseHouseCodeMap.put(new Nibble(0xB),                HouseCode.L);
        reverseHouseCodeMap.put(new Nibble(0x0),                HouseCode.M);
        reverseHouseCodeMap.put(new Nibble(0x8),                HouseCode.N);
        reverseHouseCodeMap.put(new Nibble(0x4),                HouseCode.O);
        reverseHouseCodeMap.put(new Nibble(0xC),                HouseCode.P);
        
        reverseUnitCodeMap = new Hashtable();
        reverseUnitCodeMap.put(new Nibble(0x6),                UnitCode.UNIT_1);
        reverseUnitCodeMap.put(new Nibble(0xE),                UnitCode.UNIT_2);
        reverseUnitCodeMap.put(new Nibble(0x2),                UnitCode.UNIT_3);
        reverseUnitCodeMap.put(new Nibble(0xA),                UnitCode.UNIT_4);
        reverseUnitCodeMap.put(new Nibble(0x1),                UnitCode.UNIT_5);
        reverseUnitCodeMap.put(new Nibble(0x9),                UnitCode.UNIT_6);
        reverseUnitCodeMap.put(new Nibble(0x5),                UnitCode.UNIT_7);
        reverseUnitCodeMap.put(new Nibble(0xD),                UnitCode.UNIT_8);
        reverseUnitCodeMap.put(new Nibble(0x7),                UnitCode.UNIT_9);
        reverseUnitCodeMap.put(new Nibble(0xF),                UnitCode.UNIT_10);
        reverseUnitCodeMap.put(new Nibble(0x3),                UnitCode.UNIT_11);
        reverseUnitCodeMap.put(new Nibble(0xB),                UnitCode.UNIT_12);
        reverseUnitCodeMap.put(new Nibble(0x0),                UnitCode.UNIT_13);
        reverseUnitCodeMap.put(new Nibble(0x8),                UnitCode.UNIT_14);
        reverseUnitCodeMap.put(new Nibble(0x4),                UnitCode.UNIT_15);
        reverseUnitCodeMap.put(new Nibble(0xC),                UnitCode.UNIT_16);
        
        reverseFunctionCodeMap = new Hashtable();
        reverseFunctionCodeMap.put(new Nibble(0x0),            FunctionCode.ALL_UNITS_OFF);
        reverseFunctionCodeMap.put(new Nibble(0x1),            FunctionCode.ALL_LIGHTS_ON);
        reverseFunctionCodeMap.put(new Nibble(0x2),            FunctionCode.ON);
        reverseFunctionCodeMap.put(new Nibble(0x3),            FunctionCode.OFF);
        reverseFunctionCodeMap.put(new Nibble(0x4),            FunctionCode.DIM);
        reverseFunctionCodeMap.put(new Nibble(0x5),            FunctionCode.BRIGHTEN);
        reverseFunctionCodeMap.put(new Nibble(0x6),            FunctionCode.ALL_LIGHTS_OFF);
        reverseFunctionCodeMap.put(new Nibble(0x7),            FunctionCode.EXTENDED_CODE);
        reverseFunctionCodeMap.put(new Nibble(0x8),            FunctionCode.HAIL_REQUEST);
        reverseFunctionCodeMap.put(new Nibble(0x9),            FunctionCode.HAIL_ACKNOWLEDGE);
        reverseFunctionCodeMap.put(new Nibble(0xA),            FunctionCode.PRESET_DIM_1);
        reverseFunctionCodeMap.put(new Nibble(0xB),            FunctionCode.PRESET_DIM_2);
        reverseFunctionCodeMap.put(new Nibble(0xC),            FunctionCode.EXTENDED_DATA_TRANSFER);
        reverseFunctionCodeMap.put(new Nibble(0xD),            FunctionCode.STATUS_ON);
        reverseFunctionCodeMap.put(new Nibble(0xE),            FunctionCode.STATUS_OFF);
        reverseFunctionCodeMap.put(new Nibble(0xF),            FunctionCode.STATUS_REQUEST);
    }
    /** Creates a new instance of CodeMap */
    private CodeMap() {
    }
    
    public static Nibble getNibble(HouseCode code) {
        return (Nibble)houseCodeMap.get(code);
    }
    
    public static Nibble getNibble(OtherCode code) {
        return (Nibble)otherCodeMap.get(code);
    }
    
    public static HouseCode getHouseCode(Nibble nibble) {
        return (HouseCode) reverseHouseCodeMap.get(nibble);
    }
    
    public static UnitCode getUnitCode(Nibble nibble) {
        return (UnitCode) reverseUnitCodeMap.get(nibble);
    }
    
    public static FunctionCode getFunctionCode(Nibble nibble) {
        return (FunctionCode) reverseFunctionCodeMap.get(nibble);
    }
}
