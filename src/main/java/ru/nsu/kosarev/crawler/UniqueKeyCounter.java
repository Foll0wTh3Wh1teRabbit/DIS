package ru.nsu.kosarev.crawler;

import lombok.extern.slf4j.Slf4j;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UniqueKeyCounter extends XMLCrawler<List<Map.Entry<String, Integer>>> {

    public UniqueKeyCounter(InputStream inputStream) throws XMLStreamException {
        super(inputStream);
    }

    @Override
    public List<Map.Entry<String, Integer>> crawl() throws XMLStreamException {
        Map<String, Integer> keysToNodes = new HashMap<>();

        final String tagQualifier = "tag";
        final String keyQualifier = "k";

        while (xmlEventReader.hasNext()) {
            XMLEvent event = xmlEventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();

                if (startElement.getName().getLocalPart().equals(tagQualifier)) {
                    Attribute key = startElement.getAttributeByName(new QName(keyQualifier));

                    if (key != null) {
                        keysToNodes.compute(key.getValue(), (k, v) -> v == null ? 1 : v + 1);
                    }
                }
            }
        }

        List<Map.Entry<String, Integer>> mapAsList = new ArrayList<>(keysToNodes.entrySet().stream().toList());
        mapAsList.sort((e1, e2) -> -e1.getValue().compareTo(e2.getValue()));

        mapAsList.forEach((entry) -> log.info(entry.getKey() + " " + entry.getValue()));

        return mapAsList;
    }

}
