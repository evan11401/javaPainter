package hw2_104403521;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * 姓名: 蔡承延 學號: 104403521 系級: 資管2A
 */
public class paintFrame extends JFrame {

    JPanel buttonList = new JPanel();
    JLabel stateLabel = new JLabel("游標位置：畫布外");
    JPanel paintField = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // 畫出紀錄的圖形
            for (paintObject point : points) {
                g2d.setStroke(point.size);
                g2d.setColor(point.c);
                if (point.isFill) {
                    g2d.fill(point.s);
                } else {
                    g2d.draw(point.s);
                }

            }
//          預覽的圖形
            g2d.setStroke(displayPoints.size);
            g2d.setColor(displayPoints.c);
            if (displayPoints.isFill) {
                g2d.fill(displayPoints.s);
            } else {
                g2d.draw(displayPoints.s);
            }
//          畫出橡皮擦
            for(paintObject easer : easers){
                g2d.setStroke(easer.size);
                g2d.setColor(tmpBackColor);
                g2d.draw(easer.s);
            }

        }
    };
    private final JCheckBox fillJCheckBox;
    private final JRadioButton smallJRadioButton;
    private final JRadioButton midJRadioButton;
    private final JRadioButton largeJRadioButton;
    private final JButton FGJButton;
    private final JButton BGJButton;
    private final JButton cleraJButton;
    private final JButton BackButton;
    private final JButton SaveButton;
    private String[] toolList
            = {"筆刷", "直線", "橢圓形", "矩形", "圓角矩形", "橡皮擦"};
    Shape nu = new Line2D.Double(0, 0, 0, 0);
    private final ArrayList<paintObject> points = new ArrayList<>();//設定ArrayList型態為自訂物件paintObject
    private final ArrayList<paintObject> easers = new ArrayList<>();
    private paintObject displayPoints;//為暫時的圖案 用來預覽圖形
    private final int[] painterSize = {4, 8, 12};
    private int painterSizeSelecter = 0, set = 0;
    private Color tmpBrushColor = Color.black;
    private Color tmpBackColor;
    JFileChooser save;

    public paintFrame() {
        this.save = new JFileChooser();
        this.displayPoints = new paintObject(nu, new BasicStroke(0), tmpBrushColor);
        //設定基本資料
        this.setTitle("小畫家");
        this.setSize(800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setResizable(true);

        //設定Layouts
        buttonList.setLayout(new GridLayout(12, 1));
        add(stateLabel, BorderLayout.SOUTH);
        add(buttonList, BorderLayout.WEST);
        add(paintField, BorderLayout.CENTER);
        paintField.setBackground(Color.white);

        //設定測攔物件
        JLabel ptitle = new JLabel("[繪圖工具]");
        buttonList.add(ptitle);

        JComboBox toolJComboBox = new JComboBox<String>(toolList);
        toolJComboBox.setMaximumRowCount(8);
        buttonList.add(toolJComboBox);

        JLabel toolSizeTitle = new JLabel("[筆刷大小]");
        buttonList.add(toolSizeTitle);

        smallJRadioButton = new JRadioButton("小", true);
        midJRadioButton = new JRadioButton("中", false);
        largeJRadioButton = new JRadioButton("大", false);
        buttonList.add(smallJRadioButton);
        buttonList.add(midJRadioButton);
        buttonList.add(largeJRadioButton);
        ButtonGroup sizeGroup = new ButtonGroup();
        sizeGroup.add(smallJRadioButton);
        sizeGroup.add(midJRadioButton);
        sizeGroup.add(largeJRadioButton);

        fillJCheckBox = new JCheckBox("填滿");
        buttonList.add(fillJCheckBox);

        FGJButton = new JButton("前景色");
        BGJButton = new JButton("背景色");
        FGJButton.setBackground(Color.black);
        BGJButton.setBackground(Color.white);
        tmpBackColor = Color.white;
        cleraJButton = new JButton("清除畫面");
        BackButton = new JButton("上一步");
        SaveButton = new JButton("存檔");
        buttonList.add(FGJButton);
        buttonList.add(BGJButton);
        buttonList.add(cleraJButton);
        buttonList.add(BackButton);
        buttonList.add(SaveButton);

        //combobox的監聽器
        toolJComboBox.addItemListener(
                new ItemListener() // anonymous inner class
        {
            // handle JComboBox event
            @Override
            public void itemStateChanged(ItemEvent event) {
                // determine whether item selected
                if (event.getStateChange() == ItemEvent.SELECTED) {
//                    JOptionPane.showMessageDialog(null, "你選擇了" + toolJComboBox.getSelectedItem(), "訊息", JOptionPane.PLAIN_MESSAGE);
                    set = toolJComboBox.getSelectedIndex();
                }
            }
        }
        );

        //radiobutton的監聽器
        smallJRadioButton.addItemListener(
                new RadioButtonHandler());
        midJRadioButton.addItemListener(
                new RadioButtonHandler());
        largeJRadioButton.addItemListener(
                new RadioButtonHandler());

        //checkbox的監聽器
        CheckBoxHandler handler = new CheckBoxHandler();
        fillJCheckBox.addActionListener(handler);

        //button的監聽器
        ButtonHandler btnhandler = new ButtonHandler();
        FGJButton.addActionListener(btnhandler);
        BGJButton.addActionListener(btnhandler);
        cleraJButton.addActionListener(btnhandler);
        BackButton.addActionListener(btnhandler);
        SaveButton.addActionListener(btnhandler);

        //滑鼠事件的監聽器
        MouseHandler moshandler = new MouseHandler();
        paintField.addMouseListener(moshandler);
        paintField.addMouseMotionListener(moshandler);
    }

    private class RadioButtonHandler implements ItemListener {

        String[] sel = {"小", "中", "大"};

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (e.getSource() == smallJRadioButton) {
//                    JOptionPane.showMessageDialog(null, "你選擇了 小", "訊息", JOptionPane.PLAIN_MESSAGE);
                    painterSizeSelecter = 0;
                } else if (e.getSource() == midJRadioButton) {
//                    JOptionPane.showMessageDialog(null, "你選擇了 中", "訊息", JOptionPane.PLAIN_MESSAGE);
                    painterSizeSelecter = 1;
                } else if (e.getSource() == largeJRadioButton) {
//                    JOptionPane.showMessageDialog(null, "你選擇了 大", "訊息", JOptionPane.PLAIN_MESSAGE);
                    painterSizeSelecter = 2;
                }
            }
        }
    }
    private boolean isFill = false;

    private class CheckBoxHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            if (fillJCheckBox.isSelected()) {
                isFill = true;
            } else {
                isFill = false;
            }
        }
    }

    private class ButtonHandler implements ActionListener {
        // handle button event

        @Override
        public void actionPerformed(ActionEvent event) {
//            JOptionPane.showMessageDialog(null, "你選擇了 " + event.getActionCommand(), "訊息", JOptionPane.PLAIN_MESSAGE);
            //清除畫面 背景改為white 前景改為black
            if (event.getSource() == cleraJButton) {
                points.clear();
                displayPoints = new paintObject(nu, new BasicStroke(0), tmpBrushColor);
                tmpBrushColor = Color.black;
                FGJButton.setBackground(tmpBrushColor);
                tmpBackColor = Color.white;
                paintField.setBackground(tmpBackColor);
                BGJButton.setBackground(tmpBackColor);
                repaint();
            }
//           呼叫JColorChooser
            if (event.getSource() == FGJButton) {
                tmpBrushColor = JColorChooser.showDialog(paintFrame.this, "顏色選擇器", tmpBrushColor);
                if (tmpBrushColor == null) {
                    tmpBrushColor = Color.black;
                }
                FGJButton.setBackground(tmpBrushColor);
            }
            if (event.getSource() == BGJButton) {
                tmpBackColor = JColorChooser.showDialog(paintFrame.this, "顏色選擇器", tmpBackColor);
                paintField.setBackground(tmpBackColor);
                BGJButton.setBackground(tmpBackColor);
                repaint();
            }
//            上一步功能 直接移除ArrayList最後一項
            if (event.getSource() == BackButton) {
                points.remove(points.remove(points.size() - 1));
                repaint();
            }
//            存檔功能 用JFileChooser選擇資料夾 並用ImageIO把JPanel存成masterpiece.jpg
            if (event.getSource() == SaveButton) {
                save.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                BufferedImage saveImg = new BufferedImage(paintField.getWidth(), paintField.getHeight(), BufferedImage.TYPE_INT_RGB);
                paintField.paint(saveImg.createGraphics());
                int result = save.showDialog(SaveButton, "確認");
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        System.out.println(save.getSelectedFile().getAbsolutePath());
                        ImageIO.write(saveImg, "jpg", new File(save.getSelectedFile().getAbsolutePath() + "\\masterpiece.jpg"));
                    } catch (IOException ex) {
                        Logger.getLogger(paintFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    int x1, x2, y1, y2;//抓取滑鼠點
    final float dash1[] = {10};//虛線間距          

    //抽象方法須全部實作    
    private class MouseHandler implements MouseListener,
            MouseMotionListener {

        @Override
        public void mouseEntered(MouseEvent e) {
            stateLabel.setText("游標位置：畫布內");
        }

        @Override
        public void mouseExited(MouseEvent e) {
            stateLabel.setText("游標位置：畫布外");

        }

        @Override
//        用此方法來記錄 直線 隨圓形 矩形 圓角矩形
        public void mouseReleased(MouseEvent e) {
            switch (set) {
                case 1://直線
                    x2 = e.getX();
                    y2 = e.getY();
                    Shape line2 = new Line2D.Double(x1, y1, x2, y2);
                    if (isFill) {
                        paintObject strline = new paintObject(line2, new BasicStroke(painterSize[painterSizeSelecter]), tmpBrushColor);
                        points.add(strline);
                    } else {
                        final BasicStroke dashed = new BasicStroke(painterSize[painterSizeSelecter],
                                BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_MITER,
                                10.0f, dash1, 0.0f);
                        paintObject strline = new paintObject(line2, dashed, tmpBrushColor);
                        points.add(strline);
                    }

                    break;
                case 2://隨圓形
                    Ellipse2D c = new Ellipse2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
                    paintObject circle = new paintObject(c, new BasicStroke(painterSize[painterSizeSelecter]), tmpBrushColor, isFill);
                    points.add(circle);
                    break;
                case 3://矩形
                    Rectangle2D r = new Rectangle2D.Double(Math.min(x1,
                            x2), Math.min(y1, y2), Math.abs(x2 - x1),
                            Math.abs(y2 - y1));
                    paintObject rectangle = new paintObject(r, new BasicStroke(painterSize[painterSizeSelecter]), tmpBrushColor, isFill);
                    points.add(rectangle);
                    break;
                case 4://圓角矩形
                    RoundRectangle2D rr = new RoundRectangle2D.Double(Math.min(x1,
                            x2), Math.min(y1, y2), Math.abs(x2 - x1),
                            Math.abs(y2 - y1), 30, 30);
                    paintObject RoundRectangle = new paintObject(rr, new BasicStroke(painterSize[painterSizeSelecter]), tmpBrushColor, isFill);
                    points.add(RoundRectangle);
                    break;
            }
            displayPoints = new paintObject(nu, new BasicStroke(0), tmpBrushColor);
            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            x2 = e.getX();
            y2 = e.getY();
            switch (set) {
                case 0://畫點                    
                    Shape line = new Line2D.Double(x1, y1, x2, y2);
                    paintObject tmppoint = new paintObject(line, new BasicStroke(painterSize[painterSizeSelecter]), tmpBrushColor);
                    points.add(tmppoint);
                    x1 = x2;
                    y1 = y2;
                    break;
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            x1 = e.getX();
            y1 = e.getY();
        }

        @Override
//      用此方法來記錄筆跡 其他為預覽圖案之用途 在拉動時會有圖案出來
        public void mouseDragged(MouseEvent e) {
            x2 = e.getX();
            y2 = e.getY();
            switch (set) {
                case 0:
                    Shape line = new Line2D.Double(x1, y1, x2, y2);
                    paintObject tmppoint = new paintObject(line, new BasicStroke(painterSize[painterSizeSelecter]), tmpBrushColor);
                    points.add(tmppoint);
                    x1 = x2;
                    y1 = y2;
                    break;
                case 1:
                    Shape line2 = new Line2D.Double(x1, y1, x2, y2);
                    if (isFill) {
                        paintObject strline = new paintObject(line2, new BasicStroke(painterSize[painterSizeSelecter]), tmpBrushColor);
                        displayPoints = strline;
                    } else {
                        final BasicStroke dashed = new BasicStroke(painterSize[painterSizeSelecter],
                                BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_MITER,
                                10.0f, dash1, 0.0f);
                        paintObject strline = new paintObject(line2, dashed, tmpBrushColor);
                        displayPoints = strline;
                    }
                    break;
                case 2:
                    Ellipse2D c = new Ellipse2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
                    paintObject circle = new paintObject(c, new BasicStroke(painterSize[painterSizeSelecter]), tmpBrushColor, isFill);
                    displayPoints = circle;
                    break;
                case 3:
                    Rectangle2D r = new Rectangle2D.Double(Math.min(x1,
                            x2), Math.min(y1, y2), Math.abs(x2 - x1),
                            Math.abs(y2 - y1));
                    paintObject rectangle = new paintObject(r, new BasicStroke(painterSize[painterSizeSelecter]), tmpBrushColor, isFill);
                    displayPoints = rectangle;
                    break;
                case 4:
                    RoundRectangle2D rr = new RoundRectangle2D.Double(Math.min(x1,
                            x2), Math.min(y1, y2), Math.abs(x2 - x1),
                            Math.abs(y2 - y1), 30, 30);
                    paintObject RoundRectangle = new paintObject(rr, new BasicStroke(painterSize[painterSizeSelecter]), tmpBrushColor, isFill);
                    displayPoints = RoundRectangle;
                    break;
                case 5:
                    Shape es = new Line2D.Double(x1, y1, x2, y2);
                    paintObject easer = new paintObject(es, new BasicStroke(painterSize[painterSizeSelecter]));
                    easers.add(easer);
                    x1 = x2;
                    y1 = y2;
                    break;

            }
            repaint();

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            stateLabel.setText(String.format("游標位置：(%d, %d)",
                    e.getX(), e.getY()));
        }

    }

}
