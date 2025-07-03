package com.example.onetap;

import com.example.onetap.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class SharedData {
    private static List<NewsItem> newsList = new ArrayList<>();

    public static List<NewsItem> getNewsList() {
        return newsList;
    }

    public static void setNewsList(List<NewsItem> list) {
        newsList = list;
    }

    public static void clearNewsList() {
        newsList.clear();
    }
}
