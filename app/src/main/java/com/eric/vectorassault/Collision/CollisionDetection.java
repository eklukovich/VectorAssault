package com.eric.vectorassault.Collision;


/**
 * This class is used to handle collision detection
 */
public class CollisionDetection
   {
      public static boolean detect(CollisionPackage cp1, CollisionPackage cp2)
         {
            boolean collided = false;

            // Get the distance of the two objects from the center of the circles on the x axis
            float distanceX = (cp1.worldLocation.x) - (cp2.worldLocation.x);

            // Get the distance of the two objects from the center of the circles on the y axis
            float distanceY = (cp1.worldLocation.y) - (cp2.worldLocation.y);

            // Calculate the distance between the center of each circle
            double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

            // check the two circles overlap
            if (distance < cp1.radius + cp2.radius)
               {
                  // calculate angles to rotate the objects so they are in the same direction
                  double radianAngle1 = ((cp1.facingAngle / 180) * Math.PI);
                  double cosAngle1 = Math.cos(radianAngle1);
                  double sinAngle1 = Math.sin(radianAngle1);

                  double radianAngle2 = ((cp2.facingAngle / 180) * Math.PI);
                  double cosAngle2 = Math.cos(radianAngle2);
                  double sinAngle2 = Math.sin(radianAngle2);

                  int numCrosses = 0;

                  float worldUnrotatedX;
                  float worldUnrotatedY;

                  // loop through vertices and check if there was a collision
                  for (int i = 0; i < cp1.vertexListLength - 1; i++)
                     {
                        // calculate the world position for object 1
                        worldUnrotatedX = cp1.worldLocation.x + cp1.vertexList[i].x;
                        worldUnrotatedY = cp1.worldLocation.y + cp1.vertexList[i].y;

                        // rotate the points for object 1
                        cp1.currentPoint.x = cp1.worldLocation.x + (int) ((worldUnrotatedX - cp1.worldLocation.x) * cosAngle1 - (worldUnrotatedY - cp1.worldLocation.y) * sinAngle1);
                        cp1.currentPoint.y = cp1.worldLocation.y + (int) ((worldUnrotatedX - cp1.worldLocation.x) * sinAngle1 + (worldUnrotatedY - cp1.worldLocation.y) * cosAngle1);

                        for (int j = 0; j < cp2.vertexListLength - 1; j++)
                           {
                              // calculate the world position for object 2
                              worldUnrotatedX = cp2.worldLocation.x + cp2.vertexList[j].x;
                              worldUnrotatedY = cp2.worldLocation.y + cp2.vertexList[j].y;

                              // rotate the points for object 2
                              cp2.currentPoint.x = cp2.worldLocation.x + (int) ((worldUnrotatedX - cp2.worldLocation.x) * cosAngle2 - (worldUnrotatedY - cp2.worldLocation.y) * sinAngle2);
                              cp2.currentPoint.y = cp2.worldLocation.y + (int) ((worldUnrotatedX - cp2.worldLocation.x) * sinAngle2 + (worldUnrotatedY - cp2.worldLocation.y) * cosAngle2);


                              worldUnrotatedX = cp2.worldLocation.x + cp2.vertexList[i + 1].x;
                              worldUnrotatedY = cp2.worldLocation.y + cp2.vertexList[i + 1].y;

                              // Now rotate the newly updated point, stored in worldUnrotatedX/Y
                              // around the centre point of the object (worldLocation)
                              cp2.currentPoint2.x = cp2.worldLocation.x + (int) ((worldUnrotatedX - cp2.worldLocation.x) * cosAngle2 - (worldUnrotatedY - cp2.worldLocation.y) * sinAngle2);
                              cp2.currentPoint2.y = cp2.worldLocation.y + (int) ((worldUnrotatedX - cp2.worldLocation.x)  * sinAngle2 + (worldUnrotatedY - cp2.worldLocation.y) * cosAngle2);

                              // check if there was a cross
                              if (((cp2.currentPoint.y > cp1.currentPoint.y) != (cp2.currentPoint2.y > cp1.currentPoint.y)) &&
                                      (cp1.currentPoint.x < (cp2.currentPoint2.x - cp2.currentPoint2.x) *
                                                                (cp1.currentPoint.y - cp2.currentPoint.y) / (cp2.currentPoint2.y - cp2.currentPoint.y) + cp2.currentPoint.x))
                                 numCrosses++;
                           }

                     }

                  // So do we have a collision?
                  collided = (numCrosses % 2 == 0);
               }

            return collided;
         }


      // Check if anything hits the border
      public static boolean contain(float mapWidth, float mapHeight, CollisionPackage cp)
         {
            // calculate the rotation angles
            double radianAngle = ((cp.facingAngle / 180) * Math.PI);
            double cosAngle = Math.cos(radianAngle);
            double sinAngle = Math.sin(radianAngle);

            // rotate each vertex and then check for collision
            for (int i = 0; i < cp.vertexListLength; i++)
               {
                  // get the unrotated x and y positions
                  float worldUnrotatedX = cp.worldLocation.x + cp.vertexList[i].x;
                  float worldUnrotatedY = cp.worldLocation.y + cp.vertexList[i].y;

                  // calculate the current point x and y
                  cp.currentPoint.x = cp.worldLocation.x + (int) ((worldUnrotatedX - cp.worldLocation.x) * cosAngle - (worldUnrotatedY - cp.worldLocation.y) * sinAngle);
                  cp.currentPoint.y = cp.worldLocation.y + (int) ((worldUnrotatedX - cp.worldLocation.x) * sinAngle + (worldUnrotatedY - cp.worldLocation.y) * cosAngle);

                  // Check the rotated vertex for a collision
                  if (cp.currentPoint.x < 0)
                     {
                        return true;
                     }
                  else if (cp.currentPoint.x > mapWidth)
                     {
                        return true;
                     }
                  else if (cp.currentPoint.y < 0)
                     {
                        return true;
                     }
                  else if (cp.currentPoint.y > mapHeight)
                     {
                        return true;
                     }
               }

            return false; // No collision
         }
   }
