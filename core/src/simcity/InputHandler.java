package simcity;

import java.util.Random;

import javax.swing.text.StyledEditorKit.BoldAction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Gère les entrées de l'utilisateur
 */
public class InputHandler {
    public static final float BASE_CAMERA_SPEED = 7f;
    public static final float CAMERA_ZOOM_SPEED = 0.04f;

    private OrthographicCamera camera;
    private Grid grid;
    private Boolean modifie;
    private Route route;

    public InputHandler(OrthographicCamera camera, Grid grid, Boolean saispas) {
        this.camera = camera;
        this.grid = grid;
        this.modifie = saispas; //trouver un moyen de passer de accueil à jeu
        this.route = new Route();

    }



    public void handleInput(float delta, boolean dansacc) {

        if (!dansacc) {
            if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                dansacc = true;
                this.modifie = true; 
            }
            return ;
        } else { // pour eviter les mouvements si écran d'accueil

        // Camera movement
        float actualCameraSpeed = BASE_CAMERA_SPEED * camera.zoom;  // Calcul de la vitesse de la caméra en fonction du zoom
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            camera.translate(0, actualCameraSpeed);
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            camera.translate(0, -actualCameraSpeed);
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            camera.translate(-actualCameraSpeed, 0);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            camera.translate(actualCameraSpeed, 0);
        }

        
        // Camera zoom
        if (Gdx.input.isKeyPressed(Keys.Q)) {  // Zoom in
            camera.zoom -= CAMERA_ZOOM_SPEED;               //La vraie touche c'est A parce que QWERTY
            if (camera.zoom < 0.1f) {
                camera.zoom = 0.1f;
            }
        }
        if (Gdx.input.isKeyPressed(Keys.E)) {  // Zoom out
            camera.zoom += CAMERA_ZOOM_SPEED;
        }

        // place tiles
        // à terme faudra changer pour que l'ont place les tuiles une par une au lieu de pouvoir laisser appyer avec la souris
        if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Keys.R)) {
            // Coordonnées de la souris sur l'écran
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();

            // Permet de passer en coordonées absolues 
            Vector3 abs = camera.unproject(new Vector3(x, y, 0));

            // Permet de passer en coordonées isométriques puis cast pour avoir les coordonnées de la case
            Vector2 iso = Grid.coordAbsToIso(new Vector2(abs.x, abs.y));
            int col = (int) iso.x;
            int row = (int) iso.y;



            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (route.isConnected(col, row)) { // tiles en fontion du type du batiment choisi
                    // Pour l'instant c'est juste pour tester le placement de tuiles, à termes il faudra faire un système de sélection de tuiles avec un menu et tout
             //switch (){
                    //    case "service publique":
                     //   grid.setTile(new Tile(Textures.publics.get(rand.nextInt(Textures.publics.size()))), col, row, 1);
                      //      
                     //   case "habitat":
                     //   grid.setTile(new Tile(Textures.habitats.get(rand.nextInt(Textures.habitats.size()))), col, row, 1);
                     //       
                     //   case "decoration":
                     //   grid.setTile(new Tile(Textures.deco.get(rand.nextInt(Textures.deco.size()))), col, row, 1);
                     //       
                    //}
                    Random rand = new java.util.Random();
                    grid.setTile(new Tile(Textures.deco.get(rand.nextInt(Textures.deco.size()))), col, row, 1);
                } else {
                    System.out.println("Le batiment doit etre connecte a la route principale.");
                }

            } else if (Gdx.input.isKeyPressed(Keys.R)) { // Pour tester le placement des routes
                if (route.isConnected(col, row)) {
                    grid.setTile(new Tile(Textures.road), col, row, 1);
                    route.addNoeud(new Vector2(col, row));
                } else {
                    System.out.println("La route doit etre connecte a la route principale.");
                }
            } else {
                if (route.contientNoeud(new Vector2(col, row))) {
                    if (route.taille() > 1) {
                        route.delNoeud(new Vector2(col, row));
                        grid.setTile(null, col, row, 1);
                    } else {
                        System.out.println("Ne supprime pas ta dernière route!");
                    }
                    
                } else {
                    grid.setTile(null, col, row, 1);
                }
            }
        }

        }

    }

    public Boolean getBoolean(){
        return this.modifie;
    }

    public void setBoolean(Boolean saispas){
        this.modifie = saispas;
    }
}
