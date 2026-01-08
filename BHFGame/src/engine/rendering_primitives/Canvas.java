package engine.rendering_primitives;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL33.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import engine.ShaderProgram;

public class Canvas extends Rect{
	
	private int textureid;
	private int frameBufferid;
	
	private int pixelWidth, pixelHeight;
	private int lastWidth, lastHeight;
	
	private static int vao = 0, vbo = 0, ebo = 0;
	private static ShaderProgram shader = null;
	private static ArrayList<Canvas> canvases = new ArrayList<>();
	
	public Canvas(float posX, float posY,
			float width, float height,
			int pixelWidth, int pixelHeight,
			float red, float green, float blue) {
		super(posX, posY, width, height, red, green, blue);
		this.pixelWidth = pixelWidth;
		this.pixelHeight = pixelHeight;
		Canvas.canvases.add(this);
		if(Canvas.vbo == 0) {
			float[] arr = {	-1.0f, -1.0f, 0.0f,		0.0f, 0.0f,
							-1.0f, 1.0f, 0.0f,		0.0f, 1.0f,
							1.0f, 1.0f, 0.0f,		1.0f, 1.0f,
							1.0f, -1.0f, 0.0f,		1.0f, 0.0f};

			int[] indeces = {	0, 1, 2,
								0, 2, 3};

			Canvas.vao = glGenVertexArrays();
			glBindVertexArray(vao);

			Canvas.vbo = glGenBuffers();
			Canvas.ebo = glGenBuffers();

			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER, arr, GL_STATIC_DRAW);

			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indeces, GL_STATIC_DRAW);
	
			int stride = (3 + 2) * Float.BYTES;
	
			glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3*Float.BYTES);
	
			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
		}
		
		if(Canvas.shader == null) {
			Canvas.shader = new ShaderProgram("src/shaders/spriteVertexShader.glsl", "src/shaders/spriteFragmentShader.glsl");
		}
		
		this.frameBufferid = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, this.frameBufferid);
		
		this.textureid = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, this.textureid);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.pixelWidth, this.pixelHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer)null);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.textureid, 0);
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
	}
	
	public Canvas(float posX, float posY,
				float width, float height,
				int pixelWidth, int pixelHeight) {
		this(posX, posY, width, height, pixelWidth, pixelHeight, 1.0f, 1.0f, 1.0f);
	}
	
	public void beginDrawing(int windowWidth, int windowHeight) {
		this.lastWidth = windowWidth;
		this.lastHeight = windowHeight;
		glBindFramebuffer(GL_FRAMEBUFFER, this.frameBufferid);
		glViewport(0, 0, this.pixelWidth, this.pixelHeight);
		glClearColor(this.color.x, this.color.y, this.color.z, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
	}
	
	public void stopDrawing() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, this.lastWidth, this.lastHeight);
	}
	
	public void render() {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBindVertexArray(vao);
		glBindTexture(GL_TEXTURE_2D, this.textureid);
		int transformPos = Canvas.shader.getAttribLocation("transform");
		int colorPos = Canvas.shader.getAttribLocation("color");
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		new Matrix4f().translate(new Vector3f(this.pos, 0.0f)).scale(new Vector3f(this.size, 1.0f)).get(fb);
		
		Canvas.shader.start();
		glUniformMatrix4fv(transformPos, false, fb);
		glUniform3f(colorPos, 1.0f, 1.0f, 1.0f);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}
	
	public static void cleanUp() {
		if(!Canvas.canvases.isEmpty()) {
			for(Canvas c: Canvas.canvases) {
				glBindFramebuffer(GL_FRAMEBUFFER, 0);
				glDeleteFramebuffers(c.frameBufferid);
				glDeleteTextures(c.textureid);
			}
		}
		glBindVertexArray(Canvas.vao);
		glBindBuffer(GL_ARRAY_BUFFER, Canvas.vbo);
		glDeleteVertexArrays(Canvas.vao);
		glDeleteBuffers(Canvas.vbo);
		glDeleteBuffers(Canvas.ebo);
		Canvas.shader.cleanup();
	}
	
	public void delete() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glDeleteFramebuffers(this.frameBufferid);
		glDeleteTextures(this.textureid);
		Canvas.canvases.remove(this);
	}
	
}
