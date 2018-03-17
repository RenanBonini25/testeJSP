
<%@page import="br.senac.tads.pi3a.agendaweb.Pessoa"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<%
    Pessoa p1 = new Pessoa("Claudio", "06/04/1992");
    request.setAttribute("xpto", p1);
%>    
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>  

        <h1 style="color: blue"><%= p1.getNome()%></h1>

    </body>
</html>
