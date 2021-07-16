package com.dolphln.npcfindminigame.files;

public class ConfigNPC {

    private final String npcName;
    private final String texture;
    private final String signature;

    public ConfigNPC(String npcName, String texture, String signature) {
        this.npcName = npcName;
        this.texture = texture;
        this.signature = signature;
    }

    public String getNpcName() {
        return npcName;
    }

    public String getTexture() {
        return texture;
    }

    public String getSignature() {
        return signature;
    }
}
