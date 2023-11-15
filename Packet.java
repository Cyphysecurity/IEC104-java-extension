import java.util.ArrayList;
import java.io.*;
/*
 * This class represent a single packet as Packet object 
 * which has, at current time, all the basic relevant
 * fields/attributes from each layer.
 * Fields can be added as needed
 * Plus, this object also has an array list that holds
 * all 104apci layers data that belong this the same packet
 */
public class Packet implements Serializable, APCIControlFieldType {

	private boolean isMalformed;
	private double frame_time_epoch;
	private double frame_time_relative;
	private int frame_number;
	private int frame_len;
	private String frame_coloringrule;

	private String eth_dst;
	private String eth_dst_resolved;
	private String eth_src;
	private String eth_src_resolved;
	private int ip_version;
	private int ip_len;
	private String ip_src;
	private String ip_src_host;
	private String ip_dst;
	private String ip_dst_host;
	private int tcp_src_port;
	private int tcp_dst_port;
	private long tcp_ack;
	private int tcp_pdu_size;
	private int tcp_flags_ack;
	private int tcp_flags_reset;
	private int tcp_flags_syn;
	private String tcp_flags_fin_tree;
	private int tcp_flags_fin;
	private String tcp_connection_fin;
	ArrayList<APCI> apciObj;

	// Default Constructor
	public Packet() {
		initialize();
	}
	
