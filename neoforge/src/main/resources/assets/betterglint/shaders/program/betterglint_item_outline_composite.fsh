#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D OutlineSampler;
uniform sampler2D MaskSampler;

uniform float OutlineR;
uniform float OutlineG;
uniform float OutlineB;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 uv = clamp(texCoord, vec2(0.0), vec2(1.0));
    vec4 scene = texture(DiffuseSampler, uv);
    float outlineAlpha = texture(OutlineSampler, uv).a;
    float inside = step(0.5, texture(MaskSampler, uv).a);
    outlineAlpha *= (1.0 - inside);

    vec3 outlineColor = vec3(OutlineR, OutlineG, OutlineB);
    vec3 outRgb = mix(scene.rgb, outlineColor, outlineAlpha);
    fragColor = vec4(outRgb, 1.0);
}
