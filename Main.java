package com.company;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
@SuppressWarnings("serial")

public class Main extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private JFileChooser fileChooser = null;

    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showMarkersMenuItem;

    private GraphicsDisplay1 display = new GraphicsDisplay1();

    private boolean fileLoaded = false;

    public Main() {

        super("Построение графиков функций на основе заранее подготовленных файлов");

        setSize(WIDTH, HEIGHT);

        Toolkit kit = Toolkit.getDefaultToolkit();

        setLocation((kit.getScreenSize().width - WIDTH) / 3, (kit.getScreenSize().height - HEIGHT) / 3);

        setExtendedState(MAXIMIZED_BOTH);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);

        Action openGraphicsAction = new AbstractAction("Открыть файл с графиком") {

            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
                    openGraphics(fileChooser.getSelectedFile());
                }
            }
        };

        fileMenu.add(openGraphicsAction);

        JMenu graphicsMenu = new JMenu("График");
        menuBar.add(graphicsMenu);

        Action showAxisAction = new AbstractAction("Показывать оси координат") {

            public void actionPerformed(ActionEvent event) {

                display.setShowAxis(showAxisMenuItem.isSelected());
            }
        };
        showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);

        graphicsMenu.add(showAxisMenuItem);

        showAxisMenuItem.setSelected(true);

        Action showMarkersAction = new AbstractAction("Показывать маркеры точек") {

            public void actionPerformed(ActionEvent event) {

                display.setShowMarkers(showMarkersMenuItem.isSelected());
            }
        };
        showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkersMenuItem);

        showMarkersMenuItem.setSelected(true);

        graphicsMenu.addMenuListener(new GraphicsMenuListener());

        getContentPane().add(display, BorderLayout.CENTER);
    }

    protected void openGraphics(File selectedFile) {
        try {

            double[] dbuf = {1.,1.,2.,2.,3.,3.};
            FileOutputStream fos = new FileOutputStream("d:\\test.txt");

            // create data output stream
            DataOutputStream dos = new DataOutputStream(fos);

            // for each byte in the buffer
            for (double d:dbuf) {

                // write double to the data output stream
                dos.writeDouble(d);
            }

            // force bytes to the underlying stream
            dos.flush();

            DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));

            Double[][] graphicsData = new
                    Double[in.available() / (Double.SIZE / 8) / 2][];

            int i = 0;
            while (in.available() > 0) {

                Double x = in.readDouble();

                Double y = in.readDouble();

                graphicsData[i++] = new Double[]{x, y};
            }

            if (graphicsData != null && graphicsData.length > 0) {

                fileLoaded = true;

                display.showGraphics(graphicsData);
            }

            in.close();
        } catch (FileNotFoundException ex) {

            JOptionPane.showMessageDialog(Main.this, "Указанный файл не найден", "Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE);

            return;
        } catch (IOException ex) {

            JOptionPane.showMessageDialog(Main.this, "Ошибка чтения координат точек из файла", "Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }
    }

    public static void main(String[] args) {

        Main frame = new Main();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private class GraphicsMenuListener implements MenuListener {

        public void menuSelected(MenuEvent e) {

            showAxisMenuItem.setEnabled(fileLoaded);
            showMarkersMenuItem.setEnabled(fileLoaded);
        }

        public void menuDeselected(MenuEvent e) {
        }

        public void menuCanceled(MenuEvent e) {
        }
    }
}