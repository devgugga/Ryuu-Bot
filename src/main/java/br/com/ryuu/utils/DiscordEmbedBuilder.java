package br.com.ryuu.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class DiscordEmbedBuilder {

    private static final Color COMMIT_COLOR = new Color(46, 160, 67);
    private static final Color PR_COLOR = new Color(130, 80, 223);
    private static final Color ISSUE_COLOR = new Color(206, 60, 60);
    private static final Color STAR_COLOR = new Color(255, 215, 0);
    private static final Color FORK_COLOR = new Color(35, 103, 255);

    /**
     * Creates a Discord embed message for a GitHub push event.
     *
     * @param author     The name of the author who made the push.
     * @param commits    A list of {@link CommitInfo} objects representing the commits made in the push.
     * @param repoName   The name of the repository where the push occurred.
     * @param branch     The name of the branch that was pushed.
     * @param compareUrl The URL to compare the commits between the old and new commit.
     * @return A {@link MessageEmbed} object containing the formatted push event information.
     */
    public static MessageEmbed createPushEmbed(String author, List<CommitInfo> commits,
                                               String repoName, String branch, String compareUrl) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Push para " + repoName, compareUrl)
                .setColor(COMMIT_COLOR)
                .addField("Autor", author, true)
                .addField("Branch", branch, true)
                .addField("Quantidade", commits.size() + " commit(s)", true);

        StringBuilder commitMessages = new StringBuilder();
        commits.stream()
                .limit(5)
                .forEach(commit -> {
                    commitMessages.append("• [`").append(commit.getId(), 0, 7)
                            .append("`](").append(commit.getUrl()).append(") ")
                            .append(truncateMessage(commit.getMessage()))
                            .append("\n");
                });

        if (commits.size() > 5) {
            commitMessages.append("... e mais ").append(commits.size() - 5).append(" commit(s)");
        }

        embed.addField("Commits", commitMessages.toString(), false)
                .setFooter("GitHub • Push Event")
                .setTimestamp(Instant.now());

        return embed.build();
    }

    /**
     * Creates a Discord embed message for a GitHub star event.
     *
     * @param user       The username of the user who starred the repository.
     * @param repoName   The name of the repository that was starred.
     * @param repoUrl    The URL of the repository that was starred.
     * @param totalStars The total number of stars the repository has after this event.
     * @return A {@link MessageEmbed} object containing the formatted star event information.
     */
    public static MessageEmbed createStarEmbed(String user, String repoName,
                                               String repoUrl, int totalStars) {
        return new EmbedBuilder()
                .setTitle("⭐ Nova Star em " + repoName, repoUrl)
                .setColor(STAR_COLOR)
                .addField("Usuário", user, true)
                .addField("Total de Stars", String.valueOf(totalStars), true)
                .setFooter("GitHub • Star Event")
                .setTimestamp(Instant.now())
                .build();
    }

    /**
     * Creates a Discord embed message for a GitHub fork event.
     *
     * @param user         The username of the user who forked the repository.
     * @param originalRepo The name of the original repository that was forked.
     * @param forkUrl      The URL of the newly created fork.
     * @param forkName     The name of the newly created fork.
     * @param totalForks   The total number of forks the original repository has after this event.
     * @return A {@link MessageEmbed} object containing the formatted fork event information.
     */
    public static MessageEmbed createForkEmbed(String user, String originalRepo,
                                               String forkUrl, String forkName, int totalForks) {
        return new EmbedBuilder()
                .setTitle("\uD83C\uDF74 Novo Fork de " + originalRepo, forkUrl)
                .setColor(FORK_COLOR)
                .addField("Usuário", user, true)
                .addField("Fork", forkName, true)
                .addField("Total de Forks", String.valueOf(totalForks), true)
                .setFooter("GitHub • Fork Event")
                .setTimestamp(Instant.now())
                .build();
    }

    /**
     * Creates a Discord embed message for a GitHub release event.
     *
     * @param repoName     The name of the repository where the release was created.
     * @param tagName      The tag name or version number of the release.
     * @param authorName   The name of the author who created the release.
     * @param releaseUrl   The URL of the GitHub release page.
     * @param description  The description or changelog of the release.
     * @param isPreRelease A boolean indicating whether this is a pre-release (true) or a full release (false).
     * @return A {@link MessageEmbed} object containing the formatted release event information.
     */
    public static MessageEmbed createReleaseEmbed(String repoName, String tagName,
                                                  String authorName, String releaseUrl,
                                                  String description, boolean isPreRelease) {
        Color releaseColor = isPreRelease ? new Color(255, 165, 0) : new Color(0, 150, 255);

        return new EmbedBuilder()
                .setTitle("\uD83D\uDCE6 Nova " + (isPreRelease ? "Pre-Release" : "Release") +
                        " em " + repoName, releaseUrl)
                .setColor(releaseColor)
                .addField("Versão", tagName, true)
                .addField("Autor", authorName, true)
                .addField("Descrição", truncateMessage(description), false)
                .setFooter("GitHub • Release Event")
                .setTimestamp(Instant.now())
                .build();
    }

    /**
     * Represents information about a single commit in a GitHub repository.
     */
    public static class CommitInfo {
        private final String id;
        private final String message;
        private final String url;

        /**
         * Constructs a new CommitInfo object with the specified details.
         *
         * @param id      The unique identifier (hash) of the commit.
         * @param message The commit message associated with this commit.
         * @param url     The URL to view this commit on GitHub.
         */
        public CommitInfo(String id, String message, String url) {
            this.id = id;
            this.message = message;
            this.url = url;
        }

        /**
         * Retrieves the commit identifier.
         *
         * @return The unique identifier (hash) of the commit.
         */
        public String getId() {
            return id;
        }

        /**
         * Retrieves the commit message.
         *
         * @return The message associated with this commit.
         */
        public String getMessage() {
            return message;
        }

        /**
         * Retrieves the URL of the commit.
         *
         * @return The URL to view this commit on GitHub.
         */
        public String getUrl() {
            return url;
        }
    }

    /**
     * Truncates a given message to a single line with a maximum length of 100 characters.
     * If the message is null, it returns a default message.
     *
     * @param message The input message to be truncated. Can be null.
     * @return A string containing either:
     * - "Sem descrição" if the input is null
     * - The first line of the input message, trimmed and truncated to 100 characters if necessary
     * (with "..." appended if truncated)
     */
    private static String truncateMessage(String message) {
        if (message == null) return "Sem descrição";
        String firstLine = message.split("\n")[0].trim();
        return firstLine.length() > 100 ? firstLine.substring(0, 97) + "..." : firstLine;
    }
}