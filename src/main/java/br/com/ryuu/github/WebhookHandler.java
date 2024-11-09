package br.com.ryuu.github;

import br.com.ryuu.config.Config;
import br.com.ryuu.utils.DiscordEmbedBuilder;
import br.com.ryuu.utils.DiscordEmbedBuilder.CommitInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import spark.Spark;

import java.util.ArrayList;
import java.util.List;

public class WebhookHandler {
    private final JDA jda;
    private final String channelId;

    /**
     * Constructs a new instance of the {@code WebhookHandler} class.
     * This class is responsible for handling incoming webhooks from GitHub and posting relevant information to a Discord channel.
     *
     * @param jda The JDA instance representing the Discord bot.
     * @throws NullPointerException If the provided {@code jda} is {@code null}.
     */
    public WebhookHandler(JDA jda) {
        if (jda == null) {
            throw new NullPointerException("JDA instance cannot be null");
        }

        this.jda = jda;
        this.channelId = Config.getChannelId();
        setupEndpoints();
    }

    /**
     * Sets up the necessary endpoints for handling incoming webhooks from GitHub.
     * This method configures the Spark framework to listen for POST requests at the "/webhook" endpoint.
     * When a request is received, it extracts the event type and payload from the request headers and body,
     * and then calls the {@link #processWebhook(String, String)} method to handle the webhook.
     * Finally, it logs a message indicating that the webhook server has started on the specified port.
     */
    private void setupEndpoints() {
        // Retrieve the port number from the configuration or use a default value of 8080
        int port = Integer.parseInt(Config.get("PORT", "8080"));

        // Set the port for the Spark framework
        Spark.port(port);

        // Define a POST endpoint for handling webhooks
        Spark.post("/webhook", (request, response) -> {
            // Extract the event type and payload from the request headers and body
            String eventType = request.headers("X-GitHub-Event");
            String payload = request.body();

            // Process the webhook by calling the processWebhook method
            processWebhook(eventType, payload);

            // Set the response status code to 200 and return a success message
            response.status(200);
            return "Webhook received";
        });

        // Log a message indicating that the webhook server has started on the specified port
        System.out.println("Webhook server started on port " + port);
    }

