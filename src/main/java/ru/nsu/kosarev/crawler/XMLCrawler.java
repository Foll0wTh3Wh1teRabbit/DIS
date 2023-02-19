package ru.nsu.kosarev.crawler;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

public abstract class XMLCrawler<T> implements AutoCloseable {

    protected final XMLEventReader xmlEventReader;

    protected XMLCrawler(InputStream inputStream) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();

        xmlEventReader = factory.createXMLEventReader(inputStream);
    }

    @Override
    public void close() {
        try {
            xmlEventReader.close();
        } catch (XMLStreamException ignored) {
        }
    }

    public abstract T crawl() throws XMLStreamException;

}
