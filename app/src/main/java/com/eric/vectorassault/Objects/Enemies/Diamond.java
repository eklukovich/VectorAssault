package com.eric.vectorassault.Objects.Enemies;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import com.eric.vectorassault.Collision.CollisionPackage;
import com.eric.vectorassault.GLManager;
import com.eric.vectorassault.Objects.Bullet;
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

public class Diamond extends EnemyObject
   {

      private Texture texture;


      public Diamond(Context context, int levelNumber, int mapWidth, int mapHeight)
         {
            super();

            Random r = new Random();

            scoreValue = 50;

            texture = new Texture(context, R.drawable.diamond_glow3);

            // set a random rotation rate in degrees per second
            setRotationRate(r.nextInt(50 * levelNumber) + 10);

            // travel at any random angle
            setTravellingAngle(r.nextInt(360));

            // Spawn asteroids between 50 and 550 on x and y
            // And avoid the extreme edges of map
            int x = r.nextInt(mapWidth - 100) + 50;
            int y = r.nextInt(mapHeight - 100) + 50;

            // Avoid the center where the player spawns
            if (x > 250 && x < 350) x = x + 100;
            if (y > 250 && y < 350) y = y + 100;

            // Set the location
            setWorldLocation(x, y);


            // Make them a random speed with the maximum
            // being appropriate to the level number
            setSpeed(r.nextInt(25 * levelNumber) + 1);

            setMaxSpeed(35);

            // Cap the speed
           // if (getSpeed() > getMaxSpeed())
             //  {
                  setSpeed(getMaxSpeed());
              // }

            // Make sure we know this object is a ship
            setType(Type.DIAMOND);

            float width = 30;
            float height = 30;


            // It will be useful to have a copy of the length and width/2 so we don't have to keep dividing by 2
            float halfW = width / 2;
            float halfL = height / 2;

            // Define the space ship shape as a triangle from point to point in anti clockwise order
            float[] shipVertices = new float[]{
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


            setVertices(shipVertices);
            setTextureCoords(textCoords);

            // Initialize the collision package
            // (the object space vertex list, x any world location
            // the largest possible radius, facingAngle)

            collisionPoints = new PointF[4];
            collisionPoints[0] = new PointF(-halfW, -halfL);
            collisionPoints[1] = new PointF(-halfW, halfL);
            collisionPoints[2] = new PointF(halfW, -halfL);
            collisionPoints[3] = new PointF(halfW, halfL);
            cp = new CollisionPackage(collisionPoints, getWorldLocation(), halfL, halfW, height / 2, getFacingAngle());

            setObjectShaderProgram();

         }

      @Override
      public void update(long fps, PointF playerPos, boolean avoid, Bullet bullet)
         {
            float deltaY = playerPos.y - worldLocation.y;
            float deltaX = playerPos.x - worldLocation.x;

            float angleInDegrees = (float)Math.atan2(deltaY, deltaX) * 180 / (float)Math.PI;

            setxVelocity((float) (getSpeed() * Math.cos(Math.toRadians(angleInDegrees))));
            setyVelocity((float) (getSpeed() * Math.sin(Math.toRadians(angleInDegrees))));

            move(fps);

            // Update the collision package
            cp.facingAngle = getFacingAngle();
            cp.worldLocation = getWorldLocation();
         }

      @Override
      public void bounce()
         {
            // Reverse the travelling angle
            if (getTravellingAngle() >= 180)
               {
                  setTravellingAngle(getTravellingAngle() - 180);
               }
            else
               {
                  setTravellingAngle(getTravellingAngle() + 180);
               }

            // Reverse velocity because occasionally they get stuck
            setWorldLocation((getWorldLocation().x + -getxVelocity() / 3),
                                (getWorldLocation().y + -getyVelocity() / 3));
            // Speed up by 10%
            setSpeed(getSpeed() * 1.1f);
            // Not too fast though
            if (getSpeed() > getMaxSpeed())
               {
                  setSpeed(getMaxSpeed());
               }
         }

      @Override
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
            setRotateM(modelMatrix, 0, facingAngle +90, 0, 0, 1.0f);
            multiplyMM(rotateViewportModelMatrix, 0, viewportModelMatrix, 0, modelMatrix, 0);

            // Give the matrix to OpenGL
            glUniformMatrix4fv(GLManager.objectShaderLocations.get("u_Matrix"), 1, false, rotateViewportModelMatrix, 0);
            GLES20.glUniform1i(GLManager.objectShaderLocations.get("gSampler"), 0);

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
