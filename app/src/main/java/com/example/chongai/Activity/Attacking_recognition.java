package com.example.chongai.Activity;

import static com.example.chongai.Utils.FileUtils.file2String;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chongai.PicassoImageLoader;
import com.example.chongai.R;
import com.example.chongai.Tool.GsonUtils;
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
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Attacking_recognition extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String TAG="MainActivity";
    private static final int REQUEST=1002;
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
        setContentView(R.layout.activity_attacking_recognition);
        findViewById(R.id.btn_takePhoto).setOnClickListener(listener);
        findViewById(R.id.btn_previewPhoto).setOnClickListener(listener);
        tv_time=findViewById(R.id.tv_time);
        checkBox=findViewById(R.id.cb_chooseModle);
        checkBox.setOnCheckedChangeListener(checkedChangeListener);
        checkBox.setChecked(true);
        linearLayoutLeft =findViewById(R.id.ll_left);
        linearLayouRight =findViewById(R.id.ll_right);
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
                    Intent intent = new Intent(Attacking_recognition.this, ImageGridActivity.class);
                    startActivityForResult(intent, REQUEST);
                    linearLayoutLeft.removeAllViews();
                    linearLayouRight.removeAllViews();
                    break;
                case R.id.btn_takePhoto:
                    Intent intent2 = new Intent(Attacking_recognition.this, ImageGridActivity.class);
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
        String url = "http://43.143.58.163:5000/add";
        Log.i(TAG, "url: "+url);
        String imgStr = file2String(new File(path));
        OkHttpClient client = new OkHttpClient();//??????OkHttpClient?????????
        Map<String, Object> map = new HashMap<>();
        map.put("img", imgStr);
        // map.put("top_num", "5");
        map.put("type","jpg");
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
            public void onResponse(Call call, Response response) throws IOException {//response???????????????????????????
               String responseData = response.body().string();
                Log.i(TAG, "responseData: " + responseData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        linearLayoutLeft.removeAllViews();
                        linearLayouRight.removeAllViews();
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String results = jsonObject.getString("results");
                            String Results = URLDecoder.decode(results,"utf-8");
                            TextView textView = new TextView(Attacking_recognition.this);
                            textView.setText(
                                    "???????????????"+Results);
                            linearLayoutLeft.addView(textView);
                        } catch (JSONException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        ImageView imageView=new ImageView(Attacking_recognition.this);
                        imageView.setImageBitmap(BitmapFactory.decodeFile(path));//????????????
                        linearLayouRight.addView(imageView);

                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                        tv_time.setText("????????? "+String.valueOf(lastProcessingTimeMs)+"ms");
                        Log.i(TAG, "lastProcessingTimeMs: "+lastProcessingTimeMs);
                    }
                });

            }
        });//????????????????????????get?????????????????????????????????

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