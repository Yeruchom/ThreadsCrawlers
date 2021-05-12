package ThreadsCrawlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.lang.Thread;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class Crawler extends Thread {

    //a static HashSet<String> for all threads, keeping the urls that ware crawled already
    private static HashSet<String> hashSet = new HashSet<String>();

    //the AtomicInteger that holds the number of images found (global for all threads).
    private AtomicInteger imgNum;

    //a list of all threads I create, to join them and know they finished
    private LinkedList<Thread> threads = new LinkedList<Thread>();

    //private local variables for depth and url
    private Integer mydepth;
    private String myurl;

    /**
     *
     * @param url the url it is crawling
     * @param depth the depth that is left to crawl. notice! it starts from 2 (depths to crawl) and goes down to 0!
     * @param imgCount the AtomicInteger that holds the number of images found (global for all threads).
     */
    public Crawler(String url, Integer depth, AtomicInteger imgCount) {
        mydepth = depth;
        myurl = url;
        imgNum = imgCount;
    }


    /**
     * overriding the run() function that executes when calling start()
     */
    public void run(){

        System.out.println("Starting thread for url: " + myurl + " depth ledft: " + mydepth);

//            try {
//                sleep((long) Math.random() *10000);
//            }catch (InterruptedException I){};

        try{
            hashSet.add(myurl);//for the first thread

            Document doc = Jsoup.connect(myurl).get();
            countImgs(doc);

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String url = link.absUrl("href");
                if(mydepth > 0 && add2hash(url)) //check if left depth and if the url wasn't yet crawled
                {
                    threads.add(new Crawler(url, mydepth -1, imgNum));
                    threads.getLast().start(); }
            };
        }catch (IOException e) {System.out.println("an error happened by connecting to " + myurl + " error: " + e.getMessage());}

        //join all my threads, to know that they finished
        for(Thread thread : threads){
           try {
               thread.join();
               System.out.println("end of thread for url " + myurl);
           }catch (InterruptedException i){}
        }
    }

    //a static synchronized function for adding url to the static HashSet
    private static synchronized boolean add2hash(String url){
        return hashSet.add(url);
    }

    //add to the AtomicInteger (that is shared with all threads) the number of img found
    private void countImgs(Document doc){
        int num = doc.select("img").size();
        imgNum.addAndGet(num);

        System.out.println(num + " images found in url " + myurl);
    }
}
