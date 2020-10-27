package com.laioffer.job.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.job.entity.Item;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class GitHubClient {
    private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
    private static final String DEFAULT_KEYWORD = "developer";

    public List<Item> search(double lat, double lon, String keyword){
        if (keyword==null){
            keyword= DEFAULT_KEYWORD;
        }
        try {
            keyword = URLEncoder.encode(keyword,"UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        String url= String.format(URL_TEMPLATE,keyword,lat,lon);

        CloseableHttpClient httpClient= HttpClients.createDefault();
        ResponseHandler<List<Item>> responseHandler= response -> {
            if (response.getStatusLine().getStatusCode()!=200){
                return Collections.emptyList();
            }
            HttpEntity entity= response.getEntity();
            if (entity==null){
                return Collections.emptyList();
            }
            ObjectMapper mapper= new ObjectMapper();
            List<Item> items= Arrays.asList(mapper.readValue(entity.getContent(),Item[].class));
            extractKeywords(items);
            return items;
        };
        try {
            return httpClient.execute(new HttpGet(url), responseHandler);
        }catch (IOException e){
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private void extractKeywords(List<Item> items) {
        MonkeyLearnClient monkeyLearnClient = new MonkeyLearnClient();

        List<String> descriptions = items.stream()
                .map(Item::getDescription)
                .collect(Collectors.toList());

        List<Set<String>> keywordList = monkeyLearnClient.extract(descriptions);
        for (int i = 0; i < items.size(); i++) {
            if (items.size()!=keywordList.size() && i>keywordList.size()-1){
                Set<String> tmp= new HashSet<>();
                tmp.add("python");
                tmp.add("development");
                tmp.add("SDE");
                items.get(i).setKeywords(tmp);
                continue;
            }
            items.get(i).setKeywords(keywordList.get(i));
        }
    }



}
