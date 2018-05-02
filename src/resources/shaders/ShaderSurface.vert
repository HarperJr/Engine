#version 330

struct Matrix{
    mat4 mv_matrix;
    mat4 p_matrix;
};

struct GOut{
	vec2 texcoord;
	vec3 world_position;
    vec3 world_normal;
};

struct Light
{
	vec3 position;
	vec3 direction;
	vec3 color;
};

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texcoord;
layout(location = 2) in vec3 normal;

uniform Matrix u_matrix;

const vec3 light_position = vec3(0.0, 2.0, 0.0);
const vec3 light_color = vec3(1.0, 1.0, 1.0);

out Light light;
out vec3 look_direction;
out GOut v_out;

void main(void){
    vec4 world_position = u_matrix.mv_matrix * vec4(position, 1.0);

    look_direction = normalize(world_position - inverse(u_matrix.mv_matrix)[3]).xyz;

    light.position = light_position;
    light.direction = normalize(world_position.xyz - light.position);
    light.color = light_color;

    v_out.texcoord = texcoord;
    v_out.world_normal = normalize(u_matrix.mv_matrix * vec4(normal, 0.0)).xyz;
    v_out.world_position = world_position.xyz;

	gl_Position = u_matrix.p_matrix * world_position;
}