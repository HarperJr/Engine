package meshes.loaders;

import meshes.Mesh;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderers.Material;
import renderers.MeshRenderer;
import util.TextureUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public final class MeshImporterWaveFront extends MeshImporter {

    private static final String OBJECT = "o ";
    private static final String GROUP = "g ";
    private static final String VERTEX = "v ";
    private static final String TEXCOORD = "vt ";
    private static final String NORMAL = "vn ";
    private static final String FACE = "f ";

    private static final String MTL_LIB = "mtllib ";
    private static final String MTL_USE = "usemtl ";
    private static final String MTL_NEW = "newmtl ";

    private static final String SPECULAR_F = "Ns ";
    private static final String DISSOLVE_F = "d ";
    private static final String AMBIENT = "Ka ";
    private static final String DIFFUSE = "Kd ";
    private static final String SPECULAR = "Ks ";
    private static final String EMISSIVE = "Ke ";
    private static final String ILLUMINATION = "illum ";

    private static final String MAP_AMBIENT = "map_Ka ";
    private static final String MAP_DIFFUSE = "map_Kd ";
    private static final String MAP_SPECULAR = "map_Ks ";

    private final List<Vector3f> loadedVertices = new ArrayList<>();
    private final List<Vector2f> loadedTexCoords = new ArrayList<>();
    private final List<Vector3f> loadedNormals = new ArrayList<>();

    private int verticesOffset;

    @Override
    protected void processMeshSubData(Mesh mesh, String line) throws IllegalArgumentException {

        if (line.length() <= 1) return;

        process(line, GROUP, x -> {
            final Mesh subMesh = new Mesh(x);
            verticesOffset += mesh.getVertexCount();
            loadedMeshes.add(subMesh);
        });

        process(line, VERTEX, x -> loadedVertices.add(toVector3f(x)));

        process(line, TEXCOORD, x -> loadedTexCoords.add(toVector2f(x)));

        process(line, NORMAL, x -> loadedNormals.add(toVector3f(x)));

        process(line, FACE, x -> processFace(x, mesh));

        process(line, MTL_LIB, super::loadMaterial);

        process(line, MTL_USE, x -> {
            final var renderer = (MeshRenderer) mesh.getRenderer();
            if (renderer != null) {
                renderer.setMaterial(findMaterial(x));
            }
        });
    }

    @Override
    protected void processMaterialSubData(Material material, String line) throws IllegalArgumentException {

        if (line.length() <= 1) return;

        process(line, MTL_NEW, x -> loadedMaterials.add(new Material(x)));

        process(line, SPECULAR_F, x -> material.setSpecularFactor(Float.parseFloat(x)));

        process(line, DISSOLVE_F, x -> material.setDissolveFactor(Float.parseFloat(x)));

        process(line, AMBIENT, x -> material.setAmbient(toVector3f(x)));

        process(line, DIFFUSE, x -> material.setDiffuse(toVector3f(x)));

        process(line, SPECULAR, x -> material.setSpecular(toVector3f(x)));

        process(line, EMISSIVE, x -> material.setEmissive(toVector3f(x)));

        process(line, MAP_AMBIENT, x -> material.setMapAmbient(TextureUtils.getTexture(x)));

        process(line, MAP_DIFFUSE, x -> material.setMapDiffuse(TextureUtils.getTexture(x)));

        process(line, MAP_SPECULAR, x -> material.setMapSpecular(TextureUtils.getTexture(x)));
    }

    private void processFace(String face, Mesh mesh) {
        Arrays.stream(face.split(" ")).forEach(v -> {
            int index;
            if (v.contains("/")) {
                int[] indexSet = Arrays.stream(v.split("/")).mapToInt(x -> Integer.parseInt(x) - 1).toArray();
                index = indexSet[0];
                Vector3f vp = loadedVertices.get(index);
                if (!mesh.getVertices().contains(vp)) {
                    mesh.getVertices().set(index - verticesOffset, vp);
                    mesh.getTexCoords().set(index - verticesOffset, loadedTexCoords.get(indexSet[1]));
                    if (indexSet.length > 2) {
                        mesh.getNormals().set(index - verticesOffset, loadedNormals.get(indexSet[2]));
                    }
                }
            } else if (v.contains("//")) {
                int[] indexSet = Arrays.stream(v.split("//")).mapToInt(x -> Integer.parseInt(x) - 1).toArray();
                index = indexSet[0];
                Vector3f vp = loadedVertices.get(index);
                if (!mesh.getVertices().contains(vp)) {
                    mesh.getVertices().set(index - verticesOffset, vp);
                    mesh.getNormals().set(index - verticesOffset, loadedNormals.get(indexSet[1]));
                }
            } else {
                index = Integer.parseInt(v) - 1;
                Vector3f vp = loadedVertices.get(index);
                if (!mesh.getVertices().contains(vp)) mesh.getVertices().set(index - verticesOffset, vp);
            }
            mesh.getIndices().add(index - verticesOffset);
        });
    }

    @Override
    protected void clear() {
        super.clear();
        loadedVertices.clear();
        loadedTexCoords.clear();
        loadedNormals.clear();

        verticesOffset = 0;
    }
}
