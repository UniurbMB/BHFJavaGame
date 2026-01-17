package engine.rendering.font;

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
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import engine.Window;
import engine.rendering.rendering_primitives.Sprite;

public class FontMapSprite extends Sprite{
	
	private int vao, vbo, ebo;
	//protected int textureid;
	private BFont font;
	
	public FontMapSprite(String fontPath, int fontHeight,
				float posX, float posY,
				float width, float height,
				float red, float green, float blue) {
		this(new BFont("src/assets/fonts/yuji-syuku-japanese-400-normal.ttf", fontHeight),
				posX, posY,
				width, height,
				red, green, blue);
		
	}
	
	public FontMapSprite(BFont font,
						float posX, float posY,
						float width, float height,
						float red, float green, float blue) {
		super(null, posX, posY, width, height, red, green, blue);
		this.font = font;
		this.textureid = font.getTextureid();
		float[] arr = {	-1.0f, -1.0f, 0.0f,		0.0f, 1.0f,
						-1.0f, 1.0f, 0.0f,		0.0f, 0.0f,
						1.0f, 1.0f, 0.0f,		1.0f, 0.0f,
						1.0f, -1.0f, 0.0f,		1.0f, 1.0f};

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
		glBindVertexArray(this.vao);
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
	
	@Override
	public void destroy() {
		glDeleteBuffers(vbo);
		glDeleteVertexArrays(vao);
		glDeleteBuffers(ebo);
	}

	public BFont getFont() {
		return this.font;
	}
	
}
