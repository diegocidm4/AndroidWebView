package com.dcm.webview;

public class FicheroDescarga {
    private String nombre;
    private String extension;
    private String descripcion;

    public FicheroDescarga(String nombre, String extension, String descripcion) {
        this.nombre = nombre;
        this.extension = extension;
        this.descripcion = descripcion;
    }

    public FicheroDescarga() {
        this.nombre = null;
        this.extension = null;
        this.descripcion = null;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
