/**
 * Project Name:Android_Car_Example
 * File Name:InputTipTask.java
 * Package Name:com.amap.api.car.example
 * Date:2015年4月7日上午10:42:41
 */

package com.example.demo;

import android.content.Context;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:InputTipTask <br/>
 * Function: 简单封装了Inputtips的搜索服务，将其余提示的adapter进行数据绑定
 * Date:     2015年4月7日 上午10:42:41 <br/>
 *
 * @author yiyi.qi
 * @see
 * @since JDK 1.6
 */
public class InputTipTask implements InputtipsListener {

    private static InputTipTask mInputTipTask;

    private Inputtips mInputTips;

    private RecomandAdapter mAdapter;

    public static InputTipTask getInstance(RecomandAdapter adapter) {
        if (mInputTipTask == null) {
            mInputTipTask = new InputTipTask();
        }
        //单例情况，多次进入DestinationActivity传进来的RecomandAdapter对象会不是同一个
        mInputTipTask.setRecommandAdapter(adapter);
        return mInputTipTask;
    }

    public void setRecommandAdapter(RecomandAdapter adapter) {
        mAdapter = adapter;
    }

    private InputTipTask() {

    }

    /*
    实现输入提示的步骤如下：
    1、继承 InputtipsListener 监听。
    2、构造 InputtipsQuery 对象，
    通过 InputtipsQuery(java.lang.String keyword, java.lang.String city) 设置搜索条件。
     */
    public void searchTips(Context context, String keyWord, String city) {
        //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
        InputtipsQuery query = new InputtipsQuery(keyWord, city);
        //构造 Inputtips 对象，并设置监听。
        mInputTips = new Inputtips(context, query);
        mInputTips.setInputtipsListener(this);
        //调用 PoiSearch 的 requestInputtipsAsyn() 方法发送请求。
        mInputTips.requestInputtipsAsyn();


    }

    /*
    通过回调接口 onGetInputtips 解析返回的结果，获取输入提示返回的信息。
     */
    @Override
    public void onGetInputtips(List<Tip> tips, int resultCode) {

        if (resultCode == AMapException.CODE_AMAP_SUCCESS && tips != null) {
            ArrayList<PositionEntity> positions = new ArrayList<PositionEntity>();
            for (Tip tip : tips) {

                if (tip.getPoint() != null) {

                    positions.add(new PositionEntity(tip.getPoint().getLatitude(), tip.getPoint().getLongitude(), tip.getName(), tip.getAdcode()));
                } else {
                    positions.add(new PositionEntity(0, 0, tip.getName(), tip.getAdcode()));
                }

            }
            mAdapter.setPositionEntities(positions);
            mAdapter.notifyDataSetChanged();
        }
        //TODO 可以根据app自身需求对查询错误情况进行相应的提示或者逻辑处理
    }

}
  
