package com.eric.vectorassault.Sound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.eric.vectorassault.R;

import java.io.IOException;


/**
 * This class is used to load and play sound effects for different game events.
 */
public class SoundManager
   {
      private SoundPool soundPool;
      private int shoot = -1;
      private int thrust = -1;
      private int explode = -1;
      private int shipExplode = -1;
      private int ricochet = -1;
      private int blip = -1;
      private int nextLevel = -1;
      private int gameOver = -1;

      private MediaPlayer mediaPlayer;

      public void loadSound(Context context)
         {
            AudioAttributes attributes = new AudioAttributes.Builder()
                                             .setUsage(AudioAttributes.USAGE_GAME)
                                             .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                             .build();

            soundPool = new SoundPool.Builder()
                            .setMaxStreams(20)
                            .setAudioAttributes(attributes)
                            .build();
            try
               {
                  //Create objects of the 2 required classes
                  AssetManager assetManager = context.getAssets();
                  AssetFileDescriptor descriptor;

                  mediaPlayer = MediaPlayer.create(context, R.raw.geo_game);
                  mediaPlayer.setVolume(0.5f,0.5f);

                  //create our fx
                  descriptor = assetManager.openFd("shoot-04.wav");
                  shoot = soundPool.load(descriptor, 0);

                  descriptor = assetManager.openFd("thrust.ogg");
                  thrust = soundPool.load(descriptor, 0);

                  descriptor = assetManager.openFd("explode.ogg");
                  explode = soundPool.load(descriptor, 0);

                  descriptor = assetManager.openFd("shipexplode.ogg");
                  shipExplode = soundPool.load(descriptor, 0);

                  descriptor = assetManager.openFd("ricochet.ogg");
                  ricochet = soundPool.load(descriptor, 0);

                  descriptor = assetManager.openFd("blip.ogg");
                  blip = soundPool.load(descriptor, 0);

                  descriptor = assetManager.openFd("nextlevel.ogg");
                  nextLevel = soundPool.load(descriptor, 0);

                  descriptor = assetManager.openFd("gameover.ogg");
                  gameOver = soundPool.load(descriptor, 0);

               }
            catch (IOException e)
               {
                  //Print an error message to the console
                  Log.e("error", "failed to load sound files");
               }
         }

      public void playSound(String sound)
         {
            switch (sound)
               {
                  case "shoot":
                     soundPool.play(shoot, 0.1f, 0.1f, 1, 0, 1);
                     break;

                  case "thrust":
                     soundPool.play(thrust, 1, 1, 1, 0, 1);
                     break;

                  case "explode":
                     soundPool.play(explode, 1, 1, 1, 0, 1);
                     break;

                  case "shipexplode":
                     soundPool.play(shipExplode, 1, 1, 0, 0, 1);
                     break;

                  case "ricochet":
                     soundPool.play(ricochet, 1, 1, 1, 0, 1);
                     break;

                  case "blip":
                     soundPool.play(blip, 1, 1, 1, 0, 1);
                     break;

                  case "nextlevel":
                     soundPool.play(nextLevel, 1, 1, 1, 0, 1);
                     break;

                  case "gameover":
                     soundPool.play(gameOver, 1, 1, 1, 0, 1);
                     break;
               }
         }

      public void pauseMainMusic()
         {
            if(mediaPlayer != null && mediaPlayer.isPlaying())
               {
                  mediaPlayer.pause();
               }
         }

      public void startMainMusic()
         {
            if(mediaPlayer != null && !mediaPlayer.isPlaying())
               {
                  mediaPlayer.start();
               }
         }
   }
