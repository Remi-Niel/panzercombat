package com.gampire.pc.speech;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class Speaker {

	static Speaker instance = null;

	public static Speaker getInstance() {
		if(instance==null) {
			instance=new Speaker();
		}
		return instance;
	}
	
    private static String VOICE_NAME = "kevin16";

    private Voice voice;
    
    private Speaker() {
    	voice = VoiceManager.getInstance().getVoice(VOICE_NAME);
        // Allocates the resources for the voice.
        voice.allocate();
        voice.setRate(160);
    }

    public void speakAndWait(String message) {
        voice.speak(message);
    }
 
    public void speak(String message) {
        new SpeakerThread(message).start();
    }
    
    private class SpeakerThread extends Thread {
        private String message;
        
        SpeakerThread(String message) {
        	super("SpeakerThread");
            this.message = message;
        }
        
        @Override
		public void run() {
        	voice.speak(message);
        }
    }
}
