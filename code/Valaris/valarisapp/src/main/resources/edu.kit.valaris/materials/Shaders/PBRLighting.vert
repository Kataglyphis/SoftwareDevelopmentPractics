//authors Jonas Heinle, Frederik Lingg

#ifdef IS_PARTICLE
uniform mat4 g_ViewProjectionMatrix;
uniform mat4 g_ViewMatrix;
#else
#import "Common/ShaderLib/Instancing.glsllib"
#endif

in vec3 inPosition;

#ifndef IS_PARTICLE
in vec3 inNormal;
#endif

//variables written per vertex stage to fragment stage
out vec3 vNormal;
out vec3 vPosition;

#ifdef USE_METALLIC_ATTRIBUTE
in float inTexCoord2;
#endif

#ifdef USE_ROUGHNESS_ATTRIBUTE
in float inTexCoord3;
#endif

#ifdef USE_SPECULAR_ATTRIBUTE
in float inTexCoord4;
#endif

#ifdef USE_COLOR_ATTRIBUTE
in vec4 inColor;
#endif

#ifdef USE_EMISSIVE_ATTRIBUTE
in vec4 inTexCoord5;
#endif

//if textures are used, use texcoords
#if defined(USE_METALLIC_TEXTURE) | defined(USE_ROUGHNESS_TEXTURE) | defined(USE_SPECULAR_TEXTURE) | defined(USE_COLOR_TEXTURE) | defined(USE_EMISSIVE_TEXTURE) | defined(USE_NORMAL_TEXTURE)
in vec2 inTexCoord;
out vec2 vTexCoord0;
#endif

//Normalmap
#ifdef USE_NORMAL_TEXTURE
in vec4 inTangent;
out vec4 vTangent;
#endif

//image based lighting
#ifdef USE_IBL
uniform vec3 g_CameraPosition;
out vec3 vWorldNormal;
out vec3 vWorldView;
#endif

//ramps
#ifdef NUM_METALLIC_POINTS
uniform vec2 m_MetallicPoints[NUM_METALLIC_POINTS];
#endif

#ifdef NUM_ROUGHNESS_POINTS
uniform vec2 m_RoughnessPoints[NUM_ROUGHNESS_POINTS];
#endif

#ifdef NUM_SPECULAR_POINTS
uniform vec2 m_SpecularPoints[NUM_SPECULAR_POINTS];
#endif

#ifdef NUM_COLOR_POINTS
uniform vec4 m_ColorPoints[NUM_COLOR_POINTS];
#endif

#ifdef NUM_EMISSIVE_POINTS
uniform vec4 m_EmissivePoints[NUM_EMISSIVE_POINTS];
#endif

#if defined(NUM_METALLIC_POINTS) | defined(NUM_ROUGHNESS_POINTS) | defined(NUM_SPECULAR_POINTS) | defined(NUM_COLOR_POINTS) | defined(NUM_EMISSIVE_POINTS)
    #ifdef USE_RAMP_FACTOR_ATTRIBUTE
    in float inTexCoord6;
    #else
    uniform float m_RampFactor;
    #endif
#endif

//varyings
#if defined(USE_METALLIC_ATTRIBUTE) | defined(NUM_METALLIC_POINTS)
out float vMetallic;
#endif

#if defined(USE_ROUGHNESS_ATTRIBUTE) | defined(NUM_ROUGHNESS_POINTS)
out float vRoughness;
#endif

#if defined(USE_SPECULAR_ATTRIBUTE) | defined(NUM_SPECULAR_POINTS)
out float vSpecular;
#endif

#if defined(USE_COLOR_ATTRIBUTE) | defined(NUM_COLOR_POINTS)
out vec4 vColor;
#endif

#if defined(USE_EMISSIVE_ATTRIBUTE) | defined(NUM_EMISSIVE_POINTS)
out vec4 vEmissive;
#endif

#ifdef IS_PARTICLE
in mat4 inTexCoord7;
#endif

