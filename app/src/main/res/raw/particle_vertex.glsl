attribute vec4 a_Position;
attribute vec2 a_Texture;

uniform mat4 u_Matrix;

varying vec2 tx;

void main()
{
    gl_Position = u_Matrix * a_Position;
    tx = a_Texture;
}