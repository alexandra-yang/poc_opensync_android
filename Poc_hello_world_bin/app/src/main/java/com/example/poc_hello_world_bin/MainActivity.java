package com.example.poc_hello_world_bin;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
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
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // copy C bin in assets to files, note: temporarily hard coded
            String executableFileName = "hello_alex";
            File executableFile = copyAssetToFile(executableFileName);

            if (executableFile != null) {
                // set C bin as executable
                executableFile.setExecutable(true);

                // execute C bin
                Process process = Runtime.getRuntime().exec(executableFile.getAbsolutePath());

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d("BinaryOutput", line);
                }

                int exitCode = process.waitFor();
                Log.d("BinaryExitCode", String.valueOf(exitCode));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
