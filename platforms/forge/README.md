# Platform Adapter

This module contains the adapter for the platform. Wire it into the platform's chat events and map them to the shared core.

## Responsibilities

- Listen to chat events.
- Build `ChatMessage` objects and call `ChatSystem#send`.
- Apply `ChatResult` (cancel, modify, broadcast).
