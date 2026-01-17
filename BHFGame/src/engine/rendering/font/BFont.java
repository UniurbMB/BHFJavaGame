package engine.rendering.font;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class BFont {
	
	private String filepath;
	private int fontsize;
	
	private int textureid;
	
	private int width, height, lineheight;
	private Map<Integer, CharInfo> characterMap;
	
	public BFont(String filepath, int fontsize) {
		this.filepath = filepath;
		this.fontsize = fontsize;
		this.characterMap = new HashMap<>();
		generateBitmap();
	}
	
	public void generateBitmap() {
		Font font = new Font(filepath, Font.PLAIN, fontsize);
		
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setFont(font);
		FontMetrics fontMetrics = g2d.getFontMetrics();
		
		int estimatedWidth = (int)Math.sqrt(font.getNumGlyphs()) * font.getSize() + 1;
		width = 0;
		height = fontMetrics.getHeight();
		lineheight = fontMetrics.getHeight();
		int x = 0;
		int y = (int)(fontMetrics.getHeight() * 1.4f);
		
		for(int i = 0; i < font.getNumGlyphs(); i++) {
			if(font.canDisplay(i)) {
				CharInfo charinfo = new CharInfo(x, y, fontMetrics.charWidth(i), fontMetrics.getHeight());
				this.characterMap.put(i, charinfo);
				width = Math.max(x + fontMetrics.charWidth(i), width);
				x += charinfo.width;
				if(x > estimatedWidth) {
					x = 0;
					y += fontMetrics.getHeight() * 1.4f;
					height += fontMetrics.getHeight() * 1.4f;
				}
			}
		}
		
		height += fontMetrics.getHeight() * 1.4f;
		
		g2d.dispose();
		
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(font);
		g2d.setColor(Color.WHITE);
		
		for(int i = 0; i < font.getNumGlyphs(); i++) {
			if(font.canDisplay(i)) {
				CharInfo cache = this.characterMap.get(i);
				this.characterMap.get(i).calculateTextureCoordinates(width, height);
				g2d.drawString("" + (char)i, cache.sourceX, cache.sourceY);
			}
		}
		
		g2d.dispose();
		
		uploadTexture(img);
	}
	
	public CharInfo getCharacter(int codepoint) {
		return this.characterMap.getOrDefault(codepoint, new CharInfo(0, 0, 0, 0));
	}
	
	private void uploadTexture(BufferedImage img) {
		int[] pixels = new int[img.getHeight() * img.getWidth()];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);
		for(int y = 0; y < img.getHeight(); y++) {
			for(int x = 0; x < img.getWidth(); x++) {
				int totalSize = img.getHeight() * img.getWidth();
				int pixel = pixels[(y * img.getWidth() + x)];
				byte alpha = (byte)((pixel >> 24) & 0xff);
				byte bx = (byte)(((float)x / (float)img.getWidth()) * (float)Byte.MAX_VALUE);
				buffer.put(alpha);
				buffer.put(alpha);
				buffer.put(alpha);
				buffer.put(alpha);
				//System.out.println("x: " + x + ", y: " + y + ", test: " + bx);
			}
		}
		System.out.println("width: " + img.getWidth() + ", height: " + img.getHeight());
		buffer.flip();
		
		this.textureid = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, this.textureid);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		
		free(buffer);
	}
	
	public int getTextureid() {
		return this.textureid;
	}
	
}
