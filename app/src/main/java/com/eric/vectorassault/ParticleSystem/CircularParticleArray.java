package com.eric.vectorassault.ParticleSystem;


import com.eric.vectorassault.Objects.ParticleObject;

/**
 * This class implements a circular array that is used to handle particles.
 */
public class CircularParticleArray
   {
      private ParticleObject [] particleObjects;
      private int startIndex = 0;
      private int numActiveParticles;
      private int maxParticles;

      public CircularParticleArray(int capacity)
         {
            particleObjects = new ParticleObject [capacity];
            for(int i = 0; i < capacity; i++)
               {
                  particleObjects[i] = new ParticleObject(ParticleObject.ParticleType.EMPTY);
               }

            maxParticles = capacity;
         }

      public int getStartIndex()
         {
            return startIndex;
         }

      public void setStartIndex(int value)
         {
            startIndex = value % maxParticles;
         }

      public int getNumActiveParticles()
         {
            return numActiveParticles;
         }

      public void setNumActiveParticles(int value)
         {
            numActiveParticles = value;
         }

      public int getCapacity()
         {

            return maxParticles;
         }



      public ParticleObject getParticle(int index)
         {
            return particleObjects[(startIndex + index) % maxParticles];
         }

      public void setParticle(int index, ParticleObject p)
         {
            particleObjects[(startIndex + index) % maxParticles] = p;
         }

      public void clear()
         {
            startIndex = 0;
            numActiveParticles = 0;
         }
   }
