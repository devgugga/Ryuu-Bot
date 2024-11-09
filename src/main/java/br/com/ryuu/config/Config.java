package br.com.ryuu.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .filename(".env")
            .ignoreIfMissing()
            .load();

    /**
     * Retrieves the value of the specified environment variable.
     * If the variable is not found, the method returns the provided default value.
     *
     * @param key          The name of the environment variable to retrieve.
     * @param defaultValue The value to return if the specified environment variable is not found.
     * @return The value of the specified environment variable or the provided default value.
     */
    public static String get(String key, String defaultValue) {
        String value = dotenv.get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Retrieves the Discord token from the .env file.
     * If the DISCORD_TOKEN environment variable is not found or is empty,
     * an IllegalStateException is thrown.
     *
     * @return The Discord token retrieved from the .env file.
     * @throws IllegalStateException If the DISCORD_TOKEN environment variable is not found or is empty.
     */
    public static String getDiscordToken() {
        String token = dotenv.get("DISCORD_TOKEN");
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("DISCORD_TOKEN não encontrado no arquivo .env");
        }
        return token;
    }


    /**
     * Retrieves the Channel ID from the .env file.
     * If the CHANNEL_ID environment variable is not found or is empty,
     * an IllegalStateException is thrown.
     *
     * @return The Channel ID retrieved from the .env file.
     * @throws IllegalStateException If the CHANNEL_ID environment variable is not found or is empty.
     */
    public static String getChannelId() {
        String channelId = dotenv.get("CHANNEL_ID");
        if (channelId == null || channelId.isEmpty()) {
            throw new IllegalStateException("CHANNEL_ID não encontrado no arquivo .env");
        }
        return channelId;
    }
}
