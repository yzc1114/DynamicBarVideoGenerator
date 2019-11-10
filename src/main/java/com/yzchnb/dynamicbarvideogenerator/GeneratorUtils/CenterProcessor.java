package com.yzchnb.dynamicbarvideogenerator.GeneratorUtils;

import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.GeneratorConfiguation;
import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Frame;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Line;
import org.jim2mov.core.*;
import org.jim2mov.utils.MovieUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;


public class CenterProcessor implements ImageProvider, FrameSavedListener {
    //一次性生成帧的数量
    private int linesSize;
    //存储一次性生成帧数量的集合
    private ArrayList<Line> lines;
    //并发链表队列，用于给视频生成器提供数据
    private ConcurrentLinkedQueue<BufferedImage> bufferedImages;
    //生成器所需全部配置内容
    private GeneratorConfiguation generatorConfiguation;
    //用户输入的配置内容
    private UserInputConfiguration userInputConfiguration;
    //标志视频生成是否结束
    private boolean finished = false;
    //表示视频生成是否成功
    private boolean success = false;
    //总的帧数
    private int frameCount;
    //获取资源文件
    private static final ResourceBundle resourceBundle;
    static{
        resourceBundle = ResourceBundle.getBundle("application");
    }
    //视频生成地址。
    private String videoSavingPath;
    //连续帧生成器，向它提供数据和配置文件，返回可以直接生成图片的帧数据
    private TransitionFrameGenerator transitionFrameGenerator;
    //图片生成器，向它提供帧数据，能够得到一帧图片
    private ImageGenerator imageGenerator;
    //用于生成视频的线程
    private Thread savingMovieThread;

    //构造函数
    public CenterProcessor(GeneratorConfiguation generatorConfiguation, ArrayList<String> types, int frameCount){
        this.generatorConfiguation = generatorConfiguation;
        this.userInputConfiguration = generatorConfiguation.getUserInputConfiguration();
        this.linesSize = (int)userInputConfiguration.getTi() * userInputConfiguration.getFPS();
        this.lines = new ArrayList<>(linesSize);
        this.frameCount = frameCount;
        this.bufferedImages = new ConcurrentLinkedQueue<>();
        this.transitionFrameGenerator = new TransitionFrameGenerator(generatorConfiguation);
        this.imageGenerator = new ImageGenerator(generatorConfiguation);
        this.videoSavingPath = resourceBundle.getString("generatedMoviePath") + generatorConfiguation.hashCode() + ".mov";
        this.startSavingMovie();
    }

    //获取一行的数据，持有它，直到行数到达转化的
    public void consumeDataLine(Line line){
        //TODO 获取一行的数据。持有并转化。
        lines.add(line);
        if(lines.size() == linesSize){
            //到一次性生成帧的数量，传递给过度帧生成器
            ArrayList<Frame> frames = transitionFrameGenerator.generateFrames(lines);
            lines.clear();
            for (int i = 0; i < frames.size(); i++) {
                bufferedImages.offer(imageGenerator.generateImage(frames.get(i)));
            }
        }
    }

    private void startSavingMovie() {
        savingMovieThread = new Thread(() -> {
            DefaultMovieInfoProvider dmip = new DefaultMovieInfoProvider(videoSavingPath);
            //TODO 读取配置，获得长和宽。
            // 设置帧频率
            dmip.setFPS(generatorConfiguation.getUserInputConfiguration().getFPS());
            // 设置帧数--一张图片一帧
            dmip.setNumberOfFrames(frameCount);
            // 设置视频高度
            dmip.setMWidth(generatorConfiguation.getUserInputConfiguration().getWidth());
            // 设置视频宽度
            dmip.setMHeight(generatorConfiguation.getUserInputConfiguration().getHeight());
            try{
                new Jim2Mov(this, dmip, this).saveMovie(MovieInfoProvider.TYPE_AVI_MJPEG);
            }catch (MovieSaveException e){
                e.printStackTrace();
                success = false;
                finished = true;
            }
            success = true;
            finished = true;

        });
        savingMovieThread.run();
    }

    public String waitResult() {
        try{
            savingMovieThread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
            return null;
        }
        if(!finished || !success){
            return null;
        }
        return videoSavingPath;
    }

    @Override
    public void frameSaved(int i) {
        System.out.println("saved frame " + i);
    }

    @Override
    public byte[] getImage(int i) {
        BufferedImage bufferedImage;
        while((bufferedImage = bufferedImages.poll()) == null);
        int j=1+1;
        try{
            return MovieUtils.bufferedImageToJPEG(bufferedImage, 1.0f);
        }catch (IOException e){
            e.printStackTrace();
            return new byte[0];
        }
    }
}
