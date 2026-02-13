package fr.kainovaii.obsidian.app.controllers;

import fr.kainovaii.obsidian.core.web.controller.Controller;
import fr.kainovaii.obsidian.core.web.route.methods.SSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Controller
public class NotificationController
{
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private static final Random random = new Random();

    @SSE(value = "/notifications/stream", name = "notifications.stream")
    public Object stream(Request req, Response res)
    {
        res.raw().setCharacterEncoding("UTF-8");

        PrintWriter writer = null;

        try {
            writer = res.raw().getWriter();
            logger.info("SSE client connected: {}", req.ip());

            String[] types = {"Info", "Success", "Warning", "Alert"};
            String[] messages = {
                    "New user registration",
                    "Database backup completed",
                    "High memory usage detected",
                    "New comment on your post",
                    "Payment received",
                    "Server load increasing",
                    "New message from support",
                    "System update available",
                    "API rate limit reached",
                    "Deployment successful",
                    "Build failed",
                    "Security alert detected"
            };

            for (int i = 0; i < 24; i++) {
                String type = types[random.nextInt(types.length)];
                String message = messages[random.nextInt(messages.length)];
                String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                String notification = String.format(
                        "{\"type\":\"%s\",\"message\":\"%s\",\"time\":\"%s\"}",
                        type, message, time
                );

                writer.write("data: " + notification + "\n\n");
                writer.flush();

                logger.debug("Sent notification: {} - {}", type, message);

                Thread.sleep(5000 + random.nextInt(5000));
            }

            logger.info("SSE stream completed for client: {}", req.ip());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("SSE stream interrupted for client: {}", req.ip());
        } catch (Exception e) {
            logger.warn("SSE client disconnected: {}", req.ip());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return null;
    }
}