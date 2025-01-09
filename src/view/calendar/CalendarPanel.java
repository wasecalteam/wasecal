package view.calendar;

// インポート
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import model.event.Event;
import model.subject.Subject;
import util.DateTimeUtil;

public class CalendarPanel extends JPanel {
    private JTable calendarTable;
    private DefaultTableModel tableModel;//JTableのデータを管理するためのモデル。この変数はテーブルに表示するデータを操作する。
    private LocalDate currentDate;//currentDate:現在の日付を保持するための変数,LocalDate:日付を扱うためのJavaのクラス
    private final String[] columnNames = {"時間", "月", "火", "水", "木", "金", "土", "日"};//テーブルの列名（曜日）を格納
    private final String[] timeSlots = {"朝", "1限", "2限", "昼", "3限", "4限", "5限", "6限", "7限", "夜"};//時間帯を格納
    private final int ROWS = 10; // 朝、1-7限、昼休み、夜
    private List<Event> currentEvents = new ArrayList<>();//現在表示されているイベントを格納するリスト
    private List<Subject> currentSubjects = new ArrayList<>();//現在の科目を格納するリスト

    public CalendarPanel() {//カレンダーや時間割を表示するパネルのコンストラクタ
        setLayout(new BorderLayout());
        currentDate = LocalDate.now();//現在の日付（今日の日付）を取得して、currentDateに格納
        initializeComponents();//コンポーネント（例えばテーブルやボタンなど）の初期化を行うメソッドを呼び出す
    }

