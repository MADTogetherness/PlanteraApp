package com.example.planteraapp.Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.TimeZone;
import android.util.Base64;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class AttributeConverters {
    @TypeConverter
    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        b = b.length > 500000 ? Compress(b) : b;
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    @TypeConverter
    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @TypeConverter
    public Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    private static byte[] Compress(byte[] img) {
        while (img.length > 900000) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.8), (int) (bitmap.getHeight() * 0.8), false);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
            img = stream.toByteArray();
        }
        return img;
    }

    public static Gson getGsonParser() {
        return new GsonBuilder().create();
    }

    public static long getEpochTime(int h, int m) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long[] getHoursAndMinutes(long millis) {
        long seconds = Math.round((double) millis / 1000);
        long hours = TimeUnit.SECONDS.toHours(seconds);
        if (hours > 0)
            seconds -= TimeUnit.HOURS.toSeconds(hours);
        long minutes = seconds > 0 ? TimeUnit.SECONDS.toMinutes(seconds) : 0;
        if (minutes > 0)
            seconds -= TimeUnit.MINUTES.toSeconds(minutes);
        return new long[]{hours, minutes, seconds};
    }

    public static long getMillisFrom(int hour, int minutes) {
        return TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minutes);
    }

    public static long getMillisFrom(int days) {
        return TimeUnit.DAYS.toMillis(days);
    }

    public static int toDays(long millis) {
        return (int) TimeUnit.MILLISECONDS.toDays(millis);
    }

    public static String getReadableTime(long millis) {
        long[] time = getHoursAndMinutes(millis);
        String suffix = (time[0] > 11) ? "PM" : "AM";
        time[0] = (time[0] > 12) ? time[0] -= 12 : ((time[0] == 0) ? 12 : time[0]);
        return String.format(Locale.getDefault(), "%02d:%02d %s", time[0], time[1], suffix);
    }

    public static String getReadableTime(int hours, int minutes) {
        return String.format(Locale.getDefault(), "%02d:%02d %s", (hours > 12) ? hours - 12 : ((hours == 0) ? 12 : hours), minutes, (hours > 11) ? "PM" : "AM");
    }

    // Gets remaining time from NOW to notification time in hh hours mm minutes format
    public static String getRemainingTime(long durationRemaining) {
        Duration d = Duration.ofMillis(durationRemaining - System.currentTimeMillis());
        long days = d.toDays(), hours = d.toHours() % 24;
        String dy = days > 0 ? String.format(Locale.getDefault(), "%d day(s) ", days) : "";
        String h = hours > 0 ? String.format(Locale.getDefault(), "%02d hr(s) ", hours) : "";
        String m = String.format(Locale.getDefault(), "%02d min(s)", d.toMinutes() % 60);
        return dy + h + m;
    }

}
