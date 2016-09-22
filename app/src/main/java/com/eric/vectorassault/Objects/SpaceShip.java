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

public class SpaceShip extends GameObject
   {
      public boolean isThrusting;

      private int bulletFireDelayCount = 0;

      private Texture texture;

      // Collision Variables
      public CollisionPackage cp;
      public PointF[] collisionPoints;

      public SpaceShip(Context context, float worldLocationX, float worldLocationY)
         {
            super();

            // Make sure we know this object is a ship
            setType(Type.SHIP);

            // create the texture for the ship
            texture = new Texture(context, R.drawable.ship);

            // set the initial location
            setWorldLocation(worldLocationX, worldLocationY);

            // set width and height of the ship
            float width = 18;
            float length = 18;
            setSize(width, length);

            // set the ship's max speed
            setMaxSpeed(225);

            // calculate half width and height
            float halfW = width / 2;
            float halfL = length / 2;

            // Create the ship vertices as a quad so it can be textured
            float[] shipVertices = new float[]{
                                                  -halfW, -halfL, 0,
                                                  halfW, -halfL, 0,
                                                  -halfW, halfL, 0,
                                                  halfW, halfL, 0
            };

            // Create texture coordinates
            float[] textCoords = new float[]{
                                                0.0f, 0.0f,
                                                0.0f, 1.0f,
                                                1.0f, 0.0f,
                                                1.0f, 1.0f
            };


            setVertices(shipVertices);
            setTextureCoords(textCoords);


            // Initialize the collision package (the object space vertex list, x any world location the largest possible radius, facingAngle)
            collisionPoints = new PointF[4];
            collisionPoints[0] = new PointF(-halfW, -halfL);
            collisionPoints[1] = new PointF(-halfW, halfL);
            collisionPoints[2] = new PointF(halfW, -halfL);
            collisionPoints[3] = new PointF(halfW, halfL);

            cp = new CollisionPackage(collisionPoints, getWorldLocation(), halfL, halfW, length/2, getFacingAngle());

            setObjectShaderProgram();

         }

      public void update(long fps)
         {
            float speed = getSpeed();

            // check if the ship is moving
            if (isThrusting)
               {
                  // if so then check if the speed is at the max speed
                  if (speed < getMaxSpeed())
                     {
                        // if not then slowly increment speed to max
                        setSpeed(speed + 5);
                     }
               }
            else
               {
                  // if not then slow the ship down
                  if (speed > 0)
                     {
                        setSpeed(speed - 3);
                     }
                  else
                     {
                        setSpeed(0);
                     }
               }

            setxVelocity((float) (speed * Math.cos(Math.toRadians(getFacingAngle() + 90))));
            setyVelocity((float) (speed * Math.sin(Math.toRadians(getFacingAngle() + 90))));


            move(fps);

            // Update the collision package
            cp.facingAngle = getFacingAngle();
            cp.worldLocation = getWorldLocation();
         }

      public boolean pullTrigger()
         {
            if (bulletFireDelayCount == 5)
               {
                  bulletFireDelayCount = 0;
                  return true;
               }
            bulletFireDelayCount++;

            return false;
         }

      public void setThrust(boolean value)
         {
            isThrusting = value;
         }

      public void draw(float[] viewportMatrix)
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

            // reset the matrix to the identity matrix
            setIdentityM(modelMatrix, 0);

            // set the translation
            translateM(modelMatrix, 0, worldLocation.x, worldLocation.y, 0);
            multiplyMM(viewportModelMatrix, 0, viewportMatrix, 0, modelMatrix, 0);

            // set the rotation
            setRotateM(modelMatrix, 0, getFacingAngle(), 0, 0, 1.0f);
            multiplyMM(rotateViewportModelMatrix, 0, viewportModelMatrix, 0, modelMatrix, 0);

            // Give the matrix to OpenGL
            glUniformMatrix4fv(GLManager.objectShaderLocations.get("u_Matrix"), 1, false, rotateViewportModelMatrix, 0);
            GLES20.glUniform1i(GLManager.objectShaderLocations.get("gSampler"), 0);

            // bind the texture
            texture.bindTexture(GLES20.GL_TEXTURE0);

            // enable alpha blending for transparency
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

            // render the ship
            glDrawArrays(GL_TRIANGLE_STRIP, 0, numVertices);

            // disable blending
            GLES20.glDisable(GLES20.GL_BLEND);
         }


   }
