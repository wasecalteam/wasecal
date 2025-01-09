package util;

// インポート
import java.time.*;
import java.time.format.*;

// 日付と時間を扱うためのユーティリティクラス
public class DateTimeUtil {
    // 日付のフォーマット
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    // 時間のフォーマット
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    // 日付と時間のフォーマット
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    // iCalendar形式の日時フォーマッター
    private static final DateTimeFormatter ICAL_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    private static final DateTimeFormatter ICAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 時限の開始時刻を定数として定義
    private static final LocalTime PERIOD_MORNING_START = LocalTime.of(0, 0);   // 朝
    private static final LocalTime PERIOD_1_START = LocalTime.of(8, 50);       // 1限
    private static final LocalTime PERIOD_2_START = LocalTime.of(10, 40);      // 2限
    private static final LocalTime PERIOD_LUNCH_START = LocalTime.of(12, 20);  // 昼休み
    private static final LocalTime PERIOD_3_START = LocalTime.of(13, 10);      // 3限
    private static final LocalTime PERIOD_4_START = LocalTime.of(15, 5);       // 4限
    private static final LocalTime PERIOD_5_START = LocalTime.of(17, 0);       // 5限
    private static final LocalTime PERIOD_6_START = LocalTime.of(18, 55);      // 6限
    private static final LocalTime PERIOD_7_START = LocalTime.of(20, 45);      // 7限
    private static final LocalTime PERIOD_NIGHT_START = LocalTime.of(21, 35);  // 夜

    // 時限の終了時刻を定数として定義
    private static final LocalTime PERIOD_1_END = LocalTime.of(10, 39);       // 1限終了
    private static final LocalTime PERIOD_2_END = LocalTime.of(12, 19);       // 2限終了
    private static final LocalTime PERIOD_LUNCH_END = LocalTime.of(13, 9);    // 昼休み終了
    private static final LocalTime PERIOD_3_END = LocalTime.of(15, 4);        // 3限終了
    private static final LocalTime PERIOD_4_END = LocalTime.of(16, 59);       // 4限終了
    private static final LocalTime PERIOD_5_END = LocalTime.of(18, 54);       // 5限終了
    private static final LocalTime PERIOD_6_END = LocalTime.of(20, 44);       // 6限終了
    private static final LocalTime PERIOD_7_END = LocalTime.of(21, 34);       // 7限終了
    private static final LocalTime DAY_END = LocalTime.of(23, 59);           // 日終了

