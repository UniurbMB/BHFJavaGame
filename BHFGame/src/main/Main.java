package main;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import engine.Rect;
import engine.ShaderProgram;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Main {
	
	private static int WINDOW_WIDTH = 1280;
	private static int WINDOW_HEIGHT = 768;
	private static String WINDOW_TITLE = new String("Hello world!");
	private static long window;
	
	public static void main(String[] args) {
		
		init();
		
		update();
		
		cleanup();

	}
	
	private static void init() {
		
		GLFWErrorCallback.createPrint(System.err).set();
		
		if(!org.lwjgl.glfw.GLFW.glfwInit()) {
			throw new IllegalStateException("Could not initialize GLFW!");
		}
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		
		window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE, NULL, NULL);
		glfwMakeContextCurrent(window);
		glfwShowWindow(window);
		
		GL.createCapabilities(true);
		
	}
	
	private static void update() {
		
		Rect r = new Rect(0.0f, 0.0f, 0.0f, 0.0f);
		ShaderProgram s = new ShaderProgram("src/shaders/vertexShader.glsl", "src/shaders/fragmentShader.glsl");
		s.start();
		
		while(!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			r.render();
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
		
		r.destroy();
		s.cleanup();
		
	}
	
	private static void cleanup() {
		
		glfwDestroyWindow(window);
		glfwSetErrorCallback(null).free();
		
	}
	
}
