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

import com.example.chongai.Json.ResultBean_Species;
import com.example.chongai.Json.ResultTitleBean_Species;
import com.example.chongai.PicassoImageLoader;
import com.example.chongai.R;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Species_recognition extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String TAG="MainActivity";
    private static final int REQUEST=1002;
    private static  String LocalTestPath="/storage/emulated/0/test.jpg";
    private static final String tokenUrl="https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=envGynXOqkXsYVDSbaSGOkuV&client_secret=Oe7oonrn9nqhRSHbxA0z3blaqDbmfPDB";
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
        setContentView(R.layout.activity_species_recognition);
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
     *????????????Token
     *
     */
    private void Auth() {
        OkHttpClient client = new OkHttpClient();//??????OkHttpClient?????????
        Request request = new Request.Builder()//??????Request ?????????
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
        });//????????????????????????get?????????????????????????????????

    }

    CompoundButton.OnCheckedChangeListener checkedChangeListener=new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b){
                initPicker(true);
            }else {
                initPicker(false);
                //???????????????
            }
        }
    };
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_previewPhoto:
                    Intent intent = new Intent(Species_recognition.this, ImageGridActivity.class);
                    startActivityForResult(intent, REQUEST);
                    linearLayoutLeft.removeAllViews();
                    linearLayouRight.removeAllViews();
                    break;
                case R.id.btn_takePhoto:
                    Intent intent2 = new Intent(Species_recognition.this, ImageGridActivity.class);
                    intent2.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS,true); // ???????????????????????????
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
                startTime= SystemClock.uptimeMillis();
            } else {
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void upImage(final String path) {
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/animal?access_token=" + access_token;
        Log.i(TAG, "url: "+url);
        String imgStr = file2String(new File(path));
        OkHttpClient client = new OkHttpClient();//??????OkHttpClient?????????
        FormBody formBody = new FormBody.Builder()
                //add????????????form??????
                .add("image",imgStr)//?????????????????????
                .add("top_num","1")
                .add("baike_num","2")
                  .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type","application/x-www-form-urlencode")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: "+call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {//response???????????????????????????

                ResultTitleBean_Species resultTitleBean_species = (ResultTitleBean_Species) handleReulstResponse(response.body().string());
                final List<ResultBean_Species> resultBeanList = resultTitleBean_species.result;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        linearLayoutLeft.removeAllViews();
                        linearLayouRight.removeAllViews();
//                            Log.i(TAG, "result.size(): "+resultBeanList.size());
//                            Log.i(TAG, "????????????: ");
                        for(int i=0;i<resultBeanList.size();i++){
//                                Log.i(TAG, "resultBeanList.get(i).keyword: "+ resultBeanList.get(i).name);
//                                Log.i(TAG, "resultBeanList.get(i).root: "+ resultBeanList.get(i).score);
//                                Log.i(TAG, "resultBeanList.get(i).score: "+ resultBeanList.get(i).baike_info.getDescription());
                            TextView textView = new TextView(Species_recognition.this);
                            textView.setText(
                                    "?????????"+resultBeanList.get(i).name+"\r\n"+
                                            "score???"+resultBeanList.get(i).score+"\r\n"+
                                    "?????????????????????"+resultBeanList.get(i).baike_info.getDescription());
                            linearLayoutLeft.addView(textView, i);
                        }
                        ImageView imageView=new ImageView(Species_recognition.this);
                        imageView.setImageBitmap(BitmapFactory.decodeFile(path));//????????????
                        linearLayouRight.addView(imageView);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                        tv_time.setText("????????? "+String.valueOf(lastProcessingTimeMs)+"ms");
                        Log.i(TAG, "lastProcessingTimeMs: "+lastProcessingTimeMs);
                    }
                });
                // Log.i(TAG, "onResponse: "+response.body().string());
            }
        });//????????????????????????get?????????????????????????????????

    }




    /**
     * ??????JSON??????
     * */
    public Object handleReulstResponse(String response){
        Log.i(TAG, "handleReulstResponse: " + response);

        return  new Gson().fromJson(response,ResultTitleBean_Species.class);
    }




    /**
     * @param b ?????????????????? true??????false???
     * */
    private void initPicker(boolean b) {
        imagePicker= ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());   //?????????????????????
        imagePicker.setShowCamera(true);  //??????????????????
        imagePicker.setMultiMode(false);//????????????
        imagePicker.setCrop(b);        //?????????????????????????????????
        imagePicker.setFreeCrop(b, FreeCropImageView.CropMode.FREE);//????????????,????????????????????????setCrop
        imagePicker.setSaveRectangle(true); //???????????????????????????
        imagePicker.setSelectLimit(9);    //??????????????????
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //??????????????????
        imagePicker.setFocusWidth(800);   //?????????????????????????????????????????????????????????????????????
        imagePicker.setFocusHeight(800);  //?????????????????????????????????????????????????????????????????????
        imagePicker.setOutPutX(1000);//????????????????????????????????????
        imagePicker.setOutPutY(1000);//????????????????????????????????????
    }
}