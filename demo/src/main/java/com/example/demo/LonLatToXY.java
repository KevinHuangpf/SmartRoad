package com.example.demo;

/**
 * LonLatToXY class
 * 将经纬度转换为对应的坐标
 *
 * @author keriezhang
 * @date 2019/04/17
 */

public class LonLatToXY {
    //右下角点
    //经纬度
    private static final double RIGHTBOTTOM_LON = dmsToD("108°50′22.57″");
    private static final double RIGHTBOTTOM_LAT = dmsToD("34°7′29.58″");
    //像素坐标
    private static final double RIGHTBOTTOM_PX = 1030.1805;
    private static final double RIGHTBOTTOM_PY = 1061.0717;
/*    //简图
    private static final double RIGHTBOTTOM_PX = 25.90;setX().setY(1062.0717)
    private static final double RIGHTBOTTOM_PY = 38.70;*/


    //左上角点
    //经纬度
    private static final double LEFTTTOP_LON = dmsToD("108°50′20.84″");
    private static final double LEFTTTOP_LAT = dmsToD("34°7′32.81″");
    //像素坐标
//    private static final double LEFTTTOP_PX = 30.33;
//    private static final double LEFTTTOP_PY = 400.22;
//    //像素坐标
    private static final double LEFTTTOP_PX = 658.36536;
    private static final double LEFTTTOP_PY = 296.7044;
/*    //简图
    private static final double LEFTTTOP_PX = 16.42;
    private static final double LEFTTTOP_PY = 32.78;*/


//测试代码
/*    public static void main(String[] args) {

        LonLatToXY lonLatToXY = new LonLatToXY();
        //X = 35.24 Y=45.19
        double lon = DMSToDoule("108°50′21.61″");
        double lat = DMSToDoule("34°7′29.49″");

        double x = lonLatToXY.lonToX(lon);
        double y = lonLatToXY.latToY(lat);
        System.out.println(x + "    " + y);


        //X = 35.24 Y=45.19
        double lon1 = DMSToDoule("108°50′22.56″");
        double lat1 = DMSToDoule(" 34° 7′29.52″");

        double x1 = lonLatToXY.lonToX(lon1);
        double y1 = lonLatToXY.latToY(lat1);
        System.out.println(x1 + "    " + y1);


    }*/

    /**
     * 经度转横坐标
     *
     * @param longitude
     * @return
     */
    public double lonToX(double longitude) {
        double deltaLon = RIGHTBOTTOM_LON - LEFTTTOP_LON;
        double deltaPixel = RIGHTBOTTOM_PX - LEFTTTOP_PX;
        double k = deltaPixel / deltaLon;
        double pixelX = LEFTTTOP_PX + k * (longitude - LEFTTTOP_LON);
        return pixelX;
    }

    /**
     * 纬度转纵坐标
     *
     * @param latitude
     * @return
     */
    public double latToY(double latitude) {
        double deltaLat = LEFTTTOP_LAT - RIGHTBOTTOM_LAT;
        double deltaPixel = LEFTTTOP_PY - RIGHTBOTTOM_PY;
        double k = deltaPixel / deltaLat;
        double pixelY = LEFTTTOP_PY + k * (latitude - LEFTTTOP_LAT);
        return pixelY;

    }

    /**
     * 度分秒转小数
     *
     * @param latlng
     * @return
     */
    public static double dmsToD(String latlng) {

        double degree = Double.parseDouble(latlng.substring(0, latlng.indexOf("°")));
        double minute = Double.parseDouble(latlng.substring(latlng.indexOf("°") + 1, latlng.indexOf("′")));
        double second = Double.parseDouble(latlng.substring(latlng.indexOf("′") + 1, latlng.indexOf("″")));
        if (degree < 0) {
            return -(Math.abs(degree) + (minute + (second / 60)) / 60);
        }
        return degree + (minute + (second / 60)) / 60;
    }

    /**
     * dToDms
     *
     * @param data
     * @return
     */
    public static String dToDms(double data) {
        String ret_s = "";
        int tmp_i_du = (int) data;
        ret_s = String.valueOf(tmp_i_du) + "°";
        //度小数部分
        double tmp_d_du = data - tmp_i_du;
        int tmp_i_fen = (int) (tmp_d_du * 60);
        ret_s = ret_s.concat(String.valueOf(tmp_i_fen) + "′");
        double tmp_d_fen = tmp_d_du * 60 - tmp_i_fen;
        int tmp_i_miao = (int) (tmp_d_fen * 60);
        ret_s = ret_s.concat(String.valueOf(tmp_i_miao) + "″");
        return ret_s;
    }

}
