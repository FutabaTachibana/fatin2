package org.f14a.demoplugin;

import org.f14a.fatin2.event.EventHandler;
import org.f14a.fatin2.event.command.CommandEvent;
import org.f14a.fatin2.event.command.GroupCommandEvent;
import org.f14a.fatin2.event.command.OnCommand;
import org.f14a.fatin2.event.command.PrivateCommandEvent;
import org.f14a.fatin2.event.message.MessageEvent;
import org.f14a.fatin2.event.session.Coroutines;
import org.f14a.fatin2.type.Message;
import org.f14a.fatin2.type.Response;
import org.f14a.fatin2.util.MessageGenerator;
import org.f14a.fatin2.util.MessageSender;
import org.f14a.fatin2.util.NodeGenerator;
import org.f14a.fatin2.util.RequestSender;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.random.RandomGenerator;

public class EventListener {
    // Example for command handling
    @OnCommand(command = "echo", description = "复述你说的话")
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
    @OnCommand(command = "guess", description = "猜数字游戏")
    @Coroutines
    public void onGuessNumber(GroupCommandEvent event) {
        String reply;
        int number = RandomGenerator.getDefault().nextInt(1, 101);
        reply = event.wait(MessageGenerator.text("请输入一个1-100的数字"))[0].parse();
        try {
            while (Integer.parseInt(reply) != number) {
                reply = event.wait(MessageGenerator.text(
                        Integer.parseInt(reply) > number ? "太大了" : "太小了"))[0].parse();
            }
        } catch (NumberFormatException e) {
            event.send(MessageGenerator.text("游戏结束。"));
            return;
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
    // Example for action after sending successfully by callback and message reply
    @OnCommand(command = "sendandreply", alias = {"sar"}, description = "发送并回复示例")
    @Coroutines
    public void onSendAndReply(GroupCommandEvent event) {
        event.send(response -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            MessageSender.replyGroupMessage(event.getGroupId(), event.getMessage().userId(), response.getMessageId(),
                    MessageGenerator.text("这是对5秒前消息的回复"));
        }, MessageGenerator.text("这条消息将在5秒后被回复"));
    }
    // Example for action after sending successfully by CompletableFuture and message recall
    @OnCommand(command = "sendandrecall", description = "发送并撤回示例")
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
    // Example for command with arguments
    @OnCommand(command = "cmd", needAt = true, description = "带参数的命令示例")
    public void onCommandWithArgs(CommandEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("命令: ").append(event.getCommand()).append("\n");
        sb.append("参数: \n");
        String[] args = event.getArgs();
        for (int i = 0; i < args.length; i++) {
            sb.append("arg[").append(i).append("]: ").append(args[i]).append("\n");
        }
        event.send(MessageGenerator.text(sb.toString()));
    }
    // Example for forward message
    @OnCommand(command = "forward", description = "转发消息示例")
    public void onForwardMessage(CommandEvent event) {
        String[] args = event.getArgs();
        long userId;
        try {
            userId = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            event.send(MessageGenerator.text("请提供有效的用户ID作为第一个参数"));
            return;
        }
        userId = event.getMessage().userId();
        MessageGenerator.MessageBuilder mb = MessageGenerator.builder();
        for (int i = 1; i < args.length; i++) {
            mb.text(args[i]);
        }
        if (event instanceof PrivateCommandEvent) {
            MessageSender.sendPrivateForward(event.getMessage().userId(), userId, "人", mb.build());
        }
        else if (event instanceof GroupCommandEvent) {
            long groupId = ((GroupCommandEvent) event).getGroupId();
            MessageSender.sendGroupForward(groupId, userId, "人", mb.build());
        }
    }
}
