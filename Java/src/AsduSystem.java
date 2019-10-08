/* @XI
 * This class defines the "system" mentioned in IEC 101 Page 29.
 * Each "system" contains ASDU common address and IOA number.
 * Under each "system", protocol claimed the values should be unambiguous.
 */
import java.util.*;
public class AsduSystem {
    private int common_addr;
    private int ioa_num;
    private int asdu_type;
    private int causetx;

    public AsduSystem() {
        this.common_addr = -1;
        this.ioa_num = -1;
        this.asdu_type = -1;
        this.causetx = -1;

    }
    public AsduSystem(int common, int ioa) {
        this.common_addr = common;
        this.ioa_num = ioa;
    }

    public void setCommon_addr(int common){
        this.common_addr = common;
    }
    public int getCommon_addr() {
        return common_addr;
    }

    public void setIoa_num(int ioa) {
        this.ioa_num = ioa;
    }

    public int getIoa_num() {
        return ioa_num;
    }

    public void setAsdu_type(int asdu_type) {
        this.asdu_type = asdu_type;
    }

    public int getAsdu_type() {
        return asdu_type;
    }

    public void setCausetx(int causetx) {
        this.causetx = causetx;
    }

    public int getCausetx() {
        return causetx;
    }

    // compare two systems, return true if both fields are the same
    // otherwise, return false
    public boolean systemCompare(AsduSystem a1, AsduSystem a2) {
        if (a1.common_addr == a2.common_addr && a1.ioa_num == a2.ioa_num) {
            return true;
        } else return false;
    }

    @Override
    public String toString() {
        //String str = "";
        StringBuilder sb = new StringBuilder();
        sb.append(common_addr);
        sb.append("-");
        sb.append(Integer.toString((ioa_num)));
        //str = Integer.toString(common_addr) + "-" + Integer.toString(ioa_num);
        return sb.toString();
    }

    public String toString2() {
        //String str = "";
        StringBuilder sb = new StringBuilder();
        sb.append(asdu_type);
        sb.append("-");
        sb.append(causetx);
        sb.append(":");
        sb.append(common_addr);
        sb.append("-");
        sb.append(Integer.toString((ioa_num)));
        //str = Integer.toString(common_addr) + "-" + Integer.toString(ioa_num);
        return sb.toString();
    }
}
