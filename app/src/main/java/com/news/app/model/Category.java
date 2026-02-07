package com.news.app.model;

import java.util.ArrayList;
import java.util.List;

public class Category {

    // ----------------------
    // Champs principaux
    // ----------------------
    private String id;                 // ID unique de la catégorie
    private String name;               // Nom affiché de la catégorie
    private String imageUrl;           // Image représentative
    private String description;        // Description courte
    private boolean isSelected;        // Si l'utilisateur a sélectionné cette catégorie
    private List<String> tags;         // Tags associés à la catégorie (optionnel)

    // ----------------------
    // Constructeurs
    // ----------------------
    public Category() {
        this.tags = new ArrayList<>();
    }

    public Category(String id, String name, String imageUrl, String description,
                    boolean isSelected, List<String> tags) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.isSelected = isSelected;
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    // ----------------------
    // Getters et Setters
    // ----------------------
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    // ----------------------
    // Méthodes utilitaires
    // ----------------------

    /**
     * Bascule l'état de sélection de la catégorie
     */
    public void toggleSelected() {
        this.isSelected = !this.isSelected;
    }

    /**
     * Ajoute un tag à la catégorie
     */
    public void addTag(String tag) {
        if (tag != null && !tag.isEmpty() && !tags.contains(tag)) {
            tags.add(tag);
        }
    }

    /**
     * Supprime un tag de la catégorie
     */
    public void removeTag(String tag) {
        if (tags.contains(tag)) {
            tags.remove(tag);
        }
    }

    /**
     * Vérifie si la catégorie contient un tag donné
     */
    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag);
    }

    /**
     * Validation rapide de la catégorie
     */
    public boolean isValidCategory() {
        return id != null && !id.isEmpty()
                && name != null && !name.isEmpty();
    }
}