package com.pruebanelson.dto;

public class Error {

    private String error;

    private String description;

    /**
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

	@Override
	public String toString() {
		return "Error [error=" + error + ", description=" + description + "]";
	}

    
    
}
