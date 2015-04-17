package ie.yesequality.yesequality;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;


public class CameraMainActivity extends Activity implements SurfaceHolder.Callback, Camera.ShutterCallback, Camera.PictureCallback {


    Camera mCamera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;

    int duration = Toast.LENGTH_SHORT;
    static int pictureWidth = 0;
    static int pictureHeight = 0;

    ImageView selfieButton, retakeButton, shareButtonBot, shareButton, infoButton;

    static int result;
    static int heightofbottom;
    static int heightoftoolbar;
    static float logicalDensity;
    protected static final String IMAGEVIEW_TAG = "Yes Badge";
    private RelativeLayout.LayoutParams layoutParams;
    private String msg;

    private static final Integer[] badgeArray ={
            R.drawable.ic_yes_icon,
            R.drawable.ta,
            R.drawable.ta_white,
            R.drawable.yes,
            R.drawable.yes_white,
            R.drawable.yes_imvoting,
            R.drawable.yes_imvoting_white,
            R.drawable.yes_me,
            R.drawable.yes_me_white,
            R.drawable.yes_we_revoting,
            R.drawable.yes_we_revoting_white
    };

    private int badgeArrayPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        setContentView(R.layout.surface_camera_layout);
        setContentView(R.layout.camera);

     //   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().setFormat(PixelFormat.UNKNOWN);
//        surfaceView = (SurfaceView)findViewById(R.id.surface_camera);
        surfaceView = (SurfaceView)findViewById(R.id.surface_custom_camera);


        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        heightoftoolbar = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
        int leftover = screenHeight - heightoftoolbar;
        heightofbottom = leftover - screenWidth;
        LinearLayout bottom = (LinearLayout)findViewById(R.id.bottom_bar);
        ViewGroup.LayoutParams paramsBot = bottom.getLayoutParams();
        paramsBot.height = heightofbottom;

        pictureWidth = screenWidth;
        pictureHeight = screenWidth;

//        RelativeLayout surfaceLayout = (RelativeLayout)findViewById(R.id.surface_layout);
//        FrameLayout surfaceLayout = (FrameLayout)findViewById(R.id.surface_layout);
//        LayoutParams params = surfaceLayout.getLayoutParams();
////        params.height = screenWidth;
//        params.width = screenWidth;
//
//        int usable = screenHeight - screenWidth;





//        LinearLayout topLayout = (LinearLayout)findViewById(R.id.top_bar);
//        LayoutParams paramsTop = topLayout.getLayoutParams();
       // paramsTop.height = usable/3;
       // paramsTop.width = usable/2;

//        LinearLayout bottomLayout = (LinearLayout)findViewById(R.id.bottom_bar);
//        LayoutParams paramsBot = bottomLayout.getLayoutParams();
//        paramsBot.height = usable - paramsTop.height;
       // paramsBot.width = usable/2;



        //  surfaceLayout.setLayoutParams(params);

        //Get the width of the screen
      //  int screenWidth = getWindowManager().getDefaultDisplay().getWidth();




        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);



        controlInflater = LayoutInflater.from(getBaseContext());
       // View viewControl = controlInflater.inflate(R.layout.custom_camera, null);
        ActionBar.LayoutParams layoutParamsControl = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT,
                ActionBar.LayoutParams.FILL_PARENT);
       // this.addContentView(viewControl, layoutParamsControl);


        selfieButton = (ImageView) findViewById(R.id.selfieButton);
        selfieButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here


                Camera.Parameters params =getParams();
                mCamera.setParameters(params);
                mCamera.takePicture(CameraMainActivity.this, null, null, CameraMainActivity.this);
            }
        });


        retakeButton = (ImageView) findViewById(R.id.retakeButton);
        retakeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here

                retakeLogic();
               // mCamera.takePicture(CameraMainActivity.this, null, null, CameraMainActivity.this);
            }
        });


        shareButtonBot = (ImageView) findViewById(R.id.shareButtonBotom);
        shareButtonBot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
                shareIt();

                // mCamera.takePicture(CameraMainActivity.this, null, null, CameraMainActivity.this);
            }
        });

        shareButton = (ImageView) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here

                badgeArrayPosition ++;
                if(badgeArrayPosition==badgeArray.length){
                    badgeArrayPosition = 0;
                }

                Drawable d = getResources().getDrawable(badgeArray[badgeArrayPosition]);
                ImageView imageView = (ImageView)findViewById(R.id.waterMarkPic);
                imageView.setImageDrawable(d);
            }
        });

        infoButton = (ImageView) findViewById(R.id.moreInfoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here


                Intent intent =  new Intent(CameraMainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void shareIt() {

        String fname = getPhotoDirectory(CameraMainActivity.this)+"/yesequal.jpg";

        Bitmap myfile = BitmapFactory.decodeFile(fname);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DESCRIPTION, "I voted yes");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);


        OutputStream outstream;
        try {
            outstream = getContentResolver().openOutputStream(uri);
            myfile.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());

        }

        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image"));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


        if (mCamera != null){
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
                previewing = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        selfieButton.setVisibility(View.VISIBLE);
        retakeButton.setVisibility(View.INVISIBLE);
        shareButtonBot.setVisibility(View.INVISIBLE);

        if (Camera.getNumberOfCameras() >= 2) {

            //if you want to open front facing camera use this line
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            setCameraDisplayOrientation(this,Camera.CameraInfo.CAMERA_FACING_FRONT,mCamera);

            //if you want to use the back facing camera
           // camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//            setCameraDisplayOrientation(this,Camera.CameraInfo.CAMERA_FACING_BACK,mCamera);
        } else {
            mCamera = Camera.open();
        }

//        mCamera.setDisplayOrientation(270);
//
//        Camera.Parameters mCameraParameters = mCamera.getParameters();
//        List<Camera.Size> sizes = mCameraParameters.getSupportedPreviewSizes();
//        Camera.Size optimalSize = getOptimalPreviewSize(sizes, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
//        mCameraParameters.setRotation(270);
//        mCameraParameters.setPreviewSize(optimalSize.width, optimalSize.height);
//        mCamera.setParameters(mCameraParameters);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        previewing = false;

        selfieButton.setVisibility(View.VISIBLE);
        retakeButton.setVisibility(View.INVISIBLE);
        shareButtonBot.setVisibility(View.INVISIBLE);

    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w/h;

        if (sizes==null) return null;

        Camera.Size optimalSize = null;

        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Find size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }




    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Matrix matrixInverse =  new Matrix();
        Matrix matrixOverlay =  new Matrix();

        float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1};
        Matrix matrixMirrorY = new Matrix();
        matrixMirrorY.setValues(mirrorY);
        matrixInverse.postConcat(matrixMirrorY);

        Bitmap bmInverseOverlay = Bitmap.createBitmap(bmp1, 0, 0, bmp1.getWidth(), bmp1.getHeight(), matrixInverse, true);

        Canvas canvas = new Canvas(bmInverseOverlay);
        matrixOverlay.postTranslate(pictureWidth / 75, (float) ((pictureWidth / 10) * 7.5));;
        canvas.drawBitmap(bmp2, matrixOverlay, null);

        return bmInverseOverlay;
    }

