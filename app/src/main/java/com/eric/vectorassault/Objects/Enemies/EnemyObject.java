package com.eric.vectorassault.Objects.Enemies;

import android.graphics.PointF;

import com.eric.vectorassault.Collision.CollisionPackage;
import com.eric.vectorassault.GLManager;
import com.eric.vectorassault.Objects.Bullet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class EnemyObject extends GLManager
   {
      public enum Type
         {
            SQUARE, WANDERER, BOX, DIAMOND, SEEKER, SMALL_BOX
         }


      boolean isActive;

      public int scoreValue;
      public PointF[] collisionPoints;
      public CollisionPackage cp;

      private Type type;

      public static int glProgram = -1;

      // vertice data variables
      public FloatBuffer vertices;
      public FloatBuffer textCoords;
      public float[] modelVertices;
      public float[] textureCoords;
      public int numElements;
      public int numVertices;


      // Which way is the object moving and how fast?
      private float xVelocity = 0f;
      private float yVelocity = 0f;
      private float speed = 0;
      private float maxSpeed = 200;

      // Where is the object centre in the game world?
      public PointF worldLocation = new PointF();


      // matrices for OpenGL transformations
      public final float[] modelMatrix = new float[16];
      public float[] viewportModelMatrix = new float[16];
      public float[] rotateViewportModelMatrix = new float[16];

      // Where is the GameObject facing?
      public float facingAngle = 0f;

      // How fast is it rotating?
      public float rotationRate = 0f;

      // Which direction is it heading?
      public float travellingAngle = 0f;

      // How long and wide is the GameObject?
      private float length;
      private float width;


      public EnemyObject()
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

            // Initialize the vertices ByteBuffer object based on the number of vertices in the ship design and the number of bytes there are in the float type
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
            return facingAngle + 90;
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

      public abstract void update(long fps, PointF playerPos, boolean avoid, Bullet bullet);
      public abstract void bounce();
      public abstract void draw(float[] viewportMatrix);


   }
