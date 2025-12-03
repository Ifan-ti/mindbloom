package com.project.client; // (Ganti package ini jika Anda menyimpannya di tempat lain)

import android.text.format.DateUtils;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {

    private static final String TAG = "TimeUtil";

    /**
     * Mengubah ISO 8601 timestamp (e.g., "2025-11-10T18:39:30.000Z")
     * menjadi string waktu relatif (e.g., "5 menit lalu", "1 jam lalu").
     */
    public static String getRelativeTime(String isoTimestamp) {
        if (isoTimestamp == null || isoTimestamp.isEmpty()) {
            return "Baru saja";
        }

        try {
            // 1. Tentukan format tanggal dari API
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            // 2. Wajib: Atur TimeZone ke UTC ("Z" berarti Zulu/UTC)
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            // 3. Ubah string menjadi objek Date
            Date date = sdf.parse(isoTimestamp);
            long timeInMillis = date.getTime();

            // 4. Dapatkan waktu sekarang
            long nowInMillis = System.currentTimeMillis();

            // 5. Gunakan utility bawaan Android
            return DateUtils.getRelativeTimeSpanString(
                    timeInMillis,
                    nowInMillis,
                    DateUtils.MINUTE_IN_MILLIS // Presisi minimal (cth: "0 menit lalu")
            ).toString();

        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + isoTimestamp, e);
            // Fallback jika gagal: tampilkan tanggalnya saja
            if (isoTimestamp.length() > 10) {
                return isoTimestamp.substring(0, 10); // "2025-11-10"
            }
            return "Baru saja";
        }
    }
}