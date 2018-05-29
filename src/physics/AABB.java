package physics;

import org.joml.Vector3f;

public class AABB {
    private Vector3f minPosition;
    private Vector3f maxPosition;

    public AABB(Vector3f minPosition, Vector3f maxPosition) {
        this.minPosition = minPosition;
        this.maxPosition = maxPosition;
    }

    public AABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this(new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ));
    }
}
