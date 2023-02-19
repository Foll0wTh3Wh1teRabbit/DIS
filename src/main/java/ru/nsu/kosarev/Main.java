package ru.nsu.kosarev;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import ru.nsu.kosarev.crawler.XMLCrawler;
import ru.nsu.kosarev.crawler.UniqueKeyCounter;
import ru.nsu.kosarev.crawler.UserFixCounter;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class Main {

    private static final String PROPERTIES_FILENAME = "/application.properties";

    private static final String ARCHIVE_FILENAME;

    static {
        Properties properties = new Properties();

        try {
            properties.load(Main.class.getResourceAsStream(PROPERTIES_FILENAME));
        } catch (IOException e) {
            log.error("An error has occurred during initialization, exiting with exception");
            throw new RuntimeException(e);
        }

        ARCHIVE_FILENAME = properties.getProperty("dis.archive");
    }

    public static void main(String[] args) throws InterruptedException {
        log.info("10 sec before launch of first task");
        Thread.sleep(10_000);

        new Main().launchFirst();

        log.info("First task is completed, 10 sec before launch of second task");
        Thread.sleep(10_000);

        new Main().launchSecond();
        log.info("Second task is completed");
    }

    private void launchFirst() {
        log.info("Launched first task");

        try (XMLCrawler<List<Map.Entry<String, Integer>>> crawler = new UserFixCounter(newInputStreamInstance())) {
            crawler.crawl();
        } catch (XMLStreamException | IOException e) {
            log.error("An error has occurred during execution first task: {}", e.getLocalizedMessage());
        }
    }

    private void launchSecond() {
        log.info("Launched second task");

        try (XMLCrawler<List<Map.Entry<String, Integer>>> crawler = new UniqueKeyCounter(newInputStreamInstance())) {
            crawler.crawl();
        } catch (XMLStreamException | IOException e) {
            log.error("An error has occurred during execution second task: {}", e.getLocalizedMessage());
        }
    }

    private InputStream newInputStreamInstance() throws IOException {
        return new BZip2CompressorInputStream(new BufferedInputStream(new FileInputStream(ARCHIVE_FILENAME)));
    }

}