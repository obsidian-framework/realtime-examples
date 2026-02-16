package fr.kainovaii.obsidian.app.controllers;

import fr.kainovaii.obsidian.http.controller.BaseController;
import fr.kainovaii.obsidian.http.controller.annotations.Controller;
import fr.kainovaii.obsidian.routing.methods.GET;
import spark.Request;
import spark.Response;

import java.util.Map;

@Controller
public class HomeController extends BaseController
{
    @GET(value = "/", name = "examples.index")
    public String index(Request req, Response res)
    {
        return render("home.html", Map.of());
    }

    @GET(value = "chat", name = "examples.chat")
    public String chat(Request req, Response res)
    {
        return render("chat.html", Map.of());
    }

    @GET(value = "notifications", name = "examples.notifications")
    public String notifications(Request req, Response res)
    {
        return render("notifications.html", Map.of());
    }
}