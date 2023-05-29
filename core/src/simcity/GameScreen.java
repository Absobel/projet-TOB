package simcity;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.Timer;
import java.util.TimerTask;

/**
 * L'écran principal du jeu.
 */
public class GameScreen extends ScreenAdapter {
    public static final int WIDTH = 320 * 4; // 16:9 aspect ratio
    public static final int HEIGHT = 180 * 4;
    public static final double eps = 10 ^ (-3);

    private SpriteBatch batch;
    private Viewport viewport;
    private menuHUD hudStage;
    // private Label labell;

    private OrthographicCamera camera;
    private IsometricRenderer renderer;
    private InputHandler inputHandler;
    private Boolean estdansGame, paye;
    private Gestion gestion;

    private java.util.List<Texture> skyCourant;

    private Time timer;
    private float dernierPayement;
    private CycleJN cycleJN;
    private Afficher aff;

    public GameScreen(SpriteBatch batch, boolean dedans) {
        this.batch = batch;
        this.estdansGame = dedans;
        this.paye = true;
        this.renderer = new IsometricRenderer();
        this.gestion = new Gestion(1000, 10, 10, 50, 0, 0);
        hudStage = new menuHUD(viewport, batch, this.gestion, this.renderer.getGrid());
        aff = new Afficher(this.gestion);

    }

    @Override
    public void show() { // Called when this screen becomes the current screen for a Game.
        InputMultiplexer multiplexer = new InputMultiplexer(); // gerer differentes entrees
        camera = new OrthographicCamera(WIDTH, HEIGHT);
        camera.position.set(WIDTH / 2, HEIGHT / 2, 0);
        camera.zoom = 4f;

        viewport = new FitViewport(WIDTH, HEIGHT, camera);

        inputHandler = new InputHandler(camera, renderer.getGrid(), false);
        // Nouveau stage
        multiplexer.addProcessor((InputProcessor) hudStage); // Ajouter le Stage de menuHUD

        // la musique
        // Accueil.MUSIQUE.changerMusique();

        // le menu
        Gdx.input.setInputProcessor(multiplexer);

        // le chrono
        this.timer = new Time(Gdx.graphics.getDeltaTime());
        dernierPayement = timer.getTime();

        // cycle jour nuit
        this.cycleJN = new CycleJN();
    }

    @Override
    public void resize(int width, int height) { // Called when the Application is resized.
        viewport.update(width, height);
    }

    @Override
    public void hide() { // Called when this screen is no longer the current screen for a Game.
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Set the clear color to black.
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen.
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        int effectiveScreenWidth = (int) Math.floor(WIDTH * camera.zoom) + 100; // Add 100 to avoid black borders
        int effectiveScreenHeight = (int) Math.floor(HEIGHT * camera.zoom) + 100;

        skyCourant = cycleJN.updateSky(timer.getTime());
        for (Texture skyPart : skyCourant) {
            batch.draw(skyPart, camera.position.x - effectiveScreenWidth / 2,
                    camera.position.y - effectiveScreenHeight / 2, effectiveScreenWidth, effectiveScreenHeight);
        }

        // temps
        timer.updateTime(Gdx.graphics.getDeltaTime());
        renderer.draw(batch);

        // gagner de l'argent en fonction du temps
        if (Math.abs(dernierPayement - timer.getTime()) > (float) 2) {
            gestion.impots();
            dernierPayement = timer.getTime();
        }

        batch.end();
        // gestion.miseAJour(); // nouveau mais je n'arrive pas a refiare passer à 0 les
        // ressources
        aff.maj(); // maj
        aff.draw();

        TextureRegion texture;
        if (hudStage.getBatRessources() != null) {
            texture = hudStage.getBatRessources().getTexture();
            System.out.println("texture : " + texture);
            inputHandler.handleInput(Gdx.graphics.getDeltaTime(), this.estdansGame, texture);
            // hudStage.setBatRessources(null);
        } else {
            inputHandler.handleInput(Gdx.graphics.getDeltaTime(), this.estdansGame, Textures.publics.get(0));
            System.out.println("texture : ici ");
        }

        if (inputHandler.getBatPose()) {
            hudStage.setBatRessources(null);
            inputHandler.resetBatPose();
        }

        // bool rajouté pour écran d'accueil
        camera.update();

        hudStage.act(delta);
        hudStage.draw();

        /*
         * affichagescore
         * affichageScore aff = new affichageScore(manager);
         * aff.updateGame();
         * aff.afficher(batch,font);
         */

    }

    @Override
    public void dispose() { // Called when this screen should release all resources.
        renderer.dispose();
        // hudStage.dispose();
    }

    public void setEstdansGame(Boolean estdansGame) {
        this.estdansGame = estdansGame;
    }

    public Boolean getEstdansGame() {
        return estdansGame;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public void setBooldeInput(InputHandler inputHandler, boolean bool) {
        inputHandler.setBoolean(bool);
    }

}
