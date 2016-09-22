package com.eric.vectorassault.Datatypes;


import java.util.Random;

/**
 * This class is used hold color values RGBA
 */
public class Color
   {
      public float r;
      public float g;
      public float b;
      public float a;

      public Color()
         {
            r = g = b = a = 1.0f;
         }

      public Color(float r, float g, float b, float a)
         {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
         }

      public void setRandomColor()
         {
            Random rand = new Random();
            r = rand.nextFloat() / 2f + 0.5f;
            g = rand.nextFloat() / 2f + 0.5f;
            b = rand.nextFloat() / 2f + 0.5f;
            a = 1.0f;
         }

      public Color createParticleColor()
         {
            Random rand = new Random();

            float hue1 = rand.nextFloat() * 12;

            float hue2 = (hue1 + (float) rand.nextFloat() * 3) % 12.0f;

            Color color1 = setHSVColor(hue1, 0.5f, 1);
            //Color color2 = setHSVColor(hue2, 0.5f, 1);
            Color color2 = setHSVColor(0, 0.0f, 1);

            return colorLerp(color1, color2, 0.8f);
         }

      private Color colorLerp(Color color1, Color color2, float value)
         {
            return new Color(mix(color1.r, color2.r, value), mix(color1.g, color2.g, value), mix(color1.b, color2.b, value), 0.8f);//mix(color1.a, color2.a, value));
         }

      private float mix(float x, float y, float a)
         {
            return ((x + a) * (y - x));
         }

      private Color setHSVColor(float h, float s, float v)
         {
            if (h == 0 && s == 0)
               {
                  return new Color(v, v, v, a);
               }

            float c = s * v;
            float x = c * (1 - Math.abs(h % 2 - 1));
            float m = v - c;

            if (h < 1)
               {
                  return new Color(c + m, x + m, m, 1.0f);

               }
            else if (h < 2)
               {
                  return new Color(x + m, c + m, m, 1.0f);
               }
            else if (h < 3)
               {
                  return new Color(m, c + m, x + m, 1.0f);
               }
            else if (h < 4)
               {
                  return new Color(m, x + m, c + m, 1.0f);
               }
            else if (h < 5)
               {
                  return new Color(x + m, m, c + m, 1.0f);
               }
            else
               {
                  return new Color(c + m, m, x + m, 1.0f);
               }
         }
   }