    /**
     * Processes the incoming webhook from GitHub based on the event type and payload.
     *
     * @param eventType The type of event that triggered the webhook.
     * @param payload   The payload containing the details of the event.
     * @throws Exception If an error occurs while processing the webhook.
     */
    private void processWebhook(String eventType, String payload) {
        try {
            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
            TextChannel channel = jda.getTextChannelById(channelId);

            // Check if the channel exists
            if (channel == null) {
                System.err.println("Canal não encontrado: " + channelId);
                return;
            }

            // Switch statement to handle different event types
            switch (eventType) {
                case "push":
                    handlePushEvent(json, channel);
                    break;
                case "star":
                    handleStarEvent(json, channel);
                    break;
                case "fork":
                    handleForkEvent(json, channel);
                    break;
                case "release":
                    handleReleaseEvent(json, channel);
                    break;
                default:
                    System.out.println("Evento não processado: " + eventType);
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the "push" event received from GitHub.
     * This method extracts relevant information from the provided JSON object and sends an embed message to the specified Discord channel.
     *
     * @param json    The JSON object containing the webhook payload.
     * @param channel The Discord channel where the embed message will be sent.
     */
    private void handlePushEvent(JsonObject json, TextChannel channel) {
        String repoName = json.getAsJsonObject("repository").get("full_name").getAsString();
        String author = json.getAsJsonObject("pusher").get("name").getAsString();
        String branch = json.get("ref").getAsString().replace("refs/heads/", "");
        String compareUrl = json.get("compare").getAsString();

        List<CommitInfo> commits = new ArrayList<>();
        JsonArray commitsArray = json.getAsJsonArray("commits");

        for (JsonElement commitElement : commitsArray) {
            JsonObject commit = commitElement.getAsJsonObject();
            commits.add(new CommitInfo(
                    commit.get("id").getAsString(),
                    commit.get("message").getAsString(),
                    commit.get("url").getAsString()
            ));
        }

        channel.sendMessageEmbeds(
                DiscordEmbedBuilder.createPushEmbed(author, commits, repoName, branch, compareUrl)
        ).queue();
    }

    /**
     * Handles the "star" event received from GitHub.
     * This method extracts relevant information from the provided JSON object and sends an embed message to the specified Discord channel.
     *
     * @param json    The JSON object containing the webhook payload.
     * @param channel The Discord channel where the embed message will be sent.
     * @throws NullPointerException If either {@code json} or {@code channel} is {@code null}.
     */
    private void handleStarEvent(JsonObject json, TextChannel channel) {
        if (json == null) {
            throw new NullPointerException("JSON object cannot be null");
        }
        if (channel == null) {
            throw new NullPointerException("Discord channel cannot be null");
        }

        JsonObject repo = json.getAsJsonObject("repository");
        String action = json.get("action").getAsString();

        if (action.equals("created")) {
            String user = json.getAsJsonObject("sender").get("login").getAsString();
            String repoName = repo.get("full_name").getAsString();
            String repoUrl = repo.get("html_url").getAsString();
            int totalStars = repo.get("stargazers_count").getAsInt();

            channel.sendMessageEmbeds(
                    DiscordEmbedBuilder.createStarEmbed(user, repoName, repoUrl, totalStars)
            ).queue();
        }
    }

    /**
     * Handles the "fork" event received from GitHub.
     * This method extracts relevant information from the provided JSON objects and sends an embed message to the specified Discord channel.
     *
     * @param json    The JSON object containing the webhook payload. This object must not be {@code null}.
     * @param channel The Discord channel where the embed message will be sent. This parameter must not be {@code null}.
     * @throws NullPointerException If either {@code json} or {@code channel} is {@code null}.
     */
    private void handleForkEvent(JsonObject json, TextChannel channel) {
        if (json == null) {
            throw new NullPointerException("JSON object cannot be null");
        }
        if (channel == null) {
            throw new NullPointerException("Discord channel cannot be null");
        }

        JsonObject repo = json.getAsJsonObject("repository");
        JsonObject forkRepo = json.getAsJsonObject("forkee");

        String user = json.getAsJsonObject("sender").get("login").getAsString();
        String originalRepo = repo.get("full_name").getAsString();
        String forkUrl = forkRepo.get("html_url").getAsString();
        String forkName = forkRepo.get("full_name").getAsString();
        int totalForks = repo.get("forks_count").getAsInt();

        channel.sendMessageEmbeds(
                DiscordEmbedBuilder.createForkEmbed(user, originalRepo, forkUrl, forkName, totalForks)
        ).queue();
    }

    /**
     * Handles the "release" event received from GitHub.
     * This method extracts relevant information from the provided JSON objects and sends an embed message to the specified Discord channel.
     *
     * @param json    The JSON object containing the webhook payload. This object must not be {@code null}.
     * @param channel The Discord channel where the embed message will be sent. This parameter must not be {@code null}.
     * @throws NullPointerException If either {@code json} or {@code channel} is {@code null}.
     */
    private void handleReleaseEvent(JsonObject json, TextChannel channel) {
        if (json == null) {
            throw new NullPointerException("JSON object cannot be null");
        }
        if (channel == null) {
            throw new NullPointerException("Discord channel cannot be null");
        }

        String action = json.get("action").getAsString();
        if (action.equals("published")) {
            JsonObject release = json.getAsJsonObject("release");
            JsonObject repo = json.getAsJsonObject("repository");

            String repoName = repo.get("full_name").getAsString();
            String tagName = release.get("tag_name").getAsString();
            String authorName = release.getAsJsonObject("author").get("login").getAsString();
            String releaseUrl = release.get("html_url").getAsString();
            String description = release.get("body").getAsString();
            boolean isPreRelease = release.get("prerelease").getAsBoolean();

            channel.sendMessageEmbeds(
                    DiscordEmbedBuilder.createReleaseEmbed(repoName, tagName, authorName,
                            releaseUrl, description, isPreRelease)
            ).queue();
        }
    }
}