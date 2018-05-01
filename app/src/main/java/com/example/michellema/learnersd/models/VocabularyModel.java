package com.example.michellema.learnersd.models;

/**
 * Created by michellema on 2018-03-26.
 */

public class VocabularyModel {
    private  String mot;
    private float niveauDifficulte;

    public String getMot() {
        return mot;
    }

    public float getNiveauDifficulte() {
        return niveauDifficulte;
    }

    public void setMot(String mot) {
        this.mot = mot;
    }

    public void setNiveauDifficulte(float niveauDifficulte) {
        this.niveauDifficulte = niveauDifficulte;
    }
}

