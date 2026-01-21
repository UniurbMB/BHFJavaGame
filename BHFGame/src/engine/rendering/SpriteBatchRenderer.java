package engine.rendering;

import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL33.*;

import engine.ShaderProgram;
import engine.Window;
import engine.rendering.rendering_primitives.Renderable;
import engine.rendering.rendering_primitives.Sprite;

public class SpriteBatchRenderer implements Renderable {

	private class BatchThread extends Thread{
		private final int id, threadCount;
		private float[] buffer;
		private int[] indeces;
		private List<Sprite> sprites;
		
		public BatchThread(int id, int threadCount, float[] buffer, int[] indeces, List<Sprite> sprites) {
			this.id = id;
			this.threadCount = threadCount;
			this.buffer = buffer;
			this.indeces = indeces;
			this.sprites = sprites;
		}

		@Override
		public void run() {
			float baseUvs[] = {	0, 0, 0, 1,
								1, 1, 1, 0};
			
			int baseInd[] = {	0, 1, 2,
								0, 2, 3};
			for(int i = this.id; i < this.sprites.size(); i += this.threadCount)
				for(int j = 0; j < INDECES; j++) this.indeces[i * baseInd.length + j] = baseInd[j] + i * 4;
			
			for(int i = this.id; i < this.sprites.size(); i += this.threadCount) {
				int bufPos = i * BUFFER_ELEMENTS;
				Sprite current = this.sprites.get(i);
				
				
				
				this.buffer[bufPos] = current.pos.x - current.size.x;
				this.buffer[bufPos + 5] = current.pos.x - current.size.x;
				this.buffer[bufPos + 10] = current.pos.x + current.size.x;
				this.buffer[bufPos + 15] = current.pos.x + current.size.x;
				
				this.buffer[bufPos + 1] = current.pos.y - current.size.y;
				this.buffer[bufPos + 6] = current.pos.y + current.size.y;
				this.buffer[bufPos + 11] = current.pos.y + current.size.y;
				this.buffer[bufPos + 16] = current.pos.y - current.size.y;
				
				for(int j = 0; j < 4; j++)this.buffer[bufPos + 2 + j * 5] = 0;
				
				for(int j = 0; j < baseUvs.length; j++) {
					
					int stride = 3 + (j / 2) * 5;
					this.buffer[bufPos + stride + (j%2)] = baseUvs[j];
					//System.out.println(baseUvs[j] + " " + (j / 2));
				}
				
			}
			
		}
		
	};
	
	
	protected static final int BUFFER_ELEMENTS = 20;
	protected static final int INDECES = 6;
	protected static final int RENDER_ELEMENTS_PER_SPRITE = 6;
	private Sprite sprite;
	private int spriteCount;
	private int vbo, vao, ebo;
	
	protected final static ShaderProgram shader = new ShaderProgram("src/shaders/spriteVertexShader.glsl",
			"src/shaders/spriteFragmentShader.glsl");
	
	public SpriteBatchRenderer(Sprite s) {
		this.sprite = s;
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		vbo = glGenBuffers();
		ebo = glGenBuffers();
		
		int stride = (3 + 2) * Float.BYTES;
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3*Float.BYTES);
		
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
	}
	
	public SpriteBatchRenderer() {
		this(null);
	}
	
	public void setSprite(Sprite s) {
		this.sprite = s;
	}
	
	public void constructBuffer(List<Sprite> sprites, int threadCount) {
		if(threadCount <= 0)throw new IllegalArgumentException("Thread count must be higher than 0!");
		float arr[] = new float[sprites.size() * BUFFER_ELEMENTS];
		int indeces[] = new int[sprites.size() * INDECES];
		this.spriteCount = sprites.size();
		BatchThread threads[] = new BatchThread[threadCount];
		for(int i = 0; i < threadCount; i++) threads[i] = new BatchThread(i, threadCount, arr, indeces, sprites);
		for(BatchThread v: threads)v.start();
		
		for(BatchThread v: threads)try {
			v.join();
		}catch(InterruptedException e) {e.printStackTrace();}
		
		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, arr, GL_DYNAMIC_DRAW);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indeces, GL_DYNAMIC_DRAW);

		int stride = (3 + 2) * Float.BYTES;

		glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3*Float.BYTES);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
	}
	
	@Override
	public void render() {
		Matrix4f projection = new Matrix4f(Window.projection);
		shader.start();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBindVertexArray(vao);
		glBindTexture(GL_TEXTURE_2D, this.sprite.getTextureid());
		int transformPos = shader.getAttribLocation("transform");
		int colorPos = shader.getAttribLocation("color");
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		
		new Matrix4f().mul(projection).translate(new Vector3f(0, 0, 0)).scale(new Vector3f(new Vector2f(1.0f, 1.0f), 1.0f)).get(fb);
		
		glUniformMatrix4fv(transformPos, false, fb);
		glUniform3f(colorPos, this.sprite.color.x, this.sprite.color.y, this.sprite.color.z);
		glDrawElements(GL_TRIANGLES, RENDER_ELEMENTS_PER_SPRITE * this.spriteCount, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);

	}
	
	public void destroy() {
		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glDeleteVertexArrays(vao);
		glDeleteBuffers(vbo);
		glDeleteBuffers(ebo);
	}

}
