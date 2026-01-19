package engine.rendering.font;

import static org.lwjgl.opengl.GL33.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import engine.ShaderProgram;
import engine.Window;
import engine.rendering.rendering_primitives.Renderable;

public class BText implements Renderable{

	private static final int ELEMENTS = 6;
	private final BFont font;
	private String text;
	private static final ShaderProgram shader = new ShaderProgram("src/shaders/spriteVertexShader.glsl",
																"src/shaders/fontFragmentShader.glsl");
	private int vbo, vao, ebo;
	private int textureid;
	
	public Vector2f pos;
	public Vector3f color;
	private Vector2f charSize;
	private float spacing;
	private boolean alignLeft;
	
	public BText(String text, BFont font, boolean alignLeft,
			float posX, float posY,
			float charWidth, float charHeight, float spacing,
			float red, float green, float blue) {
		this.text = text;
		this.font = font;
		this.textureid = this.font.getTextureid();
		this.charSize = new Vector2f(charWidth, charHeight);
		this.pos = new Vector2f(posX, posY);
		this.color = new Vector3f(red, green, blue);
		this.spacing = spacing;
		this.alignLeft = alignLeft;

		float[] outArr = this.generateBufferData();
		int[] outIndeces = this.generateElementBufferData();

		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		vbo = glGenBuffers();
		ebo = glGenBuffers();

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, outArr, GL_DYNAMIC_DRAW);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, outIndeces, GL_DYNAMIC_DRAW);

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
		glBindTexture(GL_TEXTURE_2D, this.textureid);
		int transformPos = shader.getAttribLocation("transform");
		int colorPos = shader.getAttribLocation("color");
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		
		new Matrix4f().mul(projection).translate(new Vector3f(this.pos, 0.0f)).scale(new Vector3f(new Vector2f(1.0f, 1.0f), 1.0f)).get(fb);
		
		glUniformMatrix4fv(transformPos, false, fb);
		glUniform3f(colorPos, this.color.x, this.color.y, this.color.z);
		glDrawElements(GL_TRIANGLES, ELEMENTS * this.text.length(), GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}
	
	public void destroy() {
		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glDeleteVertexArrays(vao);
		glDeleteBuffers(vbo);
		glDeleteBuffers(ebo);
	}
	
	public void setText(String text) {
		this.text = text;
		
		float[] outArr = this.generateBufferData();
		int[] outIndeces = this.generateElementBufferData();
		
		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, outArr, GL_DYNAMIC_DRAW);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, outIndeces, GL_DYNAMIC_DRAW);

		int stride = (3 + 2) * Float.BYTES;

		glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3*Float.BYTES);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
	}
	
	public String getText() {
		return new String(text);
	}
	
	public BFont getFont() {
		return this.font;
	}
	
	private float[] generateBufferData() {
		float[] baseArr = {	-1.0f, -1.0f, 0.0f,		0.0f, 0.0f,
							-1.0f, 1.0f, 0.0f,		0.0f, 1.0f,
							1.0f, 1.0f, 0.0f,		1.0f, 1.0f,
							1.0f, -1.0f, 0.0f,		1.0f, 0.0f};


		int arrLen = baseArr.length * text.length();
		int elementStep = 5;
		float nextOffset = (this.charSize.x + this.spacing) * ((alignLeft)?1.0f:-1.0f);

		float[] outArr = new float[arrLen];

		for(int in = 0; in < text.length(); in++) {

			int i = (alignLeft)?(in):(text.length() - in - 1);
			int p = i * baseArr.length;
			
			outArr[p] = 
			outArr[p + elementStep] = nextOffset * (float)i;
			outArr[p + elementStep * 2] =
			outArr[p + elementStep * 3] = nextOffset * (float)i + charSize.x;

			outArr[p + 1] =
			outArr[p + 1 + elementStep * 3] = 0.0f;
			outArr[p + 1 + elementStep] =
			outArr[p + 1 + elementStep * 2] = charSize.y;

			for(int j = 0; j < 4; j++) outArr[p + 2 + j * elementStep] = 0.0f;
			//System.out.println(i);
				Vector2f[] uvs = font.getGlyphPos(text.charAt(in));

				outArr[p + 3] =
				outArr[p + 3 + elementStep] = uvs[0].x;
				outArr[p + 3 + elementStep * 2] =
				outArr[p + 3 + elementStep * 3] = uvs[1].x;

				outArr[p + 4] =
				outArr[p + 4 + elementStep * 3] = uvs[1].y;
				outArr[p + 4 + elementStep] = 
				outArr[p + 4 + elementStep * 2] = uvs[0].y;

			}
		
		return outArr;
	}
	
	private int[] generateElementBufferData() {
		int[] baseIndeces = {	0, 1, 2,
								0, 2, 3};

		int[] outIndeces = new int[baseIndeces.length * text.length()];

		for(int i = 0; i < text.length(); i++) {
			for(int j = 0; j < baseIndeces.length; j++) {
				outIndeces[i * baseIndeces.length + j] = baseIndeces[j] + i * 4;
			}
		}
		return outIndeces;
	}

}
