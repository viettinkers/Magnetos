package viettinkers.magnetos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * 
 */

/**
 * An utility class to provide audio capturing features.
 * @author vietanh
 *
 */
public class AudioCapturer {
  private static AudioCapturer instance = null;

  // Prevents instantiation.
  protected AudioCapturer() {
  }

  // Returns the singleton instance.
  public static AudioCapturer getInstance() {
    if (instance == null) {
      instance = new AudioCapturer();
    }
    return instance;
  }

  private static final float SAMPLE_RATE = 8000;
  private static final int SAMPLE_SIZE_IN_BITS = 8;
  private static final int CHANNELS = 1;
  private static final boolean SIGNED = true;
  private static final boolean BIG_ENDIAN = true;

  protected boolean running;
  private ByteArrayOutputStream out;
  private Runnable captureRunner;

  /**
   * Starts capturing audio.
   */
  public void start() {
    if (captureRunner == null) {
      initCaptureRunner();
    }
    new Thread(captureRunner).start();
  }

  /**
   * Stops capturing audio.
   */
  public void stop() {
    running = false;
  }

  /**
   * Plays the captured audio.
   */
  public void play() {
    if (out == null) {
      return;
    }
    try {
      byte audio[] = out.toByteArray();
      InputStream input = new ByteArrayInputStream(audio);
      final AudioFormat format = getFormat();
      final AudioInputStream ais =
          new AudioInputStream(input, format, audio.length / format.getFrameSize());
      DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
      final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
      line.open(format);
      line.start();

      Runnable playRunner = new Runnable() {
        int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
        byte buffer[] = new byte[bufferSize];
 
        public void run() {
          try {
            int count;
            while ((count = ais.read( buffer, 0, buffer.length)) != -1) {
              if (count > 0) {
                line.write(buffer, 0, count);
              }
            }
            line.drain();
            line.close();
          } catch (IOException e) {
            System.err.println("I/O problems: " + e);
            System.exit(-3);
          }
        }
      };
      new Thread(playRunner).start();
    } catch (LineUnavailableException e) {
      System.err.println("Line unavailable: " + e);
      System.exit(-4);
    }
  }

  /**
   * Initializes the {@code captureRunner} to start capturing thread later.
   */
  private void initCaptureRunner() {
    try {
      final AudioFormat format = getFormat();
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
      final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
      line.open(format);
      line.start();
      captureRunner = new Runnable() {
        int bufferSize = (int)format.getSampleRate() * format.getFrameSize();
        byte buffer[] = new byte[bufferSize];
 
        public void run() {
          out = new ByteArrayOutputStream();
          running = true;
          try {
            while (running) {
              int count = line.read(buffer, 0, buffer.length);
              if (count > 0) {
                out.write(buffer, 0, count);
              }
            }
            out.close();
          } catch (IOException e) {
            System.err.println("I/O problems: " + e);
            System.exit(-1);
          }
        }
      };
    } catch (LineUnavailableException e) {
      System.err.println("Line unavailable: " + e);
    }
  }

  /**
   * @return The default audio format.
   */
  private AudioFormat getFormat() {
    return new AudioFormat(
        SAMPLE_RATE,
        SAMPLE_SIZE_IN_BITS,
        CHANNELS,
        SIGNED,
        BIG_ENDIAN);
  }
}
