#version 330 core

layout (location = 0) in vec3 aPos;

uniform vec2 lat_bounds;
uniform vec2 lng_bounds;

out VS_OUT{
    vec3 point;
    vec3 vertex;
} vs_out;

void main(){
    float x_rel = (lat_bounds.y - aPos.x) / (lat_bounds.y - lat_bounds.x);
    float y_rel = (lng_bounds.y - aPos.y) / (lng_bounds.y - lng_bounds.x);

    vec3 out_pos = vec3(x_rel, y_rel, aPos.z / 1000.0) - vec3(0.5, 0.5, 0.0);

    out_pos *= 2.0;

    vs_out.point = out_pos;
    vs_out.vertex = out_pos;

    gl_Position = vec4(out_pos, 1.0);
}