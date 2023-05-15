package pt.ubi.di.pmd.nothing2lose;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Award implements Serializable {
    int price;
    int category;
    String [] categoryStrings = {"Simple", "Medium", "Rare" , "Legendary!"};
    String [] categoryColors = {"#F0FFFF", "#0000FF", "#FFD700" , "#FF4500"};

    public Award(int price, int category) {
        this.price = price;
        this.category = category;
    }

    public int getPrice() {
        return price;
    }

    public String getCategoryString() {
        return categoryStrings[this.category];
    }

    public String getCategoryColor() {
        return categoryColors[this.category];
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
