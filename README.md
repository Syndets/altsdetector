# AltsDetector

**AltsDetector** is a Minecraft plugin that allows you to check players' alternate accounts by their IP addresses. It highlights banned accounts in red and active accounts in green, with additional features for server management.

## Features
- View alternate accounts associated with a player using `/alts [playername]`
- Banned accounts show in red, active accounts show in green
- Supports both online and offline player searches
- Configurable options to hide IP addresses or show player names
- Optional auto-ban feature for players logging in with banned alts

## Configuration
In the `config.yml` file, you can adjust the following settings:
- **`hide-ip:`** (default `true`): When enabled, shows player names instead of IP addresses.
- **`auto-ban:`** (default `false`): Automatically bans players if any of their associated alt accounts are banned.

## Commands
- **`/alts [playername]`**: View all accounts associated with the specified player.

## Permissions
- **`altsplugin.alts`**: Allows use of the `/alts` command (default: `op`).

## Installation
1. Download the latest release of AltsDetector.
2. Place the `.jar` file in your server's `plugins` folder.
3. Restart the server and configure the plugin in `config.yml` if needed.