//    private Bitmap scaleBitmap(Bitmap  bm) {
//        // get ImageView and determine width & height
//        int imageWidth = bm.getWidth();
//        int imageHeight = bm.getHeight();
//        Rect imageRect = new Rect(0, 0, imageWidth, imageHeight);
//
//        // determine destination rectangle
////        BitmapDrawable drawable = (BitmapDrawable)iv.getDrawable();
////        Bitmap sourceBitmap = drawable.getBitmap();
//        Rect bitmapRect = new Rect(0, 0, imageWidth, imageHeight);
//
//        // determine source rectangle
//        Rect sourceRect = computeSourceRect(imageRect, bitmapRect);
//
//        // here's where we do the magic
//        Bitmap scaledBitmap = Bitmap.createBitmap(imageRect.width(), imageRect.height(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(scaledBitmap);
//        canvas.drawBitmap(bm, sourceRect, imageRect, new Paint());
////        iv.setImageBitmap(scaledBitmap);
//        return scaledBitmap;
//    }

//    private Rect computeSourceRect(Rect imageRect, Rect bitmapRect) {
//        float imageWidth = (float) imageRect.width();
//        float imageHeight = (float) imageRect.height();
//        float bitmapWidth = (float) bitmapRect.width();
//        float bitmapHeight = (float) bitmapRect.height();
//        float aspectRatioImage = imageWidth / imageHeight;
//        float aspectRatioBitmap = bitmapWidth / bitmapHeight;
//
//        if (aspectRatioImage<aspectRatioBitmap) {
//            float newWidth = bitmapHeight * aspectRatioImage;
//            float widthOffset = (bitmapWidth - newWidth) / 2f;
//            return new Rect((int) widthOffset, 0, (int) (widthOffset + newWidth), (int) bitmapHeight);
//        }
//        else {
//            float newHeight = (bitmapWidth -heightofbottom) / aspectRatioImage;
//            float heightOffset = (bitmapHeight - newHeight) / 2f;
//            // return this for center_crop
//            return new Rect(0, (int) heightOffset, (int) bitmapWidth, (int) (heightOffset + newHeight));
//            // return this for bottom align
////            return new Rect(0, 0, (int) bitmapWidth, (int) newHeight);
//        }
//    }

//    private Bitmap cropBitmap1(Bitmap bitmap )
//    {
//        Bitmap bmOverlay = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
//        Paint p = new Paint();
//        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        p.setColor(Color.BLUE);
//        p.setStyle(Paint.Style.FILL);
//        p.setAlpha(10);
//        p.setStyle(Paint.Style.STROKE);
//        Canvas c = new Canvas(bmOverlay);
//        c.drawBitmap(bitmap, 0, 0, null);
//        c.drawRect(30, 30, 100, 100, p);
//
//        return bitmap;
//    }

    byte[] resizeImageAndWaterMark(byte[] input, int width, int height) {
        Bitmap original = BitmapFactory.decodeByteArray(input, 0, input.length);

        Matrix matrix = new Matrix();
        matrix.postRotate(result);
        Bitmap newBitmap = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight());
        Bitmap rotatedBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.getWidth(), newBitmap.getHeight(), matrix, true);

