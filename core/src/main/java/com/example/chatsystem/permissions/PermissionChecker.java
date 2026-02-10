package com.example.chatsystem.permissions;

import java.util.UUID;

/**
 * Adapter for platform-specific permission checks.
 */
public interface PermissionChecker {
    boolean hasPermission(UUID playerId, String permission);
}
