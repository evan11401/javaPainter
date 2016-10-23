/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hw1_104403521;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

/**
 *
 * @author 蔡承延
 */
public class paintObject{
    int x,y;
    Shape s;
    Color c;
    BasicStroke size;
    boolean emp = true ,isFill = false;
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
    public boolean isEmpty(){
        return emp;
    }
    
    
}
