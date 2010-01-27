package com.gampire.pc.sound;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

public class AudioClipUtil {

    public static AudioClip getAudioClip(String fileName) {
        AudioClip clip;
        String path = "/com/gampire/pc/resources/sounds/" + fileName;
        URL url = AudioClipUtil.class.getResource(path);
        if (url != null) {
            clip = Applet.newAudioClip(url);
        } else {
            clip = null;
        }
        return clip;
    }
}
