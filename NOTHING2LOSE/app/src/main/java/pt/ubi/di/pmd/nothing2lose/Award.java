package pt.ubi.di.pmd.nothing2lose;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Award implements Serializable {
    int price;
    int category; // 0 -> Simple (30s) // 1 -> Medium (60s) // 2 -> Rare (120s) // 3 -> Legendary (240s)

    public Award(int price, int category) {
        this.price = price;
        this.category = category;
    }

    public int getPrice() {
        return price;
    }

    public int getCategory() {
        return category;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Award{" +
                "price=" + price +
                ", category=" + category +
                '}';
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        oos.flush();
        return bos.toByteArray();
    }
}
