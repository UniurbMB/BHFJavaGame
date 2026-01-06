package main;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import engine.ShaderProgram;
import engine.rendering_primitives.Canvas;
import engine.rendering_primitives.Rect;
import engine.rendering_primitives.Sprite;
import engine.events.KeyListener;
import engine.events.MouseListener;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Main {
	
	private static int WINDOW_WIDTH = 900;
	private static int WINDOW_HEIGHT = 900;
	private static String WINDOW_TITLE = new String("Hello world!");
	private static long window;
	private static float delta;
	private static float lastTime;
	private static int desiredFramerate = 60;
	private static float frameInterval = 1.0f / (float)desiredFramerate;
	
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
		
		glfwSetCursorPosCallback(window, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(window, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(window, KeyListener::keyCallback);
		
		delta = 0.0f;
		lastTime = (float)glfwGetTime();
		
	}
	
	private static void update() {
		
		Rect r = new Rect(0.0f, 0.0f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		Rect r2 = new Rect(-0.5f, -0.65f, 0.125f, 0.45f, 0.0f, 1.0f, 0.0f);
		Rect r3 = new Rect(0.53125f, 0.0f, 0.46875f, 1.0f, 0.98f, 0.15f, 0.65f);
		Sprite s = new Sprite("src/assets/images/test.png", 0.5f, 0.5f, 0.25f, 0.6f, 1.0f, 1.0f, 0.0f);
		Sprite s2 = new Sprite("src/assets/images/test.png", -0.5f, 0.5f, 1.0f, 0.25f, 1.0f, 1.0f, 1.0f);
		Canvas c = new Canvas(-0.46875f, 0.0f, 0.53125f, 1.0f, 544, 1024, 0.25f, 0.25f, 0.25f);
		Canvas c2 = new Canvas(0.0f, 0.0f, 1.0f, 1.0f, 600, 600);
		
		while(!glfwWindowShouldClose(window)) {
			
			lastTime = (float)glfwGetTime();
			
			glfwPollEvents();
			
			c.beginDrawing(WINDOW_WIDTH, WINDOW_HEIGHT);
			glClear(GL_COLOR_BUFFER_BIT);
			
			r.render();
			r2.render();
			s.render();
			s2.render();
			
			c.stopDrawing();
			c2.beginDrawing(WINDOW_WIDTH, WINDOW_HEIGHT);
			c.render();
			r3.render();
			c2.stopDrawing();
			glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT);
			c2.render();
			
			if(KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
				s2.pos.x += 2.5f * delta;
			}
			if(KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
				s2.pos.x -= 2.5f * delta;
			}
			if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
				glfwSetWindowShouldClose(window, true);
			}
			
			glfwSwapBuffers(window);
			
			delta = (float)glfwGetTime() - lastTime;
			if(delta < frameInterval) {
				float d = frameInterval - delta;
				int m = (int)(d * 1000.0f);
				
				try {
					Thread.sleep(m);
					delta = (float)glfwGetTime() - lastTime;
				}catch(InterruptedException e) {
					
				}
			}
			
			//int FPS = (int)(1.0f/delta);
			//System.out.println("FPS: "  + FPS);
			
		}
		
	}
	
	private static void cleanup() {
		Rect.cleanUp();
		Sprite.cleanUp();
		Canvas.cleanUp();
		glfwDestroyWindow(window);
		glfwSetErrorCallback(null).free();
		
	}
	
}