	public void initialize(){
		this.isMalformed = false;
		this.frame_time_epoch = -1.0;
		this.frame_time_relative = -1.0;
		this.frame_number = -1;
		this.frame_len = -1;
		this.frame_coloringrule = "";
		this.eth_dst = "";
		this.eth_dst_resolved = "";
		this.eth_src = "";
		this.eth_src_resolved = "";
		this.ip_version = -1;
		this.ip_len = -1;
		this.ip_src = "";
		this.ip_src_host = "";
		this.ip_dst = "";
		this.ip_dst_host = "";
		this.tcp_src_port = -1;
		this.tcp_dst_port = -1;
		this.tcp_ack = -1;
		this.tcp_pdu_size = -1;
		this.tcp_flags_ack = -1;
		this.tcp_flags_reset = -1;
		this.tcp_flags_syn = -1;
		this.tcp_flags_fin_tree = "";
		this.tcp_flags_fin = -1;
		this.tcp_connection_fin = "";
		this.apciObj = new ArrayList<APCI>();
	}
	/*
	 * This method provides an generic interface from which
	 * a caller can set value for a number of different fields.
	 */
	public void setFieldValue(String name, String value) {
		try {

			switch (name) {
				case "isMalformed":
					isMalformed = true;
					break;
				case "frame.time_epoch":
					frame_time_epoch = Double.parseDouble(value);
					break;
				case "frame.time_relative":
					frame_time_relative = Double.parseDouble(value);
					break;
				case "frame.number":
					frame_number = Integer.parseInt(value);
					break;
				case "frame.len":
					frame_len = Integer.parseInt(value);
					break;
				case "frame.coloring_rule.string":
					frame_coloringrule = value;
					break;
				case "eth.dst:":
					eth_dst = value;
					break;
				case "eth.dst_resolved":
					eth_dst_resolved = value;
					break;
				case "eth.src:":
					eth_src = value;
					break;
				case "eth.src_resolved":
					eth_src_resolved = value;
					break;
				case "ip.version":
					ip_version = Integer.parseInt(value);
					break;
				case "ip.len":
					ip_len = Integer.parseInt(value);
					break;
				case "ip.src":
					ip_src = value;
					break;
				case "ip.src_host":
					ip_src_host = value;
					break;
				case "ip.dst":
					ip_dst = value;
					break;
				case "ip.dst_host":
					ip_dst_host = value;
					break;
				case "tcp.srcport":
					tcp_src_port = Integer.parseInt(value);
					break;
				case "tcp.dstport":
					tcp_dst_port = Integer.parseInt(value);
					break;
				case "tcp.ack":
					tcp_ack = Long.parseLong(value);
					break;
				case "tcp.flags.ack":
					tcp_flags_ack = Integer.parseInt(value);
					break;
				case "tcp.flags.reset":
					tcp_flags_reset = Integer.parseInt(value);
					break;
				case "tcp.flags.syn":
					tcp_flags_syn = Integer.parseInt(value);
					break;
				case "tcp.flags.fin_tree":
					tcp_flags_fin_tree = value;
					break;
				case "tcp.flags.fin":
					tcp_flags_fin = Integer.parseInt(value);
					break;
				case "tcp.connection.fin":
					tcp_connection_fin = value;
					break;

				case "tcp.pdu.size":
					tcp_pdu_size = Integer.parseInt(value);
					break;
				default:
					break;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.out.println("fieldName: " + name + "value" + value);
		}
	}

	/*
	 * The following methods are setters and getters
	 */
	public void addApciObj(APCI a) {
		apciObj.add(a);
	}

	public ArrayList<APCI> getApciObj() {
		return apciObj;
	}

	public void setApciObj(ArrayList<APCI> apciObj) {
		this.apciObj = apciObj;
	}

	public boolean isMalformed() {
		return isMalformed;
	}

	public void setMalformed(boolean isMalformed) {
		this.isMalformed = isMalformed;
	}

	public String getEth_dst_resolved() {
		return eth_dst_resolved;
	}

	public void setEth_dst_resolved(String eth_dst_resolved) {
		this.eth_dst_resolved = eth_dst_resolved;
	}

	public String getEth_src_resolved() {
		return eth_src_resolved;
	}

	public void setEth_src_resolved(String eth_src_resolved) {
		this.eth_src_resolved = eth_src_resolved;
	}

	public double getFrame_time_epoch() {
		return frame_time_epoch;
	}

	public void setFrame_time_epoch(double frame_time_epoch) {
		this.frame_time_epoch = frame_time_epoch;
	}

	public double getFrame_time_relative() {
		return frame_time_relative;
	}

	public void setFrame_time_relative(double frame_time_relative) {
		this.frame_time_relative = frame_time_relative;
	}

	public int getFrame_number() {
		return frame_number;
	}

	public void setFrame_number(int frame_number) {
		this.frame_number = frame_number;
	}

	public int getFrame_len() {
		return frame_len;
	}

	public void setFrame_len(int frame_len) {
		this.frame_len = frame_len;
	}

	public String getEth_dst() {
		return eth_dst;
	}

	public void setEth_dst(String eth_dst) {
		this.eth_dst = eth_dst;
	}

	public String getEth_src() {
		return eth_src;
	}

	public void setEth_src(String eth_src) {
		this.eth_src = eth_src;
	}

	public int getTcp_src_port() {
		return tcp_src_port;
	}

	public void setTcp_src_port(int tcp_src_port) {
		this.tcp_src_port = tcp_src_port;
	}

	public int getTcp_dst_port() {
		return tcp_dst_port;
	}

	public void setTcp_dst_port(int tcp_dst_port) {
		this.tcp_dst_port = tcp_dst_port;
	}

	public long getTcp_ack() {
		return tcp_ack;
	}

	public void setTcp_ack(int tcp_ack) {
		this.tcp_ack = tcp_ack;
	}

	public void setTcp_flags_ack(int tcp_flags_ack) {
		this.tcp_flags_ack = tcp_flags_ack;
	}

	public int getTcp_flags_ack() {
		return tcp_flags_ack;
	}

	public void setTcp_flags_reset(int tcp_flags_reset) {
		this.tcp_flags_reset = tcp_flags_reset;
	}

	public int getTcp_flags_reset() {
		return tcp_flags_reset;
	}

	public void setTcp_flags_fin(int tcp_flags_fin) {
		this.tcp_flags_fin = tcp_flags_fin;
	}

	public int getTcp_flags_fin() {
		return tcp_flags_fin;
	}

	public void setTcp_flags_syn(int tcp_flags_syn) {
		this.tcp_flags_syn = tcp_flags_syn;
	}

	public int getTcp_flags_syn() {
		return tcp_flags_syn;
	}

	public int getIp_version() {
		return ip_version;
	}

	public void setIp_version(int ip_version) {
		this.ip_version = ip_version;
	}

	public int getIp_len() {
		return ip_len;
	}

	public void setIp_len(int ip_len) {
		this.ip_len = ip_len;
	}

	public String getIp_src() {
		return ip_src;
	}

	public void setIp_src(String ip_src) {
		this.ip_src = ip_src;
	}

	public String getIp_src_host() {
		return ip_src_host;
	}

	public void setIp_src_host(String ip_src_host) {
		this.ip_src_host = ip_src_host;
	}

	public String getIp_dst() {
		return ip_dst;
	}

	public void setIp_dst(String ip_dst) {
		this.ip_dst = ip_dst;
	}

	public String getIp_dst_host() {
		return ip_dst_host;
	}

	public void setIp_dst_host(String ip_dst_host) {
		this.ip_dst_host = ip_dst_host;
	}

	public int getTcp_pdu_size() {
		return tcp_pdu_size;
	}

	public void setTcp_pdu_size(int tcp_pdu_size) {
		this.tcp_pdu_size = tcp_pdu_size;
	}

	@Override
	public String toString() {

		String pkt = "";
		if (isMalformed) {
			pkt = "**Malformed ";
		}
		pkt += "\nPacket [isMalformed=" + isMalformed + ", frame.time_epoch=" + frame_time_epoch + ", frame.time_relative=" + frame_time_relative + ", frame.number="
				+ frame_number + ", frame.len=" + frame_len + ", eth.src=" + eth_src + ", eth.src_resolved="
				+ eth_src_resolved + ", eth.dst=" + eth_dst + ", eth.dst_resolved=" + eth_dst_resolved + ", ip.version="
				+ ip_version + ", ip.len=" + ip_len + ", ip.src=" + ip_src + ", ip.src_host=" + ip_src_host
				+ ", ip.dst=" + ip_dst + ", ip.dst_host=" + ip_dst_host + ", tcp.src_port=" + tcp_src_port
				+ ", tcp.dst_port=" + tcp_dst_port + ", tcp.flags.ack=" + tcp_flags_ack + ", tcp.flags.reset=" + tcp_flags_reset
				+ ", tcp.flags.syn=" + tcp_flags_syn + ", tcp.flags.fin=" + tcp_flags_fin + ", tcp.pdu.size=" + tcp_pdu_size + "]\n{";

		String apciStr = "";
		for (APCI a : apciObj) {
			apciStr += a.toString();
		}

		return pkt + apciStr;
	}

}
