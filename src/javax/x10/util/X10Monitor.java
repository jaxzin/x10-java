/*
 * X10Monitor.java
 *
 * Created on July 29, 2002, 12:51 AM
 */

package javax.x10.util;

import java.util.*;

import javax.x10.*;
import javax.x10.codes.*;
import javax.x10.event.*;

/**
 *
 * @author  Brian Jackson (brian@jaxzin.com)
 */
public class X10Monitor {
    
    
    private class MonitorListener implements X10Listener, X10TransmitterListener {
        private MonitorListener() {
        }
        
        public void address(X10Event e) {               recordEvent(e); }
        public void allLightsOff(X10Event e) {          recordEvent(e); }
        public void allLightsOn(X10Event e) {           recordEvent(e); }
        public void allUnitsOff(X10Event e) {           recordEvent(e); }
        public void brighten(X10Event e) {              recordEvent(e); }
        public void dim(X10Event e) {                   recordEvent(e); }
        public void extendedCode(X10Event e) {          recordEvent(e); }
        public void extendedDataTransfer(X10Event e) {  recordEvent(e); }
        public void hailAcknowledge(X10Event e) {       recordEvent(e); }
        public void hailRequest(X10Event e) {           recordEvent(e); }
        public void off(X10Event e) {                   recordEvent(e); }
        public void on(X10Event e) {                    recordEvent(e); }
        public void presetDim1(X10Event e) {            recordEvent(e); }
        public void presetDim2(X10Event e) {            recordEvent(e); }
        public void statusOff(X10Event e) {             recordEvent(e); }
        public void statusOn(X10Event e) {              recordEvent(e); }
        public void statusRequest(X10Event e) {         recordEvent(e); }
        
        public void eventDelivered(X10TransmitterEvent e) {
            recordEvent(e.getX10Event());
        }
        public void eventUndelivered(X10TransmitterEvent e) {
            // Do nothing
        }
        public void queueEmptied(X10TransmitterEvent e) {
            // Do nothing
        }
        public void queueUpdated(X10TransmitterEvent e) {
            // Do nothing
        }
    }
    
    private HouseCode addressedHouseCode;
    private Set addressedUnitCodes;
    private X10Event.Type lastEventType;
    private Map statusMap;
    private Map brightnessMap;
    private Map lightMap;
    
    /** Creates a new instance of X10Monitor */
    public X10Monitor(X10Connection xconn) {
        // Validate argument
        if(xconn == null)
            throw new IllegalArgumentException("X10Connection can not be null.");
        
        // Register the appropriate listeners
        if(xconn.isX10Receiver()) {
            xconn.getX10Receiver().addX10Listener(new MonitorListener());
        }
        if(xconn.isX10Transmitter()) {
            xconn.getX10Transmitter().addX10TransmitterListener(new MonitorListener());
        }
        initialize();
    }
    
    private void initialize() {
        this.addressedHouseCode = null;
        this.addressedUnitCodes = new HashSet();
        this.lastEventType = null;
        this.statusMap = new HashMap();
        this.brightnessMap = new HashMap();
        this.lightMap = new HashMap();
    }
    
    public void recordEvent(X10Event e) {
        synchronized(this.addressedHouseCode) {
            if(e.getType() == X10Event.ADDRESS) {
                if(this.lastEventType == X10Event.FUNCTION) {
                    clearAddressing();
                    this.addressedHouseCode = e.getHouseCode();
                } else if(e.getHouseCode() != this.addressedHouseCode) {
                    clearAddressing();
                    this.addressedHouseCode = e.getHouseCode();
                }
                this.addressedUnitCodes.add(e.getOtherCode());
            } else if(e.getOtherCode() == FunctionCode.ON) {
                for(Iterator i = this.addressedUnitCodes.iterator();i.hasNext();) {
                    UnitCode unitCode = (UnitCode)i.next();
                    Boolean old = (Boolean)set(this.statusMap,this.addressedHouseCode,unitCode,new Boolean(true));
                    if(!old.booleanValue())
                        set(this.brightnessMap,this.addressedHouseCode,unitCode,new Double(1.0));
                }
            } else if(e.getOtherCode() == FunctionCode.OFF) {
                for(Iterator i = this.addressedUnitCodes.iterator();i.hasNext();) {
                    UnitCode unitCode = (UnitCode)i.next();
                    set(this.statusMap,this.addressedHouseCode,unitCode,new Boolean(false));
                    set(this.brightnessMap,this.addressedHouseCode,unitCode,new Double(0.0));
                }
            } else if(e.getOtherCode() == FunctionCode.DIM) {
                for(Iterator i = this.addressedUnitCodes.iterator();i.hasNext();) {
                    UnitCode unitCode = (UnitCode)i.next();
                    if(isLight(this.addressedHouseCode,unitCode)) {
                        double level = getBrightnessLevel(this.addressedHouseCode,unitCode);
                        if(isOff(this.addressedHouseCode,unitCode))
                            level = X10Event.MAX_BRIGHTNESS_LEVEL;
                        set(this.brightnessMap,this.addressedHouseCode,unitCode,new Double(Math.min(level-e.getBrightnessLevel(),X10Event.MIN_BRIGHTNESS_LEVEL)));
                    }
                }
            } else if(e.getOtherCode() == FunctionCode.BRIGHTEN) {
                for(Iterator i = this.addressedUnitCodes.iterator();i.hasNext();) {
                    UnitCode unitCode = (UnitCode)i.next();
                    if(isLight(this.addressedHouseCode,unitCode)) {
                        double level = getBrightnessLevel(this.addressedHouseCode,unitCode);
                        if(isOff(this.addressedHouseCode,unitCode))
                            level = X10Event.MAX_BRIGHTNESS_LEVEL;
                        set(this.brightnessMap,this.addressedHouseCode,unitCode,new Double(Math.max(level+e.getBrightnessLevel(),X10Event.MAX_BRIGHTNESS_LEVEL)));
                    }
                }
            } else if(e.getOtherCode() == FunctionCode.ALL_UNITS_OFF) {
                this.statusMap.clear();
            } else if(e.getOtherCode() == FunctionCode.ALL_LIGHTS_OFF) {
                allLights(false);
            } else if(e.getOtherCode() == FunctionCode.ALL_LIGHTS_ON) {
                allLights(true);
            }
            
            this.lastEventType = e.getType(); 
        }
    }
    
