package ThreadsCrawlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//import sun.jvm.hotspot.runtime.Thread;
import java.lang.Thread;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class Crawler extends Thread {

    private static HashSet<String> hashSet = new HashSet<String>();
    private static AtomicInteger imgNum = new AtomicInteger();
    private static final Integer depth = 2;

    private LinkedList<Thread> threads = new LinkedList<Thread>();

    private Integer mydepth;
    private String myurl;

    public Crawler(String url, Integer depth) {
        mydepth = depth;
        myurl = url;
//        threadsNum.addAndGet(1);//a new thread was created
    }

    public void run(){
        System.out.println("url: " + myurl + " depth = " + mydepth + " i am: "+ Thread.currentThread().getId());


        try{
            hashSet.add(myurl);//for the first thread

            Document doc = Jsoup.connect(myurl).get();
            countImgs(doc);

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String url = link.absUrl("href");
                if(mydepth < depth && add2hash(url))
                {
                    threads.add(new Crawler(url, mydepth + 1));
                    threads.getLast().start(); }
            };
        }catch (IOException e) {System.out.println("an error happened on connecting to url");}
//

        for(Thread thread : threads){
           try {
               thread.join();
           }catch (InterruptedException i){}
        }
    }

    private static synchronized boolean add2hash(String url){
        return hashSet.add(url);
    }

    private void countImgs(Document doc){
        int num = doc.select("img").size();
        imgNum.addAndGet(num);
        System.out.println(Thread.currentThread().getId() + "adding " + num + "total = " +imgNum.get() );
    }
}
