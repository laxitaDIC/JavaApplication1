/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sounds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * Reads data from the input channel and writes to the output stream
 */
public class Playback implements Runnable {

    TargetDataLine line;

    Thread thread;
    private double duration;
    private AudioInputStream audioInputStream;
    private ByteArrayOutputStream out;
    private AudioFormat format;

    public void start() {
        thread = new Thread(this);
        thread.setName("Capture");
        thread.start();
    }

    public void stop() {
        thread = null;

    }

    public void save(File wavFile) throws IOException {
        byte[] audioData = out.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format,
                audioData.length / format.getFrameSize());

        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);

        audioInputStream.close();
        out.close();
    }

    public void run() {

        duration = 0;
        audioInputStream = null;
        format = getAudioFormat();

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, " Please check if the mic connected properly.", ButtonType.OK);
            alert.showAndWait();
        }
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, line.getBufferSize());
        } catch (LineUnavailableException ex) {
            return;
        } catch (SecurityException ex) {
            //JavaSound.showInfoDialog();
            return;
        } catch (Exception ex) {
            return;
        }

        // play back the captured audio data
        out = new ByteArrayOutputStream();
        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInFrames = line.getBufferSize() / 8;
        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead;
        if (!AudioSystem.isLineSupported(info)) {
            Alert alert = new Alert(AlertType.WARNING, " Please check if the mic connected properly.", ButtonType.OK);
            alert.showAndWait();
        //    stage.close();
        }
        line.start();

        while (thread
                != null) {
            if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                break;
            }

            out.write(data, 0, numBytesRead);
            System.out.println(calculateRMSLevel(data));

        }

        // we reached the end of the stream.
        // stop and close the line.
        line.stop();

        line.close();
        line = null;

        // stop and close the output stream
        try {
            out.flush();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // load bytes into the audio input stream for playback
        byte audioBytes[] = out.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

        long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
        duration = milliseconds / 1000.0;

        try {
            audioInputStream.reset();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

    }

    public int calculateRMSLevel(byte[] audioData) {
        long lSum = 0;
        for (int i = 0; i < audioData.length; i++) {
            lSum = lSum + audioData[i];
        }

        double dAvg = lSum / audioData.length;
        double sumMeanSquare = 0d;

        for (int j = 0; j < audioData.length; j++) {
            sumMeanSquare += Math.pow(audioData[j] - dAvg, 2d);
        }

        double averageMeanSquare = sumMeanSquare / audioData.length;

        return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5);
    }

    AudioFormat getAudioFormat() {
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float sampleRate = 10000.0F;
        int sampleSizeInBits = 16;
        int channels = 1;
        int frameSize = 2;
        float frameRate = 44100.0F;
        boolean bigEndian = true;
        return new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
    }
} // End class Capture
