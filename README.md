![SudoSigns logo](https://mylesmor.dev/sudosigns/logo.png)

# SudoSigns-OG

A modern fork of SudoSigns: A Spigot plugin used to run commands by interacting with a sign. Some inspiration for commands has been taken from [Nokorbis' Command Signs](https://www.spigotmc.org/resources/command-signs.10512/). MylesMor implemented a convenient inventory-based GUI. NotAlexNoyle updated the plugin for 1.19 on behalf of [TrueOG Network](https://true-og.net).

This allows you to perform actions on SudoSigns such as:
* Editing the sign's text
* Adding permissions that a player must have to run the sign's commands
* Adding either player or console commands to a sign
* Adding messages which are shown to the player
* Use color codes (`&`) on the sign for coloured text
* Adding delays to commands/messages
* (Vault Economy) Setting the price of running a sign

For in-depth details on the command, permissions and features of this plugin, visit the [Wiki](https://github.com/MylesMor/SudoSigns/wiki)!

## Installation
1. Clone the git repository: [https://github.com/NotAlexNoyle/SudoSigns-OG](https://github.com/NotAlexNoyle/SudoSigns-OG).
2. Build the plugin with `./gradlew build`
3. Place the JAR file from SudoSigns-OG/build/libs inside your server's `./plugins` directory.
4. Reload or restart the server.

## Getting Started
Visit the [Getting Started](https://github.com/MylesMor/SudoSigns/wiki/Getting-Started) page to find out how to setup your first SudoSign!

## Issues
Contact the developer in #plugin-help on the [TrueOG Network Discord](https://discord.gg/ma9pMYpBU6).

In your issue report, please include as much information as possible including:
* The Spigot version.
* Any errors in the console (please copy these in full).
* The steps to reproduce the issue.

## Planned Features

- Fix messages being sent to chat that should only be captured by SudoSigns.

- Add a config option to change the console prefix.

- Add a config option to change the location of the currency symbol.

## Changelog:

**1.2.2:**

- Added an option to change or completely disable the prefix in config file (Fix [#5](https://github.com/MylesMor/SudoSigns/issues/5) upstream).

- Added a config option to change the currency symbol.

- Added a config option for whether or not to use decimals in the prices.

**1.2.1:**

- Uppercase color code support.

- Fixed NullPointerException with unrecognized commands.

**1.2.0:**

- 1.19.4 Support.

- Convert from maven to gradle.

- Removed spigot update checker.

- Ignore formatting codes when calculating the maximum line length (Adopt [#4](https://github.com/MylesMor/SudoSigns/pull/4) upstream).

- Lowercase color code support.

- Fixed errors when missing command parameters.