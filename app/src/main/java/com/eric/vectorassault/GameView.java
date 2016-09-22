package com.eric.vectorassault;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.eric.vectorassault.GUI.GUIManager;
import com.eric.vectorassault.Input.InputController;
import com.eric.vectorassault.Sound.SoundManager;


/**
 * This class is used to to render to the screen and has functions to initialize the game managers
 */
public class GameView extends GLSurfaceView
   {
      public Context context;
      public SoundManager soundManager;
      public GameManager gameManager;
      public InputController inputController;
      public GUIManager guiManager;

      public GameView(Context context)
         {
            super(context);
            this.context = context;
         }

      public GameView(Context context, AttributeSet attribs)
         {
            super(context, attribs);
            this.context = context;
         }

      public void initialize(int width, int height)
         {
            // create a new sound manager
            soundManager = new SoundManager();

            // load the different sounds
            soundManager.loadSound(context);

            // create a new game manager
            guiManager = new GUIManager(this);
            inputController = new InputController(this);

            gameManager = new GameManager(this, width, height);

            guiManager.setGameManager(gameManager);
            inputController.setGameManager(gameManager);

            // set the OpenGl to version 2.0
            setEGLContextClientVersion(2);

            // Attach game renderer to the GLSurfaceView
            setRenderer(new GameRenderer(this));
         }
   }