    public void recordEvents(X10Event[] events) {
        synchronized(this.addressedHouseCode) {
            for(int i = 0; i < events.length; i++) {
                recordEvent(events[i]);
            }
        }
    }
    
    public HouseCode getAddressedHouseCode() {
        return this.addressedHouseCode;
    }
    
    public Set getAddressUnitCodes() {
        return Collections.unmodifiableSet(this.addressedUnitCodes);
    }
    
    public boolean isOn(HouseCode houseCode, UnitCode unitCode) {
        Boolean on = (Boolean)get(this.statusMap,houseCode,unitCode,new Boolean(false));
        return on.booleanValue();
    }
    
    private void setOn(HouseCode houseCode, UnitCode unitCode, boolean on) {
        set(this.statusMap,houseCode,unitCode,new Boolean(on));
    }
    
    public boolean isOff(HouseCode houseCode, UnitCode unitCode) {
        return !isOn(houseCode,unitCode);
    }
    public double getBrightnessLevel(HouseCode houseCode, UnitCode unitCode) {
        Double dfault;
        if(isOn(houseCode,unitCode))
            dfault = new Double(X10Event.MAX_BRIGHTNESS_LEVEL);
        else
            dfault = new Double(X10Event.MIN_BRIGHTNESS_LEVEL);
            
        Double level = (Double)get(this.brightnessMap,houseCode,unitCode,dfault);
        return level.doubleValue();
    }
    public void setLight(HouseCode houseCode, UnitCode unitCode, boolean isLight) {
        set(this.lightMap,houseCode,unitCode,new Boolean(isLight));
    }
    public boolean isLight(HouseCode houseCode, UnitCode unitCode) {
        Boolean light = (Boolean)get(this.lightMap,houseCode,unitCode,new Boolean(true));
        return light.booleanValue();
    }
    
    private Object get(Map houseMap, HouseCode houseCode, UnitCode unitCode, Object dfault) {
        if(houseCode == null)
            throw new IllegalArgumentException("HouseCode can not be null.");
        if(unitCode == null)
            throw new IllegalArgumentException("UnitCode can not be null.");
        
        Map unitMap = (Map)houseMap.get(houseCode);
        if(unitMap == null) {
            unitMap = new HashMap();
            houseMap.put(houseCode,unitMap);
        }
        Object value = unitMap.get(unitCode);
        if(value == null) {
            value = dfault;
            unitMap.put(unitCode,value);
        }
        return value;
    }

    private Object set(Map houseMap, HouseCode houseCode, UnitCode unitCode, Object value) {
        if(houseCode == null)
            throw new IllegalArgumentException("HouseCode can not be null.");
        if(unitCode == null)
            throw new IllegalArgumentException("UnitCode can not be null.");
        
        Map unitMap = (Map)houseMap.get(houseCode);
        if(unitMap == null) {
            unitMap = new HashMap();
            houseMap.put(houseCode,unitMap);
        }
        
        Object oldValue = unitMap.put(unitCode,value);
        
        return oldValue;
    }
    
    private void clearAddressing() {
        this.addressedHouseCode = null;
        this.addressedUnitCodes.clear();
    }
    
    private void allLights(boolean on) {
        for(Iterator i = HouseCode.iterator();i.hasNext();) {
            HouseCode hc = (HouseCode)i.next();
            for(Iterator j = UnitCode.iterator();j.hasNext();) {
                UnitCode uc = (UnitCode)j.next();
                if(isLight(hc,uc)) {
                    setOn(hc,uc,on);
                }
            }
        }
    }
}
