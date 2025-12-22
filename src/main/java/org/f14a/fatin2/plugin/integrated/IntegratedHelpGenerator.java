package org.f14a.fatin2.plugin.integrated;

import org.f14a.fatin2.config.Config;
import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.event.EventHandler;
import org.f14a.fatin2.event.command.CommandEvent;
import org.f14a.fatin2.event.command.CommandEventListener;
import org.f14a.fatin2.event.command.OnCommand;
import org.f14a.fatin2.event.lifecycle.PluginsLoadDoneEvent;
import org.f14a.fatin2.plugin.Fatin2Plugin;
import org.f14a.fatin2.util.MessageGenerator;
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

    @EventHandler
    public void OnPluginsLoadDone(PluginsLoadDoneEvent event) {
        commands = new ArrayList<>(EventBus.getInstance().getAllCommandHandlers().entrySet());
        commands.sort(Map.Entry.comparingByKey());
        LOGGER.info("Generating {} commands for help...", commands.size());
    }
    @OnCommand(command = "help", alias = {"?"}, description = "Show this message")
    public void OnHelp(CommandEvent event) {
        StringBuilder sb = new StringBuilder();
        String prefix = Config.getConfig().getCommandPrefix();
        sb.append("支持的命令列表:\n");
        for (Map.Entry<String, CommandEventListener> command : commands) {
            String description = command.getValue().description();
            sb.append(prefix).append(command.getKey()).append(" - ").append(
                    description != null && !description.isEmpty() ? description : "No description"
            ).append("\n");
        }
        String string = sb.toString().trim();
        if (string.length() > 500) {
            // TODO: forward quickly
        }
        else {
            event.send(MessageGenerator.text(string));
        }
    }
}
