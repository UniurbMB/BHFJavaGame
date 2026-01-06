package engine.rendering_primitives;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import engine.ShaderProgram;

public class ShaderRect implements Renderable{
	public Vector2f pos, size;
	private static int vao = 0, vbo = 0, ebo = 0;
	private ShaderProgram shader = null;
	
	public ShaderRect(float posX, float posY,
			float width, float height,
			ShaderProgram shader) {
		super();
		this.pos = new Vector2f(posX, posY);
		this.size = new Vector2f(width, height);
		this.shader = shader;
		
		if(ShaderRect.vbo == 0 && ShaderRect.vao == 0) {
			float[] arr = {	-1.0f, -1.0f, 0.0f,
							-1.0f, 1.0f, 0.0f,
							1.0f, 1.0f, 0.0f,
							1.0f, -1.0f, 0.0f};
		
			int[] indeces = {	0, 1, 2,
								0, 2, 3};
		
			ShaderRect.vao = glGenVertexArrays();
			glBindVertexArray(vao);
		
			ShaderRect.vbo = glGenBuffers();
			ShaderRect.ebo = glGenBuffers();
		
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER, arr, GL_STATIC_DRAW);
		
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indeces, GL_STATIC_DRAW);
			
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			
			glEnableVertexAttribArray(0);
			
		}
		
	}
	
	public ShaderRect(ShaderProgram shader) {
		this(0.0f, 0.0f, 0.5f, 0.5f, shader);
	}
	
	public ShaderRect(Vector2f pos, Vector2f size, ShaderProgram shader) {
		this(pos.x, pos.y, size.x, size.y, shader);
	}
	
	public ShaderRect(float posX, float posY,
			float width, float height,
			String vertexShaderSource, String fragmentShaderSource) {
		this(posX, posY, width, height, null);
		this.shader = new ShaderProgram(vertexShaderSource, fragmentShaderSource);
	}
	
	public ShaderRect(String vertexShaderSource, String fragmentShaderSource) {
		this(0.0f, 0.0f, 0.5f, 0.5f, vertexShaderSource, fragmentShaderSource);
	}
	
	public ShaderRect(Vector2f pos, Vector2f size, String vertexShaderSource, String fragmentShaderSource) {
		this(pos.x, pos.y, size.x, size.y, vertexShaderSource, fragmentShaderSource);
	}
	
	public void render() {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBindVertexArray(vao);
		int transformPos = this.shader.getAttribLocation("transform");
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		new Matrix4f().translate(new Vector3f(this.pos, 0.0f)).scale(new Vector3f(this.size, 1.0f)).get(fb);
		
		this.shader.start();
		glUniformMatrix4fv(transformPos, false, fb);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);

	}
	
	public int getAttribLocation(String attribName) {
		return this.shader.getAttribLocation(attribName);
	}
}
