package com.eric.vectorassault.Input;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.eric.vectorassault.GameManager;
import com.eric.vectorassault.GameView;
import com.eric.vectorassault.MainGameActivity;
import com.eric.vectorassault.R;
import com.eric.vectorassault.Sound.SoundManager;

public class InputController
   {

      private int currentBullet = 0;
      private boolean isFiring = false;

      MainGameActivity main;
      private GameManager gameManager;
      private SoundManager soundManager;

      private RelativeLayout moveJoystickView;
      private RelativeLayout shootJoystickView;

      private JoyStickClass moveJoystick;
      private JoyStickClass shootJoystick;


      public InputController(GameView gameView)
         {
            this.gameManager = gameView.gameManager;
            this.soundManager = gameView.soundManager;

            Context context = gameView.context;

            main = (MainGameActivity) context;


            // get the movement joystick
            moveJoystickView = (RelativeLayout) main.findViewById(R.id.layout_move_joystick);
            moveJoystick = new JoyStickClass(context, moveJoystickView, R.drawable.image_button);
            moveJoystick.setStickSize(50, 50);
            moveJoystick.setLayoutSize(400, 400);
            moveJoystick.setLayoutAlpha(50);
            moveJoystick.setStickAlpha(50);
            moveJoystick.setOffset(50);
            moveJoystick.setMinimumDistance(50);
            moveJoystickView.setOnTouchListener(new View.OnTouchListener()
            {
               public boolean onTouch(View arg0, MotionEvent arg1)
                  {
                     moveJoystick.drawStick(arg1);

                     if (arg1.getAction() == MotionEvent.ACTION_DOWN || arg1.getAction() == MotionEvent.ACTION_MOVE)
                        {
                           gameManager.ship.setFacingAngle(270 - moveJoystick.getAngle());
                           gameManager.ship.setThrust(true);
                        }
                     else if (arg1.getAction() == MotionEvent.ACTION_UP)
                        {
                           gameManager.ship.setThrust(false);
                        }
                     return true;
                  }
            });

            // get the shooting joystick
            shootJoystickView = (RelativeLayout) main.findViewById(R.id.layout_shoot_joystick);
            shootJoystick = new JoyStickClass(context, shootJoystickView, R.drawable.image_button);
            shootJoystick.setStickSize(50, 50);
            shootJoystick.setLayoutSize(400, 400);
            shootJoystick.setLayoutAlpha(50);
            shootJoystick.setStickAlpha(50);
            shootJoystick.setOffset(50);
            shootJoystick.setMinimumDistance(50);
            shootJoystickView.setOnTouchListener(new View.OnTouchListener()
            {
               public boolean onTouch(View arg0, MotionEvent arg1)
                  {
                     shootJoystick.drawStick(arg1);

                     if (arg1.getAction() == MotionEvent.ACTION_DOWN || arg1.getAction() == MotionEvent.ACTION_MOVE)
                        {
                           isFiring = true;
                        }
                     else if (arg1.getAction() == MotionEvent.ACTION_UP)
                        {
                           isFiring = false;
                        }
                     return true;
                  }
            });
         }

      public void setGameManager(GameManager gm)
         {
            gameManager = gm;
         }


      public void update()
         {
            if (isFiring && gameManager.ship.pullTrigger())
               {
                  // shot at the direction of the joystick
                  gameManager.bullets[currentBullet].shoot(gameManager.ship.worldLocation, 270 - shootJoystick.getAngle());//gameManager.ship.getFacingAngle());

                  // increment the number of bullets
                  currentBullet++;

                  // If we are on the last bullet restart from the first one again
                  if (currentBullet == gameManager.numBullets)
                     {
                        currentBullet = 0;
                     }

                  // play shooting sound
                  soundManager.playSound("shoot");
               }
         }
   }
