/*  Copyright (c) 2012 All Right Reserved
 *
 *  This source is subject to the GNU general public License.  Please see the
 *  gpl.txt file for more information.  All other rights reserved.
 *
 *  @file:   $File$
 *  @brief:  controller for user interface, internal hold all used ui components
 *           dynamic layout code resides in the initialize() function and will be
 *           executed by the fxml loader when loading completing.
 *  @author: $Author$
 *  @date:   $Date$
 */
package tetris.core;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.web.WebView;
import tetris.ui.LargeLabel;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * @author shellfish
 */
public class UIController implements Initializable {

    /*
        UI Components
     */
    @FXML
    private LargeLabel levelLabel;
    @FXML
    private Slider    levelSlider;
    @FXML
    private StackPane window;
    @FXML
    private ToolBar toolbar;
    @FXML
    private ToggleButton toggleButton;
    @FXML
    private Pane optionPage;
    @FXML
    private BorderPane root;
    @FXML
    private HBox center;
    @FXML
    private StackPane previewBox;
    @FXML
    private Slider columnNumberSlider;
    @FXML
    private Slider rowNumberSlider;
    @FXML
    private Pane gameOverPage;
    @FXML
    private LargeLabel columnNumberLabel;
    @FXML
    private LargeLabel rowNumberLabel;
    @FXML
    private GridPane optionDisplay;
    @FXML
    private WebView helpPage;
    @FXML
    private ToggleButton helpButton;
    @FXML
    private  HBox helpContainer;
    @FXML
    private Button newGameButton;
    @FXML
    private Pane scoreBox;
    @FXML
    private Pane timerBox;
    @FXML
    private Slider lockDelaySlider;
    @FXML
    private LargeLabel lockDelayLabel;


    public HBox getCenter() {
        return center;
    }

    public Pane getPreviewBox() {
        return previewBox;
    }

    public Pane getScoreBox() {
        return scoreBox;
    }

    public Pane getTimerBox() {
        return timerBox;
    }



    private final AudioClip clipMenu;
    private final AudioClip clipGamePlay;
    private int trackNo = -1;
    private void switchTrack(int trackNo) {
        if (this.trackNo != trackNo) {
            if (trackNo == 0) {
                clipGamePlay.stop();
                clipMenu.play();
            } else if (trackNo == 1) {
                clipMenu.stop();
                clipGamePlay.play();
            }

            this.trackNo = trackNo;
        }
    }

    
    public UIController() throws URISyntaxException
    {
           clipMenu  = new AudioClip(getClass().getResource("/sounds/tetris.mp3").toURI().toString());;
           clipGamePlay =   new AudioClip(getClass().getResource("/sounds/gameplay.mp3").toURI().toString());
    }

    
    /*
        UI State machine
     */
    private Game game;
    private Option option;


    public void restartGame() {
        // restart current game
        if (game != null) {
            toggleButton.setDisable(false);
            game.restart();
            root.setCenter(center);
            center.requestFocus();
        }
    }

    @FXML
    private void startNewGame() {
        game = new Game(this, option) {
            @Override
            public void stop() {
                root.setCenter(gameOverPage);
                toggleButton.setDisable(true);
            }

            @Override
            protected final void toggle() {
                super.toggle();
                if (getState() == State.ST_PAUSED) {
                    toggleButton.setSelected(true);
                    toggleButton.setText("Resume");
                } else {
                    toggleButton.setSelected(false);
                    toggleButton.setText("Pause");
                }
            }
        };

        restartGame();
        switchTrack(1);
        toggleButton.setSelected(false);
        newGameButton.setText("New Game");
    }

    public void newGame() {
        if (root.getCenter() != optionPage) {
            if (game != null) {
                // delete current game
                game.delete();
                game = null;
            }
            toggleButton.setDisable(true);
            root.setCenter(optionPage);
            switchTrack(0);
            newGameButton.setText("Start");
        } else {
            startNewGame();
        }
    }

    @FXML
    private void toggleGame() {
        if (root.getCenter() == center && game != null) {
            // can only toggle when game is on top
            game.toggle();
        }
    }

    private final static String easy   = "Easy";
    private final static String normal = "Normal";
    private final static String hard   = "Hard";
    private String levelToDesc(int level) {
        switch (level) {
            case 1:case 2:case 3:
                return  easy;
            case 4:case 5:case 6:
                return normal;
            case 7:case 8:case 9:case 10:
                return hard;
            default:
                throw  new RuntimeException();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // install tooltip
        Tooltip.install(scoreBox, new Tooltip("score board"));
        Tooltip.install(previewBox, new Tooltip("tetromino preview"));
        Tooltip.install(timerBox, new Tooltip("timer for current game"));
        Tooltip.install(rowNumberLabel, new Tooltip("row number"));
        Tooltip.install(columnNumberLabel, new Tooltip("column number"));
        Tooltip.install(lockDelayLabel, new Tooltip("how many frames will be counted before the tetromino is locked in the bottom"));


        // initialize option
        option = new Option();
        option.rowNumberProperty().bind(rowNumberSlider.valueProperty());
        option.columnNumberProperty().bind(columnNumberSlider.valueProperty());
        option.lockDelayProperty().bind(lockDelaySlider.valueProperty());
        option.levelProperty().bind(levelSlider.valueProperty());


        levelLabel.setText(levelToDesc((int)Math.round(levelSlider.getValue())));
        lockDelayLabel.setText(lockDelaySlider.valueProperty().intValue() + "f");
        columnNumberLabel.setText(String.valueOf(columnNumberSlider.valueProperty().intValue()));
        rowNumberLabel.setText(String.valueOf(rowNumberSlider.valueProperty().intValue()));

        columnNumberSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newVal) {
                columnNumberLabel.setText(String.format("%1$,.0f", newVal));
            }
        });


        rowNumberSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                rowNumberLabel.setText(String.format("%1$,.0f", newVal));
            }
        });


        lockDelaySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newVal) {
                lockDelayLabel.setText(String.format("%1$,.0f", newVal) + "f");
            }
        });

        levelSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newVal) {
                levelLabel.setText(levelToDesc((int)Math.round(levelSlider.getValue())));
            }
        });



        // load help page
        String helpLink = getClass().getResource("/doc/help.html").toExternalForm();
        helpPage.getEngine().load(helpLink);

        // bind helpPage width
        window.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newVal) {
                double width = 0.8 * newVal.doubleValue();
                width = width > 365 ? 365 : width;
                helpContainer.setMaxWidth(width);
            }
        });

        window.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newVal) {
                double height = 0.8 * newVal.doubleValue();
                height = height > 490? 490: height;
                helpContainer.setMaxHeight(height);
            }
        });


        helpContainer.translateYProperty().bind(toolbar.heightProperty().divide(2.0f));
        helpContainer.translateXProperty().bind(window.widthProperty().subtract(helpContainer.widthProperty()).divide(16.0f));

        helpContainer.visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean newVal) {
                helpButton.setSelected(newVal);
            }
        });
        helpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                helpContainer.setVisible(!helpContainer.isVisible());
            }
        });

        switchTrack(0);

    }
}
