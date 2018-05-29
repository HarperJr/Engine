package main;


import meshes.MeshModel;
import meshes.importers.MeshImporter;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import renderers.IRenderer;
import shaders.ShaderShadows;
import shaders.ShaderSurface;
import shaders.ShaderUI;
import shadows.ShadowMap;
import ui.UI;
import util.MatrixUtils;
import util.ShaderUtils;
import java.nio.IntBuffer;
import java.util.List;

public class Application {

    private long wnd;

    private int displayWidth;
    private int displayHeight;
    private boolean fullscreen;

    private boolean running;

    static {
        Config.initConfig();
    }
    
    public Application() {
        running = false;
       Timer.setTimerSpeed(60);
    }

    private void init() throws RuntimeException {
        GL.createCapabilities();

        ShaderUtils.initShader(new ShaderSurface());
        ShaderUtils.initShader(new ShaderShadows());
        ShaderUtils.initShader(new ShaderUI());

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glClearColor(0.0f, 0.85f, 0.85f, 1.0f);
    }

    public void run() {

        try {
            fullscreen = Config.getBool("fullscreen-mode");
            wnd = initDisplay("Engine", Config.getInt("display-width"), Config.getInt("display-height"), fullscreen);
            init();

            running = true;


            //Content created for a while
            MeshModel mesh = MeshImporter.loadModel("SentinelLingerie.obj");
            mesh.dispose();


            List<IRenderer> renderers = mesh.getRenderers();


            float angle = 0f;
            while (running) {

                if (GLFW.glfwWindowShouldClose(wnd)) shutdown();

                GL11.glClear(0x4100);

                MatrixUtils.setPerspective(60f, (float)displayWidth / (float)displayHeight, 0.1f, 256f);

                MatrixUtils.pushMatrix();

                MatrixUtils.translate(0f, -2.5f, -6.0f);
                MatrixUtils.rotate(angle, 0f, 1f, 0f);
                renderers.forEach(IRenderer::render);

                MatrixUtils.popMatrix();


                GLFW.glfwSwapBuffers(wnd);

                angle = Math.min(angle + 15f * Timer.getDelta(), 360f);

                Timer.update();
                GLFW.glfwPollEvents();
            }
            renderers.forEach(IRenderer::flush);

        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        finally {
            GLFW.glfwTerminate();
            ShaderUtils.deleteShaders();
        }

        System.exit(0);
    }

    private void shutdown() {
        running = false;
    }

    private long initDisplay(String title, int w, int h, boolean fullscreen) throws RuntimeException {
        if (!GLFW.glfwInit()) throw new RuntimeException("Unable to init GLFW");

        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidMode == null) {
            throw new RuntimeException("Unable to get vid mode");
        }

        GLFW.glfwDefaultWindowHints();

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, 60);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        long window = GLFW.glfwCreateWindow(w, h, title, 0L, 0L);
        displayWidth = w;
        displayHeight = h;

        if (window == 0L) throw new RuntimeException("Unable to create window");

        if (fullscreen) {
            GLFW.glfwSetWindowSize(window, vidMode.width(), vidMode.height());
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(window, width, height);
            GLFW.glfwSetWindowPos(window, (vidMode.width() - width.get(0)) / 2, (vidMode.height() - height.get(0)) / 2);
        }

        GLFW.glfwSetWindowSizeCallback(window, (wnd, winWidth, winHeight) -> {
            displayWidth = winWidth;
            displayHeight = winHeight;
        });

        GLFW.glfwSetWindowCloseCallback(window, wnd -> GLFW.glfwSetWindowShouldClose(wnd, true));

        GLFW.glfwMakeContextCurrent(window);

        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);

        return window;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

}
