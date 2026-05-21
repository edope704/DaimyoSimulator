package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.TradeRequest;
import it.unipd.daimyosimulator.core.resource.ResourceType;

import java.util.function.Consumer;

/**
 * Modal dialog for trading resources at the Market.
 * Exchange rate: 2 of the "from" resource for 1 of the "to" resource.
 * Requires a Market building on the grid and sufficient "from" stock.
 */
public final class MarketDialog extends Dialog {
    private static final int[] AMOUNTS = {2, 4, 10};

    private final CoreGameFacade facade;
    private final Consumer<String> statusConsumer;
    private final Runnable onTradeComplete;
    private final Label resultLabel;
    private final Skin skin;

    private ResourceType fromType = ResourceType.RICE;
    private ResourceType toType   = ResourceType.TIMBER;
    private int amount = AMOUNTS[0];

    public MarketDialog(Skin skin, CoreGameFacade facade, Consumer<String> statusConsumer,
                        Runnable onTradeComplete) {
        super("Market Trade (rate 2:1)", skin);
        this.facade = facade;
        this.statusConsumer = statusConsumer;
        this.onTradeComplete = onTradeComplete;
        this.skin = skin;

        resultLabel = new Label("", skin);

        Table content = getContentTable();
        content.pad(12);

        content.add(new Label("Give:", skin)).left();
        content.add(resourceRow(true)).left().padLeft(8);
        content.row().padTop(6);

        content.add(new Label("Receive:", skin)).left();
        content.add(resourceRow(false)).left().padLeft(8);
        content.row().padTop(6);

        content.add(new Label("Amount (of 'Give'):", skin)).left();
        content.add(amountRow()).left().padLeft(8);
        content.row().padTop(8);

        content.add(resultLabel).colspan(2).left();
        content.row().padTop(4);

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
    }

    private Table resourceRow(boolean isFrom) {
        Table row = new Table();
        for (ResourceType type : ResourceType.values()) {
            TextButton btn = new TextButton(displayName(type), skin);
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    if (isFrom) {
                        fromType = type;
                    } else {
                        toType = type;
                    }
                    updateResult();
                }
            });
            row.add(btn).padRight(4).height(28);
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
        }
        return row;
    }

    private void executeTrade() {
        if (fromType == toType) {
            resultLabel.setText("Cannot trade a resource for itself!");
            return;
        }
        var result = facade.requestTrade(new TradeRequest(fromType, toType, amount));
        resultLabel.setText(result.message());
        statusConsumer.accept(result.message());
        if (result.success()) {
            onTradeComplete.run();
        }
    }

    private void updateResult() {
        if (fromType == toType) {
            resultLabel.setText("Select different resources.");
            return;
        }
        int received = Math.max(1, amount / 2);
        resultLabel.setText("Will give " + amount + " " + displayName(fromType)
                + " → receive " + received + " " + displayName(toType));
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
