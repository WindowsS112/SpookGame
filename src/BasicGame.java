import nl.saxion.app.SaxionApp;
import nl.saxion.app.interaction.GameLoop;
import nl.saxion.app.interaction.KeyboardEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class BasicGame implements GameLoop {

    private final int screenWidth = 800;
    private final int screenHeight = 600;
    private int playerX = 100; // Spelerpositie (x)
    private int playerY = 500; // Spelerpositie (y)
    private int playerWidth = 50;
    private int playerHeight = 50;
    private int playerSpeed = 10;

    private int groundY = 550; // Hoogte van de grond
    private boolean isJumping = false; // Springstatus
    private int jumpVelocity = -20;
    private int gravity = 1;
    private int verticalVelocity = 0;

    private final ArrayList<Rectangle> obstacles = new ArrayList<>();
    private final Random random = new Random();
    private int score = 0;

    // Camera shake variabelen
    private int cameraOffsetX = 0;
    private int cameraOffsetY = 0;
    private int shakeDuration = 0;
    private int maxShakeIntensity = 10;

    public static void main(String[] args) {
        SaxionApp.startGameLoop(new BasicGame(), 800, 600, 40);
    }

    @Override
    public void init() {
        SaxionApp.printLine("Gebruik spatie om te springen. Vermijd obstakels!");
    }

    @Override
    public void loop() {
        SaxionApp.clear();

        // Update camera shake
        updateCameraShake();

        // Update speler
        updatePlayer();

        // Update obstakels
        updateObstacles();

        // Teken alles
        drawPlayer();
        drawObstacles();

        // Toon score
        SaxionApp.setFill(Color.BLACK);
        SaxionApp.drawText("Score: " + score, 10 + cameraOffsetX, 20 + cameraOffsetY, 20);
    }

    @Override
    public void keyboardEvent(KeyboardEvent keyboardEvent) {
        if (keyboardEvent.getKeyCode() == KeyboardEvent.VK_SPACE && !isJumping) {
            isJumping = true;
            verticalVelocity = jumpVelocity;
            triggerCameraShake();
         }
    }

    private void updatePlayer() {
        if (isJumping) {
            playerY += verticalVelocity;
            verticalVelocity += gravity;

            if (playerY >= groundY - playerHeight) {
                playerY = groundY - playerHeight;
                isJumping = false;
            }
        }
    }

    private void drawPlayer() {
        SaxionApp.setFill(Color.BLUE);
        SaxionApp.drawRectangle(playerX + cameraOffsetX, playerY + cameraOffsetY, playerWidth, playerHeight);
    }

    private void updateObstacles() {
        if (random.nextInt(100) < 5) {
            // Voeg een nieuw obstakel toe
            int obstacleWidth = 50;
            int obstacleHeight = 50;
            Rectangle newObstacle = new Rectangle(screenWidth, groundY - obstacleHeight, obstacleWidth, obstacleHeight);

            // Controleer of het nieuwe obstakel overlapt met bestaande obstakels
            for (Rectangle obstacle : obstacles) {
                if (newObstacle.intersects(obstacle)) {
                    // Verplaats het nieuwe obstakel naar een andere positie
                    newObstacle.x = screenWidth + random.nextInt(100);
                }
            }

            obstacles.add(newObstacle);
        }

        // Beweeg obstakels en verwijder buiten beeld
        Iterator<Rectangle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Rectangle obstacle = iterator.next();
            obstacle.x -= 5;

            // Controleer op botsing
            if (playerX < obstacle.x + obstacle.width &&
                    playerX + playerWidth > obstacle.x &&
                    playerY < obstacle.y + obstacle.height &&
                    playerY + playerHeight > obstacle.y) {
                System.exit(0);
                // TEST GITHUB UPDATE 1.1
            }

            // Verwijder obstakels buiten het scherm
            if (obstacle.x + obstacle.width < 0) {
                iterator.remove();
                score++;
            }
        }
    }

    private void drawObstacles() {
        SaxionApp.setFill(Color.RED);
        for (Rectangle obstacle : obstacles) {
            SaxionApp.drawRectangle(obstacle.x + cameraOffsetX, obstacle.y + cameraOffsetY, obstacle.width, obstacle.height);
        }
    }

    private void updateCameraShake() {
        if (shakeDuration > 0) {
            // Genereer willekeurige offsetwaarden
            cameraOffsetX = random.nextInt(maxShakeIntensity * 2 + 1) - maxShakeIntensity;
            cameraOffsetY = random.nextInt(maxShakeIntensity * 2 + 1) - maxShakeIntensity;

            // Verminder de shake-tijd
            shakeDuration--;
        } else {
            // Reset de camera naar normale positie
            cameraOffsetX = 0;
            cameraOffsetY = 0;
        }
    }

    private void triggerCameraShake() {
        shakeDuration = 20; // Schudt voor 20 frames
    }

    @Override
    public void mouseEvent(nl.saxion.app.interaction.MouseEvent mouseEvent) {
        // Geen muisinteractie nodig
    }
}