    // 文字列から日付を解析
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    // 文字列から時刻を解析
    public static LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }

    // 文字列から日時を解析
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    // 日付を文字列にフォーマット
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    // 時刻を文字列にフォーマット
    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    // 日時を文字列にフォーマット
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }

    // 時刻から時限を取得
    public static int getPeriodFromTime(LocalTime time) {
        if (time.isBefore(PERIOD_1_START)) return 0;         // 朝（0:00-8:49）
        if (time.isBefore(PERIOD_2_START)) return 1;         // 1限（8:50-10:39）
        if (time.isBefore(PERIOD_LUNCH_START)) return 2;     // 2限（10:40-12:19）
        if (time.isBefore(PERIOD_3_START)) return 3;         // 昼休み（12:20-13:09）
        if (time.isBefore(PERIOD_4_START)) return 4;         // 3限（13:10-15:04）
        if (time.isBefore(PERIOD_5_START)) return 5;         // 4限（15:05-16:59）
        if (time.isBefore(PERIOD_6_START)) return 6;         // 5限（17:00-18:54）
        if (time.isBefore(PERIOD_7_START)) return 7;         // 6限（18:55-20:44）
        if (time.isBefore(PERIOD_NIGHT_START)) return 8;     // 7限（20:45-21:34）
        return 9;                                            // 夜（21:35-23:59）
    }

    // 時限の開始時刻を取得
    public static LocalTime getPeriodStartTime(int period) {
        return switch (period) {
            case 0 -> PERIOD_MORNING_START;  // 朝
            case 1 -> PERIOD_1_START;        // 1限
            case 2 -> PERIOD_2_START;        // 2限
            case 3 -> PERIOD_LUNCH_START;    // 昼
            case 4 -> PERIOD_3_START;        // 3限
            case 5 -> PERIOD_4_START;        // 4限
            case 6 -> PERIOD_5_START;        // 5限
            case 7 -> PERIOD_6_START;        // 6限
            case 8 -> PERIOD_7_START;        // 7限
            case 9 -> PERIOD_NIGHT_START;    // 夜
            default -> PERIOD_MORNING_START;
        };
    }

    // 曜日を日本語に変換
    public static String convertDayOfWeekToJapanese(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "月";
            case TUESDAY -> "火";
            case WEDNESDAY -> "水";
            case THURSDAY -> "木";
            case FRIDAY -> "金";
            case SATURDAY -> "土";
            case SUNDAY -> "日";
        };
    }

    // 時限の終了時刻を取得
    public static LocalTime getPeriodEndTime(int period) {
        return switch (period) {
            case 0 -> PERIOD_1_START.minusMinutes(1);     // 朝 -> 8:49
            case 1 -> PERIOD_1_END;                       // 1限 -> 10:39
            case 2 -> PERIOD_2_END;                       // 2限 -> 12:19
            case 3 -> PERIOD_LUNCH_END;                   // 昼休み -> 13:09
            case 4 -> PERIOD_3_END;                       // 3限 -> 15:04
            case 5 -> PERIOD_4_END;                       // 4限 -> 16:59
            case 6 -> PERIOD_5_END;                       // 5限 -> 18:54
            case 7 -> PERIOD_6_END;                       // 6限 -> 20:44
            case 8 -> PERIOD_7_END;                       // 7限 -> 21:34
            case 9 -> DAY_END;                            // 夜 -> 23:59
            default -> throw new IllegalArgumentException("無効な時限: " + period);
        };
    }

    // 時間の重複をチェック
    public static boolean isTimeOverlapping(
        LocalTime start1, LocalTime end1,
        LocalTime start2, LocalTime end2) {
        return !end1.isBefore(start2) && !start1.isAfter(end2);
    }

    // iCalendar形式の日時文字列を取得
    public static String formatDateTimeForICal(LocalDateTime dateTime) {
        return dateTime.format(ICAL_DATETIME_FORMATTER);
    }

    // iCalendar形式の日付文字列を取得
    public static String formatDateForICal(LocalDate date) {
        return date.format(ICAL_DATE_FORMATTER);
    }

    // 曜日をiCalendar形式に変換
    public static String getDayOfWeekForICal(String japaneseDay) {
        return switch (japaneseDay) {
            case "月" -> "MO";
            case "火" -> "TU";
            case "水" -> "WE";
            case "木" -> "TH";
            case "金" -> "FR";
            case "土" -> "SA";
            case "日" -> "SU";
            default -> "MO";
        };
    }

    // iCalendarの曜日表記を日本語に変換
    public static String extractDayOfWeekFromICal(String rrule) {
        if (rrule.contains("BYDAY=MO")) return "月";
        if (rrule.contains("BYDAY=TU")) return "火";
        if (rrule.contains("BYDAY=WE")) return "水";
        if (rrule.contains("BYDAY=TH")) return "木";
        if (rrule.contains("BYDAY=FR")) return "金";
        if (rrule.contains("BYDAY=SA")) return "土";
        if (rrule.contains("BYDAY=SU")) return "日";
        return "月"; // デフォルト値
    }

    // iCalendarの繰り返しパターンを日本語に変換
    public static String convertFrequencyFromICal(String rrule) {
        if (rrule.contains("FREQ=DAILY")) return "毎日";
        if (rrule.contains("FREQ=WEEKLY")) return "毎週";
        if (rrule.contains("FREQ=MONTHLY")) return "毎月";
        return "";
    }

    // 日本語の繰り返しパターンをiCalendar形式に変換
    public static String convertFrequencyToICal(String repeatPattern) {
        return switch (repeatPattern) {
            case "毎日" -> "DAILY";
            case "毎週" -> "WEEKLY";
            case "毎月" -> "MONTHLY";
            default -> "DAILY";
        };
    }
}
