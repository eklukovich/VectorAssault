package com.eric.vectorassault;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MainGameActivity extends Activity
   {
      private GameView gameView;

      @Override
      public void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // Get a Display object to access screen details
            Display display = getWindowManager().getDefaultDisplay();

            // Load the resolution into a Point object
            Point resolution = new Point();
            display.getSize(resolution);
            int screenX = resolution.x;
            int screenY = resolution.y;

            // set the view to the xml
            setContentView(R.layout.activity_fullscreen);

            // get the glsurfaceview from the XML
            gameView = (GameView) findViewById(R.id.game_screen);

            // initialize the game view
            gameView.initialize(screenX, screenY);

         }

      @Override
      protected void onPause()
         {
            super.onPause();

            // pause the game
            gameView.onPause();

            // pause music
            gameView.soundManager.pauseMainMusic();

            // pause the game timer
            gameView.gameManager.timer.pause();
         }

      @Override
      protected void onResume()
         {
            super.onResume();

            // resume the game
            gameView.onResume();

            // resume the music
            gameView.soundManager.startMainMusic();
         }
   }