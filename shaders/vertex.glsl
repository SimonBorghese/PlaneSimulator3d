#version 330 core
// Define the shader version as OpenGL 3.3, in the core profile

// Define the vertex input as groups of vec3 and vec2, which will be their vertex positions and texture cords
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTex;

//  A vec2 to be sent to the next shader stage which is the texture cords
out vec2 oTex;

// Our perspective matrix
uniform mat4 projection;

// Our view matrix
uniform mat4 view;

// Our model matrix
uniform mat4 model;

// Main function
void main(){
    // Assign the output var to the input var
    oTex = aTex;

    // Define the vertex as the input position with a W of 1.0
    gl_Position = projection * view * model * vec4(aPos, 1.0);
}