package ThreadsCrawlers;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "CrawlServlet", value = "/CrawlServlet")
public class CrawlServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       System.out.println("in crawl get");
       response.setContentType("text/html");
       PrintWriter out = response.getWriter();
       out.println("this is the meanwhile result");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter writer = response.getWriter();

        Crawler mainThread = new Crawler(request.getParameter("url").trim(), 0);
        mainThread.start();

        request.getRequestDispatcher("Crawling.html").include(request, response);

        try {
            mainThread.join();
            request.getRequestDispatcher("CrawlingResults.html").include(request, response);
        }
        catch (InterruptedException i){System.out.println("exception on joining main thread");}
        catch(IOException e){System.err.println("CrawlingResults.html file not found");};

        writer.close();
    }
}
