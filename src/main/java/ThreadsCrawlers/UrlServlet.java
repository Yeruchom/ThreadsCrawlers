package ThreadsCrawlers;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@WebServlet(name = "UrlServlet", value = "/UrlServlet")
public class UrlServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.getRequestDispatcher("UrlForm.html").include(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String urlString = request.getParameter("url").trim();


        response.setContentType("text/html");
        PrintWriter writer =response.getWriter();


        try{
            if(urlString == "" || urlString == null)
                throw new IllegalArgumentException("you need to enter a url to crawl\n");

            URL url = new URL(urlString);

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");

            if(huc.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IllegalArgumentException("connection to url did not response with success\n");

//            request.setAttribute("url", url);
            request.getRequestDispatcher("/CrawlServlet").forward(request, response);


        }catch (IllegalArgumentException e){ writer.println(e.getMessage());
                writer.println( "<a href=\"UrlForm.html\"><p>try again</p></a>"); }
        catch (MalformedURLException e){ writer.println("looks like the url is not valid ");
                writer.println("<a href=\"UrlForm.html\">try again</a>"); }
        catch (Exception e){ writer.println("an error happened by connecting to url. error: " + e.getMessage());
                writer.println( "<a href=\"UrlForm.html\">try again</a>");}

        writer.close();

    }
}
