package com.mousemover.gui;

import com.mousemover.mover.Mover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static javax.swing.BoxLayout.Y_AXIS;

public class Gui extends JFrame {

    public static final Color GREEN = new Color(62, 112, 58);
    public static final Color RED = new Color(122, 16, 16);
    private final Mover mover = new Mover();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private JTextField timePeriod;
    private ButtonGroup timePeriodButtonGroup;
    private ScheduledFuture<?> scheduledFuture;
    private JLabel statusLabel;
    private JLabel timeRemainingLabel;

    public Gui(String name) {

        setTitle(name);
        final int width = 500;
        final int height = 300;
        setPreferredSize(new Dimension(width, height));
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int)screenSize.getWidth()/2 - width/2, (int)screenSize.getHeight()/2 - height/2);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setUpComponents();

        startDisplayUpdaterThread();
    }

    private void startDisplayUpdaterThread() {
        executorService.scheduleAtFixedRate(this::updateDisplay, 500, 100, TimeUnit.MILLISECONDS);
    }

    private void setUpComponents() {

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, Y_AXIS));
        add(mainPanel);


        final Component inputFields = getInputFields();
        mainPanel.add(inputFields);


        final JPanel buttonsPanel = new JPanel();
        buttonsPanel.setPreferredSize(new Dimension(300, 100));
        buttonsPanel.setLayout(new FlowLayout(Y_AXIS));
//        buttonsPanel.setBorder(new BevelBorder(RAISED));

        final JButton start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.out.println("Starting...");
                try {
                    start(getTimePeriod(), timePeriodUnit());
                } catch (AWTException awtException) {
                    awtException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });

        final JButton stop = new JButton("Stop");
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.out.println("Stopping...");
//                try {
                    stop();
//                } catch (AWTException awtException) {
//                    awtException.printStackTrace();
//                } catch (InterruptedException interruptedException) {
//                    interruptedException.printStackTrace();
//                }
            }
        });

        start.setPreferredSize(new Dimension(150, 100));
        stop.setPreferredSize(new Dimension(150, 100));
        buttonsPanel.add(start);
        buttonsPanel.add(stop);
        mainPanel.add(buttonsPanel);


        final JPanel infoPanel = new JPanel();
        infoPanel.add(new JLabel("Status:"));
        statusLabel = new JLabel(Status.STOPPED.name());
        statusLabel.setOpaque(true);
        infoPanel.add(statusLabel);
        infoPanel.add(new JLabel("Time Remaining:"));
        timeRemainingLabel = new JLabel("--:--:--");
        infoPanel.add(timeRemainingLabel);
        mainPanel.add(infoPanel);

        pack();
    }

    private void stop() {
        mover.stop();

        //cancel timer
        scheduledFuture.cancel(true);

        updateDisplay();
    }

    private void start(final int timePeriod, final TimeUnit timePeriodUnit) throws InterruptedException, AWTException {

        System.out.println("Running for " + timePeriod + " " + timePeriodUnit.name().toLowerCase());

        //set timer to kill the mover thread
        final int timePeriodInSeconds = timePeriodUnit == TimeUnit.SECONDS ? timePeriod : timePeriod * 60;
        scheduledFuture = executorService.schedule(stopCommand(), timePeriodInSeconds, TimeUnit.SECONDS);

        //start moving
        mover.start();

        updateDisplay();
    }

    private Runnable stopCommand() {
        return mover::stop;
    }

    private int getTimePeriod() {
        return Integer.parseInt(timePeriod.getText());
    }

    private JPanel getInputFields() {
        final JPanel inputFields = new JPanel();


        inputFields.add(new JLabel("Run for:"));
        timePeriod = new JTextField("20");
        timePeriod.setPreferredSize(new Dimension(120, 20));
        inputFields.add(timePeriod);

        final JRadioButton secondsButton = new JRadioButton("seconds");
        secondsButton.setActionCommand(TimeUnit.SECONDS.name());
        secondsButton.setSelected(true);
        inputFields.add(secondsButton);
        final JRadioButton minutesButton = new JRadioButton("minutes");
        minutesButton.setActionCommand(TimeUnit.MINUTES.name());
        inputFields.add(minutesButton);

        timePeriodButtonGroup = new ButtonGroup();
        timePeriodButtonGroup.add(secondsButton);
        timePeriodButtonGroup.add(minutesButton);

        final JPanel settingsPanel = new JPanel();
        final URL resource = getClass().getClassLoader().getResource("images/settings.png");
        final ImageIcon image = new ImageIcon(resource);
        final JLabel settingsWrapper = new JLabel(image);
        settingsWrapper.setPreferredSize(new Dimension(31, 29));
//        settingsPanel.addListener
        settingsPanel.add(settingsWrapper);
        settingsPanel.add(new JLabel("Settings"));
//        inputFields.add(settingsPanel);

        return inputFields;
    }

    private TimeUnit timePeriodUnit() {
        return TimeUnit.valueOf(timePeriodButtonGroup.getSelection().getActionCommand());
    }

    private void updateDisplay() {
//        System.out.println("updating display");
        try {
            final Status status = mover.isRunning() ? Status.RUNNING : Status.STOPPED;
            statusLabel.setText(status.name());
            statusLabel.setBackground(status == Status.RUNNING ? GREEN : RED);

            boolean taskNoLongerRunning = scheduledFuture == null || scheduledFuture.isCancelled() || scheduledFuture.isDone();
            final long timeRemaining = taskNoLongerRunning ? 0 : scheduledFuture.getDelay(TimeUnit.SECONDS);
            final long secondsRemaining = timeRemaining % 60;
            final long minutesRemaining = (timeRemaining / 60) % 60;
            final long hoursRemaining = timeRemaining / 60 / 60;

            final String timeRemainingDisplay = String.format("%02d:%02d:%02d", hoursRemaining, minutesRemaining, secondsRemaining);
            timeRemainingLabel.setText(timeRemainingDisplay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
