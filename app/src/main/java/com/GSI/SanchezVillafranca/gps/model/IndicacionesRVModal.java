package com.GSI.SanchezVillafranca.gps.model;

public class IndicacionesRVModal {

    private String action;
    private int icon;
    private String instruction;

    private String direction;

    public IndicacionesRVModal() {
    }

    public IndicacionesRVModal(String action, int icon, String instruction, String direction) {
        this.action = action;
        this.icon = icon;
        this.instruction = instruction;
        this.direction = direction;
    }

    public IndicacionesRVModal(String action, String instruction, String direction) {
        this.action = action;
        this.icon = icon;
        this.instruction = instruction;
        this.direction = direction;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "IndicacionesRVModal{" +
                "action='" + action + '\'' +
                ", icon='" + icon + '\'' +
                ", instruction='" + instruction + '\'' +
                '}';
    }
}