//        Bitmap waterMark;
//        if(badgeArrayPosition!=0){
//           waterMark = BitmapFactory.decodeResource(this.getResources(),
//                    badgeArray[badgeArrayPosition - 1]);
//        }else{
//            waterMark = BitmapFactory.decodeResource(this.getResources(),
//                    badgeArray[badgeArrayPosition]);
//        }

        Bitmap waterMark = BitmapFactory.decodeResource(this.getResources(),
                    badgeArray[badgeArrayPosition]);
        ImageView image = (ImageView) findViewById(R.id.waterMarkPic);
        Bitmap IV = ((BitmapDrawable)image.getDrawable()).getBitmap();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;
        int waterMarkWidthpx = (int) (100 * logicalDensity);
        int waterMarkHeightpx = (int) (100 * logicalDensity);
        Log.i("fergal", Integer.toString(waterMarkWidthpx));
        Log.i("fergal", Float.toString(logicalDensity));
        Log.i("fergal", Float.toString(waterMark.getWidth()));

//        if(waterMark.getWidth() >= 100){
//            waterMarkWidthpx = waterMark.getWidth();
//        }
//
//        if(waterMark.getHeight() >= 100){
//            waterMarkHeightpx = waterMark.getHeight();
//        }

//        Bitmap scaledWaterMark = Bitmap.createScaledBitmap(waterMark, waterMark.getWidth() * 2, waterMark.getHeight() * 2, true);
        Bitmap scaledWaterMark = Bitmap.createScaledBitmap(IV, waterMarkWidthpx, waterMarkHeightpx,true);
        Bitmap croppedBitmap = Bitmap.createBitmap(rotatedBitmap,0,0, rotatedBitmap.getWidth(), (rotatedBitmap.getHeight()-heightofbottom+(heightoftoolbar/2)));
        Bitmap scaled = Bitmap.createScaledBitmap(croppedBitmap,width,width,true);
        scaled = overlay(scaled, scaledWaterMark);
//        scaled = overlay(scaled, waterMark);


        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 100, blob);

        return blob.toByteArray();
    }


    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        data = resizeImageAndWaterMark(data, pictureWidth, pictureHeight);

        selfieButton.setVisibility(View.INVISIBLE);
        retakeButton.setVisibility(View.VISIBLE);
        shareButtonBot.setVisibility(View.VISIBLE);


            try {
                Date now = new Date(); long nowLong = now.getTime() / 9000;
                String fname = getPhotoDirectory(CameraMainActivity.this)+"/yesequal.jpg";
                File ld = new File(getPhotoDirectory(CameraMainActivity.this));
                if (ld.exists()) {
                    if (!ld.isDirectory()){
                        CameraMainActivity.this.finish();
                    }
                } else {
                    ld.mkdir();
                }

                Log.d("YES", "open output stream "+fname +" : " +data.length);

                OutputStream os = new FileOutputStream(fname);
                os.write(data,0,data.length);
                os.close();


            } catch (FileNotFoundException e) {
                Toast.makeText(CameraMainActivity.this, "FILE NOT FOUND !", duration).show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(CameraMainActivity.this, "IO EXCEPTION", duration).show();
            }
          //  camera.startPreview();

    }


   public void retakeLogic() {

       selfieButton.setVisibility(View.VISIBLE);
       retakeButton.setVisibility(View.INVISIBLE);
       shareButtonBot.setVisibility(View.INVISIBLE);

       if ( mCamera != null ) {
           mCamera.startPreview();
       }

   }



    public static String getPhotoDirectory(Context context)
    {
        //return Environment.getExternalStorageDirectory().getPath() +"/cbo-up";
        //return context.getExternalCacheDir().getPath();
        return context.getExternalFilesDir(null).getPath();
    }

    @Override
    public void onShutter() {

        Toast.makeText(CameraMainActivity.this, "Selfie Time! :)", duration).show();
    }

//    public void onOrientationChanged(int orientation) {
//        if (orientation == ORIENTATION_UNKNOWN) return;
//        android.hardware.Camera.CameraInfo info =
//                new android.hardware.Camera.CameraInfo();
//        android.hardware.Camera.getCameraInfo(cameraId, info);
//        orientation = (orientation + 45) / 90 * 90;
//        int rotation = 0;
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            rotation = (info.orientation - orientation + 360) % 360;
//        } else {  // back-facing camera
//            rotation = (info.orientation + orientation) % 360;
//        }
//        mParameters.setRotation(rotation);
//    }

    private Camera.Parameters getParams(){
//        mCamera.setDisplayOrientation(270);
        Camera.Parameters mCameraParameters = mCamera.getParameters();
        List<Camera.Size> sizes = mCameraParameters.getSupportedPreviewSizes();
        Camera.Size optimalSize = getOptimalPreviewSize(sizes, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        mCameraParameters.setRotation(180);
        mCameraParameters.setPreviewSize(optimalSize.width, optimalSize.height);
        return mCameraParameters;
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera)
    {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation)
        {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        }
        else
        { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

}
