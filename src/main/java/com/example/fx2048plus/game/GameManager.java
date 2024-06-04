package com.example.fx2048plus.game;

import com.example.fx2048plus.Main;
import com.example.fx2048plus.config.*;
import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class GameManager extends Group {

    private static final Duration ANIMATION_EXISTING_TILE = Duration.millis(65);
    private static final Duration ANIMATION_NEWLY_ADDED_TILE = Duration.millis(125);
    private static final Duration ANIMATION_MERGED_TILE = Duration.millis(80);

    private final LevelConfig config;
    private final Board board;
    private final Stage stage;

    private boolean movingTiles = false;

    private final HashMap<Location, Tile> tiles = new HashMap<>();
    private final List<Location> locations;
    private final Set<Tile> mergedToBeRemoved = new HashSet<>();

    private GameState gameState = GameState.getInstance();

    private final Map<Modifiers, Label> modifiersCountMap = new HashMap<>();
    private final Map<Modifiers, Label> modifiersButtonMap = new HashMap<>();

    private Modifiers selectedBonus = null;
    private boolean isSelecting = false;

    public GameManager(LevelConfig config, Stage stage) {
        this.config = config;
        this.stage = stage;
        this.locations = new ArrayList<>(config.gridSize * config.gridSize);
        board = new Board(config, modifiersButtonMap, modifiersCountMap);
        getChildren().add(board);

        init();
        startGame();
        addBonusLabelListeners();
    }

    private void init() {
        locations.clear();
        tiles.clear();
        board.removeAllTiles();
        board.setGameOver(false);
        initBonusCount();

        for (int i = 0; i < config.gridSize; i++) {
            for (int j = 0; j < config.gridSize; j++) {
                Location location = new Location(j, i);
                locations.add(location);
                tiles.put(location, null);
            }
        }
    }

    private void initBonusCount(){
        gameState.modifiersCountMap.put(
                Modifiers.THREETWOADD, config.bonuses.stream().filter(m -> Modifiers.THREETWOADD == m.getName()).findFirst().map(Modifier::getCount).orElse(0)
        );
        board.updateModifiersCount(Modifiers.THREETWOADD, gameState.modifiersCountMap.get(Modifiers.THREETWOADD));
        gameState.modifiersCountMap.put(
                Modifiers.REMOVE, config.bonuses.stream().filter(m -> Modifiers.REMOVE == m.getName()).findFirst().map(Modifier::getCount).orElse(0)
        );
        board.updateModifiersCount(Modifiers.REMOVE, gameState.modifiersCountMap.get(Modifiers.REMOVE));
        gameState.modifiersCountMap.put(
                Modifiers.X2, config.bonuses.stream().filter(m -> Modifiers.X2 == m.getName()).findFirst().map(Modifier::getCount).orElse(0)
        );
        board.updateModifiersCount(Modifiers.X2, gameState.modifiersCountMap.get(Modifiers.X2));
        gameState.modifiersCountMap.put(
                Modifiers.LASTCHANCE, config.bonuses.stream().filter(m -> Modifiers.LASTCHANCE == m.getName()).findFirst().map(Modifier::getCount).orElse(0)
        );
        board.updateModifiersCount(Modifiers.LASTCHANCE, gameState.modifiersCountMap.get(Modifiers.LASTCHANCE));
        gameState.modifiersCountMap.put(
                Modifiers.SHUFFLE, config.bonuses.stream().filter(m -> Modifiers.SHUFFLE == m.getName()).findFirst().map(Modifier::getCount).orElse(0)
        );
        board.updateModifiersCount(Modifiers.SHUFFLE, gameState.modifiersCountMap.get(Modifiers.SHUFFLE));
    }

    private void startGame() {
        board.stopCountdown();
        createRandomTiles();
        int time = config.modifiers.stream().filter(m -> Modifiers.TIME == m.getName()).findFirst().map(Modifier::getCount).orElse(0);
        board.startCountdown(time * 60);
    }

    public void move(Direction direction) {
        if (gameState.isGameOver) {
            return;
        }

        synchronized (tiles) {
            if (movingTiles) {
                return;
            }
        }

        synchronized (tiles) {
            movingTiles = true;
        }

        mergedToBeRemoved.clear();
        var parallelTransition = new ParallelTransition();
        int defaultIndexI = direction == Direction.RIGHT ? config.gridSize - 1 : 0;
        int defaultIndexJ = direction == Direction.DOWN ? config.gridSize - 1 : 0;

        if (direction == Direction.RIGHT || direction == Direction.LEFT) {
            for (int j = defaultIndexJ; j < config.gridSize && j >= 0; j += direction == Direction.DOWN ? -1 : 1) {
                for (int i = defaultIndexI; i < config.gridSize && i >= 0; i += direction == Direction.RIGHT ? -1 : 1) {
                    moveTile(i, j, direction, parallelTransition);
                }
            }
        } else {
            for (int i = defaultIndexI; i < config.gridSize && i >= 0; i += direction == Direction.RIGHT ? -1 : 1) {
                for (int j = defaultIndexJ; j < config.gridSize && j >= 0; j += direction == Direction.DOWN ? -1 : 1) {
                    moveTile(i, j, direction, parallelTransition);
                }
            }
        }

        parallelTransition.play();
        board.removeTiles(mergedToBeRemoved);
        createRandomTiles();
        parallelTransition.setOnFinished(e -> {});
        synchronized (tiles) {
            checkGameWon();
            movingTiles = false;
        }
    }

    private void moveTile(int i, int j, Direction direction, ParallelTransition parallelTransition) {
        Location location = locations.get(j * config.gridSize + i);

        Optional<Tile> tile = optionalTile(location);

        if (tile.isEmpty()) {
            return;
        }

        Location farthestEmptyLocation = getLocation(findFarthestLocation(location, direction));

        Location farthestTileLocation = farthestEmptyLocation.nextLocation(direction);
        Optional<Tile> farthestTile = farthestTileLocation.isValid(config.gridSize) ? optionalTile(farthestTileLocation) : null;


        if (farthestTile != null && !farthestTile.isEmpty() && farthestTile.get().isMergeable(tile.get())) {
            mergedToBeRemoved.add(tile.get());
            farthestTile.get().merge(tile.get());
            tiles.put(location, null);
            parallelTransition.getChildren().add(animateExistingTile(tile.get(), farthestTileLocation));
            parallelTransition.getChildren().add(animateMergedTile(tile.get()));
        } else if (locations.get(farthestEmptyLocation.getY() * config.gridSize + farthestEmptyLocation.getX()) != location) {
            tiles.put(locations.get(farthestEmptyLocation.getY() * config.gridSize + farthestEmptyLocation.getX()), tile.get());
            tiles.put(location, null);
            tile.get().setLocation(farthestEmptyLocation);
            parallelTransition.getChildren().add(animateExistingTile(tile.get(), farthestEmptyLocation));
        }
    }

    private Location findFarthestLocation(Location location, Direction direction) {
        Location farthest = location;
        while (true) {
            Location next = farthest.nextLocation(direction);
            if (next.isValid(config.gridSize) && optionalTile(next).isEmpty()) {
                farthest = next;
            } else {
                break;
            }
        }
        return farthest;
    }

    private Timeline animateExistingTile(Tile tile, Location newLocation) {
        var timeline = new Timeline();
        var kvX = new KeyValue(tile.layoutXProperty(),
                newLocation.getLayoutX((int) config.cellSize) - (tile.getMinHeight() / 2) + Config.GAME_BOX_OFFSET_X, Interpolator.EASE_OUT);
        var kvY = new KeyValue(tile.layoutYProperty(),
                newLocation.getLayoutY((int) config.cellSize) - (tile.getMinHeight() / 2) + Config.GAME_BOX_OFFSET_Y, Interpolator.EASE_OUT);

        var kfX = new KeyFrame(ANIMATION_EXISTING_TILE, kvX);
        var kfY = new KeyFrame(ANIMATION_EXISTING_TILE, kvY);

        timeline.getKeyFrames().add(kfX);
        timeline.getKeyFrames().add(kfY);

        return timeline;
    }

    private ScaleTransition animateNewlyAddedTile(Tile tile) {
        final var scaleTransition = new ScaleTransition(ANIMATION_NEWLY_ADDED_TILE, tile);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.setInterpolator(Interpolator.EASE_OUT);
        scaleTransition.setOnFinished(e -> {
            checkGameOver();
        });
        return scaleTransition;
    }

    private void checkGameWon() {
        if (tiles.values().parallelStream().anyMatch(tile -> tile != null && tile.getValue() >= config.target)) {
            gameState.isGameWon = true;
            gameState.isGameOver = true;

            switch (gameState.level) {
                case EASY:
                    gameState.level = Levels.MEDIUM;
                    break;
                case MEDIUM:
                    gameState.level = Levels.HARD;
                    break;
            }

            try {
                Scene scene = new Scene(Main.loadFXML("main-menu"));
                Main.applyStyles(scene);
                board.setGameWon(true);
                Main.applyFadeTransition(scene, stage);
            } catch (IOException e) {
                System.out.println("main-menu.fxml");
            }
        }
    }
    private void checkGameOver() {
        if (tiles.values().parallelStream().noneMatch(Objects::isNull) && mergeMovementsAvailable() == 0) {
            gameState.isGameOver = true;
            board.setGameOver(true);
        }
    }

    private int mergeMovementsAvailable() {
        final var pairsOfMergeableTiles = new AtomicInteger();

        if (tiles.values().parallelStream().anyMatch(Objects::isNull)) {
            return 1;
        }

        for(int i = 0; i < config.gridSize; i++) {
            for(int j = 0; j < config.gridSize; j++) {
                Location location = new Location(i, j);
                Tile tile = tiles.get(getLocation(location));
                if (tile == null) {
                    pairsOfMergeableTiles.incrementAndGet();
                    continue;
                }

                Stream.of(Direction.values()).forEach(direction -> {
                    Location nextLocation = location.nextLocation(direction);
                    if (nextLocation.isValid(config.gridSize)) {
                        Tile nextTile = tiles.get(getLocation(nextLocation));
                        if (nextTile != null && nextTile.isMergeable(tile)) {
                            pairsOfMergeableTiles.incrementAndGet();
                        }
                    }
                });
            }
        }

        return pairsOfMergeableTiles.get();
    }

    private SequentialTransition animateMergedTile(Tile tile) {
        final var scale0 = new ScaleTransition(ANIMATION_MERGED_TILE, tile);
        scale0.setToX(1.2);
        scale0.setToY(1.2);
        scale0.setInterpolator(Interpolator.EASE_IN);

        final var scale1 = new ScaleTransition(ANIMATION_MERGED_TILE, tile);
        scale1.setToX(1.0);
        scale1.setToY(1.0);
        scale1.setInterpolator(Interpolator.EASE_OUT);

        return new SequentialTransition(scale0, scale1);
    }

    private Location getLocation(Location matchLocation) {
        return locations.get(matchLocation.getY() * config.gridSize + matchLocation.getX());
    }

    private Optional<Tile> optionalTile(Location loc) {
        return Optional.ofNullable(tiles.get(getLocation(loc)));
    }

    private boolean addAndAnimateRandomTile() {
        return addAndAnimateRandomTile(0);
    }
    private boolean addAndAnimateRandomTile(int value) {
        ArrayList<Location> randomLocs = new ArrayList<>(locations);
        Collections.shuffle(randomLocs);
        var loc = randomLocs.stream().filter(location -> tiles.get(location) == null).limit(2).iterator();

        if (!loc.hasNext()) {
            return false;
        }
        Tile tile = new Tile(value == 0 ? Math.random() < 0.9 ? 2 : 4 : value, config);
        tile.setOnMouseEntered(e -> tileHoverHandler(tile));
        tile.setOnMouseExited(e -> tileUnhoverHandler(tile));
        tile.setOnMouseClicked(e -> tileClickHandler(e, tile));
        tile.setLocation(loc.next());
        tiles.put(tile.getLocation(), tile);
        board.addTile(tile);

        animateNewlyAddedTile(tile).play();
        return true;
    }

    private void tileHoverHandler(Tile tile){
        if (!isSelecting) return;
        tile.getStyleClass().add("tile-hover");
    }
    private void tileUnhoverHandler(Tile tile){
        tile.getStyleClass().remove("tile-hover");
    }
    private void tileClickHandler(MouseEvent event, Tile tile){

        MouseButton button = event.getButton();

        if (button == MouseButton.PRIMARY) {
            if (!isSelecting || selectedBonus == null) return;

            if (selectedBonus == Modifiers.REMOVE) {
                removeClickHandler(tile);
            }

            if (selectedBonus == Modifiers.X2) {
                x2ClickHandler(tile);
            }
        }
        else {
            tileRightClickHandler(tile);
        }

    }
    private void tileRightClickHandler(Tile tile) {
        tile.getStyleClass().remove("tile-hover");
        selectedBonus = null;
        isSelecting = false;
        gameState.isUsingBonus = false;
    }

    private void createRandomTiles() {
        addAndAnimateRandomTile();
        addAndAnimateRandomTile();
    }

    // =================== Bonus Label Listeners ===================

    private void addBonusLabelListeners(){
        System.out.println(modifiersButtonMap.get(Modifiers.THREETWOADD));

        modifiersButtonMap.get(Modifiers.THREETWOADD).setOnMouseClicked(e -> {
            threeTwoAddBonusHandler();
        });

        modifiersButtonMap.get(Modifiers.SHUFFLE).setOnMouseClicked(e -> {
            shuffleBonusHandler();
        });

        modifiersButtonMap.get(Modifiers.LASTCHANCE).setOnMouseClicked(e -> {
            lastChanceHandler();
        });

        modifiersButtonMap.get(Modifiers.REMOVE).setOnMouseClicked(e -> {
            removeHandler();
        });

        modifiersButtonMap.get(Modifiers.X2).setOnMouseClicked(e -> {
            x2Handler();
        });
    }

    private void threeTwoAddBonusHandler(){
        if (gameState.isGameOver || gameState.isUsingBonus) {
            return;
        }

        if (gameState.modifiersCountMap.get(Modifiers.THREETWOADD) > 0) {
            gameState.isUsingBonus = true;
            boolean isRemovedTime = board.decreaseCounter(30);
            if (isRemovedTime) {
                boolean isAdded = addAndAnimateRandomTile(32);
                if (isAdded) {
                    gameState.modifiersCountMap.put(Modifiers.THREETWOADD, gameState.modifiersCountMap.get(Modifiers.THREETWOADD) - 1);
                    board.updateModifiersCount(Modifiers.THREETWOADD, gameState.modifiersCountMap.get(Modifiers.THREETWOADD));
                }
            }
            gameState.isUsingBonus = false;
        }
    }
    private void shuffleBonusHandler(){
        if (gameState.isGameOver || gameState.isUsingBonus) {
            return;
        }

        if (gameState.modifiersCountMap.get(Modifiers.SHUFFLE) > 0) {
            boolean isRemovedTime = board.decreaseCounter(60);

            if (isRemovedTime) {
                gameState.isUsingBonus = true;
                shuffleTiles();
                gameState.modifiersCountMap.put(Modifiers.SHUFFLE, gameState.modifiersCountMap.get(Modifiers.SHUFFLE) - 1);
                board.updateModifiersCount(Modifiers.SHUFFLE, gameState.modifiersCountMap.get(Modifiers.SHUFFLE));
                gameState.isUsingBonus = false;
            }
        }
    }
    private void lastChanceHandler(){
        if (gameState.isGameOver || gameState.isUsingBonus) {
            return;
        }

        if (gameState.modifiersCountMap.get(Modifiers.LASTCHANCE) > 0) {
            gameState.isUsingBonus = true;
            gameState.modifiersCountMap.put(Modifiers.LASTCHANCE, gameState.modifiersCountMap.get(Modifiers.LASTCHANCE) - 1);
            board.updateModifiersCount(Modifiers.LASTCHANCE, gameState.modifiersCountMap.get(Modifiers.LASTCHANCE));

            for (Location location : locations) {
                Tile tile = tiles.get(location);
                if (tile != null && (tile.getValue() == 2 || tile.getValue() == 4 || tile.getValue() == 8)) {
                    tiles.put(location, null);
                    board.removeTile(tile);
                }
            }

            board.increaseCounter(60 * 3);

            gameState.isUsingBonus = false;
        }
    }

    private void removeHandler(){
        if (gameState.isGameOver || gameState.isUsingBonus || selectedBonus != null){
            return;
        }

        if (gameState.modifiersCountMap.get(Modifiers.REMOVE) > 0) {
            gameState.isUsingBonus = true;
            selectedBonus = Modifiers.REMOVE;
            isSelecting = true;
        }
    }
    private void removeClickHandler(Tile tile){
        boolean isRemovedTime = board.decreaseCounter(60);
        if (isRemovedTime) {
            gameState.isUsingBonus = true;
            tiles.put(tile.getLocation(), null);
            board.removeTile(tile);
            gameState.modifiersCountMap.put(Modifiers.REMOVE, gameState.modifiersCountMap.get(Modifiers.REMOVE) - 1);
            board.updateModifiersCount(Modifiers.REMOVE, gameState.modifiersCountMap.get(Modifiers.REMOVE));
            selectedBonus = null;
            isSelecting = false;
        }
        gameState.isUsingBonus = false;
        tileRightClickHandler(tile);
    }

    private void x2Handler(){
        if (gameState.isGameOver || gameState.isUsingBonus || selectedBonus != null){
            return;
        }

        if (gameState.modifiersCountMap.get(Modifiers.X2) > 0) {
            gameState.isUsingBonus = true;
            selectedBonus = Modifiers.X2;
            isSelecting = true;
        }
    }
    private void x2ClickHandler(Tile tile){

        if (tile.getValue() >= 1024) return;

        boolean isRemovedTime = board.decreaseCounter(60);
        if (isRemovedTime) {
            gameState.isUsingBonus = true;
            tile.setValue(tile.getValue() * 2);
            gameState.modifiersCountMap.put(Modifiers.X2, gameState.modifiersCountMap.get(Modifiers.X2) - 1);
            board.updateModifiersCount(Modifiers.X2, gameState.modifiersCountMap.get(Modifiers.X2));
            selectedBonus = null;
            isSelecting = false;
        }
        gameState.isUsingBonus = false;
        tileRightClickHandler(tile);
        checkGameWon();
    }

    // =============================================================



    private void shuffleTiles(){
        board.removeAllTiles();
        List<Tile> tileList = new ArrayList<>(tiles.values());
        Collections.shuffle(tileList);
        tiles.clear();
        for (int i = 0; i < config.gridSize; i++) {
            for (int j = 0; j < config.gridSize; j++) {
                Location location = locations.get(i * config.gridSize + j);
                tiles.put(location, tileList.get(i * config.gridSize + j));
                var tile = tileList.get(i * config.gridSize + j);
                if (tile != null) {
                    tile.setLocation(location);
                }
            }
        }
        redrawTilesInGameGrid();
    }

    private void redrawTilesInGameGrid() {
        board.removeAllTiles();
        tiles.values().stream().filter(Objects::nonNull).forEach(board::addTile);
    }

    public void restartGame() {
        init();
        startGame();
    }

}
