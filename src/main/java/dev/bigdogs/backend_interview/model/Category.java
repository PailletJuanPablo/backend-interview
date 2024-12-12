package dev.bigdogs.backend_interview.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a category in a hierarchical catalog.
 * 
 * Root categories have no parent.
 * Subcategories have a parent and can themselves have sub-subcategories.
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the category. This name must be unique among siblings (categories with the same parent).
     */
    @Column(nullable = false)
    private String name;

    /**
     * Indicates if this subcategory is active. For root categories, this value should remain null.
     * For subcategories, it can be true or false.
     */
    @Column
    private Boolean active;

    /**
     * Parent category. Null if this is a root category.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private Category parent;

    /**
     * List of subcategories that belong to this category.
     * The mappedBy attribute points to the 'parent' field in this same entity.
     */
    @OneToMany(mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Category> subcategories = new ArrayList<>();

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getActive() {
        return active;
    }

    public Category getParent() {
        return parent;
    }

    public List<Category> getSubcategories() {
        return subcategories;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public void setSubcategories(List<Category> subcategories) {
        this.subcategories = subcategories;
    }

    // Utility methods for convenience if needed
    public void addSubcategory(Category subcategory) {
        subcategories.add(subcategory);
        subcategory.setParent(this);
    }

    public void removeSubcategory(Category subcategory) {
        subcategories.remove(subcategory);
        subcategory.setParent(null);
    }
}
