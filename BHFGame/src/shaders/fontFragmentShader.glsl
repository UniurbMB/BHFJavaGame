#version 330 core

in vec2 uv;

out vec4 fragColor;

uniform vec3 color;
uniform sampler2D textureSampler;

void main(){
	vec4 tex = texture(textureSampler, uv);
	if(tex.w == 0.0f)discard;
	fragColor = vec4(color.xyz, tex.w);
}
