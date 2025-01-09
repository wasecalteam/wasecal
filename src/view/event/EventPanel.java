package view.event;

// インポート
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import model.event.Event;
import util.DateTimeUtil;

// イベントパネルクラス
// JPanelを継承
public class EventPanel extends JPanel {
    private JList<Event> eventList;
    private DefaultListModel<Event> listModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    // コンストラクタ
    public EventPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        // リストモデルとリストの初期化
        listModel = new DefaultListModel<>();
        eventList = new JList<>(listModel); // リストモデルを使用してイベントリストを初期化
        eventList.setCellRenderer(new EventListCellRenderer()); // セルレンダラーの設定
        eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 選択モードの設定

        // ボタンパネルの作成
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("追加");
        editButton = new JButton("編集");
        deleteButton = new JButton("削除");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // 選択状態に応じてボタンの有効/無効を切り替え
        eventList.addListSelectionListener(e -> {
            boolean isSelected = !eventList.isSelectionEmpty();
            editButton.setEnabled(isSelected);
            deleteButton.setEnabled(isSelected);
        });

        // パネルにコンポーネントを追加
        add(new JLabel("予定一覧"), BorderLayout.NORTH);
        add(new JScrollPane(eventList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // リスナーを設定
    // このようにすることで、イベント処理を外部から制御できる
    public void setAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void setEditButtonListener(ActionListener listener) {
        editButton.addActionListener(listener);
    }

    public void setDeleteButtonListener(ActionListener listener) {
        deleteButton.addActionListener(listener);
    }

    // 予定リストの更新
    public void updateEventList(List<Event> events) {
        listModel.clear();
        events.forEach(listModel::addElement);
    }

    // 選択された予定の取得
    public Event getSelectedEvent() {
        return eventList.getSelectedValue();
    }

    // 予定追加ダイアログの表示
    public Event showAddEventDialog() {
        EventDialog dialog = new EventDialog(SwingUtilities.getWindowAncestor(this));
        return dialog.showDialog(null);
    }

    // 予定編集ダイアログの表示
    public Event showEditEventDialog(Event event) {
        EventDialog dialog = new EventDialog(SwingUtilities.getWindowAncestor(this));
        return dialog.showDialog(event);
    }

    // リストセルレンダラー
    // DefaultListCellRendererを継承
    private static class EventListCellRenderer extends DefaultListCellRenderer {
        @Override
        // getListCellRendererComponent
        // list: レンダリング対象のJList
        // value: 値
        // index: 位置
        // isSelected: 選択されているかどうか
        // cellHasFocus: フォーカスされているかどうか
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof model.event.Event event) { // 型チェック
                setText(String.format("%s\n(%s - %s)",
                    event.getEventName(),
                    DateTimeUtil.formatDateTime(event.getStartDateTime()),
                    DateTimeUtil.formatDateTime(event.getEndDateTime())));
            }
            return this;
        }
    }
}
