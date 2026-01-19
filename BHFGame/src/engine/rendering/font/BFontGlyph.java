package engine.rendering.font;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL33.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import engine.Window;
import engine.rendering.rendering_primitives.RenderingPrimitive;

public class BFontGlyph extends RenderingPrimitive{
	
	private int textureid;
	private int vbo, vao, ebo;
	private Vector3f color;
	private Vector2f uvmin, uvmax;
	
	public BFontGlyph(BFont font,
			float posX, float posY, float width, float height,
			float red, float green, float blue,
			float uvminx, float uvminy, float uvmaxx, float uvmaxy) {
		super(posX, posY, width, height);
		this.color = new Vector3f(red, green, blue);
		this.uvmin = new Vector2f(uvminx, uvminy);
		this.uvmax = new Vector2f(uvmaxx, uvmaxy);
		float[] arr = {	-1.0f, -1.0f, 0.0f,		uvmin.x, uvmax.y,
						-1.0f, 1.0f, 0.0f,		uvmin.x, uvmin.y,
						1.0f, 1.0f, 0.0f,		uvmax.x, uvmin.y,
						1.0f, -1.0f, 0.0f,		uvmax.x, uvmax.y};

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
		
		this.textureid = font.getTextureid();

	}

	@Override
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
		glDeleteVertexArrays(this.vao);
		glDeleteBuffers(this.vbo);
		glDeleteBuffers(this.ebo);
	}
	
}
