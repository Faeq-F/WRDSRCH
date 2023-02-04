package com.faeq.wrdsrch;

//general app imports
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//Android imports
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
//OpenCV imports
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
//Java imports; OpenCV methods require data to be given in lists of this type
import java.util.List;
//----------------------------------------------------------------------------------------------------------
public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    //------------------------------------------------------------------------------------------------------
    //Tag for activity (we only have one so this is the MainActivity)
    private static final String TAG = "MainActivity";
    //For Camera View
    JavaCameraView CameraView;
    //specifying that we are using the back camera (unable to specify wide-lens camera - maybe look into later)
    int activeCamera = CameraBridgeViewBase.CAMERA_ID_BACK;
    //Code for camera permissions
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private Mat mRgba;
    //------------------------------------------------------------------------------------------------------
    //initialises camera when app is first launched or when onResume is called from phone sleep
    private void initializeCamera(JavaCameraView CameraView, int activeCamera){
        CameraView.setCameraPermissionGranted();
        CameraView.setCameraIndex(activeCamera);
        CameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        CameraView.setCvCameraViewListener(this);
    }
    //------------------------------------------------------------------------------------------------------
    //Enables Camera when OpenCV loads correctly (see onResume())
    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(MainActivity.this) {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onManagerConnected(int status) {
            if (status == BaseLoaderCallback.SUCCESS){
                CameraView.enableView();
            }
            else super.onManagerConnected(status);
        }
    };
    //------------------------------------------------------------------------------------------------------
    //Helps with the app starting up fast (instead of waiting for onResume())
    static{
        if (OpenCVLoader.initDebug()) Log.d(TAG, "OpenCV is configured correctly");
        else Log.d(TAG, "OpenCV is NOT configured correctly");
    }
    //------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //show Main Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //show Camera View
        CameraView = findViewById(R.id.CameraView);
        //need permission checking above Android 5 (Our phone runs Android 9)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permissions granted");
            initializeCamera(CameraView, activeCamera);
        } else {
            Log.d(TAG, "Permission prompt");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        //fullscreen Camera view
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    //------------------------------------------------------------------------------------------------------
    //No need to manipulate frames - due to using back camera
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }
    //------------------------------------------------------------------------------------------------------
    private Scalar convertScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
    //------------------------------------------------------------------------------------------------------
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();


        return mRgba;
    }

    //------------------------------------------------------------------------------------------------------
    @Override
    protected void onResume(){
        super.onResume();
        //Check if OpenCV has loaded correctly
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV is configured correctly");
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            Log.d(TAG, "OpenCV is NOT configured correctly");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, MainActivity.this, baseLoaderCallback);
        }
    }
    //------------------------------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            //Camera can be used
            Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            initializeCamera(CameraView, activeCamera);
        } else Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
    }
    //------------------------------------------------------------------------------------------------------
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { super.onPointerCaptureChanged(hasCapture); }
    //------------------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() { super.onDestroy(); if (CameraView != null) CameraView.disableView(); }
    //------------------------------------------------------------------------------------------------------
    @Override
    protected void onPause() { super.onPause(); if (CameraView != null) CameraView.disableView(); }
    //------------------------------------------------------------------------------------------------------
    @Override
    public void onCameraViewStopped() { try {mRgba.release();} catch(NullPointerException e){Log.d(TAG, "No frame to release");}}
}