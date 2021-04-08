package ru.kurganov.springmessagemq;

import org.springframework.stereotype.Component;

@Component
public class MyBean {
    public void myMethod(String body) throws Exception {
//        if(body.isEmpty()) {
//            throw new Exception("Пустое сообщение!!!");
//        }
        System.out.println("Получено: " + body);
    }

}
