package pt.ubi.di.pmd.nothing2lose.utility;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * The Award class represents an award with a price and a category.
 * It implements the Serializable interface to allow objects of this class to be serialized.
 */
public class Award implements Serializable {

    // The price of the award
    int price;

    // The category of the award
    String category;

    // The color of the award
    String color;

    /**
     * Constructs an Award object with the given price and category.
     *
     * @param price    The price of the award.
     * @param category The category of the award.
     * @param color The color of the award.
     */
    public Award(int price, String category, String color) {
        this.price = price;
        this.category = category;
        this.color = color;
    }

    /**
     * Retrieves the price of the award.
     *
     * @return The price of the award.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Retrieves the award's category.
     *
     * @return The string representation of the category.
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * Retrieves the color associated with the award's category.
     *
     * @return The color associated with the category.
     */
    public String getColor() {
        return this.color;
    }

    /**
     * Returns a string representation of the Award object.
     *
     * @return A string representation of the Award object.
     */
    @Override
    public String toString() {
        return "Award{" +
                "price=" + price +
                ", category=" + category +
                ", color=" + color +
                '}';
    }

    /**
     * Converts the Award object to a byte array using serialization.
     *
     * @return The byte array representation of the Award object.
     * @throws Exception if an error occurs during serialization.
     */
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        oos.flush();
        return bos.toByteArray();
    }
}

