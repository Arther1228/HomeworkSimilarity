package pers.hdq.util;

/**
 * @author yangliangchuang 2024-01-10 9:58
 */
public class ThresholdUtil {

    public static Double getSimThre(String threshold) {
        Double simThre;
        switch (threshold) {
            case "20%":
                simThre = 0.2;
                break;
            case "30%":
                simThre = 0.3;
                break;
            case "40%":
                simThre = 0.4;
                break;
            case "50%":
                simThre = 0.5;
                break;
            case "60%":
                simThre = 0.6;
                break;
            case "70%":
                simThre = 0.7;
                break;
            case "80%":
                simThre = 0.8;
                break;
            case "90%":
                simThre = 0.9;
                break;
            case "95%":
                simThre = 0.95;
                break;
            default:
                simThre = 0.90;
        }

        return simThre;

    }
}
