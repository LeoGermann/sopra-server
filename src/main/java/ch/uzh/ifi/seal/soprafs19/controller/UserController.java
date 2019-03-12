package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    Iterable<User> all() {
        return service.getUsers();
    }

    /* original:
        @PostMapping("/users")
        User createUser(@RequestBody User newUser) {
            return this.service.createUser(newUser);
        } */
    @PostMapping("/users")
    ResponseEntity<User> createUser(@RequestBody User newUser) {
        if(this.service.userExistsByUsername(newUser.getUsername())){
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(this.service.createUser(newUser), HttpStatus.CREATED);
    }

    @PostMapping(value = "/users/login")
    ResponseEntity<User> login(@RequestBody User test){
        String username = test.getUsername();
        String password = test.getPassword();

        if (this.service.userExistsByUsername(username))
            if (this.service.correctPassword(username, password)) {
                return new ResponseEntity<>(this.service.getUserByUsername(username), HttpStatus.OK);
            }
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/users/{userId}")
    ResponseEntity<User> getUserProfile(@RequestBody long userId) {
        if (this.service.userExistsById(userId)) {
            return new ResponseEntity<>(this.service.getUserById(userId), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/users/{userId}")
    ResponseEntity<User> editUserProfile(@RequestBody User user) {
        //needs the Token and Password to confirm validity
        //send changed birthday and/or username to update

        if (this.service.userExistsById(user.getId())) {

            if (this.service.checkUser(user)) {

                if(user.getUsername() == null || !this.service.userExistsByUsername(user.getUsername())) {
                    this.service.updateUser(user);
                    //success
                    return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
                }
            }
            //if credentials (token+password) were wrong or username was already taken
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        //if the userId doesn't exist
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


/* From online guide how to do it
    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBook(@PathVariable("isbn") String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(book -> new ResponseEntity<>(book, HttpStatus.OK))
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }
*/

}