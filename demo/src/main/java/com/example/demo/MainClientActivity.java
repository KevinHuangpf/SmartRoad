package com.example.demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class MainClientActivity extends BlunoLibrary implements AMap.OnCameraChangeListener,
        AMap.OnMapLoadedListener, OnLocationGetListener, View.OnClickListener,
        RouteTask.OnRouteCalculateListener, View.OnTouchListener {

    private Handler myHandler = null;
    private TextView carSpeed;
    private MapView mMapView;// //创建一个地图容器Mapiew对象
    private AMap mAmap;  //AMap 显示地图
    private TextView mAddressTextView; //显示我的位置的文本控件

    private Socket mSocket;
    private boolean isReceivingMsgReady;
    protected BufferedWriter mWriter;//用于推送消息
    protected BufferedReader mReader;//用于接收消息
    private Button order;
    private boolean isConnected = false;
    private TextView mPeopleWarning;
    private ImageView warningP;
    //地图 SDK 提供的点标记功能包含两大部分，一部分是点（俗称 Marker）、另一部分是浮于点上方的信息窗体（俗称 InfoWindow）。
    // 同时，SDK 对 Marker 和 InfoWindow 封装了大量的触发事件，例如点击事件、长按事件、拖拽事件。
    private Marker mPositionMark; //定位点
    private LatLng mStartPosition;  //经纬度信息
    private LatLng mPeoplePosition;  //经纬度信息
    private String mStartAddress;
    private RegeocodeTask mRegeocodeTask;
    private LinearLayout mDestinationContainer;
    private TextView mRouteCostText;  //路径预估费用
    private TextView mDesitinationText; //显示目的地的文本控件
    private TextView mPeopleText; //显示目的地的文本控件
    private LocationTask mLocationTask;
    private ImageView mLocationImage;
    private LinearLayout mFromToContainer;
    private boolean mIsFirst = true;
    private boolean mIsRouteSuccess = false;
    //标点变量
    private TagLayout mTagLayout1;
    //右
//    private double lon = 108.83960277777778;
//    private double lat = 34.12488333333334;

    //test左
//    private double lon = 108.83912222222222;
//    private double lat = 34.12578055555556;

    private double lon = 108.83829722222222;
    private double lat = 34.126105555555554;
    private Drawable pIcon;
    private TagLayout.Tag mtag;
    //蓝牙配置
    private Button buttonScan;
    private long lastReceiveTime = System.currentTimeMillis();
    private boolean flag1 = false;
    //提取像素点
    private TextView tvTouchShowStart;
    private TextView tvTouchShow;
    private LinearLayout llTouch;

    public interface OnGetLocationListener {
        void getLocation(String locationAddress);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onCreateProcess();
        serialBegin(115200);
        init(savedInstanceState);
        mLocationTask = LocationTask.getInstance(getApplicationContext());
        mLocationTask.setOnLocationGetListener(this);
        mRegeocodeTask = new RegeocodeTask(getApplicationContext());
        RouteTask.getInstance(getApplicationContext())
                .addRouteCalculateListener(this);

        initPosition();
//        initSocket();
        inits();
        //接受客户端的地址信息
        myHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        String msgWrite = (String) msg.obj;
//                        mPeopleWarning.setText("我："+ msgWrite+"\n");
//                        warningP.setColorFilter(Color.RED);
                        break;
                    case 2:
                        String message = (String) msg.obj;
                        if (message.contains("用户名")) {
                            isConnected = true;
                            order.setText("服务器连接," + message);
                        } else {
                            JSONObject json;
                            try {
                                json = new JSONObject(message);
//                                System.out.println(json.getString("msg")+"接受显示的韦静度");
//                                mPeopleText.setText(json.getString("msg"));
                                String[] str = json.getString("msg").split(",");
//                                String toid = json.getString("to")
//                    System.out.println(str.length);
                                if (str != null) {
                                    if (json.getString("to").equals("800")) {
                                        if (str.length == 3) {
                                            mPeoplePosition = new LatLng(Double.parseDouble(str[0]), Double.parseDouble(str[1]));
//                                        System.out.println( mPeoplePosition +"经纬度");
//                                            Utils.addEmulateData(mAmap, mPeoplePosition, str[2]);
//                                            mPeopleText.setText(json.getString("msg"));
//                                            System.out.println(mPeoplePosition + "经纬度");
                                            break;
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (!isConnected) {
                            Toast.makeText(MainClientActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
    }

    /**
     * 提取像素点的
     */
    private void inits() {
        tvTouchShowStart = findViewById(R.id.touch_show_start);
        tvTouchShow = findViewById(R.id.touch_show);
        llTouch = findViewById(R.id.ll_touch);
        llTouch.setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            /**
             * 点击的开始位置
             */
            case MotionEvent.ACTION_DOWN:
                tvTouchShowStart.setText("起始位置：(" + event.getX() + "," + event.getY());
                break;
            /**
             * 触屏实时位置
             */
            case MotionEvent.ACTION_MOVE:
                tvTouchShow.setText("实时位置：(" + event.getX() + "," + event.getY());
                break;
            /**
             * 离开屏幕的位置
             */
            case MotionEvent.ACTION_UP:
                tvTouchShow.setText("结束位置：(" + event.getX() + "," + event.getY());
                break;
            default:
                break;
        }
        /**
         *  注意返回值
         *  true：view继续响应Touch操作；
         *  false：view不再响应Touch操作，故此处若为false，只能显示起始位置，不能显示实时位置和结束位置
         */
        return true;
    }


    private void initSocket() {
        //在子线程中初始化Socket对象
        //新建一个线程，用于初始化socket和检测是否有接收到的新的信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!isReceivingMsgReady) {
                    try {
                        isReceivingMsgReady = true;
                        //在子线程中初始化Socket对象
                        mSocket = new Socket("103.46.128.41", 57848);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                    sendMsg();
                receiveMsg();
            }
        }).start();

    }

    private void receiveMsg() {
        //新建一个线程，用于检测是否有接收到的新的信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), StandardCharsets.UTF_8));
                    while (isReceivingMsgReady) {
                        if (mReader.ready()) {
                            /*读取一行字符串，读取的内容来自于客户机
							reader.readLine()方法是一个阻塞方法，
							从调用这个方法开始，该线程会一直处于阻塞状态，
							直到接收到新的消息，代码才会往下走*/
                            //handler发送消息，在handleMessage()方法中接收
                            Message msg = myHandler.obtainMessage();
                            msg.what = 2;
//                            msgLocal.obj = msg + " （客户端发送）" ;
                            msg.obj = mReader.readLine();
//                            System.out.println(msg.obj.toString()+"接收的数据");
                            myHandler.sendMessage(msg);
                        }
                        sendMsg();
                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    try {
//                        mSocket = new Socket(ip,port);
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
                }
            }
        }).start();
    }

    //向客户端发送信息
    public void sendMsg() {
        //新建一个线程，用于写出新的信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), StandardCharsets.UTF_8));
                    while (isReceivingMsgReady) {
                        //发送给客户端
                        if (mStartPosition != null) {
                            StringBuffer buffer = new StringBuffer();
                            buffer.append(mStartPosition.latitude);
                            System.out.println("" + mStartPosition.latitude);
                            buffer.append(",");
                            buffer.append(mStartPosition.longitude);
                            buffer.append(",");
                            buffer.append("D");
                            String msgWrite = buffer.toString();

//                        System.out.println(msgWrite+"发送的数据");
//                        封装成json
                            JSONObject json = new JSONObject();
                            json.put("to", Integer.parseInt("10000"));
                            json.put("msg", msgWrite);
                            mWriter.write(json.toString() + "\n");
                            mWriter.flush();
                            //通过handler显示车发送的信息
                            Message msg = myHandler.obtainMessage();
                            msg.what = 1;
                            msg.obj = msgWrite;
                            myHandler.sendMessage(msg);
                            Thread.sleep(2000);
                        }
                    }
                } catch (Exception e) {
//                    try {
//                        mSocket = new Socket(ip,port);
//                    } catch (IOException e1) {
                    e.printStackTrace();
//                    }
                }
            }
        }).start();
    }

    /**
     * 图片标点方法代码
     */
    private void initPosition() {
        //在子线程中初始化Socket对象
        //新建一个线程，用于初始化socket和检测是否有接收到的新的信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
//                    mTagLayout1.newTag().setX(new LonLatToXY().lonToX(lon)).setY(new LonLatToXY().lonToX(lat));
                        sendPos();
//                        System.out.println("error 1");
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void sendPos() {
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                mTag();
                System.out.println("获取标记位置");
//                System.out.println("X坐标 "+ new LonLatToXY().lonToX(lon)+ " Y坐标"+ new LonLatToXY().latToY(lat));
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                mTagLayout1.cleanAllTag();
//                System.out.println("删除");
                mTagLayout1.addTag(mtag);
                System.out.println("添加标记");

//                mAddressTextView.setText("经度: " + "E " + "108°50′19.227″" + "\r\n" + "纬度: " +" N " + "34°7′33.168″");
            }
        }.execute();
    }

    public void mTag() {
        mtag = mTagLayout1.newTag().setmIcon(pIcon).setX(new LonLatToXY().lonToX(lon)).setY(new com.example.demo.LonLatToXY().latToY(lat));
//        mtag = mTagLayout1.newTag().setmIcon(pIcon).setX(1030.1805).setY(1061.0717);
//        mtag = mTagLayout1.newTag().setmIcon(pIcon).setX(658.36536).setY(296.7044);
//        mtag = mTagLayout1.newTag().setmIcon(pIcon).setX(-5.7832985858251624E7).setY(-6.34493064637355E7);

    }

    private void init(Bundle savedInstanceState) {

        mPeopleWarning = findViewById(R.id.peoplewarning_text);
        carSpeed = findViewById(R.id.speed_text);
        order = findViewById(R.id.textView_order);
        mAddressTextView = findViewById(R.id.address_text);
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mAmap = mMapView.getMap();
//        mAmap.moveCamera(CameraUpdateFactory.zoomTo(18));
        mAmap.getUiSettings().setZoomControlsEnabled(false);
        mAmap.setOnMapLoadedListener(this);
        mAmap.setOnCameraChangeListener(this);

        mDestinationContainer = findViewById(R.id.destination_container);
        mRouteCostText = findViewById(R.id.routecost_text);
        mDesitinationText = findViewById(R.id.destination_text);
        mDesitinationText.setOnClickListener(this);
        mPeopleText = findViewById(R.id.people_text);

        mLocationImage = findViewById(R.id.location_image);
        mLocationImage.setOnClickListener(this);
        mFromToContainer = findViewById(R.id.fromto_container);

        //标点
        mTagLayout1 = findViewById(R.id.imagemap);
        pIcon = getResources().getDrawable(R.drawable.icon_position1);
        warningP = findViewById(R.id.warn);
        //蓝牙
        buttonScan = findViewById(R.id.buttonScan);                    //initial the button for scanning the BLE device
        buttonScan.setOnClickListener(this);

    }

    private void hideView() {
        mFromToContainer.setVisibility(View.GONE);
    }

    private void showView() {
        mFromToContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCameraChange(CameraPosition arg0) {
//        hideView();
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        showView();
        mStartPosition = cameraPosition.target;
//        System.out.println("开始位置的经纬度"+mStartPosition);
        mRegeocodeTask.setOnLocationGetListener(this);
        mRegeocodeTask
                .search(mStartPosition.latitude, mStartPosition.longitude);
        if (mIsFirst) {
//            Utils.addEmulateData(mAmap, mStartPosition,"D");
            if (mPositionMark != null) {
                mPositionMark.setToTop();
            }
            mIsFirst = false;
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);                    //onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        onPauseProcess();
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();                                                        //onStop Process by BlunoLibrary
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        com.example.demo.Utils.removeMarkers();
        mMapView.onDestroy();
        mLocationTask.onDestroy();
        RouteTask.getInstance(getApplicationContext()).removeRouteCalculateListener(this);
        onDestroyProcess();
    }

    /*
      绘制根据实际的业务需求，在地图指定的位置上添加自定义的 Marker。
      MarkerOptions 是设置 Marker 参数变量的类，自定义 Marker 时会经常用到。
     */
    @Override
    public void onMapLoaded() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.setFlat(true); //设置marker平贴地图效果
        markerOptions.anchor(0.5f, 0.5f); //点标记的锚点
        markerOptions.position(new LatLng(0, 0)); //在地图上标记位置的经纬度值。必填参数
        markerOptions
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),
                                R.drawable.icon_car)));  //点图标
        mPositionMark = mAmap.addMarker(markerOptions);

        mPositionMark.setPositionByPixels(mMapView.getWidth() / 2,
                mMapView.getHeight() / 2); //位置聚焦到地图中间
