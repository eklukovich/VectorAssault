package com.eric.vectorassault.Application;

import android.app.Application;
import android.app.ProgressDialog;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


/**
 * This application class is part of a global scope and has function to load, update, and save high scores.
 */
public class GameApplication extends Application
   {
      private final String scoreFile = Environment.getExternalStorageDirectory() + "/Android/data/com.vectorassault/scores.txt";
      public ProgressDialog dialog;

      public int [] scores;


      @Override
      public void onCreate()
         {
            super.onCreate();

            // allocate memory for best five high scores
            scores = new int[5];

            // load the high scores from a file
            loadHighScores();
         }

      public void loadHighScores()
         {
            File file = new File(scoreFile);

            try
               {
                  Scanner scanner = new Scanner(file);
                  int i = 0;
                  while(scanner.hasNextInt())
                     {
                        scores[i] = scanner.nextInt();
                        i++;
                     }
               }
            catch (IOException e)
               {
                  e.printStackTrace();
               }
         }

      public void saveHighScores()
         {
            File storageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.vectorassault");
            if (!storageDir.exists())
               {
                  storageDir.mkdirs();
               }
            try
               {
                  BufferedWriter writer = new BufferedWriter(new FileWriter(scoreFile));
                  for(int i = 0; i < 5; i++)
                     {
                        writer.write(Integer.toString(scores[i]));
                        writer.newLine();
                     }

                  writer.close();
               }
            catch (IOException e)
               {
                  e.printStackTrace();
               }

         }

      public int updateHighScores(int newScore)
         {
            for(int i = 0; i < 5; i++)
               {
                  if (newScore > scores[i])
                     {
                        scores[i] = newScore;
                        saveHighScores();
                        return i;
                     }
               }
            return -1;
         }
   }
