package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "userinfo", urlPatterns = {"/UserInfo"})
public class UserInfo extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Définir le type de contenu de la réponse
        response.setContentType("text/html; charset=UTF-8");
        
        // 2. Récupérer les paramètres du formulaire
        String name = request.getParameter("name");
        String firstname = request.getParameter("firstname");
        String age = request.getParameter("age");
        
        // 3. Préparer la réponse HTML
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='fr'>");
        out.println("<head>");
        out.println("    <meta charset='UTF-8'>");
        out.println("    <title>Récapitulatif</title>");
        out.println("    <style>");
        out.println("        body { font-family: Arial, sans-serif; max-width: 600px; margin: 50px auto; padding: 20px; }");
        out.println("        .recap { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        out.println("        h1 { color: #28a745; }");
        out.println("        ul { list-style: none; padding: 0; }");
        out.println("        li { padding: 10px; margin: 5px 0; background: #f8f9fa; border-left: 4px solid #28a745; }");
        out.println("        .back-link { display: inline-block; margin-top: 20px; color: #007bff; text-decoration: none; }");
        out.println("    </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class='recap'>");
        out.println("        <h1>✅ Récapitulatif des informations</h1>");
        out.println("        <ul>");
        out.println("            <li><strong>Nom :</strong> " + name + "</li>");
        out.println("            <li><strong>Prénom :</strong> " + firstname + "</li>");
        out.println("            <li><strong>Âge :</strong> " + age + " ans</li>");
        out.println("        </ul>");
        out.println("        <a href='/myform.html' class='back-link'>← Retour au formulaire</a>");
        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");
        
        out.flush();
    }
}