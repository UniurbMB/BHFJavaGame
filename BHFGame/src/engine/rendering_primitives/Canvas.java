package engine.rendering_primitives;

import static org.lwjgl.opengl.GL33.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import engine.ShaderProgram;

public class Canvas extends Sprite{
	
	public Vector3f color;
	//private int textureid;
	private int frameBufferid;
	
	private int pixelWidth, pixelHeight;
	private int lastWidth, lastHeight;
	
	private static ArrayList<Canvas> canvases = new ArrayList<>();
	
	public Canvas(float posX, float posY,
			float width, float height,
			int pixelWidth, int pixelHeight,
			float red, float green, float blue) {
		super(posX, posY, width, height);
		this.pixelWidth = pixelWidth;
		this.pixelHeight = pixelHeight;
		this.color = new Vector3f(red, green, blue);
		Canvas.canvases.add(this);
		
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
