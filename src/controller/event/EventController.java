package controller.event;

// インポート
import javax.swing.*;
import controller.MainController;
import model.event.Event;
import model.subject.Subject;
import view.event.EventPanel;
import java.time.*;
import util.DateTimeUtil;

// イベントコントローラクラス
public class EventController {
    private final MainController mainController;
    private final EventPanel eventPanel;

    public EventController(MainController mainController, EventPanel eventPanel) {
        this.mainController = mainController;
        this.eventPanel = eventPanel;
        initializeListeners();
    }

    // リスナーの初期化
    private void initializeListeners() {
        eventPanel.setAddButtonListener(e -> handleAddEvent());
        eventPanel.setEditButtonListener(e -> handleEditEvent());
        eventPanel.setDeleteButtonListener(e -> handleDeleteEvent());
    }

    private void handleAddEvent() {
        Event newEvent = eventPanel.showAddEventDialog();
        if (newEvent != null) {
            // 重複チェック
            if (checkEventConflicts(newEvent)) {
                int result = JOptionPane.showConfirmDialog(
                    eventPanel,
                    "予定が重複していますが、追加しますか？",
                    "予定の重複",
                    JOptionPane.YES_NO_OPTION
                );
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            mainController.addEvent(newEvent);
        }
    }

    // イベントの編集
    private void handleEditEvent() {
        Event selectedEvent = eventPanel.getSelectedEvent();
        if (selectedEvent != null) {
            Event editedEvent = eventPanel.showEditEventDialog(selectedEvent);
            if (editedEvent != null) {
                mainController.removeEvent(selectedEvent);
                mainController.addEvent(editedEvent);
            }
        }
    }

    // イベントの削除
    private void handleDeleteEvent() {
        Event selectedEvent = eventPanel.getSelectedEvent();
        if (selectedEvent != null) {
            int result = JOptionPane.showConfirmDialog(
                eventPanel,
                "選択された予定を削除しますか？",
                "予定の削除",
                JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                mainController.removeEvent(selectedEvent);
            }
        }
    }

    private boolean checkEventConflicts(Event newEvent) {
        // 既存の予定との重複チェック
        boolean eventConflict = mainController.getEvents().stream()
            .anyMatch(existingEvent -> newEvent.conflictsWith(existingEvent));

        // 科目との重複チェック
        boolean subjectConflict = mainController.getSubjects().stream()
            .anyMatch(subject -> isEventConflictWithSubject(newEvent, subject));

        return eventConflict || subjectConflict;
    }

    private boolean isEventConflictWithSubject(Event event, Subject subject) {
        // イベントの日付から曜日を取得
        DayOfWeek eventDayOfWeek = event.getStartDateTime().getDayOfWeek();
        String eventDayStr = DateTimeUtil.convertDayOfWeekToJapanese(eventDayOfWeek);

        // 曜日が異なる場合は重複なし
        if (!subject.getDayOfWeek().equals(eventDayStr)) {
            return false;
        }

        // イベントの時間を取得
        LocalTime eventStartTime = event.getStartDateTime().toLocalTime();
        LocalTime eventEndTime = event.getEndDateTime().toLocalTime();

        // 科目の時限から時間を取得
        int subjectPeriod = subject.getPeriod();

        // 科目の実際の時限（DateTimeUtilの時限定義に合わせる）
        int actualPeriod = switch (subjectPeriod) {
            case 1 -> 1;  // 1限
            case 2 -> 2;  // 2限
            case 3 -> 4;  // 3限（昼休みをスキップ）
            case 4 -> 5;  // 4限
            case 5 -> 6;  // 5限
            case 6 -> 7;  // 6限
            case 7 -> 8;  // 7限
            default -> -1;
        };

        if (actualPeriod == -1) {
            return false;
        }

        LocalTime subjectStartTime = DateTimeUtil.getPeriodStartTime(actualPeriod);
        LocalTime subjectEndTime = DateTimeUtil.getPeriodEndTime(actualPeriod);

        // 時間の重複をチェック
        return DateTimeUtil.isTimeOverlapping(
            eventStartTime, eventEndTime,
            subjectStartTime, subjectEndTime
        );
    }
}
