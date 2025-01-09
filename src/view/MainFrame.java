package view;
// インポート
import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.event.Event;
import model.subject.Subject;
import view.calendar.CalendarPanel;
import view.subject.SubjectPanel;
import view.event.EventPanel;
import view.menu.MenuBar;
import controller.MainController;
import controller.subject.SubjectController;
import controller.event.EventController;

// メインフレーム
// JFrameを継承
public class MainFrame extends JFrame {
    private final MainController controller;
    private CalendarPanel calendarPanel;
    private SubjectPanel subjectPanel;
    private EventPanel eventPanel;
    private MenuBar menuBar;

    public MainFrame(MainController controller) {
        this.controller = controller;
        setTitle("WaseCal");
        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        // コンポーネントの初期化
        calendarPanel = new CalendarPanel();
        subjectPanel = new SubjectPanel();
        eventPanel = new EventPanel();
        menuBar = new MenuBar(controller);

        // Controllerの初期化
        new SubjectController(controller, subjectPanel);
        new EventController(controller, eventPanel);

        // MenuBarの設定
        setJMenuBar(menuBar);
    }

    private void layoutComponents() {
        // レイアウトの設定
        setLayout(new BorderLayout());

        // メインパネル（カレンダー）を中央に配置
        add(calendarPanel, BorderLayout.CENTER);

        // 右側のパネル（科目とイベントのリスト）
        JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        rightPanel.add(new JScrollPane(subjectPanel));
        rightPanel.add(new JScrollPane(eventPanel));

        // 右パネルの幅を設定
        rightPanel.setPreferredSize(new Dimension(300, getHeight()));
        add(rightPanel, BorderLayout.EAST);
    }

    // 表示更新メソッド
    public void updateCalendar(List<Event> events, List<Subject> subjects) {
        calendarPanel.updateDisplay(events, subjects);
    }

    public void updateSubjectList(List<Subject> subjects) {
        subjectPanel.updateSubjectList(subjects);
    }

    public void updateEventList(List<Event> events) {
        eventPanel.updateEventList(events);
    }
}
