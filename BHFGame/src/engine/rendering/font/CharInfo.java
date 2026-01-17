package engine.rendering.font;

import org.joml.Vector2f;

public class CharInfo {
	
	public int sourceX, sourceY;
	public int width, height;
	
	public Vector2f textureCoordinates[] = new Vector2f[4];
	
	public CharInfo(int sourceX, int sourceY, int width, int height) {
		super();
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.width = width;
		this.height = height;
	}
	
	public void calculateTextureCoordinates(int fontWidth, int fontHeight) {
		float x0 = (float)sourceX / (float)fontWidth;
		float x1 = (float)(sourceX + width) / (float)fontWidth;
		float y0 = 1.0f - (float)(sourceY - height) / (float)fontHeight;
		float y1 = 1.0f - (float)(sourceY) / (float)fontHeight;
		
		this.textureCoordinates[0] = new Vector2f(x0, y0);
		this.textureCoordinates[1] = new Vector2f(x0, y1);
		this.textureCoordinates[2] = new Vector2f(x1, y0);
		this.textureCoordinates[3] = new Vector2f(x1, y1);
	}
	
}
