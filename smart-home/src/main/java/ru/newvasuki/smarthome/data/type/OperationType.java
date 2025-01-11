package ru.newvasuki.smarthome.data.type;

public enum OperationType {
    UNDEFINED(0, "UNDEFINED"),
    ON(1, "Включить"),
    OFF(2, "Выключить"),
    VALUE(3, "Получать значение");

    private Integer id;
    private String name;

    OperationType(Integer id, String name) {
        this.id = id;
        this.name = name;
        OperationTypeSingleton.lookupStr.put(name, this);
        OperationTypeSingleton.lookupInt.put(id, this);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static OperationType get(String name) {
        return OperationTypeSingleton.lookupStr.get(name);
    }

    public static OperationType get(Integer id) {
        return OperationTypeSingleton.lookupInt.get(id);
    }
}
