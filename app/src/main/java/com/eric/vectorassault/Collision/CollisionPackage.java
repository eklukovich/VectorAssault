package com.eric.vectorassault.Collision;

import android.graphics.PointF;


/**
 * This class is used to hold collision data for each game object
 */
public class CollisionPackage
   {
      public float facingAngle;
      public PointF[] vertexList;
      public int vertexListLength;
      public PointF worldLocation;
      public float halfHeight;
      public float halfWidth;
      public float radius;

      // A couple of points to store results and avoid creating new objects during intensive collision detection
      public PointF currentPoint = new PointF();
      public PointF currentPoint2 = new PointF();

      public CollisionPackage(PointF[] vertexList, PointF worldLocation, float halfHeight, float halfWidth, float radius, float facingAngle)
         {
            // get the number of collision vertices
            vertexListLength = vertexList.length;

            // allocate memory for collision point list
            this.vertexList = new PointF[vertexListLength];

            // Make a copy of the array
            for (int i = 0; i < vertexListLength; i++)
               {
                  this.vertexList[i] = new PointF(vertexList[i].x, vertexList[i].y);
               }

            // create a new point for the world location
            this.worldLocation = new PointF(worldLocation.x, worldLocation.y);

            // set the radius
            this.halfHeight = halfHeight;
            this.halfWidth = halfWidth;
            this.radius = radius;

            // set the facing angle
            this.facingAngle = facingAngle;
         }
   }
