package com.xcinfo.xc_blueteeth_android.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.qrsacan.defineview.MyImageView;
import com.xcinfo.xc_blueteeth_android.common.qrsacan.utils.Constant;
import com.xcinfo.xc_blueteeth_android.common.qrsacan.zxing.ScanListener;
import com.xcinfo.xc_blueteeth_android.common.qrsacan.zxing.ScanManager;
import com.xcinfo.xc_blueteeth_android.common.qrsacan.zxing.camera.CameraManager;
import com.xcinfo.xc_blueteeth_android.common.qrsacan.zxing.decode.DecodeThread;
import com.xcinfo.xc_blueteeth_android.common.qrsacan.zxing.decode.Utils;

public final class CommonScanActivity extends Activity implements ScanListener, View.OnClickListener {
    static final String TAG = CommonScanActivity.class.getSimpleName();
    SurfaceView scanPreview = null;
    View scanContainer;
    View scanCropView;
    ImageView scanLine;
    ScanManager scanManager;
    TextView iv_light;
    TextView qrcode_g_gallery;
    TextView qrcode_ic_back;
    final int PHOTOREQUESTCODE = 1111;//扫描本地图片
    public static final int SCAN_RESULT = 1112;

    Button rescan;//再次扫描

    MyImageView scan_image;//扫描框
    ImageView ivLight;

    ImageView authorize_return;//返回键
    private int scanMode=DecodeThread.ALL_MODE;//扫描模型（条形，二维码，全部）


    TextView title;

    TextView scan_hint;//提示语

    TextView tv_scan_result;



    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//设置屏幕常开，高量
        setContentView(R.layout.activity_scan_code);
        bindView();

        //scanMode=getIntent().getIntExtra(Constant.REQUEST_SCAN_MODE,Constant.REQUEST_SCAN_MODE_ALL_MODE);
        initView();
    }

    private void bindView() {
        rescan= (Button) findViewById(R.id.service_register_rescan);
        scan_image= (MyImageView) findViewById(R.id.scan_image);
        scan_image.setWillNotDraw(false);
        iv_light= (TextView) findViewById(R.id.iv_light);
        authorize_return= (ImageView) findViewById(R.id.authorize_return);
        title= (TextView) findViewById(R.id.common_title_TV_center);
        scan_hint= (TextView) findViewById(R.id.scan_hint);
        tv_scan_result= (TextView) findViewById(R.id.tv_scan_result);
    }

    void initView() {
//        switch (scanMode){
//            case DecodeThread.BARCODE_MODE:
//                title.setText(R.string.scan_barcode_title);
//                scan_hint.setText(R.string.scan_barcode_hint);
//                break;
//            case DecodeThread.QRCODE_MODE:
//                title.setText(R.string.scan_qrcode_title);
//                scan_hint.setText(R.string.scan_qrcode_hint);
//                break;
//            case DecodeThread.ALL_MODE:
//                title.setText(R.string.scan_allcode_title);
//                scan_hint.setText(R.string.scan_allcode_hint);
//                break;
//        }
        title.setText("扫码连接");
//        scan_hint.setText("");
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = findViewById(R.id.capture_container);
        scanCropView = findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        qrcode_g_gallery = (TextView) findViewById(R.id.qrcode_g_gallery);
        qrcode_g_gallery.setOnClickListener(this);
        qrcode_ic_back = (TextView) findViewById(R.id.qrcode_ic_back);
        qrcode_ic_back.setOnClickListener(this);
        iv_light = (TextView) findViewById(R.id.iv_light);
        iv_light.setOnClickListener(this);
        rescan.setOnClickListener(this);
        authorize_return.setOnClickListener(this);
        //构造出扫描管理器
        scanManager = new ScanManager(this, scanPreview, scanContainer, scanCropView, scanLine, scanMode,this,iv_light);
    }

    @Override
    public void onResume() {
        super.onResume();
        scanManager.onResume();
        rescan.setVisibility(View.INVISIBLE);
        //scan_image.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        scanManager.onPause();
    }
    /**
     *
     */
    public void scanResult(Result rawResult, Bundle bundle) {
        //扫描成功后，扫描器不会再连续扫描，如需连续扫描，调用reScan()方法。
        //scanManager.reScan();
//		Toast.makeText(that, "result="+rawResult.getText(), Toast.LENGTH_LONG).show();

        if (!scanManager.isScanning()) { //如果当前不是在扫描状态
            //设置再次扫描按钮出现
            //rescan.setVisibility(View.VISIBLE);
            scan_image.setVisibility(View.VISIBLE);
            Bitmap barcode = null;
            byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
            if (compressedBitmap != null) {
                barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
            }
            //scan_image.setImageBitmap(barcode);
        }
        rescan.setVisibility(View.VISIBLE);
        scan_image.setVisibility(View.VISIBLE);
        tv_scan_result.setVisibility(View.VISIBLE);
        tv_scan_result.setText("结果："+rawResult.getText());
        Intent resultIntent=new Intent();
        resultIntent.putExtra("address",rawResult.getText());
        setResult(SCAN_RESULT,resultIntent);
        finish();
    }

    void startScan() {
        if (rescan.getVisibility() == View.VISIBLE) {
            rescan.setVisibility(View.INVISIBLE);
            scan_image.setVisibility(View.GONE);
            scanManager.reScan();
        }
    }

    @Override
    public void scanError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        //相机扫描出错时
        if(e.getMessage()!=null&&e.getMessage().startsWith("相机")){
            scanPreview.setVisibility(View.INVISIBLE);
        }
    }

    public void showPictures(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String photo_path;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTOREQUESTCODE:
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(data.getData(), proj, null, null, null);
                    if (cursor.moveToFirst()) {
                        int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(colum_index);
                        if (photo_path == null) {
                            photo_path = Utils.getPath(getApplicationContext(), data.getData());
                        }
                        scanManager.scanningImage(photo_path);
                    }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qrcode_g_gallery:
                showPictures(PHOTOREQUESTCODE);
                break;
            case R.id.iv_light:
                scanManager.switchLight();

                break;
            case R.id.qrcode_ic_back:
                finish();
                break;
            case R.id.service_register_rescan://再次开启扫描
                startScan();
                break;
            case R.id.authorize_return:
                finish();
                break;
            default:
                break;
        }
    }

}
