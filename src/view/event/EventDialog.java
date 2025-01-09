package view.event;

// インポート
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.time.*;
import model.event.Event;

// イベントダイアログクラス
// JDialogを継承
public class EventDialog extends JDialog {
    private Event result = null;
    private JTextField nameField;
    private JSpinner dateSpinner;
    private JSpinner startTimeSpinner;
    private JSpinner endTimeSpinner;
    private JTextField locationField;
    private JTextArea descriptionArea;
    private JCheckBox repeatCheckBox;
    private JComboBox<String> repeatPatternCombo;

    // コンストラクタ
    public EventDialog(Window owner) {
        super(owner, "予定の詳細", ModalityType.APPLICATION_MODAL);
        initializeComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    // コンポーネントの初期化
    private void initializeComponents() {
        setLayout(new BorderLayout());

        // 入力フィールドパネル
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 予定名
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("予定名:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        inputPanel.add(nameField, gbc);

        // 日付
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("日付:"), gbc);
        gbc.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy/MM/dd");
        dateSpinner.setEditor(dateEditor);
        inputPanel.add(dateSpinner, gbc);

        // 開始時刻
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("開始時刻:"), gbc);
        gbc.gridx = 1;
        initializeTimeSpinners();
        inputPanel.add(startTimeSpinner, gbc);

        // 終了時刻
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("終了時刻:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(endTimeSpinner, gbc);

        // 場所
        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(new JLabel("場所:"), gbc);
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

        // 繰り返し設定
        gbc.gridx = 0; gbc.gridy = 6;
        inputPanel.add(new JLabel("繰り返し:"), gbc);
        gbc.gridx = 1;
        JPanel repeatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        repeatCheckBox = new JCheckBox("繰り返す");
        repeatPatternCombo = new JComboBox<>(new String[]{"毎日", "毎週", "毎月"});
        repeatPatternCombo.setEnabled(false);
        repeatCheckBox.addActionListener(e -> repeatPatternCombo.setEnabled(repeatCheckBox.isSelected()));
        repeatPanel.add(repeatCheckBox);
        repeatPanel.add(repeatPatternCombo);
        inputPanel.add(repeatPanel, gbc);

        add(inputPanel, BorderLayout.CENTER);

        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("キャンセル");

        okButton.addActionListener(e -> {
            if (validateInput()) {
                result = createEvent();
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // 時刻の初期化
    private void initializeTimeSpinners() {
        // 開始時刻と終了時刻のSpinnerを24時間形式で設定
        SpinnerDateModel startModel = new SpinnerDateModel();
        startTimeSpinner = new JSpinner(startModel);
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);

        SpinnerDateModel endModel = new SpinnerDateModel();
        endTimeSpinner = new JSpinner(endModel);
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);
    }

    // 入力チェック
    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "予定名を入力してください。", "入力エラー", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Date startTime = (Date) startTimeSpinner.getValue();
        Date endTime = (Date) endTimeSpinner.getValue();
        if (startTime.after(endTime)) {
            JOptionPane.showMessageDialog(this,
                "開始時刻は終了時刻より前にしてください。",
                "入力エラー",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    // イベントの作成
    private Event createEvent() {
        Date date = (Date) dateSpinner.getValue();
        Date startTime = (Date) startTimeSpinner.getValue();
        Date endTime = (Date) endTimeSpinner.getValue();

        LocalDateTime startDateTime = combineDateTime(date, startTime);
        LocalDateTime endDateTime = combineDateTime(date, endTime);

        return new Event(
            nameField.getText().trim(),
            startDateTime,
            endDateTime,
            descriptionArea.getText().trim(),
            locationField.getText().trim(),
            repeatCheckBox.isSelected(),
            repeatCheckBox.isSelected() ? (String) repeatPatternCombo.getSelectedItem() : ""
        );
    }

    // 日付と時刻
    private LocalDateTime combineDateTime(Date date, Date time) {
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);

        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTime(time);

        Calendar combined = Calendar.getInstance();
        combined.set(Calendar.YEAR, dateCalendar.get(Calendar.YEAR));
        combined.set(Calendar.MONTH, dateCalendar.get(Calendar.MONTH));
        combined.set(Calendar.DAY_OF_MONTH, dateCalendar.get(Calendar.DAY_OF_MONTH));
        combined.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
        combined.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
        combined.set(Calendar.SECOND, 0);
        combined.set(Calendar.MILLISECOND, 0);

        return combined.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    // ダイアログの表示
    public Event showDialog(Event event) {
        if (event != null) {
            // 既存の予定情報を設定
            nameField.setText(event.getEventName());
            dateSpinner.setValue(Date.from(event.getStartDateTime()
                .atZone(ZoneId.systemDefault()).toInstant()));
            startTimeSpinner.setValue(Date.from(event.getStartDateTime()
                .atZone(ZoneId.systemDefault()).toInstant()));
            endTimeSpinner.setValue(Date.from(event.getEndDateTime()
                .atZone(ZoneId.systemDefault()).toInstant()));
            locationField.setText(event.getLocation());
            descriptionArea.setText(event.getDescription());
            repeatCheckBox.setSelected(event.isRepeating());
            if (event.isRepeating()) {
                repeatPatternCombo.setSelectedItem(event.getRepeatPattern());
                repeatPatternCombo.setEnabled(true);
            }
        } else {
            // 現在時刻を基準に初期値を設定
            Date now = new Date();
            dateSpinner.setValue(now);
            startTimeSpinner.setValue(now);
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.HOUR_OF_DAY, 1);
            endTimeSpinner.setValue(cal.getTime());

            // その他のフィールドをクリア
            nameField.setText("");
            locationField.setText("");
            descriptionArea.setText("");
            repeatCheckBox.setSelected(false);
            repeatPatternCombo.setEnabled(false);
            repeatPatternCombo.setSelectedIndex(0);
        }

        result = null;
        setVisible(true);
        return result;
    }
}