//        mLocationTask.startSingleLocate();//单次定位
        mLocationTask.startLocate();//多次定位
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_image:
//                mLocationTask.startSingleLocate();
//                mLocationTask.startSingleLocate();//单
                mLocationTask.startLocate();//多次次定位
                break;
            case R.id.destination_text:
                Intent destinationIntent = new Intent(this,
                        DestinationActivity.class);
                startActivity(destinationIntent);
                break;
            case R.id.buttonScan:
                buttonScanOnClickProcess();
                break;
        }
    }

    @Override
    public void onLocationGet(PositionEntity entity) {
        // todo 这里在网络定位时可以减少一个逆地理编码
//        mAddressTextView.setText(entity.address);
        RouteTask.getInstance(getApplicationContext()).setStartPoint(entity);
        mStartPosition = new LatLng(entity.latitue, entity.longitude);
//        System.out.println("地图的经纬度"+mStartPosition);
        mStartAddress = entity.address;

        //如果想改变地图中心点，可以通过如下方法构造改变地图中心点的 CameraUpdate 参数：
        CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
                mStartPosition, mAmap.getCameraPosition().zoom);
        mAmap.animateCamera(cameraUpate);
    }

    @Override
    public void onRegecodeGet(PositionEntity entity) {
//        mAddressTextView.setText(entity.address);
//        System.out.println("调用地图");
        entity.latitue = mStartPosition.latitude;
        entity.longitude = mStartPosition.longitude;
        mStartAddress = entity.address;
//        carSpeed.setText("" + entity.speed + "米/秒");
        RouteTask.getInstance(getApplicationContext()).setStartPoint(entity);
        RouteTask.getInstance(getApplicationContext()).search();
    }

    @Override
    public void onRouteCalculate(float cost, float distance, int duration) {
        mDestinationContainer.setVisibility(View.VISIBLE);
        mIsRouteSuccess = true;
        mRouteCostText.setVisibility(View.VISIBLE);
        mDesitinationText.setText(RouteTask
                .getInstance(getApplicationContext()).getEndPoint().address);
        mRouteCostText.setText(String.format("预估费用%.2f元，距离%.1fkm,用时%d分", cost,
                distance, duration));
    }

    /**
     * 蓝牙部分代码
     *
     * @param theConnectionState
     */
    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {

        switch (theConnectionState) {                                            //Four connection state
            case isConnected:
                buttonScan.setText("Connected");
                break;
            case isConnecting:
                buttonScan.setText("Connecting");
                break;
            case isToScan:
                buttonScan.setText("Scan");
                break;
            case isScanning:
                buttonScan.setText("Scanning");
                break;
            case isDisconnecting:
                buttonScan.setText("isDisconnecting");
                break;
            default:
                break;
        }
    }


    //    byte[] MSG = new byte[100];
    ArrayList<Byte> MSG = new ArrayList<>();
    boolean isMSGOK = false;

    char Preamble;
    double tempLon;
    double tempLat;


    /**
     * 接收消息处理
     *
     * @param bytes
     */
    @Override
    public void onSerialReceived(byte[] bytes) {                            //Once connection data received, this function will be called

        String Name = "";
        if (bytes == null) {
            return;
        }
        //  0-判断消息头,#开头接收开始并重置缓存
        Preamble = (char) bytes[0];
        if (Preamble == '#') {
            MSG.clear();
            System.out.println("Stack Clear");
            flag1 = true;
        }
        if (true) {
            //  i-将多段数据进行拼接
            for (int i = 0; i < bytes.length; i++) {
                MSG.add(bytes[i]);
            }

            //检测到消息末尾，一次消息接收成功，标志至1，进行处理
            if (MSG.size() >= 2) {
                if (MSG.get(MSG.size() - 2) == 13 && MSG.get(MSG.size() - 1) == 10) {
                    isMSGOK = true;
                }
            }

            if (isMSGOK && MSG.size() >= 8) {
                //消息转换为byte[]
                byte[] bytesMSG = new byte[MSG.size()];
                for (int i = 0; i < bytesMSG.length; i++) {
                    bytesMSG[i] = MSG.get(i);
                }

                String StringMSG = new String(bytesMSG);
                System.out.println("MSG: " + StringMSG);
                String[] StringMSG1 = StringMSG.split("\\*");

                // 校验
                int localXor = 999999;
                int remoteXor = 1;
                if (StringMSG1.length == 3) {
                    localXor = com.example.demo.Utils.getXor(StringMSG1[0].getBytes()) ^ '*' ^ com.example.demo.Utils.getXor(StringMSG1[1].getBytes()) ^ '*';
                    System.out.println("校验字符" + StringMSG1[2]);
                    if (StringMSG1[2].length() == 5 || StringMSG1[2].length() == 4) {
                        String str = StringMSG1[2].replaceAll("\r\n", "");
                        System.out.println("str - " + str);
                        String regex = "^[A-Fa-f0-9]+$";
                        if (str.length() == 2 || str.length() == 3) {
                            if (str.matches(regex)) {
                                remoteXor = Integer.valueOf(str, 16);
                            }
                        }

                    }
                }

                System.out.println("remoteXor- " + remoteXor + "  loaclXor- " + localXor);
                //System.out.println("remoteXor- " +Integer.parseInt(String.valueOf(remoteXor)+" or "+ remoteXor + "  loaclXor- " + localXor));
                // 校验数据
                boolean checkXor;
                if (localXor == remoteXor || localXor == Integer.parseInt(String.valueOf(remoteXor), 16)) {
                    System.out.println("校验成功");
                    checkXor = true;
                } else {
                    System.out.println("校验失败");
                    Toast.makeText(MainClientActivity.this, "VERIFICATION FAILED!", Toast.LENGTH_LONG).show();
                    checkXor = false;
                }

                // 2 - 判断消息名称
                if (StringMSG1.length >= 1 && StringMSG1[0].length() == 4) {
                    Name = StringMSG1[0].substring(1, 4);
                }

                //2.1- 定位消息处理
                precisePositioning(Name, checkXor, StringMSG1);

                //2.2- 行人检测消息
                pedestrianDetection(Name, checkXor, StringMSG1);

                System.out.println("<信息结束>");
                isMSGOK = false;
            }
            flag1 = false;
        }
    }

    /**
     * 精确定位
     *
     * @param Name
     * @param checkXor
     * @param StringMSG1
     */
    private void precisePositioning(String Name, boolean checkXor, String[] StringMSG1) {
        if (checkXor && Name.equals("POS")) {

            //按“,”分割内容
            String[] Payload = StringMSG1[1].split(",");

            //时间戳
            double timeMSG = Double.parseDouble(Payload[0]);
            //定位有效性标志：0=定位无效  1=定位有效
            int flag = Integer.parseInt(Payload[1]);

            // 纬度信息，度分格式，和NMEA0183的格式相同（ddmm.mmmmm），精确到小数点后5位
            double latitudetemp = Double.parseDouble(Payload[2]);
            int pointFrontLat = (int) latitudetemp / 100;
            double mmLat = (latitudetemp - pointFrontLat * 100) / 60;
            double latitude = pointFrontLat + mmLat;

            //纬度的指示，‘N’表示北纬，’S’表示南纬
            String indexlatitude = Payload[3];

            //经度信息，度分格式，和NMEA0183的格式相同（dddmm.mmmmm），精确到小数点后5位
            double longitudetemp = Double.parseDouble(Payload[4]);
            int pointFrontLon = (int) longitudetemp / 100;
            double mmLon = (longitudetemp - pointFrontLon * 100) / 60;
            double longitude = pointFrontLon + mmLon;

            //经度的指示，‘E’表示东经，’W’表示西经
            String indexlongitude = Payload[5];

            //海拔高度，单位为：m
            double altitude = Double.parseDouble(Payload[6]);

            //方位角，单位：度
            double azAngle = Double.parseDouble(Payload[7]);

            // 显示操作,1为有效，0为无效
            if (flag == 1) {
                double Abslon = Math.abs(longitude - tempLon);
                double Abslat = Math.abs(latitude - tempLat);
                if (Abslat > 0.000001 || Abslon > 0.000001) {
                    carSpeed.setText("Moving");
                    carSpeed.setTextColor(Color.RED);
                } else {
                    carSpeed.setText("Stop");
                    carSpeed.setTextColor(Color.RED);
                }

                // TODO: 2019/4/16
                //定位数据
                lon = longitude;//经度
                lat = latitude;//纬度
                String StringLon = new DecimalFormat("#.00000000").format(longitude);
                String StringLat = new DecimalFormat("#.00000000").format(latitude);

                String lonS = com.example.demo.Utils.dToDms(lon);
                String latS = com.example.demo.Utils.dToDms(lat);
//                mAddressTextView.setText("经度: " + indexlongitude + " " + lonS + "\r\n" + "纬度: " + indexlatitude + "  " + latS);
                mAddressTextView.setText("Longitude: " + StringLon + " " + indexlongitude + "\r\n" + "Latitude: " + StringLat + " " + indexlatitude);
                tempLon = longitude;
                tempLat = latitude;
                System.out.println("定位数据接收");
            }
        }
    }

    /**
     * 行人检测
     *
     * @param Name
     * @param checkXor
     * @param StringMSG1
     */
    private void pedestrianDetection(String Name, boolean checkXor, String[] StringMSG1) {

        if (checkXor && Name.equals("PDT")) {
            //按“,”分割内容
            String[] Payload = StringMSG1[1].split(",");

            //行人有效性标志：0=定位无效  1=定位有效
            String flag = Payload[0];

            // 检测设备的区域ID：用于表示表示哪个区域的设备检测到了行人。[0-255]之间的整数表示。
            String zoneID = Payload[1];

            // 检测设备的ID：用于表示是哪一个设备检测到了行人。[0-255]之间的整数表示。
            String devID = Payload[2];

            // 检测同步
            int sync = Integer.parseInt(Payload[3]);

            //保留字段1，缺省置空0
            String reserved1 = Payload[4];

            //保留字段1，缺省置空0
            String reserved2 = Payload[5];

            //保留字段1，缺省置空0
            String reserved3 = Payload[6];

            // 显示操作,1为有效，0为无效
            if (flag.equals("1") && (System.currentTimeMillis() - lastReceiveTime) > 3000) {

                // TODO: 2019/4/16
                mPeopleWarning.setText("Area-" + zoneID + ", device-" + devID + "\r\n" + " detects pedestrian");
                mPeopleWarning.setTextColor(Color.RED);
                warningP.setColorFilter(Color.RED);
                // 声音和弹出窗口，3s左右
                Toast.makeText(MainClientActivity.this, "Detects pedestrian!", Toast.LENGTH_LONG).show();

                MediaPlayer mMediaPlayer;
                mMediaPlayer = MediaPlayer.create(this, R.raw.pedestrianalerts);
                mMediaPlayer.start();
                lastReceiveTime = System.currentTimeMillis();

                System.out.println("行人警报接收");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                            view.setVisibility(View.GONE); //view是要隐藏的控件
                        warningP.setColorFilter(Color.BLACK);
                        mPeopleWarning.setText(" ");
                    }
                }, 3000);  //3000毫秒后执行
            }
        }
    }


}

