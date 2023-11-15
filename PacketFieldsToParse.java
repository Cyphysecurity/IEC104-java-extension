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
	frameColoringRule ("frame.coloring_rule.string"),

	ethDst ("eth.dst:"),
	ethDstResolved ("eth.dst_resolved"),
	ethSrc ("eth.src:"),
	ethSrcResolved ("eth.src_resolved"),
	//ipVersion ("ip.version"),
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
	tcpFlagsFinTree("tcp.flags.fin_tree"),
	tcpFlagsFin ("tcp.flags.fin"),
	tcpConnectionFin("tcp.connection.fin"),

	// APCI fields
	apciStart ("iec60870_asdu.start"),
	apciLen ("iec60870_104.apdulen"),
	apciType ("iec60870_104.type"),
	apciTx ("iec60870_104.tx"),
	apciRx ("iec60870_104.rx"),
	apciUType ("iec60870_104.utype"),
//	apciUTypeTestfrCon ("iec60870_104.utype.testfr.con"),
//	apciUTypeTestfrAct ("iec60870_104.utype.testfr.act"),
//	apciUTypeStopdtCon ("iec60870_104.utype.stopdt.con"),
//	apciUTypeStopdtAct ("iec60870_104.utype.stopdt.act"),
//	apciUTypeStartdtCon ("iec60870_104.utype.startdt.con"),
//	apciUTypeStartdtAct ("iec60870_104.utype.startdt.act"),

	// ASDU fields
	asduTypeID ("iec60870_asdu.typeid"),
	asduSq ("iec60870_asdu.sq"),
	asduNumix ("iec60870_asdu.numix"),
	asduCauseTx ("iec60870_asdu.causetx"),
	asduNega ("iec60870_asdu.nega"),
	asduTest ("iec60870_asdu.test"),
	asduOa ("iec60870_asdu.oa"),
	asduAddr ("iec60870_asdu.addr"),
	asduRawData ("iec60870_asdu.rawdata"),

	// IO fields
	asduIOA ("IOA:"),
	//asduIOA ("IOA"),
	asduIoa ("iec60870_asdu.ioa"),
	asduFloat ("iec60870_asdu.float"),
	asduQdsOv ("iec60870_asdu.qds.ov"),
	asduQdsBl ("iec60870_asdu.qds.bl"),
	asduQdsSb ("iec60870_asdu.qds.sb"),
	asduQdsNt ("iec60870_asdu.qds.nt"),
	asduQdsIv ("iec60870_asdu.qds.iv"),
	asduQos ("iec60870_asdu.qos"),
	asduQosQl ("iec60870_asdu.qos.ql"),
	asduQosSe ("iec60870_asdu.qos.se"),
	asduQoi ("iec60870_asdu.qoi"),
	asduVtiV ("iec60870_asdu.vti.v"),
	asduVtiT ("iec60870_asdu.vti.t"),
	asduNormv("iec60870_asdu.normval"),
	//asduCp56Time ("iec60870_asdu.cp56time:"),
	asduCp56Time ("iec60870_asdu.cp56time"),
	asduSiq ("iec60870_asdu.siq"),
	asduSiqTree ("iec60870_asdu.siq_tree"),
	asduSiqSpi ("iec60870_asdu.siq.spi"),
	asduSiqBl ("iec60870_asdu.siq.bl"),
	asduSiqSb ("iec60870_asdu.siq.sb"),
	asduSiqNt ("iec60870_asdu.siq.nt"),
	asduSiaIv ("iec60870_asdu.siq.iv"),
	asduDiqDpi ("iec60870_asdu.diq.dpi"),
	asduBitstring ("iec60870_asdu.bitstring"),
	asduCoiR ("iec60870_asdu.coi_r"),
	asduCoiI ("iec60870_asdu.coi_i"),
	asduSco ("iec60870_asdu.sco"),
	asduScoOn ("iec60870_asdu.sco.on"),
	asduScoQu ("iec60870_asdu.sco.qu"),
	asduScoSe ("iec60870_asdu.sco.se"),
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
