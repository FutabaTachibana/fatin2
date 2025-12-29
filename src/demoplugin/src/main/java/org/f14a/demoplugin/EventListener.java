package org.f14a.demoplugin;

import org.f14a.fatin2.event.EventHandler;
import org.f14a.fatin2.event.command.CommandEvent;
import org.f14a.fatin2.event.command.GroupCommandEvent;
import org.f14a.fatin2.event.command.OnCommand;
import org.f14a.fatin2.event.command.PrivateCommandEvent;
import org.f14a.fatin2.event.message.MessageEvent;
import org.f14a.fatin2.event.session.Coroutines;
import org.f14a.fatin2.model.message.Message;
import org.f14a.fatin2.model.Response;
import org.f14a.fatin2.api.MessageGenerator;
import org.f14a.fatin2.api.MessageSender;
import org.f14a.fatin2.api.RequestSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.random.RandomGenerator;

public class EventListener {
    // 命令处理示例: 一个简单的 echo 实现
    // 别看它很长，其实一点也不简单x
    @OnCommand(command = "echo", description = "复述你说的话")
    public void onEcho(CommandEvent event) {
        // 不要直接修改原始的 event#getMessage().messages() 列表，因为它是不可变的
        List<Message> messages = new ArrayList<>();
        AtomicReference<Boolean> findEcho = new AtomicReference<>(false);
        event.getMessage().messages().forEach(msg -> {
            if (!findEcho.get() && msg.parse().startsWith("/echo")) {
                findEcho.set(true);
                messages.add(new Message(msg.type(), Map.of(
                        "text", msg.parse().replace("/echo ", "").trim()
                )));
            } else {
                messages.add(msg);
            }
        });

        // MessageGenerator#create 接受的参数是可变参数，因此需要将 List 转换为数组
        event.send(MessageGenerator.create(messages.toArray(new Message[0])));
    }

    // 阻塞式异步命令处理示例: 猜数字游戏
    // 注意必须使用 @Coroutines 注解来启用协程（虚拟线程）支持
    @OnCommand(command = "guess", description = "猜数字游戏")
    @Coroutines
    public void onGuessNumber(GroupCommandEvent event) {
        String reply;
        int number = RandomGenerator.getDefault().nextInt(1, 101);
        try {
        reply = Objects.requireNonNull(event.wait(MessageGenerator.text("请输入一个1-100的数字"))).getFirst().parse();
            while (Integer.parseInt(reply) != number) {
                reply = Objects.requireNonNull(event.wait(MessageGenerator.text(
                        Integer.parseInt(reply) > number ? "太大了" : "太小了"))).getFirst().parse();
            }
        } catch (NumberFormatException | NullPointerException e) {
            event.finish(MessageGenerator.text("游戏结束。"));
            return;
        }
        event.finish(MessageGenerator.text("恭喜你，成功猜中数字" + number));
    }

    // 对于监听消息的示例
    @EventHandler
    public void onCallMe(MessageEvent event) {
        if (event.getMessage().parse().contains("Fatin")) {
            event.send(MessageGenerator.text("有什么事情吗"));
        }
    }

    // 使用回调函数在发送成功后执行操作的示例
    @OnCommand(command = "sendandreply", alias = {"sar"}, description = "发送并回复")
    public void onSendAndReply(GroupCommandEvent event) {
        // 提供回调函数可以实现简单的异步操作
        // 相较于 CompletableFuture，这种方式更为简洁
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

    // 使用 CompletableFuture 在发送成功后执行操作的示例
    @OnCommand(command = "sendandrecall", description = "发送并撤回")
    public void onSendAndRecall(CommandEvent event) {
        CompletableFuture<Response> future = event.sendFuture(MessageGenerator.text("这条消息将在5秒后被撤回"));

        // 使用 thenAccept 注册回调函数
        // 事实上，CompletableFuture 还可以完成更复杂的异步控制流
        future.thenAccept(response -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            RequestSender.deleteMessage(response.getMessageId());
        });
    }

    // 带参数的命令示例
    @OnCommand(command = "cmd", needAt = true, description = "带参数的命令")
    public void onCommandWithArgs(CommandEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("命令: ").append(event.getCommand()).append("\n");
        sb.append("参数: \n");

        // 对于 CommandEvent，使用 getArgs() 方法获取参数列表
        String[] args = event.getArgs();
        for (int i = 0; i < args.length; i++) {
            sb.append("arg[").append(i).append("]: ").append(args[i]).append("\n");
        }
        event.send(MessageGenerator.text(sb.toString()));
    }

    // 转发消息示例
    @OnCommand(command = "forward", description = "转发消息")
    public void onForwardMessage(CommandEvent event) {
        String[] args = event.getArgs();
        long userId;
        try {
            userId = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            event.send(MessageGenerator.text("请提供有效的用户ID作为第一个参数"));
            return;
        }

        // 构建转发消息
        userId = event.getMessage().userId();
        MessageGenerator.MessageBuilder mb = MessageGenerator.builder();
        for (int i = 1; i < args.length; i++) {
            mb.text(args[i]);
        }

        // 发送转发消息
        if (event instanceof PrivateCommandEvent) {
            MessageSender.sendPrivateForward(event.getMessage().userId(), userId, "人", mb.build());
        }
        else if (event instanceof GroupCommandEvent) {
            long groupId = ((GroupCommandEvent) event).getGroupId();
            MessageSender.sendGroupForward(groupId, userId, "人", mb.build());
        }
    }
}
