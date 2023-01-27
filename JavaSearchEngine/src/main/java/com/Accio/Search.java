package com.Accio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


@WebServlet("/Search")
public class Search extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // get parameter called keyword from request
        String keyword = request.getParameter("keyword");
        System.out.println(keyword);
        try {
            //establish connection to database
            Connection connection = DatabaseConnection.getConnection();
            // Saving keyword and then linking into history table
            PreparedStatement preparedStatement =connection.prepareStatement("Insert into history values(?,?)");
            preparedStatement.setString(1,keyword);
            preparedStatement.setString(2,"http://localhost:8080/JavaSearchEngine/Search?keyword="+keyword);
            preparedStatement.executeUpdate();
            // executing a query related to keyword and get the result
            ResultSet resultSet = connection.createStatement().executeQuery("select pagetitle, pagelink ,(length(lower(pagetext)) -length(replace(lower(pagetext),'" + keyword + "','')))/length('" + keyword + "') as countoccurance from pages order by countoccurance desc limit 30;");
            ArrayList<SearchResult> results = new ArrayList<SearchResult>();
            //iterate through resultSet and save all elements in results array
            while (resultSet.next()) {
                SearchResult searchResult = new SearchResult();
                //get pageTitle
                searchResult.setPageTitle(resultSet.getString("pageTitle"));
                //get pageLink
                searchResult.setPageLink(resultSet.getString("pageLink"));
                results.add(searchResult);
            }
            //display result on console
            for (SearchResult searchResult : results) {
                System.out.println(searchResult.getPageTitle() + " " + searchResult.getPageLink() + "\n");
            }
            //set the attribute of request with result arraylist
            request.setAttribute("results",results);
            //forward request to search.jsp
            request.getRequestDispatcher("/search.jsp").forward(request,response);
            response.setContentType("text/html");
            PrintWriter out =response.getWriter();

        }
        catch (SQLException | IOException | ServletException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
