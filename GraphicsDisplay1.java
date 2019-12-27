package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.util.ArrayList;


public class GraphicsDisplay1 extends JPanel {
    private Double[][] graphicsData;
    private ArrayList<Double[]> originalData;

    private boolean showAxis = true;
    private boolean showMarkers = true;

    private double minX;//допустим, график функции находится в области, ограниченной прямоугольником с вершинами
    private double maxX;//находятся с помощья поиска макс и мин знач в массиве
    private double minY;
    private double maxY;

    private double scale; // коэффицент масштаба

    private BasicStroke graphicsStroke;//тип пера для черчения линий графика
    private BasicStroke axisStroke;//тип пера для черчения осей
    private BasicStroke markerStroke;//тип пера для черчения маркеров
    private Font axisFont;//шрифт для вывода подписей осей координат

    public GraphicsDisplay1() {//инициализация повторно используемых объектов(стили линий,шрифт)
        setBackground(Color.WHITE);//задний план

        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f, new float[]{9,6,6,9,3}, 0.0f);//перо для рисования графиков

        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);//перо для рисования осей

        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);//перо для рисования контуров маркеров

        axisFont = new Font("Serif", Font.BOLD, 36);//шрифт подписей
        //addMouseMotionListener(new MouseMotionHandler());
        //addMouseListener(new MouseHandler());
    }

    public void showGraphics(Double[][] graphicsData) { //

        this.graphicsData = graphicsData;//сохранить массив точек во внутреннем поле класса

        this.originalData = new ArrayList(graphicsData.length);
        for (Double[] point : graphicsData) {
            Double[] newPoint = new Double[2];
            newPoint[0] = new Double(point[0].doubleValue());
            newPoint[1] = new Double(point[1].doubleValue());
            this.originalData.add(newPoint);
        }

        repaint();//так как любое изменение параметров приводит к изменинию в графике, то метод обновляет изменения
    }

    public void setShowAxis(boolean showAxis) { //отображение осей
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) { //метод модификатор для изменения значений метода маркеров
        this.showMarkers = showMarkers;
        repaint();
    }

    public void paintComponent(Graphics g) { //модульный метод, определяет размер отображаемой области пространства,
//вычисляет коэффиценты масштаба, при необходимости вызывает доп методы отображния
        super.paintComponent(g);//заливка фона

        if (graphicsData == null || graphicsData.length == 0) return;//если данные графика не загружены, ничего не делать
        //начальные границы области определения
        minX = graphicsData [0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        minY = graphicsData[0][1];
        maxY = minY;
        //Мин и макс знач функции
        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1] > maxY) {
                maxY = graphicsData[i][1];
            }
        }
        //определить масштабы
        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);

        scale = Math.min(scaleX, scaleY);//выбрать единый масштаб как минимальный из двух

        if (scale == scaleX) {//корректировка границ согласно выбранному масштабу
            //если за осну был взят масштаб по оси Х
            double yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;

            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale == scaleY) {//если за основу был взят масштаб по оси У

            double xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 2;

            maxX += xIncrement;
            minX -= xIncrement;
        }

        Graphics2D canvas = (Graphics2D) g;// преобразовать экземпляр графикс к графикс 2д
        Stroke oldStroke = canvas.getStroke();//сохранить текущие настройки холста
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (showAxis) paintAxis(canvas);
        paintGraphics(canvas);

        if (showMarkers) paintMarkers(canvas);

        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    protected void paintGraphics(Graphics2D canvas) {//отображение графика

        canvas.setStroke(graphicsStroke);//линия для рис графика

        canvas.setColor(Color.BLUE);//цвет

        GeneralPath graphics = new GeneralPath();//отображение линии графика,как путь от начальной точки по последующим
        for (int i = 0; i < graphicsData.length; i++) {

            Point2D.Double point = xyToPoint(graphicsData[i][0]+0.2, graphicsData[i][1]+0.2);//преобразовать значения х и у в точку на экране поинт
            if (i > 0) {//не первая интерация - ввести линию в точку поинт

                graphics.lineTo(point.getX(), point.getY());
            } else { //первая итерация - установить начало пути в точку поинт

                graphics.moveTo(point.getX(), point.getY());
            }
        }

        canvas.draw(graphics);//отобразить график
    }

    protected void paintMarkers(Graphics2D canvas) { //отображение маркеров

        canvas.setStroke(markerStroke);//специальное перо для черчения контуров маркеров

        canvas.setColor(Color.RED);

        canvas.setPaint(Color.RED);
        //цикл по всем точкам графика
        for (Double[] point : graphicsData) {

            double znach = 0;
            for(int i = 0; i < graphicsData.length; i++){
                znach += graphicsData[i][1];
            }
            znach /= graphicsData.length;
            double chetka = point[1];


            if (2 * znach >  chetka) {

                canvas.setColor(Color.RED);
                canvas.setPaint(Color.RED);
            } else {

                canvas.setColor(Color.BLUE);
                canvas.setPaint(Color.BLUE);
            }
            canvas.setStroke(markerStroke);
            GeneralPath path = new GeneralPath();
            Point2D.Double center = xyToPoint(point[0]+0.2, point[1]+0.2);
            canvas.draw(new Line2D.Double(shiftPoint(center, -8, 8), shiftPoint(center, 8, 8)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 8, 8), shiftPoint(center, 8, -8)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 8, -8), shiftPoint(center, -8, -8)));
            canvas.draw(new Line2D.Double(shiftPoint(center, -8, -8), shiftPoint(center, -8, 8)));
            Point2D.Double corner = shiftPoint(center, 3, 3);
        }
    }

    protected void paintAxis(Graphics2D canvas) { //метод модификатор для изменения значений метода осей

        canvas.setStroke(axisStroke); //особое начертание для осей

        canvas.setColor(Color.BLACK);//цвет осей

        canvas.setPaint(Color.BLACK);//заливка стрелок

        canvas.setFont(axisFont);//подписи к координатным осям делаются особым шрифтом

        FontRenderContext context = canvas.getFontRenderContext();//объект контекста отображения текста - для получения характеристик устройства
        // Шаг 2 - Определить, должна ли быть видна ось Y на графике
        if (minX <= 0.0 && maxX >= 0.0) {
            // Она видна, если левая граница показываемой области minX<=0.0,   // а правая (maxX) >= 0.0
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY-0.2), xyToPoint(0, minY)));// Шаг 2а - ось Y - это линия между точками (0, maxY) и (0, minY)

            GeneralPath arrow = new GeneralPath();//стрелка оси У

            Point2D.Double lineEnd = xyToPoint(0, maxY-0.2);//установить начальную точку ломаной точно на верхний конец оси Х
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());// Установить начальную точку ломаной точно на верхний конец оси Y

            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);// Вести левый "скат" стрелки в точку с относительными

            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());// Вести нижнюю часть стрелки в точку с относительными  // координатами (-10, 0)


            arrow.closePath();//замкнуть треуегольник стрелки
            canvas.draw(arrow);//нарисовать стрелку
            canvas.fill(arrow);//закрасить стрелку

            Rectangle2D bounds = axisFont.getStringBounds("y", context);// Определить, сколько места понадобится для надписи “y”
            Point2D.Double labelPos = xyToPoint(0, maxY-0.2);

            canvas.drawString("y", (float) labelPos.getX() + 10, (float) (labelPos.getY() - bounds.getY()));// Вывести надпись в точке с вычисленными координатами

        }

        canvas.draw(new Line2D.Double(xyToPoint(minX, minY+0.2), xyToPoint(maxX, minY+0.2)));

        GeneralPath arrow = new GeneralPath();

        Point2D.Double lineEnd = xyToPoint(maxX, minY+0.2);
        arrow.moveTo(lineEnd.getX(), lineEnd.getY());

        arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);

        arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);


        arrow.closePath();
        canvas.draw(arrow);
        canvas.fill(arrow);

        Rectangle2D bounds = axisFont.getStringBounds("x", context);
        Point2D.Double labelPos = xyToPoint(maxX, minY+0.2);

        canvas.drawString("x", (float) (labelPos.getX() - bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));

    }

    protected Point2D.Double xyToPoint(double x, double y) { //метод, который реализует алгоритм пересчета из декартовый системы в систему холста

        double deltaX = x - minX; //смещение Х от самой левой точки
        double deltaY = maxY - y;//смещение У от самой верхней точки

        return new Point2D.Double(deltaX * scale, deltaY * scale);
    }

    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY) { //В ряде случаев, например при рисовании осей координат, возникает необходимость получения точек экрана, представляемых экземплярами класса Point2D, отстоящих от заданной точки на некоторое количество пикселов по горизонтали и вертикали. В библиотеке Java 2D не существует стандартного метода, выполняющего подобную операцию, поэтому для еѐ выполнения предназначен метод-помощник shiftPoint(

        Point2D.Double dest = new Point2D.Double();//инициализирует новый экземпляр точки
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);//задать ее координаты как координаты сущ точки+заданные смещения

        return dest;
    }
}