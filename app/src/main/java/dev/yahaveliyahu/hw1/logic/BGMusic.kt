package dev.yahaveliyahu.hw1.logic


import android.content.Context
import android.media.MediaPlayer
import dev.yahaveliyahu.hw1.R

class BGMusic {
    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.us_sounds)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } else if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }
    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}