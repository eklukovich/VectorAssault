package com.eric.vectorassault.Objects;

import android.content.Context;
import android.opengl.GLES20;

import com.eric.vectorassault.R;
import com.eric.vectorassault.Texture;

import java.util.ArrayList;

import static android.opengl.GLES20.glUseProgram;

public class Grid extends GameObject
   {
      private Texture texture;
      private ArrayList<GridLine> gridLines = new ArrayList<>();

      public Grid(Context context, float mapWidth, float mapHeight, int offset)
         {
            setType(Type.BORDER);

            setObjectShaderProgram();

            int verticalLineDistance = 10;
            int horizontalLineDistance = 10;

            int verticalStart = (int) -mapWidth / 2;
            int horizontalStart = (int) -mapHeight / 2;

            for (int i = verticalStart; i < mapWidth/2; i += verticalLineDistance)
               {
                  gridLines.add(new GridLine(mapWidth, mapHeight, i, -mapHeight/2, i, mapHeight/2));
               }

            for (int i = horizontalStart; i < mapHeight/2; i += horizontalLineDistance)
               {
                  gridLines.add(new GridLine(mapWidth, mapHeight, -mapWidth/2, i, mapWidth/2, i));
               }


            texture = new Texture(context, R.drawable.grid);
         }

      public void draw(float[] viewportMatrix)
         {
            // tell OpenGl to use the glProgram
            glUseProgram(glProgram);

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

            for (int i = 0; i < gridLines.size(); i++)
               {
                  gridLines.get(i).draw(texture, viewportMatrix);
               }
            GLES20.glDisable(GLES20.GL_BLEND);

         }

   }
