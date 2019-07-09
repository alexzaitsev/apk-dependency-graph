package code;

public class UserFilters {

    List<String> regex = new ArrayList<>();

    public UserFilters(List<String> regex) {
        this.regex.addAll(regex);
    }

    public boolean isClassOk(String className) {
        return true;
    }
}