void main() {

    //init varyings
    #if defined(USE_METALLIC_ATTRIBUTE) | defined(NUM_METALLIC_POINTS)
    vMetallic = 1.0;
    #endif

    #if defined(USE_ROUGHNESS_ATTRIBUTE) | defined(NUM_ROUGHNESS_POINTS)
    vRoughness = 1.0;
    #endif

    #if defined(USE_SPECULAR_ATTRIBUTE) | defined(NUM_SPECULAR_POINTS)
    vSpecular = 1.0;
    #endif

    #if defined(USE_COLOR_ATTRIBUTE) | defined(NUM_COLOR_POINTS)
    vColor = vec4(1.0, 1.0, 1.0, 1.0);
    #endif

    #if defined(USE_EMISSIVE_ATTRIBUTE) | defined(NUM_EMISSIVE_POINTS)
    vEmissive = vec4(0.0, 0.0, 0.0, 0.0);
    #endif


    //apply vertexattributes
    #ifdef USE_METALLIC_ATTRIBUTE
    vMetallic = inTexCoord2;
    #endif

    #ifdef USE_ROUGHNESS_ATTRIBUTE
    vRoughness = inTexCoord3;
    #endif

    #ifdef USE_SPECULAR_ATTRIBUTE
    vSpecular = inTexCoord4;
    #endif

    #ifdef USE_COLOR_ATTRIBUTE
    vColor = inColor;
    #endif

    #ifdef USE_EMISSIVE_ATTRIBUTE
    vEmissive = inTexCoord5;
    #endif

    #if defined(USE_METALLIC_TEXTURE) | defined(USE_ROUGHNESS_TEXTURE) | defined(USE_SPECULAR_TEXTURE) | defined(USE_COLOR_TEXTURE) | defined(USE_NORMAL_TEXTURE)
    vTexCoord0 = inTexCoord;
    #endif

    #ifdef USE_NORMAL_TEXTURE
    vTangent = vec4(TransformNormal(inTangent.xyz).xyz, inTangent.w);
    #endif

    #if defined(NUM_METALLIC_POINTS) | defined(NUM_ROUGHNESS_POINTS) | defined(NUM_SPECULAR_POINTS) | defined(NUM_COLOR_POINTS) | defined(NUM_EMISSIVE_POINTS)
        #ifdef USE_RAMP_FACTOR_ATTRIBUTE
        float rampFactor = inTexCoord6;
        #else
        float rampFactor = m_RampFactor;
        #endif
    #endif

    //apply ramps
    #ifdef NUM_METALLIC_POINTS
    //base value
    float metallicRamp = m_MetallicPoints[NUM_METALLIC_POINTS - 1].x;

    //find right intervall
    for(int i = 0; i < NUM_METALLIC_POINTS; i++) {
        if(m_MetallicPoints[i].y >= rampFactor) {
            //get 2 enclosing points
            vec2 point0 = i > 0 ? m_MetallicPoints[i - 1] : vec2(m_MetallicPoints[i].x, 0.0);
            vec2 point1 = m_MetallicPoints[i];

            float factor = (rampFactor - point0.y) / (point1.y - point0.y);
            metallicRamp = mix(point0.x, point1.x, factor);
            break;
        }
    }

    //apply ramp
    vMetallic *= metallicRamp;
    #endif

    #ifdef NUM_ROUGHNESS_POINTS
    //base value
    float roughnessRamp = m_RoughnessPoints[NUM_ROUGHNESS_POINTS - 1].x;

    //find right intervall
    for(int i = 0; i < NUM_ROUGHNESS_POINTS; i++) {
        if(m_RoughnessPoints[i].y >= rampFactor) {
            //get 2 enclosing points
            vec2 point0 = i > 0 ? m_RoughnessPoints[i - 1] : vec2(m_RoughnessPoints[i].x, 0.0);
            vec2 point1 = m_RoughnessPoints[i];

            float factor = (rampFactor - point0.y) / (point1.y - point0.y);
            roughnessRamp = mix(point0.x, point1.x, factor);
            break;
        }
    }

    //apply ramp
    vRoughness *= roughnessRamp;
    #endif

    #ifdef NUM_SPECULAR_POINTS
    //base value
    float specularRamp = m_SpecularPoints[NUM_SPECULAR_POINTS - 1].x;

    //find right intervall
    for(int i = 0; i < NUM_SPECULAR_POINTS; i++) {
        if(m_SpecularPoints[i].y >= rampFactor) {
            //get 2 enclosing points
            vec2 point0 = i > 0 ? m_SpecularPoints[i - 1] : vec2(m_SpecularPoints[i].x, 0.0);
            vec2 point1 = m_SpecularPoints[i];

            float factor = (rampFactor - point0.y) / (point1.y - point0.y);
            specularRamp = mix(point0.x, point1.x, factor);
            break;
        }
    }

    //apply ramp
    vSpecular *= specularRamp;
    #endif

    #ifdef NUM_COLOR_POINTS
    //base value
    vec3 colorRamp = m_ColorPoints[NUM_COLOR_POINTS - 1].rgb;

    //find right intervall
    for(int i = 0; i < NUM_COLOR_POINTS; i++) {
        if(m_ColorPoints[i].a >= rampFactor) {
            //get 2 enclosing points
            vec4 point0 = i > 0 ? m_ColorPoints[i - 1] : vec4(m_ColorPoints[i].rgb, 0.0);
            vec4 point1 = m_ColorPoints[i];

            float factor = (rampFactor - point0.a) / (point1.a - point0.a);
            colorRamp = mix(point0.rgb, point1.rgb, factor);
            break;
        }
    }

    //apply ramp
    vColor *= vec4(colorRamp, 1.0);
    #endif

    #ifdef NUM_EMISSIVE_POINTS
    //base value
    vec3 emissiveRamp = m_EmissivePoints[NUM_EMISSIVE_POINTS - 1].rgb;

    //find right intervall
    for(int i = 0; i < NUM_EMISSIVE_POINTS; i++) {
        if(m_EmissivePoints[i].a >= rampFactor) {
            //get 2 enclosing points
            vec4 point0 = i > 0 ? m_EmissivePoints[i - 1] : vec4(m_EmissivePoints[i].rgb, 0.0);
            vec4 point1 = m_EmissivePoints[i];

            float factor = (rampFactor - point0.a) / (point1.a - point0.a);
            emissiveRamp = mix(point0.rgb, point1.rgb, factor);
            break;
        }
    }

    //apply ramp
    vEmissive += vec4(emissiveRamp, 1.0);
    #endif

    vec4 modelSpacePos = vec4(inPosition, 1.0);

    #ifdef IS_PARTICLE
        //particles have their WorldTransform saved as a matrix
        vec3 worldPos = (inTexCoord7 * modelSpacePos).xyz;
        //particles local normal is just y up
        vec3 worldNormal = (inTexCoord7 * vec4(0.0, 1.0, 0.0, 0.0)).xyz;

        //populate varyings
        vPosition = (g_ViewMatrix * vec4(worldPos, 1.0)).xyz;
        vNormal = normalize((g_ViewMatrix * vec4(worldNormal, 0.0)).xyz);
        //output position
        vec4 clipPos = g_ViewProjectionMatrix * vec4(worldPos, 1.0);
    #else
        vec3 worldPos = TransformWorld(modelSpacePos).xyz;
        //other models use vertex attributes
        #ifdef USE_IBL
        vec3 worldNormal = TransformWorld(vec4(inNormal, 0.0)).xyz;
        #endif
        //populate varyings
        vPosition = TransformWorldView(modelSpacePos).xyz;
        vNormal = normalize(TransformNormal(inNormal));
        //output position
        vec4 clipPos = TransformWorldViewProjection(modelSpacePos);
    #endif

    //calculate varyings for image based lighting
    #ifdef USE_IBL
    vWorldView = normalize(g_CameraPosition - worldPos).xyz;
    vWorldNormal = normalize(worldNormal);
    #endif

    gl_Position = clipPos;
}
