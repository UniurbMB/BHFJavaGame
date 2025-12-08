#version 330 core

in vec2 uv;

out vec4 fragColor;

uniform vec3 color;
uniform sampler2D textureSampler;

void main(){
	vec4 tex = texture(textureSampler, uv);
	fragColor = vec4(color.xyz * tex.xyz, tex.w);
}