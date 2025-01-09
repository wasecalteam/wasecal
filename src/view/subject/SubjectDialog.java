package view.subject;

// インポート
import javax.swing.*;
import java.awt.*;
import model.subject.Subject;

// 科目ダイアログクラス
// JDialogを継承
public class SubjectDialog extends JDialog {
    private Subject result = null;
    private JTextField nameField;
    private JComboBox<String> dayCombo;
    private JSpinner periodSpinner;
    private JTextField instructorField;

    private JTextField locationField;
    private JTextArea descriptionArea;

    // コンストラクタ
    public SubjectDialog(Window owner) {
        super(owner, "科目情報", ModalityType.APPLICATION_MODAL); // ダイアログのタイトルとモーダリティを設定
        initializeComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    // コンポーネントの初期化
    private void initializeComponents() {
        setLayout(new BorderLayout());

        // 入力パネル
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 科目名
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("科目名:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        inputPanel.add(nameField, gbc);

        // 曜日
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("曜日:"), gbc);
        gbc.gridx = 1;
        String[] days = {"月", "火", "水", "木", "金", "土", "日"};
        dayCombo = new JComboBox<>(days);
        inputPanel.add(dayCombo, gbc);

        // 時限
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("時限:"), gbc);
        gbc.gridx = 1;
        SpinnerNumberModel periodModel = new SpinnerNumberModel(1, 1, 7, 1);
        periodSpinner = new JSpinner(periodModel);
        inputPanel.add(periodSpinner, gbc);

        // 担当教員
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("担当教員:"), gbc);
        gbc.gridx = 1;
        instructorField = new JTextField(20);
        inputPanel.add(instructorField, gbc);

        // 教室
        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(new JLabel("教室:"), gbc);
        gbc.gridx = 1;
        locationField = new JTextField(20);
        inputPanel.add(locationField, gbc);

        // 説明
        gbc.gridx = 0; gbc.gridy = 5;
        inputPanel.add(new JLabel("説明:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        inputPanel.add(new JScrollPane(descriptionArea), gbc);

        add(inputPanel, BorderLayout.CENTER);

        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("キャンセル");

        okButton.addActionListener(e -> {
            if (validateInput()) {
                result = createSubject();
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // 入力チェック
    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "科目名を入力してください。",
                "入力エラー",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (instructorField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "担当教員を入力してください。",
                "入力エラー",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // 科目の作成
    private Subject createSubject() {
        return new Subject(
            nameField.getText().trim(),
            (String) dayCombo.getSelectedItem(),
            (Integer) periodSpinner.getValue(),
            instructorField.getText().trim(),
            locationField.getText().trim(),
            descriptionArea.getText().trim()
        );
    }

    // ダイアログの表示
    public Subject showDialog(Subject subject) {
        if (subject != null) { // 編集の場合
            // 既存の科目情報を設定
            nameField.setText(subject.getSubjectName());
            dayCombo.setSelectedItem(subject.getDayOfWeek());
            periodSpinner.setValue(subject.getPeriod());
            instructorField.setText(subject.getInstructor());
            locationField.setText(subject.getLocation());
            descriptionArea.setText(subject.getDescription());
        } else { // 新規の場合
            // フィールドをクリア
            nameField.setText("");
            dayCombo.setSelectedIndex(0);
            periodSpinner.setValue(1);
            instructorField.setText("");
            locationField.setText("");
            descriptionArea.setText("");
        }
        result = null;
        setVisible(true);
        return result;
    }
}
