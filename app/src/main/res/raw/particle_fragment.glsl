precision mediump float;

varying highp vec2 tx;
uniform sampler2D gSampler;
uniform vec4 u_Color;

void main()
{
    gl_FragColor = texture2D(gSampler, tx) * u_Color;
}