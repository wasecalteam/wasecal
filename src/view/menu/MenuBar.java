package view.menu;
// インポート
import javax.swing.*;
import controller.MainController;

// メニューバークラス
// JMenuBarを継承
public class MenuBar extends JMenuBar {
    private final MainController controller;

    public MenuBar(MainController controller) {
        this.controller = controller;
        initializeMenus();
    }

    private void initializeMenus() {
        // ファイルメニュー
        JMenu fileMenu = new JMenu("ファイル");

        JMenuItem saveItem = new JMenuItem("csvで保存");
        saveItem.addActionListener(e -> controller.saveData());

        JMenuItem exportICalItem = new JMenuItem("iCalendarで保存");
        exportICalItem.addActionListener(e -> controller.saveAsICalendar());

        JMenuItem exitItem = new JMenuItem("終了");
        exitItem.addActionListener(e -> {
            controller.saveData();
            System.exit(0);
        });

        fileMenu.add(saveItem);
        fileMenu.add(exportICalItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // 表示メニュー
        JMenu viewMenu = new JMenu("表示");

        JMenuItem refreshItem = new JMenuItem("更新");
        refreshItem.addActionListener(e -> {
            controller.updateViews();
        });

        viewMenu.add(refreshItem);

        // メニューバーに追加
        add(fileMenu);
        add(viewMenu);
    }
}
