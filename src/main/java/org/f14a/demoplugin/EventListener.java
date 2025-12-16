package org.f14a.demoplugin;

import org.f14a.fatin2.event.command.CommandEvent;
import org.f14a.fatin2.event.command.GroupCommandEvent;
import org.f14a.fatin2.event.command.OnCommand;
import org.f14a.fatin2.event.session.Coroutines;

import java.util.random.RandomGenerator;

public class EventListener {
    @OnCommand(command = "echo")
    public void onEcho(CommandEvent event) {
        event.send(event.getMessage().rawMessage().substring(6).trim());
    }
    @OnCommand(command = "guess")
    @Coroutines
    public void onGuessNumber(GroupCommandEvent event) {
        String reply;
        int number = RandomGenerator.getDefault().nextInt(1, 11);
        reply = event.wait("Please enter your guess: (1-10)");
        while (Integer.parseInt(reply) != number) {
            reply = event.wait(Integer.parseInt(reply) > number ?
                    "Too large! Try again:" :
                    "Too small! Try again:");
        }
        event.send("Correct! The number was " + number);
    }

}
