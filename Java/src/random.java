/* @XI
 * This class is me doodling.
 */
public class random {
    public static void main(String[] args) {
        int[] nums = {1,2,3,4,5,6,7,8,9,10};

        for (int i : nums) {
            System.out.println(i);
            if (i % 3 == 0) {
                continue;
            }

            if (i % 5 == 0) {
                break;
            }

        }
    }

}
