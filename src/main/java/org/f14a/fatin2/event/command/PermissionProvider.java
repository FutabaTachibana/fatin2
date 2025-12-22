package org.f14a.fatin2.event.command;

@Deprecated
public interface PermissionProvider {
    /**
     * Return true if the message has permission to execute the command.
     * @param event the message to check permission.
     * @param requiredPermission the required permission level.
     * @return true if the message has permission to execute the command.
     */
    boolean hasPermission(CommandEvent event, int requiredPermission);
}
