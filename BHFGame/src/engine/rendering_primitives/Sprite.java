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

public class Sprite extends Rect{
	
	private int textureId;
	
	private static int vao = 0, vbo = 0, ebo = 0;
	private static ShaderProgram shader = null;
	private static HashMap<String, Integer> textures;
	
	public Sprite(String texturePath, float posX, float posY, 
				float width, float height,
				float red, float green, float blue) {
		super(posX, posY, width, height, red, green, blue);
		
		if(Sprite.vbo == 0 && Sprite.vao == 0) {
			
			Sprite.textures = new HashMap<>();
			
			float[] arr = {	-1.0f, -1.0f, 0.0f,		0.0f, 0.0f,
							-1.0f, 1.0f, 0.0f,		0.0f, 1.0f,
							1.0f, 1.0f, 0.0f,		1.0f, 1.0f,
							1.0f, -1.0f, 0.0f,		1.0f, 0.0f};
		
			int[] indeces = {	0, 1, 2,
								0, 2, 3};
		
			Sprite.vao = glGenVertexArrays();
			glBindVertexArray(vao);
		
			Sprite.vbo = glGenBuffers();
			Sprite.ebo = glGenBuffers();
		
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
		
		if(Sprite.shader == null)
			Sprite.shader = new ShaderProgram("src/shaders/spriteVertexShader.glsl", "src/shaders/spriteFragmentShader.glsl");
		
		if(Sprite.textures.containsKey(texturePath) == false) {
			this.textureId = Sprite.loadImage(texturePath);
			Sprite.textures.put(texturePath, this.textureId);
		}else this.textureId = Sprite.textures.get(texturePath);
		
		
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
	
	public void render() {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBindVertexArray(vao);
		glBindTexture(GL_TEXTURE_2D, this.textureId);
		int transformPos = Sprite.shader.getAttribLocation("transform");
		int colorPos = Sprite.shader.getAttribLocation("color");
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		new Matrix4f().translate(new Vector3f(this.pos, 0.0f)).scale(new Vector3f(this.size, 1.0f)).get(fb);
		
		Sprite.shader.start();
		glUniformMatrix4fv(transformPos, false, fb);
		glUniform3f(colorPos, this.color.x, this.color.y, this.color.z);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);

	}
	
	public static void cleanUp() {
		glBindVertexArray(Sprite.vao);
		glBindBuffer(GL_ARRAY_BUFFER, Sprite.vbo);
		glDeleteVertexArrays(Sprite.vao);
		glDeleteBuffers(Sprite.vbo);
		glDeleteBuffers(Sprite.ebo);
		if(!Sprite.textures.isEmpty())for(Integer i: Sprite.textures.values())glDeleteTextures(i);
		Sprite.textures = null;
		Sprite.shader.cleanup();
	}

	public static int getVao() {
		return vao;
	}

	public static int getVbo() {
		return vbo;
	}
	
	public static int getEbo() {
		return ebo;
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
}
