package com.eric.vectorassault.GUI;


import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eric.vectorassault.Application.GameApplication;
import com.eric.vectorassault.GameManager;
import com.eric.vectorassault.GameView;
import com.eric.vectorassault.MainGameActivity;
import com.eric.vectorassault.R;

import java.text.NumberFormat;
import java.util.Locale;


/**
 * This class is used to show/hide and update different menus and other GUI information
 */
public class GUIManager implements View.OnClickListener
   {
      private MainGameActivity main;

      private ImageButton pauseButton;

      private TextView numLivesText;
      private TextView timerText;
      private TextView enemyText;
      private TextView scoreText;
      private TextView multiplierText;

      private RelativeLayout pauseMenu;
      private RelativeLayout settingsScreen;
      private RelativeLayout restartScreen;
      private RelativeLayout quitScreen;
      private RelativeLayout gameOverScreen;

      private String[] highscoreMessages = {"Top", "2nd Best", "3rd Best", "4th Best", "5th Best"};
      private LinearLayout mainGUI;
      GameView gameView;


      private GameManager gameManager;

      public boolean reset = false;

      public GUIManager(GameView gameView)
         {
            gameManager = gameView.gameManager;

            main = (MainGameActivity) gameView.context;
            this.gameView = gameView;

            // get the pause button and set a listener
            pauseButton = (ImageButton) main.findViewById(R.id.pause_button);
            pauseButton.setOnClickListener(this);

            // get the main information textviews
            numLivesText = (TextView) main.findViewById(R.id.lives_text);
            timerText = (TextView) main.findViewById(R.id.timer_text);
            enemyText = (TextView) main.findViewById(R.id.enemies_text);
            scoreText = (TextView) main.findViewById(R.id.score_text);
            multiplierText = (TextView) main.findViewById(R.id.multiplier_text);

            mainGUI = (LinearLayout) main.findViewById(R.id.GUI);

            // get pause menu buttons
            Button resumeButton = (Button) main.findViewById(R.id.resume_button);
            Button settingsButton = (Button) main.findViewById(R.id.settings_button);
            Button restartButton = (Button) main.findViewById(R.id.restart_button);
            Button quitButton = (Button) main.findViewById(R.id.quit_button);

            // get the other menu buttons
            Button settingsBackButton = (Button) main.findViewById(R.id.settings_back_button);
            Button restartYesButton = (Button) main.findViewById(R.id.restart_yes_button);
            Button restartNoButton = (Button) main.findViewById(R.id.restart_no_button);
            Button quitYesButton = (Button) main.findViewById(R.id.quit_yes_button);
            Button quitNoButton = (Button) main.findViewById(R.id.quit_no_button);
            Button gameOverContinueButton = (Button) main.findViewById(R.id.gameover_continue_button);

            // set on click listeners
            resumeButton.setOnClickListener(this);
            settingsButton.setOnClickListener(this);
            restartButton.setOnClickListener(this);
            quitButton.setOnClickListener(this);

            // set on click listeners
            settingsBackButton.setOnClickListener(this);
            restartYesButton.setOnClickListener(this);
            restartNoButton.setOnClickListener(this);
            quitYesButton.setOnClickListener(this);
            quitNoButton.setOnClickListener(this);
            gameOverContinueButton.setOnClickListener(this);


            // get the different menu options from the layout
            pauseMenu = (RelativeLayout) main.findViewById(R.id.pause_menu);
            settingsScreen = (RelativeLayout) main.findViewById(R.id.settings_screen);
            restartScreen = (RelativeLayout) main.findViewById(R.id.restart_screen);
            quitScreen = (RelativeLayout) main.findViewById(R.id.quit_screen);
            gameOverScreen = (RelativeLayout) main.findViewById(R.id.game_over_screen);

            // hide the menu screens
            pauseMenu.setVisibility(View.GONE);
            settingsScreen.setVisibility(View.GONE);
            restartScreen.setVisibility(View.GONE);
            quitScreen.setVisibility(View.GONE);
            gameOverScreen.setVisibility(View.GONE);
         }

      public void setGameManager(GameManager gm)
         {
            gameManager = gm;
         }

      public void updateLives(final int numLives)
         {
            main.runOnUiThread(new Runnable()
               {
                  @Override
                  public void run()
                     {
                        String livesString = "x" + Integer.toString(numLives);
                        numLivesText.setText(livesString);
                     }
               });
         }

      public void updateEnemiesRemaining(final int numEnemies)
         {
            main.runOnUiThread(new Runnable()
               {
                  @Override
                  public void run()
                     {
                        String enemiesString = "Enemies: " + Integer.toString(numEnemies);
                        enemyText.setText(enemiesString);
                     }
               });
         }

      public void updateScore(final int score)
         {
            main.runOnUiThread(new Runnable()
               {
                  @Override
                  public void run()
                     {

                        String scoreString = "Score: " + NumberFormat.getNumberInstance(Locale.US).format(score);
                        scoreText.setText(scoreString);
                     }
               });
         }

      public void updateMultiplier(final int multiplier)
         {
            main.runOnUiThread(new Runnable()
               {
                  @Override
                  public void run()
                     {

                        String multiplierString = "x" + NumberFormat.getNumberInstance(Locale.US).format(multiplier);
                        multiplierText.setText(multiplierString);
                     }
               });
         }

      public void updateTimer(long timeLeft)
         {
            // get the number of minutes remaining
            final int minutes = (int) timeLeft / 1000 / 60;
            timeLeft -= minutes * 1000 * 60;

            // get the number of seconds remaing
            final int seconds = (int) timeLeft / 1000;

            main.runOnUiThread(new Runnable()
               {
                  @Override
                  public void run()
                     {
                        String timerString;

                        if (seconds < 10)
                           {
                              timerString = Integer.toString(minutes) + ":0" + Integer.toString(seconds);
                           }
                        else
                           {
                              timerString = Integer.toString(minutes) + ":" + Integer.toString(seconds);
                           }

                        timerText.setText(timerString);
                     }
               });
         }

      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.pause_button:

                     if (gameManager.isPlaying())
                        {
                           pauseGame();
                        }
                     else
                        {
                           gameManager.switchPlayingStatus();
                           gameManager.timer.resume();
                           pauseButton.setImageResource(R.drawable.ic_pause_white_36dp);
                        }
                     break;

                  case R.id.resume_button:
                     if (!gameManager.isPlaying())
                        {
                           resumeGame();
                        }
                     break;

                  case R.id.settings_button:
                     showSettings();
                     break;

                  case R.id.settings_back_button:
                     pauseMenu.setVisibility(View.VISIBLE);
                     settingsScreen.setVisibility(View.GONE);
                     break;

                  case R.id.restart_button:
                     restartGame();
                     break;

                  case R.id.restart_yes_button:
                     reset = true;
                     pauseButton.setImageResource(R.drawable.ic_play_arrow_white_36dp);
                     restartScreen.setVisibility(View.GONE);
                     mainGUI.setVisibility(View.VISIBLE);
                     gameManager.restartTimer();

                     break;

                  case R.id.restart_no_button:
                     pauseMenu.setVisibility(View.VISIBLE);
                     restartScreen.setVisibility(View.GONE);
                     break;

                  case R.id.quit_button:
                     quitGame();
                     break;

                  case R.id.quit_yes_button:
                     main.finish();
                     break;

                  case R.id.quit_no_button:
                     pauseMenu.setVisibility(View.VISIBLE);
                     quitScreen.setVisibility(View.GONE);
                     break;

                  case R.id.gameover_continue_button:
                     main.finish();
                     break;
               }
         }

      private void pauseGame()
         {
            gameManager.switchPlayingStatus();
            gameManager.timer.pause();
            pauseButton.setImageResource(R.drawable.ic_play_arrow_white_36dp);
            pauseMenu.setVisibility(View.VISIBLE);
            mainGUI.setVisibility(View.GONE);
         }

      private void resumeGame()
         {
            gameManager.switchPlayingStatus();
            gameManager.timer.resume();
            pauseButton.setImageResource(R.drawable.ic_pause_white_36dp);
            pauseMenu.setVisibility(View.GONE);
            mainGUI.setVisibility(View.VISIBLE);
         }

      private void restartGame()
         {
            pauseMenu.setVisibility(View.GONE);
            restartScreen.setVisibility(View.VISIBLE);

         }

      private void showSettings()
         {
            pauseMenu.setVisibility(View.GONE);
            settingsScreen.setVisibility(View.VISIBLE);
         }

      private void quitGame()
         {
            pauseMenu.setVisibility(View.GONE);
            quitScreen.setVisibility(View.VISIBLE);
         }

      public void showGameOver(final String message)
         {
            gameManager.switchPlayingStatus();
            main.runOnUiThread(new Runnable()
               {
                  @Override
                  public void run()
                     {
                        gameOverScreen.setVisibility(View.VISIBLE);

                        TextView text = (TextView) main.findViewById(R.id.message_text);
                        text.setText(message);

                        text = (TextView) main.findViewById(R.id.final_score_text);
                        text.setText("Score: " + gameManager.score);

                        int rank = ((GameApplication) main.getApplication()).updateHighScores(gameManager.score);

                        text = (TextView) main.findViewById(R.id.new_highscore_text);

                        if (rank != -1)
                           {
                              text.setText("New " + highscoreMessages[rank] + " High Score!");
                           }
                        else
                           {
                              text.setVisibility(View.GONE);
                           }
                     }
               });


         }
   }
