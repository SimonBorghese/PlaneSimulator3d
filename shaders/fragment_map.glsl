#version 330 core

out vec4 FragColor;
// The outputs for the vertex shader
flat in vec3 f_point;
in vec3 f_vertex;

void main(){
    //vec3 f_color = (f_vertex + vec3(0.5, 0.5, 0.5)) / 2.0;

    //FragColor = vec4(f_color, 0.5);

    float r = distance(f_vertex, f_point);
        vec3 f_color = (f_vertex + vec3(0.5, 0.5, 0.5)) / 2.0;

        FragColor = vec4(vec3(length(f_color / (4.0 * (pow(r,2) + 1.0)))), 1.0);
}