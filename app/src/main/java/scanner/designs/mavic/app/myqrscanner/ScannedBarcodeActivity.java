package scanner.designs.mavic.app.myqrscanner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScannedBarcodeActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    boolean isEmail = false;
    FloatingActionButton fab;
     MediaPlayer mp ;

    Vibrator v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        initViews();
    }

    private void initViews() {txtBarcodeValue = (TextView) findViewById(R.id.txtBarcodeValue);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mp = MediaPlayer.create(this, R.raw.camera_focus);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (intentData.length() > 0) {
            if (isEmail) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, intentData);
                startActivity(Intent.createChooser(intent, "Send Email"));
            }else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentData)));
            }
        }

    }

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();


        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);

                        cameraSource.start(surfaceView.getHolder());
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {




                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {

                            for (int index = 0; index < barcodes.size(); index++) {
                                Barcode code = barcodes.valueAt(index);

                                int type = barcodes.valueAt(index).valueFormat;
                                switch (type) {
                                    case Barcode.CONTACT_INFO:
                                        //add your action here
                                        break;
                                    case Barcode.EMAIL:
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/html");
                                        intent.putExtra(Intent.EXTRA_EMAIL, intentData);
                                        startActivity(Intent.createChooser(intent, "Send Email"));
                                        break;
                                    case Barcode.ISBN:
                                      //Action for ISBN
                                        break;
                                    case Barcode.PHONE:
                                        //Action for Phone number
                                        break;
                                    case Barcode.PRODUCT:
                                        //Action for Product
                                        break;
                                    case Barcode.SMS:
                                        //Action for SMS
                                        break;
                                    case Barcode.TEXT:
                                        //Action for Blind Text
                                        break;
                                    case Barcode.URL:
                                        barcodeDetector.release();
                                        mp.start();
                                        isEmail = false;
                                        intentData = barcodes.valueAt(0).displayValue;
                                        txtBarcodeValue.setText("Detected");

                                        v.vibrate(500);
                                        startActivity(new Intent(ScannedBarcodeActivity.this,BrowserActivity.class).putExtra("Url",intentData));
                                        break;
                                    case Barcode.WIFI:
                                        //Action for Wifi
                                        break;
                                    case Barcode.GEO:
                                        //Action for Geo Location
                                        break;
                                    case Barcode.CALENDAR_EVENT:
                                        //Action for Calendar Event
                                        break;
                                    default:
                                        //Action for Defualt Value
                                        break;
                                }
                            }


                        }
                    });

                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();


    }
}
