package engine.rendering_primitives;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import engine.ShaderProgram;

import static org.lwjgl.opengl.GL33.*;

public class Rect implements Renderable{
	
	public Vector2f pos, size;
	public Vector3f color;
	private static int vao = 0, vbo = 0, ebo = 0;
	private static ShaderProgram shader = null;
	
	public Rect(float posX, float posY, 
				float width, float height,
				float red, float green, float blue) {
		super();
		this.pos = new Vector2f(posX, posY);
		this.size = new Vector2f(width, height);
		this.color = new Vector3f(red, green, blue);
		
		if(Rect.vbo == 0 && Rect.vao == 0) {
			float[] arr = {	-1.0f, -1.0f, 0.0f,
							-1.0f, 1.0f, 0.0f,
							1.0f, 1.0f, 0.0f,
							1.0f, -1.0f, 0.0f};
		
			int[] indeces = {	0, 1, 2,
								0, 2, 3};
		
			Rect.vao = glGenVertexArrays();
			glBindVertexArray(vao);
		
			Rect.vbo = glGenBuffers();
			Rect.ebo = glGenBuffers();
		
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER, arr, GL_STATIC_DRAW);
		
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indeces, GL_STATIC_DRAW);
			
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			
			glEnableVertexAttribArray(0);
			
		}
		
		if(Rect.shader == null) {
			Rect.shader = new ShaderProgram("src/shaders/vertexShader.glsl", "src/shaders/fragmentShader.glsl");
		}
		
	}
	
	public Rect() {
		this(0.0f, 0.0f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f);
	}
	
	public Rect(float posX, float posY, float width, float height) {
		this(posX, posY, width, height, 1.0f, 1.0f, 1.0f);
	}
	
	public Rect(Vector2f pos, Vector2f size, Vector3f color) {
		this(pos.x, pos.y, size.x, size.y, color.x, color.y, color.z);
	}
	
	public Rect(Vector2f pos, Vector2f size) {
		this(pos.x, pos.y, size.x, size.y, 1.0f, 1.0f, 1.0f);
	}
	
	public void render() {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBindVertexArray(vao);
		int transformPos = Rect.shader.getAttribLocation("transform");
		int colorPos = Rect.shader.getAttribLocation("color");
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		new Matrix4f().translate(new Vector3f(this.pos, 0.0f)).scale(new Vector3f(this.size, 1.0f)).get(fb);
		
		Rect.shader.start();
		glUniformMatrix4fv(transformPos, false, fb);
		glUniform3f(colorPos, this.color.x, this.color.y, this.color.z);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);

	}
	
	public static void cleanUp() {
		glBindVertexArray(Rect.vao);
		glBindBuffer(GL_ARRAY_BUFFER, Rect.vbo);
		glDeleteVertexArrays(Rect.vao);
		glDeleteBuffers(Rect.vbo);
		glDeleteBuffers(Rect.ebo);
		if(Rect.shader != null)Rect.shader.cleanup();
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
}
