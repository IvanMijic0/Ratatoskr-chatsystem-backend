package ba.nosite.chatsystem.rest.controllers;

import ba.nosite.chatsystem.core.dto.chatDtos.ChannelClusterInfo;
import ba.nosite.chatsystem.core.dto.chatDtos.ServerInfoResponse;
import ba.nosite.chatsystem.core.exceptions.auth.UserNotFoundException;
import ba.nosite.chatsystem.core.models.chat.Channel;
import ba.nosite.chatsystem.core.services.ServerService;
import com.amazonaws.services.kms.model.NotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/channelClusters")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> findChannelsByServerId(@RequestParam String serverId) {
        try {
            List<ChannelClusterInfo> channelInfoResponse = serverService.findChannelClustersByServerId(serverId);

            return ResponseEntity.ok().body(channelInfoResponse);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/channelCluster")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> addChannelClusterToServer(
            @RequestParam String serverId,
            @RequestParam String channelClusterName
    ) {
        try {
            serverService.addChannelClusterToServer(serverId, channelClusterName);
            return ResponseEntity.ok("Successfully added channel cluster to server");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/channel")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> addChannelToCluster(
            @RequestParam String serverId,
            @RequestParam String channelClusterId,
            @RequestParam String channelName
    ) {
        try {
            serverService.addChannelToCluster(serverId, channelClusterId, channelName);
            return ResponseEntity.ok("Successfully added channel to cluster");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/channel")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getChannelByIds(
            @RequestParam String serverId,
            @RequestParam String channelClusterId,
            @RequestParam String channelId
    ) {
        try {
            Channel channelInfoResponse = serverService.getChannelByIds(serverId, channelClusterId, channelId);

            return ResponseEntity.ok().body(channelInfoResponse);
        } catch (UserNotFoundException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{serverId}/channelCluster/{clusterId}/channels")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void deleteChannelsInCluster(
            @PathVariable String serverId,
            @PathVariable String clusterId,
            @RequestBody String[] channelIds
    ) {
        serverService.deleteChannelsInCluster(serverId, clusterId, channelIds);
    }

    @DeleteMapping("/{serverId}/channelCluster/{clusterId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> deleteChannelCluster(
            @PathVariable String serverId,
            @PathVariable String clusterId
    ) {
        try {
            serverService.deleteChannelClusterById(serverId, clusterId);
            return ResponseEntity.ok("Channel cluster deleted successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{serverId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> deleteServer(
            @PathVariable String serverId
    ) {
        try {
            serverService.deleteServerById(serverId);
            return ResponseEntity.ok("Server deleted successfully");
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Server not found");
        }
    }

//    @GetMapping("/refreshImages")
//    public ResponseEntity<?> refreshImages() {
//
//    }
}