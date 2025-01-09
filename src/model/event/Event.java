package model.event;

// インポート
import java.io.*;
import java.util.*;
import java.time.*;
import util.FileUtil;
import util.DateTimeUtil;

public class Event {
    // 基本情報フィールド
    // イベント名, 開始日時, 終了日時
    private String eventName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    // 追加情報フィールド
    // 備考, 場所, 繰り返しフラグ, 繰り返しパターン
    private String description;
    private String location;
    private boolean isRepeating;
    private String repeatPattern; // "DAILY", "WEEKLY", "MONTHLY"

    // 必須項目のみのコンストラクタ
    public Event(String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
            this.eventName = eventName;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.description = "";
            this.location = "";
            this.isRepeating = false;
            this.repeatPattern = "";
    }

    // 全フィールドのコンストラクタ
    public Event(String eventName, LocalDateTime startDateTime, LocalDateTime endDateTime, String description, String location, boolean isRepeating, String repeatPattern) {
            this.eventName = eventName;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.description = description;
            this.location = location;
            this.isRepeating = isRepeating;
            this.repeatPattern = repeatPattern;
    }

    // Getter/Setter
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isRepeating() { return isRepeating; }
    public void setRepeating(boolean repeating) { isRepeating = repeating; }

    public String getRepeatPattern() { return repeatPattern; }
    public void setRepeatPattern(String repeatPattern) { this.repeatPattern = repeatPattern; }

    // 予定の時限を取得するメソッド
    private int getPeriodFromTime(LocalTime time) { return DateTimeUtil.getPeriodFromTime(time); }

    // 予定の重複チェックメソッド
    public boolean conflictsWith(Event other) {
        // 繰り返しの場合は、checkRepeatingConflictメソッドで処理
        if (this.isRepeating || other.isRepeating) {
            return checkRepeatingConflict(other);
        }

        // 繰り返しでない場合
        LocalTime thisStart = this.startDateTime.toLocalTime();
        LocalTime thisEnd = this.endDateTime.toLocalTime();
        LocalTime otherStart = other.startDateTime.toLocalTime();
        LocalTime otherEnd = other.endDateTime.toLocalTime();

        if (this.startDateTime.toLocalDate().equals(other.startDateTime.toLocalDate())) {
            int thisPeriodStart = getPeriodFromTime(thisStart);
            int thisPeriodEnd = getPeriodFromTime(thisEnd);
            int otherPeriodStart = getPeriodFromTime(otherStart);
            int otherPeriodEnd = getPeriodFromTime(otherEnd);

            return !(thisPeriodEnd < otherPeriodStart || thisPeriodStart > otherPeriodEnd);
        }
        return false;
    }

    // 繰り返し予定の重複チェックメソッド
    //　今後1年間の予定をチェック
    private boolean checkRepeatingConflict(Event other) {
        LocalDateTime checkStart = LocalDateTime.now(); // 開始日時
        LocalDateTime checkEnd = checkStart.plusYears(1); // 終了日時

        List<LocalDateTime> thisOccurrences = getOccurrences(checkStart, checkEnd);
        List<LocalDateTime> otherOccurrences = other.getOccurrences(checkStart, checkEnd);

        // 予定の発生日時リストを比較
        // thisOccurrencesのLocalDateTimerをthisTimeに、otherOccurrencesのLocalDateTimeをotherTimeに代入
        for (LocalDateTime thisTime : thisOccurrences) {
            for (LocalDateTime otherTime : otherOccurrences) {
                if (thisTime.toLocalDate().equals(otherTime.toLocalDate())) {
                    int thisPeriod = getPeriodFromTime(thisTime.toLocalTime());
                    int otherPeriod = getPeriodFromTime(otherTime.toLocalTime());
                    if (thisPeriod == otherPeriod) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 予定の発生日時リスト取得メソッド
    public List<LocalDateTime> getOccurrences(LocalDateTime start, LocalDateTime end) {
        List<LocalDateTime> occurrences = new ArrayList<>();
        LocalDateTime current = this.startDateTime;

        // 繰り返しでない場合は、開始日時のみを返す
        if (!isRepeating) {
            if (!current.isBefore(start) && !current.isAfter(end)) {
                occurrences.add(current);
            }
            return occurrences;
        }

        // 繰り返しの場合は、期間内の全ての発生を追加
        while (!current.isAfter(end)) {
            if (!current.isBefore(start)) {
                occurrences.add(current);
            }

            // 繰り返しパターンに基づいて次の発生日時を計算
            switch (repeatPattern) {
                case "毎日":
                    current = current.plusDays(1);
                    break;
                case "毎週":
                    current = current.plusWeeks(1);
                    break;
                case "毎月":
                    current = current.plusMonths(1);
                    break;
                default:
                    return occurrences;
            }
        }
        return occurrences;
    }

    // ファイル入出力メソッド
    public static List<Event> loadFromFile(File file) throws IOException {
            return FileUtil.loadEvents(file);
    }

    public static void saveToFile(File file, List<Event> events) throws IOException {
            FileUtil.saveEvents(file, events);
    }
}
