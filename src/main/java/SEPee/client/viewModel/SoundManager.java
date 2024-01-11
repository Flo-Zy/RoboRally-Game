package SEPee.client.viewModel;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.*;

public class SoundManager {
    private static boolean isMuted = false;
    private static final Map<String, MediaPlayer> soundPlayers = new HashMap<>();
    private static final List<MediaPlayer> allMediaPlayers = new ArrayList<>();

    public static void playSound(String soundName) {
        try {
            String soundFilePath = "src/main/resources/Sounds/Events/" + soundName + ".mp3";
            Media sound = new Media(new File(soundFilePath).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            allMediaPlayers.add(mediaPlayer);
            if (!isMuted) {
                mediaPlayer.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playEventSound(String eventName) {
        System.out.println("event name: " + eventName + "ismuted: " + isMuted);
        try {
            File eventDirectory = new File("src/main/resources/Sounds/Events/" + eventName);
            if (eventDirectory.isDirectory()) {
                List<String> soundFiles = new ArrayList<>();
                for (File file : eventDirectory.listFiles()) {
                    if (file.isFile()) {
                        soundFiles.add(file.toURI().toString());
                    }
                }

                if (!soundFiles.isEmpty()) {
                    Random random = new Random();
                    String randomSound = soundFiles.get(random.nextInt(soundFiles.size()));
                    Media sound = new Media(randomSound);
                    MediaPlayer mediaPlayer = new MediaPlayer(sound);
                    allMediaPlayers.add(mediaPlayer);
                    if (!isMuted) {
                        mediaPlayer.play();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playUISound(String eventName) {
        try {
            File eventDirectory = new File("src/main/resources/Sounds/UI/" + eventName);
            if (eventDirectory.isDirectory()) {
                List<String> soundFiles = new ArrayList<>();
                for (File file : eventDirectory.listFiles()) {
                    if (file.isFile()) {
                        soundFiles.add(file.toURI().toString());
                    }
                }

                if (!soundFiles.isEmpty()) {
                    Random random = new Random();
                    String randomSound = soundFiles.get(random.nextInt(soundFiles.size()));
                    Media sound = new Media(randomSound);
                    MediaPlayer mediaPlayer = new MediaPlayer(sound);
                    allMediaPlayers.add(mediaPlayer);
                    if (!isMuted) {
                        mediaPlayer.play();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toggleSoundMute() {
        isMuted = !isMuted;
        // Mute/unmute all MediaPlayers stored in allMediaPlayers list
        for (MediaPlayer mediaPlayer : allMediaPlayers) {
            mediaPlayer.setMute(isMuted);
            if (isMuted) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        }
    }
}
