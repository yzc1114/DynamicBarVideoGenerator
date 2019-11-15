package com.yzchnb.dynamicbarvideogenerator.GeneratorUtils;

import java.io.*;

public class VideoTransformer {
    private transient boolean error = false;
    private transient boolean finished = false;

    public String transformVideo(File sourceVideo) throws Exception{
        //待执行的dos命令
        String targetVideoPath = sourceVideo.getAbsolutePath().replaceFirst("\\.avi", "_h264.mp4");
        File targetFile = new File(targetVideoPath);
        if(targetFile.exists()){
            //视频若存在，则删掉，重新转码
            System.out.println("视频已经存在，先将其删除，再转码。");
            if(!targetFile.delete()){
                throw new Exception("已有的视频删除失败。");
            }
        }
        String strCmd = "ffmpeg -i " + sourceVideo.getAbsolutePath() + " -f mp4 -vcodec h264 " + targetVideoPath;
        //启动线程运行进程。等待进程结束
        Thread t = new Thread(() -> {
            try {
                Process process = Runtime.getRuntime().exec(strCmd);
                Thread input = new Thread(() -> {
                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = null;
                    try {
                        while ((line = in.readLine()) != null) {
                            System.out.println("input:"+line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Thread error=new Thread(() -> {
                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line = null;
                    try {
                        while ((line = in.readLine()) != null) {
                            System.out.println("error:"+line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                input.start();
                error.start();
                process.waitFor();
                this.finished = true;
            }catch (IOException | InterruptedException e){
                e.printStackTrace();
                this.finished = true;
                this.error = true;
            }
        });
        t.start();
        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < 240 * 1000){
            if(finished){
                break;
            }
            Thread.sleep(1000);
        }
        if(!this.finished){
            throw new Exception("视频转码超时。");
        }
        if(this.error){
            throw new Exception("视频转码出错。");
        }
        if(!sourceVideo.delete()){
            System.out.println("原视频删除失败。");
        }
        return targetFile.getName();
    }

    public static void main(String[] args) {
        File source = new File("/Users/purchaser/IdeaProjects/dynamic-bar-video-generator/target/classes/resources/movies/1763913741.avi");
        File target = new File("/Users/purchaser/IdeaProjects/dynamic-bar-video-generator/target/classes/resources/movies/1763913741_h264.mp4");
        VideoTransformer videoTransformer = new VideoTransformer();
        try{
            System.out.println(videoTransformer.transformVideo(source));
        }catch (Exception e){
            e.printStackTrace();
        }
//        String strCmd = "ffmpeg -i " + source.getAbsolutePath() + " -f mp4 -vcodec mpeg4 " + target.getAbsolutePath();
//        try{
//            Process process = Runtime.getRuntime().exec(strCmd);
//            System.out.println(process.waitFor());
//        }catch (IOException | InterruptedException e){
//            e.printStackTrace();
//        }
    }
}
