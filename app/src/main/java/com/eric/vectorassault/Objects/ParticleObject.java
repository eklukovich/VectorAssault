package com.eric.vectorassault.Objects;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.os.SystemClock;

import com.eric.vectorassault.Collision.CollisionPackage;
import com.eric.vectorassault.Datatypes.Color;
import com.eric.vectorassault.GLManager;
import com.eric.vectorassault.Texture;

import java.util.Random;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setRotateM;
import static android.opengl.Matrix.translateM;

public class ParticleObject extends GameObject
   {
      public enum ParticleType
         {
            EXPLOSION, THRUST, EMPTY
         }

      public ParticleType particleType;

      public boolean inFlight = false;
      public CollisionPackage cp;
      public PointF[] collisionPoints;
     // public Texture texture;
      public float lifetime;
      public float lifePercent;
      public Color color;
      public int reflect = 1;


      public ParticleObject(ParticleType t)
         {
            super();

            particleType = t;

            setType(Type.PARTICLE);

            float width;
            float length;

            if(particleType == ParticleType.EXPLOSION)
               {
                  width = 6.0f;
                  length = 12.0f;
               }
            else
               {
                  width = 4.0f;
                  length = 9.0f;
               }



            setSize(width, length);


            float halfW = width / 2;
            float halfL = length / 2;

            float[] vertices = new float[]{
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

            setVertices(vertices);
            setTextureCoords(textCoords);



         }

      public void createEnemyExplosion(PointF enemyPosition)
         {
            Random rand = new Random();

            float angle = Math.abs(rand.nextInt() % 360) + 90;

            float lifetime = Math.abs(rand.nextInt() % 20);

            setFacingAngle(angle);
            this.lifetime = lifetime;
            this.lifePercent = 1.0f;

            inFlight = true;
            setSpeed(rand.nextInt() % 200 + 20);


            worldLocation.x = enemyPosition.x;
            worldLocation.y = enemyPosition.y;
         }

      public void update(int mapWidth, int mapHeight, long fps)
         {
            // Set the velocity if bullet in flight
            if (inFlight && lifePercent > 0)
               {
                  lifePercent -= 1.0f/lifetime;
                  color.a = lifePercent;

                  if(worldLocation.x < 0 || worldLocation.x > mapWidth || worldLocation.y < 0 || worldLocation.y > mapHeight)
                     {
                        facingAngle -= 180;
                        setSpeed(getSpeed() + 50);
                     }




                  if(particleType == ParticleType.THRUST)
                     {
                        float time = SystemClock.uptimeMillis() / 1000.0f;

                        float basexVel = (float) (getSpeed() * Math.cos(Math.toRadians(getFacingAngle())));
                        float baseyVel = (float) (getSpeed() * Math.sin(Math.toRadians(getFacingAngle())));

                        float perpxVel = baseyVel * (1.2f * (float)Math.sin(Math.toRadians(reflect * time * 10000.0f)));
                        float perpyVel = basexVel * (1.2f * (float)Math.sin(Math.toRadians(reflect * time * 10000.0f)));


                        //System.out.println("X VEL: " + basexVel + " | " + perpxVel);
                       // System.out.println("Y VEL: " + baseyVel + " | " + perpyVel);

                        setxVelocity(basexVel + perpxVel);
                        setyVelocity(baseyVel - perpyVel);
                     }
                  else
                     {
                        setxVelocity((float) (getSpeed() * Math.cos( Math.toRadians(getFacingAngle()))));
                        setyVelocity((float) (getSpeed() * Math.sin(Math.toRadians(getFacingAngle()))));

                     }





               }
            else
              {
                 inFlight = false;
                  // Have it sit inside the ship
                  setWorldLocation(0, 0);
               }


            move(fps);

            // Update the collision package
           // cp.facingAngle = getFacingAngle() +90 ;
           // cp.worldLocation = getWorldLocation();
         }

      public boolean isInFlight()
         {
            return inFlight;
         }


      public void draw(Texture texture, float[] viewportMatrix)
         {
            if (isInFlight())
               {

                  // tell OpenGl to use the glProgram
                  glUseProgram(getGLParticleShaderProgram());

                  // Set vertices to the first byte
                  vertices.position(0);
                  textCoords.position(0);

                  glVertexAttribPointer(GLManager.particleShaderLocations.get("a_Position"), COMPONENTS_PER_POSITION, GL_FLOAT, false, POSITION_STRIDE, vertices);
                  glVertexAttribPointer(GLManager.particleShaderLocations.get("a_Texture"), COMPONENTS_PER_TEXTURE, GL_FLOAT, false, TEXTURE_STRIDE, textCoords);

                  glEnableVertexAttribArray(GLManager.particleShaderLocations.get("a_Position"));
                  glEnableVertexAttribArray(GLManager.particleShaderLocations.get("a_Texture"));

                  // Translate model coordinates into world coordinates
                  // Make an identity matrix to base our future calculations on
                  // Or we will get very strange results
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
                  setRotateM(modelMatrix, 0, facingAngle + 90, 0, 0, 1.0f);

                  // And multiply the rotation matrix into the model-viewport matrix
                  multiplyMM(rotateViewportModelMatrix, 0, viewportModelMatrix, 0, modelMatrix, 0);

                  // Set the shader uniform values
                  glUniformMatrix4fv(GLManager.particleShaderLocations.get("u_Matrix"), 1, false, rotateViewportModelMatrix, 0);
                  GLES20.glUniform1i(GLManager.particleShaderLocations.get("gSampler"), 0);

                  // Assign a color to the fragment shader
                  glUniform4f(GLManager.particleShaderLocations.get("u_Color"), color.r, color.g, color.b, color.a);


                  texture.bindTexture(GLES20.GL_TEXTURE0);

                  // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
                  GLES20.glEnable(GLES20.GL_BLEND);
                  GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
                  glDrawArrays(GL_TRIANGLE_STRIP, 0, numVertices);
                  GLES20.glDisable(GLES20.GL_BLEND);
               }
         }
   }
