package com.eric.vectorassault.Timer;

import com.eric.vectorassault.GameManager;
import com.eric.vectorassault.GameView;
import com.eric.vectorassault.GUI.GUIManager;

public class GameTimer extends BaseTimer
   {

      GUIManager guiManager;
      GameManager gameManager;

      public GameTimer()
         {
            super();
         }

      public GameTimer(GameView gameView, long interval, long duration)
         {
            super(interval, duration);
            guiManager = gameView.guiManager;
            gameManager = gameView.gameManager;
         }

      @Override
      protected void onTick()
         {
            guiManager.updateTimer(getRemainingTime());
         }

      @Override
      protected void onFinish()
         {
            guiManager.showGameOver("Time has Expired!");
         }
   }