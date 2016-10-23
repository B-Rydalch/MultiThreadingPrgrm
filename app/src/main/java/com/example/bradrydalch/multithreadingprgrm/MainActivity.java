package com.example.bradrydalch.multithreadingprgrm;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
 import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    String numbers = "";
    private int progress = 0;
    private ListView listView;
    private Button loadButton;
    private Button clearButton;
    private Button createButton;
    ArrayAdapter<String> adapter;
    FileOutputStream outputStream;
    private Context context = this;
    private ProgressBar progressBar;
    String fileName = "numbers.txt";
    private Handler mHandler = new Handler();


    private synchronized void setProgressBar(int progress) {
        this.progress = Math.min(100, progress);
    }

    private synchronized int getProgressBar() {
        return this.progress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.fileList);
        loadButton = (Button)findViewById(R.id.loadButton);
        clearButton = (Button)findViewById(R.id.clearButton);
        createButton = (Button)findViewById(R.id.createButton);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

    }

    List<String> theList;


    public void createButton(View v) {

        progressBar.setVisibility(View.VISIBLE);

        // perform in background thread
        Thread backgroundThread = new Thread(new Runnable() {
            public void run() {
                try {
                    File file = new File(context.getFilesDir(), fileName);

                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    setProgressBar(0);

                    for (int i = 0; i < 10; i++) {

                        numbers += ((i + 1) + "\n");

                        setProgressBar(getProgressBar() + 10);

                        mHandler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(getProgressBar());
                            }
                        });

                        Thread.sleep(250);
                    }

                    outputStream = context.openFileOutput(fileName, context.MODE_PRIVATE);
                    outputStream.write(numbers.getBytes());
                    outputStream.close();
                    progressBar.setVisibility(View.INVISIBLE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        backgroundThread.start();
    }

    public void loadButton(View v) {
        theList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);

        final File file = new File(context.getFilesDir(), fileName);
        Thread displayThread = new Thread(new Runnable() {
            public void run() {
                try {

                    setProgressBar(0);
                    Scanner scan = new Scanner(file);

                    while (scan.hasNextLine()) {
                        String line = scan.nextLine();

                        theList.add(line);

                        setProgressBar(getProgressBar() + 10);

                        mHandler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(getProgressBar());
                            }
                        });

                        Thread.sleep(250);
                    }

                    /* // progress bar disappears after reaching 100%
                       for some reason it causes problems with the other functions?
                    if (progress == 100){
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    */
                    scan.close();


                    mHandler.post(new Runnable() {
                        public void run() {
                            adapter.clear();
                            adapter.addAll(theList);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        displayThread.start();
    }

    public void clearButton(View v)
    {
        setProgressBar(0);
        numbers = "";
        progressBar.setVisibility(View.INVISIBLE);

        mHandler.post(new Runnable() {
            public void run() {
                progressBar.setProgress(getProgressBar());
            }
        });

        theList.clear();
        adapter.clear();
    }
}