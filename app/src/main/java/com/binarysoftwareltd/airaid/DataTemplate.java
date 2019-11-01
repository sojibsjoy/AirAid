package com.binarysoftwareltd.airaid;

public class DataTemplate {
    private String name;
    private int piece;

    public DataTemplate() {

    }

    public DataTemplate(String name, int piece) {
        this.name = name;
        this.piece = piece;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPiece() {
        return piece;
    }

    public void setPiece(int piece) {
        this.piece = piece;
    }
}
