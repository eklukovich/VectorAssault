package com.eric.vectorassault.ParticleSystem;


import android.content.Context;
import android.graphics.PointF;

import com.eric.vectorassault.Datatypes.Color;
import com.eric.vectorassault.Objects.ParticleObject;
import com.eric.vectorassault.R;
import com.eric.vectorassault.Texture;

import java.util.Random;

/**
 * This class is used to manage all of the active particles on the screen. Has functions to handle
 * updating and rendering particles
 */

public class ParticleManager
   {
      private Texture lineTexture;
      private Texture glowTexture;

      ParticleObject particle;

      private CircularParticleArray particleList;

      public ParticleManager(int capacity)
         {
            particleList = new CircularParticleArray(capacity);
         }

      public void loadTextures(Context context)
         {
            lineTexture = new Texture(context, R.drawable.particle_glow);
            glowTexture = new Texture(context, R.drawable.glow);
         }

      private ParticleObject addParticleToArray(ParticleObject.ParticleType type)
         {
            int index;

            // if the particle list is full then create a new particle at the beginning
            if (particleList.getNumActiveParticles() == particleList.getCapacity())
               {
                  index = 0;
                  particleList.setStartIndex(particleList.getStartIndex() + 1);
               }
            else
               {
                  index = particleList.getNumActiveParticles();
                  particleList.setNumActiveParticles(particleList.getNumActiveParticles() + 1);
               }

            // update the particle data in the list
            particleList.setParticle(index, new ParticleObject(type));

            return particleList.getParticle(index);
         }

      // creates thrust particles (creates 4 particles)
      public void createThrustParticles(PointF position, float angle)
         {
            for (int i = 0; i <4; i++)
               {
                  // add a thrust particle in the array
                  particle = addParticleToArray(ParticleObject.ParticleType.THRUST);

                  // calculate the position of the particle with respect to the ship
                  particle.worldLocation.x = position.x - 15.0f * -1 * (float) Math.sin(angle * Math.PI / 180.0);
                  particle.worldLocation.y = position.y - 15.0f * (float) Math.cos(angle * Math.PI / 180.0);

                  // set the particle color
                  if(i%2 == 0)
                     {
                        particle.color = new Color(0.78f, 0.15f, 0.04f, 1);
                     }
                  else
                     {
                        particle.color = new Color(1.0f, 0.73f, 0.12f, 0.6f);
                     }

                  // set other particle data
                  particle.setFacingAngle(angle + 90);
                  particle.lifetime = 10.0f;
                  particle.lifePercent = 1.0f;
                  particle.inFlight = true;
                  particle.setSpeed(100);
               }
         }

      public void createExplosionParticles(PointF position, int maxSpeed, int numParticles)
         {
            Random rand = new Random();

            Color color = new Color();

            // create a particle color
            color = color.createParticleColor();

            // create a bunch of new particles
            for (int i = 0; i < numParticles; i++)
               {
                  // create explosion particles
                  particle = addParticleToArray(ParticleObject.ParticleType.EXPLOSION);

                  // create random values
                  float angle = Math.abs(rand.nextInt() % 360) + 90;
                  float lifetime = Math.abs(rand.nextInt() % 200);
                  float speed = rand.nextInt() % maxSpeed;

                  // set particle data
                  particle.color = color;
                  particle.setFacingAngle(angle);
                  particle.lifetime = lifetime;
                  particle.lifePercent = 1.0f;
                  particle.inFlight = true;
                  particle.setSpeed(speed);
                  particle.setWorldLocation(position.x, position.y);
               }
         }


      public void update(int mapWidth, int mapHeight, long fps)
         {
            int removalCount = 0;

            for (int i = 0; i < particleList.getNumActiveParticles(); i++)
               {
                  // get the particle
                  particle = particleList.getParticle(i);

                  // update the particle
                  particle.update(mapWidth, mapHeight, fps);

                  // swap particle into active area
                  Swap(particleList, i - removalCount, i);

                  // remove any particles
                  if (particle.lifePercent <= 0)
                     {
                        removalCount++;
                     }
               }

            particleList.setNumActiveParticles(particleList.getNumActiveParticles() - removalCount);
         }

      public void draw(float[] viewportMatrix)
         {
            for (int i = 0; i < particleList.getNumActiveParticles(); i++)
               {
                  // get the particle
                  particle = particleList.getParticle(i);

                  // draw explosion particle
                  if (particle.particleType == ParticleObject.ParticleType.EXPLOSION)
                     {
                        particle.draw(lineTexture, viewportMatrix);
                     }

                  // draw thrust particle
                  else if (particle.particleType == ParticleObject.ParticleType.THRUST)
                     {
                        particle.draw(glowTexture, viewportMatrix);
                     }

               }
         }

      public void Swap(CircularParticleArray list, int index1, int index2)
         {
            particle = list.getParticle(index1);
            list.setParticle(index1, list.getParticle(index2));
            list.setParticle(index2, particle);
         }
   }
