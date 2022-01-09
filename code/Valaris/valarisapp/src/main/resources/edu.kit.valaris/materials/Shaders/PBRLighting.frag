//authors: Jonas Heinle, Frederik Lingg

#import "edu.kit.valaris/materials/Shaders/Lib/PrincipledBRDF.glsllib"
#import "Common/ShaderLib/Lighting.glsllib"

#ifdef USE_IBL
#import "edu.kit.valaris/materials/Shaders/Lib/ImportanceSample.glsllib"
#import "Common/ShaderLib/Optics.glsllib"
#endif
//for calculating lighting in view space
uniform mat4 g_ViewMatrix;
uniform vec4 g_LightData[NB_LIGHTS];

in vec3 vNormal;
in vec3 vPosition;

//material params
#ifdef USE_METALLIC_TEXTURE
uniform sampler2D m_MetallicTexture;
#endif
#if defined(USE_METALLIC_ATTRIBUTE) | defined(NUM_METALLIC_POINTS)
varying float vMetallic;
#endif
uniform float m_Metallic;

#ifdef USE_ROUGHNESS_TEXTURE
uniform sampler2D m_RoughnessTexture;
#endif
#if defined(USE_ROUGHNESS_ATTRIBUTE) | defined(NUM_ROUGHNESS_POINTS)
in float vRoughness;
#endif
uniform float m_Roughness;

#ifdef USE_SPECULAR_TEXTURE
uniform sampler2D m_SpecularTexture;
#endif
#if defined(USE_SPECULAR_ATTRIBUTE) | defined(NUM_SPECULAR_POINTS)
in float vSpecular;
#endif
uniform float m_Specular;

#ifdef USE_COLOR_TEXTURE
uniform sampler2D m_ColorTexture;
#endif
#if defined(USE_COLOR_ATTRIBUTE) | defined(NUM_COLOR_POINTS)
in vec4 vColor;
#endif
uniform vec4 m_BaseColor;

//emission
#ifdef USE_EMISSIVE_TEXTURE
uniform sampler2D m_EmissiveTexture;
#endif
#if defined(USE_EMISSIVE_ATTRIBUTE) | defined(NUM_EMISSIVE_POINTS)
in vec4 vEmissive;
#endif
uniform vec4 m_Emissive;

//normalmapping
#ifdef USE_NORMAL_TEXTURE
uniform sampler2D m_NormalTexture;
in vec4 vTangent;
#endif

//if textures are used, use texcoords
#if defined(USE_METALLIC_TEXTURE) | defined(USE_ROUGHNESS_TEXTURE) | defined(USE_SPECULAR_TEXTURE) | defined(USE_COLOR_TEXTURE) | defined(USE_EMISSIVE_TEXTURE) | defined(USE_NORMAL_TEXTURE)
in vec2 vTexCoord0;
#endif

#ifdef USE_IBL
uniform ENVMAP m_EnvMap;
uniform int m_EnvSamples;

//compute reflection in worldspace
in vec3 vWorldNormal;
in vec3 vWorldView;
#endif

