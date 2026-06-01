package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.TradeRequest;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.core.service.TradeService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modal dialog for trading resources at the Market.
 *
 * Capacity: +10 per Market building.  After any trade, a 10-tick global cooldown
 * locks the Market.  Exchange rates are asymmetric (see TradeService.exchanged()).
 *
 * Amount entry is a free-form TextField accepting only positive integers.
 * Fractional outputs are floored (integer division) and capacity overflows
 * are blocked with an inline error before the trade is executed.
 */
public final class MarketDialog extends Dialog {

    private static final Color COLOR_SELECTED = new Color(0.98f, 0.82f, 0.35f, 1f);
    private static final Color COLOR_NORMAL   = new Color(0.80f, 0.75f, 0.60f, 1f);
    private static final Color COLOR_SUCCESS  = new Color(0.50f, 0.90f, 0.50f, 1f);
    private static final Color COLOR_ERROR    = new Color(1.00f, 0.40f, 0.40f, 1f);
    private static final Color COLOR_WARN     = new Color(1.00f, 0.65f, 0.20f, 1f);

    private final CoreGameFacade facade;
    private final Consumer<String> statusConsumer;
    private final Runnable onTradeComplete;
    private final Label resultLabel;
    private final Label cooldownLabel;
    private final Label capacityLabel;
    private final Label fromSelLabel;
    private final Label toSelLabel;
    private final TextField amountField;
    private final Skin skin;

    private final List<TextButton> fromButtons = new ArrayList<>();
    private final List<TextButton> toButtons   = new ArrayList<>();

    private ResourceType fromType = ResourceType.RICE;
    private ResourceType toType   = ResourceType.TIMBER;

