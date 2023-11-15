import java.io.Serializable;

/*
 * This class represent a single SIQ object (TYPE 1, 30)
 * which has all the relevant fields/attributes that belong to this particular SIQ.
 */
public class SIQ implements Serializable {
    private int siq_spi;
    private int siq_bl;
    private int siq_sb;
    private int siq_nt;
    private int siq_iv;

    // Default constructor
    public SIQ() {
        initialize();
    }

    public void initialize(){
        this.siq_spi = -1;
        this.siq_bl = -1;
        this.siq_sb = -1;
        this.siq_nt = -1;
        this.siq_iv = -1;
    }

    /*
     * Individual setter and getter for each data member are provided below.
     * This methods simply provides a convenient interface (single point)
     * for all data members of this object
     */
    public void setFieldName(String name, String value) {

        switch (name) {
            case "iec60870_asdu.siq.spi": siq_spi = Integer.parseInt(value);
                break;
            case "iec60870_asdu.siq.bl": siq_bl = Integer.parseInt(value);
                break;
            case "iec60870_asdu.siq.sb": siq_sb = Integer.parseInt(value);
                break;
            case "iec60870_asdu.siq.nt": siq_nt = Integer.parseInt(value);
                break;
            case "iec60870_asdu.siq.iv": siq_iv = Integer.parseInt(value);
                break;
            default: break;
        }

    }

    public void setSiq_spi(int siq_spi) {
        this.siq_spi = siq_spi;
    }

    public void setSiq_bl(int siq_bl) {
        this.siq_bl = siq_bl;
    }

    public void setSiq_iv(int siq_iv) {
        this.siq_iv = siq_iv;
    }

    public void setSiq_nt(int siq_nt) {
        this.siq_nt = siq_nt;
    }

    public void setSiq_sb(int siq_sb) {
        this.siq_sb = siq_sb;
    }

    public int getSiq_spi() {
        return siq_spi;
    }

    public int getSiq_bl() {
        return siq_bl;
    }

    public int getSiq_iv() {
        return siq_iv;
    }

    public int getSiq_nt() {
        return siq_nt;
    }

    public int getSiq_sb() {
        return siq_sb;
    }

    @Override
    public String toString() {
        return "\n\tSIQ:{siq_spi=" + siq_spi + ", siq_bl=" + siq_bl
                + ", siq_sb=" + siq_sb + ", siq_nt=" + siq_nt + ", siq_iv=" + siq_iv + "}";
    }
}

