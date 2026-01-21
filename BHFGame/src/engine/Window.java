package engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.*;
import org.lwjgl.opengl.GL;

import engine.events.KeyListener;
import engine.events.MouseListener;
import engine.events.WindowResizeListener;

public class Window {
	
	public static Matrix4f projection = new Matrix4f();
	
	private final int WINDOW_WIDTH;
	private final int WINDOW_HEIGHT;
	private final String WINDOW_TITLE;
	private long window;
	private float delta;
	private float lastTime;
	private int desiredFramerate;
	private float frameInterval;
	private Scene currentScene = null;
	
	private long audioContext;
	private long audioDevice;
	
	public Window(int width, int height, String title, int desiredFramerate, boolean resizable, boolean fullScreen) {
		this.WINDOW_WIDTH = width;
		this.WINDOW_HEIGHT = height;
		this.WINDOW_TITLE = title;
		this.delta = 0.0f;
		this.lastTime = 0.0f;
		this.desiredFramerate = desiredFramerate;
		this.frameInterval = 1.0f / (float)desiredFramerate;
		
		GLFWErrorCallback.createPrint(System.err).set();
		
		if(!org.lwjgl.glfw.GLFW.glfwInit()) {
			throw new IllegalStateException("Could not initialize GLFW!");
		}
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		
		int resize = GLFW_FALSE;
		if(resizable)resize = GLFW_TRUE;
		glfwWindowHint(GLFW_RESIZABLE, resize);
		
		long monitor = NULL;
		if(fullScreen)monitor = glfwGetPrimaryMonitor();
		
		this.window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE, monitor, NULL);
		
		glfwMakeContextCurrent(this.window);
		glfwShowWindow(this.window);
		
		String defaultAudioDevice = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		audioDevice = alcOpenDevice(defaultAudioDevice);
		
		int[] attributes = {0};
		audioContext = alcCreateContext(audioDevice, attributes);
		alcMakeContextCurrent(audioContext);
		
		ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
		ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
		
		if(!alCapabilities.OpenAL10) {
			throw new RuntimeException("OpenAL10 is not supported on this device!");
		}
		
		GL.createCapabilities(true);
		
		WindowResizeListener.init(WINDOW_WIDTH, WINDOW_HEIGHT);
		glfwSetCursorPosCallback(window, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(window, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(window, KeyListener::keyCallback);
		glfwSetFramebufferSizeCallback(window, WindowResizeListener::windowResizeCallback);
		
		delta = 0.0f;
		lastTime = (float)glfwGetTime();
		
	}
	
	public Window(int width, int height, String title, int desiredFramerate, boolean fullScreen) {
		this(width, height, title, desiredFramerate, false, fullScreen);
	}
	
	public Window(int width, int height, String title, int desiredFramerate) {
		this(width, height, title, desiredFramerate, true, false);
	}
	
	public void beginRenderingFrame() {
		lastTime = (float)glfwGetTime();
		glfwPollEvents();
	}
	
	public void endRenderingFrame() {
		glfwSwapBuffers(window);
		
		delta = (float)glfwGetTime() - lastTime;
		if(delta < frameInterval) {
			float d = frameInterval - delta;
			int m = (int)(d * 1000.0f);
			
			try {
				Thread.sleep(m);
				delta = (float)glfwGetTime() - lastTime;
			}catch(InterruptedException e) {
				System.err.println("Window sleeping error: " + e);
			}
		}
	}
	
	public void update() {
		this.beginRenderingFrame();
		if(this.currentScene!=null)this.currentScene.update(delta);
		this.endRenderingFrame();
	}
	
	public void destroy() {
		if(this.currentScene != null)this.currentScene.cleanUp();
		glfwDestroyWindow(window);
		alcDestroyContext(this.audioContext);
		alcCloseDevice(this.audioDevice);
		glfwSetErrorCallback(null).free();
	}
	
	public boolean shouldClose() {
		return glfwWindowShouldClose(this.window);
	}
	
	public void setShouldClose(boolean shouldClose) {
		glfwSetWindowShouldClose(window, shouldClose);
	}
	
	public void setDesiredFramerate(int desiredFramerate) {
		this.desiredFramerate = desiredFramerate;
		this.frameInterval = 1.0f / (float)desiredFramerate;
	}
	
	public void setCurrentScene(Scene s, boolean shouldCleanUp) {
		if(this.currentScene != null && shouldCleanUp) this.currentScene.cleanUp();
		this.currentScene = s;
		this.currentScene.attachWindow(this);
		this.currentScene.init();
	}
	
	public void setCurrentScene(Scene s) {
		this.setCurrentScene(s, true);
	}
	
	public float getDelta() {
		return this.delta;
	}
	
	public float getFps() {
		return 1.0f/this.delta;
	}
	
	public int getDesiredFramerate() {
		return this.desiredFramerate;
	}

}
