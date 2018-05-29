package main;

public final class Timer {

    private static float timerSpeed;
    private static long prevTimeMills;
    private static long currentTimeMills;
    private static float deltaTime;

    static {
        prevTimeMills = System.currentTimeMillis();
        currentTimeMills = 0L;
        deltaTime = 0f;
    }

    public static void setTimerSpeed(float speed) {
        timerSpeed = speed;
    }
    public static void update() {
        currentTimeMills = System.currentTimeMillis();
        float partial = (float)100000000L / timerSpeed;
        float delta = (float) (currentTimeMills - prevTimeMills) / partial;

        deltaTime = delta * timerSpeed;
        prevTimeMills = currentTimeMills;
    }


    public static float getDelta() {
        return deltaTime;
    }
}
