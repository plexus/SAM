/*
 * Created on 14-okt-2005
 *
 */
package com.plexus.sam.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

/**
 * TODO: this is mostly copied from the jMusic project, could use a clean-room implementation
 * 
 * @author plexus
 *
 */
public class Compressor {
	/** 
	 * A positive value above which the amplitude is attenuated.
	 */
	private float threshold = (float)1.0;
	
	/**
	 * The attenuation value. 1.0 makes no change. 4.0 = 1:4 ratio.
	 */
	private double ratio = 1.0;
	
	/** The amount of real gain reduction 
	 * allowing for perceptual loudness effect */
	private float gainReduction = 1.0f;
	
	// compensate for compression volume reduction
	private float gain = 1.0f;
	
	/**
	 * The standard Compressor constructor takes a Single
	 * Audio Object, threshold and ratio as inputs. 
	 * @param ao The single AudioObject taken as input.
	 * @param thresh The value above which to compress.
	 * @param ratio The attenuation divisor value.
	 */
	public Compressor(double thresh, double ratio, double gain){
		this.threshold = (float)thresh;
		this.ratio = ratio;
		this.gain = (float)gain;
		calcGainReduction();
	}
	
public Compressor() {}

	//	Reduces the raw ratio to a logrithmic reduction value
	// which compensates for the logarithmic nature of
	// loudness perception.
	private void calcGainReduction() {
		if (ratio == 1.0) {
			this.gainReduction = 1.0f;
		}
		else if (ratio > 1.0 ) {
			this.gainReduction = (float)Math.min(1.0, (Math.abs(Math.log(1.0 - 1.0/ratio) * 0.2 )));
		}
		else if (ratio > 0.0) { // ratio less than 1.0
			this.gainReduction = (float)(1.0 / ratio);
		}
		else {
			System.out.println("jMusic error: Compressor ratio values cannot be less than 0.0");
			System.exit(0);
		}
		
	}
	
	//----------------------------------------------
	// Protected Methods
	//----------------------------------------------
	/**
	 * The nextWork method for <bl>Compressor<bl> will divide
	 * the sample value above the threshold by the ratio. 
	 */
	public void work(float[] buffer, int samplecount) {
		for(int i=0;i<samplecount;i++){
			// positive values
			if(buffer[i] > threshold) {
				buffer[i] = threshold + (buffer[i] - threshold) * this.gainReduction;
			}
			// negative values
			if(buffer[i] < -threshold) {
				buffer[i] = -threshold + (buffer[i] + threshold) * this.gainReduction;
			}
			
			buffer[i] *= gain;
		}
	}
	
	/**
	 * Specify a new threshold value above which the audio will be compressed.
	 * @param Thresh The value. 1.0 by default.
	 */
	public void setThreshold(double thresh){
		this.threshold = (float)thresh;
	}
	
	/**
	 * Specify a new ratio value - the amount of compression to apply.
	 * @param ratio The ration value. 1.0 by default (no effect).
	 */
	public void setRatio(double ratio){
		this.ratio = ratio;
		calcGainReduction();
		
	}
	
	/**
	 * Specify a new ratio value - the amount of compression to apply.
	 * @param ratio The ration value. 1.0 by default (no effect).
	 */
	public void setGain(double gain){
		this.gain = (float)gain;        
	}

	float[] myBuffer = new float[SongStream.EXTERNAL_BUFFER_SIZE];
	
	public void compress(byte[] buffer, int position, int length, AudioFormat f) {
		
		
	}
}
