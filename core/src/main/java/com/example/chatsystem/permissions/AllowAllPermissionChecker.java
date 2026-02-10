package com.example.chatsystem.permissions;

import java.util.UUID;

/**
 * Permissive permission adapter that always allows access.
 */
public final class AllowAllPermissionChecker implements PermissionChecker {
    @Override
    public boolean hasPermission(UUID playerId, String permission) {
        return true;
    }
}