void main() {
    //view direction
    #ifdef USE_NORMAL_TEXTURE
    mat3 tbnMat = mat3(vTangent.xyz, vTangent.w * cross((vNormal), (vTangent.xyz)), vNormal.xyz);
    if(!gl_FrontFacing) {
        tbnMat[2] = -tbnMat[2];
    }
    vec3 view = normalize(-vPosition.xyz * tbnMat);
    #else
    vec3 view = normalize(-vPosition.xyz);
    #endif


    //normal
    #ifdef USE_NORMAL_TEXTURE
    vec4 texNormal = texture2D(m_NormalTexture, vTexCoord0);
    vec3 normal = (texNormal.xyz * vec3(2.0, -2.0, 2.0) - vec3(1.0, -1.0, 1.0));

    normal = normalize(normal);
    #else
    vec3 normal = normalize(vNormal);
    if(!gl_FrontFacing) {
        normal = -normal;
    }
    #endif

    //set material parameters
    float metallic = 1;
    #ifdef USE_METALLIC_TEXTURE
    metallic *= texture2D(m_MetallicTexture, vTexCoord0).r;
    #endif
    #if defined(USE_METALLIC_ATTRIBUTE) | defined(NUM_METALLIC_POINTS)
    metallic *= vMetallic;
    #endif
    metallic *= m_Metallic;

    float roughness = 1;
    #ifdef USE_ROUGHNESS_TEXTURE
    roughness *= texture2D(m_RoughnessTexture, vTexCoord0).r;
    #endif
    #if defined(USE_ROUGHNESS_ATTRIBUTE) | defined(NUM_ROUGHNESS_POINTS)
    roughness *= vRoughness;
    #endif
    roughness *= m_Roughness;

    float specular = 1;
    #ifdef USE_SPECULAR_TEXTURE
    specular *= texture2D(m_SpecularTexture, vTexCoord0).r;
    #endif
    #if defined(USE_SPECULAR_ATTRIBUTE) | defined(NUM_SPECULAR_POINTS)
    specular *= vSpecular;
    #endif
    specular *= m_Specular;

    //blend colors
    vec4 color = vec4(1.0, 1.0, 1.0, 1.0);
    #ifdef USE_COLOR_TEXTURE
    color *= texture2D(m_ColorTexture, vTexCoord0);
    #endif
    #if defined(USE_COLOR_ATTRIBUTE) | defined(NUM_COLOR_POINTS)
    color *= vColor;
    #endif

    color *= m_BaseColor;

    //handle the case that roughness is given as gloss
    #ifdef IS_GLOSS
    roughness = 1.0 - roughness;
    #endif

    vec3 litColor = vec3(0.0, 0.0, 0.0);
    for(int i = 0; i < NB_LIGHTS; i+=3){
        vec4 lightColor = g_LightData[i + 0];
        vec4 lightData1 = g_LightData[i + 1];

        vec4 lightDir;
        vec3 lightVec;

        lightComputeDir(vPosition, lightColor.w, lightData1, lightDir, lightVec);
        float intensity = computeSpotFalloff(g_LightData[i + 2], lightVec) * lightDir.w;

//        //lightdirection
        #ifdef USE_NORMAL_TEXTURE
        //compute in tangentspace
        vec3 normedLightDir = normalize(lightDir.xyz * tbnMat);
        #else
        //compute in viewspace
        vec3 normedLightDir = normalize(lightDir.xyz);
        #endif

        // half-vector
        vec3 normedH = normalize(lightDir.xyz + view);//g_LightDirection.xyz + viewDir);

        //calculating dots
        float cosThetaH = dot(normal, normedH);
        float cosThetaL = dot(normal, normedLightDir);
        float cosThetaV = dot(normal, view);
        float cosThetaD = dot(normedLightDir, normedH);

        //calc color from this one light
        vec3 pixelColor = calcPixelColor(color.rgb, roughness, specular, metallic, intensity, cosThetaL, cosThetaV, cosThetaD, cosThetaH) * lightColor.rgb;

        //sum lights to get result
        litColor += pixelColor;
    }

    #ifdef USE_IBL
    litColor += sampleImage(color.rgb, roughness, specular, metallic, m_EnvMap, m_EnvSamples, vWorldView, vWorldNormal);
    #endif

    litColor += m_Emissive.rgb;
    #if defined(USE_EMISSIVE_ATTRIBUTE) | defined(NUM_EMISSIVE_POINTS)
    litColor += vEmissive.rgb;
    #endif
    #ifdef USE_EMISSIVE_TEXTURE
    litColor += texture2D(m_EmissiveTexture, vTexCoord0).rgb;
    #endif

	gl_FragColor = vec4(litColor, 1.0);
}
