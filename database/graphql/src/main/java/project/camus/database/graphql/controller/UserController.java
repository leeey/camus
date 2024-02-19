package project.camus.database.graphql.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import project.camus.database.graphql.query.User;

@Controller
public class UserController {

    @QueryMapping
    public User findById(@Argument String id) {

        return User.findById(id);
    }
}
