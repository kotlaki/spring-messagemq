package ru.kurganov.springmessagemq;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
    С помощью Java написать программу, реализующую передачу сообщения из одной
    очереди Apache ActiveMQв две другие, в каждую по одной копии.
    Добавить обработку ошибок при получении пустого сообщения в виде вывода
    произвольного текста ошибки в консоль Java. Пустым сообщением считать сообщение,
    не содержащее ни одного символа.
 */

@Component
public class MyRouter extends RouteBuilder {

    @Autowired
    private MyBean myBean;

    @Override
    public void configure() throws Exception {
        // источник из файлов папки data. noop=true установлен для того чтобы отправить файлы без удаления
        from("file:E:\\work_temp\\project\\spring-messagemq\\src\\data?noop=true")
               // если сообщение(файл) не содержит символов, отправляем его в очередь error.
               // иначе делаем копии сообщений в reverse1, reverse2 и basic
               .doTry()
                .choice()
                    .when(body().isEqualTo(""))
                    .log(LoggingLevel.ERROR, "Сообщение пустое, переправлено в очередь error!!!")
                    .to("jms:queue:error")
                .endChoice()
                .otherwise()
               // копируем сообщения в дополнительные очереди, затем отправляем в конечную очередь
               .log("Message: ${body}")
                    .wireTap("jms:queue:reserve1")
                    .log("Copy message in reserve1!!!")
                    .wireTap("jms:queue:reserve2")
                    .log("Copy message in reserve2!!!")
                    .to("jms:queue:basic")
                .end();

        // потребитель
       from("jms:queue:basic")
               .bean(myBean);
    }
}
