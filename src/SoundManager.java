package org.example;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {
    private Clip backsoundClip;

    public void playLoopingBacksound() {
        try {
            URL url = getClass().getResource("/assets/backsound-music.wav");
            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                backsoundClip = AudioSystem.getClip();
                backsoundClip.open(audioIn);
                // Set agar musik berputar terus-menerus
                backsoundClip.loop(Clip.LOOP_CONTINUOUSLY);
                backsoundClip.start();
            }
        } catch (Exception e) {
            System.err.println("Gagal memutar backsound: " + e.getMessage());
        }
    }

    public void playWinSound() {
        try {
            URL url = getClass().getResource("/assets/winner-sound.wav");
            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            }
        } catch (Exception e) { /* Silent */ }
    }
}