    public MarketDialog(Skin skin, CoreGameFacade facade, Consumer<String> statusConsumer,
                        Runnable onTradeComplete) {
        super("", skin);
        this.facade = facade;
        this.statusConsumer = statusConsumer;
        this.onTradeComplete = onTradeComplete;
        this.skin = skin;

        resultLabel   = new Label("", skin);
        cooldownLabel = new Label("", skin, "warning");
        capacityLabel = new Label("", skin, "dim");
        fromSelLabel  = new Label("", skin, "title");
        toSelLabel    = new Label("", skin, "title");

        // ── Amount text field (digits only, max 6 chars) ────────────────────
        amountField = new TextField("1", skin);
        amountField.setMaxLength(6);
        amountField.setTextFieldFilter((field, c) -> Character.isDigit(c));
        amountField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateResult();
            }
        });

        Table content = getContentTable();
        content.pad(16);

        // Title
        Label title = new Label("Market Trade", skin, "title");
        title.setColor(new Color(0.98f, 0.82f, 0.35f, 1f));
        content.add(title).colspan(2).center().padBottom(4);
        content.row();

        // Cooldown / capacity status lines
        content.add(cooldownLabel).colspan(2).center().padBottom(2);
        content.row();
        content.add(capacityLabel).colspan(2).center().padBottom(12);
        content.row();

        // Rates reference
        Label ratesLabel = new Label(
                "Rates (give:receive):  Rice 5:1 Timber | 15:1 Tools | 30:1 Luxury\n"
                + "Timber 1:5 Rice | 10:1 Tools | 30:1 Luxury\n"
                + "Tools  1:10 Timber | 1:15 Rice | 20:1 Luxury",
                skin, "dim");
        ratesLabel.setWrap(true);
        content.add(ratesLabel).colspan(2).width(400).center().padBottom(12);
        content.row();

        // Give row
        Label giveLabel = new Label("Give:", skin);
        giveLabel.setColor(COLOR_NORMAL);
        content.add(giveLabel).right().padRight(8);
        content.add(resourceRow(true)).left();
        content.row().padTop(6);
        content.add(new Label("", skin));
        content.add(fromSelLabel).left().padBottom(6);
        content.row();

        // Receive row
        Label receiveLabel = new Label("Receive:", skin);
        receiveLabel.setColor(COLOR_NORMAL);
        content.add(receiveLabel).right().padRight(8);
        content.add(resourceRow(false)).left();
        content.row().padTop(6);
        content.add(new Label("", skin));
        content.add(toSelLabel).left().padBottom(6);
        content.row();

        // Amount row — free-form text field
        Label amtLabel = new Label("Amount:", skin);
        amtLabel.setColor(COLOR_NORMAL);
        content.add(amtLabel).right().padRight(8);
        content.add(amountField).width(120).height(28).left().padBottom(8);
        content.row();

        // Result preview / validation message
        resultLabel.setWrap(true);
        content.add(resultLabel).colspan(2).center().width(380).padBottom(6);
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
                    else        toType   = type;
                    updateResult();
                }
            });
            row.add(btn).padRight(4).height(28);
            group.add(btn);
        }
        return row;
    }

    private void executeTrade() {
        if (fromType == toType) {
            resultLabel.setColor(COLOR_ERROR);
            resultLabel.setText("Cannot trade a resource for itself!");
            return;
        }
        int amount = parseAmount();
        if (amount <= 0) {
            resultLabel.setColor(COLOR_ERROR);
            resultLabel.setText("Enter a positive whole number as the trade amount.");
            return;
        }

        // Capacity guard (UI-level check before calling core)
        VillageSnapshot snap = facade.getCurrentSnapshot();
        long markets = snap.cells().stream()
                .filter(c -> c.building() != null && c.building().type() == BuildingType.MARKET)
                .count();
        int capacity = (int) markets * TradeService.CAPACITY_PER_MARKET;
        if (amount > capacity) {
            resultLabel.setColor(COLOR_ERROR);
            resultLabel.setText("Amount " + amount + " exceeds your trade capacity of " + capacity
                    + " (" + markets + " Market" + (markets == 1 ? "" : "s")
                    + " x " + TradeService.CAPACITY_PER_MARKET + "). Build more Markets to increase it.");
            return;
        }

        var result = facade.requestTrade(new TradeRequest(fromType, toType, amount));
        resultLabel.setColor(result.success() ? COLOR_SUCCESS : COLOR_ERROR);
        resultLabel.setText(result.message());
        statusConsumer.accept(result.message());
        if (result.success()) {
            onTradeComplete.run();
        }
        updateCooldownDisplay(result.afterState());
    }

    private void updateCooldownDisplay(VillageSnapshot snapshot) {
        int cd = snapshot.marketCooldownTicks();
        if (cd > 0) {
            cooldownLabel.setText("! Market on cooldown — " + cd + " tick" + (cd == 1 ? "" : "s") + " remaining");
            cooldownLabel.setColor(COLOR_WARN);
        } else {
            cooldownLabel.setText("Market ready");
            cooldownLabel.setColor(COLOR_SUCCESS);
        }
        long markets = snapshot.cells().stream()
                .filter(c -> c.building() != null && c.building().type() == BuildingType.MARKET)
                .count();
        int cap = (int) markets * TradeService.CAPACITY_PER_MARKET;
        capacityLabel.setText("Trade capacity: " + cap + " units  (" + markets
                + " Market" + (markets == 1 ? "" : "s") + " x "
                + TradeService.CAPACITY_PER_MARKET + ")");
    }

    private void updateResult() {
        // Highlight selected resource buttons
        ResourceType[] types = ResourceType.values();
        for (int i = 0; i < fromButtons.size(); i++) {
            fromButtons.get(i).getLabel().setColor(types[i] == fromType ? COLOR_SELECTED : COLOR_NORMAL);
        }
        for (int i = 0; i < toButtons.size(); i++) {
            toButtons.get(i).getLabel().setColor(types[i] == toType ? COLOR_SELECTED : COLOR_NORMAL);
        }

        fromSelLabel.setText("Selected: " + displayName(fromType));
        fromSelLabel.setColor(COLOR_SELECTED);
        toSelLabel.setText("Selected: " + displayName(toType));
        toSelLabel.setColor(COLOR_SELECTED);

        if (fromType == toType) {
            resultLabel.setColor(COLOR_ERROR);
            resultLabel.setText("Select different resources.");
        } else {
            int amount = parseAmount();
            if (amount <= 0) {
                resultLabel.setColor(COLOR_ERROR);
                resultLabel.setText("Enter a positive whole number.");
            } else {
                int received = TradeService.exchanged(fromType, toType, amount);
                resultLabel.setColor(COLOR_NORMAL);
                resultLabel.setText("Give " + amount + " " + displayName(fromType)
                        + "  ->  receive " + received + " " + displayName(toType)
                        + "  (floored)");
            }
        }

        try {
            updateCooldownDisplay(facade.getCurrentSnapshot());
        } catch (Exception ignored) { }
    }

    /** Parses the text field. Returns 0 for invalid/empty input. */
    private int parseAmount() {
        try {
            int v = Integer.parseInt(amountField.getText().trim());
            return v > 0 ? v : 0;
        } catch (NumberFormatException e) {
            return 0;
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
