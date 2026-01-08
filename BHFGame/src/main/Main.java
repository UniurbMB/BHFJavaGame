package main;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import engine.Entity;
import engine.ShaderProgram;
import engine.rendering_primitives.Canvas;
import engine.rendering_primitives.Rect;
import engine.rendering_primitives.Renderable;
import engine.rendering_primitives.ShaderRect;
import engine.rendering_primitives.Sprite;
import engine.events.AbstractEvent;
import engine.events.KeyListener;
import engine.events.MouseListener;
import engine.events.WindowResizeListener;

import java.nio.*;
import java.util.Optional;

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
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE, NULL, NULL);
		
		glfwMakeContextCurrent(window);
		glfwShowWindow(window);
		
		
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
	
	private static void update() {
		
		Rect r = new Rect(0.0f, 0.0f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		Rect r2 = new Rect(-0.5f, -0.65f, 0.125f, 0.45f, 0.0f, 1.0f, 0.0f);
		Rect r3 = new Rect(0.53125f, 0.0f, 0.46875f, 1.0f, 0.98f, 0.15f, 0.65f);
		Sprite s = new Sprite("src/assets/images/test.png", 0.5f, 0.5f, 0.25f, 0.6f, 1.0f, 1.0f, 0.0f);
		Sprite s2 = new Sprite("src/assets/images/test.png", -0.5f, 0.5f, 1.0f, 0.25f, 1.0f, 1.0f, 1.0f);
		Canvas c = new Canvas(-0.46875f, 0.0f, 0.53125f, 1.0f, 544, 1024, 0.25f, 0.25f, 0.25f);
		Canvas c2 = new Canvas(0.0f, 0.0f, 1.0f, 1.0f, 600, 600);
		ShaderRect shad = new ShaderRect(0.0f, 0.0f, 1.0f, 1.0f, "src/shaders/vertexShader.glsl", "src/shaders/specialFragmentShader.glsl");
		Entity en = new Entity() {
			public void init() {
				this.renderObject = Optional.of(new Rect());
				this.visible = true;
			}
			public void update() {
				this.renderObject.get().render();
			}
		};
		
		WindowResizeListener.addEvent(new AbstractEvent() {
			private Canvas canv = c2;
			
			public void invoke() {
				if(WindowResizeListener.getWidth() > WindowResizeListener.getHeight()) {
					float ratio = ((float)WindowResizeListener.getHeight())/((float)WindowResizeListener.getWidth());
					canv.size.x = ratio;
					canv.size.y = 1.0f;
				}else {
					float ratio = ((float)WindowResizeListener.getWidth())/((float)WindowResizeListener.getHeight());
					canv.size.y = ratio;
					canv.size.x = 1.0f;
				}
			}
		});
		en.init();
		
		while(!glfwWindowShouldClose(window)) {
			
			lastTime = (float)glfwGetTime();
			
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			
			glfwPollEvents();
			
			c.beginDrawing(WindowResizeListener.getWidth(), WindowResizeListener.getHeight());
			glClear(GL_COLOR_BUFFER_BIT);
			
			r.render();
			r2.render();
			s.render();
			s2.render();
			en.update();
			
			c.stopDrawing();
			
			//if(WindowResizeListener.getWidth() > WindowResizeListener.getHeight()) {
			//	float ratio = ((float)WindowResizeListener.getHeight())/((float)WindowResizeListener.getWidth());
			//	c2.size.x = ratio;
			//	c2.size.y = 1.0f;
			//}else {
			//	float ratio = ((float)WindowResizeListener.getWidth())/((float)WindowResizeListener.getHeight());
			//	c2.size.y = ratio;
			//	c2.size.x = 1.0f;
			//}
			
			c2.beginDrawing(WindowResizeListener.getWidth(), WindowResizeListener.getHeight());
			c.render();
			r3.render();
			c2.stopDrawing();
			glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT);
			shad.render();
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
