/*
 * These are interested packet fields that are used to perform analysis on a given packet.
 * To add or remove any of these interested fields, simply add or remove them from the enum list below.
 * However, for efficiency, be sure to add them in order as they appear on the pcap packet attributes/fields, 
 * from the lowest layer first, e.g. frame is lowest and application is highest.
 */
public enum PacketFieldsToParse {
	
	frameTime ("frame.time_epoch"), 
	frameTimeRelative ("frame.time_relative"),
	frameNumber ("frame.number"),
	frameLen ("frame.len"),
	/*ethDst ("eth.dst:"),
	ethDstResolved ("eth.dst_resolved"),
	ethSrc ("eth.src:"),
	ethSrcResolved ("eth.src_resolved"),
	ipVersion ("ip.version"),*/
	ipLen ("ip.len"),
	//ipSrc ("ip.src:"),
	ipSrc ("ip.src"),
	ipSrcHost ("ip.src_host"),
	//ipDst ("ip.dst:"),
	ipDst ("ip.dst"),
	ipDstHost ("ip.dst_host"),
	tcpSrcPort ("tcp.srcport"),
	tcpDstPort ("tcp.dstport"),
	tcpAck ("tcp.ack"),
	tcpPduSize ("tcp.pdu.size"),
	tcpFlagsAck ("tcp.flags.ack"),
	tcpFlagsReset ("tcp.flags.reset"),
	tcpFlagsSyn ("tcp.flags.syn"),
	tcpFlagsFin ("tcp.flags.fin"),

	// APCI fields
	apciStart ("104asdu.start"),
	apciLen ("104apci.apdulen"),
	apciType ("104apci.type"),
	apciTx ("104apci.tx"),
	apciRx ("104apci.rx"),
	apciUType ("104apci.utype"),
//	apciUTypeTestfrCon ("104apci.utype.testfr.con"),
//	apciUTypeTestfrAct ("104apci.utype.testfr.act"),
//	apciUTypeStopdtCon ("104apci.utype.stopdt.con"),
//	apciUTypeStopdtAct ("104apci.utype.stopdt.act"),
//	apciUTypeStartdtCon ("104apci.utype.startdt.con"),
//	apciUTypeStartdtAct ("104apci.utype.startdt.act"),

	// ASDU fields
	asduTypeID ("104asdu.typeid"),
	asduSq ("104asdu.sq"),
	asduNumix ("104asdu.numix"),
	asduCauseTx ("104asdu.causetx"),
	asduNega ("104asdu.nega"),
	asduTest ("104asdu.test"),
	asduOa ("104asdu.oa"),
	asduAddr ("104asdu.addr"),
	// IO fields
	//asduIOA ("IOA:"),
	asduIOA ("IOA"),
	asduIoa ("104asdu.ioa"),
	asduFloat ("104asdu.float"),
	asduQdsOv ("104asdu.qds.ov"),
	asduQdsBl ("104asdu.qds.bl"),
	asduQdsSb ("104asdu.qds.sb"),
	asduQdsNt ("104asdu.qds.nt"),
	asduQdsIv ("104asdu.qds.iv"),
	asduQos ("104asdu.qos"),
	asduQosQl ("104asdu.qos.ql"),
	asduQosSe ("104asdu.qos.se"),
	asduQoi ("104asdu.qoi"),
	asduVtiV ("104asdu.vti.v"),
	asduVtiT ("104asdu.vti.t"),
	asduNormv("104asdu.normval"),
	//asduCp56Time ("104asdu.cp56time:"),
	asduCp56Time ("104asdu.cp56time"),
	asduSiq ("104asdu.siq"),
	asduSiqSpi ("104asdu.siq.spi"),
	asduSiqBl ("104asdu.siq.bl"),
	asduSiqSb ("104asdu.siq.sb"),
	asduSiqNt ("104asdu.siq.nt"),
	asduSitIv ("104asdu.siq.iv"),
	asduDiqDpi ("104asdu.diq.dpi"),
	asduBitstring ("104asdu.bitstring"),
	asduCoiR ("104asdu.coi_r"),
	asduCoiI ("104asdu.coi_i"),
	asduMalformed ("malformed");
	
	private String fieldName;

	private PacketFieldsToParse(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
