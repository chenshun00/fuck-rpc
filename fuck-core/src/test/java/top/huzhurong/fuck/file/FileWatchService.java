package top.huzhurong.fuck.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenshun00@gmail.com
 * @since 2019/4/30
 */
public class FileWatchService implements Runnable {

    protected volatile boolean stopped = false;

    public boolean isStopped() {
        return stopped;
    }

    private final List<String> watchFiles;
    private final List<String> fileCurrentHash;
    private final Listener listener;
    private static final int WATCH_INTERVAL = 1000;
    private MessageDigest md = MessageDigest.getInstance("MD5");
    private Thread thread;

    public FileWatchService(final String[] watchFiles, final Listener listener) throws Exception {
        this.listener = listener;
        this.watchFiles = new ArrayList<>();
        this.fileCurrentHash = new ArrayList<>();

        for (String watchFile : watchFiles) {
            if (new File(watchFile).exists()) {
                this.watchFiles.add(watchFile);
                String hash = hash(watchFile);
                this.fileCurrentHash.add(hash);
            }
        }
        thread = new Thread(this, "文件监控");
        thread.setDaemon(false);
        thread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> thread.interrupt()));
    }

    private String hash(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        md.update(Files.readAllBytes(path));
        byte[] hash = md.digest();
        return UtilAll.bytes2string(hash);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(WATCH_INTERVAL);
                for (int i = 0; i < watchFiles.size(); i++) {
                    String newHash;
                    try {
                        newHash = hash(watchFiles.get(i));
                    } catch (Exception ignored) {
                        continue;
                    }
                    System.out.println(newHash);
                    if (!newHash.equals(fileCurrentHash.get(i))) {
                        fileCurrentHash.set(i, newHash);
                        listener.onChanged(watchFiles.get(i));
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}
