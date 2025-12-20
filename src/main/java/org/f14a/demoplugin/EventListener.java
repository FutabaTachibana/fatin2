package org.f14a.demoplugin;

import org.f14a.fatin2.event.EventHandler;
import org.f14a.fatin2.event.command.CommandEvent;
import org.f14a.fatin2.event.command.GroupCommandEvent;
import org.f14a.fatin2.event.command.OnCommand;
import org.f14a.fatin2.event.message.MessageEvent;
import org.f14a.fatin2.event.session.Coroutines;
import org.f14a.fatin2.type.Message;
import org.f14a.fatin2.type.Response;
import org.f14a.fatin2.util.MessageGenerator;
import org.f14a.fatin2.util.RequestSender;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.random.RandomGenerator;

public class EventListener {
    // Example for command handling
    @OnCommand(command = "echo")
    public void onEcho(CommandEvent event) {
        // Do NOT edit the original message array directly
        Message[] messages = Arrays.copyOf(event.getMessage().messages(), event.getMessage().messages().length);
        for (int i = 0; i < messages.length; i++) {
            if (messages[i].parse().startsWith("/echo")) {
                try {
                    messages[i] = new Message(messages[i].type(), Map.of(
                            "text", messages[i].parse().replace("/echo ", "").trim()
                    ));
                    break;
                } catch (Exception e) {
                    return;
                }
            }
        }
        event.send(MessageGenerator.create(messages));
    }
    // Example for coroutine handling and session management
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
    // Example for raw message handling
    @EventHandler
    public void onCallMe(MessageEvent event) {
        if (event.getMessage().parse().contains("Fatin")) {
            event.send(MessageGenerator.text("有什么事情吗"));
        }
    }
    // Example for action after sending successfully and message recall
    @OnCommand(command = "sendandrecall")
    public void onSendAndRecall(CommandEvent event) {
        CompletableFuture<Response> future = event.sendFuture(MessageGenerator.text("这条消息将在5秒后被撤回"));
        future.thenAccept(response -> {
            // Recall the message after 5 seconds
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            RequestSender.deleteMessage(response.getMessageId());
        });
    }
}
