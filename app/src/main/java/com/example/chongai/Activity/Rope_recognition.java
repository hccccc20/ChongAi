package com.example.chongai.Activity;

import static com.example.chongai.Utils.FileUtils.file2String;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chongai.Json.ResultBean_Rope;
import com.example.chongai.Json.ResultTitleBean_Rope;
import com.example.chongai.PicassoImageLoader;
import com.example.chongai.R;
import com.example.chongai.Tool.GsonUtils;
import com.google.gson.Gson;
import com.isseiaoki.simplecropview.FreeCropImageView;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Rope_recognition extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String TAG="MainActivity";
    private static final int REQUEST=1002;
    private static final String tokenUrl="https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=7d6kmxG8T85jxeGGnpwaKvvB&client_secret=tVK3eUy7wANzG0GM9cwOQY3gVYAhx4uu";
    String access_token=null;
    TextView tv_time;
    LinearLayout linearLayoutLeft;
    LinearLayout linearLayouRight;
    CheckBox checkBox;
    private long lastProcessingTimeMs;
    private long startTime;
    ImagePicker imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rope_recognition);
        findViewById(R.id.btn_takePhoto).setOnClickListener(listener);
        findViewById(R.id.btn_previewPhoto).setOnClickListener(listener);
        tv_time=findViewById(R.id.tv_time);
        checkBox=findViewById(R.id.cb_chooseModle);
        checkBox.setOnCheckedChangeListener(checkedChangeListener);
        checkBox.setChecked(true);
        linearLayoutLeft =findViewById(R.id.ll_left);
        linearLayouRight =findViewById(R.id.ll_right);

        Auth();
    }


    /*
     *获取鉴权Token
     *
     */
    private void Auth() {
        //   Log.i(TAG, "Auth: ");
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        Request request = new Request.Builder()//创建Request 对象。
                .url(tokenUrl)
                .build();
        Log.i(TAG,"tokenUrl: "+tokenUrl);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onDestroy();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String getResponse = response.body().string();
                    Log.i(TAG, "getResponse: "+getResponse);
                    if(!TextUtils.isEmpty(getResponse)) {
                        JSONObject tokenObject = new JSONObject(getResponse);
                        access_token = tokenObject.getString("access_token");
                        Log.i(TAG, "access_token: "+access_token);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });//回调方法的使用与get异步请求相同，此时略。

    }

    CompoundButton.OnCheckedChangeListener checkedChangeListener=new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b){
                initPicker(true);
            }else {
                initPicker(false);
                //不允许裁剪
            }
        }
    };
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_previewPhoto:
                    Intent intent = new Intent(Rope_recognition.this, ImageGridActivity.class);
                    startActivityForResult(intent, REQUEST);
                    linearLayoutLeft.removeAllViews();
                    linearLayouRight.removeAllViews();
                    break;
                case R.id.btn_takePhoto:
                    Intent intent2 = new Intent(Rope_recognition.this, ImageGridActivity.class);
                    intent2.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS,true); // 是否是直接打开相机
                    startActivityForResult(intent2, REQUEST);
                    linearLayoutLeft.removeAllViews();
                    linearLayouRight.removeAllViews();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == REQUEST) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                //     Log.i(TAG,"image%%" +images.size() );
                upImage(images.get(0).path);
                startTime=SystemClock.uptimeMillis();
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void upImage(final String path) {
        //     Log.i(TAG, "upImage: ");
           String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/detection/getpost_rope?access_token=" + access_token;
        Log.i(TAG, "url: "+url);

        String imgStr = file2String(new File(path));
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        Map<String, Object> map = new HashMap<>();
        map.put("image", imgStr);
        String param = GsonUtils.toJson(map);
        RequestBody body = RequestBody.create(JSON, param);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: "+call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {//response为服务器返回的数据


                Log.i(TAG, "response.body() " + response.body().toString());
                ResultTitleBean_Rope mreulstTitleBean=(ResultTitleBean_Rope) handleReulstResponse(response.body().string());
                Log.i(TAG, "mreulstTitleBean.log_id: "+mreulstTitleBean.log_id);
//                    Log.i(TAG, "mreulstTitleBean.result_num: "+mreulstTitleBean.result_num);
//                    Log.i(TAG, "mreulstTitleBean.log_id: "+mreulstTitleBean.log_id);
                final List<ResultBean_Rope> resultBeanList = mreulstTitleBean.results;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = new TextView(Rope_recognition.this);
                        int k = 0;
                        linearLayoutLeft.removeAllViews();
                        linearLayouRight.removeAllViews();
                            Log.i(TAG, "result.size(): "+resultBeanList.size());
//                            Log.i(TAG, "进入线程: ");
                        for(int i=0;i<resultBeanList.size();i++){
//                            Log.i(TAG, "resultBeanList.get(i).score: "+ resultBeanList.get(1).name);
                            if (resultBeanList.get(i).name.equals("rope"))
                                k++;
                        }
                        if (k>0) textView.setText("是否牵绳："+"已牵绳");
                        else textView.setText("是否牵绳："+"未牵绳");
                        linearLayoutLeft.addView(textView);
                        ImageView imageView=new ImageView(Rope_recognition.this);
                        imageView.setImageBitmap(BitmapFactory.decodeFile(path));//展示图片
                        linearLayouRight.addView(imageView);

                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                        tv_time.setText("用时： "+String.valueOf(lastProcessingTimeMs)+"ms");
                        Log.i(TAG, "lastProcessingTimeMs: "+lastProcessingTimeMs);
                    }
                });

                // Log.i(TAG, "onResponse: "+response.body().string());
            }
        });//回调方法的使用与get异步请求相同，此时略。

    }


    /**
     * 解析JSON数据
     * */
    public Object handleReulstResponse(String response){
        Log.i(TAG, "handleReulstResponse: " + response);
        return  new Gson().fromJson(response,ResultTitleBean_Rope.class);
    }




    /**
     * @param b 是否允许裁剪 true是，false否
     * */
    private void initPicker(boolean b) {
        imagePicker= ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setMultiMode(false);//单选照片
        imagePicker.setCrop(b);        //允许裁剪（单选才有效）
        imagePicker.setFreeCrop(b, FreeCropImageView.CropMode.FREE);//新版添加,自由裁剪，优先于setCrop
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

}