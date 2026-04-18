package util;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片资源管理器 - 管理游戏中的所有图片资源
 */
public class ImageManager {
    private static ImageManager instance;
    private Map<String, BufferedImage> images;
    
    // 默认图片路径配置
    private static final String IMAGE_DIR = "resources/images";
    
    private ImageManager() {
        images = new HashMap<>();
        preloadImages();
    }
    
    public static ImageManager getInstance() {
        if (instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }
    
    /**
     * 预加载所有图片资源
     */
    private void preloadImages() {
        // 定义需要加载的图片
        loadImage("background", "background.png");
        loadImage("player1", "player1.png");
        loadImage("player2", "player2.png");
        loadImage("ball", "ball.png");
        loadImage("racket", "racket.png");
        loadImage("net", "net.png");
        loadImage("court", "court.png");
        loadImage("cloud", "cloud.png");
        loadImage("title", "title.png");
        loadImage("button", "button.png");
        loadImage("particle_hit", "particle_hit.png");
        loadImage("particle_smash", "particle_smash.png");
    }
    
    /**
     * 加载单个图片
     */
    public boolean loadImage(String name, String filename) {
        String path = IMAGE_DIR + "/" + filename;
        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                BufferedImage image = ImageIO.read(filePath.toFile());
                if (image != null) {
                    images.put(name, image);
                    System.out.println("已加载图片: " + path);
                    return true;
                }
            } else {
                System.out.println("图片文件不存在: " + path);
            }
        } catch (IOException e) {
            System.err.println("加载图片失败 " + path + ": " + e.getMessage());
        }
        return false;
    }
    
    /**
     * 获取图片
     */
    public BufferedImage getImage(String name) {
        return images.get(name);
    }
    
    /**
     * 检查图片是否存在
     */
    public boolean hasImage(String name) {
        return images.containsKey(name);
    }
    
    /**
     * 获取缩放后的图片
     */
    public Image getScaledImage(String name, int width, int height) {
        BufferedImage original = images.get(name);
        if (original != null) {
            return original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        }
        return null;
    }
    
    /**
     * 重新加载所有图片
     */
    public void reloadAll() {
        images.clear();
        preloadImages();
    }
    
    /**
     * 获取已加载图片数量
     */
    public int getLoadedCount() {
        return images.size();
    }
}
