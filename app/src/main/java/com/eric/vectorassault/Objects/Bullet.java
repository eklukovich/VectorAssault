package com.eric.vectorassault.Objects;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import com.eric.vectorassault.Collision.CollisionPackage;
import com.eric.vectorassault.GLManager;
import com.eric.vectorassault.R;
import com.eric.vectorassault.Texture;

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

public class Bullet extends GameObject
   {

      private boolean inFlight = false;
      public CollisionPackage cp;
      public PointF[] collisionPoints;
      private Texture texture;

      public Bullet(Context context, float shipX, float shipY)
         {
            super();

            texture = new Texture(context, R.drawable.bullet);

            setType(Type.BULLET);

            //setWorldLocation(shipX, shipY);

            setWorldLocation(-20.0f,-20.0f);

            float width = 4;
            float length = 16;

            setSize(width, length);


            float halfW = width / 2;
            float halfL = length / 2;

            // Define the space ship shape
            // as a triangle from point to point
            // in anti clockwise order
            float[] bulletVertices = new float[]{
                                                  -halfW, -halfL, 0,
                                                  halfW, -halfL, 0,
                                                  -halfW, halfL, 0,
                                                  halfW, halfL, 0
            };

            float[] textCoords = new float[]{
                                                0.0f, 0.0f,
                                                0.0f, 1.0f,
                                                1.0f,  0.0f,
                                                1.0f,  1.0f
            };

            setVertices(bulletVertices);
            setTextureCoords(textCoords);


            // Initialize the collision package (the object space vertex list, x any world location the largest possible radius, facingAngle)
            collisionPoints = new PointF[4];
            collisionPoints[0] = new PointF(-halfW, -halfL);
            collisionPoints[1] = new PointF(-halfW, halfL);
            collisionPoints[2] = new PointF(halfW, -halfL);
            collisionPoints[3] = new PointF(0, 0 + halfL);

            cp = new CollisionPackage(collisionPoints, getWorldLocation(), halfL, halfW,  length/2, getFacingAngle());

            setObjectShaderProgram();

         }

      public void shoot(PointF shipLocation, float shipFacingAngle)
         {
            setFacingAngle(shipFacingAngle);
            inFlight = true;
            setSpeed(300);

            worldLocation.x = shipLocation.x + 20.0f * -1 *(float)Math.sin(facingAngle*Math.PI/180.0);
            worldLocation.y = shipLocation.y + 20.0f * (float)Math.cos(facingAngle * Math.PI / 180.0);
         }

      public void resetBullet(PointF shipLocation)
         {
            // Remove the velocity if bullet out of bounds
            inFlight = false;
            setxVelocity(0);
            setyVelocity(0);
            setSpeed(0);
           // setWorldLocation(shipLocation.x, shipLocation.y);
            setWorldLocation(-20.0f, -20.0f);


         }

      public boolean isInFlight()
         {
            return inFlight;
         }

      public void update(long fps, PointF shipLocation)
         {
            // Set the velocity if bullet in flight
            if (inFlight)
               {
                  setxVelocity((float) (getSpeed() * Math.cos(Math.toRadians(getFacingAngle() + 90))));
                  setyVelocity((float) (getSpeed() * Math.sin(Math.toRadians(getFacingAngle() + 90))));
               }
            else
               {
                  // Have it sit inside the ship
                  setWorldLocation(-20.0f, -20.0f);
               }


            move(fps);

            // Update the collision package
            cp.facingAngle = getFacingAngle() +90 ;
            cp.worldLocation = getWorldLocation();
         }

      public void draw(float[] viewportMatrix)
         {
            if (isInFlight())
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

                  setIdentityM(modelMatrix, 0);

                  // Make a translation matrix
                  translateM(modelMatrix, 0, worldLocation.x, worldLocation.y, 0);

                  // Combine the model with the viewport into a new matrix
                  multiplyMM(viewportModelMatrix, 0, viewportMatrix, 0, modelMatrix, 0);

        /*
            Now rotate the model - just the ship model

            Parameters
            rm	returns the result
            rmOffset	index into rm where the result matrix starts
            a	angle to rotate in degrees
            x	X axis component
            y	Y axis component
            z	Z axis component
        */
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
