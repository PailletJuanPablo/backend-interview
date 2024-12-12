package dev.bigdogs.backend_interview.dto;

import java.util.List;

public class CategoryTreeDTO {
    private Long id;
    private String name;
    private Boolean active;
    private Long parentId;
    private List<CategoryTreeDTO> subcategories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

	public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

	public List<CategoryTreeDTO> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<CategoryTreeDTO> subcategories) {
        this.subcategories = subcategories;
    }
}