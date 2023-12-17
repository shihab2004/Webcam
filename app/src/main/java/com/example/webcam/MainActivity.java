package com.example.webcam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    private AudioRecord audioRecord;
    private boolean isRecording = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("AudioRecord", String.valueOf(BUFFER_SIZE));


    }


    public void job(View v) {
        if (!isRecording) {
            isRecording = true;
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(this, "Streaming sdsd", Toast.LENGTH_SHORT).show();
                return;
            }
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);

            audioRecord.startRecording();
            new SendAudioTask().execute();
            Toast.makeText(this, "Streaming started", Toast.LENGTH_SHORT).show();
        }






}

    private class SendAudioTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("AudioRecord","working-1");
            try {
                Socket socket = new Socket("192.168.1.104", 6004); // Replace with your server IP and port
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                byte[] buffer = new byte[BUFFER_SIZE];
                Log.e("AudioRecord","working-2");
                while (isRecording) {
                    Log.w("AudioRecord","working-loop");
                    int bytesRead = audioRecord.read(buffer, 0, BUFFER_SIZE);
                    dos.write(buffer, 0, bytesRead);
                    dos.flush();
                }
                Log.e("AudioRecord","working-3");
                dos.close();
                socket.close();
            } catch (IOException e) {
//                Toast.makeText(this, "Streaming sdsd", Toast.LENGTH_SHORT).show();
                Log.e("AudioRecord","error-111");

                e.printStackTrace();
            }
            return null;
        }
    }
}