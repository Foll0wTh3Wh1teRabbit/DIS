package ru.nsu.kosarev.crawler;

import lombok.extern.slf4j.Slf4j;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class UserFixCounter extends XMLCrawler<List<Map.Entry<String, Integer>>> {

    public UserFixCounter(InputStream inputStream) throws XMLStreamException {
        super(inputStream);
    }

    @Override
    public List<Map.Entry<String, Integer>> crawl() throws XMLStreamException {
        Map<String, Integer> usersToChanges = new HashMap<>();

        final String nodeQualifier = "node";
        final String userQualifier = "user";

        while (xmlEventReader.hasNext()) {
            XMLEvent event = xmlEventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();

                if (startElement.getName().getLocalPart().equals(nodeQualifier)) {
                    Attribute user = startElement.getAttributeByName(new QName(userQualifier));

                    if (user != null) {
                        usersToChanges.compute(user.getValue(), (k, v) -> v == null ? 1 : v + 1);
                    }
                }
            }
        }

        List<Map.Entry<String, Integer>> mapAsList = new ArrayList<>(usersToChanges.entrySet().stream().toList());
        mapAsList.sort((e1, e2) -> -e1.getValue().compareTo(e2.getValue()));

        mapAsList.forEach((entry) -> log.info(entry.getKey() + " " + entry.getValue()));

        return mapAsList;
    }

}
