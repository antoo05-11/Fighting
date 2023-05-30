package com.example.fighting.character;

import com.example.fighting.Position;

public class CharacterConfiguration {
    private Position initialPos;
    private int characterID;

    public CharacterConfiguration(Position initialPos, int characterID) {
        this.initialPos = initialPos;
        this.characterID = characterID;
    }

    public Position getInitialPos() {
        return initialPos;
    }

    public void setInitialPos(Position initialPos) {
        this.initialPos = initialPos;
    }

    public int getCharacterID() {
        return characterID;
    }

    public void setCharacterID(int characterID) {
        this.characterID = characterID;
    }
}
