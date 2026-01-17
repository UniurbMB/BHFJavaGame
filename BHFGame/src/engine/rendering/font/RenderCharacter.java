package engine.rendering.font;

import engine.Window;
import engine.rendering.rendering_primitives.Sprite;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL33.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class RenderCharacter extends Sprite{
	
	private int vao, vbo, ebo;
	
	public RenderCharacter(BFont font, int codepoint,
			float posX, float posY,
			float width, float height,
			float r, float g, float b) {
		
		this.pos = new Vector2f(posX, posY);
		this.size = new Vector2f(width, height);
		this.color = new Vector3f(r, g, b);
		this.textureid = font.getTextureid();
		Vector2f[] coords = font.getCharacter(codepoint).textureCoordinates;
		System.out.println(coords[0].x + " " + (1.0f - coords[0].y));
		System.out.println(coords[1].x + " " + (1.0f - coords[1].y));
		System.out.println(coords[2].x + " " + (1.0f - coords[2].y));
		System.out.println(coords[3].x + " " + (1.0f - coords[3].y));
		System.out.println(coords[0].x + " " + (coords[0].y));
		System.out.println(coords[1].x + " " + (coords[1].y));
		System.out.println(coords[2].x + " " + (coords[2].y));
		System.out.println(coords[3].x + " " + (coords[3].y));
		
		/*float[] arr = {	-1.0f, -1.0f, 0.0f,		coords[0].x, coords[0].y,
						-1.0f, 1.0f, 0.0f,		coords[0].x, coords[3].y,
						1.0f, 1.0f, 0.0f,		coords[3].x, coords[3].y,
						1.0f, -1.0f, 0.0f,		coords[3].x, coords[0].y};*/
		
		float[] arr = {	-1.0f, -1.0f, 0.0f,		coords[0].x, (1.0f - coords[1].y),
						-1.0f, 1.0f, 0.0f,		coords[0].x, (1.0f - coords[0].y),
						1.0f, 1.0f, 0.0f,		coords[3].x, (1.0f - coords[0].y),
						1.0f, -1.0f, 0.0f,		coords[3].x, (1.0f - coords[1].y)};

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
	
	public void render() {
		Matrix4f projection = new Matrix4f(Window.projection);
		shader.start();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBindVertexArray(vao);
		glBindTexture(GL_TEXTURE_2D, this.textureid);
		int transformPos = shader.getAttribLocation("transform");
		int colorPos = shader.getAttribLocation("color");
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		
		new Matrix4f().mul(projection).translate(new Vector3f(this.pos, 0.0f)).scale(new Vector3f(this.size, 1.0f)).get(fb);
		
		glUniformMatrix4fv(transformPos, false, fb);
		glUniform3f(colorPos, this.color.x, this.color.y, this.color.z);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}
	
	public void destroy() {
		glDeleteBuffers(vbo);
		glDeleteVertexArrays(vao);
		glDeleteBuffers(ebo);
	}
}
