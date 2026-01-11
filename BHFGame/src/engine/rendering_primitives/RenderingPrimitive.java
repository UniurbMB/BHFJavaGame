package engine.rendering_primitives;

import static org.lwjgl.opengl.GL33.*;

import org.joml.*;

import engine.ShaderProgram;

public abstract class RenderingPrimitive implements Renderable{
	public Vector2f pos, size;
	
	protected static int vao = 0, vbo = 0, ebo = 0;
	protected final static ShaderProgram shader = new ShaderProgram("src/shaders/spriteVertexShader.glsl", "src/shaders/spriteFragmentShader.glsl");
	
	public RenderingPrimitive(float posX, float posY, 
			float width, float height) {
	
	this.pos = new Vector2f(posX, posY);
	this.size = new Vector2f(width, height);
	
	if(vbo == 0 && vao == 0) {
		
			float[] arr = {	-1.0f, -1.0f, 0.0f,		0.0f, 0.0f,
							-1.0f, 1.0f, 0.0f,		0.0f, 1.0f,
							1.0f, 1.0f, 0.0f,		1.0f, 1.0f,
							1.0f, -1.0f, 0.0f,		1.0f, 0.0f};
	
			int[] indeces = {	0, 1, 2,
								0, 2, 3};
	
			vao = glGenVertexArrays();
			glBindVertexArray(vao);
	
			vbo = glGenBuffers();
			ebo = glGenBuffers();
	
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
	
	}
	
	public abstract void render();
	
	public static void cleanUpPrimitive() {
		shader.cleanup();
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
