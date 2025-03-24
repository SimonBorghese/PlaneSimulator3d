#version 330 core
// OpenGL 3.3 Core profile

// Define one color output in the fragment shader
out vec4 FragColor;

// Define the texture cord input
in vec2 aTex;

// Define the texture input as a uniform
uniform sampler2D iTex;

void main(){
    // Set the color output to be the texture
    FragColor = texture(iTex, aTex);
}