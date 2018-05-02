package main;

public class Timer {

    private float timerSpeed;
    private long prevTimeMills;
    private long currentTimeMills;
    private float deltaTime;

    public Timer(float speed) {
        prevTimeMills = System.currentTimeMillis();
        currentTimeMills = 0L;
        timerSpeed = speed;
        deltaTime = 0f;
    }

    public void update() {
        currentTimeMills = System.currentTimeMillis();
        float partial = (float)100000000L / timerSpeed;
        float delta = (float) (currentTimeMills - prevTimeMills) / partial;

        deltaTime = delta * timerSpeed;
        prevTimeMills = currentTimeMills;
    }


    public float getDelta() {
        return deltaTime;
    }
}
