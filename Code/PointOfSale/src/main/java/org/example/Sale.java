package org.example;

public class Sale {
    // vars
    private String category;
    private String description;
    private int price;
    private int amount;

    // Construtores
    public Sale() {
    }
    // construtor
    public Sale(String category, String description, int price,int amount) {
        this.category = category;
        this.description = description;
        this.price = price;
        this.amount = amount;
    }

    // get da categoria
    public String getCategory() {
        return category;
    }

    // set da categoria
    public void setCategory(String category) {
        this.category = category;
    }

    // get da descricao
    public String getDescription() {
        return description;
    }

    // set da descricao
    public void setDescription(String description) {
        this.description = description;
    }

    // get do preco
    public int getPrice() {
        return price;
    }

    // set do preco
    public void setPrice(int price) {
        this.price = price;
    }

    // get da quantidade
    public int getAmount() {
        return amount;
    }

    // set da quantidade
    public void setAmount(int amount) {
        this.amount = amount;
    }

    // passar para string a mensagem
    @Override
    public String toString() {
        return "Sale{" +
                "category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price +  + '€' + '\'' +
                ", amount='" + amount +
                '}';
    }
}

