package pt.ubi.di.pmd.nothing2lose;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * The Award class represents an award with a price and a category.
 * It implements the Serializable interface to allow objects of this class to be serialized.
 */
public class Award implements Serializable {

    // The price of the award
    int price;

    // The category of the award
    int category;

    // Strings representing the category names
    String[] categoryStrings = {"Simple", "Medium", "Rare", "Legendary!"};

    // Colors corresponding to each category
    String[] categoryColors = {"#F0FFFF", "#0000FF", "#FFD700", "#FF4500"};

    /**
     * Constructs an Award object with the given price and category.
     *
     * @param price    The price of the award.
     * @param category The category of the award.
     */
    public Award(int price, int category) {
        this.price = price;
        this.category = category;
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
     * Retrieves the string representation of the award's category.
     *
     * @return The string representation of the category.
     */
    public String getCategoryString() {
        return categoryStrings[this.category];
    }

    /**
     * Retrieves the color associated with the award's category.
     *
     * @return The color associated with the category.
     */
    public String getCategoryColor() {
        return categoryColors[this.category];
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

