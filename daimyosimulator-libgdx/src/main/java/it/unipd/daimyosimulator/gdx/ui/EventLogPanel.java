package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.app.view.EventLogViewModel;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.assets.ParameterType;

import java.util.ArrayDeque;
import java.util.Deque;

public final class EventLogPanel extends Table {
    private static final int   MAX_ENTRIES       = 20;
    private static final float HEIGHT_COLLAPSED  = 80f;
    private static final float HEIGHT_EXPANDED   = 180f;
    private static final float ROW_WIDTH         = 148f;
    private static final Color COLOR_GAME        = new Color(0.88f, 0.80f, 0.58f, 1f);
    private static final Color COLOR_STATUS      = new Color(0.60f, 0.75f, 0.60f, 1f);

    private final Skin skin;
    private final Deque<Entry> entries = new ArrayDeque<>();
    private final Table rows;
    private final com.badlogic.gdx.scenes.scene2d.ui.Cell<?> scrollCell;
    private boolean expanded = false;

    private record Entry(String text, boolean isGame) {}

    public EventLogPanel(Skin skin, GameAssetManager assetManager) {
        this.skin = skin;
        setBackground(skin.getDrawable("hud-panel"));
        pad(4, 6, 4, 6);

        TextButton expandBtn = new TextButton("v", skin);
        expandBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                expanded = !expanded;
                expandBtn.setText(expanded ? "^" : "v");
                scrollCell.height(expanded ? HEIGHT_EXPANDED : HEIGHT_COLLAPSED);
                invalidateHierarchy();
            }
        });

        Table header = new Table();
        header.add(new Image(assetManager.getParameterIcon(ParameterType.EVENT_ALERT))).size(16).padRight(4);
        header.add(new Label("EVENTS", skin, "title")).left().expandX();
        header.add(expandBtn).size(22).right();
        add(header).fillX().padBottom(3);
        row();

        rows = new Table();
        rows.defaults().left();
        rows.top();

        ScrollPane scroll = new ScrollPane(rows, new ScrollPaneStyle());
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(true);
        scrollCell = add(scroll).width(ROW_WIDTH + 16).height(HEIGHT_COLLAPSED).left().top();
    }

    public void refresh(EventLogViewModel eventLog) {
        entries.clear();
        for (String ev : eventLog.events()) {
            entries.addLast(new Entry(ev, true));
        }
        trim();
        redraw();
    }

    public void addStatus(String status) {
        if (status == null || status.isBlank()) return;
        entries.addLast(new Entry(status, false));
        trim();
        redraw();
    }

    private void trim() {
        while (entries.size() > MAX_ENTRIES) entries.removeFirst();
    }

    private void redraw() {
        rows.clearChildren();
        for (Entry entry : entries) {
            String bullet = entry.isGame() ? "* " : ". ";
            Color  color  = entry.isGame() ? COLOR_GAME : COLOR_STATUS;
            Label  row    = new Label(bullet + entry.text(), skin, "dim");
            row.setColor(color);
            row.setWrap(true);
            rows.add(row).width(ROW_WIDTH).left().padBottom(1);
            rows.row();
        }
        rows.layout();
    }
}
