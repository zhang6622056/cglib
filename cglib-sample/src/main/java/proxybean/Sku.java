package proxybean;

public class Sku {
    private String name;
    private int height;
    private int width;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    @Override
    public String toString() {
        return "Sku{" +
                "name='" + name + '\'' +
                ", height=" + height +
                ", width=" + width +
                '}';
    }



}
