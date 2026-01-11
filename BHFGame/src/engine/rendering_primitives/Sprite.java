package engine.rendering_primitives;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import engine.ShaderProgram;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;

public class Sprite extends RenderingPrimitive{
	
	public Vector3f color;
	
	protected int textureid;

	private static HashMap<String, Integer> textures = new HashMap<>();
	private final static int defaultTexture = Sprite.loadImage();
	
	public Sprite(String texturePath, float posX, float posY, 
				float width, float height,
				float red, float green, float blue) {
		
		super(posX, posY, width, height);
		this.color = new Vector3f(red, green, blue);
		
		if(texturePath != null) {
			if(!Sprite.textures.containsKey(texturePath)) {
				this.textureid = Sprite.loadImage(texturePath);
				Sprite.textures.put(texturePath, this.textureid);
			}else this.textureid = Sprite.textures.get(texturePath);
		}else this.textureid = Sprite.defaultTexture;
		
	}
	
	public Sprite(String texturePath, float posX, float posY, 
			float width, float height) {
		this(texturePath, posX, posY, width, height, 1.0f, 1.0f, 1.0f);
	}
	
	public Sprite(String texturePath, float posX, float posY) {
		this(texturePath, posX, posY, 0.5f, 0.5f);
	}
	
	public Sprite(String texturePath) {
		this(texturePath, 0.0f, 0.0f);
	}
	
	public Sprite(String texturePath, Vector2f pos, Vector2f size, Vector3f col) {
		this(texturePath, pos.x, pos.y, size.x, size.y, col.x, col.y, col.z);
	}
	
	public Sprite(String texturePath, Vector2f pos, Vector2f size) {
		this(texturePath, pos, size, new Vector3f(1.0f, 1.0f, 1.0f));
	}
	
	public Sprite(String texturePath, Vector2f pos) {
		this(texturePath, pos, new Vector2f(0.5f, 0.5f));
	}
	
	public Sprite(float posX, float posY, 
			float width, float height,
			float red, float green, float blue) {
		this(null, posX, posY, width, height, red, green, blue);
	}
	
	public Sprite(float posX, float posY, 
			float width, float height) {
		this(null, posX, posY, width, height, 1.0f, 1.0f, 1.0f);
	}
	
	public Sprite(float posX, float posY) {
		this(null, posX, posY, 0.5f, 0.5f);
	}
	
	public Sprite() {
		this(null, 0.0f, 0.0f);
	}
	
	public Sprite(Vector2f pos, Vector2f size, Vector3f col) {
		this(null, pos.x, pos.y, size.x, size.y, col.x, col.y, col.z);
	}
	
	public Sprite(Vector2f pos, Vector2f size) {
		this(null, pos, size, new Vector3f(1.0f, 1.0f, 1.0f));
	}
	
	public Sprite(Vector2f pos) {
		this(null, pos, new Vector2f(0.5f, 0.5f));
	}
	
	public void render() {
		shader.start();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBindVertexArray(vao);
		glBindTexture(GL_TEXTURE_2D, this.textureid);
		int transformPos = shader.getAttribLocation("transform");
		int colorPos = shader.getAttribLocation("color");
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		new Matrix4f().translate(new Vector3f(this.pos, 0.0f)).scale(new Vector3f(this.size, 1.0f)).get(fb);
		
		glUniformMatrix4fv(transformPos, false, fb);
		glUniform3f(colorPos, this.color.x, this.color.y, this.color.z);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}
	
	public static void cleanUp() {
		glBindVertexArray(Sprite.vao);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glDeleteVertexArrays(vao);
		glDeleteBuffers(vbo);
		glDeleteBuffers(ebo);
		if(!Sprite.textures.isEmpty())for(Integer i: Sprite.textures.values())glDeleteTextures(i);
		Sprite.textures = null;
		Sprite.shader.cleanup();
	}
	
	private static int loadImage(String texturePath) {
		int result = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, result);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		IntBuffer width, height, channels;
		width = BufferUtils.createIntBuffer(1);
		height = BufferUtils.createIntBuffer(1);
		channels = BufferUtils.createIntBuffer(1);
		stbi_set_flip_vertically_on_load(true);
		ByteBuffer image = stbi_load(texturePath, width, height, channels, 4);
		
		if(image != null)glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
		else throw new RuntimeException("Failed to load sprite at path " + texturePath);
		
		stbi_image_free(image);
		return result;
	}
	
	private static int loadImage() {
		int result = glGenTextures();
		glActiveTexture(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, result);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		ByteBuffer image = ByteBuffer.allocateDirect(4);
		image.put((byte)-1).put((byte)-1).put((byte)-1).put((byte)-1);
		image.rewind();

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
		
		return result;
	}
}
