package com.eric.vectorassault;

import android.content.Context;

import com.eric.vectorassault.Objects.Border;
import com.eric.vectorassault.Objects.Bullet;
import com.eric.vectorassault.Objects.Enemies.Box;
import com.eric.vectorassault.Objects.Enemies.Diamond;
import com.eric.vectorassault.Objects.Enemies.EnemyObject;
import com.eric.vectorassault.Objects.Enemies.Square;
import com.eric.vectorassault.Objects.Enemies.Wanderer;
import com.eric.vectorassault.Objects.Grid;
import com.eric.vectorassault.Objects.Multiplier;
import com.eric.vectorassault.Objects.SpaceShip;
import com.eric.vectorassault.ParticleSystem.ParticleManager;
import com.eric.vectorassault.Timer.GameTimer;

import java.util.Random;


/**
 * This class is used to hold all the data for the game
 */
public class GameManager
   {
      public int mapWidth = 800;
      public int mapHeight = 500;
      private boolean playing = false;

      // Player variables
      public SpaceShip ship;
      public int numLives = 3;
      public int score = 0;
      public int multiplier = 1;

      // border variables
      public Border innerBorder;
      public Border outerBorder;
      public int screenWidth;
      public int screenHeight;
      public int metersToShowX = 520;
      public int metersToShowY = 293;

      // grid variables
      public Grid grid;

      // object variables
      public Bullet[] bullets;
      public int numBullets = 20;
      public EnemyObject[] enemies;
      public int numEnemies;
      public Multiplier[] multipliers;
      public ParticleManager particleManager;

      // interface variables
      public int numEnemiesRemaining;
      public int levelNumber = 1;
      public int baseNumEnemies = 10;


      public GameTimer timer;
      Context context;
      GameView gameView;


      public GameManager(GameView gameView, int x, int y)
         {
            this.gameView = gameView;

            screenWidth = x;
            screenHeight = y;
            context = gameView.context;

            enemies = new EnemyObject[500];
            multipliers = new Multiplier[500];

            bullets = new Bullet[numBullets];

            particleManager = new ParticleManager(1024);
            timer = new GameTimer(gameView, 1000, 2 * 60 * 1000);

         }

      public void switchPlayingStatus()
         {
            playing = !playing;
         }

      public boolean isPlaying()
         {
            return playing;
         }

      public void restartTimer()
         {
            timer.cancel();
            timer = new GameTimer(gameView, 1000, 2 * 60 * 1000);
         }

      public void createObjects()
         {
            // create the ship in the center of the map
            ship = new SpaceShip(context, mapWidth / 2, mapHeight / 2);

            // create the border around the level
            innerBorder = new Border(context, mapWidth, mapHeight, 0);
            outerBorder = new Border(context, mapWidth + 20, mapHeight + 20, 10);

            // create background grid
            grid = new Grid(context, mapWidth, mapHeight, 0);

            // create some bullets to be fired
            for (int i = 0; i < numBullets; i++)
               {
                  bullets[i] = new Bullet(context, ship.getWorldLocation().x, ship.getWorldLocation().y);
               }

            // Determine the number of asteroids based on the level
            numEnemies = baseNumEnemies + 2 * levelNumber;

            // Set how many asteroids need to be destroyed by player
            numEnemiesRemaining = numEnemies;

            Random random = new Random();

            // Spawn the enemies
            for (int i = 0; i < numEnemies; i++)
               {
                  int type = random.nextInt() % 4;

                  // square enemy
                  if (type == 0)
                     {
                        enemies[i] = new Square(context, levelNumber, mapWidth, mapHeight);

                     }

                  // wanderer enemy
                  else if (type == 1)
                     {
                        enemies[i] = new Wanderer(context, levelNumber, mapWidth, mapHeight);
                     }

                  // diamond enemy
                  else if (type == 2)
                     {
                        enemies[i] = new Diamond(context, levelNumber, mapWidth, mapHeight);
                     }

                  // box enemy
                  else
                     {
                        enemies[i] = new Box(context, levelNumber, mapWidth, mapHeight);
                     }

                  multipliers[i] = new Multiplier(context);
               }
         }
   }
