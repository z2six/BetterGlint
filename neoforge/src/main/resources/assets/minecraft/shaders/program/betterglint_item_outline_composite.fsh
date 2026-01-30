#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D OutlineSampler;
uniform sampler2D MaskSampler;
uniform sampler2D MainDepthSampler;
uniform sampler2D MaskDepthSampler;

uniform float OutlineR;
uniform float OutlineG;
uniform float OutlineB;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 uv = clamp(texCoord, vec2(0.0), vec2(1.0));
    vec4 scene = texture(DiffuseSampler, uv);
    float outlineAlpha = texture(OutlineSampler, uv).a;
    float inside = step(0.01, texture(MaskSampler, uv).a);
    outlineAlpha *= (1.0 - inside);
    outlineAlpha = smoothstep(0.02, 0.08, outlineAlpha);

    float itemDepth = 1.0;
    float maxInside = 0.0;
    vec2 oneTexel = 1.0 / vec2(textureSize(MaskSampler, 0));
    for (int r = 0; r <= 6; r++) {
        float fr = float(r);
        vec2 offsets[8] = vec2[](
            vec2(-fr,  0.0),
            vec2( fr,  0.0),
            vec2( 0.0, -fr),
            vec2( 0.0,  fr),
            vec2(-fr, -fr),
            vec2( fr, -fr),
            vec2(-fr,  fr),
            vec2( fr,  fr)
        );
        for (int i = 0; i < 8; i++) {
            vec2 sampleUv = clamp(uv + oneTexel * offsets[i], vec2(0.0), vec2(1.0));
            float a = texture(MaskSampler, sampleUv).a;
            if (a > maxInside) {
                maxInside = a;
                itemDepth = texture(MaskDepthSampler, sampleUv).r;
            }
        }
    }

    if (outlineAlpha > 0.0 && maxInside > 0.01) {
        float sceneDepth = texture(MainDepthSampler, uv).r;
        float occludedByForeground = (sceneDepth + 0.0005 < itemDepth) ? 1.0 : 0.0;
        outlineAlpha *= (1.0 - occludedByForeground);
    }

    vec3 outlineColor = vec3(OutlineR, OutlineG, OutlineB);
    vec3 outRgb = mix(scene.rgb, outlineColor, outlineAlpha);
    fragColor = vec4(outRgb, 1.0);
}
