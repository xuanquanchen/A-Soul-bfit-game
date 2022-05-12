package com.example.dk.bfit1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity{

    int n=0;
    int k=0;    //log value
    boolean bool1 = true;
    TextView scores;    //show how many ava do you hit
    Button[] buttons = new Button[48];  //button
    public int[] sleepTimeList = {3000,2000,1000,800,600,400,200}; //ava appear time list
    public int sleepTime = 3000;    //ava appear time,
    public Thread myThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //the scores you have
        scores = (TextView) findViewById(R.id.scores);

        //the array to identify the buttons
        for (int j = 0; j < 48; j++) {
            String buttonID = "a" + j;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[j] = ((Button) findViewById(resID));
        }

        //spinner create and run
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.levelArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sleepTime = sleepTimeList[position];
                Toast.makeText(MainActivity.this,"Now you are playing level " + (position+1) + ", Ava will appear for every "+ sleepTime + " millisecond", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Guide();
    }

    public void Guide() {
        //showcase
        final SharedPreferences tutorialShowcases = getSharedPreferences("showcaseTutorial", MODE_PRIVATE);

        boolean run;

        run = tutorialShowcases.getBoolean("run?", true);

        if (run) {
            final ViewTarget startButton = new ViewTarget(R.id.startButton , this);//Variable holds the item that the showcase will focus on.
            final ViewTarget square = new ViewTarget(R.id.a12 , this);
            final ViewTarget scores = new ViewTarget(R.id.scores , this);

            //This code creates a new layout parameter so the button in the showcase can move to a new spot.
            final RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // This aligns button to the bottom left side of screen
            lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
            // Set margins to the button, we add 16dp margins here
            int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
            lps.setMargins(margin, margin, margin, margin);

            final ShowcaseView guideToStart = new ShowcaseView.Builder(this)
                    .withHoloShowcase()
                    .setStyle(2)
                    .setTarget(new ViewTarget(findViewById(R.id.startButton)))
                    .setContentTitle("guideToStart")
                    .setContentText("Click this button to start")
                    .hideOnTouchOutside()
                    .build();

            final ShowcaseView guideToPlay = new ShowcaseView.Builder(MainActivity.this)
                    .withHoloShowcase()
                    .setStyle(2)
                    .setTarget(new ViewTarget(findViewById(R.id.a12)))
                    .setContentTitle("guideToPlay")
                    .setContentText("Please hit Ava when she appears in these squares")
                    .hideOnTouchOutside()
                    .build();

            guideToPlay.hide();

            final ShowcaseView guideToGetYourScores = new ShowcaseView.Builder(MainActivity.this)
                    .withMaterialShowcase()
                    .setStyle(2)
                    .setTarget(new ViewTarget(findViewById(R.id.scores)))
                    .setContentTitle("guideToGetYourScores")
                    .setContentText("Your scores will show in here")
                    .hideOnTouchOutside()
                    .build();

            guideToGetYourScores.hide();


            guideToStart.overrideButtonClick(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    guideToStart.hide();
                    guideToPlay.show();
                }
            });

            guideToPlay.overrideButtonClick(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    guideToPlay.hide();
                    guideToGetYourScores.show();
                }
            });

            guideToGetYourScores.overrideButtonClick(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    guideToGetYourScores.hide();
                }
            });

        }
    }


    public void onIntemSelected(AdapterView<?> adapterView, View view, int i, long l){
        String[] levelArray = getResources().getStringArray(R.array.levelArray);
        Toast.makeText(this,levelArray[i],Toast.LENGTH_LONG).show();
    }

    //to check whether you hit ava
    public void check(View view){

        Log.d("checkButton","the button clicked "+((Button)view).getId());
        Log.d("checkButton","the value of n "+n);
        if(buttons[n].getId()==((Button)view).getId()){
            k++;
            Log.d("checkButton","the value of k"+k);
            view.setBackground(getResources().getDrawable(R.drawable.ava_hit));
            MediaPlayer hitSound = MediaPlayer.create(getApplicationContext(), R.raw.hit_sound);
            if(!hitSound.isPlaying()){
                hitSound.start();
            }
            else ;
        }
        else
            view.setBackgroundColor(0xffbd7d74);

        scores.setText("0"+k);
    }


    private class runGame implements Runnable {
        //mutiThread implement
        @Override
        public void run() {

            int i = 0;
            for (i = 0; i < 80; i++) {
                n = (int) (Math.random() * 47 + 1);
                final int finalN = n;

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //buttons[finalN].setBackgroundColor(Color.GREEN);
                                    buttons[finalN].setBackground(getResources().getDrawable
                                            (R.drawable.ava_not_hit));
                                    Log.d("createSquare", "main" + finalN);
                                    Log.d("createSquare", "main id" + buttons[finalN].getId());

                                }
                            });

                        }
                    });

                    Thread.sleep(sleepTime); ///the restart time to create the next

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[finalN].setBackgroundColor(Color.WHITE);
                            Log.d("createSquare", "main after" + finalN);
                        }
                    });
                    Log.d("createSquare", "Button icon is" + n);
                } catch (InterruptedException ex) {
                    Log.d("createSquare", "error at " + ex);
                }
            }

        }


    }

    //for the button to start the game
    public void runTheGame (View view){
        myThread = new Thread(new runGame());
        myThread.start();
    }

    public void stopTheGame(View view) {
        myThread.interrupted();
    }

    //to create a menu
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    //set situation when options in the menu are clicked
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case R.id.about:
                getIntent().addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(new Intent(this, About.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}