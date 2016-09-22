package com.eric.vectorassault.Objects;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import com.eric.vectorassault.Collision.CollisionPackage;
import com.eric.vectorassault.GLManager;
import com.eric.vectorassault.R;
import com.eric.vectorassault.Texture;

import java.util.Random;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setRotateM;
import static android.opengl.Matrix.translateM;

public class Multiplier extends GameObject
   {
      public CollisionPackage cp;
      public PointF[] collisionPoints;
      private Texture texture;

      int timeToLive = 0;

      public Multiplier(Context context)
         {
            super();

            texture = new Texture(context, R.drawable.multiplier_glow);

            setType(Type.MULTIPLIER);

            isActive = false;

            float width = 15;
            float length = 15;

            setSize(width, length);


            float halfW = width / 2;
            float halfL = length / 2;

            float[] bulletVertices = new float[]{
                                                    -halfW, -halfL, 0,
                                                    halfW, -halfL, 0,
                                                    -halfW, halfL, 0,
                                                    halfW, halfL, 0
            };

            float[] textCoords = new float[]{
                                                0.0f, 0.0f,
                                                0.0f, 1.0f,
                                                1.0f, 0.0f,
                                                1.0f, 1.0f
            };

            setVertices(bulletVertices);
            setTextureCoords(textCoords);


            // Initialize the collision package (the object space vertex list, x any world location the largest possible radius, facingAngle)
            collisionPoints = new PointF[4];
            collisionPoints[0] = new PointF(-halfW, -halfL);
            collisionPoints[1] = new PointF(-halfW, halfL);
            collisionPoints[2] = new PointF(halfW, -halfL);
            collisionPoints[3] = new PointF(0, 0 + halfL);

            cp = new CollisionPackage(collisionPoints, getWorldLocation(), halfL, halfW, length / 2, getFacingAngle());

            setObjectShaderProgram();

         }

      public void initialize(float x, float y)
         {
            setWorldLocation(x, y);

            Random r = new Random();
            // set a random rotation rate in degrees per second
            setRotationRate(r.nextInt(50) + 10);

            // travel at any random angle
            setTravellingAngle(r.nextInt(360));

            setSpeed(r.nextInt(25) + 1);

            setMaxSpeed(140);

            isActive = true;
            timeToLive = 175;
         }

      public void update(long fps, PointF shipLocation)
         {
            timeToLive--;

            if (timeToLive != 0)
               {
                  // Set the velocity if bullet in flight
                  setxVelocity((float) (getSpeed() * Math.cos(Math.toRadians(getFacingAngle() + 90))));
                  setyVelocity((float) (getSpeed() * Math.sin(Math.toRadians(getFacingAngle() + 90))));


                  move(fps);

                  // Update the collision package
                  cp.facingAngle = getFacingAngle() + 90;
                  cp.worldLocation = getWorldLocation();
               }
            else
               {
                  isActive = false;
               }
         }

      public void draw(float[] viewportMatrix)
         {
            if (timeToLive > 100 || (timeToLive < 100 && timeToLive % 6 == 0))
               {
                  // tell OpenGl to use the glProgram
                  glUseProgram(glProgram);

                  // Set vertices to the first byte
                  vertices.position(0);
                  textCoords.position(0);

                  glVertexAttribPointer(GLManager.objectShaderLocations.get("a_Position"), COMPONENTS_PER_POSITION, GL_FLOAT, false, POSITION_STRIDE, vertices);
                  glVertexAttribPointer(GLManager.objectShaderLocations.get("a_Texture"), COMPONENTS_PER_TEXTURE, GL_FLOAT, false, TEXTURE_STRIDE, textCoords);

                  glEnableVertexAttribArray(GLManager.objectShaderLocations.get("a_Position"));
                  glEnableVertexAttribArray(GLManager.objectShaderLocations.get("a_Texture"));

                  // Translate model coordinates into world coordinates
                  // Make an identity matrix to base our future calculations on
                  // Or we will get very strange results
                  setIdentityM(modelMatrix, 0);
                  // Make a translation matrix


                  translateM(modelMatrix, 0, worldLocation.x, worldLocation.y, 0);

                  // Combine the model with the viewport into a new matrix
                  multiplyMM(viewportModelMatrix, 0, viewportMatrix, 0, modelMatrix, 0);

                  setRotateM(modelMatrix, 0, facingAngle, 0, 0, 1.0f);

                  // And multiply the rotation matrix into the model-viewport matrix
                  multiplyMM(rotateViewportModelMatrix, 0, viewportModelMatrix, 0, modelMatrix, 0);

                  // Give the matrix to OpenGL
                  glUniformMatrix4fv(GLManager.objectShaderLocations.get("u_Matrix"), 1, false, rotateViewportModelMatrix, 0);
                  GLES20.glUniform1i(GLManager.objectShaderLocations.get("gSampler"), 0);


                  texture.bindTexture(GLES20.GL_TEXTURE0);

                  // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
                  GLES20.glEnable(GLES20.GL_BLEND);
                  GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
                  glDrawArrays(GL_TRIANGLE_STRIP, 0, numVertices);
                  GLES20.glDisable(GLES20.GL_BLEND);
               }
         }

   }
