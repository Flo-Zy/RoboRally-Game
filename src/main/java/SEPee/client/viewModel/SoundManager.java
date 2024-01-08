package SEPee.client.viewModel;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.*;

public class SoundManager {
    private static boolean isMuted = false;

    private static final Map<String, MediaPlayer> soundPlayers = new HashMap<>();

    public static void playSound(String soundName) {
        try {
            String soundFilePath = "src/main/resources/Sounds/" + soundName + ".mp3";
            Media sound = new Media(new File(soundFilePath).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
            soundPlayers.put(soundName, mediaPlayer); // Store MediaPlayer instance
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playEventSound(String eventName) {
        try {
            File eventDirectory = new File("src/main/resources/Sounds/" + eventName);
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
                    mediaPlayer.play();
                    soundPlayers.put(eventName, mediaPlayer); // Store MediaPlayer instance
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toggleSoundMute() {
        isMuted = !isMuted;
        soundPlayers.forEach((soundName, mediaPlayer) -> mediaPlayer.setMute(isMuted));
    }
}
