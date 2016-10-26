/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hw2_104403521;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

/**
 *
 * @author 蔡承延
 * 自訂物件
 */
public class paintObject{    
    Shape s;//存放形狀
    Color c;//存放顏色
    BasicStroke size;//存放筆刷大小 虛線與否
    boolean emp = true ,isFill = false;//判斷是否填滿
    public paintObject(Shape s,BasicStroke size,Color c){
            this.s = s;            
            this.size = size;
            this.c = c;
            this.emp = false;
    }
    public paintObject(Shape s,BasicStroke size,Color c,boolean isFill){
            this.s = s;            
            this.size = size;
            this.c = c;
            this.emp = false;
            this.isFill = isFill;
    }
    public paintObject(Shape s,BasicStroke size){
            this.s = s;            
            this.size = size;
            this.emp = false;
    }
    public boolean isEmpty(){
        return emp;
    }
    
    
}
