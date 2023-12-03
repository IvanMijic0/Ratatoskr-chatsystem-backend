package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.dto.chatDtos.ServerInfoResponse;
import ba.nosite.chatsystem.core.exceptions.auth.UserNotFoundException;
import ba.nosite.chatsystem.core.models.chat.ChannelCluster;
import ba.nosite.chatsystem.core.services.ServerService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/server")
public class ServerController {
    private final ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> uploadAvatar(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("avatarIcon") MultipartFile avatarIcon,
            @RequestParam("serverName") String serverName
    ) {

        try {
            serverService.saveServer(serverName, authHeader, avatarIcon);
            return ResponseEntity.ok("Successfully created server");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(e.hashCode())).body(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> findServersByMemberId(@RequestHeader("Authorization") String authHeader) {
        try {
            List<ServerInfoResponse> serverInfoResponse = serverService.findServerByMemberId(authHeader);

            return ResponseEntity.ok().body(serverInfoResponse);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(e.hashCode())).body(e.getMessage());
        }
    }

    @GetMapping("/channelClusters")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> findChannelsByServerId(@RequestParam String serverId) {
        try {
            List<ChannelCluster> channelInfoResponse = serverService.findChannelsByServerId(serverId);

            return ResponseEntity.ok().body(channelInfoResponse);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(e.hashCode())).body(e.getMessage());
        }
    }
}
