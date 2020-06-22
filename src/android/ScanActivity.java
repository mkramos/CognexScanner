package com.cognex.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cognex.mobile.barcode.sdk.ReadResults;
import com.cognex.mobile.barcode.sdk.ReaderDevice;
import com.cognex.mobile.barcode.sdk.ReadResults;
import com.cognex.mobile.barcode.sdk.ReaderDevice;
import com.cognex.dataman.sdk.CameraMode;
import com.cognex.dataman.sdk.ConnectionState;
import com.cognex.dataman.sdk.PreviewOption;
import com.cognex.mobile.barcode.sdk.ReadResult;
import com.cognex.mobile.barcode.sdk.ReaderDevice.Availability;
import com.cognex.mobile.barcode.sdk.ReaderDevice.OnConnectionCompletedListener;
import com.cognex.mobile.barcode.sdk.ReaderDevice.ReaderDeviceListener;
import com.cognex.mobile.barcode.sdk.ReaderDevice.Symbology;

import java.io.ByteArrayOutputStream;

public class ScanActivity extends AppCompatActivity implements OnConnectionCompletedListener, ReaderDeviceListener {
    String camstate;
    ReaderDevice readerDevice;
    ReadResult result;
    TextView tvCode;
    Button btshow;
    ImageView ivPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        tvCode = (TextView) findViewById(R.id.tvCode);
        btshow = (Button) findViewById(R.id.btshow);
        ivPreview = (ImageView)findViewById(R.id.ivPreview);

        Intent intent = getIntent();
        camstate = intent.getStringExtra("camstate");

        if (camstate.equals("front")) {
            readerDevice = ReaderDevice.getPhoneCameraDevice(ScanActivity.this, CameraMode.FRONT_CAMERA, PreviewOption.DEFAULTS);
        }
        else if (camstate.equals("back")){
            readerDevice = ReaderDevice.getPhoneCameraDevice(ScanActivity.this, CameraMode.NO_AIMER, PreviewOption.HIGH_RESOLUTION | PreviewOption.HIGH_FRAME_RATE);
        }
        readerDevice.setReaderDeviceListener(ScanActivity.this);
        readerDevice.enableImage(true);
        readerDevice.connect(ScanActivity.this);




    }

    @Override
    public void onConnectionCompleted(ReaderDevice readerDevice, Throwable error) {
        if (error != null) {
            // READER DISCONNECTED (ERROR OCCURED)
            readerDevice.stopScanning();
        }

        readerDevice.startScanning();
        readerDevice.setSymbologyEnabled(Symbology.QR, true, null);
        readerDevice.setSymbologyEnabled(Symbology.UPC_EAN, true, null);

    }

    @Override
    public void onConnectionStateChanged(ReaderDevice readerDevice) {
        if (readerDevice.getConnectionState() == ConnectionState.Connected) {
            readerDevice.startScanning();
            readerDevice.setSymbologyEnabled(Symbology.QR, true, null);
            readerDevice.setSymbologyEnabled(Symbology.UPC_EAN, true, null);
            readerDevice.setSymbologyEnabled(Symbology.DATAMATRIX, true, null);
        } else if (readerDevice.getConnectionState() == ConnectionState.Disconnected) {
            readerDevice.stopScanning();
            // READER DISCONNECTED
        }
    }

    @Override
    public void onReadResultReceived(ReaderDevice readerDevice, ReadResults results) {
        if (results.getCount() > 0) {
            result = results.getResultAt(0);
            // USE String symbologyName; String code; Bitmap frame; VARIABLES IN YOUR APPLICATION
            if (result.isGoodRead()) {
                // String symbologyName;
                String code = result.getReadString();
                //Symbology symbology = result.getSymbology();
                //if (symbology != null) {
                // symbologyName = symbology.getName();
                // tvSymbology.setText(symbologyName);
                // } else {
                // tvSymbology.setText("UNKNOWN SYMBOLOGY");
                //  }
               // tvCode.setText(code);
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("tvCode", code);
                Bitmap bitmap = result.getImage(); // your bitmap
                ByteArrayOutputStream _bs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, _bs);
                intent.putExtra("byteArray", _bs.toByteArray());
                startActivity(intent);
            } else {
                //tvSymbology.setText("NO READ");
                tvCode.setText("");
            }
            /*btshow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap frame = result.getImage();
                    ivPreview.setImageBitmap(frame);
                    ivPreview.setVisibility(View.VISIBLE);
                    btshow.setVisibility(View.GONE);
                }
            });*/

            readerDevice.disconnect();
        }


    }

    @Override
    public void onAvailabilityChanged(ReaderDevice readerDevice) {
        if (readerDevice.getAvailability() == Availability.AVAILABLE) {
            readerDevice.startScanning();
            readerDevice.setSymbologyEnabled(Symbology.QR, true, null);
            readerDevice.setSymbologyEnabled(Symbology.UPC_EAN, true, null);
        } else {
// DISCONNECT DEVICE
            readerDevice.stopScanning();
        }



    }


}
