package view.subject;

// インポート
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import model.subject.Subject;

// 科目パネルクラス
// JPanelを継承
public class SubjectPanel extends JPanel {
    private JList<Subject> subjectList; // 科目リスト
    private DefaultListModel<Subject> listModel; // リストモデル
    private JButton addButton; // 追加ボタン
    private JButton editButton; // 編集ボタン
    private JButton deleteButton; // 削除ボタン

    // コンストラクタ
    public SubjectPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
    }

    // コンポーネントの初期化
    private void initializeComponents() {
        // リストモデルとリストの初期化
        listModel = new DefaultListModel<>();
        subjectList = new JList<>(listModel); // リストモデルを使用して科目リストを初期化
        subjectList.setCellRenderer(new SubjectListCellRenderer());
        subjectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ボタンパネルの作成
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("追加");
        editButton = new JButton("編集");
        deleteButton = new JButton("削除");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // 選択状態に応じてボタンの有効/無効を切り替え
        subjectList.addListSelectionListener(e -> {
            boolean isSelected = !subjectList.isSelectionEmpty();
            editButton.setEnabled(isSelected);
            deleteButton.setEnabled(isSelected);
        });

        // パネルにコンポーネントを追加
        add(new JLabel("科目一覧"), BorderLayout.NORTH);
        add(new JScrollPane(subjectList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // リスナー設定メソッド
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

    // 科目リストの更新
    public void updateSubjectList(List<Subject> subjects) {
        listModel.clear();
        subjects.forEach(listModel::addElement);
    }

    // 選択された科目の取得
    public Subject getSelectedSubject() {
        return subjectList.getSelectedValue();
    }

    // 科目追加ダイアログの表示
    public Subject showAddSubjectDialog() {
        SubjectDialog dialog = new SubjectDialog(SwingUtilities.getWindowAncestor(this));
        return dialog.showDialog(null);
    }

    // 科目編集ダイアログの表示
    public Subject showEditSubjectDialog(Subject subject) {
        SubjectDialog dialog = new SubjectDialog(SwingUtilities.getWindowAncestor(this));
        return dialog.showDialog(subject);
    }

    // リストセルレンダラー
    // DefaultListCellRendererを継承
    private static class SubjectListCellRenderer extends DefaultListCellRenderer {
        @Override
        // getListCellRendererComponent
        // list: レンダリング対象のJList
        // value: 値
        // index: 位置
        // isSelected: 選択されているかどうか
        // cellHasFocus: フォーカスされているかどうか
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Subject subject) { // 型チェック
                setText(String.format("%s (%s%d限) - %s",
                    subject.getSubjectName(),
                    subject.getDayOfWeek(),
                    subject.getPeriod(),
                    subject.getInstructor()));
            }
            return this;
        }
    }
}
