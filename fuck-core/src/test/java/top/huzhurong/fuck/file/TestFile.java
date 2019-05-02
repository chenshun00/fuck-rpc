package top.huzhurong.fuck.file;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author chenshun00@gmail.com
 * @since 2019/4/30
 */
public class TestFile {
    public static void main(String[] args) throws Exception {
        TestFile file = new TestFile();
        file.testFileWatch();
    }
    public void testFileWatch() throws Exception {
        FileWatchService fileWatchService = new FileWatchService(new String[]{"/Users/chenshun/Desktop/abc.sh"}, path -> {
            Path ppp = Paths.get(path);
            try {
                List<String> stringList = Files.readAllLines(ppp);
                stringList.forEach(System.out::println);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
