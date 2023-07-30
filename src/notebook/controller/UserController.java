package notebook.controller;

import notebook.model.User;
import notebook.model.repository.GBRepository;
import notebook.util.Commands;
import notebook.view.UserView;

import java.util.List;
import java.util.Objects;

public class UserController {
    private final GBRepository repository;
    private final UserView view;

    public UserController(GBRepository repository, UserView view) {
        this.repository = repository;
        this.view = view;
    }


    public void run(){
        Commands com;

        while (true) {
            String command = view.prompt("Введите команду: ");
            try {
                com = Commands.valueOf(command);
            }catch (IllegalArgumentException e){
                com = Commands.UNKNOWN;
            }

            if (com == Commands.EXIT) return;
            switch (com) {
                case SHOW_LIST:
                    List<User> users = readAll();
                    view.showMessage(users.toString());
                    break;
                case CREATE:
                    User u = createUser();
                    saveUser(u);
                    break;
                case READ:
                    String id = view.prompt("Идентификатор пользователя: ");
                    try {
                        User user = readUser(Long.parseLong(id));
                        view.showMessage(user.toString());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case UPDATE:
                    String userId = view.prompt("Enter user id: ");
                    updateUser(userId, createUser());
                    break;
                case DELETE:
                    deleteUser(view.prompt("Enter user id: "));
                    break;
                case UNKNOWN:
                    view.showMessage("Команда не распознана! Повторите.");
                    break;
            }
        }
    }


    private User createUser() {
        String firstName = view.prompt("Имя: ");
        String lastName = view.prompt("Фамилия: ");
        String phone = view.prompt("Номер телефона: ");
        return new User(firstName, lastName, phone);
    }


    public void saveUser(User user) {
        repository.create(user);
    }

    public User readUser(Long userId) throws Exception {
        List<User> users = repository.findAll();
        for (User user : users) {
            if (Objects.equals(user.getId(), userId)) {
                return user;
            }
        }

        throw new RuntimeException("User not found");
    }

    public void updateUser(String userId, User update) {
        update.setId(Long.parseLong(userId));
        repository.update(Long.parseLong(userId), update);
    }

    public List<User> readAll() {
        return repository.findAll();
    }

    public void deleteUser(String userId) {
        repository.delete(Long.parseLong(userId));
    }

}
