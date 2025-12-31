package org.f14a.fatin2.plugin.integrated;

import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.event.EventHandler;
import org.f14a.fatin2.event.command.CommandEvent;
import org.f14a.fatin2.event.command.CommandEventListener;
import org.f14a.fatin2.event.command.OnCommand;
import org.f14a.fatin2.event.lifecycle.PluginsLoadDoneEvent;
import org.f14a.fatin2.plugin.Fatin2Plugin;
import org.f14a.fatin2.plugin.PluginManager;
import org.f14a.fatin2.plugin.PluginWrapper;
import org.f14a.fatin2.api.generator.MessageGenerator;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IntegratedHelpGenerator implements Fatin2Plugin {
    private final static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(IntegratedHelpGenerator.class);
    List<Map.Entry<String, CommandEventListener>> commands;

    @Override
    public void onLoad() { }

    @Override
    public void onEnable() {
        EventBus.getInstance().register(this, this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getName() {
        return "integrated-help";
    }

    @Override
    public String getDisplayName() {
        return "Integrated Help";
    }

    @Override
    public String getVersion() {
        return "integrated";
    }

    @Override
    public String getAuthor() {
        return "Fatin2";
    }

    @Override
    public String getDescription() {
        return "内置的帮助命令生成器插件。";
    }

    @EventHandler
    public void OnPluginsLoadDone(PluginsLoadDoneEvent event) {
        commands = new ArrayList<>(EventBus.getInstance().getAllCommandHandlers().entrySet());
        commands.sort(Map.Entry.comparingByKey());
        LOGGER.info("Generating {} commands for help...", commands.size());
    }

    @OnCommand(command = "help", alias = {"?"}, description = "显示这条消息")
    public void OnHelp(CommandEvent event) {
        StringBuilder sb = new StringBuilder();
        String prefix = Config.getConfig().getCommandPrefix();
        if (event.getArgs().length == 0) {
            // General help message
            sb.append("支持的命令列表:\n");
            for (Map.Entry<String, CommandEventListener> command : commands) {
                String description = command.getValue().description();
                sb.append(prefix).append(command.getKey()).append(" - ").append(
                        description != null && !description.isEmpty() ? description : "暂无描述"
                ).append("\n");
            }
        } else {
            // Detailed help message for a specific command
            String cmd = event.getArgs()[0];
            CommandEventListener found = null;
            for (Map.Entry<String, CommandEventListener> command : commands) {
                if (command.getKey().equals(cmd)) {
                    found = command.getValue();
                    break;
                }
            }
            if (found != null) {
                sb.append("命令: ").append(prefix).append(cmd).append("\n");
                sb.append("描述: ").append(
                        found.description() != null && !found.description().isEmpty() ?
                                found.description() : "暂无描述"
                ).append("\n");
                sb.append("用法: ").append(
                        found.usage() != null && !found.usage().isEmpty() ?
                                found.usage() : "暂无用法说明"
                ).append("\n");
                sb.append("需要艾特: ").append(found.needAt() ? "是" : "否").append("\n");
                sb.append("来自插件: ").append(found.plugin().getDisplayName()).append("\n");
            } else {
                sb.append("未找到命令 '").append(cmd).append("' 的帮助信息。");
            }
        }
        String string = sb.toString().trim();
        if (string.length() > 1000) {
            event.setSendForward(true);
        }
        event.send(MessageGenerator.text(string));
    }

    @OnCommand(command = "plugins", description = "显示已加载的插件列表", permission = 2)
    public void OnPlugins(CommandEvent event) {
        StringBuilder sb = new StringBuilder();
        Map<String, PluginWrapper> plugins = PluginManager.getInstance().getPlugins();
        sb.append("已加载的插件列表:\n");
        for (PluginWrapper plugin : plugins.values()) {
            sb.append(plugin.getPlugin().getDisplayName()).append(", ");
        }
        sb.deleteCharAt(sb.length() - 2);
        event.send(MessageGenerator.text(sb.toString().trim()));
    }

    @OnCommand(command = "plugininfo", description = "显示指定插件的信息", permission = 2, usage = "/plugin <插件名称>")
    public void OnPluginInfo(CommandEvent event) {
        if (event.getArgs().length == 0) {
            event.send(MessageGenerator.text("请提供插件名称。用法: /plugininfo <插件名称>"));
            return;
        }
        String pluginName = event.getArgs()[0];
        PluginWrapper plugin = PluginManager.getInstance().getPluginWrapper(pluginName);
        if (plugin == null) {
            event.send(MessageGenerator.text("未找到名为 '" + pluginName + "' 的插件。"));
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("插件名称: ").append(plugin.getPlugin().getDisplayName()).append("\n");
        sb.append("内部名称: ").append(plugin.getPlugin().getName()).append("\n");
        sb.append("状态: ").append(plugin.isEnabled() ? "已启用" : "已禁用").append("\n");
        sb.append("版本: ").append(plugin.getPlugin().getVersion() != null ?
                plugin.getPlugin().getVersion() : "未知").append("\n");
        sb.append("作者: ").append(plugin.getPlugin().getAuthor() != null ?
                plugin.getPlugin().getAuthor() : "未知").append("\n");
        sb.append("描述: ").append(plugin.getPlugin().getDescription() != null ?
                plugin.getPlugin().getDescription() : "暂无描述").append("\n");
        event.send(MessageGenerator.text(sb.toString().trim()));
    }
}
