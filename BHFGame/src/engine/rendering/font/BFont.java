package engine.rendering.font;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.opengl.GL33.*;

public class BFont {
	private int textureid;
	private int width, height;
	private float fontSize;
	private STBTTFontinfo info;
	private STBTTBakedChar.Buffer cdata;
	private ByteBuffer ttf;
	
	private final int ascent;
    private final int descent;
    private final int lineGap;
	
	public BFont(String filePath, float fontSize) {
		this.textureid = glGenTextures();
		this.fontSize = fontSize;
		ByteBuffer bitmap = BufferUtils.createByteBuffer(1024 * 1024);
		try {
			this.ttf = ioResourceToByteBuffer(filePath, 1024 * 1024);
		}catch(IOException e) {
			e.printStackTrace();
		}
		this.info = STBTTFontinfo.create();
		stbtt_InitFont(info, ttf);
		
		cdata = STBTTBakedChar.malloc(96);
		try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

            ascent = pAscent.get(0);
            descent = pDescent.get(0);
            lineGap = pLineGap.get(0);
        }
		stbtt_BakeFontBitmap(ttf, fontSize, bitmap, 1024, 1024, 32, cdata);
		glBindTexture(GL_TEXTURE_2D, textureid);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, 1024, 1024, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	}
	
	public BFontGlyph getGlyph(char c) {
		BFontGlyph glyph = null;
		try (MemoryStack stack = stackPush()){
			IntBuffer codepoint = stack.mallocInt(1);
			STBTTAlignedQuad quad = STBTTAlignedQuad.malloc(stack);
			//int codepoint = Character.codePointAt(t, 0);
		//	stbtt_GetBakedQuad(cdata, 1024, 1024, codepoint, x, y, quad, true);
		}
		
		FloatBuffer x = FloatBuffer.allocate(1);
		FloatBuffer y = FloatBuffer.allocate(1);
		char[] t = new char[1];
		t[0] = c;
		
		//System.out.println(codepoint);
		//stbtt_GetBakedQuad(cdata, 1024, 1024, c, x, y, quad, true);
		//System.out.println(quad.x0() + " " + quad.x1() + " " + quad.y0() + " " + quad.y1());
		return glyph;
	}
	
	public int getTextureid() {
		return this.textureid;
	}
	
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
