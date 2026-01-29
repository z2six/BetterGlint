#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D MaskSampler;

uniform float OutlineR;
uniform float OutlineG;
uniform float OutlineB;
uniform float OutlineA;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {
    vec2 clampedTexCoord = clamp(texCoord, vec2(0.0), vec2(1.0));
    vec4 scene = texture(DiffuseSampler, clampedTexCoord);

    float center = texture(MaskSampler, clampedTexCoord).a;
    float threshold = 0.01;

    float maxNeighbor = 0.0;
    const int RADIUS = 10;
    for (int i = 1; i <= RADIUS; i++) {
        float fi = float(i);
        vec2 offsets[8] = vec2[](
            vec2(-fi,  0.0),
            vec2( fi,  0.0),
            vec2( 0.0, -fi),
            vec2( 0.0,  fi),
            vec2(-fi, -fi),
            vec2( fi, -fi),
            vec2(-fi,  fi),
            vec2( fi,  fi)
        );

        for (int j = 0; j < 8; j++) {
            vec2 sampleCoord = clamp(clampedTexCoord + oneTexel * offsets[j], vec2(0.0), vec2(1.0));
            maxNeighbor = max(maxNeighbor, texture(MaskSampler, sampleCoord).a);
        }
    }

    float inside = step(threshold, center);
    float neighborInside = step(threshold, maxNeighbor);

    float outline = (1.0 - inside) * neighborInside;
    float alpha = outline * OutlineA;

    vec3 outlineColor = vec3(OutlineR, OutlineG, OutlineB);
    vec3 outRgb = mix(scene.rgb, outlineColor, alpha);

    fragColor = vec4(outRgb, 1.0);
}
