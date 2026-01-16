package engine.rendering.font;

import org.joml.Vector2f;

public class CharInfo {
	
	public int sourceX, sourceY;
	public int width, height;
	
	public Vector2f coords[] = new Vector2f[4];
	
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
		float y0 = (float)sourceY / (float)fontHeight;
		float y1 = (float)(sourceY + height) / (float)fontHeight;
		
		this.coords[0] = new Vector2f(x0, y0);
		this.coords[1] = new Vector2f(x0, y1);
		this.coords[2] = new Vector2f(x1, y0);
		this.coords[3] = new Vector2f(x1, y1);
	}
	
}
