package com.example.poc_hello_world_bin;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.poc_hello_world_bin.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private int currentExecutable = 0;
    private String[] executableNames = {"hello_alex", "hello_rx", "hello_tx"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the process after 5 seconds
        handler.postDelayed(startNextExecutable, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Runnable to start the next executable
    private Runnable startNextExecutable = new Runnable() {
        @Override
        public void run() {
            if (currentExecutable < executableNames.length) {
                String executableFileName = executableNames[currentExecutable];
                File executableFile = copyAssetToFile(executableFileName);

                if (executableFile != null) {
                    // set C bin as executable
                    executableFile.setExecutable(true);

                    // Get the app's private data directory path
                    File appPrivateDir = getFilesDir();
                    String appPrivateDirPath = appPrivateDir.getAbsolutePath();

                    // Set the app's private data directory path as an environment variable
                    String[] envp = {"APP_PRIVATE_DIR=" + appPrivateDirPath};
                    Log.i("APP_PRIVATE_DIR=", appPrivateDirPath);

                    // Execute the binary asynchronously
                    executeBinaryAsync(executableFile.getAbsolutePath(), envp);
                }

                currentExecutable++;
                // Start the next executable after 5 seconds
                handler.postDelayed(startNextExecutable, 5000);
            }
        }
    };

    // Function to copy C bin from assets to files directory
    private File copyAssetToFile(String fileName) {
        try {
            InputStream inputStream = getAssets().open(fileName);
            File outputFile = new File(getFilesDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return outputFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Function to execute the binary asynchronously
    private void executeBinaryAsync(String cmd, String[] envp) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("start CMD ", cmd);
                    Process process = Runtime.getRuntime().exec(cmd, envp);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        Log.d("BinaryOutput", line);
                    }

                    while ((line = errorReader.readLine()) != null) {
                        Log.e("BinaryError", line);
                    }

                    int exitCode = process.waitFor();
                    Log.d("BinaryExitCode", String.valueOf(exitCode));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Log.d("thread.start()", cmd);
        thread.start();
    }
}
