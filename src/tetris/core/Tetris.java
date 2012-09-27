/* centering game class
 */


package tetris.core;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tetris.api.game.Game;
import tetris.api.game.GameProperty;
import tetris.api.game.GameState;

class GameRoot extends BorderPane {

    private static final double scaleFactor = 0.15;

    private Button _createButton(String id, String text) {
        return ButtonBuilder.create()
                .text(text)
                .id(id)
                .maxHeight(Double.MAX_VALUE)
                .maxWidth(Double.MAX_VALUE)
                .build();
    }

    private class CloseAppHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Platform.exit();
        }
    }


    GameRoot(final GameState gs) {
        super();
        Button exitButton = _createButton("exitButton", "Exit");
        Button newButton = _createButton("newButton", "New Game");
        exitButton.setOnAction(new CloseAppHandler());
        newButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        setCenter(new GameBoard(gs));
                    }
                });

        final VBox vbox = VBoxBuilder.create()
                .alignment(Pos.CENTER)
                .children(newButton
                        , _createButton("saveButton", "Save")
                        , exitButton)
                .build();
        vbox.maxWidthProperty().bind(this.widthProperty().multiply(0.77));

        this.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                Insets oldInsets = vbox.getPadding();
                Double newPadding = newValue.doubleValue() * scaleFactor;
                Insets newInsets = new Insets(oldInsets.getTop(), newPadding,
                        oldInsets.getBottom(), newPadding);
                vbox.setPadding(newInsets);
            }
        });

        this.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                Insets oldInsets = vbox.getPadding();
                Double newPadding = newValue.doubleValue() * scaleFactor;
                Insets newInsets = new Insets(newPadding, oldInsets.getLeft(),
                        newPadding, oldInsets.getRight());
                vbox.setPadding(newInsets);
                vbox.setSpacing(newPadding);
            }
        });

        this.setCenter(vbox);
    }
}

class TetrisStatic implements GameProperty {

    private final static SimpleStringProperty _version = new SimpleStringProperty("0.01");
    private final static SimpleStringProperty _name = new SimpleStringProperty("Tetris Game");

    @Override
    public final ReadOnlyStringProperty version() {
        return _version;
    }

    @Override
    public final String getVersion() {
        return version().getValue();
    }

    @Override
    public final ReadOnlyStringProperty name() {
        return _name;
    }

    @Override
    public final String getName() {
        return name().getValue();
    }
}

class TetrisDynamic extends TetrisStatic implements GameState {
    private final SimpleStringProperty _title = new SimpleStringProperty();
    private final SimpleDoubleProperty _width = new SimpleDoubleProperty();
    private final SimpleDoubleProperty _height = new SimpleDoubleProperty();

    TetrisDynamic(double width, double height) {
        super();
        _title.setValue(name().getValue());
        _width.setValue(width);
        _height.setValue(height);
    }

    TetrisDynamic() {
        this(800, 600);
    }

    @Override
    public final StringProperty title() {
        return _title;
    }

    @Override
    public final String getTitle() {
        return title().getValue();
    }

    @Override
    public final void setTitle(String title) {
        title().setValue(title);
    }

    @Override
    public final DoubleProperty widthProperty() {
        return _width;
    }

    @Override
    public final double getWidth() {
        return widthProperty().getValue();
    }

    @Override
    public final void setWidth(double width) {
        widthProperty().setValue(width);
    }

    @Override
    public DoubleProperty heightProperty() {
        return _height;
    }

    @Override
    public double getHeight() {
        return heightProperty().getValue();
    }

    @Override
    public final void setHeight(double height) {
        heightProperty().setValue(height);
    }
}

public class Tetris extends TetrisDynamic implements  Game  {
    private Stage primaryStage;
    private boolean primaryStageHasBeenShowed;
    private BooleanProperty rsProperty;

    public Tetris() {
        super();
        rsProperty = new SimpleBooleanProperty(false);
        primaryStageHasBeenShowed = false;

        rsProperty.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal ) {
                if (oldVal == newVal) {
                    // do nothing
                } else {
                    if (newVal == true) { // start
                        if (primaryStageHasBeenShowed == false) {
                            primaryStage.show();
                            primaryStageHasBeenShowed = true;
                        }
                         // TODO resume game
                    } else { // stop/pause
                        // TODO pause game
                    }

                }
            }
        });
    }

    private Group extraGroup;
    private GameRoot gameRoot;
    public void init(Stage primaryStage) {
        this.primaryStage = primaryStage;
        String csspath = this.getClass()
                .getResource("/css/stylesheet.css")
                .toExternalForm();

        gameRoot = new GameRoot((this));
        extraGroup = new Group();
        gameRoot.setTop(extraGroup);

        Scene primaryScene = SceneBuilder.create()
                .root(gameRoot)
                .stylesheets(csspath)
                .width(getWidth())
                .height(getHeight())
                .fill(Color.LIGHTSEAGREEN)
                .build();

        primaryStage.titleProperty().bindBidirectional(title());
        widthProperty().bind(primaryStage.widthProperty());
        heightProperty().bind(primaryStage.heightProperty());
        primaryStage.setScene(primaryScene);
    }

    @Override
    public ReadOnlyBooleanProperty runningStatusProperty() {
        return rsProperty;
    }

    @Override
    public boolean getRunningStatus() {
        return runningStatusProperty().getValue();
    }

    @Override
    public void setRunningStatus(boolean rs) {
        rsProperty.set(rs);
    }

    @Override
    public void addNode(Node n) {
        extraGroup.getChildren().add(n);
        gameRoot.toBack();
    }

    @Override
    public void removeNode(Node n) {
        extraGroup.getChildren().remove(n);
    }

    @Override
    public void start() {
        setRunningStatus(true);
    }

    @Override
    public void stop() {
        setRunningStatus(false);
    }

    @Override
    public void quit() {
        Platform.exit();
    }
}
