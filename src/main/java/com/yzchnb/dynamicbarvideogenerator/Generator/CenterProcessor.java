package com.yzchnb.dynamicbarvideogenerator.Generator;
import com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity.GeneratorConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Entity.GeneratorEntity.Frame;
import com.yzchnb.dynamicbarvideogenerator.Entity.GeneratorEntity.Line;
import com.yzchnb.dynamicbarvideogenerator.Logger.Logger;
import org.jim2mov.core.*;
import org.jim2mov.utils.MovieUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;


public class CenterProcessor implements ImageProvider, FrameSavedListener {
    //并发链表队列，用于给视频生成器提供数据
    private ConcurrentLinkedQueue<BufferedImage> bufferedImages;
    //生成器所需全部配置内容
    private GeneratorConfiguration generatorConfiguration;
    //
    private boolean dataEnd = false;
    //总的帧数
    private int frameCount;
    //已生成帧数
    private int savedFrameNum;
    //视频生成地址。
    private String videoSavingPath;
    //视频名字
    private String videoName;
    //连续帧生成器，向它提供数据和配置文件，返回可以直接生成图片的帧数据
    private TransitionFrameGenerator transitionFrameGenerator;
    //图片生成器，向它提供帧数据，能够得到一帧图片
    private ImageGenerator imageGenerator;
    //用于生成视频的线程
    private Thread savingMovieThread;
    private Frame lastFrame = null;

    //构造函数
    public CenterProcessor(GeneratorConfiguration generatorConfiguration, int frameCount, String generateDir){
        this.generatorConfiguration = generatorConfiguration;
        this.frameCount = frameCount;
        this.bufferedImages = new ConcurrentLinkedQueue<>();
        this.transitionFrameGenerator = new TransitionFrameGenerator(generatorConfiguration);
        this.imageGenerator = new ImageGenerator(generatorConfiguration);
        this.videoName = generatorConfiguration.hashCode() + ".avi";
        this.videoSavingPath =generateDir+this.videoName;
        this.startSavingMovie();
    }

    //获取一行的数据，持有它，直到行数到达转化的
    public void consumeDataLine(Line line){
        //TODO 获取一行的数据。持有并转化。
        while(bufferedImages.size()>10);
        Frame currFrame = transitionFrameGenerator.generateFrame(lastFrame, line);
        bufferedImages.offer(imageGenerator.generateImage(currFrame));
        lastFrame = currFrame;
    }

    private void startSavingMovie() {
        savingMovieThread = new Thread(() -> {
            DefaultMovieInfoProvider dmip = new DefaultMovieInfoProvider(videoSavingPath);
            //TODO 读取配置，获得长和宽。
            // 设置帧频率
            dmip.setFPS(generatorConfiguration.getUserInputConfiguration().getFPS());
            // 设置帧数--一张图片一帧
            dmip.setNumberOfFrames(frameCount);
            // 设置视频高度
            dmip.setMWidth(generatorConfiguration.getUserInputConfiguration().getWidth());
            // 设置视频宽度
            dmip.setMHeight(generatorConfiguration.getUserInputConfiguration().getHeight());
            try{
                new Jim2Mov(this, dmip, this).saveMovie(MovieInfoProvider.TYPE_AVI_MJPEG);
            }catch (MovieSaveException e){
                e.printStackTrace();
                Logger.log(e.getMessage());
            }

        });
        savingMovieThread.start();
    }

    public void dispose() {
        dataEnd = true;
    }

    public String waitResult() throws Exception{
        savingMovieThread.join();
        VideoTransformer videoTransformer = new VideoTransformer();
        File videoSavedFile = new File(videoSavingPath);
        if(!videoSavedFile.exists()){
            throw new Exception("视频生成失败。");
        }
        return videoTransformer.transformVideo(new File(videoSavingPath));
    }
    public Double getRate(){
        return (double)savedFrameNum/frameCount;
    }
    @Override
    public void frameSaved(int i) {
        savedFrameNum = i;
        Logger.log("saved frame " + i);
    }

    @Override
    public byte[] getImage(int i) {
        BufferedImage bufferedImage;
        while((bufferedImage = bufferedImages.poll()) == null){
            if(dataEnd){
                return new byte[0];
            }
        }
        try{
            return MovieUtils.bufferedImageToJPEG(bufferedImage, 1.0f);
        }catch (IOException e){
            e.printStackTrace();
            return new byte[0];
        }
    }

}
