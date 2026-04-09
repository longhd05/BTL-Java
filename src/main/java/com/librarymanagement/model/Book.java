package com.librarymanagement.model;

public class Book {
    private String id;
    private String tenSach;
    private int soLuong;
    private String chuDe;
    private String tacGia;

    public Book() {
    }

    public Book(String id, String tenSach, int soLuong, String chuDe, String tacGia) {
        this.id = id;
        this.tenSach = tenSach;
        this.soLuong = soLuong;
        this.chuDe = chuDe;
        this.tacGia = tacGia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getChuDe() {
        return chuDe;
    }

    public void setChuDe(String chuDe) {
        this.chuDe = chuDe;
    }

    public String getTacGia() {
        return tacGia;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }
}
