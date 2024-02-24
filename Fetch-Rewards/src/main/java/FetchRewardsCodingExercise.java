import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class FetchRewardsCodingExercise {
    public static void main(String[] args) {

        List<item> items = fetchData();

        List<item> filteredAndSortedItems = items.stream()
                .filter(item -> item.name != null && !item.name.isEmpty())
                .sorted(Comparator.comparing(item::getListId).thenComparing(item::getId))
                .collect(Collectors.toList());

        Map<Integer, List<item>> groupedItems = filteredAndSortedItems.stream()
                .collect(Collectors.groupingBy(item::getListId));

        for (Map.Entry<Integer, List<item>> entry : groupedItems.entrySet()) {
            System.out.println("ListId: " + entry.getKey());
            for (item item : entry.getValue()) {
                System.out.println("  - " + item.name);
            }
        }

    }

    private static List<item> fetchData(){
        try {
            URL url = new URL("https://fetch-hiring.s3.amazonaws.com/hiring.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            return parseResponse(response.toString());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<item> parseResponse(String response)
    {
        List<item> items = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(response);

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int id = jsonObject.getInt("id");
            int listId = jsonObject.getInt("listId");
            String name = jsonObject.optString("name");
            items.add(new item(id, listId, name));
        }

        return items;
    }


    private static class item {
        int id;
        int listId;
        String name;

        public item(int id, int listId, String name) {
            this.id =id;
            this.listId = listId;
            this.name = name;
        }

        public int getId(){
            return id;
        }

        public int getListId() {
            return listId;
        }

        public String getName() {
            return name;
        }
    }
}
