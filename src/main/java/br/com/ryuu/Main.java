package br.com.ryuu;

import br.com.ryuu.config.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Main {
    private final JDA jda;

    public Main() throws Exception {
        jda = JDABuilder.createDefault(Config.getDiscordToken())
                .setMemberCachePolicy(MemberCachePolicy.NONE)
                .setChunkingFilter(ChunkingFilter.NONE)
                .enableIntents(GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT)
                .build();

        jda.awaitReady();
        System.out.println("Bot está online!");
    }

    public static void main(String[] args) {
        try {
            new Main();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JDA getJda() {
        return jda;
    }
}