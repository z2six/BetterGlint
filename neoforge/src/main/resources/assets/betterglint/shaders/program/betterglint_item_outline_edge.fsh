#version 150

uniform sampler2D DiffuseSampler;

uniform float OutlineA;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {
    vec2 uv = clamp(texCoord, vec2(0.0), vec2(1.0));

    float threshold = 0.5;
    float aa = 0.05;

    float center = texture(DiffuseSampler, uv).a;

    float maxNeighbor = 0.0;
    maxNeighbor = max(maxNeighbor, texture(DiffuseSampler, clamp(uv + oneTexel * vec2(-1.0,  0.0), vec2(0.0), vec2(1.0))).a);
    maxNeighbor = max(maxNeighbor, texture(DiffuseSampler, clamp(uv + oneTexel * vec2( 1.0,  0.0), vec2(0.0), vec2(1.0))).a);
    maxNeighbor = max(maxNeighbor, texture(DiffuseSampler, clamp(uv + oneTexel * vec2( 0.0, -1.0), vec2(0.0), vec2(1.0))).a);
    maxNeighbor = max(maxNeighbor, texture(DiffuseSampler, clamp(uv + oneTexel * vec2( 0.0,  1.0), vec2(0.0), vec2(1.0))).a);
    maxNeighbor = max(maxNeighbor, texture(DiffuseSampler, clamp(uv + oneTexel * vec2(-1.0, -1.0), vec2(0.0), vec2(1.0))).a);
    maxNeighbor = max(maxNeighbor, texture(DiffuseSampler, clamp(uv + oneTexel * vec2( 1.0, -1.0), vec2(0.0), vec2(1.0))).a);
    maxNeighbor = max(maxNeighbor, texture(DiffuseSampler, clamp(uv + oneTexel * vec2(-1.0,  1.0), vec2(0.0), vec2(1.0))).a);
    maxNeighbor = max(maxNeighbor, texture(DiffuseSampler, clamp(uv + oneTexel * vec2( 1.0,  1.0), vec2(0.0), vec2(1.0))).a);

    float inside = smoothstep(threshold - aa, threshold + aa, center);
    float neighborInside = smoothstep(threshold - aa, threshold + aa, maxNeighbor);

    float edge = (1.0 - inside) * neighborInside;
    fragColor = vec4(1.0, 1.0, 1.0, edge * OutlineA);
}
