package com.example.commonwordfinderweb;

public class WordRequest {
    private String text; 
    private String structure;
    private int limit;

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getStructure(){
        return structure;
    }

    public void setStructure(String structure){
        this.structure = structure;
    }

    public int getLimit(){
        return limit;
    }

    public void setLimit(int limit){
        this.limit = limit;
    }
}
