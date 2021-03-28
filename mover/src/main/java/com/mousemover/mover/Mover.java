package com.mousemover.mover;

import java.awt.*;

public class Mover {

    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static final int WIDTH = (int) (screenSize.getWidth() - 20);
    private static final int HEIGHT = (int) (screenSize.getHeight() - 20);
    private int x = WIDTH/2;
    private int y = HEIGHT/2;
    private int xModifier = 1;
    private int yModifier = 1;

    private boolean running;
    private Thread moverThread;

    public void start() throws AWTException, InterruptedException {

        running = true;
        final long waitPeriod = 300;

        moverThread = new Thread(() -> {

            Robot robot = null;
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }

            int i = 0;
            while (true) {
                if (!running) {
                    System.out.println("Finished");
                    return;
                }

                if (i % 10 < 5) {
                    robot.mouseMove(x, y);
                    updateCoordinates();
                }

                try {
                    Thread.sleep(waitPeriod);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                i++;
            }

        });

        moverThread.start();
    }

    private void updateCoordinates() {
        final int delta = 10;
        x += (delta * xModifier);
        y += (delta * yModifier);
        if (x > WIDTH || x < 20) {
            xModifier *= -1;
        }
        if (y > HEIGHT || y < 20) {
            yModifier *= -1;
        }
    }

    public void stop() {
        System.out.println("Stopping");
        running = false;
        moverThread = null;
    }

    public boolean isRunning() {
        return running;
    }
}
