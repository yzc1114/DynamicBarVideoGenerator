package com.yzchnb.dynamicbarvideogenerator.GeneratorUtils;

import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.GeneratorConfiguration;
import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Bar;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Frame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;

public class ImageGenerator {
    private GeneratorConfiguration generatorConfiguration;
    private UserInputConfiguration userInputConfiguration;
    public ImageGenerator(GeneratorConfiguration generatorConfiguration){
        this.generatorConfiguration = generatorConfiguration;
        this.userInputConfiguration = generatorConfiguration.getUserInputConfiguration();
    }
    public BufferedImage generateImage(Frame frame) {

        //TODO 读取配置，获得长和宽
        int width = userInputConfiguration.getWidth();
        int height = userInputConfiguration.getHeight();
        //TODO 此处的frame内部的bars应该是已经排好序的，index为0的bar为最高的bar
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        //TODO *****************************************************
        //TODO 以下数据的计算应放置在配置读取处，读取完配置后立刻算出这些数据
        //TODO 因为这些数据应该由连续帧生成器使用，并计算出bar的position。

        double marginHorizontal = width / 15.0;
        double marginVertical = height / 20.0;
        int originX = (int)marginHorizontal;
        int originY = height / 6;

        // 柱状图宽高
        double heightOfBarChart =height-originY-marginVertical;
        double widthOfBarChart = width - originX -marginHorizontal;
        //TODO 先计算出每个柱子的宽度，需要通过读配置，来获得同屏的柱子的数量。
        int numOfBarsInChart = userInputConfiguration.getNumOfBarsInChart(); //假定现在是十个柱子同屏。
        //TODO 规定柱子的宽度 = 整个图的高度 / 同屏柱子数量 * ( 5/6 )
        int widthOfBar = (int)(heightOfBarChart / numOfBarsInChart * ( 5.0/6.0 ));
        //TODO 到此为止
        //TODO *****************************************************

        //获得前numOfBarsInChart个bar
        java.util.List<Bar>bars = frame.getBars();

        //需要将bars重新排序，按照一定的顺序绘图，不会导致帧的重叠。
        bars.sort(Comparator.comparing(Bar::getTypeName));



        //定义数值字体的大小
        int value_font_size_px=widthOfBar*2/3;
        int value_font_size_pt=value_font_size_px*4/3;
        Font value_font=new Font("黑体",Font.PLAIN,value_font_size_pt);
        graphics.setFont(value_font);
        FontMetrics v_fm=graphics.getFontMetrics();
        int value_max_width_px=0;
        for(Bar bar :bars){
            value_max_width_px=Math.max(value_max_width_px,v_fm.stringWidth(bar.getValue().toString()));
        }

        //定义名字字体的大小
        int name_font_size_px=widthOfBar*2/3;
        int name_font_size_pt=name_font_size_px*4/3;
        Font name_font=new Font("黑体",Font.PLAIN,name_font_size_pt);
        graphics.setFont(name_font);
        FontMetrics n_fm=graphics.getFontMetrics();
        int name_max_width_px=0;
        for(Bar bar :bars){
            name_max_width_px=Math.max(name_max_width_px,n_fm.stringWidth(bar.getTypeName()));
        }


        //调整画布位置以适应柱形图相应名字信息
        originX=Math.max(originX,name_max_width_px+width/40);
        marginHorizontal=Math.max(marginHorizontal,value_max_width_px);
        widthOfBarChart=width-originX-marginHorizontal;

        //TODO 接下来只需要按照顺序绘图
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0,0, width, height);

        graphics.setColor(Color.BLACK);
        //graphics.drawRect(originX,originY,(int)widthOfBarChart,(int)heightOfBarChart);
        //绘制时间
        int title_font_size_px=height/20;
        int title_font_size_pt=title_font_size_px*4/3;
        Font title_font=new Font("黑体",Font.BOLD,title_font_size_pt);
        graphics.setFont(title_font);
        String title=userInputConfiguration.getTitle();
        FontMetrics title_fm=graphics.getFontMetrics();
        graphics.drawString(title, (width-title_fm.stringWidth(title))/2, (int)(title_font_size_px*1.2));

        int time_font_size_px=height/30;
        int time_font_size_pt=time_font_size_px*4/3;
        Font time_font=new Font("黑体",Font.PLAIN,time_font_size_pt);
        graphics.setFont(time_font);
        String time_str=frame.getTimeStr();
        FontMetrics t_fm=graphics.getFontMetrics();
        graphics.drawString(time_str, (width-t_fm.stringWidth(time_str))/2, (time_font_size_px*4));

        for(int i=0;i<bars.size();++i){
            Bar bar=bars.get(i);
            if(bar.getPosition()>1){
                continue;
            }
            int real_bar_position=(int)(bar.getPosition()*heightOfBarChart)+originY;
            int bar_length=(int)(widthOfBarChart*(bar.getValue()-frame.getBaseDegree())/(frame.getPeekDegree()-frame.getBaseDegree()));

            graphics.setColor(Color.BLACK);
            graphics.setFont(name_font);
            graphics.drawString(bar.getTypeName(),(originX-n_fm.stringWidth(bar.getTypeName()))/2,real_bar_position+(name_font_size_px+widthOfBar)/2);

            graphics.setColor(bar.getColor());
            graphics.fillRect(originX,real_bar_position,bar_length,Math.min(widthOfBar,originY+(int)heightOfBarChart-real_bar_position));

            graphics.setFont(value_font);
            graphics.drawString(bar.getValue().toString(),originX+bar_length,real_bar_position+(value_font_size_px+widthOfBar)/2);
        }
        graphics.dispose();

        return image;
    }
}
