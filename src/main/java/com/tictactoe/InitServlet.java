package com.tictactoe;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "InitServlet", value = "/start")
@Log4j2
public class InitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        log.info("Создани сессии: " + session.getId());

        Field field = new Field();
        Map<Integer,Sign> fieldData = field.getField();

        List<Sign> data = field.getFieldData();

        session.setAttribute("field", field);
        session.setAttribute("data", data);
        log.info("Передаем атрибуты \"field\": " + field + ", и \"data\": " + data + " в сессию " + session.getId());

        getServletContext().getRequestDispatcher("/index.jsp").forward(request,response);
        log.info("Переадрисация на \"/index.jsp\"");
    }
}
