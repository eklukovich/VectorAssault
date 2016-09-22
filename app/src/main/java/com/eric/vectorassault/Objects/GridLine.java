package com.eric.vectorassault.Objects;

import android.opengl.GLES20;

import com.eric.vectorassault.GLManager;
import com.eric.vectorassault.Texture;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setRotateM;
import static android.opengl.Matrix.translateM;

public class GridLine extends GameObject
   {
      private Texture texture;


      public GridLine(float mapWidth, float mapHeight, float startX, float startY, float endX, float endY)
         {

            setType(Type.GRID_LINE);
            //border centre is the exact centre of map
            setWorldLocation(mapWidth / 2, mapHeight / 2);


            float w = mapWidth;
            float h = mapHeight;
            setSize(w, h);

            // The vertices of the border represent four lines
            float[] lineVertices = new float[]{
                                                    // A line from point 1 to point 2
                                                    startX, startY, 0,
                                                    endX, endY, 0
            };

            float[] textCoords = new float[]{
                                                0.0f, 0.0f,

            };

            setVertices(lineVertices);
            setTextureCoords(textCoords);

           // setObjectShaderProgram();

         }

      public void draw(Texture texture, float[] viewportMatrix)
         {
            // tell OpenGl to use the glProgram
            //glUseProgram(glProgram);

            // Set vertices to the first byte
            vertices.position(0);
            textCoords.position(0);

            glVertexAttribPointer(GLManager.objectShaderLocations.get("a_Position"), COMPONENTS_PER_POSITION, GL_FLOAT, false, POSITION_STRIDE, vertices);
            //glVertexAttribPointer(GLManager.objectShaderLocations.get("a_Texture"), COMPONENTS_PER_TEXTURE, GL_FLOAT, false, TEXTURE_STRIDE, textCoords);

            glEnableVertexAttribArray(GLManager.objectShaderLocations.get("a_Position"));
           // glEnableVertexAttribArray(GLManager.objectShaderLocations.get("a_Texture"));


            // reset the matrix to the identity matrix
            setIdentityM(modelMatrix, 0);

            // set the translation
            translateM(modelMatrix, 0, worldLocation.x, worldLocation.y, 0);
            multiplyMM(viewportModelMatrix, 0, viewportMatrix, 0, modelMatrix, 0);

            // set the rotation
            setRotateM(modelMatrix, 0, facingAngle, 0, 0, 1.0f);
            multiplyMM(rotateViewportModelMatrix, 0, viewportModelMatrix, 0, modelMatrix, 0);

            // Give the matrix to OpenGL
            glUniformMatrix4fv(GLManager.objectShaderLocations.get("u_Matrix"), 1, false, rotateViewportModelMatrix, 0);
            GLES20.glUniform1i(GLManager.objectShaderLocations.get("gSampler"), 0);

            // bind the texture
            texture.bindTexture(GLES20.GL_TEXTURE0);

            glLineWidth(1.0f);

            // render the border
            glDrawArrays(GL_LINES, 0, numVertices);
         }

   }
