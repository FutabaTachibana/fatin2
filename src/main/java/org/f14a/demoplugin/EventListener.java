package org.f14a.demoplugin;

import org.f14a.fatin2.event.EventHandler;
import org.f14a.fatin2.event.command.CommandEvent;
import org.f14a.fatin2.event.command.GroupCommandEvent;
import org.f14a.fatin2.event.command.OnCommand;
import org.f14a.fatin2.event.message.MessageEvent;
import org.f14a.fatin2.event.session.Coroutines;
import org.f14a.fatin2.type.Message;
import org.f14a.fatin2.util.MessageGenerator;

import java.util.Arrays;
import java.util.random.RandomGenerator;

public class EventListener {
    @OnCommand(command = "echo")
    public void onEcho(CommandEvent event) {
        Message[] message = event.getMessage().message();
        event.send(MessageGenerator.create(Arrays.copyOfRange(message, 1, message.length)));
    }
    @OnCommand(command = "guess")
    @Coroutines
    public void onGuessNumber(GroupCommandEvent event) {
        String reply;
        int number = RandomGenerator.getDefault().nextInt(1, 101);
        reply = event.wait(MessageGenerator.text("请输入一个1-100的数字"));
        while (Integer.parseInt(reply) != number) {
            reply = event.wait(MessageGenerator.text(
                    Integer.parseInt(reply) > number ? "太大了" : "太小了"));
        }
        event.send(MessageGenerator.text("恭喜你，成功猜中数字" + number));
    }
    @EventHandler
    public void onCallMe(MessageEvent event) {
        if (event.getMessage().parse().contains("Fatin")) {
            event.send(MessageGenerator.text("有什么事情吗"));
        }
    }
}
