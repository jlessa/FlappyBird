package com.jeffersonlessa.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] birds;
    private Texture background;
    private Texture gameOver;
    private Texture smallPipeTop;
    private Texture smallPipeBottom;

    private int game = 0;
    private float screenWidth;
    private float screenHeight;

    private float birdVariation = 0;
    private float gravity = 0;
    private float birdInitialPositionY;
    private float birdInitialPositionX;

    private float pipePositionX;
    private float pipePositionY;
    private float spaceBetweenPipes;
    private float spaceBetweenPipesRandom;

    private Random random;

    private BitmapFont bitmapFont;
    private BitmapFont msgGameOver;

    private int score = 0;
    private boolean scoreCheck;

    private Circle birdCircle;
    private Rectangle pipeBottomRectangle;
    private Rectangle pipeTopRectangle;
    private ShapeRenderer shapeRenderer;

    //Camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 760;
    private final float VIRTUAL_HEIGTH = 1024;


    @Override
    public void create() {

        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGTH / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGTH, camera);

        batch = new SpriteBatch();

        birds = new Texture[3];
        birds[0] = new Texture("passaro1.png");
        birds[1] = new Texture("passaro2.png");
        birds[2] = new Texture("passaro3.png");
        gameOver = new Texture("game_over.png");

        smallPipeTop = new Texture("cano_topo.png");
        smallPipeBottom = new Texture("cano_baixo.png");

        background = new Texture("fundo.png");
        screenWidth = VIRTUAL_WIDTH;
        screenHeight = VIRTUAL_HEIGTH;
        birdInitialPositionY = screenHeight / 2;
        birdInitialPositionX = 120;

        pipePositionX = screenWidth - 150;
        pipePositionY = screenHeight / 2;
        spaceBetweenPipes = 300;

        random = new Random();

        bitmapFont = new BitmapFont();
        bitmapFont.setColor(Color.WHITE);
        bitmapFont.getData().setScale(6);

        msgGameOver = new BitmapFont();
        msgGameOver.setColor(Color.WHITE);
        msgGameOver.getData().setScale(3);


        scoreCheck = false;

        birdCircle = new Circle();
        pipeBottomRectangle = new Rectangle();
        pipeTopRectangle = new Rectangle();
        shapeRenderer = new ShapeRenderer();

    }

    @Override
    public void render() {

        //clear frames
        camera.update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        birdVariation += Gdx.graphics.getDeltaTime() * 8;
        if (birdVariation >= 3) birdVariation = 0;

        if (game == 0) {
            if (Gdx.input.justTouched())
                game = 1;
        } else if (game == 1) {

            pipePositionX -= Gdx.graphics.getDeltaTime() * 400;
            gravity++;

            //make the bird jump
            if (Gdx.input.justTouched()) gravity = -20;

            if (birdInitialPositionY > 0 || gravity < 20) birdInitialPositionY -= gravity;

            //Check if pipes is out of the screen
            if (pipePositionX < -smallPipeBottom.getWidth()) {
                pipePositionX = screenWidth;
                spaceBetweenPipesRandom = random.nextInt(400) - 200;
                scoreCheck = false;
            }

            //Score
            if (pipePositionX < birdInitialPositionX) {
                if (!scoreCheck) {
                    score++;
                    scoreCheck = true;

                }

            }

        } else {
            gravity++;
            if (birdInitialPositionY > 0 || gravity < 20) birdInitialPositionY -= gravity;
            if (Gdx.input.justTouched() && birdInitialPositionY <= 0) {
                game = 0;
                score = 0;
                gravity = 0;
                birdInitialPositionY = screenHeight / 2;
                birdInitialPositionX = 120;
                pipePositionX = screenWidth - 150;
                pipePositionY = screenHeight / 2;

            }
        }

        //Configure Camera
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(background, 0, 0, screenWidth, screenHeight);

        batch.draw(smallPipeTop, pipePositionX, pipePositionY + spaceBetweenPipes / 2 + spaceBetweenPipesRandom);
        batch.draw(smallPipeBottom, pipePositionX, pipePositionY - smallPipeBottom.getHeight() - spaceBetweenPipes / 2 + spaceBetweenPipesRandom);


        batch.draw(birds[(int) birdVariation], birdInitialPositionX, birdInitialPositionY);

        bitmapFont.draw(batch, String.valueOf(score), screenWidth / 2, screenHeight - 100);

        if (game == 2) {
            batch.draw(gameOver, screenWidth / 2 - gameOver.getWidth() / 2, screenHeight / 2);
            msgGameOver.draw(batch, "Toque para Reiniciar o Jogo", screenWidth / 2 - gameOver.getWidth() / 2 - 60, screenHeight / 2 - gameOver.getHeight() / 2);
        }

        batch.end();

        birdCircle.set(birdInitialPositionX + birds[0].getWidth() / 2, birdInitialPositionY + birds[0].getHeight() - 20, (birds[0].getWidth() / 2) - 10);

        pipeBottomRectangle.set(pipePositionX,
                pipePositionY - smallPipeBottom.getHeight() - spaceBetweenPipes / 2 + spaceBetweenPipesRandom,
                smallPipeBottom.getWidth(),
                smallPipeBottom.getHeight());

        pipeTopRectangle.set(pipePositionX,
                pipePositionY + spaceBetweenPipes / 2 + spaceBetweenPipesRandom,
                smallPipeTop.getWidth(),
                smallPipeTop.getHeight());


        //Geometric forms
        /*
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(pipeBottomRectangle.x, pipeBottomRectangle.y, pipeBottomRectangle.width, pipeBottomRectangle.height);
        shapeRenderer.rect(pipeTopRectangle.x, pipeTopRectangle.y, pipeTopRectangle.width, pipeTopRectangle.height);
        shapeRenderer.end();
        */

        //Test colliding
        if (Intersector.overlaps(birdCircle, pipeTopRectangle) ||
                Intersector.overlaps(birdCircle, pipeBottomRectangle) ||
                birdInitialPositionY <= 0 ||
                birdInitialPositionY >= screenHeight)
            game = 2;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
