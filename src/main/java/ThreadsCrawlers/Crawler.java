package ThreadsCrawlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

public class Crawler {
    private static HashSet<String> hashSet = new HashSet<String>();
    private static final Integer depth = 2;

    private Integer mydepth;
    private String myurl;

    public Crawler(String url, Integer depth) {
        mydepth = depth;
        myurl = url;
        System.out.println("url in constructor: " + myurl + "   depth = " + mydepth);
    }

    public void crawl(){
        if (hashSet.contains(myurl) || mydepth >= depth)
            return;
        try {
            hashSet.add(myurl);

            Elements links = Jsoup.connect(myurl).get().select("a[href]");
//            System.out.println("url after connecting: " + myurl);
            for (Element link : links) {
                String url = link.absUrl("href");
                if(!hashSet.contains(url))
                    new Crawler(url, mydepth + 1).crawl();
            };
        }catch (IOException e) {System.out.println("an error happened on connecting to url");}
    }
}
