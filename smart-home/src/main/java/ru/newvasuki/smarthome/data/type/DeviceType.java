package ru.newvasuki.smarthome.data.type;

public enum DeviceType {
    UNDEFINED(0, "UNDEFINED"),
    SENSOR(1, "Датчик"),
    EXECUTIVE(2, "Исполнительное устройство");

    private Integer id;
    private String name;

    DeviceType(Integer id, String name) {
        this.id = id;
        this.name = name;
        DeviceTypeSingleton.lookupStr.put(name, this);
        DeviceTypeSingleton.lookupInt.put(id, this);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static DeviceType get(String name) {
        return DeviceTypeSingleton.lookupStr.get(name);
    }

    public static DeviceType get(Integer id) {
        return DeviceTypeSingleton.lookupInt.get(id);
    }
}
