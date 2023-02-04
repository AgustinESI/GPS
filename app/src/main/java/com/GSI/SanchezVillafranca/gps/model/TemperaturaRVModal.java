package com.GSI.SanchezVillafranca.gps.model;

public class TemperaturaRVModal {


    private String hora;
    private String temperatura;
    private String icon;
    private String velocidad_viento;

    public TemperaturaRVModal() {
    }

    public TemperaturaRVModal(String hora, String temperatura, String icon, String velocidad_viento) {
        this.hora = hora;
        this.temperatura = temperatura;
        this.icon = icon;
        this.velocidad_viento = velocidad_viento;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getVelocidad_viento() {
        return velocidad_viento;
    }

    public void setVelocidad_viento(String velocidad_viento) {
        this.velocidad_viento = velocidad_viento;
    }

    @Override
    public String toString() {
        return "TemperaturaRVModal{" +
                "hora='" + hora + '\'' +
                ", temperatura='" + temperatura + '\'' +
                ", icon='" + icon + '\'' +
                ", velocidad_viento='" + velocidad_viento + '\'' +
                '}';
    }
}
