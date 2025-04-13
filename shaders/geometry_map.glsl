#version 330 core

layout (points) in;
layout (triangle_strip, max_vertices = 7) out;


in VS_OUT {
    vec3 point;
    vec3 vertex;
} gs_in[];

// The outputs for the vertex shader
flat out vec3 f_point;
out vec3 f_vertex;

void main(){
    vec3 in_point = vec3(gl_in[0].gl_Position.xyz);
    f_point = gs_in[0].point;

    vec3 o_point = vec3(0.0, 0.0, 0.0);

    // bottom right
    o_point = vec3(1.0, -1.0, 1.0);
    f_vertex = o_point;
    gl_Position = vec4(o_point, 1.0);
    EmitVertex();

    // top right
    o_point = vec3(1.0, 1.0, 1.0);
    f_vertex = o_point;
    gl_Position = vec4(o_point, 1.0);
    EmitVertex();

    // center
    o_point = in_point;
    f_vertex = o_point;
    gl_Position = vec4(o_point, 1.0);
    EmitVertex();

    // Top left
    o_point = vec3(-1.0, 1.0, 1.0);
    f_vertex = o_point;
    gl_Position = vec4(o_point, 1.0);
    EmitVertex();

    // bottom left
    o_point = vec3(-1.0, -1.0, 1.0);
    f_vertex = o_point;
    gl_Position = vec4(o_point, 1.0);
    EmitVertex();

    // center
    o_point = in_point;
    f_vertex = o_point;
    gl_Position = vec4(o_point, 1.0);
    EmitVertex();

    // bottom right
    o_point = vec3(1.0, -1.0, 1.0);
    f_vertex = o_point;
    gl_Position = vec4(o_point, 1.0);
    EmitVertex();

    EndPrimitive();
}