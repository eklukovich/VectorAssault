attribute vec4 a_Position;
attribute vec2 a_Texture;

uniform mat4 u_Matrix;

varying vec2 tx;

void main()
{
    // comment
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = 3.0;
    tx = a_Texture;
}