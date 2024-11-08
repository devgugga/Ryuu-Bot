package br.com.ryuu.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;

public class DiscordEmbedBuilder {
    private static final Color COMMIT_COLOR = new Color(46, 160, 67);
    private static final Color PR_COLOR = new Color(130, 80, 223);
    private static final Color ISSUE_COLOR = new Color(206, 60, 60);

    public static MessageEmbed createCommitEmbed(String author, String commitMessage,
                                                 String commitUrl, String branch) {
        return new EmbedBuilder()
                .setTitle("Novo Commit", commitUrl)
                .setColor(COMMIT_COLOR)
                .addField("Autor", author, true)
                .addField("Branch", branch, true)
                .addField("Mensagem", commitMessage, false)
                .setFooter("GitHub Webhook")
                .setTimestamp(Instant.now())
                .build();
    }

    public static MessageEmbed createPullRequestEmbed(String title, String author,
                                                      String prUrl, String state,
                                                      String description) {
        Color color = state.equalsIgnoreCase("opened") ? PR_COLOR :
                state.equalsIgnoreCase("closed") ? ISSUE_COLOR : Color.GRAY;

        return new EmbedBuilder()
                .setTitle("Pull Request: " + title, prUrl)
                .setColor(color)
                .addField("Autor", author, true)
                .addField("Estado", state, true)
                .addField("Descrição", description.length() > 1024 ?
                        description.substring(0, 1021) + "..." : description, false)
                .setFooter("GitHub Webhook")
                .setTimestamp(Instant.now())
                .build();
    }

    public static MessageEmbed createIssueEmbed(String title, String author,
                                                String issueUrl, String state,
                                                String description) {
        Color color = state.equalsIgnoreCase("opened") ? ISSUE_COLOR :
                state.equalsIgnoreCase("closed") ? COMMIT_COLOR : Color.GRAY;

        return new EmbedBuilder()
                .setTitle("Issue: " + title, issueUrl)
                .setColor(color)
                .addField("Autor", author, true)
                .addField("Estado", state, true)
                .addField("Descrição", description.length() > 1024 ?
                        description.substring(0, 1021) + "..." : description, false)
                .setFooter("GitHub Webhook")
                .setTimestamp(Instant.now())
                .build();
    }
}
