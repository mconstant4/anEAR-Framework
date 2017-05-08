package wbl.egr.uri.anear.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Created by root on 5/1/17.
 */

public class WavObject extends AudioStorageObject {
    private File mDestination;

    public WavObject(File destination) {
        mDestination = destination;

        mDestination.getParentFile().mkdirs();
    }

    @Override
    public void processRawAudio(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            File directory = mDestination.getParentFile();
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".wav");
                }
            };
            int count = directory.list(filter).length;

            String fileName = mDestination.getName()
                    .substring(0, mDestination.getName().length() - 4) + "_" + count + ".wav";

            File destination = new File(mDestination.getParentFile(), fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(destination);

            writeHeader(fileInputStream, fileOutputStream);
            byte[] audioData = new byte[2048];
            while (fileInputStream.read(audioData, 0, 2048) != -1) {
                fileOutputStream.write(audioData);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            fileInputStream.close();

            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHeader(FileInputStream inputStream, FileOutputStream outputStream) {
        byte[] header = new byte[44];
        int channels = 1;
        long longSampleRate = 44100;
        long byteRate = 16 * longSampleRate * channels/8;
        long totalDataLen, totalAudioLen;
        try {
            totalAudioLen = inputStream.getChannel().size();
        } catch (IOException e) {
            totalAudioLen = 0;
            e.printStackTrace();
        }
        totalDataLen = totalAudioLen + 36;

        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        try {
            outputStream.write(header);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
