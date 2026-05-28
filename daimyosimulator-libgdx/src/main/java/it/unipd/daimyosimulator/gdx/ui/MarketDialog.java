package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.TradeRequest;
import it.unipd.daimyosimulator.core.resource.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modal dialog for trading resources at the Market.
 * Exchange rate: 2 of the "from" resource for 1 of the "to" resource.
 */
public final class MarketDialog extends Dialog {
    private static final int[] AMOUNTS = {2, 4, 10};
    private static final Color COLOR_SELECTED = new Color(0.98f, 0.82f, 0.35f, 1f);
    private static final Color COLOR_NORMAL   = new Color(0.80f, 0.75f, 0.60f, 1f);
    private static final Color COLOR_SUCCESS  = new Color(0.50f, 0.90f, 0.50f, 1f);
    private static final Color COLOR_ERROR    = new Color(1.00f, 0.40f, 0.40f, 1f);

    private final CoreGameFacade facade;
    private final Consumer<String> statusConsumer;
    private final Runnable onTradeComplete;
    private final Label resultLabel;
    private final Label fromSelLabel;
    private final Label toSelLabel;
    private final Label amtSelLabel;
    private final Skin skin;

    private final List<TextButton> fromButtons = new ArrayList<>();
    private final List<TextButton> toButtons   = new ArrayList<>();
    private final List<TextButton> amtButtons  = new ArrayList<>();

    private ResourceType fromType = ResourceType.RICE;
    private ResourceType toType   = ResourceType.TIMBER;
    private int amount = AMOUNTS[0];

    public MarketDialog(Skin skin, CoreGameFacade facade, Consumer<String> statusConsumer,
                        Runnable onTradeComplete) {
        super("", skin);
        this.facade = facade;
        this.statusConsumer = statusConsumer;
        this.onTradeComplete = onTradeComplete;
        this.skin = skin;

        resultLabel  = new Label("", skin);
        fromSelLabel = new Label("", skin, "title");
        toSelLabel   = new Label("", skin, "title");
        amtSelLabel  = new Label("", skin, "title");

        Table content = getContentTable();
        content.pad(16);

        // Title
        Label title = new Label("Market Trade", skin, "title");
        title.setColor(new Color(0.98f, 0.82f, 0.35f, 1f));
        content.add(title).colspan(2).center().padBottom(4);
        content.row();
        Label subtitle = new Label("Exchange rate: 2 : 1", skin, "dim");
        subtitle.setColor(new Color(0.60f, 0.60f, 0.50f, 1f));
        content.add(subtitle).colspan(2).center().padBottom(14);
        content.row();

        // Give row
        Label giveLabel = new Label("Give:", skin);
        giveLabel.setColor(new Color(0.80f, 0.75f, 0.60f, 1f));
        content.add(giveLabel).right().padRight(8);
        content.add(resourceRow(true)).left();
        content.row().padTop(6);

        content.add(new Label("", skin));
        content.add(fromSelLabel).left().padBottom(6);
        content.row();

        // Receive row
        Label receiveLabel = new Label("Receive:", skin);
        receiveLabel.setColor(new Color(0.80f, 0.75f, 0.60f, 1f));
        content.add(receiveLabel).right().padRight(8);
        content.add(resourceRow(false)).left();
        content.row().padTop(6);

        content.add(new Label("", skin));
        content.add(toSelLabel).left().padBottom(6);
        content.row();

        // Amount row
        Label amtLabel = new Label("Amount:", skin);
        amtLabel.setColor(new Color(0.80f, 0.75f, 0.60f, 1f));
        content.add(amtLabel).right().padRight(8);
        content.add(amountRow()).left();
        content.row().padTop(6);

        content.add(new Label("", skin));
        content.add(amtSelLabel).left().padBottom(8);
        content.row();

        // Result label
        resultLabel.setWrap(true);
        content.add(resultLabel).colspan(2).center().width(340).padBottom(6);
        content.row();

        // Trade button
        TextButton tradeButton = new TextButton("Execute Trade", skin);
        tradeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                executeTrade();
            }
        });
        content.add(tradeButton).colspan(2).fillX().padTop(4);

        button("Close");
        setMovable(true);
        setResizable(false);

        // Show initial preview
        updateResult();
    }

    private Table resourceRow(boolean isFrom) {
        Table row = new Table();
        List<TextButton> group = isFrom ? fromButtons : toButtons;
        for (ResourceType type : ResourceType.values()) {
            TextButton btn = new TextButton(displayName(type), skin);
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    if (isFrom) fromType = type;
                    else         toType   = type;
                    updateResult();
                }
            });
            row.add(btn).padRight(4).height(28);
            group.add(btn);
        }
        return row;
    }

    private Table amountRow() {
        Table row = new Table();
        for (int amt : AMOUNTS) {
            TextButton btn = new TextButton(String.valueOf(amt), skin);
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    amount = amt;
                    updateResult();
                }
            });
            row.add(btn).padRight(4).width(44).height(28);
            amtButtons.add(btn);
        }
        return row;
    }

    private void executeTrade() {
        if (fromType == toType) {
            resultLabel.setColor(COLOR_ERROR);
            resultLabel.setText("Cannot trade a resource for itself!");
            return;
        }
        var result = facade.requestTrade(new TradeRequest(fromType, toType, amount));
        resultLabel.setColor(result.success() ? COLOR_SUCCESS : COLOR_ERROR);
        resultLabel.setText(result.message());
        statusConsumer.accept(result.message());
        if (result.success()) onTradeComplete.run();
    }

    private void updateResult() {
        // Highlight selected buttons in each group
        ResourceType[] types = ResourceType.values();
        for (int i = 0; i < fromButtons.size(); i++) {
            fromButtons.get(i).getLabel().setColor(types[i] == fromType ? COLOR_SELECTED : COLOR_NORMAL);
        }
        for (int i = 0; i < toButtons.size(); i++) {
            toButtons.get(i).getLabel().setColor(types[i] == toType ? COLOR_SELECTED : COLOR_NORMAL);
        }
        for (int i = 0; i < amtButtons.size(); i++) {
            amtButtons.get(i).getLabel().setColor(AMOUNTS[i] == amount ? COLOR_SELECTED : COLOR_NORMAL);
        }

        fromSelLabel.setText("Selected: " + displayName(fromType));
        fromSelLabel.setColor(COLOR_SELECTED);
        toSelLabel.setText("Selected: " + displayName(toType));
        toSelLabel.setColor(COLOR_SELECTED);
        amtSelLabel.setText("Selected: " + amount);
        amtSelLabel.setColor(COLOR_SELECTED);

        if (fromType == toType) {
            resultLabel.setColor(COLOR_ERROR);
            resultLabel.setText("Select different resources.");
        } else {
            int received = Math.max(1, amount / 2);
            resultLabel.setColor(COLOR_NORMAL);
            resultLabel.setText("Give " + amount + " " + displayName(fromType)
                    + "  →  receive " + received + " " + displayName(toType));
        }
    }

    private static String displayName(ResourceType type) {
        return switch (type) {
            case RICE         -> "Rice";
            case TIMBER       -> "Timber";
            case TOOLS        -> "Tools";
            case LUXURY_GOODS -> "Luxury";
        };
    }
}
