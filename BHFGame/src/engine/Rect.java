package engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL33.*;

public class Rect {
	
	public float posX, posY, width, height;
	private int vao, vbo;
	
	public Rect(float posX, float posY, float width, float height) {
		super();
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		
		float[] arr = {	-0.5f, -0.5f, 0.0f,
				-0.5f, 0.5f, 0.0f,
				0.5f, 0.5f, 0.0f};
		
		FloatBuffer data = BufferUtils.createFloatBuffer(arr.length);
		data.put(arr);
		data.flip();
		
		this.vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		this.vbo = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		glEnableVertexAttribArray(0);
		
	}
	
	public void render() {
		glBindVertexArray(vao);
		glDrawArrays(GL_TRIANGLES, 0, 3);
	}
	
	public void destroy() {
		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glDeleteVertexArrays(vao);
		glDeleteBuffers(vbo);
	}

	public int getVao() {
		return vao;
	}

	public int getVbo() {
		return vbo;
	}
	
}
