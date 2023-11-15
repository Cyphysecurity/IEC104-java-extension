/* @XI
 * This class contains all the fields for packet or data unit statistics on APCI component
 * To be called in PktStatistics.java
 * Work for fuction 7 in the main menu
 */

import java.util.ArrayList;
import java.util.HashMap;

public class AnalyzedAPCI implements APCIControlFieldType{
    private double firstTimeSeen;
    private double lastTimeSeen;
    private double averageTimeDelta;
    private String srcIP;
    private String dstIP;
    private String direction;
    private String apciFormatType;
    private boolean isContainS;
    private boolean isContainU;
    private boolean isContainI;
    private int pktN_S;
    private int pktN_U;
    private int pktN_I;
    private int numTimesReported;
    private int numMalformedPackets;

    // Default constructor
    public AnalyzedAPCI() {
        initialize();
    }

    public void initialize() {
        this.firstTimeSeen = -1.0;
        this.lastTimeSeen = -1.0;
        this.averageTimeDelta = -1.0;
        this.srcIP = "";
        this.dstIP = "";
        this.direction = "";
        this.apciFormatType = "";
        this.numTimesReported = 0;
        this.numMalformedPackets = 0;
        this.isContainI = false;
        this.isContainS = false;
        this.isContainU = false;
        this.pktN_I = 0;
        this.pktN_S = 0;
        this.pktN_U = 0;

    }

    // setters and getters


    public void setApciFormatType(String apciFormatType) {
        this.apciFormatType = apciFormatType;
    }

    public String getApciFormatType() {
        return apciFormatType;
    }

    public void setContainI(boolean containI) {
        isContainI = containI;
    }

    public boolean getContainI() {
        return isContainI;
    }

    public void setContainU(boolean containU) {
        isContainU = containU;
    }

    public boolean getContainU() {
        return isContainU;
    }

    public void setContainS(boolean containS) {
        isContainS = containS;
    }

    public boolean getContainS() {
        return isContainS;
    }

    public void setPktN_I(int pktN_I) {
        this.pktN_I = pktN_I;
    }

    public int getPktN_I() {
        return pktN_I;
    }

    public void setPktN_S(int pktN_S) {
        this.pktN_S = pktN_S;
    }

    public int getPktN_S() {
        return pktN_S;
    }

    public void setPktN_U(int pktN_U) {
        this.pktN_U = pktN_U;
    }

    public int getPktN_U() {
        return pktN_U;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public void setDstIP(String dstIP) {
        this.dstIP = dstIP;
    }

    public String getDstIP() {
        return dstIP;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setNumMalformedPackets(int numMalformedPackets) {
        this.numMalformedPackets = numMalformedPackets;
    }
}