    private void initializeComponents() {
        // テーブルモデルの初期化
        tableModel = new DefaultTableModel(columnNames, ROWS) { //DefaultTableModel:テーブルのデータを管理するモデル、columnNames:カラムの名前（曜日,ROWS:行数（時間帯の数）
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // テーブルの初期化
        calendarTable = new JTable(tableModel);
        calendarTable.setRowHeight(80);//各行の高さを80ピクセルに設定(セルを見やすくする)

        // 時限列（0列目）の幅を設定
        TableColumn timeColumn = calendarTable.getColumnModel().getColumn(0);
        timeColumn.setPreferredWidth(40);
        timeColumn.setMaxWidth(40);//幅を40ピクセルに固定

        // テーブル本体のセルレンダラーを設定
        //テーブルのセルがどのように表示されるか
        calendarTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JTextArea textArea = new JTextArea();
                textArea.setText(value != null ? value.toString() : "");
                textArea.setWrapStyleWord(true);//テキストがセルの幅を超えたら折り返して表示
                textArea.setLineWrap(true);

                textArea.setOpaque(true);//JTextAreaの背景を不透明に設定
                textArea.setFont(table.getFont());

                // 時限列の設定
                if (column == 0) {
                    textArea.setBackground(new Color(240, 240, 240));
                    textArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                    //背景色を薄いグレーにし、セルの周りに余白（ボーダー）を設定
                    //それ以外の列（曜日の列）には白背景と違う余白を設定
                } else {
                    textArea.setBackground(Color.WHITE);
                    textArea.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
                }
                return textArea;
            }
        });

        // 時限の設定
        for (int i = 0; i < ROWS; i++) {
            tableModel.setValueAt(timeSlots[i], i, 0);
            //timeSlots配列:朝から夜までの時間帯（朝、1限、2限、昼休みなど）が格納
            //forループで各時間帯（timeSlots[i]）をテーブルの0列目（時限の列）にセット
        }

        // スクロールペインに追加
        JScrollPane scrollPane = new JScrollPane(calendarTable);//JScrollPane:スクロール可能なコンポーネント
        add(scrollPane, BorderLayout.CENTER);//calendarTableをJScrollPaneに包んで、パネルの中央に配置

        // 操作パネルの追加
        add(createControlPanel(), BorderLayout.NORTH);
    }

    private String formatWeekRange(LocalDate date) {
        LocalDate weekStart = date.with(DayOfWeek.MONDAY);// 与えられた日付からその週の月曜日を取得
        LocalDate weekEnd = weekStart.plusDays(6);// その週の月曜日から6日後（つまり日曜日）を計算
        //この範囲を "yyyy/MM/dd" の形式でフォーマットし、String.format() を使って文字列に整形する
        return String.format("%s - %s",
            weekStart.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
            weekEnd.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
            //例:date が2025年1月6日（月曜日）の場合、formatWeekRangeは「2025/01/06 - 2025/01/12」のような文字列を返す。
    }
    //カレンダーの操作パネルを作成するためのメソッド
    //前の週、次の週に移動するためのボタンと、その週の日付範囲を表示するラベルを含む
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        //FlowLayout:コンポーネント（ボタンやラベル）を横並びに配置するレイアウト
        JButton prevButton = new JButton("◀");// 前の週に移動するボタン
        JButton nextButton = new JButton("▶");// 次の週に移動するボタン
        JLabel dateLabel = new JLabel(formatWeekRange(currentDate));//現在の日付を基にした週の範囲を表示するラベル
        //prevButtonを押すと、currentDateが1週間前に移動し、dateLabelに新しい週の日付範囲が表示される
        prevButton.addActionListener(e -> {
            System.out.println("\n=== 前の週へ移動 ===");
            System.out.println("移動前: " + currentDate);
            currentDate = currentDate.minusWeeks(1);//1週間前の日付に移動
            System.out.println("移動後: " + currentDate);
            dateLabel.setText(formatWeekRange(currentDate));//新しい週の日付範囲を表示
            updateDisplay(currentEvents, currentSubjects);//表示を更新（イベントや科目のデータを反映）
        });
        //nextButtonを押すと、currentDateが1週間後に移動し、同じようにdateLabelが更新される
        nextButton.addActionListener(e -> {
            System.out.println("\n=== 次の週へ移動 ===");
            System.out.println("移動前: " + currentDate);
            currentDate = currentDate.plusWeeks(1);// 1週間後の日付に移動
            System.out.println("移動後: " + currentDate);
            dateLabel.setText(formatWeekRange(currentDate));// 新しい週の日付範囲を表示
            updateDisplay(currentEvents, currentSubjects);// 表示を更新
        });
        //controlPanelに、前の週ボタン、週の範囲ラベル、次の週ボタンを追加
        controlPanel.add(prevButton);
        controlPanel.add(dateLabel);
        controlPanel.add(nextButton);

        return controlPanel;//完成したパネル（controlPanel）を返す
    }

    //テーブルを更新するための一連の流れ
    public void updateDisplay(List<Event> events, List<Subject> subjects) {
        // デバッグ情報を追加
        //最初に現在の日付を表示して、どの時点で更新が行われたか確認できるようにする
        System.out.println("\n=== デバッグ: 表示更新 ===");
        System.out.println("現在の日付: " + currentDate);

        // 現在のイベントと科目を更新
        this.currentEvents = new ArrayList<>(events);
        this.currentSubjects = new ArrayList<>(subjects);
        clearTable();
        displaySubjects();
        events.forEach(this::displayEvent);
    }
    //テーブルを空にするためのメソッド
    //すべてのセルの内容を空にし、次に新しいデータを表示できるように準備
    private void clearTable() {
        System.out.println("テーブルをクリア");
        for (int row = 0; row < ROWS; row++) {
            for (int col = 1; col < columnNames.length; col++) {
                tableModel.setValueAt("", row, col);
            }
        }
    }
    //科目をテーブルに表示するためのメソッド
    private void displaySubjects() {
        System.out.println("科目を表示");
        //各科目を1つずつ取り出し、その科目の「曜日」と「時限」に応じた位置に表示
        for (Subject subject : currentSubjects) {
            // 時限を0から始まる配列のインデックスに変換
            //科目の「時限」（例えば、1限、2限など）を、テーブルの行番号に変換する
            int row = switch (subject.getPeriod()) {
                case 1 -> 1;  // 1限は配列の1番目
                case 2 -> 2;  // 2限は配列の2番目
                case 3 -> 4;  // 3限は配列の4番目（昼休みがあるため）
                case 4 -> 5;  // 4限は配列の5番目
                case 5 -> 6;  // 5限は配列の6番目
                case 6 -> 7;  // 6限は配列の7番目
                case 7 -> 8;  // 7限は配列の8番目
                default -> -1;
            };
            //科目の曜日（例えば「月」や「火」）をテーブルの列番号に変換するためのメソッド
            int col = getDayColumn(subject.getDayOfWeek());
            if (row >= 0 && row < ROWS && col > 0) {//科目を配置する位置が有効かどうかを確認
                System.out.println("科目を配置: " + subject.getSubjectName() + " at (" + row + "," + col + ")");
                //実際に科目をテーブルに配置する処理
                //テーブルの指定された行（row）と列（col）に科目名（subject.getSubjectName()）をセット
                tableModel.setValueAt(subject.getSubjectName(), row, col);
            }
        }
    }
    //曜日（dayOfWeek）を元に、対応するテーブルの列番号を返すメソッド
    private int getDayColumn(String dayOfWeek) {
        return switch (dayOfWeek) {
            case "月" -> 1;
            case "火" -> 2;
            case "水" -> 3;
            case "木" -> 4;
            case "金" -> 5;
            case "土" -> 6;
            case "日" -> 7;
            default -> -1;
        };
    }
