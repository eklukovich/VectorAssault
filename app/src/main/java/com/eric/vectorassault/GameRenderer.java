package com.eric.vectorassault;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.eric.vectorassault.Application.GameApplication;
import com.eric.vectorassault.Collision.CollisionDetection;
import com.eric.vectorassault.GUI.GUIManager;
import com.eric.vectorassault.Input.InputController;
import com.eric.vectorassault.Objects.Bullet;
import com.eric.vectorassault.Objects.Enemies.EnemyObject;
import com.eric.vectorassault.Objects.Enemies.SmallBox;
import com.eric.vectorassault.Objects.Multiplier;
import com.eric.vectorassault.Sound.SoundManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;


/**
 * This class is used to render and update (including collision detection) the game objects
 */
public class GameRenderer implements Renderer
   {
      Context context;

      // debug flag
      boolean debugging = true;

      // frames per second variables
      long frameCounter = 0;
      long averageFPS = 0;
      private long fps;

      // Matrix used to convert game world coordinate into a GL space coordinate (-1,-1 to 1,1) for drawing on the screen
      private final float[] viewportMatrix = new float[16];

      // manager classes
      private GameManager gm;
      private SoundManager sm;
      public GUIManager im;
      private InputController ic;

      // For capturing various PointF details without creating new objects in the speed critical areas
      PointF handyPointF;
      PointF handyPointF2;


      public GameRenderer(GameView gameView)
         {
            this.context = gameView.context;
            gm = gameView.gameManager;
            sm = gameView.soundManager;
            im = gameView.guiManager;
            ic = gameView.inputController;

            handyPointF = new PointF();
            handyPointF2 = new PointF();
         }


      @Override
      public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
         {
            // The color that will be used to clear the screen each frame in onDrawFrame()
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

            // Get our GLManager to compile and link the shaders into an object
            GLManager.setContext(context);

            // compile and link the shader programs
            GLManager.buildPrograms();

            // load the particle textures
            gm.particleManager.loadTextures(context);

            // create all of the game objects
            gm.createObjects();

            // start main game music
            sm.startMainMusic();
            ((GameApplication) (((MainGameActivity) context).getApplication())).dialog.dismiss();

         }

      @Override
      public void onSurfaceChanged(GL10 glUnused, int width, int height)
         {
            // Make full screen
            glViewport(0, 0, width, height);

            // Initialize our viewport matrix by passing in the starting
            orthoM(viewportMatrix, 0, 0, gm.metersToShowX, 0, gm.metersToShowY, 0f, 1f);
         }


      @Override
      public void onDrawFrame(GL10 glUnused)
         {
            // get the amount of time for the FPS
            long startFrameTime = System.currentTimeMillis();

            // if the game is not pause then call update
            if (gm.isPlaying())
               {
                  updateObjects(fps);
               }

            // render everything to the screen
            draw();

            // Calculate the fps this frame
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1)
               {
                  fps = 1000 / timeThisFrame;
               }

            // Output the average frames per second to the console
            if (debugging)
               {
                  frameCounter++;
                  averageFPS = averageFPS + fps;
                  if (frameCounter > 100)
                     {
                        averageFPS = averageFPS / frameCounter;
                        frameCounter = 0;
                        Log.e("averageFPS:", "" + averageFPS);
                     }
               }
         }


      private void updateObjects(long fps)
         {
            ic.update();
            if (im.reset)
               {
                  gm.levelNumber = 1;
                  gm.numLives = 3;
                  im.updateLives(gm.numLives);
                  gm.createObjects();
                  im.reset = false;
               }
            else
               {
                  // Run the ship update() method
                  gm.ship.update(fps);

                  // create exhaust particles if the ship is moving
                  if (gm.ship.isThrusting)
                     {
                        gm.particleManager.createThrustParticles(gm.ship.getWorldLocation(), gm.ship.facingAngle);
                     }

                  // update particles
                  gm.particleManager.update(gm.mapWidth, gm.mapHeight, fps);

                  // Update all the bullets
                  for (int i = 0; i < gm.numBullets; i++)
                     {
                        // If not in flight they will need the ships location
                        gm.bullets[i].update(fps, gm.ship.getWorldLocation());
                     }

                  // Update all the enemies and multipliers
                  for (int i = 0; i < gm.numEnemies; i++)
                     {
                        if (gm.enemies[i].isActive())
                           {
                              if (gm.enemies[i].getType() == EnemyObject.Type.SQUARE)
                                 {
                                    int bulletIndex = avoidBullet(gm.enemies[i].getWorldLocation());
                                    boolean avoid = (bulletIndex != -1);
                                    Bullet bullet = (avoid) ? gm.bullets[bulletIndex] : new Bullet(context, 1000, 1000);
                                    gm.enemies[i].update(fps, gm.ship.getWorldLocation(), avoid, bullet);
                                 }
                              else
                                 {
                                    gm.enemies[i].update(fps, gm.ship.getWorldLocation(), false, null);
                                 }

                           }
                        if (gm.multipliers[i].isActive())
                           {
                              gm.multipliers[i].update(fps, gm.ship.getWorldLocation());
                           }
                     }


                  // handle any collisions
                  handleCollisionDetection();
               }
         }


      private void handleCollisionDetection()
         {
            // Check if the ship collides with the level border
            // Keep ship in bounds
            if (CollisionDetection.contain(gm.mapWidth, gm.mapHeight, gm.ship.cp))
               {
                  if (gm.ship.worldLocation.x < 9)
                     {
                        gm.ship.worldLocation.x = 9;
                     }
                  if (gm.ship.worldLocation.x > gm.mapWidth - 9)
                     {
                        gm.ship.worldLocation.x = gm.mapWidth - 9;
                     }

                  if (gm.ship.worldLocation.y < 9)
                     {
                        gm.ship.worldLocation.y = 9;
                     }
                  if (gm.ship.worldLocation.y > gm.mapHeight - 9)
                     {
                        gm.ship.worldLocation.y = gm.mapHeight - 9;
                     }
               }


            // Check if the enemies collide with the level border
            // Keep enemies in bounds
            for (int i = 0; i < gm.numEnemies; i++)
               {
                  if (gm.enemies[i].isActive())
                     {
                        if (CollisionDetection.contain(gm.mapWidth, gm.mapHeight, gm.enemies[i].cp))
                           {
                              // Bounce the asteroid back into the game
                              gm.enemies[i].bounce();
                              // Play a sound
                              sm.playSound("blip");
                           }
                     }
               }

            // Check if the bullets collide with the level border
            // make bullets explode
            for (int i = 0; i < gm.numBullets; i++)
               {
                  // check if the bullet in flight
                  if (gm.bullets[i].isInFlight())
                     {
                        handyPointF = gm.bullets[i].getWorldLocation();
                        handyPointF2 = gm.ship.getWorldLocation();

                        if (handyPointF.x > handyPointF2.x + gm.metersToShowX / 2)
                           {
                              // reset the bullet
                              gm.bullets[i].resetBullet(gm.ship.getWorldLocation());
                           }
                        else if (handyPointF.x < handyPointF2.x - gm.metersToShowX / 2)
                           {
                              // reset the bullet
                              gm.bullets[i].resetBullet(gm.ship.getWorldLocation());
                           }
                        else if (handyPointF.y > handyPointF2.y + gm.metersToShowY / 2)
                           {
                              // reset the bullet
                              gm.bullets[i].resetBullet(gm.ship.getWorldLocation());
                           }
                        else if (handyPointF.y < handyPointF2.y - gm.metersToShowY / 2)
                           {
                              // reset the bullet
                              gm.bullets[i].resetBullet(gm.ship.getWorldLocation());
                           }

                        // does bullet need containing?
                        if (CollisionDetection.contain(gm.mapWidth, gm.mapHeight, gm.bullets[i].cp))
                           {
                              // show explosion
                              gm.particleManager.createExplosionParticles(gm.bullets[i].getWorldLocation(), 150, 30);

                              // Reset the bullet
                              gm.bullets[i].resetBullet(gm.ship.getWorldLocation());

                              // Play a sound
                              sm.playSound("ricochet");
                           }
                     }
               }

            // check if a bullet and enemy collided
            for (int bulletNum = 0; bulletNum < gm.numBullets; bulletNum++)
               {
                  for (int asteroidNum = 0; asteroidNum < gm.numEnemies; asteroidNum++)
                     {
                        // check that the bullet is in flight and the enemy is active before proceeding
                        if (gm.bullets[bulletNum].isInFlight() && gm.enemies[asteroidNum].isActive())
                           {
                              // perform the collision checks
                              if (CollisionDetection.detect(gm.bullets[bulletNum].cp, gm.enemies[asteroidNum].cp))
                                 {
                                    // destroy the enemy
                                    destroyEnemy(asteroidNum);

                                    // Reset the bullet
                                    gm.bullets[bulletNum].resetBullet(gm.ship.getWorldLocation());
                                 }
                           }
                     }
               }

            // check if the ship and an enemy collided
            for (int asteroidNum = 0; asteroidNum < gm.numEnemies; asteroidNum++)
               {
                  // Is the enemy active before proceeding
                  if (gm.enemies[asteroidNum].isActive())
                     {
                        // perform the collision checks
                        if (CollisionDetection.detect(gm.ship.cp, gm.enemies[asteroidNum].cp))
                           {
                              // destroy the enemy
                              destroyEnemy(asteroidNum);

                              // lose a life and handle ship reset
                              lifeLost();
                           }
                     }
               }

            // check if the player collided with a score multiplier
            for (int multiplierNum = 0; multiplierNum < gm.numEnemies; multiplierNum++)
               {
                  // check if multiplier is active
                  if (gm.multipliers[multiplierNum].isActive())
                     {
                        // check for collision
                        if (CollisionDetection.detect(gm.ship.cp, gm.multipliers[multiplierNum].cp))
                           {
                              // turn off multiplier
                              gm.multipliers[multiplierNum].setActive(false);

                              // increase score multiplier and update interface
                              gm.multiplier++;
                              im.updateMultiplier(gm.multiplier);
                           }
                     }
               }
         }

      private void draw()
         {
            // get the location of the ship
            handyPointF = gm.ship.getWorldLocation();

            // create the viewport matrix based on the ship's location
            orthoM(viewportMatrix, 0,
                handyPointF.x - gm.metersToShowX / 2,
                handyPointF.x + gm.metersToShowX / 2,
                handyPointF.y - gm.metersToShowY / 2,
                handyPointF.y + gm.metersToShowY / 2,
                0f, 1f);

            // Clear the screen
            glClear(GL_COLOR_BUFFER_BIT);

            // draw all of the particles particles
            gm.particleManager.draw(viewportMatrix);

            gm.grid.draw(viewportMatrix);

            // draw the bullets
            for (int i = 0; i < gm.numBullets; i++)
               {
                  gm.bullets[i].draw(viewportMatrix);
               }

            // draw the enemies
            for (int i = 0; i < gm.numEnemies; i++)
               {
                  if (gm.enemies[i].isActive())
                     {
                        gm.enemies[i].draw(viewportMatrix);
                     }
                  if (gm.multipliers[i].isActive())
                     {
                        gm.multipliers[i].draw(viewportMatrix);
                     }
               }

            // draw the ship
            gm.ship.draw(viewportMatrix);

            // draw the level border
            gm.innerBorder.draw(viewportMatrix);
            gm.outerBorder.draw(viewportMatrix);

         }

      public void lifeLost()
         {
            // reset the ship to the center
            gm.ship.setWorldLocation(gm.mapWidth / 2, gm.mapHeight / 2);

            // play a sound
            sm.playSound("shipexplode");

            // decrease the number of lives
            gm.numLives = gm.numLives - 1;
            im.updateLives(gm.numLives);

            // check if the player is out of lives
            if (gm.numLives == 0)
               {
                  // show game over screen
                  im.showGameOver("Ran Out of Lives!");
               }
         }

      public void destroyEnemy(int enemyIndex)
         {
            // show explosion
            gm.particleManager.createExplosionParticles(gm.enemies[enemyIndex].getWorldLocation(), 220, 120);

            // mark the enemy as inactive
            gm.enemies[enemyIndex].setActive(false);

            // play the explosion sound
            sm.playSound("explode");

            // check if the enemy was a box
            if (gm.enemies[enemyIndex].getType() == EnemyObject.Type.BOX)
               {
                  // spawn three smaller box enemies
                  for (int i = 0; i < 3; i++)
                     {
                        gm.enemies[gm.numEnemies + i] = new SmallBox(context, gm.enemies[enemyIndex].worldLocation);
                        gm.multipliers[gm.numEnemies + i] = new Multiplier(context);
                     }

                  gm.numEnemies += 3;
                  gm.numEnemiesRemaining += 3;
               }

            // reduce the number of active enemies and update the interface
            gm.numEnemiesRemaining--;
            im.updateEnemiesRemaining(gm.numEnemiesRemaining);

            // calculate score and update the interface
            gm.score = gm.score + gm.enemies[enemyIndex].scoreValue * gm.multiplier;
            im.updateScore(gm.score);

            // increment the multiplier and update the interface
            gm.multipliers[enemyIndex].initialize(gm.enemies[enemyIndex].getWorldLocation().x, gm.enemies[enemyIndex].getWorldLocation().y);


            // check if all of the enemies were destroyed
            if (gm.numEnemiesRemaining == 0)
               {
                  // Increment the level number
                  gm.levelNumber++;

                  // give an extra life
                  gm.numLives++;
                  im.updateLives(gm.numLives);

                  // play sound for next level
                  sm.playSound("nextlevel");

                  // Respawn everything
                  gm.createObjects();
                  im.updateEnemiesRemaining(gm.numEnemiesRemaining);
               }
         }

      // AI for enemies to dodge bullets
      int avoidBullet(PointF enemyPos)
         {
            int range = 40;

            float x = enemyPos.x;
            float y = enemyPos.y;

            for (int i = 0; i < gm.numBullets; i++)
               {
                  float bX = gm.bullets[i].getWorldLocation().x;
                  float bY = gm.bullets[i].getWorldLocation().y;

                  if (bX > (x - range) && bX < (x + range) && bY > (y - range) && bY < (y + range))
                     {
                        return i;
                     }
               }
            return -1; // no need to avoid
         }
   }

