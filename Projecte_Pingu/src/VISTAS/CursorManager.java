package VISTAS;

import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;

/**
 * Gestiona els dos cursors personalitzats de l'aplicació:
 * <ul>
 *   <li><b>cursor1.png</b> — cursor per defecte (fletxa de gel, navegació normal)</li>
 *   <li><b>cursor2.png</b> — cursor de clic/selecció (mà de gel, elements interactius)</li>
 * </ul>
 *
 * Ús:
 *   CursorManager.apply(scene)           → cursor1 a tota la scene + cursor2 a tots els elements interactius
 *   CursorManager.applyClickable(node)   → cursor2 a un node concret
 *   CursorManager.applyWhenReady(node)   → aplica cursors quan el node tingui scene
 *   CursorManager.getClickCursor()       → retorna l'ImageCursor de cursor2 (per a setCursor manual)
 */
public class CursorManager {

    private static ImageCursor defaultCursor = null;
    private static ImageCursor clickCursor   = null;
    private static boolean loaded = false;

    private CursorManager() {}

    /* ── Càrrega lazy dels dos cursors ─────────────────────────── */

    private static void load() {
        if (loaded) return;
        loaded = true;
        try {
            Image img1 = new Image(CursorManager.class.getResourceAsStream("/pngs_cursores/cursor1.png"));
            // Hotspot a la punta superior esquerra del cursor fletxa
            defaultCursor = new ImageCursor(img1, img1.getWidth() * 0.15, img1.getHeight() * 0.05);
        } catch (Exception e) {
            System.out.println("CursorManager: no s'ha pogut carregar cursor1.png — " + e.getMessage());
        }
        try {
            Image img2 = new Image(CursorManager.class.getResourceAsStream("/pngs_cursores/cursor2.png"));
            // Hotspot a la punta del dit índex (part superior central de la mà)
            clickCursor = new ImageCursor(img2, img2.getWidth() * 0.40, img2.getHeight() * 0.05);
        } catch (Exception e) {
            System.out.println("CursorManager: no s'ha pogut carregar cursor2.png — " + e.getMessage());
        }
    }

    /** @return l'ImageCursor de clic (cursor2), o {@code null} si no s'ha pogut carregar. */
    public static ImageCursor getClickCursor() {
        load();
        return clickCursor;
    }

    /* ── API pública ───────────────────────────────────────────── */

    /**
     * Aplica cursor1 com a cursor per defecte de la Scene, i recorre
     * tot l'arbre de nodes per assignar cursor2 a cada element interactiu
     * (botons, checkboxes, combo-boxes, sliders, hyperlinks, etc.).
     */
    public static void apply(Scene scene) {
        if (scene == null) return;
        load();

        // Cursor per defecte a tota la Scene (cursor1 — fletxa)
        if (defaultCursor != null) {
            scene.setCursor(defaultCursor);
        }

        // Recórrer l'arbre i marcar elements interactius amb cursor2
        if (scene.getRoot() != null) {
            applyHandCursorRecursive(scene.getRoot());
        }
    }

    /** Aplica el cursor per defecte (cursor1) a un Node concret. */
    public static void apply(Node node) {
        load();
        if (node != null && defaultCursor != null) {
            node.setCursor(defaultCursor);
        }
    }

    /** Aplica el cursor de clic (cursor2) a un Node concret. */
    public static void applyClickable(Node node) {
        load();
        if (node != null && clickCursor != null) {
            node.setCursor(clickCursor);
        }
    }

    /**
     * Quan el node s'afegeix a una Scene, aplica cursors automàticament
     * (cursor1 a la scene + cursor2 a tots els elements interactius).
     */
    public static void applyWhenReady(Node anchor) {
        anchor.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) apply(newScene);
        });
    }

    /**
     * Quan el node s'afegeix a una Scene, aplica cursor2 al propi node.
     * Ideal per a botons i elements interactius carregats des de FXML.
     */
    public static void applyClickableWhenReady(Node node) {
        node.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) applyClickable(node);
        });
    }

    /* ── Internals ─────────────────────────────────────────────── */

    private static void applyHandCursorRecursive(Node node) {
        if (isInteractive(node) && clickCursor != null) {
            node.setCursor(clickCursor);
        }

        // Recórrer fills
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                applyHandCursorRecursive(child);
            }
        }

        // TabPane: el contingut dels tabs no és fill directe
        if (node instanceof TabPane) {
            for (Tab tab : ((TabPane) node).getTabs()) {
                if (tab.getContent() != null) {
                    applyHandCursorRecursive(tab.getContent());
                }
            }
        }
    }

    /**
     * Retorna {@code true} si el node és "interactiu" i ha de mostrar cursor2.
     */
    private static boolean isInteractive(Node node) {
        return node instanceof ButtonBase   // Button, ToggleButton, CheckBox, RadioButton…
            || node instanceof Hyperlink
            || node instanceof ComboBox
            || node instanceof Slider
            || node instanceof MenuBar;
    }
}
