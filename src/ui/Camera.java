package ui;

import main.Timer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private Vector3f velocity;

    private Vector3f lookDirection;
    private float near;
    private float far;

    private Quaternionf rotation;
    private Quaternionf angularVelocity;

    private float speed;

    public void update() {
        Vector3f prevPosition = position;
        position = prevPosition.add(velocity.mul(Timer.getDelta() * speed));
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public void setAngularVelocity(Quaternionf angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    //TODO set update look, setup matix, and so on
}
