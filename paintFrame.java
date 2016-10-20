package hw1_104403521;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author 蔡承延
 */
public class paintFrame extends JFrame {

    JPanel buttonList = new JPanel();
    JLabel stateLabel = new JLabel("游標位置：畫布外");
    JPanel paintField = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g); // clears drawing area
            Graphics2D g2d = (Graphics2D) g;

            // draw all 
            for (paintObject point : points) {
                g2d.setStroke(new BasicStroke(point.size));
                g2d.setColor(point.c);
                g2d.draw(point.s);
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
    private String[] toolList
            = {"筆刷", "直線", "橢圓形", "矩形", "圓角矩形"};

    private final ArrayList<paintObject> points = new ArrayList<>();
    private final int[] painterSize = {4, 8, 12};
    private int painterSizeSelecter = 0, set = 0;
    private Color tmpBrushColor = Color.black;
    

    public paintFrame() {
        //設定基本資料
        this.setTitle("小畫家");
        this.setSize(800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setResizable(true);

        //設定Layouts
        buttonList.setLayout(new GridLayout(10, 1));
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
        BGJButton.setBackground(Color.BLACK);
        cleraJButton = new JButton("清除畫面");
        buttonList.add(FGJButton);
        buttonList.add(BGJButton);
        buttonList.add(cleraJButton);

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

    private class CheckBoxHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            if (fillJCheckBox.isSelected()) {
                System.out.println("你選擇了填滿");
            } else {
                System.out.println("你取消了填滿");
            }
        }
    }

    private class ButtonHandler implements ActionListener {
        // handle button event

        @Override
        public void actionPerformed(ActionEvent event) {
//            JOptionPane.showMessageDialog(null, "你選擇了 " + event.getActionCommand(), "訊息", JOptionPane.PLAIN_MESSAGE);
            if (event.getSource() == cleraJButton) {
                points.clear();
                repaint();
            }
            if(event.getSource() == FGJButton){
                tmpBrushColor = JColorChooser.showDialog(paintFrame.this, "顏色選擇器", tmpBrushColor);
                if(tmpBrushColor == null){
                    tmpBrushColor = Color.black;
                }
                FGJButton.setBackground(tmpBrushColor);
            }
            if(event.getSource() == BGJButton){
                
            }
        }
    }
    int x1,x2,y1,y2;//抓取滑鼠點
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
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            x1 = e.getX();
            y1 = e.getY();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            x2 = e.getX();
            y2 = e.getY();
            switch (set) {
                case 0:       
                    Shape line = new Line2D.Double(x1,y1,x2,y2);
                    paintObject tmppoint = new paintObject(line, painterSize[painterSizeSelecter],tmpBrushColor);
                    points.add(tmppoint);
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
