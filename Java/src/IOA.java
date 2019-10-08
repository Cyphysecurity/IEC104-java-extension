/*
 * This class represent a single IOA object 
 * which has all the relevant fields/attributes that eblong to this particular IOA.
 */
public class IOA {

	private int ioa;
	private int asdu_ioa;
	private double float_value;
	private double vti_value;
	private double vti_transient;
	private double norm_value;

	private int qds_ov;
	private int qds_bl;
	private int qds_sb;
	private int qds_nt;
	private int qds_iv;
	private String cp56time;
	// @XI
	// private String qos;
	private int qos_ql;
	private int qos_se;
	private int qoi;
	private int siq_spi;
	private int siq_bl;
	private int siq_sb;
	private int siq_nt;
	private int siq_iv;
	private int diq_dpi;
	private int bitstring;
	private int coi_r;
	private int coi_i;

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
		this.qds_ov = -1;
		this.qds_bl = -1;
		this.qds_sb = -1;
		this.qds_nt = -1;
		this.qds_iv = -1;
		this.cp56time = "";
		this.qos_ql = -1;
		this.qos_se = -1;
		this.qoi = -1;
		this.siq_spi = -1;
		this.siq_bl = -1;
		this.siq_sb = -1;
		this.siq_nt = -1;
		this.siq_iv = -1;
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
			case "104asdu.ioa": asdu_ioa = Integer.parseInt(value);
				break;
			case "104asdu.float":
				if (value.equals("-nan") || value.equals("nan") || value.equals("-1.#QNAN") || value.equals("1.#QNAN")) {
					float_value = -16777216.0;
				} else {
					float_value = Double.parseDouble(value);
				}
				break;
			case "104asdu.vti.v": vti_value = Double.parseDouble(value);
				break;
			case "104asdu.vti.t": vti_transient = Double.parseDouble(value);
				break;
			case "104asdu.normval": norm_value = Double.parseDouble(value);
				break;
			case "104asdu.qds.ov": qds_ov = Integer.parseInt(value);
				break;
			case "104asdu.qds.bl": qds_bl = Integer.parseInt(value);
				break;
			case "104asdu.qds.sb": qds_sb = Integer.parseInt(value);
				break;
			case "104asdu.qds.nt": qds_nt = Integer.parseInt(value);
				break;
			case "104asdu.qds.iv": qds_iv = Integer.parseInt(value);
				break;
			case "104asdu.cp56time": cp56time = value;
				break;
			case "104asdu.qos.ql": qos_ql = Integer.parseInt(value);
				break;
			case "104asdu.qos.se": qos_se = Integer.parseInt(value);
				break;
			case "104asdu.qoi": qoi = Integer.parseInt(value);
				break;
			case "104asdu.siq.spi": siq_spi = Integer.parseInt(value);
				break;
			case "104asdu.siq.bl": siq_bl = Integer.parseInt(value);
				break;
			case "104asdu.siq.sb": siq_sb = Integer.parseInt(value);
				break;
			case "104asdu.siq.nt": siq_nt = Integer.parseInt(value);
				break;
			case "104asdu.siq.iv": siq_iv = Integer.parseInt(value);
				break;
			case "104asdu.diq.dpi": diq_dpi = Integer.parseInt(value);
				break;
			case "104asdu.bitstring": bitstring = Integer.parseInt(value);
				break;
			case "104asdu.coi_r": coi_r = Integer.parseInt(value);		// cause of initialization
				break;
			case "104asdu.coi_i": coi_i = Integer.parseInt(value);
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

	public int getQos_ql() {
		return qos_ql;
	}

	public void setQos_ql(int qos_ql) {
		this.qos_ql = qos_ql;
	}

	public int getQos_se() {
		return qos_se;
	}

	public void setQos_se(int qos_se) {
		this.qos_se = qos_se;
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

	public void setSiq_spi(int siq_spi) {
		this.siq_spi = siq_spi;
	}

	public int getSiq_spi() {
		return siq_spi;
	}

	@Override
	public String toString() {
		return "\n\tIOA:{ioa=" + ioa + ", float_value=" + float_value + ", pds_ov=" + qds_ov + ", qds_bl=" + qds_bl
				+ ", qds_sb=" + qds_sb + ", qds_nt=" + qds_nt + ", qds_iv=" + qds_iv + ", cp56time=" + cp56time + "}";
	}
}

