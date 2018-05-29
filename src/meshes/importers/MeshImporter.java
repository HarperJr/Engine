package meshes.importers;

import main.Config;
import main.Resources;
import meshes.Mesh;
import meshes.MeshModel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import meshes.Material;
import renderers.MeshRenderer;
import util.TextureUtils;


import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class MeshImporter {

    private static final List<Material> materials = new ArrayList<>();

    public static MeshModel loadModel(String path) {
        return new MeshModel(path, load(path));
    }

    public static Collection<Mesh> load(String path) throws RuntimeException{
        final String sourcePath = Resources.getSourceFolder() + Config.getAttribute("src-obj");
        AIScene scene = Assimp.aiImportFile(sourcePath + path, Assimp.aiProcess_OptimizeMeshes
                | Assimp.aiProcess_FlipUVs | Assimp.aiProcess_JoinIdenticalVertices);

        if (scene == null) throw new RuntimeException("Unable to import mesh " + path);

        final int numMaterials = scene.mNumMaterials();
        final PointerBuffer materialsBuffer = scene.mMaterials();
        Stream.iterate(0, i -> i < numMaterials, i -> i + 1).forEach(i -> {
            final AIMaterial material = AIMaterial.create(materialsBuffer.get(i));
            final Material processedMaterial = processMaterial(material);
            materials.add(processedMaterial);
        });

        final int numMeshes = scene.mNumMeshes();
        final PointerBuffer meshesBuffer = scene.mMeshes();
        Collection<Mesh> meshes = new ArrayList<>();
        Stream.iterate(0, i -> i < numMeshes, i -> i + 1).forEach(i -> {
            final AIMesh mesh = AIMesh.create(meshesBuffer.get(i));
            final Mesh processedMesh = processMesh(mesh);
            meshes.add(processedMesh);
        });

        materials.clear();
        return meshes;
    }

    private static Mesh processMesh(AIMesh sourceMesh) {
        final String meshName = sourceMesh.mName().dataString();
        final Mesh meshGroup = new Mesh(meshName);

        Collection<Vector3f> vertices = new ArrayList<>();
        AIVector3D.Buffer aiVertices = sourceMesh.mVertices();

        while (aiVertices.hasRemaining()) {
            AIVector3D vertex = aiVertices.get();
            vertices.add(new Vector3f(vertex.x(), vertex.y(), vertex.z()));
        }

        Collection<Vector2f> texCoords = new ArrayList<>();
        AIVector3D.Buffer aiTexCoords = sourceMesh.mTextureCoords(0);

        while (aiTexCoords.hasRemaining()) {
            AIVector3D texCoord = aiTexCoords.get();
            texCoords.add(new Vector2f(texCoord.x(), texCoord.y()));
        }

        Collection<Vector3f> normals = new ArrayList<>();
        AIVector3D.Buffer aiNormals = sourceMesh.mNormals();

        while (aiNormals.hasRemaining()) {
            AIVector3D normal = aiNormals.get();
            normals.add(new Vector3f(normal.x(), normal.y(), normal.z()));
        }

        Collection<Integer> indices = new ArrayList<>();
        AIFace.Buffer aiFaces = sourceMesh.mFaces();

        while (aiFaces.hasRemaining()) {
            AIFace face = aiFaces.get();
            IntBuffer aiIndices = face.mIndices();

            while (aiIndices.hasRemaining()) {
                indices.add(aiIndices.get());
            }
        }

        meshGroup.setVertices(vertices.stream().toArray(Vector3f[]::new));
        meshGroup.setTexCoords(texCoords.stream().toArray(Vector2f[]::new));
        meshGroup.setNormals(normals.stream().toArray(Vector3f[]::new));

        meshGroup.setIndinces(indices.stream().mapToInt(Integer::valueOf).toArray());

        MeshRenderer renderer = (MeshRenderer)meshGroup.getRenderer();

        final int materialIndex = sourceMesh.mMaterialIndex();
        if (materialIndex > materials.size()) {
            throw new RuntimeException("Unable to get linked material " + materialIndex);
        }

        Material material = materials.get(materialIndex);
        renderer.setMaterial(material);

        return meshGroup;
    }

    private static Material processMaterial(AIMaterial sourceMaterial) {

        AIString namePointer = AIString.calloc();
        Assimp.aiGetMaterialString(sourceMaterial, Assimp.AI_MATKEY_NAME, Assimp.AI_AISTRING,0, namePointer);

        final String materialName = namePointer.dataString();

        final Material material = new Material(materialName);

        AIColor4D aiAmbient = getColor(sourceMaterial, Assimp.AI_MATKEY_COLOR_AMBIENT);
        material.setAmbient(new Vector3f(aiAmbient.r(), aiAmbient.g(), aiAmbient.b()));
        material.setDissolveFactor(aiAmbient.a());

        AIColor4D aiDiffuse = getColor(sourceMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE);
        material.setDiffuse(new Vector3f(aiDiffuse.r(), aiDiffuse.g(), aiDiffuse.b()));

        AIColor4D aiSpecular = getColor(sourceMaterial, Assimp.AI_MATKEY_COLOR_SPECULAR);
        material.setSpecular(new Vector3f(aiSpecular.r(), aiSpecular.g(), aiSpecular.b()));
        material.setSpecularFactor(aiSpecular.a());

        AIColor4D aiEmissive = getColor(sourceMaterial, Assimp.AI_MATKEY_COLOR_EMISSIVE);
        material.setEmissive(new Vector3f(aiEmissive.r(), aiEmissive.g(), aiEmissive.b()));

        int ambientMap = processTexture(sourceMaterial, Assimp.aiTextureType_AMBIENT);
        material.setMapAmbient(ambientMap);

        int diffuseMap = processTexture(sourceMaterial, Assimp.aiTextureType_DIFFUSE);
        material.setMapDiffuse(diffuseMap);

        int specularMap = processTexture(sourceMaterial, Assimp.aiTextureType_SPECULAR);
        material.setMapSpecular(specularMap);

        return material;
    }

    private static int processTexture(AIMaterial sourceMaterial, int type) {
        AIString aiMapPath = AIString.calloc();
        Assimp.aiGetMaterialTexture(sourceMaterial, type, 0, aiMapPath, (IntBuffer)null, null, null, null, null, null);

        final String mapPath = aiMapPath.dataString();

        if (!mapPath.isEmpty()) {
            return TextureUtils.getTexture(mapPath);
        }

        return TextureUtils.STANDARD;
    }

    private static AIColor4D getColor(AIMaterial sourceMaterial, String type) {
        AIColor4D aiColor = AIColor4D.create();
        int result = Assimp.aiGetMaterialColor(sourceMaterial, type, Assimp.aiTextureType_NONE, 0, aiColor);

        if (result == 0) {
            return aiColor;
        }

        aiColor.set(1f, 1f, 1f, 1f);

        return aiColor;
    }


}
