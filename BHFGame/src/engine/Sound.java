package engine;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.libc.LibCStdlib.*;

import java.nio.*;
import java.util.HashMap;

public class Sound {
	private int bufferid;
	private int sourceid;
	private String file;
	private boolean isPlaying = false;
	private static HashMap<String, Sound> sounds = new HashMap<>();
	
	public static final float DEFAULT_GAIN = 1.0f;
	
	public static Sound newSound(String file, boolean loops) {
		if(!Sound.sounds.containsKey(file)) {
			Sound s = new Sound(file, loops);
			Sound.sounds.put(file, s);
		}
		return Sound.sounds.get(file);
	}
	
	public static Sound newSound(String file) {
		return Sound.newSound(file, false);
	}
	
	private Sound(String file, boolean loops) {
		
		this.file = file;
		
		stackPush();
		IntBuffer channelsBuffer = stackMallocInt(1);
		stackPush();
		IntBuffer samplerateBuffer = stackMallocInt(1);
		
		ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(file, channelsBuffer, samplerateBuffer);
		if(rawAudioBuffer == null) {
			stackPop();
			stackPop();
			System.err.println("Could not load audio from: \"" + this.file +"\"!");
			return;
		}
		
		int channels = channelsBuffer.get();
		int sampleRate = samplerateBuffer.get();
		
		stackPop();
		stackPop();
		
		int format = -1;
		if(channels == 1)format = AL_FORMAT_MONO16;
		else format = AL_FORMAT_STEREO16;
		
		this.bufferid = alGenBuffers();
		
		alBufferData(this.bufferid, format, rawAudioBuffer, sampleRate);
		
		this.sourceid = alGenSources();
		
		alSourcei(sourceid, AL_BUFFER, this.bufferid);
		alSourcei(sourceid, AL_LOOPING, loops?1:0);
		alSourcei(sourceid, AL_POSITION, 0);
		alSourcef(sourceid, AL_GAIN, DEFAULT_GAIN);
		
		free(rawAudioBuffer);
	}
	
	private Sound(String file) {
		this(file, false);
	}
	
	public void play() {
		int state = alGetSourcei(this.sourceid, AL_SOURCE_STATE);
		if(state == AL_STOPPED) {
			isPlaying = false;
			alSourcei(this.sourceid, AL_POSITION, 0);
		}
		
		if(!this.isPlaying) {
			this.isPlaying = true;
			alSourcePlay(this.sourceid);
		}
	}
	
	public void pause() {
		int state = alGetSourcei(this.sourceid, AL_SOURCE_STATE);
		if(state != AL_STOPPED || state != AL_PAUSED) {
			this.isPlaying = false;
			alSourcePause(this.sourceid);
		}
	}
	
	public void stop() {
		if(this.isPlaying) {
			this.isPlaying = false;
			alSourceStop(this.sourceid);
		}
	}
	
	public void delete() {
		alDeleteBuffers(this.bufferid);
		alDeleteSources(this.sourceid);
	}
	
	public static void cleanUp() {
		if(!Sound.sounds.isEmpty())for(Sound s: Sound.sounds.values())s.delete();
		Sound.sounds.clear();
	}
	
	public boolean isPlaying() {
		int state = alGetSourcei(this.sourceid, AL_SOURCE_STATE);
		if(state == AL_STOPPED || state == AL_PAUSED)this.isPlaying = false;
		return this.isPlaying;
	}
	
	public String getFile() {
		return this.file;
	}
	
}
