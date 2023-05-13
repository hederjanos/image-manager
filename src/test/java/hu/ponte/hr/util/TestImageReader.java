package hu.ponte.hr.util;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TestImageReader {

    public static Map<String, byte[]> readTestImagesFromResources(String path) {
        Map<String, byte[]> bytesMap = new HashMap<>();
        try (Stream<Path> pathStream = Files.list(Paths.get(new ClassPathResource(path).getURI()))) {
            pathStream
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            byte[] bytes = Files.readAllBytes(filePath);
                            bytesMap.putIfAbsent(filePath.getFileName().toString(), bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytesMap;
    }

}