/*     //指定されたイベントが現在の週に含まれているかをチェックするためのメソッド
    private boolean isEventInCurrentWeek(Event event) {
        LocalDate eventDate = event.getStartDateTime().toLocalDate();
        //event.getStartDateTime() でイベントの開始日時を取得
        //その日付が今週（現在の日付から始まる週）に含まれるかを判定
        LocalDate weekStart = currentDate.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = currentDate.with(DayOfWeek.SUNDAY);
        return !eventDate.isBefore(weekStart) && !eventDate.isAfter(weekEnd);
        //現在の日付 (currentDate) から週の開始日（Monday）と終了日（Sunday）を計算
        //その範囲内であれば true を返し、そうでなければ false を返す
    } */
    //現在の週の開始日時と終了日時を求める
    private LocalDateTime getCurrentWeekStart() {
        return currentDate.with(DayOfWeek.MONDAY).atStartOfDay();
    }

    private LocalDateTime getCurrentWeekEnd() {
        return getCurrentWeekStart()
            .plusDays(6)
            .withHour(23)
            .withMinute(59)
            .withSecond(59);
    }

    private void displayEvent(Event event) {
        // 現在表示中の週の開始日と終了日を取得
        LocalDateTime weekStart = getCurrentWeekStart();
        LocalDateTime weekEnd = getCurrentWeekEnd();

        // イベントの時限を取得
/*         int period = DateTimeUtil.getPeriodFromTime(event.getStartDateTime().toLocalTime());
 */
        // イベントの発生日時を取得
        List<LocalDateTime> occurrences = getEventOccurrencesInWeek(event, weekStart, weekEnd);
        //getEventOccurrencesInWeek()メソッドを呼び出してイベントが発生する日時のリスト（その週内の発生日時）を取得

        // 各発生日時について表示処理
        for (LocalDateTime startTime : occurrences) {
            displaySingleOccurrence(event, startTime);
        }
    }

    // 新しいメソッド：週内のイベント発生日時を取得
    private List<LocalDateTime> getEventOccurrencesInWeek(Event event, LocalDateTime weekStart, LocalDateTime weekEnd) {
        System.out.println("\n=== デバッグ: イベント「" + event.getEventName() + "」の発生日時計算 ===");
        System.out.println("週の開始: " + weekStart);
        System.out.println("週の終了: " + weekEnd);
        System.out.println("イベント開始時刻: " + event.getStartDateTime());
        System.out.println("繰り返しパターン: " + (event.isRepeating() ? event.getRepeatPattern() : "なし"));

        if (!event.isRepeating()) {
            // 非繰り返しイベントの場合
            boolean isInWeek = event.getStartDateTime().isBefore(weekEnd) && event.getStartDateTime().isAfter(weekStart);
            System.out.println("非繰り返しイベント - 週内判定: " + isInWeek);
            return isInWeek ?
                    Collections.singletonList(event.getStartDateTime()) :
                    Collections.emptyList();
        }

        // 繰り返しイベントの場合
        LocalDateTime eventStart = event.getStartDateTime();

        // イベントの開始日時が週末より後の場合はスキップ
        if (eventStart.isAfter(weekEnd)) {
            System.out.println("イベント開始が週末より後のためスキップ");
            return Collections.emptyList();
        }

        // 開始日時を週の開始日以降の最初の発生日時に調整
        LocalDateTime current;
        if (eventStart.isBefore(weekStart)) {
            // イベント開始日から週開始日までの日数を計算
            long daysUntilWeekStart = eventStart.until(weekStart, ChronoUnit.DAYS);
            // 繰り返しパターンに基づいて週開始日以降の最初の発生日を計算
            switch (event.getRepeatPattern()) {
                case "毎日"://日付を1日進める
                    current = eventStart.plusDays(daysUntilWeekStart);
                    break;
                case "毎週"://1週間進める
                    current = eventStart.plusWeeks((daysUntilWeekStart + 6) / 7);
                    break;
                case "毎月"://1ヶ月進める
                    current = eventStart.plusMonths((daysUntilWeekStart + 29) / 30);
                    break;
                default:
                    return Collections.emptyList();
            }
        } else {
            current = eventStart;
        }

        List<LocalDateTime> weekOccurrences = new ArrayList<>();
        System.out.println("\n--- 繰り返し計算開始 ---");
        System.out.println("計算開始日時: " + current);

        int iterationCount = 0;
        while (!current.isAfter(weekEnd) && iterationCount < 100) {
            if (!current.isBefore(weekStart)) {
                System.out.println("→ 週内のため追加: " + current);
                weekOccurrences.add(current);
            }

            LocalDateTime previous = current;
            current = switch (event.getRepeatPattern()) {
                case "毎日" -> current.plusDays(1);
                case "毎週" -> current.plusWeeks(1);
                case "毎月" -> current.plusMonths(1);
                default -> weekEnd.plusSeconds(1);
            };
            System.out.println("次の日時へ: " + previous + " → " + current);

            iterationCount++;
        }

        if (iterationCount >= 100) {
            System.out.println("警告: 繰り返し回数が100回を超えました");
        }

        System.out.println("\n検出された発生回数: " + weekOccurrences.size());
        weekOccurrences.forEach(dt -> System.out.println("- " + dt));
        return weekOccurrences;
        //各発生日時は週内であればリストに追加され、最終的にそのリスト（weekOccurrences）が返される
    }

    // 新しいメソッド：単一のイベント発生を表示
    //単一のイベントの発生日時（startTime）を受け取り、そのイベントを適切な場所に表示する処理を行う
    private void displaySingleOccurrence(Event event, LocalDateTime startTime) {
        System.out.println("\n=== デバッグ: イベント表示処理 ===");
        System.out.println("イベント: " + event.getEventName());
        //event.getEventName() でイベント名を取得し、startTime と一緒にコンソールに表示
        //これによりどのイベントがどの日時に表示されるかを確認できる
        System.out.println("表示日時: " + startTime);

        Duration duration = Duration.between(
            //event.getStartDateTime()とevent.getEndDateTime()を使用しイベントの開始から終了までの時間差（duration）を計算
            event.getStartDateTime(),
            event.getEndDateTime()
        );
        //startTime にその時間差を加えて、終了日時（endTime）を求める
        LocalDateTime endTime = startTime.plus(duration);
        System.out.println("終了日時: " + endTime);

        //曜日の計算
        //startTime.getDayOfWeek() で、イベントが発生する曜日を取得
        //曜日はDayOfWeek型として返され、その曜日に対応する列番号（col）を計算
        DayOfWeek dayOfWeek = startTime.getDayOfWeek();
        int col = dayOfWeek.getValue();
        System.out.println("曜日: " + dayOfWeek + " (列: " + col + ")");

        //時限の計算
        //startRow と endRow は、イベントの開始時刻と終了時刻に基づいて、テーブルの行番号を取得
        //これにより、どの時間帯にイベントが表示されるかが決まる
        int startRow = DateTimeUtil.getPeriodFromTime(startTime.toLocalTime());//DateTimeUtil.getPeriodFromTime():時間を時限に変換するユーティリティメソッド
        int endRow = DateTimeUtil.getPeriodFromTime(endTime.toLocalTime());
        System.out.println("開始時限: " + startRow + ", 終了時限: " + endRow);

        //セルの更新
        //startRowからendRowまでの行を指定された曜日（col）の列で繰り返し処理しテーブルのセルを更新
        //もしそのセルに既に内容があればイベント名を追記して表示。内容が空なら、イベント名だけをセット
        for (int row = startRow; row <= endRow && row < ROWS; row++) {
            String currentContent = (String) tableModel.getValueAt(row, col);
            String newContent = formatEventCell(currentContent, event);
            System.out.println("セル(" + row + "," + col + ") 更新: [" + currentContent + "] → [" + newContent + "]");
            tableModel.setValueAt(newContent, row, col);
        }
    }

    // 新しいメソッド：セル内のイベント表示をフォーマット
    //テーブルのセルにイベント情報を適切にフォーマットして表示するための補助メソッド
    private String formatEventCell(String currentContent, Event event) {
        if (currentContent == null || currentContent.trim().isEmpty()) {
            //currentContent が空または null であれば、イベント名をそのまま表示
            return event.getEventName();
        }
        return currentContent + "\n" + event.getEventName();
    }
}
