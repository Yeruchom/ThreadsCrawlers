package ThreadsCrawlers;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet(name = "CrawlServlet", value = "/CrawlServlet")
public class CrawlServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        //first check if this user submitted a uel
       HttpSession session = request.getSession();
       if (session == null || session.getAttribute("id") == null){
           out.println("please first submit a url ");
           out.println("<a href=\"UrlForm.html\">here</a>");
       }
       else {
           out.println("my id : " + session.getAttribute("id"));
               LinkedList<AtomicInteger> results = (LinkedList<AtomicInteger>)getServletContext().getAttribute("results");
               Integer imgs = results.get((Integer)session.getAttribute("id")-1).get();

               //check if still crawling or we are don
               if(session.getAttribute("finished") != null)
                   out.println("<h3>Crawling URL " +request.getParameter("url") +" is finished</h3> <p><b>Final</b> results: "
                           + imgs + " images found.</p>");
               else
                out.println("<h3>Crawling URL " +request.getParameter("url") +"...</h3> <p><b>Current</b> results: "
                        + imgs + " images found.</p> <p>please reload page <a href=\"javascript:window.location.href=window.location.href\">Reload</a> for more results</p>");
           }

    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter writer = response.getWriter();

        //get the global list of results, and add a new AtomicInteger that will keep the
        // number of images found, and will be shared with all threads.
        LinkedList<AtomicInteger> results = (LinkedList<AtomicInteger>) getServletContext().getAttribute("results");
        results.add(new AtomicInteger(0));

        //get the maxDepth parameter from web.xml file.
        Integer maxdepth = (Integer.parseInt(getServletContext().getInitParameter("maxdepth")));

        //creat the first thread that will crawl the url
        Crawler mainThread = new Crawler(request.getParameter("url").trim(), maxdepth, results.getLast());
        mainThread.start();

        writer.println("<body>\n" +
                "<h1>Crawling!</h1>\n" +
                "<h3>Crawling started, visit <a href=\"../CrawlServlet?url=" + request.getParameter("url") +
                "\">here</a> to see results</h3>\n" +
                "</body>\n" +
                "</html>");
        writer.close();

        try {
            //the first thread finished - that means all threads joined and we have the final results
            mainThread.join();
            HttpSession session = request.getSession();
            session.setAttribute("finished", true);
        }
        catch (InterruptedException i){System.out.println("exception on joining main thread");}
    }
}
