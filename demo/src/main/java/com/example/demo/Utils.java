/**
 * Project Name:Android_Car_Example
 * File Name:Utils.java
 * Package Name:com.amap.api.car.example
 * Date:2015年4月7日下午3:43:05
 */

package com.example.demo;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * ClassName:Utils <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2015年4月7日 下午3:43:05 <br/>  
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
public class Utils {

    private static ArrayList<Marker> markers = new ArrayList<Marker>();
//	private static ArrayList<Marker> markers2 = new ArrayList<Marker>();

    /**
     * 添加模拟用户的点
     */
    public static void addEmulateData(AMap amap, LatLng center, String str) {
        if (markers.size() == 0) {
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_loaction_start);

            double latitudeDelt = (Math.random() - 0.5) * 0.1;
            double longtitudeDelt = (Math.random() - 0.5) * 0.1;
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.setFlat(true);
            markerOptions.anchor(0.5f, 0.5f);
            markerOptions.icon(bitmapDescriptor);
            markerOptions.position(new LatLng(center.latitude + latitudeDelt, center.longitude + longtitudeDelt));
            Marker marker = amap.addMarker(markerOptions);
            markers.add(marker);
        } else {
            for (Marker marker : markers) {
                double latitudeDelt = (Math.random() - 0.5) * 0.1;
                double longtitudeDelt = (Math.random() - 0.5) * 0.1;
                marker.setPosition(new LatLng(center.latitude + latitudeDelt, center.longitude + longtitudeDelt));
            }
        }
    }

    /**
     * 添加模拟用户的点
     */
//	public static void addEmulateData2(AMap amap,LatLng center){
//		if(markers2.size()==0){
//			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
//					.fromResource(R.drawable.icon_loaction_start);
//
//			double latitudeDelt = (Math.random()-0.5)*0.1;
//			double longtitudeDelt = (Math.random()-0.5)*0.1;
//			MarkerOptions markerOptions = new MarkerOptions();
//			markerOptions.setFlat(true);
//			markerOptions.anchor(0.5f, 0.5f);
//			markerOptions.icon(bitmapDescriptor);
//			markerOptions.position(new LatLng(center.latitude + latitudeDelt, center.longitude+longtitudeDelt));
//			Marker marker = amap.addMarker(markerOptions);
//			markers2.add(marker);
//		}
//		else{
//			for(Marker marker:markers2){
//				double latitudeDelt = (Math.random()-0.5)*0.1;
//				double longtitudeDelt = (Math.random()-0.5)*0.1;
//				marker.setPosition(new LatLng(center.latitude+latitudeDelt, center.longitude+longtitudeDelt));
//			}
//		}
//	}

    /**
     * 移除marker
     */
    public static void removeMarkers() {
        for (Marker marker : markers) {
            marker.remove();
            marker.destroy();
        }
        markers.clear();
    }

    /**
     * 异或操作
     *
     * @param datas
     * @return
     */
    public static byte getXor(byte[] datas) {
        byte temp = datas[0];
        for (int i = 1; i < datas.length; i++) {
            temp ^= datas[i];
        }
        return temp;
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
  
