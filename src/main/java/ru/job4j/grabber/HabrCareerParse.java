package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PREFIX = "/vacancies?page=";
    private static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final int PAGE_COUNT = 5;

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) {
        Connection connection = Jsoup.connect(link);
        String description;
        try {
            Document document = connection.get();
            Elements row = document.select(".faded-content__container");
            description = row.first().text();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return description;
    }

    public static void main(String[] args) throws IOException {
        String fullLink = "%s%s%s".formatted(SOURCE_LINK, PREFIX, SUFFIX);
        HabrCareerParse parser = new HabrCareerParse(new HabrCareerDateTimeParser());
        List<Post> parse = parser.parse(fullLink);
        parse.forEach(System.out::println);
    }

    @Override
    public List<Post> parse(String link) throws IOException {
        AtomicInteger id = new AtomicInteger(1);
        List<Post> result = new ArrayList<>();
        for (int pageNumber = 1; pageNumber <= PAGE_COUNT; pageNumber++) {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element vacancyDate = row.select(".vacancy-card__date").first();
                Element linkElement = titleElement.child(0);
                Element linkDate = vacancyDate.child(0);
                String vacancyName = titleElement.text();
                String date = linkDate.attr("datetime");
                String links = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String description = retrieveDescription(links);
                result.add(new Post(id.getAndIncrement(), vacancyName, dateTimeParser.parse(date), links, description));
                System.out.printf("%s %s %s%n %s%n%n", vacancyName, date, links, description);
            });
        }
        return result;
    }
}