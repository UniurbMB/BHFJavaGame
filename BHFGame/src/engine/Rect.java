package engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL33.*;

public class Rect {
	
	public float posX, posY, width, height;
	private int vao, vbo, ebo;
	
	public Rect(float posX, float posY, float width, float height) {
		super();
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		
		float[] arr = {	-0.5f, -0.5f, 0.0f,
						-0.5f, 0.5f, 0.0f,
						0.5f, 0.5f, 0.0f,
						0.5f, -0.5f, 0.0f};
		
		int[] indeces = {	0, 1, 2,
							0, 2, 3};
		
		FloatBuffer data = BufferUtils.createFloatBuffer(arr.length);
		data.put(arr);
		data.flip();
		
		this.vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		this.vbo = glGenBuffers();
		this.ebo = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indeces, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		glEnableVertexAttribArray(0);
		
	}
	
	public void render() {
		glBindVertexArray(vao);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		//glDrawArrays(GL_TRIANGLES, 0, 3);
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
	
	public int getEbo() {
		return ebo;
	}
}
