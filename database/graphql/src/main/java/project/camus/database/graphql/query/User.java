package project.camus.database.graphql.query;


import java.util.List;

public record User(String id, String name, String email, Integer age) {

    private static final List<User> users = List.of(
        new User("u-1", "Alice", "qwer@example.com", 30),
        new User("u-2", "Bob", "asdf@example.com", 35),
        new User("u-3", "Charlie", "zxcv@example.com", 40),
        new User("u-4", "David", "1234@example.com", 45),
        new User("u-5", "Eve", "5678@example.com", 50)
    );

    public static User findById(String id) {

        return users.stream()
            .filter(user -> user.id().equals(id))
            .findFirst().orElse(null);
    }
}
