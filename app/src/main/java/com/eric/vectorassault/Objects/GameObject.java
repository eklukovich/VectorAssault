package com.eric.vectorassault.Objects;

import android.graphics.PointF;

import com.eric.vectorassault.GLManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GameObject extends GLManager
   {

      boolean isActive;

      public enum Type
         {
            SHIP, MULTIPLIER, BORDER, BULLET, STAR, PARTICLE, GRID_LINE
         }

      private Type type;

      public static int glProgram = -1;

      // How many vertices does it take to make
      // this particular game object?
      public int numElements;
      public int numVertices;

      // To hold the coordinates of the vertices that define our GameObject model
      public float[] modelVertices;
      public float[] textureCoords;


      // Which way is the object moving and how fast?
      private float xVelocity = 0f;
      private float yVelocity = 0f;
      private float speed = 0;
      private float maxSpeed = 200;

      // Where is the object centre in the game world?
      public PointF worldLocation = new PointF();

      // Holds the vertex data that is passed into the openGL glProgram
      public FloatBuffer vertices;
      public FloatBuffer textCoords;


      // For translating each point from the model (ship, asteroid etc)
      // to its game world coordinates
      public final float[] modelMatrix = new float[16];

      // Some more matrices for Open GL transformations
      float[] viewportModelMatrix = new float[16];
      float[] rotateViewportModelMatrix = new float[16];

      // Where is the GameObject facing?
      public float facingAngle = 0f;

      // How fast is it rotating?
      public float rotationRate = 0f;

      // Which direction is it heading?
      public float travellingAngle = 0f;

      // How long and wide is the GameObject?
      private float length;
      private float width;


      public GameObject()
         {
            isActive = true;
         }

      public boolean isActive()
         {
            return isActive;
         }

      public void setActive(boolean isActive)
         {
            this.isActive = isActive;
         }

      public void setObjectShaderProgram()
         {
            glProgram = GLManager.getGLObjectShaderProgram();
         }
      public void setParticleShaderProgram()
         {
            glProgram = GLManager.getGLParticleShaderProgram();
         }

      public Type getType()
         {
            return type;
         }

      public void setType(Type t)
         {
            this.type = t;
         }

      public void setSize(float w, float l)
         {
            width = w;
            length = l;

         }

      public PointF getWorldLocation()
         {
            return worldLocation;
         }

      public void setWorldLocation(float x, float y)
         {
            this.worldLocation.x = x;
            this.worldLocation.y = y;
         }

      public void setVertices(float[] objectVertices)
         {

            modelVertices = new float[objectVertices.length];
            modelVertices = objectVertices;

            // Store how many vertices and elements there is for future use
            numElements = modelVertices.length;

            numVertices = numElements / ELEMENTS_PER_VERTEX;

            // Initialize the vertices ByteBuffer object based on the
            // number of vertices in the ship design and the number of
            // bytes there are in the float type
            vertices = ByteBuffer.allocateDirect(numElements * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();

            // Add the ship into the ByteBuffer object
            vertices.put(modelVertices);

         }

      public void setTextureCoords(float[] coords)
         {
            textureCoords = new float[coords.length];
            textureCoords = coords;

            textCoords = ByteBuffer.allocateDirect(textureCoords.length * FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
            textCoords.put(textureCoords);
         }


      public void setRotationRate(float rotationRate)
         {
            this.rotationRate = rotationRate;
         }

      public float getTravellingAngle()
         {
            return travellingAngle;
         }

      public void setTravellingAngle(float travellingAngle)
         {
            this.travellingAngle = travellingAngle;
         }

      public float getFacingAngle()
         {
            return facingAngle;
         }

      public void setFacingAngle(float facingAngle)
         {
            this.facingAngle = facingAngle;
         }


      void move(float fps)
         {
            if (xVelocity != 0)
               {
                  worldLocation.x += xVelocity / fps;
               }

            if (yVelocity != 0)
               {
                  worldLocation.y += yVelocity / fps;
               }

            // Rotate
            if (rotationRate != 0)
               {
                  facingAngle = facingAngle + rotationRate / fps;
               }


         }

      public float getxVelocity()
         {
            return xVelocity;
         }

      public void setxVelocity(float xVelocity)
         {
            this.xVelocity = xVelocity;
         }

      public float getyVelocity()
         {
            return yVelocity;
         }

      public void setyVelocity(float yVelocity)
         {
            this.yVelocity = yVelocity;
         }

      public float getSpeed()
         {
            return speed;
         }

      public void setSpeed(float speed)
         {
            this.speed = speed;
         }

      public float getMaxSpeed()
         {
            return maxSpeed;
         }

      public void setMaxSpeed(float maxSpeed)
         {
            this.maxSpeed = maxSpeed;
         }


   }
