package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "mytest", urlPatterns = {"/myurl"})
public class MyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // 1. Préparer le writer pour écrire la réponse
        PrintWriter out = new PrintWriter(resp.getOutputStream());
        
        // 2. Écrire du contenu HTML
        out.println("<html>");
        out.println("<head><title>Ma première Servlet</title></head>");
        out.println("<body>");
        out.println("<h1>Hello World from Servlet!</h1>");
        out.println("<p>Cette page est générée par Java, pas par un fichier HTML statique.</p>");
        out.println("<p>Heure du serveur : " + new java.util.Date() + "</p>");
        out.println("</body>");
        out.println("</html>");
        
        // 3. Envoyer la réponse
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        PrintWriter out = new PrintWriter(resp.getOutputStream());
        out.println("<html><body>");
        out.println("<h1>POST request received!</h1>");
        out.println("</body></html>");
        out.flush();
    }
}