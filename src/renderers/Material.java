package renderers;

import jdk.jfr.Category;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import shaders.ShaderSurface;
import util.ShaderUtils;
import util.TextureUtils;

import java.nio.FloatBuffer;

@Category("Data")
@SuppressWarnings("all")
public final class Material {

    private static final FloatBuffer materialBuffer = BufferUtils.createFloatBuffer(16);
    private static final Material standard = new Material("Standard", new Vector3f(1f, 1f, 1f), new Vector3f(1f, 1f, 1f));

    private ShaderSurface shader;

    private final String name;

    private float dissolveFactor;
    private float specularFactor;

    private Vector3f ambient;
    private Vector3f diffuse;
    private Vector3f specular;
    private Vector3f emissive;

    private int mapAmbient;
    private int mapDiffuse;
    private int mapSpecular;

    public Material(String n) {
        name = n;

        shader = (ShaderSurface) ShaderUtils.getShader("ShaderSurface");

        dissolveFactor = 1f;
        specularFactor = 1f;

        ambient = new Vector3f(0f, 0f, 0f);
        diffuse = new Vector3f(0f, 0f, 0f);
        specular = new Vector3f(0f, 0f, 0f);
        emissive = new Vector3f(0f, 0f, 0f);

        mapAmbient = TextureUtils.getStandard();
        mapDiffuse = TextureUtils.getStandard();
        mapSpecular = TextureUtils.getStandard();
    }

    public Material(String n, Vector3f a, Vector3f d) {
        this(n);
        ambient = a;
        diffuse = d;
    }

    public FloatBuffer toBuffer() {
        materialBuffer.clear();
        float[] materialArray = new float[]{
                ambient.x, ambient.y, ambient.z, dissolveFactor,
                diffuse.x, diffuse.y, diffuse.z, 1f,
                specular.x, specular.y, specular.z, specularFactor,
                emissive.x, emissive.y, emissive.z, 1f
        };
        materialBuffer.put(materialArray);
        return materialBuffer.flip();
    }

    public String getName() {
        return name;
    }

    public float getDissolveFactor() {
        return dissolveFactor;
    }

    public void setDissolveFactor(float dissolveFactor) {
        this.dissolveFactor = dissolveFactor;
    }

    public float getSpecularFactor() {
        return specularFactor;
    }

    public void setSpecularFactor(float specularFactor) {
        this.specularFactor = specularFactor;
    }

    public Vector3f getAmbient() {
        return ambient;
    }

    public void setAmbient(Vector3f ambient) {
        this.ambient = ambient;
    }

    public Vector3f getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(Vector3f diffuse) {
        this.diffuse = diffuse;
    }

    public Vector3f getSpecular() {
        return specular;
    }

    public void setSpecular(Vector3f specular) {
        this.specular = specular;
    }

    public Vector3f getEmissive() {
        return emissive;
    }

    public void setEmissive(Vector3f emissive) {
        this.emissive = emissive;
    }

    public int getMapAmbient() {
        return mapAmbient;
    }

    public void setMapAmbient(int mapAmbient) {
        this.mapAmbient = mapAmbient;
    }

    public int getMapDiffuse() {
        return mapDiffuse;
    }

    public void setMapDiffuse(int mapDiffuse) {
        this.mapDiffuse = mapDiffuse;
    }

    public int getMapSpecular() {
        return mapSpecular;
    }

    public void setMapSpecular(int mapSpecular) {
        this.mapSpecular = mapSpecular;
    }

    public static Material getStandard() {
        return standard;
    }

    public ShaderSurface getShader() {
        return shader;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Material) return name.equals(((Material) o).name);
        return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
