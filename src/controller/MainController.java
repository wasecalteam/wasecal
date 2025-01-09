package controller;

// インポート
import javax.swing.*;
import java.io.*;
import java.util.*;
import model.subject.Subject;
import model.event.Event;
import view.MainFrame;
import util.FileUtil;

// メインコントローラークラス
public class MainController {
    private List<Subject> subjects; // 科目リスト
    private List<Event> events; // イベントリスト
    private MainFrame mainFrame; // メインフレーム

    // ファイルパスの定数を追加
    private static final String DATA_DIR = "data";
    private static final String SUBJECTS_FILE = DATA_DIR + "/subjects.csv";
    private static final String EVENTS_FILE = DATA_DIR + "/events.csv";
    private static final String ICAL_FILE = DATA_DIR + "/ical.ics";

    // コンストラクタ
    // データの読み込みとGUIの初期化
    public MainController() {
        this.subjects = new ArrayList<>();
        this.events = new ArrayList<>();
        initializeGUI();
        loadData();
    }

    // GUIの初期化
    private void initializeGUI() {
        mainFrame = new MainFrame(this);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null); // メインフレームを中央に配置
        mainFrame.setVisible(true);
    }

    // データの読み込み
    private void loadData() {
        boolean loadedFromCsv = false;
        // まずCSVファイルからの読み込みを試みる
        try {
            File subjectsFile = new File(SUBJECTS_FILE);
            File eventsFile = new File(EVENTS_FILE);

            if (subjectsFile.exists()) {
                subjects = FileUtil.loadSubjects(subjectsFile);
                loadedFromCsv = true;
            }
            if (eventsFile.exists()) {
                events = FileUtil.loadEvents(eventsFile);
                loadedFromCsv = true;
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame,
                "CSVファイルの読み込みに失敗しました: " + e.getMessage(),
                "エラー",
                JOptionPane.ERROR_MESSAGE);
        }

        // CSVファイルからの読み込みが失敗した場合、iCalendarファイルからの読み込みを試みる
        if (!loadedFromCsv) {
            try {
                File icalFile = new File(ICAL_FILE);
                if (icalFile.exists()) {
                    subjects = new ArrayList<>();
                    events = new ArrayList<>();
                    FileUtil.loadFromICalendar(icalFile, subjects, events);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainFrame,
                    "iCalendarファイルの読み込みに失敗しました: " + e.getMessage(),
                    "エラー",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        updateViews();
    }

    // データの保存
    public void saveData() {
        try {
            new File(DATA_DIR).mkdirs(); // データディレクトリが存在しない場合は作成
            FileUtil.saveSubjects(new File(SUBJECTS_FILE), subjects);
            FileUtil.saveEvents(new File(EVENTS_FILE), events);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "データの保存に失敗しました: " + e.getMessage(), "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ビューの更新
    public void updateViews() {
        mainFrame.updateCalendar(events, subjects);
        mainFrame.updateSubjectList(subjects);
        mainFrame.updateEventList(events);
    }

    // 科目の追加
    public void addSubject(Subject subject) {
        subjects.add(subject);
        updateViews();
    }

    // 科目の削除
    public void removeSubject(Subject subject) {
        subjects.remove(subject);
        updateViews();
    }

    // イベントの追加
    public void addEvent(Event event) {
        events.add(event);
        updateViews();
    }

    // イベントの削除
    public void removeEvent(Event event) {
        events.remove(event);
        updateViews();
    }

    // 科目リストの取得
    public List<Subject> getSubjects() {
        return new ArrayList<>(subjects);
    }

    // イベントリストの取得
    public List<Event> getEvents() {
        return new ArrayList<>(events);
    }

    // 科目の衝突チェック
    public boolean isSubjectConflict(Subject newSubject) {
        return subjects.stream().anyMatch(existingSubject ->
            existingSubject.getDayOfWeek().equals(newSubject.getDayOfWeek()) &&
            existingSubject.getPeriod() == newSubject.getPeriod()
        );
    }

    public void saveAsICalendar() {
        try {
            new File(DATA_DIR).mkdirs(); // データディレクトリが存在しない場合は作成
            FileUtil.saveAsICalendar(new File(ICAL_FILE), subjects, events);
            JOptionPane.showMessageDialog(mainFrame,
                "iCalendarファイルを保存しました。",
                "保存完了",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame,
                "iCalendarファイルの保存に失敗しました: " + e.getMessage(),
                "エラー",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
