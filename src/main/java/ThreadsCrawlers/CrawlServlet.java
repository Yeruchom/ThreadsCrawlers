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
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("in crawl post");
//        if(request.getParameter("url") != null)
//            System.out.println("param: " + request.getParameter("url"));
//else
//    System.out.println("its null");


        PrintWriter writer = response.getWriter();
        writer.println("working" + request.getParameter("url").trim());
        request.getRequestDispatcher("").include(request, response);
        writer.close();
        new Crawler(request.getParameter("url").trim(), 0).crawl();

    }
}
