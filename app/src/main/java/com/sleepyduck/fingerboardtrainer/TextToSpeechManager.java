
package com.sleepyduck.fingerboardtrainer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

class TextToSpeechManager {
    private static boolean HasEngine = true;

    private Context mContext;

    private boolean mIsTextToSpeechActive = false;

    private TextToSpeech mTTS;

    private Map<String, File> mSpeachMap = new HashMap<String, File>();

    private Map<String, MediaPlayer> mMediaMap = new HashMap<String, MediaPlayer>();

    private TextToSpeech.OnInitListener mInitListener = new TextToSpeech.OnInitListener() {

        @Override
        public void onInit(int status) {
            mIsTextToSpeechActive = status == TextToSpeech.SUCCESS;
            if (mIsTextToSpeechActive) {
                if (mTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE) {
                    mTTS.setLanguage(Locale.US);
                }
            }
        }
    };

    private UtteranceProgressListener mUttranceListener = new UtteranceProgressListener() {

        @Override
        public void onStart(String utteranceId) {
        }

        @Override
        public void onError(String utteranceId) {
        }

        @Override
        public void onDone(String text) {
            if (mSpeachMap.containsKey(text)) {
                mMediaMap.put(text,
                        MediaPlayer.create(mContext, Uri.fromFile(mSpeachMap.get(text))));
            }
        }
    };

    TextToSpeechManager(Context context) {
        mContext = context;
        if (HasEngine) {
            mTTS = new TextToSpeech(mContext, mInitListener);
            mTTS.setOnUtteranceProgressListener(mUttranceListener);
        }
    }

    void shutdown() {
        for (File file : mSpeachMap.values()) {
            file.deleteOnExit();
        }
        for (MediaPlayer mp : mMediaMap.values()) {
            mp.release();
        }
        mTTS.shutdown();
    }

    boolean isActive() {
        return mIsTextToSpeechActive && HasEngine;
    }

    private void say(String text) {
        if (!mIsTextToSpeechActive) {
            return;
        }

        if (mMediaMap.containsKey(text)) {
            mMediaMap.get(text).start();
        } else {
            if (mTTS.isSpeaking()) {
                mTTS.stop();
            }

            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

            prepareTextToSay(text);
        }
    }

    void say(int textResId) {
        say(mContext.getString(textResId));
    }

    private void prepareTextToSay(String text) {
        if (!mIsTextToSpeechActive) {
            return;
        }

        try {
            File tmpfile = File.createTempFile("tts", ".wav");
            Timber.d("Synthesizing to file %s", tmpfile.getAbsolutePath());
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);
            int res = mTTS.synthesizeToFile(text, map, tmpfile.getAbsolutePath());
            if (res == TextToSpeech.SUCCESS) {
                Timber.d("Synthesizing success");
                mTTS.addSpeech(text, tmpfile.getAbsolutePath());
                mSpeachMap.put(text, tmpfile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void prepareTextToSay(int textResId) {
        prepareTextToSay(mContext.getString(textResId));
    }

    static void initializeTTSCheck(Activity activity) {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        if (checkIntent.resolveActivity(activity.getPackageManager()) == null) {
            Intent installIntent = new Intent();
            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            activity.startActivityForResult(installIntent, MainActivity.REQUEST_TTS);
            HasEngine = false;
        }
    }

    static void setHasEngine(boolean hasEngine) {
        HasEngine = hasEngine;
    }
}
