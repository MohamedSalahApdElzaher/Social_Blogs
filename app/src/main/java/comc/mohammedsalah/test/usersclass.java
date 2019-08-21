package comc.mohammedsalah.test;

public class usersclass {
    private String name , phone , image , id;
    public usersclass(){}
    public usersclass(String name, String phone, String image) {
        this.name = name;
        this.phone = phone;
        this.image = image;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getphone() {
        return phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    public String getimage() {
        return image;
    }

    public void setimage(String image) {
        this.image = image;
    }
}
