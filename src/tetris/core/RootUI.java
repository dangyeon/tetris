/* The out most UI container for the whole game
   Copyright (C) 2012, thu10e team.
   This file is part of the implementaion of Tetris Game  made by thu10e team
   for the assessment of COMP1110/67 ** 10 assignment.
 */

package tetris.core;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import tetris.api.game.GameControl;
import tetris.api.game.GameControl.Status;
import tetris.api.game.GameControl.StatusListener;
import tetris.api.game.GameState;

import static tetris.api.game.GameControl.Status.PLAY_GAME;
import static tetris.api.game.GameControl.Status.RESTART_GAME;
import static tetris.api.game.GameControl.Status.SHOW_MENU;

public class RootUI extends BorderPane {

    private static final double scaleFactor = 0.15;

    private Button _createButton(String id, String text) {
        return ButtonBuilder.create()
                .text(text)
                .id(id)
                .maxHeight(Double.MAX_VALUE)
                .maxWidth(Double.MAX_VALUE)
                .build();
    }

    RootUI(final GameState gs) {
        super();


        Button exitButton = _createButton("exitButton", "Exit");
        Button playButton = _createButton("playButton", "Play");
        Button replayButton = _createButton("replayButton", "Re-Play");
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ((GameControl) gs).quit();
            }
        });


        playButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ((GameControl) gs).play();
                    }
                }
        );

        replayButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ((GameControl) gs).restart();
            }
        });

        final GameUI gameUI = new GameUI(gs);


        final VBox menuBoard = VBoxBuilder.create()
                .alignment(Pos.CENTER)
                .children(playButton, replayButton, exitButton)
                .build();
        menuBoard.maxWidthProperty().bind(this.widthProperty().multiply(0.77));

        this.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                Insets oldInsets = menuBoard.getPadding();
                Double newPadding = newValue.doubleValue() * scaleFactor;
                Insets newInsets = new Insets(oldInsets.getTop(), newPadding,
                        oldInsets.getBottom(), newPadding);
                menuBoard.setPadding(newInsets);
            }
        });

        this.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                Insets oldInsets = menuBoard.getPadding();
                Double newPadding = newValue.doubleValue() * scaleFactor;
                Insets newInsets = new Insets(newPadding, oldInsets.getLeft(),
                        newPadding, oldInsets.getRight());
                menuBoard.setPadding(newInsets);
                menuBoard.setSpacing(newPadding);
            }
        });


        ((GameControl) gs).addStatusListener(new StatusListener() {
            @Override
            public void callback(Status oldStatus, Status newStatus) {
                if ((newStatus == PLAY_GAME || newStatus == RESTART_GAME)
                        && oldStatus == SHOW_MENU) {
                    RootUI.this.setCenter(gameUI);
                    gameUI.requestFocus();
                } else if (newStatus == SHOW_MENU) {
                    RootUI.this.setCenter(menuBoard);
                    menuBoard.requestFocus();
                }
            }
        });
    }
}

