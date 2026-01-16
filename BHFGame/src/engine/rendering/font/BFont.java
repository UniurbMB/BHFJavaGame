package engine.rendering.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class BFont {
	
	private String filepath;
	private int fontsize;
	
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
		
		try {
			File file = new File("test.png");
			ImageIO.write(img, "png", file);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
}
