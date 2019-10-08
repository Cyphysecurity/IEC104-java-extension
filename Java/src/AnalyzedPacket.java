/*
 This class focuses on Packet level analysis, based on AnalyzedObj
 similar role as AnalyzedIOAClusterDistribution & ConstructAnalysisData
 */

import java.util.*;
import java.io.*;
import java.util.stream.Stream;

public class AnalyzedPacket implements APCIControlFieldType {
    private int numMalformedPackets;
    private String pktSignature;
    private AnalyzedObj analysisObj;

    // Constructor
    public AnalyzedPacket() {
        initialize();
    }

    public void initialize() {
        this.numMalformedPackets = 0;
        this.pktSignature = "";
        this.analysisObj = null;
    }

    // collect basic information:
    // 1 packet per line
    public void basics(ArrayList<Packet> pkts, List<AnalyzedObj> pkts_out, int choice) {
        for (Packet pkt : pkts) {
            if (pkt.isMalformed()) {
                numMalformedPackets++;
                //continue;
            }
/*
this.tcpFlagsSyn = -1;
			this.tcpFlagsFin = -1;
			this.tcpFlagsAck = -1;
			this.tcpFlagsReset = -1;
			this.direction = "";
			this.numIoa = 0;
			this.ioaArr = new ArrayList<>();
			this.numS = 0;
			this.numU = 0;
			this.numI = 0;
 */
            pktSignature = pkt.getIp_src() + "," + pkt.getIp_dst() + ",";
            analysisObj = new AnalyzedObj(choice);
            analysisObj.setTcpFlagsSyn(pkt.getTcp_flags_syn());
            analysisObj.setTcpFlagsFin(pkt.getTcp_flags_fin());
            analysisObj.setTcpFlagsReset(pkt.getTcp_flags_reset());
            analysisObj.setTcpFlagsAck(pkt.getTcp_flags_ack());
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
                pktSignature += apciType;
                analysisObj.setApciFormatType(apciType);
                analysisObj.setApduLen(apci.getApdulen());
                pkts_out.add(analysisObj);
                //for (ASDU asdu : apci.getAsduObj()) {
                //} // for asdu
            } // for apci

        } // for packet
    }


}
