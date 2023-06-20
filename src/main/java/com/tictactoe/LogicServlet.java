package com.tictactoe;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;


@WebServlet(name = "LogicServlet", value = "/logic")
@Log4j2
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int index = getSelectedIndex(request);
        log.info("Получаем индекс: "+ index +" нажатой кнопки из запроса.");

        HttpSession session = request.getSession();
        log.info("Получаем открытую сессию: "+ session.getId() +" из запроса.");
        Field field = extractField(session);

        Sign currentSign = field.getField().get(index);

        if (Sign.EMPTY != currentSign) {
            log.info("Нажатая клавиша уже занята.");
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(request,response);
            return;
        }

        field.getField().put(index,Sign.CROSS);
        log.info("Поставили " + Sign.CROSS + " на позиции " + index);

        if (checkWin(response,session,field)) {
            return;
        }

        int emptyFieldIndex = field.getEmptyFieldIndex();
        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            log.info("Компьютер сделал ход на позицию: " + emptyFieldIndex);
            if (checkWin(response,session,field)) {
                return;
            }
        } else {
            session.setAttribute("draw",true);
            log.info("Произошла ничья!");
            List<Sign> data = field.getFieldData();
            session.setAttribute("data",data);
            response.sendRedirect("/index.jsp");
            return;
        }

        List<Sign> data = field.getFieldData();
        session.setAttribute("data", data);
        session.setAttribute("field", field);
        log.info("Передаем атрибуты \"field\": " + field + ", и \"data\": " + data + " в сессию " + session.getId());

        response.sendRedirect("/index.jsp");
        log.info("Переадрисация на \"/index.jsp\"");
    }

    private Field extractField(HttpSession session) {
        Object field = session.getAttribute("field");
        if (Field.class != field.getClass()) {
            session.invalidate();
            log.error("Сессия сломалась.");
            throw new RuntimeException("Session is broken, try one more time");
        }
        log.info("Получаем \"field\": "+field +" из сессии: " + session);
        return (Field) field;
    }

    private int getSelectedIndex(HttpServletRequest req) {
        String click = req.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession session, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            session.setAttribute("winner",winner);
            log.info("Победила сторона играющая " + winner);

            List<Sign> data = field.getFieldData();
            session.setAttribute("data", data);

            response.sendRedirect("/index.jsp");
            return  true;
        }
        return false;
    }
}
