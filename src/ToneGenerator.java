import javax.sound.sampled.*;

public class ToneGenerator extends Thread {
    private volatile boolean running = true;
    private volatile double baseFrequency = 100;
    private final double volume = 1.0;

    public void setFrequency(double frequency) {
        this.baseFrequency = frequency;
    }

    public void stopEngine() {
        this.running = false;
    }

    @Override
    public void run() {
        try {
            float sampleRate = 44100;
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];


            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, true);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format, bufferSize);
            line.start();

            double angle = 0.0;
            double harmonic1Angle = 0.0;
            double harmonic2Angle = 0.0;
            double step, harmonic1Step, harmonic2Step;

            while (running) {
                step = 2.0 * Math.PI * baseFrequency / sampleRate;
                harmonic1Step = 2.0 * Math.PI * (baseFrequency * 2) / sampleRate;
                harmonic2Step = 2.0 * Math.PI * (baseFrequency * 3) / sampleRate;

                for (int i = 0; i < buffer.length; i++) {

                    double sample = Math.sin(angle) + 0.5 * Math.sin(harmonic1Angle) + 0.25 * Math.sin(harmonic2Angle);

                    sample = Math.max(-1.0, Math.min(1.0, sample));


                    buffer[i] = (byte) (sample * 127 * volume);


                    angle += step;
                    harmonic1Angle += harmonic1Step;
                    harmonic2Angle += harmonic2Step;


                    if (angle > 2.0 * Math.PI) angle -= 2.0 * Math.PI;
                    if (harmonic1Angle > 2.0 * Math.PI) harmonic1Angle -= 2.0 * Math.PI;
                    if (harmonic2Angle > 2.0 * Math.PI) harmonic2Angle -= 2.0 * Math.PI;
                }

                line.write(buffer, 0, buffer.length);
            }

            line.drain();
            line.close();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}