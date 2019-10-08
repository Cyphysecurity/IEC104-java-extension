/* @XI
 * This class defines an object records any type of counts and count rate in timing axis
 */
public class CntInTime {
    private double time;
    private int cnt;
    private double cntRate;

    public CntInTime(double T, int C, double R) {
        this.time = T;
        this.cnt = C;
        this.cntRate = R;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCntRate(double cntRate) {
        this.cntRate = cntRate;
    }

    public double getCntRate() {
        return cntRate;
    }
}
