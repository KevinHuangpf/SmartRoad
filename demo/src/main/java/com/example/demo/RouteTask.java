/**
 * Project Name:Android_Car_Example
 * File Name:RouteTask.java
 * Package Name:com.amap.api.car.example
 * Date:2015年4月3日下午2:38:10
 */

package com.example.demo;

import android.content.Context;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.FromAndTo;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:RouteTask <br/>
 * Function: 封装的驾车路径规划 <br/>
 * Date: 2015年4月3日 下午2:38:10 <br/>
 *
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
public class RouteTask implements OnRouteSearchListener {

    private static RouteTask mRouteTask;

    private RouteSearch mRouteSearch;

    private PositionEntity mFromPoint;

    private PositionEntity mToPoint;

    private List<OnRouteCalculateListener> mListeners = new ArrayList<OnRouteCalculateListener>();

    public interface OnRouteCalculateListener {
        void onRouteCalculate(float cost, float distance, int duration);

    }

    public static RouteTask getInstance(Context context) {
        if (mRouteTask == null) {
            mRouteTask = new RouteTask(context);
        }
        return mRouteTask;
    }

    public PositionEntity getStartPoint() {
        return mFromPoint;
    }

    public void setStartPoint(PositionEntity fromPoint) {
        mFromPoint = fromPoint;
    }

    public PositionEntity getEndPoint() {
        return mToPoint;
    }

    public void setEndPoint(PositionEntity toPoint) {
        mToPoint = toPoint;
    }

    private RouteTask(Context context) {
        mRouteSearch = new RouteSearch(context);  //初始化 RouteSearch 对象
        mRouteSearch.setRouteSearchListener(this);  //设置数据回调监听器
    }

    /*
    通过 DriveRouteQuery(RouteSearch.FromAndTo fromAndTo, int mode, List<LatLonPoint> passedByPoints,
     List<List<LatLonPoint>> avoidpolygons, String avoidRoad) 设置搜索条件，方法对应的参数说明如下：
    fromAndTo，路径的起点终点；
    mode，路径规划的策略，可选，默认为0-速度优先；详细策略请见驾车策略说明；
    passedByPoints，途经点，可选；
    avoidpolygons，避让区域，可选，支持32个避让区域，每个区域最多可有16个顶点。如果是四边形则有4个坐标点，如果是五边形则有5个坐标点。
    avoidRoad，避让道路，只支持一条避让道路，避让区域和避让道路同时设置，只有避让道路生效。
     */
    public void search() {
        if (mFromPoint == null || mToPoint == null) {
            return;
        }
        FromAndTo fromAndTo = new FromAndTo(new LatLonPoint(mFromPoint.latitue,
                mFromPoint.longitude), new LatLonPoint(mToPoint.latitue,
                mToPoint.longitude));
        DriveRouteQuery driveRouteQuery = new DriveRouteQuery(fromAndTo,
                RouteSearch.DrivingDefault, null, null, "");
        // 使用类 RouteSearch 的 calculateRideRouteAsyn(RideRouteQuery query) 方法进行骑行规划路径计算
        mRouteSearch.calculateDriveRouteAsyn(driveRouteQuery);
    }

    public void search(PositionEntity fromPoint, PositionEntity toPoint) {

        mFromPoint = fromPoint;
        mToPoint = toPoint;
        search();

    }

    public void addRouteCalculateListener(OnRouteCalculateListener listener) {
        synchronized (this) {
            if (mListeners.contains(listener))
                return;
            mListeners.add(listener);
        }
    }

    public void removeRouteCalculateListener(OnRouteCalculateListener listener) {
        synchronized (this) {
            mListeners.remove(listener);
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult arg0, int arg1) {

        // TODO Auto-generated method stub

    }

    /*
    在 RouteSearch.OnRouteSearchListener 接口回调方法
    void onDriveRouteSearched(DriveRouteResult result, int rCode) 处理驾车规划路径结果。
    返回的信息中包括：路线的距离、高速费用（仅针对7座以下轿车）、路况情况等等。
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult,
                                     int resultCode) {
        if (resultCode == AMapException.CODE_AMAP_SUCCESS && driveRouteResult != null) {
            synchronized (this) {
                for (OnRouteCalculateListener listener : mListeners) {
                    List<DrivePath> drivepaths = driveRouteResult.getPaths();
                    float distance = 0;
                    int duration = 0;
                    if (drivepaths.size() > 0) {
                        DrivePath drivepath = drivepaths.get(0);

                        distance = drivepath.getDistance() / 1000;

                        duration = (int) (drivepath.getDuration() / 60);
                    }

                    float cost = driveRouteResult.getTaxiCost();

                    listener.onRouteCalculate(cost, distance, duration);
                }
            }
        }
        // TODO 可以根据app自身需求对查询错误情况进行相应的提示或者逻辑处理
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {

        // TODO Auto-generated method stub

    }

    @Override
    public void onRideRouteSearched(RideRouteResult arg0, int arg1) {

        // TODO Auto-generated method stub
    }
}
