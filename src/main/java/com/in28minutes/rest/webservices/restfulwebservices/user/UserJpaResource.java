package com.in28minutes.rest.webservices.restfulwebservices.user;

import com.in28minutes.rest.webservices.restfulwebservices.jpa.PostRepository;
import com.in28minutes.rest.webservices.restfulwebservices.jpa.UserRepository;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@RestController
public class UserJpaResource {

    private UserRepository userRepository;

    private PostRepository postRepository;

    public UserJpaResource(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @GetMapping("/jpa/users")
    public List<User> retrieveAllUsers() {
        return userRepository.findAll();
    }


    // EntityModel
    //WebMvcLinkBuilder
    // add link to user without changing user bean
    @GetMapping("/jpa/users/{id}")
    public EntityModel<User> retrieveUser(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("id " + id);
        }
        EntityModel<User> entityModel = EntityModel.of(user.get());
        //make link to method in controller
        WebMvcLinkBuilder link = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
        entityModel.add(link.withRel("all-users"));
        return entityModel;
    }

    @PostMapping("/jpa/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);
        // /users/4 => /users/{id}, user.getId()
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        // header to show user after it create
        // location - /users/4
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/jpa/users/{id}")
    public void deleteUser(@PathVariable int id) {
        userRepository.deleteById(id);
    }

    @GetMapping("/jpa/users/{id}/posts")
    public List<Post> retrievePostsForUser(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("id " + id);
        }
        return user.get().getPosts();
    }

    @PostMapping("/jpa/users/{id}/posts")
    public ResponseEntity<Object> createPostForUser(@PathVariable int id, @Valid @RequestBody Post post) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("id " + id);
        }
        post.setUser(user.get());
        Post savedPost = postRepository.save(post);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPost.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/jpa/users/{idUser}/posts/{idPost}")
    public ResponseEntity<Object> retrievePostFromUser(@PathVariable Integer idUser, @PathVariable Integer idPost) {
        Optional<User> user = userRepository.findById(idUser);
        User userReal = user.orElseGet(null);
        if (userReal != null) {
            List<Post> posts = userReal.getPosts();
            Predicate<Post> predicate = post -> post.getId().equals(idPost);
            Post post = posts.stream().filter(predicate).findFirst().orElse(null);
            return ResponseEntity.ok(post);
        } else {
            throw new UserNotFoundException("User did not find");
        }

    }

}
