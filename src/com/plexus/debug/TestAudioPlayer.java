/*
 * Created on 7-okt-2005
 *
 */
package com.plexus.debug;

import com.plexus.sam.audio.Mp3Song;
import com.plexus.sam.audio.Song;
import com.plexus.sam.audio.SongStream;

public class TestAudioPlayer {
	String file1 = "/media/Liedjes/Spearhead/Spearhead - Best of Acid Jazz - The Brand New Heavies - Acid jazz.mp3";
	String file2 = "/media/Liedjes/Spearhead/Spearhead - Home - 06 - Hole In The Bucket.mp3";
	
	SongStream stream1 = new SongStream();
	SongStream stream2 = new SongStream();
	
	Song song1 = new Mp3Song();
	Song song2 = new Mp3Song();
	
	public TestAudioPlayer() {
		song1.setFile(file1);
		song2.setFile(file2);
		
		stream1.setMedia(song1);
		stream2.setMedia(song2);
		
		stream1.initLine();
		stream2.initLine();
		
		stream1.start();
		stream2.start();
	}
	
	public static void main(String[] args) {
		new TestAudioPlayer();
	}
}
