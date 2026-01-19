package engine.rendering.font;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import engine.rendering.rendering_primitives.Sprite;

import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.opengl.GL33.*;

public class BFont extends Sprite{
	
	private float fontSize;
	private STBTTFontinfo info;
	private STBTTBakedChar.Buffer cdata;
	private ByteBuffer ttf;
	private static HashMap<String, BFont> loadedFonts = new HashMap<>();
	
    public BFont(String filePath, float fontSize,
    			float posX, float posY,
    			float width, float height,
    			float red, float green, float blue) {
    	this(filePath, fontSize);
    	this.pos.x = posX;
    	this.pos.y = posY;
    	this.size.x = width;
    	this.size.y = -height;
    	this.color.x = red;
    	this.color.y = green;
    	this.color.z = blue;
    	
    }
    
	public BFont(String filePath, float fontSize) {
		if(!BFont.loadedFonts.containsKey(filePath)) {
		this.textureid = glGenTextures();
		this.fontSize = fontSize;
		this.size.y *= -1.0f;
		ByteBuffer bitmap = BufferUtils.createByteBuffer(1024 * 1024);
		try {
			this.ttf = ioResourceToByteBuffer(filePath, 1024 * 1024);
		}catch(IOException e) {
			e.printStackTrace();
		}
		this.info = STBTTFontinfo.create();
		stbtt_InitFont(info, ttf);
		
		cdata = STBTTBakedChar.malloc(96);
		stbtt_BakeFontBitmap(ttf, fontSize, bitmap, 1024, 1024, 32, cdata);
		glBindTexture(GL_TEXTURE_2D, textureid);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, 1024, 1024, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		}else {
			BFont f = BFont.loadedFonts.get(filePath);
			this.textureid = f.textureid;
			this.fontSize = f.fontSize;
			this.size.y = f.size.y;
			this.ttf = f.ttf;
			this.info = f.info;
		}
	}
	
	public BFontGlyph getGlyph(char c) {
		BFontGlyph glyph = null;
		try (MemoryStack stack = stackPush()){
			STBTTAlignedQuad quad = STBTTAlignedQuad.malloc(stack);
			FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats(0.0f);
            char[] t = new char[1];
            t[0] = c;
			int codepoint = Character.codePointAt(t, 0);
			stbtt_GetBakedQuad(cdata, 1024, 1024, codepoint - 32, x, y, quad, true);
			glyph = new BFontGlyph(this, 0.0f, 0.0f, 
					0.5f, 0.5f, 
					1.0f, 1.0f, 1.0f, 
					quad.s0(), quad.t0(), quad.s1(), quad.t1());
		}
		
		return glyph;
	}
	
	public Vector2f[] getGlyphPos(char c) {
		Vector2f[] result = new Vector2f[2];
		result[0] = new Vector2f();
		result[1] = new Vector2f();
		try (MemoryStack stack = stackPush()){
			STBTTAlignedQuad quad = STBTTAlignedQuad.malloc(stack);
			FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats(0.0f);
            char[] t = new char[1];
            t[0] = c;
			int codepoint = Character.codePointAt(t, 0);
			stbtt_GetBakedQuad(cdata, 1024, 1024, codepoint - 32, x, y, quad, true);
			result[0] = new Vector2f(quad.s0(), quad.t0());
			result[1] = new Vector2f(quad.s1(), quad.t1());
		}
		return result;
	}
	
	public float getFontSize() {
		return this.fontSize;
	}
	
	public int getTextureid() {
		return this.textureid;
	}
	
	@SuppressWarnings("deprecation")
	private static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = resource.startsWith("http") ? null : Paths.get(resource);
        if (path != null && Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) {
                    ;
                }
            }
        } else {
            try (
                InputStream source = resource.startsWith("http")
                    ? new URL(resource).openStream()
                    : BFont.class.getClassLoader().getResourceAsStream(resource);
                ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
        }

        buffer.flip();
        return memSlice(buffer);
    }
	
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

}
