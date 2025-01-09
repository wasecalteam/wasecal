package controller.subject;

// インポート
import javax.swing.*;
import controller.MainController;
import model.subject.Subject;
import view.subject.SubjectPanel;

// 科目コントローラクラス
public class SubjectController {
    private final MainController mainController;
    private final SubjectPanel subjectPanel;

    public SubjectController(MainController mainController, SubjectPanel subjectPanel) {
        this.mainController = mainController;
        this.subjectPanel = subjectPanel;
        initializeListeners();
    }

    // リスナーの初期化
    private void initializeListeners() {
        subjectPanel.setAddButtonListener(e -> handleAddSubject());
        subjectPanel.setEditButtonListener(e -> handleEditSubject());
        subjectPanel.setDeleteButtonListener(e -> handleDeleteSubject());
    }

    // 科目の追加
    private void handleAddSubject() {
        Subject newSubject = subjectPanel.showAddSubjectDialog();
        if (newSubject != null) {
            // 重複チェック
            if (mainController.isSubjectConflict(newSubject)) {
                JOptionPane.showMessageDialog(
                    subjectPanel,
                    "選択された曜日・時限には既に科目が登録されています。",
                    "登録エラー",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            mainController.addSubject(newSubject);
        }
    }

    // 科目の編集
    private void handleEditSubject() {
        Subject selectedSubject = subjectPanel.getSelectedSubject();
        if (selectedSubject != null) {
            Subject editedSubject = subjectPanel.showEditSubjectDialog(selectedSubject);
            if (editedSubject != null) {
                mainController.removeSubject(selectedSubject);
                if (mainController.isSubjectConflict(editedSubject)) {
                    mainController.addSubject(selectedSubject);
                    JOptionPane.showMessageDialog(
                        subjectPanel,
                        "選択された曜日・時限には既に科目が登録されています。",
                        "編集エラー",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                mainController.addSubject(editedSubject);
            }
        }
    }

    // 科目の削除
    private void handleDeleteSubject() {
        Subject selectedSubject = subjectPanel.getSelectedSubject();
        if (selectedSubject != null) {
            int result = JOptionPane.showConfirmDialog(
                subjectPanel,
                "選択された科目を削除しますか？",
                "科目の削除",
                JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                mainController.removeSubject(selectedSubject);
            }
        }
    }
}
