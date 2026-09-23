package tech.nikelyh.quizpit.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

object SoundManager {
    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<String, Int>()
    private val pendingPlays = mutableSetOf<Int>()

    fun init(context: Context) {
        if (soundPool == null) {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            
            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(attributes)
                .build()

            soundPool?.setOnLoadCompleteListener { sp, sampleId, status ->
                if (status == 0 && pendingPlays.contains(sampleId)) {
                    sp.play(sampleId, 1f, 1f, 1, 0, 1f)
                    pendingPlays.remove(sampleId)
                }
            }

            // Pre-load common UI sounds
            loadSound(context, "ui/audio/cursor-select.mp3")
            loadSound(context, "ui/audio/confirm-ding.mp3")
            loadSound(context, "ui/audio/power-activation.mp3")
            loadSound(context, "ui/audio/podium-pop.mp3")
        }
    }

    private fun loadSound(context: Context, assetPath: String): Int {
        if (soundIds.containsKey(assetPath)) {
            return soundIds[assetPath]!!
        }
        return try {
            val afd = context.assets.openFd(assetPath)
            val soundId = soundPool?.load(afd, 1) ?: -1
            if (soundId != -1) {
                soundIds[assetPath] = soundId
            }
            soundId
        } catch (e: Exception) {
            Log.e("SoundManager", "Error loading sound $assetPath", e)
            -1
        }
    }

    fun playClickSound(context: Context) {
        init(context)
        playSound(context, "ui/audio/cursor-select.mp3")
    }
    
    fun playConfirmSound(context: Context) {
        init(context)
        playSound(context, "ui/audio/confirm-ding.mp3")
    }
    
    fun playEquipSound(context: Context) {
        init(context)
        playSound(context, "ui/audio/power-activation.mp3")
    }

    fun playOpeningSound(context: Context) {
        init(context)
        playSound(context, "ui/audio/podium-pop.mp3")
    }

    fun playCharacterSound(context: Context, characterId: String) {
        init(context)
        // Some mock ids might not have folders if they are dynamically created or spelled differently, 
        // but based on the assets folder, they match the id (e.g., 'dog', 'medusa' which might not exist if it's 'medusa' but I'll use try-catch)
        val path = "characters/$characterId/select.mp3"
        playSound(context, path)
    }

    private fun playSound(context: Context, path: String) {
        val id = if (soundIds.containsKey(path)) soundIds[path]!! else loadSound(context, path)
        if (id != -1) {
            // SoundPool.play returns 0 if it fails (e.g. not loaded yet)
            val streamId = soundPool?.play(id, 1f, 1f, 1, 0, 1f) ?: 0
            if (streamId == 0) {
                // If it failed to play, it might still be loading. Add to pending plays.
                pendingPlays.add(id)
            }
        }
    }
}
