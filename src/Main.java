//　インポート
import javax.swing.*;
import java.io.*;
import controller.MainController;

// メインクラス
public class Main {
    public static void main(String[] args) {
        // Swing UIの見た目をOSに似たものに設定
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Event Dispatch Threadでアプリケーションを起動
        SwingUtilities.invokeLater(() -> {
            // データディレクトリの作成
            createDataDirectory();
            // MainControllerの作成（GUI起動)
            new MainController();
        });
    }

    // データディレクトリの作成
    private static void createDataDirectory() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            if (!dataDir.mkdir()) {
                System.err.println("データディレクトリの作成に失敗しました。");
            }
        }
    }
}