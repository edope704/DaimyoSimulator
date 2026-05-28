package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import it.unipd.daimyosimulator.core.app.view.PopulationViewModel;
import it.unipd.daimyosimulator.core.villager.Role;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.assets.ParameterType;

public final class PopulationPanel extends Table {
    private final Skin skin;
    private final Label label;
    private PopulationViewModel currentPop;

    public PopulationPanel(Skin skin, GameAssetManager assetManager) {
        this.skin = skin;
        setBackground(skin.getDrawable("hud-panel"));
        label = new Label("", skin);
        add(new Image(assetManager.getParameterIcon(ParameterType.POPULATION))).size(24).padRight(4);
        add(label).left();

        addListener(new InputListener() {
            @Override public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) { return true; }
            @Override public void touchUp(InputEvent e, float x, float y, int pointer, int button) {
                openModal();
            }
        });
    }

    public void refresh(PopulationViewModel population) {
        currentPop = population;
        label.setText("Pop " + population.total()
                + "  Idle " + population.idle()
                + "  Employed " + population.employed()
                + "  Unhoused " + population.unhoused());
    }

    private void openModal() {
        if (getStage() == null || currentPop == null) return;

        Dialog dialog = new Dialog("Population Breakdown", skin);
        Table content = dialog.getContentTable();
        content.pad(16);

        addRow(content, skin, "Total",    String.valueOf(currentPop.total()));
        addRow(content, skin, "Housed",   String.valueOf(currentPop.total() - currentPop.unhoused()));
        addRow(content, skin, "Unhoused", String.valueOf(currentPop.unhoused()));
        addRow(content, skin, "Employed", String.valueOf(currentPop.employed()));
        addRow(content, skin, "Idle",     String.valueOf(currentPop.idle()));

        content.add(new Label("", skin)).padTop(8);
        content.row();
        Label jobsHeader = new Label("Jobs", skin, "title");
        content.add(jobsHeader).colspan(2).left().padBottom(4);
        content.row();

        for (Role role : Role.values()) {
            if (role == Role.UNHOUSED || role == Role.IDLE) continue;
            int count = currentPop.roleCounts().getOrDefault(role, 0);
            addRow(content, skin, roleName(role), String.valueOf(count));
        }

        dialog.button("Close");
        dialog.setMovable(true);
        dialog.show(getStage());
        getStage().cancelTouchFocus();
    }

    private static void addRow(Table content, Skin skin, String name, String value) {
        content.add(new Label(name, skin)).left().padRight(24).width(120);
        content.add(new Label(value, skin)).left();
        content.row();
    }

    private static String roleName(Role role) {
        return switch (role) {
            case RICE_FARMER -> "Rice Farmer";
            case WOODCUTTER  -> "Woodcutter";
            case BLACKSMITH  -> "Blacksmith";
            case ARTISAN     -> "Artisan";
            case TRADER      -> "Trader";
            case SAMURAI     -> "Samurai";
            case MONK        -> "Monk";
            default          -> role.name();
        };
    }
}
