package me.hayden.privatemines.world;

public class PointUtil {
    public static int[] findPoint(int n) {
        if (n == 1) {
            return new int[]{0, 0};
        }
        int x = 0;
        int y = 0;
        int direction = 0;
        int counter = 1;
        int layer = 1;
        while (counter < n) {
            if (direction == 0) {
                x++;
                counter++;
                if (x == layer) {
                    direction = 1;
                }
            } else if (direction == 1) {
                y++;
                counter++;
                if (y == layer) {
                    direction = 2;
                }
            } else if (direction == 2) {
                x--;
                counter++;
                if (x == -layer) {
                    direction = 3;
                }
            } else {
                y--;
                counter++;
                if (y == -layer) {
                    direction = 0;
                    layer++;
                }
            }
            if (counter == n) {
                break;
            }
        }
        return new int[]{x, y};
    }

}
