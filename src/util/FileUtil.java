package util;

// インポート
import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
import model.subject.Subject;
import model.event.Event;

// ファイルを読み込むためのユーティリティクラス
public class FileUtil {
    // 科目データを読み込むメソッド
    public static List<Subject> loadSubjects(File file) throws IOException {
        List<Subject> subjects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 空行をスキップ
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                subjects.add(new Subject(
                    parts[0],  // subjectName
                    parts[1],  // dayOfWeek
                    Integer.parseInt(parts[2]),  // period
                    parts[3],  // instructor
                    parts[4],  // location
                    parts[5]   // description
                ));
            }
        }
        return subjects;
    }

    // 科目データを保存するメソッド
    public static void saveSubjects(File file, List<Subject> subjects) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Subject subject : subjects) {
                writer.printf("%s,%s,%d,%s,%s,%s%n",
                    subject.getSubjectName(),
                    subject.getDayOfWeek(),
                    subject.getPeriod(),
                    subject.getInstructor(),
                    subject.getLocation(),
                    subject.getDescription()
                );
            }
        }
    }

    // イベントデータを読み込むメソッド
    public static List<Event> loadEvents(File file) throws IOException {
        List<Event> events = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            while ((line = reader.readLine()) != null) {
                // 空行をスキップ
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                events.add(new Event(
                    parts[0], // eventName
                    LocalDateTime.parse(parts[1], formatter), // startDateTime
                    LocalDateTime.parse(parts[2], formatter), // endDateTime
                    parts[3], // description
                    parts[4], // location
                    Boolean.parseBoolean(parts[5]), // isRepeating
                    parts[6]  // repeatPattern
                ));
            }
        }
        return events;
    }

    // イベントデータを保存するメソッド
    public static void saveEvents(File file, List<Event> events) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            for (Event event : events) {
                String repeatPattern = event.isRepeating() ? event.getRepeatPattern() : "なし";
                writer.printf("%s,%s,%s,%s,%s,%b,%s%n",
                    event.getEventName(),
                    event.getStartDateTime().format(formatter),
                    event.getEndDateTime().format(formatter),
                    event.getDescription(),
                    event.getLocation(),
                    event.isRepeating(),
                    repeatPattern
                );
            }
        }
    }

    // iCalendarファイルとして保存するメソッド
    public static void saveAsICalendar(File file, List<Subject> subjects, List<Event> events) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // デバッグ情報を出力
            System.out.println("=== iCalendar出力開始 ===");
            System.out.println("科目数: " + subjects.size());
            System.out.println("イベント数: " + events.size());

            // iCalendarヘッダー
            writer.println("BEGIN:VCALENDAR");
            writer.println("VERSION:2.0");
            writer.println("PRODID:-//WaseCal//JP");

            // 科目をイベントとして出力
            for (Subject subject : subjects) {
                System.out.println("科目を出力: " + subject.getSubjectName());
                writeSubjectAsICalEvent(writer, subject);
            }

            // イベントを出力
            for (Event event : events) {
                System.out.println("イベントを出力: " + event.getEventName());
                writeEventAsICal(writer, event);
            }

            writer.println("END:VCALENDAR");
            System.out.println("=== iCalendar出力完了 ===");
        }
    }

    private static void writeSubjectAsICalEvent(PrintWriter writer, Subject subject) {
        // 現在の日付を取得
        LocalDate now = LocalDate.now();

        // 開始日を設定（現在が1-3月の場合は前年の4月から、4-12月の場合は当年の4月から）
        LocalDate startDate = now.getMonthValue() <= 3
            ? now.minusYears(1).withMonth(4).withDayOfMonth(1)
            : now.withMonth(4).withDayOfMonth(1);

        // 終了日は開始日の翌年3月末
        LocalDate endDate = startDate.plusYears(1).withMonth(3).withDayOfMonth(31);

        // 科目の時限を取得
        int subjectPeriod = subject.getPeriod();

        // 科目の実際の時限（DateTimeUtilの時限定義に合わせる）
        int actualPeriod = switch (subjectPeriod) {
            case 1 -> 1;  // 1限
            case 2 -> 2;  // 2限
            case 3 -> 4;  // 3限
            case 4 -> 5;  // 4限
            case 5 -> 6;  // 5限
            case 6 -> 7;  // 6限
            case 7 -> 8;  // 7限
            default -> -1;
        };

        LocalTime startTime = DateTimeUtil.getPeriodStartTime(actualPeriod);
        LocalTime endTime = DateTimeUtil.getPeriodEndTime(actualPeriod);

        writer.println("BEGIN:VEVENT");
        writer.println("UID:" + UUID.randomUUID().toString());
        writer.println("SUMMARY:" + escapeICalText(subject.getSubjectName()));
        writer.println("DESCRIPTION:" + escapeICalText(subject.getDescription()));
        writer.println("LOCATION:" + escapeICalText(subject.getLocation()));
        writer.println("RRULE:FREQ=WEEKLY;UNTIL=" + DateTimeUtil.formatDateForICal(endDate) + ";BYDAY=" + DateTimeUtil.getDayOfWeekForICal(subject.getDayOfWeek()));
        writer.println("DTSTART:" + DateTimeUtil.formatDateTimeForICal(startDate.atTime(startTime)));
        writer.println("DTEND:" + DateTimeUtil.formatDateTimeForICal(startDate.atTime(endTime)));
        writer.println("END:VEVENT");
    }

    private static void writeEventAsICal(PrintWriter writer, Event event) {
        writer.println("BEGIN:VEVENT");
        writer.println("UID:" + UUID.randomUUID().toString());
        writer.println("SUMMARY:" + escapeICalText(event.getEventName()));
        writer.println("DESCRIPTION:" + escapeICalText(event.getDescription()));
        writer.println("LOCATION:" + escapeICalText(event.getLocation()));

        if (event.isRepeating()) {
            writer.println("RRULE:FREQ=" + DateTimeUtil.convertFrequencyToICal(event.getRepeatPattern()));
        }

        writer.println("DTSTART:" + DateTimeUtil.formatDateTimeForICal(event.getStartDateTime()));
        writer.println("DTEND:" + DateTimeUtil.formatDateTimeForICal(event.getEndDateTime()));
        writer.println("END:VEVENT");
    }

    private static String escapeICalText(String text) {
        if (text == null) return "";
        return text.replace(",", "\\,")
                  .replace(";", "\\;")
                  .replace("\\", "\\\\")
                  .replace("\n", "\\n")
                  .replace("\r", "");
    }

    // iCalendarファイルから科目とイベントを読み込むメソッド
    public static void loadFromICalendar(File file, List<Subject> subjects, List<Event> events) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Event currentEvent = null;
            String uid = "", summary = "", description = "", location = "";
            LocalDateTime startDateTime = null, endDateTime = null;
            String rrule = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.equals("BEGIN:VEVENT")) {
                    uid = ""; summary = ""; description = ""; location = "";
                    startDateTime = null; endDateTime = null; rrule = "";
                } else if (line.startsWith("UID:")) {
                    uid = line.substring(4);
                } else if (line.startsWith("SUMMARY:")) {
                    summary = line.substring(8).replace("\\,", ",");
                } else if (line.startsWith("DESCRIPTION:")) {
                    description = line.substring(12).replace("\\,", ",");
                } else if (line.startsWith("LOCATION:")) {
                    location = line.substring(9).replace("\\,", ",");
                } else if (line.startsWith("DTSTART:")) {
                    startDateTime = LocalDateTime.parse(
                        line.substring(8),
                        DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
                    );
                } else if (line.startsWith("DTEND:")) {
                    endDateTime = LocalDateTime.parse(
                        line.substring(6),
                        DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
                    );
                } else if (line.startsWith("RRULE:")) {
                    rrule = line.substring(6);
                } else if (line.equals("END:VEVENT")) {
                    if (startDateTime != null && endDateTime != null && !summary.isEmpty()) {
                        if (rrule.contains("BYDAY=")) {
                            // 科目として処理
                            String dayOfWeek = DateTimeUtil.extractDayOfWeekFromICal(rrule);
                            int actualPeriod = DateTimeUtil.getPeriodFromTime(startDateTime.toLocalTime());

                            // 実際の時限から科目の時限に変換
                            int subjectPeriod = switch (actualPeriod) {
                                case 1 -> 1;  // 1限
                                case 2 -> 2;  // 2限
                                case 4 -> 3;  // 3限
                                case 5 -> 4;  // 4限
                                case 6 -> 5;  // 5限
                                case 7 -> 6;  // 6限
                                case 8 -> 7;  // 7限
                                default -> -1;
                            };

                            subjects.add(new Subject(
                                summary,
                                dayOfWeek,
                                subjectPeriod, // 変換後の時限を使用
                                "", // instructor (空欄)
                                location,
                                description
                            ));
                        } else {
                            // イベントとして処理
                            String repeatPattern = "";
                            boolean isRepeating = false;
                            if (!rrule.isEmpty()) {
                                isRepeating = true;
                                repeatPattern = DateTimeUtil.convertFrequencyFromICal(rrule);
                            }
                            events.add(new Event(
                                summary,
                                startDateTime,
                                endDateTime,
                                description,
                                location,
                                isRepeating,
                                repeatPattern
                            ));
                        }
                    }
                }
            }
        }
    }
}