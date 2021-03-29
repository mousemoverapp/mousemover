package com.mousemover.mover;

import java.awt.*;

public class Mover {

    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static final int WIDTH = (int) (screenSize.getWidth());
    private static final int HEIGHT = (int) (screenSize.getHeight());
    private int x;
    private int y;
    private int xModifier = 1;
    private int yModifier = 1;

    private boolean running;
    private Thread moverThread;

    public void start() throws AWTException, InterruptedException {

        running = true;
        final long waitPeriod = 50;

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

                if (i % 30 < 15) {
                    updateCoordinates();
                    robot.mouseMove(x, y);
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
        final int delta = 2;

        final Point cursorLocation = MouseInfo.getPointerInfo().getLocation();
        x = (int) cursorLocation.getX();
        y = (int) cursorLocation.getY();

        x += (delta * xModifier);
        y += (delta * yModifier);
        if (x > WIDTH || x < 0) {
            xModifier *= -1;
        }
        if (y > HEIGHT || y < 0) {
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
