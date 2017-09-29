package com.olegsagenadatrytwo.partyapp.utilities.ConvertionUtilities;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static com.olegsagenadatrytwo.partyapp.Constant.MILES_TO_METERS_RATIO;

/**
 * Created by aaron on 9/18/17.
 */

public class ConvertionUtilities {
    public static double convertMetersToMiles(double meters){
        return meters / MILES_TO_METERS_RATIO;
    }
    public static double convertMilesToMeters(double miles){
        return miles * MILES_TO_METERS_RATIO;
    }
    public static final double convertMetersToKilometers(double meters){
        return meters/1000;
    }
    public static final double convertKilometersToMeters(double kilometers){
        return kilometers * 1000;
    }
    public static final int convertMinutesToMillisec(int minutes){
        return minutes * 60 * 1000;
    }

    public static Bitmap convertToMutable(final Context context, final Bitmap imgIn) {
        final int width = imgIn.getWidth(), height = imgIn.getHeight();
        final Bitmap.Config type = imgIn.getConfig();
        File outputFile = null;
        final File outputDir = context.getCacheDir();
        try {
            outputFile = File.createTempFile(Long.toString(System.currentTimeMillis()), null, outputDir);
            outputFile.deleteOnExit();
            final RandomAccessFile randomAccessFile = new RandomAccessFile(outputFile, "rw");
            final FileChannel channel = randomAccessFile.getChannel();
            final MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            imgIn.recycle();
            final Bitmap result = Bitmap.createBitmap(width, height, type);
            map.position(0);
            result.copyPixelsFromBuffer(map);
            channel.close();
            randomAccessFile.close();
            outputFile.delete();
            return result;
        } catch (final Exception e) {
        } finally {
            if (outputFile != null)
                outputFile.delete();
        }
        return null;
    }

}
