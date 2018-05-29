package shadows;

import org.joml.Vector3f;
import physics.AABB;
import renderers.IRenderer;
import renderers.ShadowMapRenderer;
import util.MatrixUtils;

import java.nio.FloatBuffer;

public class ShadowMap {

    private final IRenderer shadowMapRenderer;
    private final int dimension;

    private AABB shadowBox;

    public ShadowMap(int dimension) {
        shadowMapRenderer = new ShadowMapRenderer(this);
        this.dimension = dimension;
    }

    public void generateOrtoMap() {
        final Vector3f camPos = getCameraPosition();


    }

    public void generatePerspectiveMap() {
        final Vector3f camPos = getCameraPosition();
    }

    public Vector3f getCameraPosition() {
        FloatBuffer buffer = MatrixUtils.getModelViewMatrixAsBuffer();
        return new Vector3f(buffer.get(10), buffer.get(11), buffer.get(12));
    }

    public IRenderer getRenderer() {
        return shadowMapRenderer;
    }

    public int getDimension() {
        return dimension;
    }
}
