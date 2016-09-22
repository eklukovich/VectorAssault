package com.eric.vectorassault;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glGetAttribLocation;

import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUseProgram;

/**
 * This class is used to hold the data for the particle and object shaders and has functions to
 * load/compile shaders and get them when rendering objects.
 */

public class GLManager
   {
      // Constants to help count the number of bytes between elements of our vertex data arrays
      public static final int COMPONENTS_PER_POSITION = 3;
      public static final int COMPONENTS_PER_TEXTURE = 2;

      // Constant for the size of a float
      public static final int FLOAT_SIZE = 4;

      // Striding distance
      public static final int TEXTURE_STRIDE = (COMPONENTS_PER_TEXTURE) * FLOAT_SIZE;
      public static final int POSITION_STRIDE = (COMPONENTS_PER_POSITION) * FLOAT_SIZE;

      // number of dimensions per vertex
      public static final int ELEMENTS_PER_VERTEX = 3;

      // Maps used to hold shader variable locations
      public static HashMap<String, Integer> objectShaderLocations = new HashMap<>();
      public static HashMap<String, Integer> particleShaderLocations = new HashMap<>();

      private static Context context;

      // A handle to the GL glProgram
      public static int objectProgram;
      public static int particleProgram;



      public static int getGLObjectShaderProgram()
         {
            return objectProgram;
         }

      public static int getGLParticleShaderProgram()
         {
            return particleProgram;
         }

      public static void setContext(Context c)
         {
            context = c;
         }


      private static void findShaderVariableLocations()
         {
            glUseProgram(objectProgram);
            objectShaderLocations.put("u_Matrix", glGetUniformLocation(objectProgram, "u_Matrix"));
            objectShaderLocations.put("a_Position", glGetAttribLocation(objectProgram, "a_Position"));
            objectShaderLocations.put("a_Texture", glGetAttribLocation(objectProgram, "a_Texture"));
            objectShaderLocations.put("gSampler", glGetUniformLocation(objectProgram, "gSampler"));
            objectShaderLocations.put("u_Color", glGetUniformLocation(objectProgram, "u_Color"));

            glUseProgram(particleProgram);
            particleShaderLocations.put("u_Matrix", glGetUniformLocation(particleProgram, "u_Matrix"));
            particleShaderLocations.put("a_Position", glGetAttribLocation(particleProgram, "a_Position"));
            particleShaderLocations.put("a_Texture", glGetAttribLocation(particleProgram, "a_Texture"));
            particleShaderLocations.put("gSampler", glGetUniformLocation(particleProgram, "gSampler"));
            particleShaderLocations.put("u_Color", glGetUniformLocation(particleProgram, "u_Color"));

         }



      public static void buildPrograms()
         {
            // Create the shader to render all of the game objects
            objectProgram = linkProgram(compileVertexShader(R.raw.texture_vertex), compileFragmentShader(R.raw.texture_fragment));

            // Create the shader to render all of the particles
            particleProgram = linkProgram(compileVertexShader(R.raw.particle_vertex), compileFragmentShader(R.raw.particle_fragment));

            System.out.println("SHADER: " + particleProgram);

            // get all the variables from the shader
            findShaderVariableLocations();
         }

      private static int compileVertexShader(int shaderFile)
         {
            String vertexShader = readTextFileFromRawResource(context, shaderFile);
            return compileShader(GL_VERTEX_SHADER, vertexShader);
         }

      private static int compileFragmentShader(int shaderFile)
         {
            String fragmentShader = readTextFileFromRawResource(context, shaderFile);
            return compileShader(GL_FRAGMENT_SHADER, fragmentShader);
         }

      private static int compileShader(int type, String shaderCode)
         {
            // Create a shader object and store its ID
            final int shader = glCreateShader(type);

            // Pass in the code then compile the shader
            glShaderSource(shader, shaderCode);
            glCompileShader(shader);

            return shader;
         }

      private static int linkProgram(int vertexShader, int fragmentShader)
         {
            int program = glCreateProgram();

            // Attach the vertex and fragment shaders to the glProgram.
            glAttachShader(program, vertexShader);
            glAttachShader(program, fragmentShader);

            // Link the two shaders together into a glProgram.
            glLinkProgram(program);

            return program;
         }


      private static String readTextFileFromRawResource(final Context context, final int resourceId)
         {
            // open the resource into an input stream
            final InputStream inputStream = context.getResources().openRawResource(resourceId);

            // create a reader for the input stream
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            // create a buffer reader to read the data
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine;
            final StringBuilder body = new StringBuilder();

            try
               {
                  // get all the lines and append to the string
                  while ((nextLine = bufferedReader.readLine()) != null)
                     {
                        body.append(nextLine);
                        body.append('\n');
                     }
               }
            catch (IOException e)
               {
                  return null;
               }

            return body.toString();
         }
   }