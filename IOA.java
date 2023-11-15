import java.io.Serializable;

/*
 * This class represent a single IOA object 
 * which has all the relevant fields/attributes that belong to this particular IOA.
 */
public class IOA implements Serializable {

	private int ioa;
	private int asdu_ioa;
	private double float_value;
	private double vti_value;
	private double vti_transient;
	private double norm_value;
	// tidied
	private String siq;
	private SIQ siq_tree;
	private String sco;
	private SCO sco_tree;
	private String qos;
	private QOS qos_tree;

	private int qds_ov;
	private int qds_bl;
	private int qds_sb;
	private int qds_nt;
	private int qds_iv;
	private String cp56time;
	private int diq_dpi;
	private int bitstring;
	private int coi_r;
	private int coi_i;
	private int qoi;

	// Default constructor
	public IOA() {
		initialize();
	}

	public void initialize(){
		this.ioa = -1;
		asdu_ioa = -1;
		this.float_value = -1.0;
		this.vti_value = -1.0;
		this.vti_transient = -1.0;
		this.norm_value = -1.0;
		this.siq = "";
		this.siq_tree = new SIQ();
		this.sco = "";
		this.sco_tree = new SCO();
		this.qos = "";
		this.qos_tree = new QOS();

		this.qoi = -1;
		this.qds_ov = -1;
		this.qds_bl = -1;
		this.qds_sb = -1;
		this.qds_nt = -1;
		this.qds_iv = -1;
		this.cp56time = "";
		this.diq_dpi = -1;
		this.bitstring = -1;
		this.coi_r = -1;
		this.coi_i = -1;

	}

	/*
	 * Individual setter and getter for each data member are provided below.
	 * This methods simply provides a convenient interface (single point) 
	 * for all data members of this object
	 */
	public void setFieldName(String name, String value) {
		
		switch (name) {
			case "IOA": ioa = Integer.parseInt(value);
				break;
			case "iec60870_asdu.ioa": asdu_ioa = Integer.parseInt(value);
				break;
			case "iec60870_asdu.float":
				if (value.equals("-nan") || value.equals("nan") || value.equals("-1.#QNAN") || value.equals("1.#QNAN")) {
					float_value = -16777216.0;
				} else {
					float_value = Double.parseDouble(value);
				}
				break;
			case "iec60870_asdu.vti.v": vti_value = Double.parseDouble(value);
				break;
			case "iec60870_asdu.vti.t": vti_transient = Double.parseDouble(value);
				break;
			case "iec60870_asdu.normval": norm_value = Double.parseDouble(value);
				break;
			case "iec60870_asdu.qds.ov": qds_ov = Integer.parseInt(value);
				break;
			case "iec60870_asdu.qds.bl": qds_bl = Integer.parseInt(value);
				break;
			case "iec60870_asdu.qds.sb": qds_sb = Integer.parseInt(value);
				break;
			case "iec60870_asdu.qds.nt": qds_nt = Integer.parseInt(value);
				break;
			case "iec60870_asdu.qds.iv": qds_iv = Integer.parseInt(value);
				break;
			case "iec60870_asdu.cp56time": cp56time = value;
				break;
			case "iec60870_asdu.qoi": qoi = Integer.parseInt(value);
				break;
			case "iec60870_asdu.diq.dpi": diq_dpi = Integer.parseInt(value);
				break;
			case "iec60870_asdu.bitstring": bitstring = Integer.parseInt(value);
				break;
			case "iec60870_asdu.coi_r": coi_r = Integer.parseInt(value);		// cause of initialization
				break;
			case "iec60870_asdu.coi_i": coi_i = Integer.parseInt(value);
				break;
			case "iec60870_asdu.siq": siq = value;
				break;
			case "iec60870_asdu.sco": sco = value;
				break;
			case "iec60870_asdu.qos": qos = value;
				break;
			default: break;
		}
		
	}
	public int getIoa() {
		return ioa;
	}

	public void setIoa(int ioa) {
		this.ioa = ioa;
	}

	public int getCoi_i() {
		return coi_i;
	}

	public int getCoi_r() {
		return coi_r;
	}

	public void setCoi_i(int coi_i) {
		this.coi_i = coi_i;
	}

	public void setCoi_r(int coi_r) {
		this.coi_r = coi_r;
	}

	public int getAsdu_ioa() {
		return asdu_ioa;
	}

	public void setAsdu_ioa(int asdu_ioa) {
		this.asdu_ioa = asdu_ioa;
	}

	public double getFloat_value() {
		return float_value;
	}

	public void setFloat_value(double float_value) {
		this.float_value = float_value;
	}

	public double getVti_value() {
		return vti_value;
	}

	public void setVti_value(double vti_value) {
		this.vti_value = vti_value;
	}

	public double getNorm_value() {
		return norm_value;
	}

	public void setNorm_value(double norm_value) {
		this.norm_value = norm_value;
	}

	public int getQds_ov() {
		return qds_ov;
	}

	public void setQds_ov(int pds_ov) {
		this.qds_ov = pds_ov;
	}

	public int getQds_bl() {
		return qds_bl;
	}

	public void setQds_bl(int qds_bl) {
		this.qds_bl = qds_bl;
	}

	public int getQds_sb() {
		return qds_sb;
	}

	public void setQds_sb(int qds_sb) {
		this.qds_sb = qds_sb;
	}

	public int getQds_nt() {
		return qds_nt;
	}

	public void setQds_nt(int qds_nt) {
		this.qds_nt = qds_nt;
	}

	public int getQds_iv() {
		return qds_iv;
	}

	public void setQds_iv(int qds_iv) {
		this.qds_iv = qds_iv;
	}

	public String getCp56time() {
		return cp56time;
	}

	public void setCp56time(String cp56time) {
		this.cp56time = cp56time;
	}

	public int getQoi() {
		return qoi;
	}

	public void setQoi(int qoi) {
		this.qoi = qoi;
	}

	public void setDiq_dpi(int diq_dpi) {
		this.diq_dpi = diq_dpi;
	}

	public int getDiq_dpi() {
		return diq_dpi;
	}
/*
	public void setSiq_spi(int siq_spi) {
		this.siq_spi = siq_spi;
	}

	public int getSiq_spi() {
		return siq_spi;
	}
*/

	public void setSco(String sco) {
		this.sco = sco;
	}

	public String getSco() {
		return sco;
	}

	public void setSco_tree(SCO sco_tree) {
		this.sco_tree = sco_tree;
	}

	public SCO getSco_tree() {
		return sco_tree;
	}

	public void setSiq(String siq) {
		this.siq = siq;
	}

	public String getSiq() {
		return siq;
	}

	public void setSiq_tree(SIQ siq_tree) {
		this.siq_tree = siq_tree;
	}

	public SIQ getSiq_tree() {
		return siq_tree;
	}

	public void setQos(String qos) {
		this.qos = qos;
	}

	public String getQos() {
		return qos;
	}

	public void setQos_tree(QOS qos_tree) {
		this.qos_tree = qos_tree;
	}

	public QOS getQos_tree() {
		return qos_tree;
	}

	@Override
	public String toString() {
		return "\n\tIOA:{ioa=" + ioa + ", float_value=" + float_value + ", siq=" + siq + ", sco=" + sco + ", qos=" + qos + ", pds_ov=" + qds_ov + ", qds_bl=" + qds_bl
				+ ", qds_sb=" + qds_sb + ", qds_nt=" + qds_nt + ", qds_iv=" + qds_iv + ", cp56time=" + cp56time + "}";
	}
}

