package ba.nosite.chatsystem.core.services;

import ba.nosite.chatsystem.core.dto.userDtos.UserInfo;
import ba.nosite.chatsystem.core.dto.userDtos.UserResponseWithoutId;
import ba.nosite.chatsystem.core.dto.userDtos.UsersResponse;
import ba.nosite.chatsystem.core.exceptions.auth.UserNotFoundException;
import ba.nosite.chatsystem.core.models.chat.DirectMessaging;
import ba.nosite.chatsystem.core.models.user.Friend;
import ba.nosite.chatsystem.core.models.user.Role;
import ba.nosite.chatsystem.core.models.user.User;
import ba.nosite.chatsystem.core.repository.UserRepository;
import ba.nosite.chatsystem.core.services.authServices.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ba.nosite.chatsystem.core.services.authServices.JwtService.extractJwtFromHeader;


@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    public User findUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElse(null);
    }

    public User findUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElse(null);
    }

    public User findUserByUsernameOrEmail(String usernameOrEmail) {
        Optional<User> optionalUser = userRepository.findByEmailOrUsername(usernameOrEmail);
        return optionalUser.orElse(null);
    }

    public User findUserByVerificationCode(String verificationCode) {
        Optional<User> optionalUser = userRepository.findByVerificationCode(verificationCode);
        return optionalUser.orElse(null);
    }

    public UsersResponse save(User newUser) {
        if (newUser.get_id() == null) {
            newUser.setCreatedAt(LocalTime.now());
            newUser.setRole(Role.ROLE_USER);
        }
        newUser.setUpdatedAt(LocalTime.now());

        return new UsersResponse(userRepository.save(newUser));
    }

    public List<UsersResponse> listUserResponse() {
        List<User> users = userRepository.findAll();

        return users
                .stream()
                .map(UsersResponse::new)
                .collect(Collectors.toList());
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public UserResponseWithoutId update(String userId, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(userId);

        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            if (updatedUser.getFirst_name() != null) {
                userToUpdate.setFirst_name(updatedUser.getFirst_name());
            }
            if (updatedUser.getLast_name() != null) {
                userToUpdate.setLast_name(updatedUser.getLast_name());
            }
            if (updatedUser.getEmail() != null) {
                userToUpdate.setEmail(updatedUser.getEmail());
            }
            updatedUser.setUpdatedAt(LocalTime.now());

            return new UserResponseWithoutId(userRepository.save(userToUpdate));
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    public void deleteById(String userId) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException("User not found with ID: ".concat(userId));
        }
    }

    public UserInfo getUserByAuthHeader(String authHeader) {
        String jwt = extractJwtFromHeader(authHeader);
        String username = jwtService.extractUsername(jwt);

        Optional<User> potentialUser = userRepository.findByUsername(username);

        User user = potentialUser.orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserInfo(
                user.get_id(),
                user.getUsername(),
                user.getFull_name(),
                user.getEmail(),
                user.getAvatarImageUrl()
        );
    }

    public User getUserById(String userId) {
        Optional<User> potentialUser = userRepository.findById(userId);

        if (potentialUser.isPresent()) {
            return potentialUser.get();
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    public boolean checkIfUserIsInDatabaseByEmail(String email) {
        Optional<User> potentialUser = userRepository.findByEmail(email);
        return potentialUser.isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmailOrUsername(username);

        return user.orElse(null);
    }

    public List<Friend> getFriends(String authHeader) {
        String userId = extractUserIdFromHeader(authHeader);
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        return user.getFriends();
    }

    public UsersResponse addFriend(String authHeader, String friendId) {
        String userId = extractUserIdFromHeader(authHeader);
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<User> friendOptional = userRepository.findById(friendId);

        if (userOptional.isPresent() && friendOptional.isPresent()) {
            User user = userOptional.get();
            User friend = friendOptional.get();

            if (user.getFriends().stream().noneMatch(f -> f._id().equals(friendId))) {
                Friend newFriend = new Friend(
                        friend.get_id(),
                        friend.getUsername(),
                        friend.getFull_name(),
                        friend.getEmail(),
                        friend.getAvatarImageUrl()
                );

                user.getFriends().add(newFriend);
                user.setUpdatedAt(LocalTime.now());

                return new UsersResponse(userRepository.save(user));
            } else {
                throw new RuntimeException("User is already friends with the specified friend.");
            }
        } else {
            throw new UserNotFoundException("User or friend not found");
        }
    }

    public UsersResponse deleteFriend(String authHeader, String friendId) {
        String userId = extractUserIdFromHeader(authHeader);
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            user.setFriends(user.getFriends().stream()
                    .filter(friend -> !friend._id().equals(friendId))
                    .collect(Collectors.toList()));

            user.setUpdatedAt(LocalTime.now());

            return new UsersResponse(userRepository.save(user));
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    public List<UserInfo> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    public List<DirectMessaging> getDirectMessagings(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.map(User::getDirectMessagings).orElse(null);
    }

    public void saveDirectMessaging(String userId, DirectMessaging directMessaging) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<DirectMessaging> directMessagings = user.getDirectMessagings();
            directMessagings.add(directMessaging);
            userRepository.save(user);
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    public void deleteDirectMessaging(String userId, String directMessagingId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<DirectMessaging> directMessagings = user.getDirectMessagings();

            directMessagings.removeIf(dm -> dm.get_id().equals(directMessagingId));

            userRepository.save(user);
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);

        }
    }

    private String extractUserIdFromHeader(String authHeader) {
        String jwt = extractJwtFromHeader(authHeader);
        return jwtService.extractCustomClaim(jwt, "user_id");
    }
}
