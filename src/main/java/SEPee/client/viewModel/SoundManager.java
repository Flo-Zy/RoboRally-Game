package SEPee.client.viewModel;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.*;

public class SoundManager {
    private static double uiSoundVolume = 0.5;
    private static double eventSoundVolume = 0.5;
    private static double musicVolume = 0.5;
    private static double masterVolume = 0.5;
    private static boolean isMuted = false;
    private static MediaPlayer backgroundMediaPlayer;
    private static final List<MediaPlayer> allMediaPlayers = new ArrayList<>();


    public static void playMusic(String soundName) {
        try {
            String soundFilePath = "src/main/resources/Sounds/Music/" + soundName + ".mp3";
            Media sound = new Media(new File(soundFilePath).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            allMediaPlayers.add(mediaPlayer);

            if (!isMuted) {
                mediaPlayer.setVolume(musicVolume * masterVolume);
                mediaPlayer.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playEventSound(String eventName) {
        System.out.println("event name: " + eventName + " ismuted: " + isMuted);
        try {
            if (backgroundMediaPlayer != null && backgroundMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                return;
            }

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
                    backgroundMediaPlayer = new MediaPlayer(sound);
                    allMediaPlayers.add(backgroundMediaPlayer);

                    if (!isMuted) {
                        backgroundMediaPlayer.play();
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
                        mediaPlayer.setVolume(uiSoundVolume * masterVolume);
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

        if (isMuted) {
            masterVolume = 0.0;
        } else {
            masterVolume = 1.0;
        }

        updateAllMediaPlayersVolume();
    }

    public static void setUISoundVolume(double volume) {
        uiSoundVolume = volume;
        updateAllMediaPlayersVolume();
    }

    public static void setEventSoundVolume(double volume) {
        eventSoundVolume = volume;
        updateAllMediaPlayersVolume();
    }

    public static void setMusicVolume(double volume) {
        musicVolume = volume;
        updateAllMediaPlayersVolume();
    }

    public static void setMasterVolume(double volume) {
        masterVolume = volume;
        updateAllMediaPlayersVolume();
    }

    private static void updateAllMediaPlayersVolume() {
        for (MediaPlayer mediaPlayer : allMediaPlayers) {
            if (mediaPlayer != null) {
                double volume = 1.0;

                if (mediaPlayer == backgroundMediaPlayer) {
                    volume = eventSoundVolume;
                } else if (mediaPlayer.getMedia().getSource().contains("/Sounds/UI/")) {
                    volume = uiSoundVolume;
                } else if (mediaPlayer.getMedia().getSource().contains("/Sounds/Music/")) {
                    volume = musicVolume;
                } else {
                    volume = musicVolume;
                }

                mediaPlayer.setVolume(volume * masterVolume);
            }
        }
    }

    @FunctionalInterface
    interface VolumeSetter {
        void setVolume(double volume);
    }
}