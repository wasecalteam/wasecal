package model.subject;
// インポート
import java.io.*;
import java.util.*;
import util.FileUtil;

public class Subject {
    // 基本情報フィールド
    // 科目名, 曜日, 時限, 担当教員
    private String subjectName;
    private String dayOfWeek;
    private int period;
    private String instructor;

    // 追加情報フィールド
    // 場所, 備考
    private String location;
    private String description;

    // 必須項目のみのコンストラクタ
    public Subject(String subjectName, String dayOfWeek, int period, String instructor) {
        this.subjectName = subjectName;
        this.dayOfWeek = dayOfWeek;
        this.period = period;
        this.instructor = instructor;
        this.location = "";
        this.description = "";
    }

    // 全フィールドのコンストラクタ
    public Subject(String subjectName, String dayOfWeek, int period, String instructor, String location, String description) {
        this.subjectName = subjectName;
        this.dayOfWeek = dayOfWeek;
        this.period = period;
        this.instructor = instructor;
        this.location = location;
        this.description = description;
    }

    // Getter/Setterメソッド
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public int getPeriod() { return period; }
    public void setPeriod(int period) { this.period = period; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // ファイル入出力メソッド
    public static List<Subject> loadFromFile(File file) throws IOException {
        return FileUtil.loadSubjects(file);
    }

    public static void saveToFile(File file, List<Subject> subjects) throws IOException {
        FileUtil.saveSubjects(file, subjects);
    }
}