package notebook.model.repository.impl;

import notebook.util.mapper.impl.UserMapper;
import notebook.model.User;
import notebook.model.repository.GBRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements GBRepository {
    private List<User> cachedUsers;
    private boolean hasChanged;
    private final UserMapper mapper;
    private final String fileName;
        public UserRepository(String fileName) {
        this.fileName = fileName;
        this.mapper = new UserMapper();
        cachedUsers = readUsers();
        hasChanged = false;
    }

    @Override
    public List<User> findAll() {
        return cachedUsers;
    }

    private List<User> readUsers(){
        List<String> lines = readAllLines();
        List<User> users = new ArrayList<>();
        for (String line : lines) {
            users.add(mapper.toOutput(line));
        }
        return users;
    };



    private List<String> readAllLines() {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(fileName);
            //создаем объект FileReader для объекта File
            FileReader fr = new FileReader(file);
            //создаем BufferedReader с существующего FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr);
            // считаем сначала первую строку
            String line = reader.readLine();
            if (line != null) {
                lines.add(line);
            }
            while (line != null) {
                // считываем остальные строки в цикле
                line = reader.readLine();
                if (line != null) {
                    lines.add(line);
                }
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
    @Override

      public User create(User user) {

        long max = 0L;
        for (User u : cachedUsers) {
            long id = u.getId();
            if (max < id){
                max = id;
            }
        }
        long next = max + 1;
        user.setId(next);
        cachedUsers.add(user);
        this.hasChanged = true;
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> update(Long userId, User update) {
        List<User> users = findAll();
        User editUser = users.stream()
                .filter(u -> u.getId()
                        .equals(userId))
                .findFirst().orElseThrow(() -> new RuntimeException("User not found"));

        if(!update.getFirstName().isEmpty()){
            editUser.setFirstName(update.getFirstName());
        }else{
            editUser.setFirstName(editUser.getFirstName());
        }

        editUser.setLastName(update.getLastName().isEmpty() ? editUser.getLastName() : update.getLastName());
        editUser.setPhone(update.getPhone().isEmpty() ? editUser.getPhone() : update.getPhone());

        hasChanged = true;
        return Optional.of(update);
    }

    @Override
    public boolean delete(Long id) {
        List<User> users = findAll();
        User delUser = users.stream()
                .filter(u -> u.getId()
                        .equals(id))
                .findFirst().orElseThrow(() -> new RuntimeException("User not found"));

        users.remove(delUser);
        hasChanged = true;
        return true;
    }

    @Override
    public void save() {
        // System.out.println("hasChanged = " + hasChanged);
        if(this.hasChanged) write();
        this.hasChanged = false;
    }

        private void write() {
        List<String> lines = new ArrayList<>();
        for (User u: cachedUsers) {
            lines.add(mapper.toInput(u));
        }
        saveAll(lines);
    }

    private void saveAll(List<String> data) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            for (String line : data) {
                // запись всей строки
                writer.write(line);
                // запись по символам
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public User createUser(String firstName, String lastName, String phone) {
        return new User(firstName, lastName, phone);
    }

    public boolean isHasChanged() {
        return hasChanged;
    }

}
