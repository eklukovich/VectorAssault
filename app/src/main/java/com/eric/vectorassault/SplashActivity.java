package com.eric.vectorassault;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eric.vectorassault.Application.GameApplication;

/**
 * The first activity that is launched when the game opens. Shows a splash screen with the options
 * to start a new game or show the high scores.
 */
public class SplashActivity extends Activity implements View.OnClickListener
   {
      private RelativeLayout scoreScreen;
      private GameApplication app;
      private MediaPlayer mediaPlayer;


      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // set the layout xml
            setContentView(R.layout.activity_splash);

            // get the screen that displays the scores
            scoreScreen = (RelativeLayout) findViewById(R.id.score_screen);

            // get the buttons from the layout
            Button startButton = (Button) findViewById(R.id.start_button);
            Button scoreButton = (Button) findViewById(R.id.score_button);
            Button scoreBackButton = (Button) findViewById(R.id.score_back_button);

            // set OnClickListeners to the buttons
            startButton.setOnClickListener(this);
            scoreButton.setOnClickListener(this);
            scoreBackButton.setOnClickListener(this);

            // hide the score screen
            scoreScreen.setVisibility(View.GONE);

            // get the application
            app = (GameApplication) getApplication();

            // create a new media player for the splash screen music and set volume
            mediaPlayer = MediaPlayer.create(this, R.raw.geo_splash);
            mediaPlayer.setVolume(0.5f,0.5f);
         }

      @Override
      protected void onPause()
         {
            super.onPause();

            // pause the music when the app is paused
            pauseMainMusic();
         }

      @Override
      protected void onResume()
         {
            super.onResume();

            // start the music when the app is resumed
            startMainMusic();
         }

      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  // launch game
                  case R.id.start_button:

                     // show a loading dialog
                     app.dialog = ProgressDialog.show(this, "", "Loading. Please wait...", true);

                     // launch new game activity
                     Intent intent = new Intent(this, MainGameActivity.class);
                     this.startActivity(intent);

                     break;

                  case R.id.score_button:

                     // show the score screen and load the scores
                     scoreScreen.setVisibility(View.VISIBLE);
                     loadScores();
                     break;

                  case R.id.score_back_button:

                     // hide the score screen
                     scoreScreen.setVisibility(View.GONE);
                     break;
               }
         }

      public void loadScores()
         {
            // get textview IDs for each score
            int[] ids = {R.id.p1_score_text, R.id.p2_score_text, R.id.p3_score_text, R.id.p4_score_text, R.id.p5_score_text};

            // put score in each textview
            for (int i = 0; i < 5; i++)
               {
                  TextView scoreText = (TextView) findViewById(ids[i]);
                  scoreText.setText(Integer.toString(app.scores[i]));
               }
         }

      public void pauseMainMusic()
         {
            // pause the music if the music is playing
            if (mediaPlayer != null && mediaPlayer.isPlaying())
               {
                  mediaPlayer.pause();
               }
         }

      public void startMainMusic()
         {
            // set the music from the beginning
            mediaPlayer.seekTo(0);

            // if the music is not playing then start the music
            if (mediaPlayer != null && !mediaPlayer.isPlaying())
               {
                  mediaPlayer.start();
               }
         }
   }
