/*
 This class focuses on APDU level analysis, based on AnalyzedObj
 similar role as AnalyzedIOAClusterDistribution & ConstructAnalysisData
 */

import java.util.*;
import java.io.*;
import java.util.stream.Stream;

public class AnalyzedAPDU implements APCIControlFieldType {
    private int numMalformedPackets;
    private String apduSignature;
    private AnalyzedObj analysisObj;

    // Constructor
    public AnalyzedAPDU() {
        initialize();
    }

    public void initialize() {
        this.numMalformedPackets = 0;
        this.apduSignature = "";
        this.analysisObj = null;
    }

    // collect basic information:
    // 1 APDU per line
    public void basics(ArrayList<Packet> pkts, List<AnalyzedObj> apdus, int choice) {
        for (Packet pkt : pkts) {
            if (pkt.isMalformed()) {
                numMalformedPackets++;
                continue;
            }

            apduSignature = pkt.getIp_src() + "," + pkt.getIp_dst() + ",";
            analysisObj = new AnalyzedObj(choice);
            analysisObj.setSrcIP(pkt.getIp_src());
            analysisObj.setDstIP(pkt.getIp_dst());
            analysisObj.setEpochTime(pkt.getFrame_time_epoch());
            analysisObj.setRelativeTime(pkt.getFrame_time_relative());
            for (APCI apci : pkt.getApciObj()) {
                String apciType;
                if (apci.getType() == U_FORMAT) {
                    apciType = "U-format";
                } else if (apci.getType() == S_FORMAT) {
                    apciType = "S-format";
                } else if (apci.getType() == I_FORMAT){
                    apciType = "I-format";
                } else {
                    apciType = "invalid-format";
                }
                apduSignature += apciType;
                analysisObj.setApciFormatType(apciType);
                analysisObj.setApduLen(apci.getApdulen());
                apdus.add(analysisObj);
                //for (ASDU asdu : apci.getAsduObj()) {
                //} // for asdu
            } // for apci

        } // for packet
    }


}
