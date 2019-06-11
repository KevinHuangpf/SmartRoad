/**
 * Project Name:Android_Car_Example
 * File Name:PoiSearchTask.java
 * Package Name:com.amap.api.car.example
 * Date:2015年4月7日上午11:25:07
 */

package com.example.demo;

import android.content.Context;
import android.util.Log;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:PoiSearchTask <br/>
 * Function: 简单封装了poi搜索的功能，搜索结果配合RecommendAdapter进行使用显示 <br/>
 * Date: 2015年4月7日 上午11:25:07 <br/>
 *
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
public class PoiSearchTask implements OnPoiSearchListener {

    private Context mContext;

    private RecomandAdapter mRecommandAdapter;

    public PoiSearchTask(Context context, RecomandAdapter recomandAdapter) {
        mContext = context;

        mRecommandAdapter = recomandAdapter;

    }

    public void search(String keyWord, String city) {
        Log.i("MY", "search");
        //2、构造 PoiSearch.Query 对象，
        // 通过 PoiSearch.Query(String query, String ctgr, String city) 设置搜索条件。
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        Query query = new PoiSearch.Query(keyWord, "", city);
        query.setPageSize(10); // 设置每页最多返回多少条poiitem
        query.setPageNum(0); //设置查询页码
        //构造 PoiSearch 对象，并设置监听。
        PoiSearch poiSearch = new PoiSearch(mContext, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();  //调用 PoiSearch 的 searchPOIAsyn() 方法发送请求。
    }

    /*
    通过回调接口 onPoiSearched 解析返回的结果，将查询到的 POI 以绘制点的方式显示在地图上。
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int resultCode) {
        if (resultCode == AMapException.CODE_AMAP_SUCCESS && poiResult != null) {
            ArrayList<PoiItem> pois = poiResult.getPois();
            if (pois == null) {
                return;
            }
            List<PositionEntity> entities = new ArrayList<PositionEntity>();
            for (PoiItem poiItem : pois) {
                PositionEntity entity = new PositionEntity(poiItem.getLatLonPoint().getLatitude(),
                        poiItem.getLatLonPoint().getLongitude(), poiItem.getTitle()
                        , poiItem.getCityName());
                entities.add(entity);
            }
            mRecommandAdapter.setPositionEntities(entities);
            mRecommandAdapter.notifyDataSetChanged();
        }
        //TODO 可以根据app自身需求对查询错误情况进行相应的提示或者逻辑处理
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
