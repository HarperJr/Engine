#version 330

struct Matrix {
mat4 mv_matrix;
mat4 p_matrix;
};

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texcoord;
layout(location = 2) in vec3 normal;

uniform Matrix u_matrix;

void main(void) {
    gl_Position = u_matrix.p_matrix * u_matrix.mv_matrix * vec4(position, 1.0);
}