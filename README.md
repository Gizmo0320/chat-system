# Minecraft Chat System (Multi-Platform)

This repository provides a shared chat system core with platform adapters for Forge, NeoForge, Fabric, Folia, Paper, and Sponge. The goal is to keep behavior consistent across platforms while allowing each adapter to plug into the platform-specific chat event pipeline.

## Modules

- **core**: Pure Java chat engine (channels, routing, formatting, moderation, history).
- **platforms/***: Adapter modules for each Minecraft platform.

## Build & packaging

This project uses a Gradle multi-module build:

- `:core`
- `:platforms:forge`
- `:platforms:neoforge`
- `:platforms:fabric`
- `:platforms:paper`
- `:platforms:folia`
- `:platforms:sponge`

Run:

- `./gradlew build` (or `gradle build` if wrapper is unavailable)
- `./gradlew :core:test`

## Core architecture

### ChatSystem

`ChatSystem` is the facade used by adapters to route messages through permissions, moderation, filters, and formatting. Build it with the fluent builder to wire in platform-specific adapters.

### API

Use the `ChatService` API for integrations that need a stable surface to send messages, relay, or inspect channels. It wraps a `ChatSystem` instance and exposes helper methods like `send`, `sendAndRelay`, `findChannel`, `listChannels`, `listChannelIds`, `registerChannel`, `updateChannel`, `removeChannel`, `listLinkedChannels`, `recentHistory`, and `canSpeak`.

### Channels

`ChatChannel` defines a routing target, permission string, format template, muted flag, relay mode, and linked channels. Channels are stored in `ChatChannelRegistry` and registered via `ChatSystem.Builder#channels`, `addChannel`, or `addChannels`.

### Routing

`ChatRouter` is the pipeline that evaluates permissions, moderation, and filters before applying a formatter to render the final message. Linked channels are also rendered when configured. The outcome is returned as a `ChatResult`.

### Filters

`ChatFilter` allows you to deny or replace messages. `ProfanityFilter` provides a basic word replacement example. Use `ChatFilterPipeline` to compose filters and capture filter decisions for debugging or auditing.

### Formatting

`MessageFormatter` renders messages. Use `LegacyFormatter` for legacy text, `PlainFormatter` for a simple format, and `MiniMessageFormatter` for Adventure MiniMessage output. Deliveries include a `ChatFormat` so platform handlers can pick the correct renderer. `FormatPresetRegistry` plus `PresetAwareFormatter` lets channels reference shared format presets.

### Moderation

`ChatModerationPolicy` handles mute and slow mode checks. `SimpleModerationPolicy` enforces per-channel slow mode and player mute lists, while `AllowAllModerationPolicy` allows all messages. Use `ModerationStateStore` implementations like `FileModerationStateStore` to persist moderation state.

### Permissions

`PermissionChecker` bridges platform permission APIs to the routing layer. `AllowAllPermissionChecker` is a permissive default. Every channel has a permission string; users must pass the check to speak in that channel, and linked-channel deliveries also respect the target channel's permission.

### History

`ChatHistory` stores recent messages, and `InMemoryChatHistory` provides a bounded in-memory implementation. `FileChatHistory` supports retention limits, optional payload ciphering, and content redaction hooks.

### Audit logging & observability

`ChatAuditLogger` receives callbacks when messages are allowed or denied. Use `LoggingChatAuditLogger` to emit audit events via `java.util.logging`, or provide your own implementation.

`ChatMetrics` provides counters for allowed/denied messages. `InMemoryChatMetrics` is included for easy wiring and testing.

### Configuration loading and hot-reload

`ChatConfigLoader` provides a config loading interface. `SimpleYamlConfigLoader` parses the example YAML channels and format presets.

For safer reloads:

- `ChatConfigValidator` validates schema/linking/presets.
- `SafeChatConfigLoader` keeps the last-known-good config if reload fails.
- `ChatConfigWatcher` watches the config file and triggers reloads.

### Redis relay (cross-server chat)

Use the relay API to publish rendered chat messages to Redis so multiple servers can share chat. The core exposes `ChatRelay` and the Redis integration types:

- `RedisChatRelay` publishes rendered messages to a Redis channel.
- `RedisChatSubscriber` consumes the channel and provides decoded `ChatRelayMessage` payloads.
- `RedisPublisher`/`RedisSubscriber` are adapter interfaces for your Redis client of choice.

Adapters can call `ChatSystem#sendAndRelay` to ensure permitted messages are published for channels marked with `REDIS` relay mode. When receiving relay events, the platform adapter should broadcast the rendered message without re-routing to avoid loops.

## Platform adapter completion

Each platform bootstrap now includes:

1. Channel-aware message routing (`onChat(channelId, ...)`).
2. Relay-aware sends (`sendAndRelay`).
3. Admin command handling (`onAdminCommand(...)`) via `AdminCommandHandler`.

## Admin command layer

The shared `AdminCommandHandler` currently supports:

- `channel mute <channelId> <true|false>`

## Configuration

See `config/chat-system.yml` for an example configuration.
