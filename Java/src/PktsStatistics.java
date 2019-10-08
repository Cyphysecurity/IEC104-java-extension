import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
/*
 * @XI
 * This class computes statistics for pkts, similar to AnalyzeIOAClusterDistribution
 * it generates the analysis results to feed into ConstructAnalysisData.java
 * Currently, it supports the following analysis:
 * 1. computeAPCIType(): packet number percentage of three APCI types;
 * packet number percentage of APCI only and APCI + ASDU
 * more can be added as needed
 */

public class PktsStatistics implements APCIControlFieldType {
	//private AnalyzedObj analysisObj;
	private int cnt_total;	// total number of all packets
	private int cnt_apdu;	// total number of all APDUs
	private int cnt_asdu;	// total number of all ASDUs
	private int pkt_apci;	// number of packets which only contain APCI
	private int pkt_asdu;	// number of packets which contain APCI + ASDU
	private int cntI;		// number of APDU/APCI which is I format
	private int cntS;
	private int cntU;
	private int cnt_multiple_apci;	// number of packets which contain more than one APDUs
	private int cnt_invalid;
	private int numMalformedPackets;
	private HashSet<Integer> ioaNumSet;		// distinct IOA numbers, good packets
	private HashSet<Integer> malformedIoaNumSet;	// distinct IOA numbers contained in malformed packets
	private HashSet<AsduSystem> systemSet;	// distinct "system" (common asdu address and IOA number), good packets
	private HashSet<String> systemNameSet;	// distinct "system" string names, good packets
	private AnalyzedAPCI apciAnalysisObj;	// contain all the fields after apci analysis

	public PktsStatistics() {
		initialize();
	}

	// initialize any global variables
	public void initialize() {
		cnt_total = 0;
		pkt_apci = 0;
		pkt_asdu = 0;
		cntI = 0;
		cntS = 0;
		cntU = 0;
		cnt_apdu = 0;
		cnt_asdu = 0;
		cnt_multiple_apci = 0;
		cnt_invalid = 0;
		ioaNumSet = new HashSet<>();
		malformedIoaNumSet = new HashSet<>();
		systemSet = new HashSet<>();
		systemNameSet = new HashSet<>();
		apciAnalysisObj = new AnalyzedAPCI();
	}

	// compute numbers and percentages of three types of APCI format
	// print to screen
	// compute numbers and percentages of packets which contain APCI only
	// or APCI + ASDU
	public void computeAPCIType(ArrayList<Packet> pkts) {
		for (Packet p : pkts) {
			cnt_total++;
			boolean malformed = false;
			if (p.isMalformed()) {
				numMalformedPackets += 1;
				malformed = true;
				// continue;
			}
			if (malformed) {	// only generate IOA number set for malformed packets
				for (APCI apci : p.getApciObj()) {
					for (ASDU asdu : apci.getAsduObj()) {
						for (IOA ioa : asdu.getIoaObj()) {
							int ioaNum = ioa.getIoa();
							if (!malformedIoaNumSet.contains(ioaNum)) {
								malformedIoaNumSet.add(ioaNum);
							}
						}
					}
				}

			} else {
				apciAnalysisObj.setSrcIP(p.getIp_src());
				apciAnalysisObj.setDstIP(p.getIp_dst());
				int cnt_apci = 0;
				int asdu_flg = 0;
				//analysisObj = new AnalyzedObj();
				for (APCI apci : p.getApciObj()) {
					cnt_apci++;
					cnt_apdu++;
					if (apci.getType() == I_FORMAT) {
						cntI++;
						asdu_flg = 1;
					} else if (apci.getType() == S_FORMAT) {
						cntS++;
					} else if (apci.getType() == U_FORMAT) {
						cntU++;
					} else {
						System.out.println("Invalid APCI format!!!");
						cnt_invalid++;
					}
					for (ASDU asdu : apci.getAsduObj()) {
						cnt_asdu++;
						int commonAddr = asdu.getAddr();
						for (IOA ioa : asdu.getIoaObj()) {
							int ioaNum = ioa.getIoa();
							if (!ioaNumSet.contains(ioaNum)) {
								ioaNumSet.add(ioaNum);
							}
							AsduSystem s = new AsduSystem(commonAddr, ioaNum);
							if (!systemNameSet.contains(s.toString())) {
								systemNameSet.add(s.toString());
							}

							// update systemSet
							int existFlag = 0;
							if (systemSet.isEmpty()) {
								systemSet.add(s);
							}
							for (AsduSystem a : systemSet) {
								if (s.getCommon_addr() == a.getCommon_addr() && s.getIoa_num() == a.getIoa_num()) {
									existFlag = 1;
									break;
								}
							}
							if (existFlag == 0) {
								systemSet.add(s);
							}
						} // for ioa
					} // for asdu
				} // for apci
				if (cnt_apci > 1) {
					//System.out.format("This packet contains %d APDUs.\n", cnt_apci);
					cnt_multiple_apci++;
				}
				if (asdu_flg == 1) {
					pkt_asdu++;
				} else {
					pkt_apci++;
				}
			} // else, non-malformed operations
		} // for packet
		System.out.println("Total number of packets = " + cnt_total);
		System.out.println("Number of malformed packets = " + numMalformedPackets + ", %" + numMalformedPackets * 100 / cnt_total);
		System.out.println("Number of packets which contain only APCI = " + pkt_apci + " %" + pkt_apci * 100 / cnt_total);
		System.out.println("Number of packets which contain APCI + ASDU = " + pkt_asdu + " %" + pkt_asdu * 100 / cnt_total);
		System.out.format("Total number of APDUs = %d\n", cnt_apdu);
		System.out.println("number of packets which contain more than one APDUs = " + cnt_multiple_apci + " %" + cnt_multiple_apci * 100 / cnt_apdu);
		System.out.println("Number of ASDUs = " + cnt_asdu);
		System.out.println("APCI I type of APDUs = " + cntI + " %" + cntI * 100 / cnt_apdu);
		System.out.println("APCI S type of APDUs = " + cntS + " %" + cntS * 100 / cnt_apdu);
		System.out.println("APCI U type of APDUs = " + cntU + " %" + cntU * 100 / cnt_apdu);		
		System.out.println("Number of invalid APCI format APDUs = " + cnt_invalid + " %" + cnt_invalid * 100 / cnt_apdu);
		System.out.println("ioaNumSet contains " + ioaNumSet.size() + " distinct IOA numbers.\nAnd they are: "+ ioaNumSet);
		System.out.println("systemSet contains " + systemSet.size() + " distinct systems.\nAnd they are: " + systemSet);
	}

	public void setIoaNumSet(HashSet<Integer> ioaNumSet) {
		this.ioaNumSet = ioaNumSet;
	}

	public HashSet<Integer> getIoaNumSet() {
		return ioaNumSet;
	}

	public void setMalformedIoaNumSet(HashSet<Integer> malformedIoaNumSet) {
		this.malformedIoaNumSet = malformedIoaNumSet;
	}

	public HashSet<Integer> getMalformedIoaNumSet() {
		return malformedIoaNumSet;
	}

	public void setSystemSet(HashSet<AsduSystem> systemSet) {
		this.systemSet = systemSet;
	}

	public HashSet<AsduSystem> getSystemSet() {
		return systemSet;
	}

	public void setSystemNameSet(HashSet<String> systemNameSet) {
		this.systemNameSet = systemNameSet;
	}

	public HashSet<String> getSystemNameSet() {
		return systemNameSet;
	}
}