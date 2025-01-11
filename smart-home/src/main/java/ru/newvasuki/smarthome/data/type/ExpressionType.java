package ru.newvasuki.smarthome.data.type;

public enum ExpressionType {
    UNDEFINED(0, "UNDEFINED"),
    MORE_OR_EQUALS(1, "Больше или равно"),
    LESS_OR_EQUALS(2, "Меньше или равно");

    private Integer id;
    private String name;

    ExpressionType(Integer id, String name) {
        this.id = id;
        this.name = name;
        ExpressionTypeSingleton.lookupStr.put(name, this);
        ExpressionTypeSingleton.lookupInt.put(id, this);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static ExpressionType get(String name) {
        return ExpressionTypeSingleton.lookupStr.get(name);
    }

    public static ExpressionType get(Integer id) {
        return ExpressionTypeSingleton.lookupInt.get(id);
    }
}
