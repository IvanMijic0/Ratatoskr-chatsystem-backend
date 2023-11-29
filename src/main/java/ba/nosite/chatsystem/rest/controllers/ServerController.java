package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.services.ServerService;
import ba.nosite.chatsystem.core.services.authServices.JwtService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/server")
public class ServerController {
    private final ServerService serverService;

    public ServerController(ServerService serverService, JwtService jwtService) {
        this.serverService = serverService;
    }

    @PostMapping("")
    public ResponseEntity<?> uploadAvatar(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("avatarIcon") MultipartFile avatarIcon,
            @RequestParam("serverName") String serverName
    ) {

        System.out.println("This is my avatar icon ----->  " + avatarIcon.getOriginalFilename());
        System.out.println("This is my Server name ------>  " + serverName);

        try {
            serverService.saveServer(serverName, authHeader, avatarIcon);
            return ResponseEntity.ok("Successfully saved server");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(e.hashCode())).body(e.getMessage());
        }
    }
}
