package br.com.ryuu.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .filename(".env")
            .ignoreIfMissing()
            .load();

    public static String getDiscordToken() {
        String token = dotenv.get("DISCORD_TOKEN");
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("DISCORD_TOKEN não encontrado no arquivo .env");
        }
        return token;
    }

    public static String getGithubToken() {
        String token = dotenv.get("GITHUB_TOKEN");
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("GITHUB_TOKEN não encontrado no arquivo .env");
        }
        return token;
    }

    public static String getChannelId() {
        String channelId = dotenv.get("CHANNEL_ID");
        if (channelId == null || channelId.isEmpty()) {
            throw new IllegalStateException("CHANNEL_ID não encontrado no arquivo .env");
        }
        return channelId;
    }
}
