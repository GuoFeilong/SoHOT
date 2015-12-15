package tools;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class MusicUtils {
	private static MusicUtils instance;
	private SoundPool mSoundPool;
	private int mVoiceResource;
	private Context context;

	synchronized public static MusicUtils getInstance(Context context) {
		if (null == instance) {
			instance = new MusicUtils(context);
		}
		return instance;
	}

	private MusicUtils(Context context) {
		this.context = context;
	}

	public MusicUtils(Context context, int mVoiceResource) {
		super();
		this.context = context;
		this.mVoiceResource = mVoiceResource;
		preLoadRaw(mVoiceResource);
	}

	private void preLoadRaw(int voiceResource) {
		mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

		try {
			mVoiceResource = mSoundPool.load(context, voiceResource, 1);
		} catch (Exception e) {

		}
	}

	public int playMusic(boolean loop) {
		AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = streamVolumeCurrent / streamVolumeMax;
		int streamId;
		if (loop) {
			streamId = mSoundPool.play(mVoiceResource, volume, volume, 1, -1, 1f);
		} else {
			streamId = mSoundPool.play(mVoiceResource, volume, volume, 1, 0, 1f);
		}
		return streamId;
	}

	public void stopMusic(int streamId) {
		mSoundPool.stop(streamId);
	}

}
