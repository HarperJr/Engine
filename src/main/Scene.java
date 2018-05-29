package main;

import lightning.Light;
import meshes.MeshModel;
import shadows.ShadowMap;

import java.util.ArrayList;
import java.util.Collection;

public class Scene {

    private final Collection<MeshModel> models = new ArrayList<>();
    private final Collection<Light> lights = new ArrayList<>();
    private final Collection<ShadowMap> shadowMaps = new ArrayList<>();


}
