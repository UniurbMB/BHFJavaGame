package engine.rendering_primitives;

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

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import engine.ShaderProgram;
import engine.Window;

public class Ellipse extends Rect{

	private final static ShaderProgram shader = new ShaderProgram("src/shaders/spriteVertexShader.glsl", "src/shaders/ellipseFragmentShader.glsl");
	
	public Ellipse(float posX, float posY,
				float width, float height,
				float r, float g, float b) {
		super(posX, posY, width, height, r, g, b);
	}
	
	public Ellipse(float posX, float posY,
				float width, float height) {
		super(posX, posY, width, height);
	}
	
	public Ellipse() {
		super();
	}

	public Ellipse(float posX, float posY) {
		super(posX, posY);
	}

	public Ellipse(Vector2f pos, Vector2f size, Vector3f col) {
		super(pos, size, col);
	}

	public Ellipse(Vector2f pos, Vector2f size) {
		super(pos, size);
	}

	public Ellipse(Vector2f pos) {
		super(pos);
	}

	public void render() {
		Matrix4f projection = Window.projection;
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

}
