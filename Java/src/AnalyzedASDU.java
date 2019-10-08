/* @XI
 * This is a supported class for analysis based on the ASDU Type
 * It holds relevant data for each ASDU (not the packet) that are needed
 * used in TimeSeries.java, public void analyzeASDUType(ArrayList<Packet> pkts)
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;

public class AnalyzedASDU {
    private double time;
    private int asduCnt;    // count of distinct ASDUs, not the count for each individual ASDU
    private double asduCntRate;
    private ArrayList<AsduSystem> systemList;
    private int sysCnt;     // count of distinct SYSTEMs, not the count for each individual SYSTEM
    private double sysCntRate;
    private ArrayList<IOA> ioaList;


    public AnalyzedASDU() {
        initialize();
    }

    public AnalyzedASDU(double T, int asduCnt, double asduR, ArrayList<AsduSystem> sysList, int sysCnt, double sysR, ArrayList<IOA> ioaList) {
        this.time = T;
        this.asduCnt = asduCnt;
        this.asduCntRate = asduR;
        this.systemList = sysList;
        this.sysCnt = sysCnt;
        this.sysCntRate = sysR;
        this.ioaList = ioaList;
    }
    public void initialize() {
        this.time = -1.0;
        this.asduCnt = -1;
        this.asduCntRate = -1.0;
        this.systemList = new ArrayList<>();
        this.sysCnt = -1;
        this.sysCntRate = -1.0;
        this.ioaList = new ArrayList<>();
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    public void setAsduCnt(int asduCnt) {
        this.asduCnt = asduCnt;
    }

    public int getAsduCnt() {
        return asduCnt;
    }

    public void setAsduCntRate(double asduCntRate) {
        this.asduCntRate = asduCntRate;
    }

    public double getAsduCntRate() {
        return asduCntRate;
    }

    public void setSystemList(ArrayList<AsduSystem> systemList) {
        this.systemList = systemList;
    }

    public ArrayList<AsduSystem> getSystemList() {
        return systemList;
    }

    public void setSysCnt(int sysCnt) {
        this.sysCnt = sysCnt;
    }

    public int getSysCnt() {
        return sysCnt;
    }

    public void setSysCntRate(double sysCntRate) {
        this.sysCntRate = sysCntRate;
    }

    public double getSysCntRate() {
        return sysCntRate;
    }

    public void setIoaList(ArrayList<IOA> ioaList) {
        this.ioaList = ioaList;
    }

    public ArrayList<IOA> getIoaList() {
        return ioaList;
    }

   
}
